package com.sip.controllers;

import com.sip.entities.Projet;
import com.sip.interfaces.ProjetService;
import com.sip.requests.ProjetRequest;
import com.sip.responses.ProjetResponse;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projets")
@CrossOrigin(origins = "*")
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
    public List<Projet> getProjets() {
        return projetService.getProjets();
    }
    
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getAllProjets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> response = projetService.getAllProjets(pageable);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjet(@PathVariable Long id) {
        projetService.deleteProjet(id);
        return ResponseEntity.noContent().build();
    }
}
