package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.application.services.IniciarConsultaAppService;
import br.ifsp.tdd_triagem_hospitalar.application.services.ReclassificarRiscoAppService;
import br.ifsp.tdd_triagem_hospitalar.application.services.RegistrarReavaliacaoAppService;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/atendimentos")
public class AtendimentoController {

    private final IniciarConsultaAppService iniciarConsultaAppService;
    private final RegistrarReavaliacaoAppService registrarReavaliacaoAppService;
    private final ReclassificarRiscoAppService reclassificarRiscoAppService;

    public AtendimentoController(IniciarConsultaAppService iniciarConsultaAppService,
                                 RegistrarReavaliacaoAppService registrarReavaliacaoAppService,
                                 ReclassificarRiscoAppService reclassificarRiscoAppService) {
        this.iniciarConsultaAppService = iniciarConsultaAppService;
        this.registrarReavaliacaoAppService = registrarReavaliacaoAppService;
        this.reclassificarRiscoAppService = reclassificarRiscoAppService;
    }

    @PatchMapping("/{id}/consulta")
    public ResponseEntity<Void> iniciarConsulta(@PathVariable UUID id) {
        iniciarConsultaAppService.iniciarConsulta(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/medicoes")
    public ResponseEntity<Void> registrarMedicao(@PathVariable UUID id, @RequestBody RegistrarMedicaoRequest request) {
        final MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(UUID.randomUUID(), request.temperatura(), request.frequenciaCardiaca(), LocalDateTime.now());
        registrarReavaliacaoAppService.registrarReavaliacao(id, medicao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/classificacao-risco")
    public ResponseEntity<Void> reclassificarRisco(@PathVariable UUID id, @RequestBody ReclassificarRiscoRequest request) {
        reclassificarRiscoAppService.reclassificarRisco(id, request.novaClassificacao(), request.justificativa());
        return ResponseEntity.noContent().build();
    }
}
