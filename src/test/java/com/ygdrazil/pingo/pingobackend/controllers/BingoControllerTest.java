package com.ygdrazil.pingo.pingobackend.controllers;

import com.ygdrazil.pingo.pingobackend.auth.AuthenticationService;
import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.services.BingoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BingoControllerTest {

    private BingoService bingoService;
    private AuthenticationService authenticationService;
    private MockMvc mockMvc;

    private User mockUser;

    @BeforeEach
    public void init() {
        bingoService = mock(BingoService.class);
        authenticationService = mock(AuthenticationService.class);
        mockMvc = standaloneSetup(new BingoController(bingoService, authenticationService)).build();

        mockUser = User.builder().id(0L).build();
    }

    @Test
    public void find_noGridFound() throws Exception {
        when(bingoService.find(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/bingo/0"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void find_gridFound() throws Exception {
        when(bingoService.find(any())).thenReturn(Optional.of(BingoGrid.builder().name("test").user(mockUser).build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo/0"))
                .andExpect(status().isOk());
    }
}