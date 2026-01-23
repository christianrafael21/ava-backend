package com.christian.goldenraspberry_api.dto;
import java.util.List;

public record ResultadoIntervaloDTO(
        int menorIntervalo,
        int maiorIntervalo,
        List<ProducerIntervalResponseDTO> min,
        List<ProducerIntervalResponseDTO> max) {}
