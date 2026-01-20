package com.christian.goldenraspberry_api.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.christian.goldenraspberry_api.model.Filme;

public interface FilmeRepository extends JpaRepository<Filme, UUID> {
    
    List<Filme> findByWinnerTrue();

    
}
