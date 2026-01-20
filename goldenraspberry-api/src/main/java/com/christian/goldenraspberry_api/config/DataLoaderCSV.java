package com.christian.goldenraspberry_api.config;

import java.io.*; 
import java.nio.charset.StandardCharsets;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.christian.goldenraspberry_api.model.Filme;
import com.christian.goldenraspberry_api.repository.FilmeRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoaderCSV implements CommandLineRunner {

    private final FilmeRepository filmeRepository;

    @Override
    public void run(String... args) throws Exception {
        loadData();
    }

    private void loadData() {
        try {
            ClassPathResource resource = new ClassPathResource("data/movielist.csv");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );

            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] campos = linha.split(";");
                
                if (campos.length >= 5) {
                    Filme filme = new Filme();
                    filme.setAno(Integer.valueOf(campos[0].trim()));
                    filme.setTitle(campos[1].trim());
                    filme.setStudios(campos[2].trim());
                    filme.setProducers(campos[3].trim());
                    filme.setWinner("yes".equalsIgnoreCase(campos[4].trim()));
                    
                    filmeRepository.save(filme);
                }
            }

            reader.close();

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar CSV: " + e.getMessage());
        }
    }
}