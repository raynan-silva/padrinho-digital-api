package com.dnnr.padrinho_digital_api.services;

import com.dnnr.padrinho_digital_api.dtos.users.RegisterDTO;
import com.dnnr.padrinho_digital_api.dtos.users.RegisterOngDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Address;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;
import com.dnnr.padrinho_digital_api.entities.users.*;
import com.dnnr.padrinho_digital_api.exceptions.DuplicateOngException;
import com.dnnr.padrinho_digital_api.exceptions.DuplicateUserException;
import com.dnnr.padrinho_digital_api.repositories.ong.AddressRepository;
import com.dnnr.padrinho_digital_api.repositories.ong.OngRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
}
