package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.models.Categoria;

public record SerieDTO( Long id,
                        String titulo,
                        int totalTemporadas,
                        double avaliacao,
                        Categoria genero,
                        String autores,
                        String poster,
                        String sinopse) {
}
