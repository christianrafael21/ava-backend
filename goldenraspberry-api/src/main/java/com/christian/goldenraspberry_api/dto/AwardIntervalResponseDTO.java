package com.christian.goldenraspberry_api.dto;

import java.util.List;

public record AwardIntervalResponseDTO(

        List<ProducerIntervalResponseDTO> min,
        List<ProducerIntervalResponseDTO> max) {

}
