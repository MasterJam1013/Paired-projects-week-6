package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.EmployeeDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		ArrayList<Employee> employees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date "+
				"FROM employee ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}

		return employees;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {

		ArrayList<Employee> employees = new ArrayList<>();
		String sql = null;
		SqlRowSet results;
		if(firstNameSearch.equals("")){
			sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date "+
					"FROM employee "+ "WHERE last_name ILIKE ?";
			results = jdbcTemplate.queryForRowSet(sql, "%"+ lastNameSearch+ "%");
		}else if(lastNameSearch.equals("")){
			sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date "+
					"FROM employee "+ "WHERE first_name ILIKE ?";
			results = jdbcTemplate.queryForRowSet(sql, "%"+ firstNameSearch+ "%");
		}else{
			sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date "+
					"FROM employee "+ "WHERE first_name ILIKE ? AND last_name ILIKE ?";
			results = jdbcTemplate.queryForRowSet(sql, "%"+ firstNameSearch+ "%", "%"+ lastNameSearch+ "%");
		}
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		ArrayList<Employee> employees = new ArrayList<>();

		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date "+
				"FROM employee "+ "WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		ArrayList<Employee> employees = new ArrayList<>();

		String sql = "SELECT employee.employee_id, department_id, first_name, last_name, birth_date, gender, hire_date "+
				"FROM employee "+ "LEFT JOIN project_employee pe ON pe.employee_id = employee.employee_id "+
				"WHERE pe.employee_id IS NULL";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		ArrayList<Employee> employees = new ArrayList<>();

		String sql = "SELECT employee.employee_id, department_id, first_name, last_name, birth_date, gender, hire_date "+
				"FROM employee "+ "JOIN project_employee pe ON pe.employee_id = employee.employee_id "+
				"WHERE project_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);
		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}
		return employees;
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String sql = "UPDATE employee "+
				"SET department_id=? "+
				"WHERE employee_id = ?;";
		jdbcTemplate.update(sql, departmentId, employeeId);
		
	}

	private Employee mapRowToEmployee(SqlRowSet results) {
		Employee theEmployee;
		theEmployee = new Employee();
		theEmployee.setId(results.getLong("employee_id"));
		theEmployee.setDepartmentId(results.getLong("department_id"));
		theEmployee.setFirstName(results.getString("first_name"));
		theEmployee.setLastName(results.getString("last_name"));
		theEmployee.setBirthDay(results.getDate("birth_date").toLocalDate());
		theEmployee.setGender(results.getString("gender").charAt(0));
		theEmployee.setHireDate(results.getDate("hire_date").toLocalDate());

		return theEmployee;
	}


}
