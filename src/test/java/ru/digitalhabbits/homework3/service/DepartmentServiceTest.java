package ru.digitalhabbits.homework3.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentRequest;
import ru.digitalhabbits.homework3.model.PersonInfo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DepartmentServiceImpl.class)
class DepartmentServiceTest {

	@MockBean
	private DepartmentDao departmentDao;

	@MockBean
	private PersonDao personDao;

	@Autowired
	private DepartmentService departmentService;

	private List<Department> departments;

	@BeforeEach
	void init() {
		departments = createTestData();
	}

	private List<Department> createTestData() {
		return IntStream.range(0, 10)
				.mapToObj(value ->
						new Department()
								.setId(value)
								.setName(RandomStringUtils.randomAlphabetic(10))
				)
				.collect(Collectors.toList());
	}

	@Test
	void findAllDepartments() {
		Mockito.when(departmentDao.findAll()).thenReturn(departments);

		assertEquals(departments.size(), departmentService.findAllDepartments().size());
	}

	@Test
	void getDepartment() {
		var department = departments.get(0);
		Mockito.when(departmentDao.findById(1)).thenReturn(department);
		var departmentResponse = departmentService.getDepartment(1);

		assertEquals(department.getId(), departmentResponse.getId());
		assertEquals(department.getName(), departmentResponse.getName());
	}

	@Test
	void createDepartment() {
		var department = departments.get(0);
		Mockito.when(departmentDao.create(new Department().setName(department.getName()))).thenReturn(department);
		Integer createDepartment = departmentService.createDepartment(new DepartmentRequest().setName(department.getName()));

		assertEquals(0, createDepartment.intValue());
	}

	@Test
	void updateDepartment() {
		var department = departments.get(0);

		Mockito.when(departmentDao.findById(0)).thenReturn(department);
		Mockito.when(departmentDao.update(department)).thenReturn(department.setName("Update name"));
		var updateDepartment = departmentService.updateDepartment(0, new DepartmentRequest().setName(department.getName()));

		assertEquals("Update name", updateDepartment.getName());
	}

	@Test
	void deleteDepartment() {
		var department = departments.get(0);
		Mockito.when(departmentDao.findById(anyInt())).thenReturn(department);

		departmentService.deleteDepartment(5);

		verify(departmentDao, times(1)).findById(anyInt());
		verify(departmentDao, times(1)).delete(anyInt());
	}

	@Test
	void addPersonToDepartment() {
		var department = departments.get(0);
		var person = new Person()
				.setId(1)
				.setLastName("LastName")
				.setFirstName("FirstName")
				.setMiddleName("MiddleName")
				.setAge(30)
				.setDepartment(department);

		Mockito.when(departmentDao.findById(1)).thenReturn(department);
		Mockito.when(personDao.findById(1)).thenReturn(person);

		departmentService.addPersonToDepartment(1, 1);

		var departmentResponse = departmentService.getDepartment(1);
		assertThat(departmentResponse.getPersons()).extracting(PersonInfo::getFullName).isEqualTo(List.of("LastName FirstName MiddleName"));
	}

	@Test
	void removePersonToDepartment() {
		var department = departments.get(0);
		var person = new Person()
				.setId(1)
				.setLastName("LastName")
				.setFirstName("FirstName")
				.setMiddleName("MiddleName")
				.setAge(30)
				.setDepartment(department);
		department.getPersons().add(person);

		Mockito.when(departmentDao.findById(any(Integer.class))).thenReturn(department);
		Mockito.when(personDao.findById(any(Integer.class))).thenReturn(person);

		departmentService.removePersonToDepartment(1, 1);

		assertEquals(0, department.getPersons().size());
		assertNull(person.getDepartment());
		verify(departmentDao, times(1)).update(department);
	}

	@Test
	void closeDepartment() {
		var department = departments.get(0);

		Mockito.when(departmentDao.findById(0)).thenReturn(department);

		departmentService.closeDepartment(0);
		var departmentResponse = departmentService.getDepartment(0);

		assertTrue(departmentResponse.isClosed());
		assertEquals(0, departmentResponse.getPersons().size());
	}
}