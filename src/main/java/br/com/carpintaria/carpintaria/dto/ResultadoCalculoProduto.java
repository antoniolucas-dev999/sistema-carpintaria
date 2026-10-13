package br.com.carpintaria.carpintaria.dto;

import java.math.BigDecimal;

public record ResultadoCalculoProduto(

        BigDecimal medidaCalculada,
        BigDecimal valorTotal

) {
}