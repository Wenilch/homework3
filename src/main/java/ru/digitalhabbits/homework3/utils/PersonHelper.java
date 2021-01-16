package ru.digitalhabbits.homework3.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

public class PersonHelper {

	private PersonHelper() {
		throw new IllegalStateException("Utility class");
	}

	public static String createPersonFullName(Person person) {
		return person.getFirstName() + " " + person.getMiddleName() + " " + person.getLastName();
	}

	public static Person createPerson(Department department) {
		var person = new Person()
				.setId(RandomUtils.nextInt())
				.setFirstName(RandomStringUtils.randomAlphabetic(10))
				.setMiddleName(RandomStringUtils.randomAlphabetic(10))
				.setLastName(RandomStringUtils.randomAlphabetic(10))
				.setAge(RandomUtils.nextInt());

		if (department != null) {
			person.setDepartment(department);
			department.getPersons().add(person);
		}

		return person;
	}

	public static Person createPerson(PersonRequest request) {
		return new Person()
				.setId(RandomUtils.nextInt())
				.setFirstName(request.getFirstName())
				.setMiddleName(request.getMiddleName())
				.setLastName(request.getLastName())
				.setAge(request.getAge())
				.setDepartment(null);
	}

	public static PersonRequest createPersonRequest() {
		return new PersonRequest()
				.setFirstName(RandomStringUtils.randomAlphabetic(10))
				.setMiddleName(RandomStringUtils.randomAlphabetic(10))
				.setLastName(RandomStringUtils.randomAlphabetic(10))
				.setAge(RandomUtils.nextInt(1, 99));
	}

	public static PersonRequest createPersonRequest(Person person) {
		return new PersonRequest()
				.setFirstName(person.getFirstName())
				.setMiddleName(person.getMiddleName())
				.setLastName(person.getLastName())
				.setAge(person.getAge());
	}

	public static PersonResponse createPersonResponse(Integer id) {
		return new PersonResponse()
				.setId(id)
				.setAge(RandomUtils.nextInt(1, 99))
				.setFullName(RandomStringUtils.randomAlphabetic(10))
				.setDepartment(
						new DepartmentInfo()
								.setId(id)
								.setName(RandomStringUtils.randomAlphabetic(10))
				);
	}

	public static PersonResponse createPersonResponse(Person person) {
		var department = person.getDepartment();

		return new PersonResponse()
				.setId(person.getId())
				.setAge(person.getAge())
				.setFullName(PersonHelper.createPersonFullName(person))
				.setDepartment(
						department != null
								? new DepartmentInfo().setId(department.getId()).setName(department.getName())
								: null
				);
	}

	public static PersonInfo createPersonInfo(Person person) {
		return new PersonInfo()
				.setId(person.getId())
				.setFullName(createPersonFullName(person));
	}
}
