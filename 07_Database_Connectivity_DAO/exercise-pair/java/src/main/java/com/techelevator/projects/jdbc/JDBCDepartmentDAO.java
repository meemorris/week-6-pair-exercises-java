package com.techelevator.projects.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCDepartmentDAO implements DepartmentDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	@Override
	public List<Department> getAllDepartments() {
		ArrayList<Department> departments = new ArrayList<>();
		String sql = "SELECT department_id, name " +
				 	 "FROM department";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			Department theDepartment = mapRowToDepartment(results);
			departments.add(theDepartment);
		}
		return departments;
	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		ArrayList<Department> departments = new ArrayList<>();
		String sql = "SELECT department_id, name FROM department WHERE name ILIKE ? "; // NO , I DONT LIKE
		nameSearch = "%" + nameSearch + "%";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, nameSearch);
		while(results.next()) {
			Department theDepartment = mapRowToDepartment(results);
			departments.add(theDepartment);
		}
		return departments;
	}

	@Override
	public Department saveDepartment(Department updatedDepartment) {
		String sql = "UPDATE department SET name = ? WHERE department_id = ?";
		jdbcTemplate.update(sql, updatedDepartment.getName(), updatedDepartment.getId());
		return updatedDepartment;
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		String sql = "INSERT INTO department(name) VALUES (?) RETURNING department_id";
//		newDepartment.setId(getNextDepartmentId());
//		jdbcTemplate.update(sql, newDepartment.getName());
		Long id = jdbcTemplate.queryForObject(sql, Long.class, newDepartment.getName());
		newDepartment.setId(id);
		return newDepartment;
	}

	@Override
	public Department getDepartmentById(Long id) {
		String sql = "SELECT department_id, name FROM department WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id); //who knows how many rows will be returned. Row set similar to results set.Cursor is a way/cue (invalid cursor) cursor is a database version of a for each
		if (results.next()) {
			return mapRowToDepartment(results);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new department");
		}
	}

	public Department mapRowToDepartment(SqlRowSet results) {
		Department theDepartment = new Department();
		theDepartment.setId(results.getLong("department_id"));
		theDepartment.setName(results.getString("name"));
		return theDepartment;
	}

	public long getNextDepartmentId() {
		String sqlGetNextId = "SELECT nextval('seq_department_id')";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetNextId);
		if (results.next()) {
			return results.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new department");
		}
	}


}
