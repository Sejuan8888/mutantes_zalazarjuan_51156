package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.DnaRequest;
import org.example.dto.StatsResponse;
import org.example.service.MutantService;
import org.example.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MutantController.class)
@DisplayName("Pruebas de Integración para MutantController")
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    private final String[] mutantDna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
    private final String[] humanDna = {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};

    @Test
    @DisplayName("POST /mutant - Debe devolver 200 OK para ADN de mutante")
    void checkMutant_WhenDnaIsMutant_ShouldReturnOk() throws Exception {
        // Arrange
        when(mutantService.analyzeDna(any())).thenReturn(true);
        DnaRequest request = new DnaRequest(mutantDna);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant - Debe devolver 403 Forbidden para ADN de humano")
    void checkMutant_WhenDnaIsHuman_ShouldReturnForbidden() throws Exception {
        // Arrange
        when(mutantService.analyzeDna(any())).thenReturn(false);
        DnaRequest request = new DnaRequest(humanDna);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /mutant - Debe devolver 400 Bad Request para ADN inválido (no cuadrado)")
    void checkMutant_WhenDnaIsInvalid_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String[] invalidDna = {"ATGC", "G", "C"};
        DnaRequest request = new DnaRequest(invalidDna);
        
        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant - Debe devolver 400 Bad Request para body vacío")
    void checkMutant_WhenBodyIsEmpty_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /stats - Debe devolver 200 OK y las estadísticas correctas")
    void getStats_ShouldReturnOkWithStats() throws Exception {
        // Arrange
        StatsResponse stats = new StatsResponse(40L, 100L, 0.4);
        when(statsService.getStats()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }

    @Test
    @DisplayName("GET /stats - Debe devolver 200 OK con estadísticas en cero si no hay datos")
    void getStats_WhenNoData_ShouldReturnOkWithZeros() throws Exception {
        // Arrange
        StatsResponse stats = new StatsResponse(0L, 0L, 0.0);
        when(statsService.getStats()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));
    }
}
