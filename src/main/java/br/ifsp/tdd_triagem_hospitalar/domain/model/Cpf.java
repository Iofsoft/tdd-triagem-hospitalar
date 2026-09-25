package br.ifsp.tdd_triagem_hospitalar.domain.model;

public record Cpf(String valor) {

    public Cpf{
        if (valor == null){
            throw new IllegalArgumentException("O CPF não pode ser nulo.");
        }

        if (!isValid(valor)){
            throw new IllegalArgumentException("CPF inválido" + valor);
        }

    }

    private static boolean isValid(String cpf){
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")){
            return false;
        }
        return true;
    }


}
