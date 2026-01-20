package com.christian.goldenraspberry_api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.christian.goldenraspberry_api.controller.ProducerController;
import com.christian.goldenraspberry_api.dto.AwardIntervalResponseDTO;
import com.christian.goldenraspberry_api.repository.FilmeRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
public class ProducerControllerTest {

    @Autowired
    private ProducerController producerController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmeRepository filmeRepository;

    @Test
    public void deveRetornarIntervalosDePremioDosProducers() {
        var response = producerController.getAwardIntervals();
        
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        
        var resultado = response.getBody();
        assertThat(resultado.min()).isNotEmpty();
        assertThat(resultado.max()).isNotEmpty();
        
        var menorIntervalo = resultado.min().get(0);
        assertThat(menorIntervalo.producer()).isNotBlank();
        assertThat(menorIntervalo.interval()).isPositive();
        assertThat(menorIntervalo.previousWin()).isLessThan(menorIntervalo.followingWin());
        
        var maiorIntervalo = resultado.max().get(0);
        assertThat(maiorIntervalo.producer()).isNotBlank();
        assertThat(maiorIntervalo.interval()).isPositive();
        assertThat(maiorIntervalo.previousWin()).isLessThan(maiorIntervalo.followingWin());
        
        assertThat(menorIntervalo.interval()).isLessThanOrEqualTo(maiorIntervalo.interval());
    }

    
    @Test
    public void deveCarregarDadosDoCSVCorretamente() {
        assertThat(filmeRepository.count()).isGreaterThan(0);
        assertThat(filmeRepository.findByWinnerTrue()).isNotEmpty();
    }


    @Test
    public void deveCalcularIntervaloMinCorreto() {
        var dados = producerController.getAwardIntervals().getBody();
        var produtor = dados.min().get(0);
        
        int intervaloEsperado = produtor.followingWin() - produtor.previousWin();
        assertThat(produtor.interval()).isEqualTo(intervaloEsperado);
    }


    @Test
    public void deveCalcularIntervaloMaxCorreto() {
        var dados = producerController.getAwardIntervals().getBody();
        var produtor = dados.max().get(0);
        
        int intervaloEsperado = produtor.followingWin() - produtor.previousWin();
        assertThat(produtor.interval()).isEqualTo(intervaloEsperado);
    }


    @Test
    public void deveSerializarParaJSONCorretamente() {
        var dados = producerController.getAwardIntervals().getBody();
        
        String json = objectMapper.writeValueAsString(dados);
        assertThat(json).contains("min", "max", "producer", "interval");
        
        var deserializado = objectMapper.readValue(json, AwardIntervalResponseDTO.class);
        assertThat(deserializado.min()).hasSameSizeAs(dados.min());
        assertThat(deserializado.max()).hasSameSizeAs(dados.max());
    }
}
