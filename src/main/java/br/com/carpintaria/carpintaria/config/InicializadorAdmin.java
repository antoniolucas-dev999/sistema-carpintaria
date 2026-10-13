package br.com.carpintaria.carpintaria.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.com.carpintaria.carpintaria.service.UsuarioService;


@Component
public class InicializadorAdmin
        implements ApplicationRunner {


    private final UsuarioService usuarioService;



    public InicializadorAdmin(
            UsuarioService usuarioService) {

        this.usuarioService =
                usuarioService;
    }



    /*
     * ==================================================
     * EXECUTA AO INICIAR O SISTEMA
     * ==================================================
     */

    @Override
    public void run(
            ApplicationArguments args) {


        usuarioService
            .criarAdministradorInicial();
    }
}