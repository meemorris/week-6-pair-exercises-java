package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Department;
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
		String sql = "SELECT project_id, name, from_date, to_date FROM project " +
		"WHERE NOW() BETWEEN from_date AND to_date " +
		"OR NOW() > from_date AND to_date IS NULL";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			Project theProject = mapRowToProject(results);
			projects.add(theProject);
		}
		return projects;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "DELETE FROM project_employee WHERE employee_id = ? AND project_id = ?";
		jdbcTemplate.update(sql, employeeId, projectId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sql = "INSERT INTO project_employee(employee_id, project_id) VALUES (?,?)";
		jdbcTemplate.update(sql, employeeId, projectId);
	}

	public Project mapRowToProject(SqlRowSet results) {
		Project theProject = new Project();
		theProject.setId(results.getLong("project_id"));
		theProject.setName(results.getString("name"));
		theProject.setStartDate(results.getDate("from_date").toLocalDate());
		if (results.getDate("to_date") != null) {
			theProject.setEndDate(results.getDate("to_date").toLocalDate());
		}
		return theProject;
	}

	//from_date and to_date are allowed to be null. to_date could be null, but having a from_date be null doesn't make sense
//write a query to give me all of the active projects
/*
SELECT project_id, name, from_date, to_date
FROM project
WHERE NOW() BETWEEN from_date AND to_date (both filled in and we're in the middle)
OR NOW() > from_date AND to_date IS NULL (the project has started but has an unknown end date)
--this gives you a list of projects, use that NOW() just as a regular variable
 */

}
