package com.techelevator.projects.jdbc;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCProjectDAOIntegrationTests {

    private static final long TEST_PROJECT = 123;
    private static final long TEST_EMPLOYEE = 456;
    private static final long TEST_DEPARTMENT = 678;

    private static SingleConnectionDataSource dataSource;
    private JDBCProjectDAO dao;
    private JDBCEmployeeDAO employeeDAO;


    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects2");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
    }

    @AfterClass
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }


    @Before
    public void setup() {
        //making local objects
        //then push them to the database
        //local objects that you can reference get ID

//        LocalDate startDate = LocalDate.parse("2021-02-16");
//        LocalDate endDate = LocalDate.parse("2021-04-16");
//        Project theProject = makeLocalProjectObject("Test Project", startDate, endDate);




        String sql = "TRUNCATE project CASCADE";
        String sqlInsertProject = "INSERT INTO project(project_id, name, from_date, to_date) " +
                " VALUES (?, 'testProject', '2021-02-16', '2021-04-16')";
        String sqlInsertEmployee = "INSERT INTO employee(employee_id, department_id, first_name, last_name" +
                ", birth_date, gender, hire_date) VALUES (?, ?, 'Testy', 'Tester', '2000-06-15', 'M', '2017-08-20')";
        String sqlInsertEmployeeIntoProject = "INSERT INTO project_employee(project_id, employee_id) VALUES " +
                "(?,?)";
        String sqlTestDepartment = "INSERT INTO department(department_id, name) VALUES (?, 'SoSoTired')";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql);
        jdbcTemplate.update(sqlTestDepartment, TEST_DEPARTMENT);
        jdbcTemplate.update(sqlInsertProject, TEST_PROJECT);
        jdbcTemplate.update(sqlInsertEmployee, TEST_EMPLOYEE, TEST_DEPARTMENT);
        jdbcTemplate.update(sqlInsertEmployeeIntoProject, TEST_PROJECT, TEST_EMPLOYEE);

        dao = new JDBCProjectDAO(dataSource);
        employeeDAO = new JDBCEmployeeDAO(dataSource);
    }


    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }


    @Test
    public void test_get_all_active_projects() {
        LocalDate startDate = LocalDate.parse("2020-02-15");
        LocalDate endDate = LocalDate.parse("2020-04-15");

        Project theProject = makeLocalProjectObject("Friday Project", startDate, endDate);
        dao.createProject(theProject);

        List<Project> projects = dao.getAllActiveProjects();

        assertNotNull(projects);
        assertEquals(2, projects.size());
    }
    
    @Test
    public void test_add_and_remove_employee_from_project() {
        //arrange
        LocalDate birthDate = LocalDate.parse("1978-05-15");
        LocalDate hireDate = LocalDate.parse("2017-07-17");
        Employee theEmployee = makeLocalEmployeeObject(TEST_DEPARTMENT, "Katie", "Dwyer", birthDate, 'F', hireDate);

        employeeDAO.hireEmployee(theEmployee);
        Long id = theEmployee.getId();
        dao.addEmployeeToProject(TEST_PROJECT, id);

        List<Employee> employees = employeeDAO.getEmployeesByProjectId(TEST_PROJECT);

        assertNotEquals(null, theEmployee.getId());
        assertEquals(2, employees.size());

        Employee savedEmployee = employees.get(1);
        assertEmployeesOnProjectsAreEqual(theEmployee, savedEmployee);

        //remove

        dao.removeEmployeeFromProject(TEST_PROJECT, id);

        List<Employee> updatedList = employeeDAO.getEmployeesByProjectId(TEST_PROJECT);

        assertEquals(1, updatedList.size());
    }



    private Project makeLocalProjectObject(String name, LocalDate startDate, LocalDate endDate) {
        Project theProject = new Project();
        theProject.setName(name);
        theProject.setStartDate(startDate);
        theProject.setEndDate(endDate);
        return theProject;
    }

    private Employee makeLocalEmployeeObject(Long departmentId, String firstName, String lastName, LocalDate birthDay, char gender, LocalDate hireDate) {
        Employee theEmployee = new Employee();
        theEmployee.setDepartmentId(departmentId);
        theEmployee.setFirstName(firstName);
        theEmployee.setLastName(lastName);
        theEmployee.setBirthDay(birthDay);
        theEmployee.setGender(gender);
        theEmployee.setHireDate(hireDate);
        return theEmployee;
    }

    private void assertEmployeesOnProjectsAreEqual (Employee expected, Employee actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDepartmentId(), actual.getDepartmentId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getBirthDay(), actual.getBirthDay());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getHireDate(), actual.getHireDate());
    }









}
