package ru.digitalhabbits.homework3.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Department;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class DepartmentDaoImpl
		implements DepartmentDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Department findById(@Nonnull Integer integer) {
		return entityManager.find(Department.class, integer);
	}

	@Override
	public List<Department> findAll() {
		return entityManager
				.createQuery("SELECT d FROM Department d", Department.class)
				.getResultList();
	}

	@Override
	public Department create(Department entity) {
		entityManager.persist(entity);

		return entity;
	}

	@Transactional
	@Override
	public Department update(Department entity) {
		return entityManager.merge(entity);
	}

	@Transactional
	@Override
	public Department delete(Integer integer) {
		var department = findById(integer);
		if (department != null) {
			entityManager.remove(department);
		}

		return department;
	}
}
