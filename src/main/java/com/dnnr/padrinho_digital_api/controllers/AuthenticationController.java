package com.dnnr.padrinho_digital_api.controllers;

import com.dnnr.padrinho_digital_api.dtos.users.AuthenticationDTO;
import com.dnnr.padrinho_digital_api.dtos.users.LoginResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.users.RegisterDTO;
import com.dnnr.padrinho_digital_api.dtos.users.RegisterOngDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.infra.security.TokenService;
import com.dnnr.padrinho_digital_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return  ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register/godfather")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        userService.registerGodfather(data);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/ong")
    public ResponseEntity registerOng(@RequestBody @Valid RegisterOngDTO data) {
        userService.registerManager(data);

        return ResponseEntity.ok().build();
    }
}
