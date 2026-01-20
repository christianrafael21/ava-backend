package com.christian.goldenraspberry_api.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.christian.goldenraspberry_api.dto.AwardIntervalResponseDTO;
import com.christian.goldenraspberry_api.dto.ProducerIntervalResponseDTO;
import com.christian.goldenraspberry_api.model.Filme;
import com.christian.goldenraspberry_api.repository.FilmeRepository;

import lombok.*;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final FilmeRepository filmeRepository;

    public AwardIntervalResponseDTO getAwardIntervals() {

        List<Filme> filmesVencedores = filmeRepository.findByWinnerTrue();

        Map<String, List<Integer>> producerYears = new HashMap<>();

        for (Filme filme : filmesVencedores) {

            String producersString = filme.getProducers();

            List<String> nomes = splitProducers(producersString);

            Integer ano = filme.getAno();

            for (String produtor : nomes) {
                if (!producerYears.containsKey(produtor)) {
                    producerYears.put(produtor, new ArrayList<>());
                }
                producerYears.get(produtor).add(ano);
            }
        }

        List<ProducerIntervalResponseDTO> todosIntervalos = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entrada : producerYears.entrySet()) {
            String produtorNome = entrada.getKey();
            List<Integer> anosVencedores = entrada.getValue();

            if (anosVencedores.size() < 2) {
                continue;
            }

            Collections.sort(anosVencedores);

            for (int i = 0; i < anosVencedores.size() - 1; i++) {
                int anoAnterior = anosVencedores.get(i);
                int anoSeguinte = anosVencedores.get(i + 1);
                int intervalo = anoSeguinte - anoAnterior;

                ProducerIntervalResponseDTO dto = new ProducerIntervalResponseDTO(
                        produtorNome,
                        intervalo,
                        anoAnterior,
                        anoSeguinte);
                todosIntervalos.add(dto);
            }
        }


        if (todosIntervalos.isEmpty()) {

            return new AwardIntervalResponseDTO(new ArrayList<>(), new ArrayList<>());
        }


        int menorIntervalo = todosIntervalos.get(0).interval(); 
        int maiorIntervalo = todosIntervalos.get(0).interval(); 


        for (ProducerIntervalResponseDTO dto : todosIntervalos) {
            if (dto.interval() < menorIntervalo) {
                menorIntervalo = dto.interval();
            }
        }
 
        for (ProducerIntervalResponseDTO dto : todosIntervalos) {
            if (dto.interval() > maiorIntervalo) {
                maiorIntervalo = dto.interval();
            }
        }

        List<ProducerIntervalResponseDTO> min = new ArrayList<>();
        for (ProducerIntervalResponseDTO dto : todosIntervalos) {
            if (dto.interval() == menorIntervalo) {
                min.add(dto);
            }
        }

        List<ProducerIntervalResponseDTO> max = new ArrayList<>();
        for (ProducerIntervalResponseDTO dto : todosIntervalos) {
            if (dto.interval() == maiorIntervalo) {
                max.add(dto);
            }
        }

        
        return new AwardIntervalResponseDTO(min, max);
    }

    private List<String> splitProducers(String producersString) {

        String[] partes = producersString.split(",\\s*and\\s+|,\\s+|\\s+and\\s+");
        List<String> nomes = new ArrayList<>();

        for (String parte : partes) {
            String nomeLimpo = parte.trim();

            if (!nomeLimpo.isEmpty()) {
                nomes.add(nomeLimpo);
            }
        }

        return nomes;

    }

}
