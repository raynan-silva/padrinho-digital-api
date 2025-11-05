package com.dnnr.padrinho_digital_api.services.cost;

import com.dnnr.padrinho_digital_api.dtos.cost.CostResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.cost.CreateCostDTO;
import com.dnnr.padrinho_digital_api.dtos.cost.UpdateCostDTO;
import com.dnnr.padrinho_digital_api.entities.pet.Cost;
import com.dnnr.padrinho_digital_api.entities.pet.CostHistory;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.exceptions.BusinessException; // Supondo que você tenha
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException; // Supondo que você tenha
import com.dnnr.padrinho_digital_api.repositories.pet.CostHistoryRepository;
import com.dnnr.padrinho_digital_api.repositories.pet.CostRepository;
import com.dnnr.padrinho_digital_api.repositories.pet.PetRepository; // Você precisará injetar este
import com.dnnr.padrinho_digital_api.services.mappers.CostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostService {

    private final CostRepository costRepository;
    private final CostHistoryRepository costHistoryRepository;
    private final PetRepository petRepository; // Assumindo que você tenha um PetRepository
    private final CostMapper costMapper;

    @Transactional
    public CostResponseDTO create(CreateCostDTO dto) {
        // 1. Encontra o Pet
        Pet pet = petRepository.findById(dto.petId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com o ID: " + dto.petId()));

        // 2. Cria a entidade "Cost" (pai)
        Cost cost = new Cost();
        cost.setPet(pet);

        // 3. Cria o primeiro "CostHistory" (filho)
        CostHistory history = new CostHistory();
        history.setCost(cost); // Linka o filho ao pai
        history.setName(dto.name());
        history.setDescription(dto.description());
        history.setMonthlyAmount(dto.monthlyAmount());
        history.setStartDate(dto.startDate());
        history.setEndDate(null); // Nulo significa que é o período atual

        // 4. Adiciona o histórico à lista do pai
        cost.setHistory(List.of(history));

        // 5. Salva o pai (CascadeType.ALL salvará o filho)
        Cost savedCost = costRepository.save(cost);

        return costMapper.toCostDTO(savedCost);
    }

    @Transactional(readOnly = true)
    public CostResponseDTO findById(Long id) {
        Cost cost = costRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada com o ID: " + id));
        return costMapper.toCostDTO(cost);
    }

    @Transactional(readOnly = true)
    public List<CostResponseDTO> findAllByPetId(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet não encontrado com o ID: " + petId);
        }

        List<Cost> costs = costRepository.findAllByPetId(petId);
        return costs.stream()
                .map(costMapper::toCostDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CostResponseDTO update(Long costId, UpdateCostDTO dto) {
        // 1. Encontra a Despesa (Cost)
        Cost cost = costRepository.findById(costId)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada com o ID: " + costId));

        // 2. Encontra o histórico ativo (o que não tem data de fim)
        CostHistory currentHistory = cost.getHistory().stream()
                .filter(h -> h.getEndDate() == null)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Não foi encontrado um período de despesa ativo para atualizar."));

        // 3. Validação de data
        LocalDate newStartDate = dto.startDate();
        if (newStartDate.isBefore(currentHistory.getStartDate()) || newStartDate.isEqual(currentHistory.getStartDate())) {
            throw new BusinessException("A data de início do novo período (" + newStartDate + ") deve ser posterior à data de início do período atual (" + currentHistory.getStartDate() + ").");
        }

        // 4. Encerra o período atual
        currentHistory.setEndDate(newStartDate.minusDays(1));

        // 5. Cria o novo período (novo CostHistory)
        CostHistory newHistory = new CostHistory();
        newHistory.setCost(cost); // Linka ao mesmo pai
        newHistory.setName(dto.name());
        newHistory.setDescription(dto.description());
        newHistory.setMonthlyAmount(dto.monthlyAmount());
        newHistory.setStartDate(newStartDate);
        newHistory.setEndDate(null); // Novo período ativo

        // 6. Salva as alterações
        costHistoryRepository.save(currentHistory); // Salva o período antigo (agora com end_date)
        costHistoryRepository.save(newHistory); // Salva o novo período

        // 7. Recarrega a entidade Cost para retornar os dados atualizados
        Cost updatedCost = costRepository.findById(costId).get();
        return costMapper.toCostDTO(updatedCost);
    }

    @Transactional
    public void delete(Long id) {
        if (!costRepository.existsById(id)) {
            throw new ResourceNotFoundException("Despesa não encontrada com o ID: " + id);
        }
        // Graças ao CascadeType.ALL, ao deletar o "Cost", todos os "CostHistory" relacionados serão deletados.
        costRepository.deleteById(id);
    }
}