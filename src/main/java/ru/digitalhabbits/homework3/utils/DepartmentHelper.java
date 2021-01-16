package ru.digitalhabbits.homework3.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.model.DepartmentResponse;
import ru.digitalhabbits.homework3.model.DepartmentShortResponse;
import ru.digitalhabbits.homework3.model.PersonInfo;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class DepartmentHelper {

	public static Department createDepartment() {
		return new Department()
				.setId(RandomUtils.nextInt())
				.setName(RandomStringUtils.randomAlphabetic(10))
				.setPersons(new ArrayList<>())
				.setClosed(false);
	}

	public static DepartmentResponse createDepartmentResponse(Department department) {
		var personInfoList = department.getPersons()
				.stream()
				.map(PersonHelper::createPersonInfo)
				.collect(Collectors.toList());

		return new DepartmentResponse()
				.setId(department.getId())
				.setName(department.getName())
				.setClosed(department.isClosed())
				.setPersons(personInfoList);
	}

	public static DepartmentShortResponse createDepartmentShortResponse(Department department) {
		return new DepartmentShortResponse()
				.setId(department.getId())
				.setName(department.getName());
	}
}
