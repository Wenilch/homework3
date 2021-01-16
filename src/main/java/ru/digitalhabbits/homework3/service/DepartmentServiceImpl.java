package ru.digitalhabbits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.exceptions.ConflictException;
import ru.digitalhabbits.homework3.model.DepartmentRequest;
import ru.digitalhabbits.homework3.model.DepartmentResponse;
import ru.digitalhabbits.homework3.model.DepartmentShortResponse;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.utils.PersonHelper;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl
		implements DepartmentService {
	private final DepartmentDao departmentDao;

	private final PersonDao personDao;

	@Nonnull
	@Override
	public List<DepartmentShortResponse> findAllDepartments() {
		return departmentDao.findAll()
				.stream()
				.map(this::createDepartmentShortResponse)
				.collect(Collectors.toList());
	}

	private DepartmentShortResponse createDepartmentShortResponse(Department department) {
		return new DepartmentShortResponse()
				.setId(department.getId())
				.setName(department.getName());
	}

	@Nonnull
	@Override
	public DepartmentResponse getDepartment(@Nonnull Integer id) {
		var department = Optional.ofNullable(departmentDao.findById(id))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Департамент с идентификатором %d не найдет", id)));

		return createDepartmentResponse(department);
	}

	@Transactional
	@Nonnull
	@Override
	public Integer createDepartment(@Nonnull DepartmentRequest request) {
		return departmentDao.create(new Department().setName(request.getName())).getId();
	}

	@Transactional
	@Nonnull
	@Override
	public DepartmentResponse updateDepartment(@Nonnull Integer id, @Nonnull DepartmentRequest request) {
		var department = Optional.ofNullable(departmentDao.findById(id))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Департамент с идентификатором %d не найдет", id)));

		return createDepartmentResponse(departmentDao.update(department.setName(request.getName())));
	}

	private DepartmentResponse createDepartmentResponse(Department department) {
		var personInfoList = department.getPersons().stream()
				.map(person -> new PersonInfo().setId(person.getId()).setFullName(PersonHelper.createPersonFullName(person)))
				.collect(Collectors.toList());

		return new DepartmentResponse()
				.setId(department.getId())
				.setName(department.getName())
				.setClosed(department.isClosed())
				.setPersons(personInfoList);

	}

	@Transactional
	@Override
	public void deleteDepartment(@Nonnull Integer id) {
		var department = departmentDao.findById(id);
		if (department != null) {
			department.getPersons().forEach(person -> person.setDepartment(null));
			departmentDao.update(department);
			departmentDao.delete(id);
		}
	}

	@Transactional
	@Override
	public void addPersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
		var department = Optional.ofNullable(departmentDao.findById(departmentId))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Департамент с идентификатором %d не найдет.", departmentId)));

		if (department.isClosed()) {
			throw new ConflictException("Департамент закрыт.");
		}

		var person = Optional.ofNullable(personDao.findById(personId))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Человек с идентификатором %d не найдет.", personId)));

		person.setDepartment(department);
		department.getPersons().add(person);
		departmentDao.update(department);
	}

	@Transactional
	@Override
	public void removePersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
		var department = Optional.ofNullable(departmentDao.findById(departmentId))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Департамент с идентификатором %d не найдет.", departmentId)));

		var person = personDao.findById(personId);
		if (person != null && department.getPersons().contains(person)) {
			person.setDepartment(null);
			department.getPersons().remove(person);
			departmentDao.update(department);
		}
	}

	@Transactional
	@Override
	public void closeDepartment(@Nonnull Integer id) {
		var department = Optional.ofNullable(departmentDao.findById(id))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Департамент с идентификатором %d не найдет.", id)));

		if (department.getPersons() != null) {
			department.getPersons().forEach(person -> person.setDepartment(null));
			department.getPersons().clear();
		}

		department.setClosed(true);
		departmentDao.update(department);
	}
}
