package com.dnnr.padrinho_digital_api.services.user;

import com.dnnr.padrinho_digital_api.dtos.users.RegisterDTO;
import com.dnnr.padrinho_digital_api.dtos.users.RegisterOngDTO;
import com.dnnr.padrinho_digital_api.dtos.users.RegisterVolunteerDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Address;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;
import com.dnnr.padrinho_digital_api.entities.users.*;
import com.dnnr.padrinho_digital_api.exceptions.*;
import com.dnnr.padrinho_digital_api.repositories.ong.AddressRepository;
import com.dnnr.padrinho_digital_api.repositories.ong.OngRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import com.dnnr.padrinho_digital_api.repositories.users.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository repository;
    @Autowired
    GodfatherRepository godfatherRepository;
    @Autowired
    ManagerRepository managerRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    OngRepository ongRepository;
    @Autowired
    VolunteerRepository volunteerRepository;

    @Transactional
    public void registerGodfather(RegisterDTO data){
        if(this.repository.findByEmail(data.login()) != null) throw new DuplicateUserException();

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.name(), UserStatus.ATIVO, data.login(), encryptedPassword, Role.PADRINHO);

        User user = this.repository.save(newUser);

        Godfather godfather = new Godfather(user);

        this.godfatherRepository.save(godfather);
    }

    @Transactional
    public void registerManager(RegisterOngDTO data){
        if(this.repository.findByEmail(data.login()) != null) throw new DuplicateUserException();
        if(this.ongRepository.findByCnpj(data.cnpj()) != null) throw new DuplicateOngException();

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.name(), UserStatus.ATIVO, data.login(), encryptedPassword, Role.GERENTE);

        User user = this.repository.save(newUser);

        Ong ong = new Ong(data.ong_name(), data.cnpj(), data.phone(), LocalDate.now(), data.description(), OngStatus.PENDENTE);

        this.ongRepository.save(ong);

        Manager manager = new Manager(user, ong);

        this.managerRepository.save(manager);

        Address address = new Address(data.street(), data.address_number(), data.neighborhood(), data.city(),
                data.uf(), data.complement(), data.cep(), ong);

        this.addressRepository.save(address);
    }

    @Transactional
    public void registerVolunteer(RegisterVolunteerDTO data, User authenticatedUser) {
        if(this.repository.findByEmail(data.login()) != null) throw new DuplicateUserException();

        Ong ong;

        if (authenticatedUser.getRole() == Role.GERENTE){
            Manager manager = managerRepository.findByUser(authenticatedUser)
                    .orElseThrow(() -> new UserNotFoundException("Gerente não encontrado na base de dados."));
            ong = manager.getOng();
        } else if (authenticatedUser.getRole() == Role.ADMIN){
            if (data.org_id() == null){
                throw new MissingParameterException("O 'org_id' é obrigatório ao cadastrar como ADMIN.");
            }
            ong = ongRepository.findById(data.org_id())
                    .orElseThrow(() -> new OngNotFoundException("ONG com ID " + data.org_id() + " não encontrada."));
        } else {
            throw new AccessDeniedException("Você não tem permissão para esta ação");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.name(), data.login(), encryptedPassword, data.cpf(),UserStatus.ATIVO, Role.VOLUNTARIO);

        User user = this.repository.save(newUser);

        Volunteer newVolunteer = new Volunteer(user, ong);
        this.volunteerRepository.save(newVolunteer);
    }
}
