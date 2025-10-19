package com.dnnr.padrinho_digital_api.controllers;

import com.dnnr.padrinho_digital_api.dtos.users.AuthenticationDTO;
import com.dnnr.padrinho_digital_api.dtos.users.LoginResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.users.RegisterDTO;
import com.dnnr.padrinho_digital_api.entities.users.Role;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.AdminRegistrationException;
import com.dnnr.padrinho_digital_api.infra.security.TokenService;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return  ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(data.role().equals(Role.ADMIN)) throw  new AdminRegistrationException();

        if(this.repository.findByEmail(data.login()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.status(), data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
