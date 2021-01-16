package ru.digitalhabbits.homework3.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
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
import ru.digitalhabbits.homework3.domain.Person;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PersonDaoTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private PersonDao personDao;

	private List<Person> persons;

	@BeforeEach
	void initTestData() {
		persons = Stream.of(createTestPerson(), createTestPerson(), createTestPerson())
				.map(person -> personDao.create(person))
				.collect(Collectors.toList());
	}

	private Person createTestPerson() {
		return new Person()
				.setFirstName(RandomStringUtils.randomAlphabetic(10))
				.setLastName(RandomStringUtils.randomAlphabetic(10))
				.setMiddleName(RandomStringUtils.randomAlphabetic(10))
				.setAge(RandomUtils.nextInt(1, 90));

	}

	@Test
	void findById() {
		var findPerson = personDao.findById(persons.get(0).getId());
		Assertions.assertThat(persons.get(0)).isEqualToComparingFieldByField(findPerson);
	}

	@Test
	void findAll() {
		Assertions.assertThat(persons).isEqualTo(personDao.findAll());
	}

	@Test
	void update() {
		var updatePerson = personDao.findById(persons.get(0).getId());
		updatePerson.setFirstName(RandomStringUtils.randomAlphabetic(10))
				.setLastName(RandomStringUtils.randomAlphabetic(10))
				.setMiddleName(RandomStringUtils.randomAlphabetic(10))
				.setAge(RandomUtils.nextInt(1, 90));

		updatePerson = personDao.update(updatePerson);

		var findPerson = personDao.findById(persons.get(0).getId());
		Assertions.assertThat(findPerson).isEqualToComparingFieldByField(updatePerson);
	}

	@Test
	void delete() {
		var deletePerson = personDao.delete(persons.get(0).getId());
		org.junit.jupiter.api.Assertions.assertNull(personDao.findById(deletePerson.getId()));
	}
}