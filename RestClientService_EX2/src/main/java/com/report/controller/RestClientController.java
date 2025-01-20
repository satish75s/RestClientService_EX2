package com.report.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.report.dto.EmployeeResponse;
import com.report.entity.Employee;
import com.report.service.BatchService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/emp")
@Slf4j
public class RestClientController {
	private RestClient restClient;
	
	
	
	@Autowired
	BatchService batchService;

	@Value("${emp.rest.url}")
	private String restClientURL;
	
	

	@PostConstruct
	public void init() {

		restClient = RestClient.builder().baseUrl(restClientURL).build();
	}

	

	@GetMapping("/sendAttachment")
	public void getEmpList() {
		log.info("Fetching Employee Details from Employee Service");
		List<EmployeeResponse> employeeResponse= fetchEmpList();
		
		log.info("Storing Employee Data into H2 DB");
		List<Employee> empList = batchService.storeData(employeeResponse);
		
		log.info("Generating CSV Report");
		batchService.generateCSVReport(empList);
		
		log.info("Send mail attachment");
		batchService.sendEmailWithAttachment();
		
	}
	
	public List<EmployeeResponse> fetchEmpList(){
		return restClient.get().uri("/emp/getEmpList").retrieve().body(new ParameterizedTypeReference<List<EmployeeResponse>>() {});
	}

	
}
