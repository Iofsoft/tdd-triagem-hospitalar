# Relatório de Cobertura Estrutural - Caminhos Inalcançáveis

Este documento registra as classes e trechos de código do projeto em que a cobertura de 100% no critério estrutural (Condição/Decisão - Branches e Linhas) não é viável em virtude da existência de caminhos não alcançáveis (código defensivo ou salvaguardas).

## Tabela de Caminhos Inalcançáveis

| Classe | Linha | Justificativa |
| :--- | :---: | :--- |
| `br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf` | 29-31 | **Código defensivo inalcançável:** O bloco `catch (Exception e) { return false; }` dentro do método privado `isValid(String)` foi inserido como salvaguarda. No entanto, o construtor já valida previamente o valor com `replaceAll("\\D", "")` e a guarda inicial `if (cpf.length() != 11 \|\| cpf.matches("(\\d)\\1{10}")) return false;` assegura que apenas strings com exatamente 11 dígitos numéricos entrem no bloco `try`. Dessa forma, nem `cpf.charAt(i)` (pois os índices 0 a 10 são estritamente válidos) nem `Character.getNumericValue` disparam exceções em tempo de execução. Como nenhuma exceção pode ocorrer, a entrada no bloco `catch` e o retorno `false` tornam-se inalcançáveis por testes unitários. |
