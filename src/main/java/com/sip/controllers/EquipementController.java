package com.sip.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sip.requests.EquipementRequest;
import com.sip.responses.EquipementResponse;

import com.sip.entities.Equipement;
import com.sip.entities.ImportResult;
import com.sip.interfaces.EquipementService;

@CrossOrigin("*")
@RestController
@RequestMapping("/equipements")
public class EquipementController {

    private final EquipementService equipementService;

    public EquipementController(EquipementService equipementService) {
        this.equipementService = equipementService;
    }
 
    @GetMapping("/")
    public List<Equipement> getEquipements() {
        return equipementService.getEquipements();
    }

    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getAllEquipements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> result = equipementService.getAllEquipements(pageable);
        return ResponseEntity.ok(result);
    }

    
    @PostMapping("/")
    public ResponseEntity<EquipementResponse> createEquipement(@RequestBody EquipementRequest equipementRequest) {
        EquipementResponse savedEquipement = equipementService.addEquipement(equipementRequest);
        return ResponseEntity.ok(savedEquipement);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<EquipementResponse> updateEquipement(@PathVariable Long id,@RequestBody EquipementRequest equipementRequest) {
        EquipementResponse updatedEquipement = equipementService.updateEquipement(id, equipementRequest);
        return ResponseEntity.ok(updatedEquipement);
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipement(@PathVariable Long id) {
        equipementService.deleteEquipement(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/nombreEquipements")
    public int nbreEquipements() {
    	return this.equipementService.NombreEquipements();
    }
    
    @PostMapping("/import")
    public ResponseEntity<ImportResult> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResult("Fichier vide", 0, false));
        }

        try {
            Map<String, Object> result = equipementService.importFromExcel(file);
            ImportResult importResult = new ImportResult(
                "Importation réussie", 
                (int) result.get("count"), 
                true
            );
            return ResponseEntity.ok(importResult);
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(new ImportResult("Erreur lors de l'importation : " + e.getMessage(), 0, false));
        }
    }


    
}
