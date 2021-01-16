package ru.digitalhabbits.homework3.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "department")
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 80, unique = true)
	private String name;

	@Column(nullable = false, columnDefinition = "BOOL NOT NULL DEFAULT FALSE")
	private boolean closed;

	@OneToMany(mappedBy = "department",
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Person> persons = new ArrayList<>();
}