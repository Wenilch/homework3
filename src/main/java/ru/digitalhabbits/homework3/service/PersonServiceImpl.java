package ru.digitalhabbits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;
import ru.digitalhabbits.homework3.utils.PersonHelper;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl
		implements PersonService {

	private final PersonDao personDao;

	@Nonnull
	@Override
	public List<PersonResponse> findAllPersons() {
		return personDao.findAll().stream()
				.map(PersonHelper::createPersonResponse)
				.collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public PersonResponse getPerson(@Nonnull Integer id) {
		var person = Optional.ofNullable(personDao.findById(id))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Человек с идентификатором %d не найдет.", id)));

		return PersonHelper.createPersonResponse(person);
	}

	@Transactional
	@Nonnull
	@Override
	public Integer createPerson(@Nonnull PersonRequest request) {
		var person = new Person()
				.setFirstName(request.getFirstName())
				.setMiddleName(request.getMiddleName())
				.setLastName(request.getLastName())
				.setAge(request.getAge());

		return personDao.create(person).getId();
	}

	@Transactional
	@Nonnull
	@Override
	public PersonResponse updatePerson(@Nonnull Integer id, @Nonnull PersonRequest request) {
		var person = Optional.ofNullable(personDao.findById(id))
				.orElseThrow(() -> new EntityNotFoundException(String.format("Человек с идентификатором %d не найдет.", id)));

		var requestFirstName = request.getFirstName();
		if (requestFirstName != null && !requestFirstName.equals(person.getFirstName())) {
			person.setFirstName(requestFirstName);
		}

		var requestLastName = request.getLastName();
		if (requestLastName != null && !requestLastName.equals(person.getLastName())) {
			person.setLastName(requestLastName);
		}

		var requestMiddleName = request.getMiddleName();
		if (requestMiddleName != null && !requestMiddleName.equals(person.getMiddleName())) {
			person.setMiddleName(requestMiddleName);
		}

		var requestAge = request.getAge();
		if (requestAge != null && !requestAge.equals(person.getAge())) {
			person.setAge(requestAge);
		}

		personDao.update(person);

		return PersonHelper.createPersonResponse(person);
	}

	@Transactional
	@Override
	public void deletePerson(@Nonnull Integer id) {
		Optional.ofNullable(personDao.findById(id))
				.ifPresent(person -> personDao.delete(person.getId()));
	}
}
