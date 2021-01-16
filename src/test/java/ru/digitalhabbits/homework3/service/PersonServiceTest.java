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
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.utils.PersonHelper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PersonServiceImpl.class)
class PersonServiceTest {

	@MockBean
	private PersonDao personDao;

	@Autowired
	private PersonService personService;

	private List<Person> persons;

	@BeforeEach
	void init() {
		persons = createTestData();
	}

	private List<Person> createTestData() {
		return IntStream.range(0, 10)
				.mapToObj(value ->
						new Person()
								.setId(value)
								.setFirstName(RandomStringUtils.randomAlphabetic(10))
								.setLastName(RandomStringUtils.randomAlphabetic(10))
								.setMiddleName(RandomStringUtils.randomAlphabetic(10))
								.setAge(30)
								.setDepartment(new Department().setName(RandomStringUtils.randomAlphabetic(10)).setId(value))
				)
				.collect(Collectors.toList());
	}

	@Test
	void findAllPersons() {
		when(personDao.findAll()).thenReturn(persons);

		assertEquals(personService.findAllPersons().size(), persons.size());
	}

	@Test
	void getPerson() {
		var person = persons.get(0);
		when(personDao.findById(0)).thenReturn(person);
		var personResponse = personService.getPerson(0);

		assertEquals(PersonHelper.createPersonFullName(person), personResponse.getFullName());
	}

	@Test
	void createPerson() {
		var person = persons.get(0);
		when(personDao.create(Mockito.any(Person.class))).thenReturn(person);
		var id = personService.createPerson(new PersonRequest());

		assertEquals(id, person.getId());
	}

	@Test
	void updatePerson() {
		var person = persons.get(0);
		var updatePerson = persons.get(1);

		when(personDao.findById(Mockito.anyInt())).thenReturn(person);
		when(personDao.update(Mockito.any(Person.class))).thenReturn(updatePerson);

		var personResponse = personService.updatePerson(person.getId(), new PersonRequest());

		assertEquals(personResponse.getFullName(), PersonHelper.createPersonFullName(updatePerson));
	}

	@Test
	void deletePerson() {
		var person = persons.get(0);

		when(personDao.findById(person.getId())).thenReturn(person);
		when(personDao.delete(person.getId())).thenReturn(person);
		personService.deletePerson(person.getId());
		verify(personDao, times(1)).delete(person.getId());
	}
}