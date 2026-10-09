package br.ifsp.tdd_triagem_hospitalar.infrastructure.repository;

import br.ifsp.tdd_triagem_hospitalar.application.services.AtendimentoRepository;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcAtendimentoRepository implements AtendimentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAtendimentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(Atendimento atendimento) {
        final String classificacaoRisco = atendimento.getClassificacaoRisco() == null ? null : atendimento.getClassificacaoRisco().name();

        jdbcTemplate.update("INSERT OR REPLACE INTO atendimento (id, cpf, status, classificacao_risco, prescricao) VALUES (?, ?, ?, ?, ?)",
                atendimento.getId().toString(),
                atendimento.getCpf().valor(),
                atendimento.getStatus().name(),
                classificacaoRisco,
                atendimento.getPrescricao());

        for (MedicaoSinaisVitais medicao : atendimento.getMedicoes()) {
            jdbcTemplate.update("INSERT OR REPLACE INTO medicao (id, atendimento_id, temperatura, frequencia_cardiaca, data_hora) VALUES (?, ?, ?, ?, ?)",
                    medicao.getId().toString(),
                    atendimento.getId().toString(),
                    medicao.getTemperatura(),
                    medicao.getFrequenciaCardiaca(),
                    medicao.getDataHora().toString());
        }
    }

    @Override
    public Optional<Atendimento> buscarPorId(UUID id) {
        final List<MedicaoSinaisVitais> medicoes = jdbcTemplate.query("SELECT * FROM medicao WHERE atendimento_id = ? ORDER BY data_hora",
                (rs, rowNum) -> new MedicaoSinaisVitais(
                        UUID.fromString(rs.getString("id")),
                        rs.getDouble("temperatura"),
                        rs.getInt("frequencia_cardiaca"),
                        LocalDateTime.parse(rs.getString("data_hora"))),
                id.toString());

        final List<Atendimento> atendimentos = jdbcTemplate.query("SELECT * FROM atendimento WHERE id = ?",
                (rs, rowNum) -> new Atendimento(
                        UUID.fromString(rs.getString("id")),
                        new Cpf(rs.getString("cpf")),
                        StatusAtendimento.valueOf(rs.getString("status")),
                        rs.getString("classificacao_risco") == null ? null : ClassificacaoRisco.valueOf(rs.getString("classificacao_risco")),
                        medicoes,
                        rs.getString("prescricao")),
                id.toString());

        return atendimentos.stream().findFirst();
    }

    @Override
    public boolean existeAtendimentoAtivoPorCpf(Cpf cpf) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM atendimento where cpf = ? AND status NOT IN ('FINALIZADO', 'CANCELADO')",
                Integer.class,
                cpf.valor());
        return count != null && count > 0;
    }
}
