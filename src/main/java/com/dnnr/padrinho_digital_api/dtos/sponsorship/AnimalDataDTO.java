package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import java.time.LocalDate;

public record AnimalDataDTO(
        String name,           // antigo: nome
        String species,        // antigo: especie (ex: "Dog - Labrador")
        String age,            // antigo: idade (ex: "3 years")
        LocalDate godfatherSince, // antigo: padrinhoDesde
        String imageUrl        // URL ou Base64
) {}