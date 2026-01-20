package com.christian.goldenraspberry_api.dto;

public record ProducerIntervalResponseDTO(
        String producer, Integer interval, Integer previousWin, Integer followingWin) {

}
