package ru.digitalhabbits.homework3.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;
import ru.digitalhabbits.homework3.service.PersonService;
import ru.digitalhabbits.homework3.utils.PersonHelper;

import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PersonController.class)
@AutoConfigureRestDocs
class PersonControllerTest {

	@MockBean
	private PersonService personService;

	@Autowired
	private MockMvc mockMvc;

	private final Gson gson = new GsonBuilder().create();

	@Test
	void persons() throws Exception {

		var randomId = new Random().nextInt();
		var firstPerson = PersonHelper.createPersonResponse(randomId);
		var secondPerson = PersonHelper.createPersonResponse(randomId + randomId);

		when(personService.findAllPersons()).thenReturn(List.of(firstPerson, secondPerson));
		mockMvc.perform(get("/api/v1/persons")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id").value(firstPerson.getId()))
				.andExpect(jsonPath("$[0].age").value(firstPerson.getAge()))
				.andExpect(jsonPath("$[0].fullName").value(firstPerson.getFullName()))
				.andExpect(jsonPath("$[0].department.id").value(firstPerson.getDepartment().getId()))
				.andExpect(jsonPath("$[0].department.name").value(firstPerson.getDepartment().getName()))
				.andDo(document("persons",
						responseFields(
								fieldWithPath("[].id").description("ID"),
								fieldWithPath("[].age").description("Age"),
								fieldWithPath("[].fullName").description("FullName"),
								fieldWithPath("[].department.id").description("Department ID"),
								fieldWithPath("[].department.name").description("Department Name")
						)));
	}

	@Test
	void person() throws Exception {
		var randomId = RandomUtils.nextInt();
		var person = PersonHelper.createPersonResponse(randomId);
		when(personService.getPerson(randomId)).thenReturn(person);
		mockMvc.perform(get("/api/v1/persons/" + randomId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(person.getId()))
				.andExpect(jsonPath("$.age").value(person.getAge()))
				.andExpect(jsonPath("$.fullName").value(person.getFullName()))
				.andExpect(jsonPath("$.department.id").value(person.getDepartment().getId()))
				.andExpect(jsonPath("$.department.name").value(person.getDepartment().getName()))
				.andDo(document("person",
						responseFields(
								fieldWithPath("id").description("ID"),
								fieldWithPath("age").description("Age"),
								fieldWithPath("fullName").description("FullName"),
								fieldWithPath("department.id").description("Department ID"),
								fieldWithPath("department.name").description("Department Name")
						)));
	}

	@Test
	void createPerson() throws Exception {
		var personRequest = PersonHelper.createPersonRequest();
		var personResponse = PersonHelper.createPerson(personRequest);

		when(personService.createPerson(any(PersonRequest.class))).thenReturn(personResponse.getId());

		mockMvc.perform(post("/api/v1/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(gson.toJson(personRequest)))
				.andExpect(status().isCreated())
				.andDo(document("createPerson",
						requestFields(
								fieldWithPath("age").description("Age"),
								fieldWithPath("firstName").description("First Name"),
								fieldWithPath("middleName").description("Middle Name"),
								fieldWithPath("lastName").description("Last Name")
						)));
	}

	@Test
	void updatePerson() throws Exception {
		var personRequest = new PersonRequest()
				.setAge(18)
				.setFirstName(RandomStringUtils.randomAlphabetic(10))
				.setMiddleName(RandomStringUtils.randomAlphabetic(10))
				.setLastName(RandomStringUtils.randomAlphabetic(10));

		var personResponse = new PersonResponse()
				.setId(RandomUtils.nextInt())
				.setAge(18)
				.setFullName(personRequest.getLastName() + " " + personRequest.getFirstName() + " " + personRequest.getMiddleName())
				.setDepartment(new DepartmentInfo().setId(RandomUtils.nextInt()).setName(RandomStringUtils.randomAlphabetic(10)));

		when(personService.updatePerson(1, personRequest)).thenReturn(personResponse);

		mockMvc.perform(patch("/api/v1/persons/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(personRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(personResponse.getId()))
				.andExpect(jsonPath("$.age").value(personResponse.getAge()))
				.andExpect(jsonPath("$.fullName").value(personResponse.getFullName()))
				.andExpect(jsonPath("$.department.id").value(personResponse.getDepartment().getId()))
				.andExpect(jsonPath("$.department.name").value(personResponse.getDepartment().getName()))
				.andDo(document("updatePerson",
						requestFields(
								fieldWithPath("age").description("Age"),
								fieldWithPath("firstName").description("First Name"),
								fieldWithPath("middleName").description("Middle Name"),
								fieldWithPath("lastName").description("Last Name")
						),
						responseFields(
								fieldWithPath("id").description("ID"),
								fieldWithPath("age").description("Age"),
								fieldWithPath("fullName").description("FullName"),
								fieldWithPath("department.id").description("Department ID"),
								fieldWithPath("department.name").description("Department Name")
						)));
	}

	@Test
	void deletePerson() throws Exception {
		var personId = RandomUtils.nextInt();
		doNothing().when(personService).deletePerson(personId);

		mockMvc.perform(delete(String.format("/api/v1/persons/%s", personId))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(personService, times(1)).deletePerson(personId);
	}
}