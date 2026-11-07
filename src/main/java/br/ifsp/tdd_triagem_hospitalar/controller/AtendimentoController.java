package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.application.services.*;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/atendimentos")
public class AtendimentoController {

    private final AbrirAtendimentoAppService abrirAtendimentoAppService;
    private final RealizarTriagemAppService realizarTriagemAppService;
    private final IniciarConsultaAppService iniciarConsultaAppService;
    private final RegistrarReavaliacaoAppService registrarReavaliacaoAppService;
    private final ReclassificarRiscoAppService reclassificarRiscoAppService;
    private final RegistrarPrescricaoAppService registrarPrescricaoAppService;
    private final FinalizarAtendimentoAppService finalizarAtendimentoAppService;
    private final CancelarAtendimentoAppService cancelarAtendimentoAppService;
    private final ListarAtendimentosAppService listarAtendimentosAppService;
    private final BuscarAtendimentoAppService buscarAtendimentoAppService;

    public AtendimentoController(AbrirAtendimentoAppService abrirAtendimentoAppService,
                                 RealizarTriagemAppService realizarTriagemAppService,
                                 IniciarConsultaAppService iniciarConsultaAppService,
                                 RegistrarReavaliacaoAppService registrarReavaliacaoAppService,
                                 ReclassificarRiscoAppService reclassificarRiscoAppService,
                                 RegistrarPrescricaoAppService registrarPrescricaoAppService,
                                 FinalizarAtendimentoAppService finalizarAtendimentoAppService,
                                 CancelarAtendimentoAppService cancelarAtendimentoAppService,
                                 ListarAtendimentosAppService listarAtendimentosAppService,
                                 BuscarAtendimentoAppService buscarAtendimentoAppService) {
        this.abrirAtendimentoAppService = abrirAtendimentoAppService;
        this.realizarTriagemAppService = realizarTriagemAppService;
        this.iniciarConsultaAppService = iniciarConsultaAppService;
        this.registrarReavaliacaoAppService = registrarReavaliacaoAppService;
        this.reclassificarRiscoAppService = reclassificarRiscoAppService;
        this.registrarPrescricaoAppService = registrarPrescricaoAppService;
        this.finalizarAtendimentoAppService = finalizarAtendimentoAppService;
        this.cancelarAtendimentoAppService = cancelarAtendimentoAppService;
        this.listarAtendimentosAppService = listarAtendimentosAppService;
        this.buscarAtendimentoAppService = buscarAtendimentoAppService;
    }

    @GetMapping
    public ResponseEntity<List<AtendimentoResponse>> listarAtendimentos() {
        final List<AtendimentoResponse> atendimentos = listarAtendimentosAppService.listarAtendimentos().stream()
                .map(AtendimentoResponse::new)
                .toList();
        return ResponseEntity.ok(atendimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoResponse> buscarAtendimento(@PathVariable UUID id) {
        return ResponseEntity.ok(new AtendimentoResponse(buscarAtendimentoAppService.buscarAtendimento(id)));
    }

    @PostMapping
    public ResponseEntity<UUID> abrirAtendimento(@RequestBody AbrirAtendimentoRequest request) {
        UUID id = abrirAtendimentoAppService.abrirAtendimento(request.cpf());
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping("/{id}/triagem")
    public ResponseEntity<Void> realizarTriagem(@PathVariable UUID id, @RequestBody RealizarTriagemRequest request) {
        final MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(
                UUID.randomUUID(),
                request.temperatura(),
                request.frequenciaCardiaca(),
                LocalDateTime.now()
        );
        realizarTriagemAppService.realizarTriagem(id, medicao, request.classificacaoRisco());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    @PatchMapping("/{id}/prescricao")
    public ResponseEntity<Void> registrarPrescricao(@PathVariable UUID id, @RequestBody
    RegistrarPrescricaoRequest request) {
        registrarPrescricaoAppService.registrarPrescricao(id, request.prescricao());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/finalizacao")
    public ResponseEntity<Void> finalizarAtendimento(@PathVariable UUID id) {
        finalizarAtendimentoAppService.finalizarAtendimento(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancelamento")
    public ResponseEntity<Void> cancelarAtendimento(@PathVariable UUID id, @RequestBody
    CancelarAtendimentoRequest request) {
        cancelarAtendimentoAppService.cancelarAtendimento(id, request.motivo());
        return ResponseEntity.noContent().build();
    }
}
