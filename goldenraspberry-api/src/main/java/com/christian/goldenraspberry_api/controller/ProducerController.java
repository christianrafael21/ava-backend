package com.christian.goldenraspberry_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.christian.goldenraspberry_api.dto.AwardIntervalResponseDTO;
import com.christian.goldenraspberry_api.service.ProducerService;

import lombok.*;

@RestController
@RequestMapping("/api/producers")
@RequiredArgsConstructor
public class ProducerController {

     private final ProducerService producerService;

    @GetMapping("/awards-interval")
    public ResponseEntity<AwardIntervalResponseDTO> getAwardIntervals() {
        try {
            AwardIntervalResponseDTO resultado = producerService.getAwardIntervals();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}
