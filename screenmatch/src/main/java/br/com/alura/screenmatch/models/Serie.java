package br.com.alura.screenmatch.models;


import java.util.OptionalDouble;

public class Serie {
    private String titulo;
    private int totalTemporadas;
    private double avaliacao;
    private Categoria genero;
    private String autores;
    private String poster;
    private String sinopse;

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.autores = dadosSerie.autores();
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.sinopse();
    }

}
