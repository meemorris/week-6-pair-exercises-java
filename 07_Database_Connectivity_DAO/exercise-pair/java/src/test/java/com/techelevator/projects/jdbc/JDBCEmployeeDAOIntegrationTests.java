package com.techelevator.projects.jdbc;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.mockito.cglib.core.Local;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCEmployeeDAOIntegrationTests {

    private static final long TEST_DEPARTMENT = 123;
    private static final long COMPLAINTS_DEPARTMENT = 999;
    private static final long TEST_PROJECT = 123;
    private static long testEmployeeId = 456;

    private static SingleConnectionDataSource dataSource;
    private JDBCEmployeeDAO dao;


    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects2");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
    }

    @AfterClass
    public static void closeDataSource() {
        dataSource.destroy();
    }


    @Before
    public void setup() {
        String sql = "TRUNCATE department CASCADE";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql);

        String sqlInsertProject = "INSERT INTO project(project_id, name, from_date, to_date) " +
                " VALUES (?, 'testProject', '2021-02-16', '2021-04-16')";
        String sqlTestDepartment = "INSERT INTO department(department_id, name) VALUES (?, 'SoSoTired')";
        String sqlTestDepartment2 = "INSERT INTO department(department_id, name) VALUES (?, 'Complaints')";
        String sqlInsertProjectEmployee = "INSERT INTO project_employee(project_id, employee_id) " +
                "VALUES (?,?)";
        String sqlInsertEmployee = "INSERT INTO employee(employee_id, department_id, first_name, last_name" +
               ", birth_date, gender, hire_date) VALUES (?, ?, 'Testy', 'Tester', '2000-06-15', 'M', '2017-08-20')";
        jdbcTemplate.update(sqlTestDepartment, TEST_DEPARTMENT);
        jdbcTemplate.update(sqlTestDepartment2, COMPLAINTS_DEPARTMENT);
        jdbcTemplate.update(sqlInsertProject, TEST_PROJECT);
        jdbcTemplate.update(sqlInsertEmployee, testEmployeeId, TEST_DEPARTMENT);
        jdbcTemplate.update(sqlInsertProjectEmployee, TEST_PROJECT, testEmployeeId);
        dao = new JDBCEmployeeDAO(dataSource);
    }


    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void test_get_all_employees() {
        Long id = dao.getNextEmployeeId() + 1;
        LocalDate birthDate = LocalDate.parse("1955-03-15");
        LocalDate hireDate = LocalDate.parse("2015-08-20");
        Employee theEmployee = makeLocalEmployeeObject(TEST_DEPARTMENT, "Diana", "Ross", birthDate, 'F', hireDate);

        dao.hireEmployee(theEmployee);
        List<Employee> employees = dao.getAllEmployees();

        Assert.assertEquals(2, employees.size());
    }

    @Test
    public void test_search_employees_by_name() {
        LocalDate birthDate = LocalDate.parse("1990-06-07");
        LocalDate hireDate = LocalDate.parse("2015-07-17");
        Employee theEmployee = makeLocalEmployeeObject(TEST_DEPARTMENT, "Testy", "Tester", birthDate, 'M', hireDate);

        dao.hireEmployee(theEmployee);

        List<Employee> employees = dao.searchEmployeesByName("Testy", "Tester");

        assertNotNull(employees);
        assertEquals(2, employees.size());
        Employee savedEmployee = employees.get(1);
        assertEmployeesAreEqual(theEmployee, savedEmployee);
    }

    @Test
    public void test_get_employees_by_department_id() {
        LocalDate birthDate = LocalDate.parse("1978-05-15");
        LocalDate hireDate = LocalDate.parse("2017-07-17");
        Employee theEmployee = makeLocalEmployeeObject(TEST_DEPARTMENT, "Katie", "Dwyer", birthDate, 'F', hireDate);

        dao.hireEmployee(theEmployee);
        List<Employee> employees = dao.getEmployeesByDepartmentId(TEST_DEPARTMENT);

        assertNotNull(employees);
        assertEquals(2, employees.size());
        Employee savedEmployee = employees.get(1);
        assertEmployeesAreEqual(theEmployee, savedEmployee);
    }

    @Test
    public void test_get_employees_by_project_id() {
        LocalDate birthDate = LocalDate.parse("1978-05-15");
        LocalDate hireDate = LocalDate.parse("2017-07-17");
        Employee theEmployee = makeLocalEmployeeObject(TEST_DEPARTMENT, "Katie", "Dwyer", birthDate, 'F', hireDate);

        dao.hireEmployee(theEmployee);
        String sql = "UPDATE project_employee SET employee_id = ? WHERE project_id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        long id = theEmployee.getId();


        jdbcTemplate.update(sql, id, TEST_PROJECT);

        List<Employee> employees = dao.getEmployeesByProjectId(TEST_PROJECT);

        assertNotNull(employees);
        assertEquals(1, employees.size());
        Employee savedEmployee = employees.get(0);
        assertEmployeesAreEqual(theEmployee, savedEmployee);
    }


    @Test
    public void test_change_employee_department() {
        LocalDate birthDate = LocalDate.parse("1978-05-15");
        LocalDate hireDate = LocalDate.parse("2017-07-17");
        Employee theEmployee = makeLocalEmployeeObject(TEST_DEPARTMENT, "Katie", "Dwyer", birthDate, 'F', hireDate);

        dao.hireEmployee(theEmployee);
        Long id = theEmployee.getId();
        dao.changeEmployeeDepartment(id, COMPLAINTS_DEPARTMENT);


        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Long id2 = theEmployee.getId();
        String sqlInsertProjectEmployee = "INSERT INTO project_employee(project_id, employee_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sqlInsertProjectEmployee, TEST_PROJECT, id2);

        List<Employee> employees = dao.getEmployeesByProjectId(TEST_PROJECT);

        assertNotNull(employees);
        assertEquals(2, employees.size());
        Employee savedEmployee = employees.get(1);
        assertNotEquals(theEmployee.getDepartmentId(), savedEmployee.getDepartmentId());

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

    private void assertEmployeesAreEqual (Employee expected, Employee actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDepartmentId(), actual.getDepartmentId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getBirthDay(), actual.getBirthDay());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getHireDate(), actual.getHireDate());
    }





}
