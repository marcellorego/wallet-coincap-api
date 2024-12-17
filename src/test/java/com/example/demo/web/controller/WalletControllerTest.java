package com.example.demo.web.controller;

import com.example.demo.test.SpringProfileRunnerTest;
import com.example.demo.web.api.WalletApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class WalletControllerTest extends SpringProfileRunnerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDefaultMessage() throws Exception {

        var apiPath = WalletApi.PATH;

        /**
         * {
         *   "total": 33116.95,
         *   "best_asset": "BTC",
         *   "best_performance": 180,
         *   "worst_asset": "BNB",
         *   "worst_performance": -3
         * }
         */

        this.mockMvc.perform(
                        MockMvcRequestBuilders.post(apiPath + "/{walletId}", "wallet_id")
                                .content("BTC,0.12345,37870.5058\nETH,4.89532,2004.9774\nBNB,1.05,750.244144127")
                                .contentType(MediaType.TEXT_PLAIN)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.best_asset").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.best_performance").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.worst_asset").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.worst_performance").isNotEmpty());
    }


}
