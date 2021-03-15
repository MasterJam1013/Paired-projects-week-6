package com.techelevator.model.jdbc;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCEmployeeDAOTest {
    private static SingleConnectionDataSource dataSource;
    private JDBCProjectDAO dao;

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
        dao = new JDBCProjectDAO(dataSource);

        //delete the data
        String sql = "TRUNCATE project CASCADE";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql);

        //test projects
        /* active projects */
        createTestProject(1L, "project1", LocalDate.now().minusDays(1), null);
        createTestProject(2L, "project2", LocalDate.now().minusDays(2), LocalDate.now().plusDays(18));
        /* future project */
        createTestProject(3L, "project3", LocalDate.now().plusDays(1), LocalDate.now().plusDays(21));
        /* past project */
        createTestProject(4L, "project4", LocalDate.now().minusDays(21), LocalDate.now().minusDays(1));
        /* potential project */
        createTestProject(5L, "project5", null, null);

        //test department
        sql = "INSERT INTO department(department_id, name) VALUES(?, ?)";
        jdbcTemplate.update(sql, 999, "test department");

        //test employees
        createTestEmployee(1L, 999L, "Jasper", "Clifton", LocalDate.of(2010, 6, 9), 'M', LocalDate.now());
        createTestEmployee(2L, 999L, "Lily", "Clifton", LocalDate.of(2014, 8, 9), 'F', LocalDate.now());

        //test project_employee entry
        createTestProjectEmployeeConnections(1L, 1L);
        createTestProjectEmployeeConnections(1L, 2L);

    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }






    private void assertEmployeesAreEqual(Project expected, Project actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }


    private void createTestProject(Long projectID, String name, LocalDate startDate, LocalDate endDate) {
        String sql = "INSERT INTO project(project_id, name, from_date, to_date) " +
                "VALUES(?, ?, ?, ?)";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql, projectID, name, startDate, endDate);

    }

    private void createTestEmployee(Long employeeID, Long departmentId, String firstName, String lastName, LocalDate birthday, char gender, LocalDate hireDate) {
        String sql = "INSERT INTO employee(employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql, employeeID, departmentId, firstName, lastName, birthday, gender, hireDate);
    }

    private void createTestProjectEmployeeConnections(Long projectID, Long employeeID){
        String sql = "INSERT INTO project_employee(project_id, employee_id) VALUES(?, ?)";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql, projectID, employeeID);
    }

}
