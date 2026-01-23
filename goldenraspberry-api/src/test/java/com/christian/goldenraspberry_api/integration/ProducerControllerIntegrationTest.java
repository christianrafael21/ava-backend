package com.christian.goldenraspberry_api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.christian.goldenraspberry_api.dto.AwardIntervalResponseDTO;
import com.christian.goldenraspberry_api.model.Filme;
import com.christian.goldenraspberry_api.repository.FilmeRepository;
import com.christian.goldenraspberry_api.service.ProducerService;

@SpringBootTest
class ProducerControllerIntegrationTest {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private FilmeRepository filmeRepository;

    @Test
    void deveRetornarDadosDeAcordoComOArquivoCSV() {
        List<Filme> filmesVencedores = filmeRepository.findByWinnerTrue();
        Map<String, List<Integer>> produtoresAnos = calcularProdutoresAnos(filmesVencedores);
        List<IntervaloProdutorTest> intervalosEsperados = calcularTodosIntervalos(produtoresAnos);

        assertThat(intervalosEsperados).isNotEmpty();

        int menorIntervaloEsperado = intervalosEsperados.stream()
            .mapToInt(IntervaloProdutorTest::intervalo)
            .min()
            .orElseThrow();

        int maiorIntervaloEsperado = intervalosEsperados.stream()
            .mapToInt(IntervaloProdutorTest::intervalo)
            .max()
            .orElseThrow();

        List<IntervaloProdutorTest> minsEsperados = intervalosEsperados.stream()
            .filter(i -> i.intervalo == menorIntervaloEsperado)
            .toList();

        List<IntervaloProdutorTest> maxsEsperados = intervalosEsperados.stream()
            .filter(i -> i.intervalo == maiorIntervaloEsperado)
            .toList();

        AwardIntervalResponseDTO resultadoAPI = producerService.getAwardIntervals();

        assertThat(resultadoAPI.min()).hasSize(minsEsperados.size());
        assertThat(resultadoAPI.max()).hasSize(maxsEsperados.size());

        for (IntervaloProdutorTest esperado : minsEsperados) {
            boolean encontrado = resultadoAPI.min().stream()
                .anyMatch(dto -> 
                    dto.producer().equals(esperado.produtor) &&
                    dto.interval() == esperado.intervalo &&
                    dto.previousWin() == esperado.anoAnterior &&
                    dto.followingWin() == esperado.anoSeguinte
                );
            
            assertThat(encontrado).isTrue();
        }

        for (IntervaloProdutorTest esperado : maxsEsperados) {
            boolean encontrado = resultadoAPI.max().stream()
                .anyMatch(dto -> 
                    dto.producer().equals(esperado.produtor) &&
                    dto.interval() == esperado.intervalo &&
                    dto.previousWin() == esperado.anoAnterior &&
                    dto.followingWin() == esperado.anoSeguinte
                );
            
            assertThat(encontrado).isTrue();
        }
    }

    private Map<String, List<Integer>> calcularProdutoresAnos(List<Filme> filmes) {
        Map<String, List<Integer>> mapa = new HashMap<>();
        
        for (Filme filme : filmes) {
            String[] produtores = filme.getProducers().split(",\\s*and\\s+|,\\s+|\\s+and\\s+");
            for (String produtor : produtores) {
                String nome = produtor.trim();
                if (!nome.isEmpty()) {
                    mapa.computeIfAbsent(nome, k -> new ArrayList<>()).add(filme.getAno());
                }
            }
        }
        
        return mapa;
    }

    private List<IntervaloProdutorTest> calcularTodosIntervalos(Map<String, List<Integer>> produtoresAnos) {
        List<IntervaloProdutorTest> intervalos = new ArrayList<>();
        
        for (Map.Entry<String, List<Integer>> entry : produtoresAnos.entrySet()) {
            String produtor = entry.getKey();
            List<Integer> anos = entry.getValue();
            
            if (anos.size() < 2) {
                continue;
            }
            
            Collections.sort(anos);
            
            for (int i = 0; i < anos.size() - 1; i++) {
                int anoAnterior = anos.get(i);
                int anoSeguinte = anos.get(i + 1);
                int intervalo = anoSeguinte - anoAnterior;
                
                intervalos.add(new IntervaloProdutorTest(produtor, intervalo, anoAnterior, anoSeguinte));
            }
        }
        
        return intervalos;
    }

    private record IntervaloProdutorTest(String produtor, int intervalo, int anoAnterior, int anoSeguinte) {}
}