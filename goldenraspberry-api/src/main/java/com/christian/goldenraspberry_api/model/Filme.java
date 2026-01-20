package com.christian.goldenraspberry_api.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "filme")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filme {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String studios;

    @Column(nullable = false, length = 500)
    private String producers;

    @Column(nullable = false)
    private Boolean winner;
    
}
