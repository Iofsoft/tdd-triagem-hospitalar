# Relatório de Testes de Mutação - Mutantes Equivalentes

Este documento lista e justifica os mutantes gerados pela ferramenta **Pitest** que são considerados **equivalentes** (ou inalcançáveis por código defensivo) e, portanto, não alteram o comportamento observável da aplicação nem podem ser mortos por testes unitários.

---

| Linha | ID / Descrição do Mutante | Justificativa de Equivalência com o Código Original |
| :---: | :--- | :--- |
| **30** | `BooleanReturnsMutator`: replaced boolean return with true for `br/ifsp/tdd_triagem_hospitalar/domain/model/Cpf::isValid` | **Mutante equivalente por código inalcançável:** O retorno `return false;` está localizado dentro do bloco `catch (Exception e)` do método privado `isValid(String)`. Como o código prévio garante que a string `cpf` foi sanitizada (`replaceAll("\\D", "")`) e possui tamanho estritamente igual a 11 dígitos numéricos (`cpf.length() != 11`), nenhuma instrução dentro do bloco `try` (`calcularDigito`, `cpf.charAt(...)` ou `Character.getNumericValue`) poderia disparar uma exceção em tempo de execução. Como o bloco `catch` nunca é atingido sob nenhuma entrada possível, substituir seu retorno por `true` produz um programa com comportamento externamente idêntico ao original (mutante equivalente inalcançável). |

---

### Resumo Geral de Mutação no Domínio e Aplicação
- **Total de Classes Avaliadas**: 14 (9 AppServices + 5 Agregados/Modelos de Domínio)
- **Mutantes Gerados**: 116
- **Mutantes Mortos (Killed)**: 115 (99% de Mutation Coverage e 100% de Test Strength)
- **Mutantes Equivalentes**: 1 (Linha 30 de `Cpf.java`)
