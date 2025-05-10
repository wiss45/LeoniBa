package com.sip.controllers;

import com.sip.entities.Projet;
import com.sip.interfaces.ProjetService;
import com.sip.requests.ProjetRequest;
import com.sip.responses.ProjetResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projets")
@CrossOrigin("*")
public class ProjetController {

    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @PostMapping("/")
    public ResponseEntity<ProjetResponse> createProjet(@RequestBody ProjetRequest request) {
        ProjetResponse response = projetService.createProjet(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetResponse> updateProjet(@PathVariable Long id, @RequestBody ProjetRequest request) {
        ProjetResponse response = projetService.updateProjet(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<Projet>> getAllProjets() {
        List<Projet> projets = projetService.getProjets();
        return ResponseEntity.ok(projets);
    }

    @GetMapping("/draftprojets")
    public ResponseEntity<List<Projet>> getApprovedProjets() {
        List<Projet> projets = projetService.projetDRAFT();
        return ResponseEntity.ok(projets);
    }

    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getPagedProjets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> response = projetService.getAllProjets(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-responsable")
    public Page<ProjetResponse> getProjetsByResponsable(
        @RequestParam String responsable,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "6") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return projetService.findByResponsable(responsable, pageable);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjet(@PathVariable Long id) {
        projetService.deleteProjet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nombreProjets")
    public int getNombreProjetsActifs() {
        int count = projetService.NombreProjets();
        return count ;
    }
    
}
