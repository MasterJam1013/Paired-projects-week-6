package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		ArrayList<Project> projects = new ArrayList<>();
		String sql = "SELECT project_id, name, from_date, to_date "+
				"FROM project "+ "WHERE (from_date IS NOT NULL AND from_date <= CURRENT_DATE) AND " +
				"(to_date IS NULL OR to_date > CURRENT_DATE)";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			Project theProject = mapRowToProject(results);
			projects.add(theProject);
		}

		return projects;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "DELETE FROM project_employee WHERE project_id = ? AND employee_id = ?";
		jdbcTemplate.update(sql,projectId, employeeId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sql = "INSERT INTO project_employee(project_id, employee_id) " +
				"VALUES(?, ?)";
		jdbcTemplate.update(sql, projectId, employeeId);
	}


	private Project mapRowToProject(SqlRowSet results) {
		Project theProject = new Project();
		theProject.setId(results.getLong("project_id"));
		theProject.setName(results.getString("name"));
		if(results.getDate("from_date") != null) {
			theProject.setStartDate(results.getDate("from_date").toLocalDate());
		}
		if(results.getDate(("to_date")) != null) {
			theProject.setEndDate(results.getDate("to_date").toLocalDate());
		}


		return theProject;
	}

	//commented out because we never use it
//	private long getNextProjectId() {
//		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_project_id')");
//		if (nextIdResult.next()) {
//			return nextIdResult.getLong(1);
//		} else {
//			throw new RuntimeException("Something went wrong while getting a project id for the new project");
//		}
//	}
}
