package com.christian.goldenraspberry_api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.christian.goldenraspberry_api.dto.AwardIntervalResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ProducerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornarIntervalosDePremioDosProducers() throws Exception {
        // Act
        String jsonResponse = mockMvc.perform(get("/api/producers/awards-interval"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AwardIntervalResponseDTO resultado = objectMapper.readValue(
            jsonResponse, 
            AwardIntervalResponseDTO.class
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.min()).isNotEmpty();
        assertThat(resultado.max()).isNotEmpty();

        // Valida estrutura do min
        var primeiroMin = resultado.min().get(0);
        assertThat(primeiroMin.producer()).isNotBlank();
        assertThat(primeiroMin.interval()).isGreaterThan(0);
        assertThat(primeiroMin.previousWin()).isGreaterThan(0);
        assertThat(primeiroMin.followingWin()).isGreaterThan(primeiroMin.previousWin());

        // Valida estrutura do max
        var primeiroMax = resultado.max().get(0);
        assertThat(primeiroMax.producer()).isNotBlank();
        assertThat(primeiroMax.interval()).isGreaterThan(0);
        assertThat(primeiroMax.previousWin()).isGreaterThan(0);
        assertThat(primeiroMax.followingWin()).isGreaterThan(primeiroMax.previousWin());

        // Valida que min é menor ou igual ao max
        int menorIntervalo = resultado.min().get(0).interval();
        int maiorIntervalo = resultado.max().get(0).interval();
        assertThat(menorIntervalo).isLessThanOrEqualTo(maiorIntervalo);
    }

    @Test
    void deveCarregarDadosDoCSVCorretamente() throws Exception {
        // Act
        String jsonResponse = mockMvc.perform(get("/api/producers/awards-interval"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AwardIntervalResponseDTO resultado = objectMapper.readValue(
            jsonResponse, 
            AwardIntervalResponseDTO.class
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.min()).isNotEmpty();
        assertThat(resultado.max()).isNotEmpty();
    }

    @Test
    void deveRetornarJSONComFormatoCorreto() throws Exception {
        // Act
        String jsonResponse = mockMvc.perform(get("/api/producers/awards-interval"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AwardIntervalResponseDTO resultado = objectMapper.readValue(
            jsonResponse, 
            AwardIntervalResponseDTO.class
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.min()).isNotNull();
        assertThat(resultado.max()).isNotNull();
        
        resultado.min().forEach(dto -> {
            assertThat(dto.producer()).isNotNull();
            assertThat(dto.interval()).isNotNull();
            assertThat(dto.previousWin()).isNotNull();
            assertThat(dto.followingWin()).isNotNull();
        });
        
        resultado.max().forEach(dto -> {
            assertThat(dto.producer()).isNotNull();
            assertThat(dto.interval()).isNotNull();
            assertThat(dto.previousWin()).isNotNull();
            assertThat(dto.followingWin()).isNotNull();
        });
    }
}