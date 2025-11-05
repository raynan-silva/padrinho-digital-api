package com.dnnr.padrinho_digital_api.controllers.auth;

import com.dnnr.padrinho_digital_api.dtos.users.*;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.infra.security.TokenService;
import com.dnnr.padrinho_digital_api.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("aquiiui");
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        System.out.println("Auth: " + auth);

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

    @PostMapping("/register/volunteer")
    public ResponseEntity registerVolunteer(@RequestBody @Valid RegisterVolunteerDTO data,
                                            @AuthenticationPrincipal User authenticatedUser) {
        userService.registerVolunteer(data, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/profile")
    public ResponseEntity profile(@AuthenticationPrincipal User authenticatedUser) {
        ProfileResponseDTO response = new ProfileResponseDTO(authenticatedUser.getId(), authenticatedUser.getName(), authenticatedUser.getEmail(), authenticatedUser.getRole());
        return ResponseEntity.ok(response);
    }
}
