package com.ygdrazil.pingo.pingobackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygdrazil.pingo.pingobackend.auth.AuthenticationService;
import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.Role;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoGridRequest;
import com.ygdrazil.pingo.pingobackend.services.BingoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BingoGridControllerTest {

    private final String CONTENT_TEXT = "text/plain;charset=ISO-8859-1";
    private final String CONTENT_JSON = "application/json";

    private BingoService bingoService;
    private AuthenticationService authenticationService;
    private MockMvc mockMvc;

    private User mockUser;

    @BeforeEach
    public void init() {
        bingoService = mock(BingoService.class);
        authenticationService = mock(AuthenticationService.class);
        mockMvc = standaloneSetup(new BingoGridController(bingoService, authenticationService)).build();

        mockUser = User.builder().id(0L).email("toto@gmail.com").username("toto").role(Role.USER).password("toto").password("toto").build();
    }

    @Test
    public void find_noGridFound() throws Exception {
        when(bingoService.find(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo/0"))
                .andExpect(status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TEXT));
    }

    @Test
    public void find_gridFound() throws Exception {
        when(bingoService.find(any())).thenReturn(Optional.of(BingoGrid.builder().id(0L).name("test").user(mockUser).build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo/0"))
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0L))
                .andExpect(status().isOk());
    }

    @Test
    public void findByCode_noGridFound() throws Exception {
        when(bingoService.find(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo/url_code/bqdiuqzbu67438"))
                .andExpect(status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TEXT));
    }

    @Test
    public void findByCode_gridFound() throws Exception {
        when(bingoService.findByUrlCode(any())).thenReturn(Optional.of(BingoGrid.builder().name("test").urlCode("bqdiuqzbu67438").user(mockUser).build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo/url_code/bqdiuqzbu67438"))
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.urlCode").value("bqdiuqzbu67438"))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_emptyList() throws Exception {
        when(bingoService.findAll()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo"))
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_JSON))
                .andExpect(MockMvcResultMatchers.content().json("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_foundList() throws Exception {
        when(bingoService.findAll()).thenReturn(List.of(BingoGrid.builder().id(0L).name("test").user(mockUser).build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo"))
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_JSON))
                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":0,\"authorId\":0,\"urlCode\":null,\"name\":\"test\",\"dim\":0,\"gridData\":null}]"))
                .andExpect(status().isOk());
    }

    @Test
    void insert_notAuthenticated() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/bingo")
                    .content(asJsonString(CreateBingoGridRequest.builder()
                            .name("test_grid")
                            .dim(3)
                            .gridData(createListOfXElements(9))
                            .build())
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(403))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are not authenticated"));
    }

    @Test
    void insert_resourceAlreadyExists() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.insert(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bingo")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid")
                                .dim(3)
                                .gridData(createListOfXElements(9))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(409))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, resource name already exists"));
    }

    @Test
    void insert_badRequest() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.insert(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bingo")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid")
                                .dim(4)
                                .gridData(List.of("Test1"))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, bad request"));
    }

    @Test
    void insert_successful() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.insert(any(), any())).thenReturn(Optional.of(BingoGrid.builder()
                .id(0L)
                .user(mockUser)
                .name("tet_grid")
                .urlCode("osekesvoijevkn")
                .dim(3)
                .gridData(List.of("Test1"))
                .build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bingo")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid")
                                .dim(3)
                                .gridData(createListOfXElements(9))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(201))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.urlCode").value("osekesvoijevkn"));
    }

    @Test
    void modify_notAuthenticated() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/bingo/0")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid")
                                .dim(3)
                                .gridData(List.of("Test1"))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(403))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are not authenticated"));
    }

    @Test
    void modify_resourceNotFound() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.find(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/bingo/0")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid_modified")
                                .dim(3)
                                .gridData(createListOfXElements(9))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(404))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, resource not found"));
    }

    @Test
    void modify_resourceNotMadeByUser() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.find(any())).thenReturn(Optional.of(BingoGrid.builder()
                .id(0L)
                .user(User.builder().id(1L).build())
                .name("test_grid")
                .urlCode("osekesvoijevkn")
                .dim(3)
                .gridData(List.of("Test1"))
                .build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/bingo/0")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid_modified")
                                .dim(3)
                                .gridData(createListOfXElements(9))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(401))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, unauthorized access to resource"));
    }

    @Test
    void modify_badRequest() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/bingo/0")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid_modified")
                                .dim(4)
                                .gridData(List.of("Test1"))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, bad request"));
    }

    @Test
    void modify_successful() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.find(any())).thenReturn(Optional.of(BingoGrid.builder()
                .id(0L)
                .user(User.builder().id(0L).build())
                .name("test_grid")
                .urlCode("osekesvoijevkn")
                .dim(3)
                .gridData(List.of("Test1"))
                .build()));

        when(bingoService.modify(any(), any())).thenReturn(
                BingoGrid.builder()
                        .id(0L)
                        .user(User.builder().id(0L).build())
                        .name("test_grid_modified")
                        .urlCode("osekesvoijevkn")
                        .dim(3)
                        .gridData(List.of("Test1"))
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/bingo/0")
                        .content(asJsonString(CreateBingoGridRequest.builder()
                                .name("test_grid_modified")
                                .dim(3)
                                .gridData(createListOfXElements(9))
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("test_grid_modified"));
    }





    @Test
    void delete_notAuthenticated() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/bingo/sfejksfej")
                )
                .andExpect(status().is(403))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are not authenticated"));
    }

    @Test
    void delete_resourceNotFound() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.findByUrlCode(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/bingo/sfejksfej")
                )
                .andExpect(status().is(404))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, resource not found"));
    }

    @Test
    void delete_resourceNotMadeByUser() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.findByUrlCode(any())).thenReturn(Optional.of(BingoGrid.builder()
                .id(0L)
                .user(User.builder().id(1L).build())
                .name("test_grid")
                .urlCode("sfejksfej")
                .dim(3)
                .gridData(List.of("Test1"))
                .build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/bingo/sfejksfej")
                )
                .andExpect(status().is(401))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, unauthorized access to resource"));
    }

    @Test
    void delete_successful() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.findByUrlCode(any())).thenReturn(Optional.of(BingoGrid.builder()
                .id(0L)
                .user(User.builder().id(0L).build())
                .name("test_grid")
                .urlCode("sfejksfej")
                .dim(3)
                .gridData(List.of("Test1"))
                .build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/bingo/sfejksfej")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Grid deleted"));
    }

    private static List<String> createListOfXElements(int nb) {
        ArrayList<String> ls = new ArrayList<>();

        for(var i = 0; i<nb; i++) {
            ls.add("test");
        }

        return ls.stream().toList();

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}