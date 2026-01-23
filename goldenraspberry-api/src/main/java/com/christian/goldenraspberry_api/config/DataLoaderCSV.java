package com.christian.goldenraspberry_api.config;

import java.io.*;
import org.slf4j.*;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.christian.goldenraspberry_api.model.Filme;
import com.christian.goldenraspberry_api.repository.FilmeRepository;

import lombok.*;

@Component
@RequiredArgsConstructor
public class DataLoaderCSV implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderCSV.class);
    private final FilmeRepository filmeRepository;

    @Override
    public void run(String... args) throws Exception {
        loadData();
    }

    private void loadData() {
        logger.info("Iniciando a carga de dados do arquivo CSV");
        
        try {
            ClassPathResource resource = new ClassPathResource("data/movielist.csv");
            
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                String linha;
                boolean primeiraLinha = true;

                while ((linha = reader.readLine()) != null) {
                    if (primeiraLinha) {
                        primeiraLinha = false;
                        continue;
                    }

                    String[] campos = linha.split(";");
                    
                    if (campos.length >= 5) {
                        try {
                            Filme filme = new Filme();
                            filme.setAno(Integer.valueOf(campos[0].trim()));
                            filme.setTitle(campos[1].trim());
                            filme.setStudios(campos[2].trim());
                            filme.setProducers(campos[3].trim());
                            filme.setWinner("yes".equalsIgnoreCase(campos[4].trim()));
                            
                            filmeRepository.save(filme);
                        } catch (NumberFormatException e) {
                            logger.warn("Erro ao processar linha: {}", linha, e);
                        }
                    }
                }

                logger.info("Carga concluída");
            }

        } catch (IOException e) {
            logger.error("Erro ao carregar dados do arquivo CSV", e);
            throw new RuntimeException("Falha ao carregar dados iniciais", e);
        }
    }
}