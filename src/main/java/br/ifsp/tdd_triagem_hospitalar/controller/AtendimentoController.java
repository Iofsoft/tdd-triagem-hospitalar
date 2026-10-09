package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.application.services.IniciarConsultaAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/atendimentos")
public class AtendimentoController {

    private final IniciarConsultaAppService iniciarConsultaAppService;

    public AtendimentoController(IniciarConsultaAppService iniciarConsultaAppService) {
        this.iniciarConsultaAppService = iniciarConsultaAppService;
    }

    @PatchMapping("/{id}/consulta")
    public ResponseEntity<Void> iniciarConsulta(@PathVariable UUID id) {
        iniciarConsultaAppService.iniciarConsulta(id);
        return ResponseEntity.noContent().build();
    }
}
