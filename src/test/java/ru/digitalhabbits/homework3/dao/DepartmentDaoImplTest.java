package ru.digitalhabbits.homework3.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Department;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DepartmentDaoImplTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private DepartmentDao departmentDao;

	private List<Department> departments;

	@BeforeEach
	void initTestData() {
		var firstDepartment = new Department().setName("firstDepartment");
		var secondDepartment = new Department().setName("secondDepartment");
		var thirdDepartment = new Department().setName("thirdDepartment");

		departments = Stream.of(firstDepartment, secondDepartment, thirdDepartment)
				.map(department -> departmentDao.create(department))
				.collect(Collectors.toList());
	}

	@Test
	void findById() {
		var findDepartment = departmentDao.findById(departments.get(0).getId());
		Assertions.assertEquals(departments.get(0), findDepartment);
	}

	@Test
	void findAll() {
		Assertions.assertEquals(departments, departmentDao.findAll());
	}

	@Test
	void update() {
		var updateDepartment = departmentDao.findById(departments.get(0).getId());
		updateDepartment.setName("updateDepartment");

		updateDepartment = departmentDao.update(updateDepartment);

		var findDepartment = departmentDao.findById(updateDepartment.getId());
		Assertions.assertTrue(updateDepartment.getName() == findDepartment.getName());
	}

	@Test
	void delete() {
		var deleteDepartment = departmentDao.delete(departments.get(0).getId());
		Assertions.assertNull(departmentDao.findById(deleteDepartment.getId()));
	}
}