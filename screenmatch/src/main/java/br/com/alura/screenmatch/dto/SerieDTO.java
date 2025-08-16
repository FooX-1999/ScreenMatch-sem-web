package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.models.Categoria;

import java.util.stream.Stream;

public record SerieDTO( Long id,
                        String titulo,
                        int totalTemporadas,
                        double avaliacao,
                        Categoria genero,
                        String atores,
                        String poster,
                        String sinopse) {
}
