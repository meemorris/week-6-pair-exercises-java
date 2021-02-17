package com.techelevator.projects.model;

import java.util.List;

public interface ProjectDAO {

	/**
	 * Get a list of all active projects. A project is active if we are past
	 * its from_date but before its to_date. If its to_date is null, then
	 * we consider it ongoing. If its from_date is null, then we consider
	 * it not started yet.
	 * 
	 * @return all active projects as Project objects in a List
	 */
	public List<Project> getAllActiveProjects();

	/**
	 * Unassign the employee from a project.
	 * 
	 * @param projectId the project to remove the employee from
	 * @param employeeId the employee to remove
	 */
	public void removeEmployeeFromProject(Long projectId, Long employeeId);

	/**
	 * Assign an employee to a project
	 * 
	 * @param projectId the project to put the employee on
	 * @param employeeId the employee to assign
	 */
	public void addEmployeeToProject(Long projectId, Long employeeId);
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