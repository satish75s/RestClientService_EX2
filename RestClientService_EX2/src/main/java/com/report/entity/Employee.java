package com.report.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="EMP_DETAILS")
public class Employee {
	@Id
	//@GeneratedValue
	int empno;	
	String name;
	String contact;
	String email;
	LocalDate dateOfJoining;
	boolean isEmployee;
	double sal;

}
