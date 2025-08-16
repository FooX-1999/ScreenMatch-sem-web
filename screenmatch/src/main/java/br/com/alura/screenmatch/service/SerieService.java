package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.models.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository respositorio;

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDados(respositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(respositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    private List<SerieDTO> converteDados(List<Serie> series){
        return series.stream()
                .map(s-> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAutores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }
}
