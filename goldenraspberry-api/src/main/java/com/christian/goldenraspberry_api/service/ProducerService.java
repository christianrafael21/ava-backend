package com.christian.goldenraspberry_api.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.christian.goldenraspberry_api.dto.AwardIntervalResponseDTO;
import com.christian.goldenraspberry_api.dto.ProducerIntervalResponseDTO;
import com.christian.goldenraspberry_api.dto.ResultadoIntervaloDTO;
import com.christian.goldenraspberry_api.model.Filme;
import com.christian.goldenraspberry_api.repository.FilmeRepository;

import lombok.*;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final FilmeRepository filmeRepository;

    public AwardIntervalResponseDTO getAwardIntervals() {

        List<Filme> filmesVencedores = filmeRepository.findByWinnerTrue();

        Map<String, List<Integer>> produtoresAno = construirMapaAnosPorProdutor(filmesVencedores);
        List<ProducerIntervalResponseDTO> todosIntervalos = calcularIntervalos(produtoresAno);

        if (todosIntervalos.isEmpty()) {
            return new AwardIntervalResponseDTO(new ArrayList<>(), new ArrayList<>());
        }

         ResultadoIntervaloDTO resultado = encontrarMinMax(todosIntervalos);

        return new AwardIntervalResponseDTO(resultado.min(), resultado.max());
    }

    private ResultadoIntervaloDTO encontrarMinMax(List<ProducerIntervalResponseDTO> todosIntervalos) {
        int menorIntervalo = Integer.MAX_VALUE;
        int maiorIntervalo = Integer.MIN_VALUE;
        List<ProducerIntervalResponseDTO> min = new ArrayList<>();
        List<ProducerIntervalResponseDTO> max = new ArrayList<>();

        for (ProducerIntervalResponseDTO dto : todosIntervalos) {
            int intervalo = dto.interval();

            if (intervalo < menorIntervalo) {
                menorIntervalo = intervalo;
                min.clear();
                min.add(dto);
            } else if (intervalo == menorIntervalo) {
                min.add(dto);
            }

            if (intervalo > maiorIntervalo) {
                maiorIntervalo = intervalo;
                max.clear();
                max.add(dto);
            } else if (intervalo == maiorIntervalo) {
                max.add(dto);
            }
        }

        return new ResultadoIntervaloDTO(menorIntervalo, maiorIntervalo, min, max);
    }

    private Map<String, List<Integer>> construirMapaAnosPorProdutor(List<Filme> filmesVencedores) {
        Map<String, List<Integer>> produtoresAno = new HashMap<>();

        for (Filme filme : filmesVencedores) {
            List<String> nomes = splitProducers(filme.getProducers());
            Integer ano = filme.getAno();

            for (String produtor : nomes) {
                produtoresAno
                        .computeIfAbsent(produtor, k -> new ArrayList<>())
                        .add(ano);
            }
        }

        return produtoresAno;
    }

    private List<ProducerIntervalResponseDTO> calcularIntervalos(Map<String, List<Integer>> produtoresAno) {
        List<ProducerIntervalResponseDTO> todosIntervalos = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entrada : produtoresAno.entrySet()) {
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

                todosIntervalos.add(new ProducerIntervalResponseDTO(
                        produtorNome,
                        intervalo,
                        anoAnterior,
                        anoSeguinte));
            }
        }

        return todosIntervalos;
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
