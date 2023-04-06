package com.ygdrazil.pingo.pingobackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ygdrazil.pingo.pingobackend.auth.AuthenticationService;
import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.BingoSave;
import com.ygdrazil.pingo.pingobackend.models.Role;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoSaveRequest;
import com.ygdrazil.pingo.pingobackend.services.BingoSaveService;
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

class UserControllerTest {

    private final String CONTENT_TEXT = "text/plain;charset=ISO-8859-1";
    private final String CONTENT_JSON = "application/json";

    private BingoSaveService bingoSaveService;
    private BingoService bingoService;
    private AuthenticationService authenticationService;
    private MockMvc mockMvc;

    private User mockUser;
    private BingoSave mockSave;
    private BingoGrid mockGrid;

    @BeforeEach
    public void init() {
        bingoSaveService = mock(BingoSaveService.class);
        bingoService = mock(BingoService.class);
        authenticationService = mock(AuthenticationService.class);
        mockMvc = standaloneSetup(new UserController(bingoSaveService, authenticationService, bingoService)).build();

        mockUser = User.builder().id(0L).email("toto@gmail.com").username("toto").role(Role.USER).password("toto").password("toto").build();
        mockSave = BingoSave.builder().id(0L).user(mockUser).gridCompletion("").gridUrlCode("jibezfiqfvbh").build();
        mockGrid = BingoGrid.builder().id(0L).user(mockUser).gridData(createListOfXElements(9)).dim(3).name("test_grid").build();
    }

    @Test
    void findGridSaveByUserIdAndUrlCode_notAuthenticated() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/0/save/jibezfiqfvbh")
                )
                .andExpect(status().is(403))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are not authenticated"));
    }

    @Test
    void findGridSaveByUserIdAndUrlCode_resourceNotMadeByUser() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/1/save/jibezfiqfvbh")
                )
                .andExpect(status().is(401))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are trying to access the save of someone else"));
    }

    @Test
    void findGridSaveByUserIdAndUrlCode_resourceNotFound() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoSaveService.findByUserAndUrlCode(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/0/save/jibezfiqfvbh")
                )
                .andExpect(status().is(404))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, no save found for this grid"));
    }

    @Test
    void findGridSaveByUserIdAndUrlCode_successful() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoSaveService.findByUserAndUrlCode(any(), any())).thenReturn(Optional.of(mockSave));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/0/save/jibezfiqfvbh")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gridUrlCode").value("jibezfiqfvbh"));
    }

    @Test
    void insertGridSave_notAuthenticated() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/0/save")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(403))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are not authenticated"));
    }

    @Test
    void insertGridSave_resourceNotMadeByUser() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/1/save")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(401))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are trying to access the save of someone else"));
    }

    @Test
    void insertGridSave_resourceNotExisting() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoSaveService.insert(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/0/save")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(404))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, couldn't insert save for this grid"));
    }

    @Test
    void insertGridSave_successful() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoSaveService.insert(any(), any())).thenReturn(Optional.of(mockSave));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/0/save")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gridUrlCode").value("jibezfiqfvbh"));
    }

    @Test
    void modifyGridSave_notAuthenticated() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user/0/save/jibezfiqfvbh")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(403))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are not authenticated"));
    }

    @Test
    void modifyGridSave_resourceNotMadeByUser() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user/1/save/jibezfiqfvbh")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(401))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are trying to access the save of someone else"));
    }

    @Test
    void modifyGridSave_resourceNotExisting() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoSaveService.modify(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user/0/save/jibezfiqfvbh")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(404))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, no save found for this grid"));
    }

    @Test
    void modifyGridSave_successful() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoSaveService.modify(any(), any())).thenReturn(Optional.of(mockSave));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user/0/save/jibezfiqfvbh")
                        .content(asJsonString(CreateBingoSaveRequest.builder()
                                .urlCode("jibezfiqfvbh")
                                .gridCompletion("")
                                .build())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gridUrlCode").value("jibezfiqfvbh"));
    }

    @Test
    void findAllGrids_notAuthenticated() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/0/bingo")
                )
                .andExpect(status().is(403))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are not authenticated"));
    }

    @Test
    void findAllGrids_resourceNotMadeByUser() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/1/bingo")
                )
                .andExpect(status().is(401))
                .andExpect(content().contentType(CONTENT_TEXT))
                .andExpect(content().string("Error, you are trying to access grids that are not your own"));
    }

    @Test
    void findAllGrids_successful() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));
        when(bingoService.findAllByUser(any())).thenReturn(List.of(mockGrid));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/0/bingo")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(content().json("[{\"id\":0,\"authorId\":0,\"urlCode\":null,\"name\":\"test_grid\",\"dim\":3,\"gridData\":[\"test\",\"test\",\"test\",\"test\",\"test\",\"test\",\"test\",\"test\",\"test\"]}]"));
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