package com.dnnr.padrinho_digital_api.controllers.cost;

import com.dnnr.padrinho_digital_api.dtos.cost.CostResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.cost.CreateCostDTO;
import com.dnnr.padrinho_digital_api.dtos.cost.UpdateCostDTO;
import com.dnnr.padrinho_digital_api.services.cost.CostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("costs")
@RequiredArgsConstructor
public class CostController {

    private final CostService costService;

    @PostMapping
    public ResponseEntity<CostResponseDTO> create(@Valid @RequestBody CreateCostDTO dto) {
        CostResponseDTO newCost = costService.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newCost.id())
                .toUri();
        return ResponseEntity.created(uri).body(newCost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CostResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(costService.findById(id));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<CostResponseDTO>> findAllByPetId(@PathVariable Long petId) {
        return ResponseEntity.ok(costService.findAllByPetId(petId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CostResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateCostDTO dto) {
        return ResponseEntity.ok(costService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        costService.delete(id);
        return ResponseEntity.noContent().build();
    }
}