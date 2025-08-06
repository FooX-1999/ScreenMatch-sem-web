package br.com.alura.screenmatch.models;


import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private int temporada;
    private String titulo;
    private int numeroEpisodio;
    private double avaliacao;
    private LocalDate  dataLancamento;

    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
        this.temporada = numeroTemporada;
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero();
        try {
            this.avaliacao = Double.valueOf(dadosEpisodio.avalia());
        } catch (NumberFormatException ex){
            this.avaliacao = 0.0;
        }

        try {
            this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        } catch (DateTimeParseException ex){
            this.dataLancamento = null;
        }

    }

    @Override
    public String toString() {
        return  "temporada=" + temporada +
                ", titulo= " + titulo +
                ", numeroEpisodio= " + numeroEpisodio +
                ", avaliacao= " + avaliacao +
                ", dataLancamento= " + dataLancamento ;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getTemporada() {
        return temporada;
    }

    public double getAvaliacao() {
        return avaliacao;
    }
}
