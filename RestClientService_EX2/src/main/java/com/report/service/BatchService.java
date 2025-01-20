package com.report.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.report.dto.EmployeeResponse;
import com.report.entity.Employee;
import com.report.repository.EmployeeRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class BatchService {

	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	String emailIdFrom;

	@Value("${spring.mail.emailto}")
	String emailTo;
	
	@Value("${csv.fileName}")
	String csvFileName;
	

	public List<Employee> storeData(List<EmployeeResponse> employeeResponse) {
		return employeeRepository.saveAll(
				employeeResponse.stream().map(c1 -> modelMapper.map(c1, Employee.class)).collect(Collectors.toList()));
	}

	public String generateCSVReport(List<Employee> empList) {

		// Create a writer object
		Writer writer = null;
		CSVPrinter csvPrinter = null;
		try {
			writer = new FileWriter(csvFileName);

			csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Emp Id", "Name", "Contact", "Email",
					"Date of Joining", "Employee", "Salary"));

			// Write the customer data into the CSV
			for (Employee employee : empList) {

				csvPrinter.printRecord(employee.getEmpno(), employee.getName(), employee.getContact(),
						employee.getEmail(), employee.getDateOfJoining(), employee.isEmployee(), employee.getSal());

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			try {
				csvPrinter.flush();
				csvPrinter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return "CSV File Generated successfully";
	}

	public String sendEmailWithAttachment() {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart message
			helper.setFrom(emailIdFrom);
			helper.setTo(emailTo);
			helper.setSubject("Employee Report");
			helper.setText("Report has been generated");
			FileSystemResource file = new FileSystemResource(new File(csvFileName));
			helper.addAttachment(file.getFilename(), file); // Add attachment

			mailSender.send(message);
			return "Mail sent successfully with attachment " + file.getFilename();
		} catch (MessagingException | MailException e) {
			e.printStackTrace(); // Log the exception
		}
		return "failed to send mail";
	}
}
