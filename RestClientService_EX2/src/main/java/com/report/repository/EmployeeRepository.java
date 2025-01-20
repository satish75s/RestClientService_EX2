package com.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.report.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
