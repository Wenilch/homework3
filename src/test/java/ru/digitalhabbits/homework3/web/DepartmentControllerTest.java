package ru.digitalhabbits.homework3.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.model.DepartmentRequest;
import ru.digitalhabbits.homework3.service.DepartmentService;
import ru.digitalhabbits.homework3.utils.DepartmentHelper;
import ru.digitalhabbits.homework3.utils.PersonHelper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
@WebMvcTest(controllers = DepartmentController.class)
@AutoConfigureRestDocs
class DepartmentControllerTest {

	@MockBean
	private DepartmentService departmentService;

	@Autowired
	private MockMvc mockMvc;

	private final Gson gson = new GsonBuilder().create();

	@Test
	void departments() throws Exception {
		var department = DepartmentHelper.createDepartmentShortResponse(DepartmentHelper.createDepartment());

		when(departmentService.findAllDepartments()).thenReturn(List.of(department, DepartmentHelper.createDepartmentShortResponse(DepartmentHelper.createDepartment()), DepartmentHelper.createDepartmentShortResponse(DepartmentHelper.createDepartment())));
		mockMvc.perform(get("/api/v1/departments")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id").value(department.getId()))
				.andExpect(jsonPath("$[0].name").value(department.getName()))
				.andDo(document("departments",
						responseFields(
								fieldWithPath("[].id").description("ID"),
								fieldWithPath("[].name").description("Name")
						)));
	}

	@Test
	void department() throws Exception {
		var department = DepartmentHelper.createDepartment();
		var person = PersonHelper.createPerson(department);

		var departmentResponse = DepartmentHelper.createDepartmentResponse(department);

		when(departmentService.getDepartment(departmentResponse.getId())).thenReturn(departmentResponse);
		mockMvc.perform(get("/api/v1/departments/" + departmentResponse.getId())

				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(departmentResponse.getId()))
				.andExpect(jsonPath("$.name").value(departmentResponse.getName()))
				.andExpect(jsonPath("$.closed").value(departmentResponse.isClosed()))
				.andExpect(jsonPath("$.persons").isArray())
				.andExpect(jsonPath("$.persons[0].id").value(departmentResponse.getPersons().get(0).getId()))
				.andExpect(jsonPath("$.persons[0].fullName").value(departmentResponse.getPersons().get(0).getFullName()))
				.andDo(document("department",
						responseFields(
								fieldWithPath("id").description("ID"),
								fieldWithPath("name").description("Name"),
								fieldWithPath("closed").description("Closed"),
								fieldWithPath("persons[].id").description("ID Person"),
								fieldWithPath("persons[].fullName").description("Full name person")
						)));
	}

	@Test
	void createDepartment() throws Exception {
		var department = DepartmentHelper.createDepartment();
		var departmentRequest = new DepartmentRequest().setName(department.getName());

		when(departmentService.createDepartment(any(DepartmentRequest.class))).thenReturn(department.getId());
		mockMvc.perform(post("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(departmentRequest)))
				.andExpect(status().isCreated())
				.andDo(document("createDepartment",
						requestFields(
								fieldWithPath("name").description("Name")
						)));
	}

	@Test
	void updateDepartment() throws Exception {
		var department = DepartmentHelper.createDepartment();
		var person = PersonHelper.createPerson(department);

		var departmentRequest = new DepartmentRequest().setName(department.getName());
		var departmentResponse = DepartmentHelper.createDepartmentResponse(department);

		when(departmentService.updateDepartment(1, departmentRequest)).thenReturn(departmentResponse);
		mockMvc.perform(patch("/api/v1/departments/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(departmentRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(departmentResponse.getId()))
				.andExpect(jsonPath("$.name").value(departmentResponse.getName()))
				.andExpect(jsonPath("$.closed").value(departmentResponse.isClosed()))
				.andExpect(jsonPath("$.persons").isArray())
				.andExpect(jsonPath("$.persons[0].id").value(departmentResponse.getPersons().get(0).getId()))
				.andExpect(jsonPath("$.persons[0].fullName").value(departmentResponse.getPersons().get(0).getFullName()))
				.andDo(document("updateDepartment",
						requestFields(
								fieldWithPath("name").description("Name")
						),
						responseFields(
								fieldWithPath("id").description("ID"),
								fieldWithPath("name").description("Name"),
								fieldWithPath("closed").description("Closed"),
								fieldWithPath("persons[].id").description("ID Person"),
								fieldWithPath("persons[].fullName").description("Full name person")
						)));
	}

	@Test
	void deleteDepartment() throws Exception {
		var departmentResponse = DepartmentHelper.createDepartmentResponse(DepartmentHelper.createDepartment());
		doNothing().when(departmentService).deleteDepartment(departmentResponse.getId());

		mockMvc.perform(delete("/api/v1/departments/" + departmentResponse.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void addPersonToDepartment() throws Exception {
		var department = DepartmentHelper.createDepartment();
		var person = PersonHelper.createPerson((Department) null);

		doNothing().when(departmentService).addPersonToDepartment(department.getId(), person.getId());
		mockMvc.perform(post("/api/v1/departments/" + department.getId() + "/" + person.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void removePersonToDepartment() throws Exception {
		var department = DepartmentHelper.createDepartment();
		var person = PersonHelper.createPerson(department);

		doNothing().when(departmentService).removePersonToDepartment(person.getDepartment().getId(), person.getId());
		mockMvc.perform(delete(String.format("/api/v1/departments/%s/%s", person.getDepartment().getId(), person.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void closeDepartment() throws Exception {
		var department = DepartmentHelper.createDepartment();
		doNothing().when(departmentService).closeDepartment(department.getId());

		mockMvc.perform(post(String.format("/api/v1/departments/%s/close", department.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}
}