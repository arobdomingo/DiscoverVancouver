package com.discovervancouver.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.discovervancouver.service.RecommendationService;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @Test
    void invalidRecommendationParametersReturnBadRequest()
        throws Exception {

        mockMvc.perform(get("/api/recommendations")
                .param("lat", "200")
                .param("lng", "-300")
                .param("maxWalkingMinutes", "0")
                .param("budgetPreference", "impossible"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                .value("Request parameter validation failed"))
            .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    void invalidParameterTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/recommendations")
                .param("lat", "not-a-number")
                .param("lng", "-123.1207"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                .value("A request parameter has the wrong type"))
            .andExpect(jsonPath("$.errors.lat").exists());
    }
}
