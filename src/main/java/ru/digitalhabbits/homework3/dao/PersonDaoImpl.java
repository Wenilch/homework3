package ru.digitalhabbits.homework3.dao;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Person;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PersonDaoImpl
		implements PersonDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Person findById(@Nonnull Integer id) {
		return entityManager.find(Person.class, id);
	}

	@Override
	public List<Person> findAll() {
		return entityManager
				.createQuery("SELECT p FROM Person p", Person.class)
				.getResultList();
	}

	@Override
	public Person create(Person entity) {
		entityManager.persist(entity);

		return entity;
	}

	@Transactional
	@Override
	public Person update(Person entity) {
		return entityManager.merge(entity);
	}

	@Transactional
	@Override
	public Person delete(Integer integer) {
		var person = findById(integer);
		if (person != null) {
			entityManager.remove(person);
		}

		return person;
	}
}
