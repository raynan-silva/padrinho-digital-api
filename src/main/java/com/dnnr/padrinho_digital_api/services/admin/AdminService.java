package com.dnnr.padrinho_digital_api.services.admin;

import com.dnnr.padrinho_digital_api.dtos.ong.OngsPendingResponseDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;
import com.dnnr.padrinho_digital_api.entities.users.Role;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.BusinessException;
import com.dnnr.padrinho_digital_api.exceptions.OngNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.ong.OngRepository;
import com.dnnr.padrinho_digital_api.services.ong.OngService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final OngRepository ongRepository;

    @Transactional(readOnly = true)
    public Page<OngsPendingResponseDTO> getOngsPending(Pageable pageable) {
        Page<Ong> pendingOngs = ongRepository.findAllByStatus(OngStatus.PENDENTE, pageable);

        return pendingOngs.map(OngsPendingResponseDTO::new);
    }

    @Transactional
    public void approveOng(Long id, User user) {
        if (user.getRole() != Role.ADMIN) {
            throw  new AccessDeniedException("Usuário não tem permissão para esta ação");
        }

        Ong ong = ongRepository.findById(id).orElseThrow(() -> new OngNotFoundException("Ong com este id não encontrada"));

        if (ong.getStatus() != OngStatus.PENDENTE) {
            throw new BusinessException("Ong já aprovada ou inativa");
        }

        ong.setStatus(OngStatus.ATIVO);
        ongRepository.save(ong);
    }

    @Transactional
    public void rejectOng(Long id, User user) {
        if (user.getRole() != Role.ADMIN) {
            throw  new AccessDeniedException("Usuário não tem permissão para esta ação");
        }

        Ong ong = ongRepository.findById(id).orElseThrow(() -> new OngNotFoundException("Ong com este id não encontrada"));

        if (ong.getStatus() != OngStatus.ATIVO) {
            throw new BusinessException("Ong pendente ou inativa");
        }

        ong.setStatus(OngStatus.INATIVO);
        ongRepository.save(ong);
    }
}
