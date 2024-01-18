package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }
    
    @Override
	public ReportingStructure getReportingStructureById(String id)
    {
        Employee employee = employeeRepository.findByEmployeeId(id);
        
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }
        
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(this.getNumberOfReports(employee));
        return reportingStructure;
    }
    
    @Override
    public Compensation createCompensation(Compensation compensation)
    {
        if (compensation != null)
        {
        	compensation.setCompensationId(UUID.randomUUID().toString());
            compensationRepository.insert(compensation);
        }

        return compensation;
    }

    @Override
    public Compensation getCompensationByEmployeeId(String employeeId)
    {
    	return compensationRepository.findFirstByEmployeeId(employeeId);
    }

    private int getNumberOfReports(Employee employee)
    {
        int numberOfReports = 0;
        List<Employee> directReports = employee.getDirectReports();
        if (directReports != null && directReports.size() > 0)
        {
            numberOfReports = directReports.size();
            for (Employee report: directReports)
            {
                //fixes issue with direct reports not eager loading
                Employee updatedReport = employeeRepository.findByEmployeeId(report.getEmployeeId());
                numberOfReports += getNumberOfReports(updatedReport);
            }
        }

        return numberOfReports;
    }
}
