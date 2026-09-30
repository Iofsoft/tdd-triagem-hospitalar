package br.ifsp.tdd_triagem_hospitalar.domain.model;

public record Cpf(String valor) {

    public Cpf {
        if (valor == null) {
            throw new IllegalArgumentException("O CPF não pode ser nulo.");
        }

        // Remove pontuações e espaços
        valor = valor.replaceAll("\\D", "");

        if (!isValid(valor)) {
            throw new IllegalArgumentException("CPF inválido: " + valor);
        }
    }

    private static boolean isValid(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int digito1 = calcularDigito(cpf, 10, 9);
            int digito2 = calcularDigito(cpf, 11, 10);

            return (digito1 == Character.getNumericValue(cpf.charAt(9))) &&
                    (digito2 == Character.getNumericValue(cpf.charAt(10)));
        } catch (Exception e) {
            return false;
        }
    }

    private static int calcularDigito(String cpf, int pesoInicial, int limite) {
        int soma = 0;
        int peso = pesoInicial;

        for (int i = 0; i < limite; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
        }

        int resto = 11 - (soma % 11);
        return (resto >= 10) ? 0 : resto;
    }

    @Override
    public String toString() {
        // insere pontuações e espaços
        return valor.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }


}
