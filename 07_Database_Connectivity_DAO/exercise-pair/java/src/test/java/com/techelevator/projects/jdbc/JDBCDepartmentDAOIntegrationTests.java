package com.techelevator.projects.jdbc;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Project;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCDepartmentDAOIntegrationTests {

    private static SingleConnectionDataSource dataSource;
    private JDBCDepartmentDAO dao;


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
        String sql = "TRUNCATE department CASCADE";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql);
        dao = new JDBCDepartmentDAO(dataSource);
    }


    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void test_get_all_departments() {
       //Arrange
        Department theDepartment = makeLocalDepartmentObject("ReallyTired");
        dao.createDepartment(theDepartment); //puts them in the database

        //Act
        //list to get all departments
        List<Department> departmentList = dao.getAllDepartments();
        //for other ones, getId(), pull from database and attach it to the one you have in memory

        //Assert
        //check to see if your list size is the size that you're expecting
        //on other ones assert that they are equal
        Assert.assertEquals(1, departmentList.size());


    }

    @Test
    public void test_search_departments_by_name() {
        String testName = "ReallyTired";
        Department theDepartment = makeLocalDepartmentObject(testName);
        dao.createDepartment(theDepartment);

        List<Department> departmentList = dao.searchDepartmentsByName(testName);

        assertNotNull(departmentList);
        assertEquals(1, departmentList.size());
        Department savedDepartment = departmentList.get(0);
        assertDepartmentsAreEqual(theDepartment, savedDepartment);
    }

    @Test
    public void test_save_department() {
        String testName = "StillTired";
        Department theDepartment = makeLocalDepartmentObject(testName);

        dao.createDepartment(theDepartment);
        theDepartment.setName("MoreTired");

        Department updateResult = dao.saveDepartment(theDepartment);

        List<Department> results = dao.searchDepartmentsByName(updateResult.getName());
        Department savedDepartment = results.get(0);
        assertDepartmentsAreEqual(theDepartment, savedDepartment);
    }
    //come back to it
    @Test
    public void test_get_department_by_id() {
        Department theDepartment = makeLocalDepartmentObject("Complaints");
        dao.createDepartment(theDepartment);
        Long id = theDepartment.getId();

        Department savedDepartment = dao.getDepartmentById(id);

        assertNotEquals(null, theDepartment.getId());
        assertDepartmentsAreEqual(theDepartment, savedDepartment);

    }

    private Department makeLocalDepartmentObject(String name) {
        Department theDepartment = new Department();
        theDepartment.setName(name);
        return theDepartment;
    }

    private void assertDepartmentsAreEqual(Department expected, Department actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }





}
