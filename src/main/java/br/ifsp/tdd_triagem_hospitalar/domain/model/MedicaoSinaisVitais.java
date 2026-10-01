package br.ifsp.tdd_triagem_hospitalar.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MedicaoSinaisVitais {

    private static final Double TEMP_MINIMA = 32.00;
    private static final Double TEMP_MAXIMA = 43.0;
    private static final int FC_MINIMA = 20;
    private static final int FC_MAXIMA = 260;

    private final UUID id;
    private final double temperatura;
    private final int frequenciaCardiaca;
    private final LocalDateTime dataHora;

    public MedicaoSinaisVitais(UUID id, double temperatura, int frequenciaCardiaca, LocalDateTime dataHora) {
        if (id == null) {
            throw new IllegalArgumentException("O Id da medição não pode ser nulo.");
        }

        if (temperatura < TEMP_MINIMA || temperatura > TEMP_MAXIMA) {
            throw new IllegalArgumentException("Temperatura fora da faixa permitida (32.0°C a 43.0°C): " + temperatura);
        }
        if (frequenciaCardiaca < FC_MINIMA || frequenciaCardiaca > FC_MAXIMA) {
            throw new IllegalArgumentException("Frequência cardíaca fora da faixa permitida (20 a 260 bpm): " + frequenciaCardiaca);
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("A data e hora da medição não pode ser nula.");
        }
        if (dataHora.isAfter(LocalDateTime.now().plusMinutes(1))) { // tolerância pequena para desvio de relógio
            throw new IllegalArgumentException("A data e hora da medição não pode ser no futuro.");
        }

        this.id = id;
        this.temperatura = temperatura;
        this.frequenciaCardiaca = frequenciaCardiaca;
        this.dataHora = dataHora;
    }

    public UUID getId() {
        return id;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public int getFrequenciaCardiaca() {
        return frequenciaCardiaca;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicaoSinaisVitais that = (MedicaoSinaisVitais) o;
        return Objects.equals(id, that.id);
    }

}
