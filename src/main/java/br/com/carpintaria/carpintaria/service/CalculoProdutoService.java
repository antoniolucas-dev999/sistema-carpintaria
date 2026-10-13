package br.com.carpintaria.carpintaria.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import br.com.carpintaria.carpintaria.dto.ResultadoCalculoProduto;
import br.com.carpintaria.carpintaria.model.CriterioVenda;


@Service
public class CalculoProdutoService {


    /*
     * ==================================================
     * REGRAS DE CÁLCULO
     * ==================================================
     *
     * ML
     * comprimento × quantidade
     *
     * M2
     * comprimento × largura × quantidade
     *
     * M3
     * comprimento × largura × espessura × quantidade
     *
     * UNIDADE / PECA / CONJUNTO
     * quantidade
     *
     * O valor total sempre será:
     *
     * medidaCalculada × precoUnitario
     *
     * Para UNIDADE / PECA / CONJUNTO,
     * medidaCalculada representa a própria quantidade.
     * ==================================================
     */


    public ResultadoCalculoProduto calcular(
            CriterioVenda criterioVenda,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal espessura,
            Integer quantidade,
            BigDecimal precoUnitario) {


        validarDados(
            criterioVenda,
            comprimento,
            largura,
            espessura,
            quantidade,
            precoUnitario
        );


        BigDecimal quantidadeDecimal =
                BigDecimal.valueOf(
                    quantidade
                );


        BigDecimal medidaCalculada;


        /*
         * ==================================================
         * METRO LINEAR
         * ==================================================
         *
         * comprimento × quantidade
         */

        if (criterioVenda
                == CriterioVenda.ML) {


            medidaCalculada =
                    comprimento
                        .multiply(
                            quantidadeDecimal
                        );
        }


        /*
         * ==================================================
         * METRO QUADRADO
         * ==================================================
         *
         * comprimento
         * × largura
         * × quantidade
         */

        else if (criterioVenda
                == CriterioVenda.M2) {


            medidaCalculada =
                    comprimento
                        .multiply(
                            largura
                        )
                        .multiply(
                            quantidadeDecimal
                        );
        }


        /*
         * ==================================================
         * METRO CÚBICO
         * ==================================================
         *
         * comprimento
         * × largura
         * × espessura
         * × quantidade
         */

        else if (criterioVenda
                == CriterioVenda.M3) {


            medidaCalculada =
                    comprimento
                        .multiply(
                            largura
                        )
                        .multiply(
                            espessura
                        )
                        .multiply(
                            quantidadeDecimal
                        );
        }


        /*
         * ==================================================
         * UNIDADE
         * ==================================================
         *
         * Não utiliza medidas.
         *
         * medidaCalculada = quantidade
         */

        else if (criterioVenda
                == CriterioVenda.UNIDADE) {


            medidaCalculada =
                    quantidadeDecimal;
        }


        /*
         * ==================================================
         * PEÇA
         * ==================================================
         *
         * Não utiliza medidas.
         *
         * medidaCalculada = quantidade
         */

        else if (criterioVenda
                == CriterioVenda.PECA) {


            medidaCalculada =
                    quantidadeDecimal;
        }


        /*
         * ==================================================
         * CONJUNTO
         * ==================================================
         *
         * Não utiliza medidas.
         *
         * medidaCalculada = quantidade
         */

        else if (criterioVenda
                == CriterioVenda.CONJUNTO) {


            medidaCalculada =
                    quantidadeDecimal;
        }


        else {


            throw new IllegalArgumentException(
                "Critério de cálculo inválido."
            );
        }


        /*
         * ==================================================
         * PRECISÃO DA MEDIDA
         * ==================================================
         *
         * Mantemos 6 casas decimais para
         * não perder precisão em ML, M² e M³.
         *
         * Para unidade, peça e conjunto,
         * teremos algo como:
         *
         * 3,000000
         */

        medidaCalculada =
                medidaCalculada.setScale(
                    6,
                    RoundingMode.HALF_UP
                );


        /*
         * ==================================================
         * VALOR TOTAL DO ITEM
         * ==================================================
         *
         * medida calculada × preço unitário
         *
         * Exemplos:
         *
         * M²:
         * 10,50 × R$ 200,00
         *
         * Unidade:
         * 3 × R$ 500,00
         */

        BigDecimal valorTotal =
                medidaCalculada
                    .multiply(
                        precoUnitario
                    )
                    .setScale(
                        2,
                        RoundingMode.HALF_UP
                    );


        return new ResultadoCalculoProduto(
            medidaCalculada,
            valorTotal
        );
    }


    /*
     * ==================================================
     * VALIDAÇÕES
     * ==================================================
     */

    private void validarDados(
            CriterioVenda criterioVenda,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal espessura,
            Integer quantidade,
            BigDecimal precoUnitario) {


        /*
         * ==================================================
         * CRITÉRIO
         * ==================================================
         */

        if (criterioVenda == null) {


            throw new IllegalArgumentException(
                "Selecione o critério de cálculo."
            );
        }


        /*
         * ==================================================
         * QUANTIDADE
         * ==================================================
         */

        if (quantidade == null
                || quantidade <= 0) {


            throw new IllegalArgumentException(
                "A quantidade deve ser maior que zero."
            );
        }


        /*
         * ==================================================
         * PREÇO
         * ==================================================
         */

        if (precoUnitario == null
                || precoUnitario.compareTo(
                    BigDecimal.ZERO
                ) < 0) {


            throw new IllegalArgumentException(
                "Informe um preço válido."
            );
        }


        /*
         * ==================================================
         * ML
         * ==================================================
         *
         * Exige apenas comprimento.
         */

        if (criterioVenda
                == CriterioVenda.ML) {


            validarMedida(
                comprimento,
                "Comprimento"
            );
        }


        /*
         * ==================================================
         * M²
         * ==================================================
         *
         * Exige:
         *
         * comprimento
         * largura
         */

        if (criterioVenda
                == CriterioVenda.M2) {


            validarMedida(
                comprimento,
                "Comprimento"
            );


            validarMedida(
                largura,
                "Largura"
            );
        }


        /*
         * ==================================================
         * M³
         * ==================================================
         *
         * Exige:
         *
         * comprimento
         * largura
         * espessura
         */

        if (criterioVenda
                == CriterioVenda.M3) {


            validarMedida(
                comprimento,
                "Comprimento"
            );


            validarMedida(
                largura,
                "Largura"
            );


            validarMedida(
                espessura,
                "Espessura"
            );
        }


        /*
         * ==================================================
         * UNIDADE / PEÇA / CONJUNTO
         * ==================================================
         *
         * Não exigem nenhuma medida.
         *
         * A quantidade e o preço já foram
         * validados acima.
         */
    }


    /*
     * ==================================================
     * VALIDAR MEDIDA
     * ==================================================
     */

    private void validarMedida(
            BigDecimal valor,
            String nomeCampo) {


        if (valor == null
                || valor.compareTo(
                    BigDecimal.ZERO
                ) <= 0) {


            throw new IllegalArgumentException(
                nomeCampo
                + " deve ser maior que zero."
            );
        }
    }
}