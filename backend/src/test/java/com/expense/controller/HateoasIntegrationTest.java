package com.expense.controller;

import com.expense.dto.request.LoginRequestDTO;
import com.expense.dto.request.UserRequestDTO;
import com.expense.model.User;
import com.expense.security.JwtTokenProvider;
import com.expense.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração para validar a implementação HATEOAS
 * Demonstra como os links hipermídia funcionam em todos os endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HateoasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    private String jwtToken;
    private User testUser;

    @BeforeEach
    public void setup() throws Exception {
        // Criar usuário de teste
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setName("Test User");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");

        testUser = userService.createUser(userRequest);

        // Autenticar e obter token
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password123"
            )
        );
        jwtToken = tokenProvider.generateToken("test@example.com");
    }

    @Test
    public void testLoginReturnsHateoasLinks() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$._links.user.href", containsString("/api/users/")))
                .andExpect(jsonPath("$._links.users.href", endsWith("/api/users")))
                .andExpect(jsonPath("$._links.expenses.href", containsString("/api/expenses/user/")))
                .andExpect(jsonPath("$._links.categories.href", endsWith("/api/categories")));
    }

    @Test
    public void testRegisterReturnsHateoasLinks() throws Exception {
        UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setName("New User");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$._links.user.href", containsString("/api/users/")))
                .andExpect(jsonPath("$._links.login.href", endsWith("/api/auth/login")))
                .andExpect(jsonPath("$._links.users.href", endsWith("/api/users")));
    }

    @Test
    public void testGetUserByIdReturnsHateoasLinks() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/users/" + testUser.getId())))
                .andExpect(jsonPath("$._links.users.href", endsWith("/api/users")))
                .andExpect(jsonPath("$._links['user-by-email'].href", containsString("/api/users/email/")))
                .andExpect(jsonPath("$._links.update.href", containsString("/api/users/" + testUser.getId())))
                .andExpect(jsonPath("$._links.delete.href", containsString("/api/users/" + testUser.getId())))
                .andExpect(jsonPath("$._links.expenses.href", containsString("/api/expenses/user/" + testUser.getId())));
    }

    @Test
    public void testGetAllUsersReturnsHateoasLinks() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userModelList", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$._embedded.userModelList[0]._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._embedded.userModelList[0]._links.users.href", endsWith("/api/users")))
                .andExpect(jsonPath("$._embedded.userModelList[0]._links.expenses.href", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/users")));
    }

    @Test
    public void testGetAllCategoriesReturnsHateoasLinks() throws Exception {
        mockMvc.perform(get("/api/categories")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/categories")))
                .andExpect(jsonPath("$._embedded.categoryModelList[*]._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._embedded.categoryModelList[*]._links.categories.href", notNullValue()))
                .andExpect(jsonPath("$._embedded.categoryModelList[*]._links.expenses.href", notNullValue()));
    }

    @Test
    public void testGetAllExpensesReturnsHateoasLinks() throws Exception {
        mockMvc.perform(get("/api/expenses")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/expenses")));
    }

    @Test
    public void testHateoasLinksAreNavigable() throws Exception {
        // 1. Login e obter links
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        String userLink = objectMapper.readTree(loginResponse)
                .get("_links").get("user").get("href").asText();

        // 2. Navegar para o link do usuário
        mockMvc.perform(get(userLink)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", is(userLink)));
    }

    @Test
    public void testExpenseHateoasLinksIncludeUserAndCategory() throws Exception {
        // Este teste verifica que as despesas incluem links para usuário e categoria
        mockMvc.perform(get("/api/expenses")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/expenses")));
        
        // Se houver despesas, verificar os links
        MvcResult result = mockMvc.perform(get("/api/expenses")
                .header("Authorization", "Bearer " + jwtToken))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        if (objectMapper.readTree(response).has("_embedded")) {
            mockMvc.perform(get("/api/expenses")
                    .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(jsonPath("$._embedded.expenseModelList[0]._links.user.href", notNullValue()))
                    .andExpect(jsonPath("$._embedded.expenseModelList[0]._links.category.href", notNullValue()))
                    .andExpect(jsonPath("$._embedded.expenseModelList[0]._links['user-expenses'].href", notNullValue()))
                    .andExpect(jsonPath("$._embedded.expenseModelList[0]._links['category-expenses'].href", notNullValue()));
        }
    }

    @Test
    public void testCreateUserReturnsHateoasLinks() throws Exception {
        UserRequestDTO newUser = new UserRequestDTO();
        newUser.setName("HATEOAS Test User");
        newUser.setEmail("hateoas@example.com");
        newUser.setPassword("password123");

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/users/")))
                .andExpect(jsonPath("$._links.users.href", endsWith("/api/users")))
                .andExpect(jsonPath("$._links.expenses.href", containsString("/api/expenses/user/")));
    }

    @Test
    public void testUpdateUserReturnsHateoasLinks() throws Exception {
        UserRequestDTO updateRequest = new UserRequestDTO();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("test@example.com");
        updateRequest.setPassword("password123");

        mockMvc.perform(put("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/users/" + testUser.getId())))
                .andExpect(jsonPath("$._links.users.href", endsWith("/api/users")));
    }
}
