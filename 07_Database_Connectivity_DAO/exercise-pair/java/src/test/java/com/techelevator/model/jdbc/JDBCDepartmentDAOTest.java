package com.techelevator.model.jdbc;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.util.List;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCDepartmentDAOTest {

    private static SingleConnectionDataSource dataSource;
    private JDBCDepartmentDAO dao;

    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
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

        dao = new JDBCDepartmentDAO(dataSource);
    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void get_all_departments_returns_all_departments() {
        Department department = makeLocalDepartmentObject("Surplus Department");
        dao.createDepartment(department);

        department = makeLocalDepartmentObject("Extra Department");
        dao.createDepartment(department);

        List<Department> results = dao.getAllDepartments();

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    public void search_departments_by_name_returns_correct_department() {
        String testName = "Surplus Department";
        Department department = makeLocalDepartmentObject(testName);
        dao.createDepartment(department);

        List<Department> results = dao.searchDepartmentsByName(testName);

        assertNotNull(results);
        assertEquals(1, results.size());
        Department result = results.get(0);
        assertDepartmentsAreEqual(department, result);
    }

    @Test
    public void save_department_returns_correct_changes() {
        Department department = makeLocalDepartmentObject("Surplus Department");
        dao.createDepartment(department);

        department.setName("Extra Department");

        dao.saveDepartment(department);

        Department updatedDepartment = dao.getDepartmentById(department.getId());

        assertDepartmentsAreEqual(department, updatedDepartment);
    }

    @Test
    public void get_department_by_id_returns_correct_department() {
        Department department = makeLocalDepartmentObject("Surplus Department");
        dao.createDepartment(department);

        Department result = dao.getDepartmentById(department.getId());

        assertNotEquals(null, result.getId());
        assertDepartmentsAreEqual(department, result);
    }



    private Department makeLocalDepartmentObject(String name) {
        Department department = new Department();
        department.setName(name);
        return department;
    }

    private void assertDepartmentsAreEqual(Department expected, Department actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }


}
