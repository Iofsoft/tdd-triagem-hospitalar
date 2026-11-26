package br.ifsp.tdd_triagem_hospitalar.utils;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import br.ifsp.tdd_triagem_hospitalar.security.user.Role;
import br.ifsp.tdd_triagem_hospitalar.security.user.User;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EntityBuilder {

    private static final Faker FAKER = new Faker();

    public static User createUser(String password){
        return User.builder()
                .id(UUID.randomUUID())
                .name(FAKER.name().firstName())
                .lastname(FAKER.name().lastName())
                .email(FAKER.internet().emailAddress())
                .password(password)
                .role(Role.USER)
                .build();
    }

    public static Atendimento createAtendimento() {
        return new Atendimento(
                UUID.randomUUID(),
                new Cpf("640.174.540-11"),
                StatusAtendimento.AGUARDANDO_TRIAGEM,
                null,
                List.of()
        );
    }

    public static MedicaoSinaisVitais createMedicao() {
        return new MedicaoSinaisVitais(
                UUID.randomUUID(),
                36.0 + (Math.random() * 2.0),
                60 + (int) (Math.random() * 61),
                LocalDateTime.now()
        );
    }
}
