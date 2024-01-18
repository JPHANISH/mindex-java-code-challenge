package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationUrl;
    private String compensationIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
		employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "reportingStructure/{id}";
        compensationUrl = "http://localhost:" + port + "compensation";
        compensationIdUrl = "http://localhost:" + port + "compensation/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
	public void testGetReportingStructureById()
	{
	    // Arrange
    	String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
	    String expectedFirstName = "John";
	    String expectedLastName = "Lennon";
	    int expectedReportStructureCount = 4;

	    // Execute
	    ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employeeId).getBody();

	    // Assert
	    Employee employee = reportingStructure.getEmployee();
	    assertEquals(expectedFirstName, employee.getFirstName());
	    assertEquals(expectedLastName, employee.getLastName());
	    assertEquals(expectedReportStructureCount, reportingStructure.getNumberOfReports());
	}

    @Test
	public void testCreateReadCompensation()
	{
	    // Create check
	    Compensation testCompensation = new Compensation();
	    testCompensation.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
	    testCompensation.setSalary(200000);

	    // Execute
	    Compensation newCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

	    // Assert
	    assertNotNull(newCompensation.getCompensationId());
	    assertEquals(newCompensation.getEmployeeId(), testCompensation.getEmployeeId());
	    assertEquals(newCompensation.getSalary(), testCompensation.getSalary(), 0.001);
	    
	    // Read check
	    Compensation compensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, testCompensation.getEmployeeId()).getBody();

	    // Assert
	    assertNotNull(compensation);
	    assertEquals(testCompensation.getEmployeeId(), compensation.getEmployeeId());
	    assertEquals(testCompensation.getSalary(), compensation.getSalary(), 0.001);
	}

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
