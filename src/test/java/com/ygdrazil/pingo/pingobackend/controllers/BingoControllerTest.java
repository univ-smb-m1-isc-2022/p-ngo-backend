package com.ygdrazil.pingo.pingobackend.controllers;

import com.ygdrazil.pingo.pingobackend.responseObjects.BingoResponse;
import com.ygdrazil.pingo.pingobackend.services.BingoService;
import com.ygdrazil.pingo.pingobackend.utils.BingoGridNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BingoControllerTest {

    private BingoService bingoService;
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        bingoService = mock(BingoService.class);
        mockMvc = standaloneSetup(new BingoController(bingoService)).build();
    }

    @Test
    public void find_noGridFound() throws Exception {
        when(bingoService.find(any())).thenThrow(BingoGridNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/bingo/0"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void find_gridFound() throws Exception {
        when(bingoService.find(any())).thenReturn(BingoResponse.builder().name("test").build());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bingo/0"))
                .andExpect(status().isOk());
    }
}