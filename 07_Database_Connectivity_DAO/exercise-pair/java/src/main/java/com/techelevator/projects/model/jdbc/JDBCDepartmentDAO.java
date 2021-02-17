package com.techelevator.projects.model.jdbc;

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
		String sql = "SELECT department_id, name FROM department WHERE name ILIKE ? ";
		nameSearch = "%" + nameSearch + "%";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, nameSearch);
		while(results.next()) {
			Department theDepartment = mapRowToDepartment(results);
			departments.add(theDepartment);
		}
		return departments;
	}

	@Override
	public void saveDepartment(Department updatedDepartment) {
		String sql = "UPDATE department SET name = ? WHERE department_id = ?";
		jdbcTemplate.update(sql, updatedDepartment.getName(), updatedDepartment.getId());
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		String sql = "INSERT INTO department(department_id, name) VALUES (?,?)";
		jdbcTemplate.update(sql, getNextDepartmentId(), newDepartment.getName());
		return newDepartment;
	}

	@Override
	public Department getDepartmentById(Long id) {
		String sql = "SELECT name FROM department WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		return mapRowToDepartment(results);
	}

	private Department mapRowToDepartment(SqlRowSet results) {
		Department theDepartment = new Department();
		theDepartment.setId(results.getLong("department_id"));
		theDepartment.setName(results.getString("name"));
		return theDepartment;
	}

	private long getNextDepartmentId() {
		String sqlGetNextId = "SELECT nextval('seq_department_id')";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetNextId);
		results.next();
		return results.getLong(1);
	}


}
