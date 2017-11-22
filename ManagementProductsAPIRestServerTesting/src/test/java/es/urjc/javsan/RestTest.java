package es.urjc.javsan;

/**
 * Uses JsonPath: http://goo.gl/nwXpb, Hamcrest and MockMVC
 */

import es.urjc.javsan.master.configuration.ProductsDB;
import es.urjc.javsan.master.controllers.ProductsController;
import es.urjc.javsan.master.entities.Product;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class RestTest {

	@Mock
	private ProductsDB productDB;

	@InjectMocks
	private ProductsController productRestController;

	private MockMvc mockMvc;

	private Product productA, productB;

	private ObjectMapper obj;

	@Before
	public void setup() throws Exception {
		productRestController = new ProductsController();

		productA = new Product();
		productA.setCode(1);
		productA.setName("Code 01");
		productA.setDescription("Code 01");
		productA.setPrice(12.12);

		productB = new Product();
		productB.setCode(2);
		productB.setName("Code 02");
		productB.setDescription("Code 02");
		productB.setPrice(15.15f);

		obj = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(productRestController).build();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetProducts() throws Exception {

		List<Product> products = new ArrayList<>();
		products.add(productA);
		products.add(productB);

		when(productDB.findAll()).then(answer -> {
			return products;
		});

		mockMvc.perform(get("/list").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.size()", is(2)));
	}

	@Test
	public void testGetProduct() throws Exception {

		when(productDB.get(productA.getCode())).then(answer -> {
			return productA;
		});

		mockMvc.perform(get("/product?code=" + productA.getCode()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted()).andExpect(jsonPath("$.code", is(productA.getCode())))
				.andExpect(jsonPath("$.name", is(productA.getName())))
				.andExpect(jsonPath("$.description", is(productA.getDescription())))
				.andExpect(jsonPath("$.price", is(productA.getPrice())));
	}

	@Test
	public void testInsertProduct() throws Exception {
		mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON).content(obj.writeValueAsString(productA)))
				.andExpect(status().isCreated()).andExpect(content().string("Product Added!"));
	}

	@Test
	public void testDeleteProduct() throws Exception {
		mockMvc.perform(get("/delete?code=" + productA.getCode()).contentType(MediaType.APPLICATION_JSON)
				.content(obj.writeValueAsString(productA))).andExpect(status().isAccepted())
				.andExpect(content().string("Product Deleted!!"));
	}

	@Test
	public void testEditProduct() throws Exception {
		mockMvc.perform(post("/edit").contentType(MediaType.APPLICATION_JSON).content(obj.writeValueAsString(productA)))
				.andExpect(status().isCreated()).andExpect(content().string("Product Edited!"));
	}
}
