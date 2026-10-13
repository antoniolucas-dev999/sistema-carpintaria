package br.com.carpintaria.carpintaria.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.carpintaria.carpintaria.dto.AtualizacaoPerfilDTO;
import br.com.carpintaria.carpintaria.dto.CadastroUsuarioDTO;
import br.com.carpintaria.carpintaria.dto.EdicaoUsuarioDTO;
import br.com.carpintaria.carpintaria.model.PerfilUsuario;
import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.repository.UsuarioRepository;


@Service
public class UsuarioService {


    private static final String SENHA_TEMPORARIA =
            "CarpintariaReis";


    private static final String LOGIN_ADMINISTRADOR =
            "adm.carpintaria";


    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;


    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository =
                usuarioRepository;

        this.passwordEncoder =
                passwordEncoder;
    }


    /*
     * ==================================================
     * VERIFICAR LOGIN
     * ==================================================
     */

    public boolean loginJaCadastrado(
            String login) {

        if (login == null
                || login.isBlank()) {

            return false;
        }

        return usuarioRepository
                .existsByLoginIgnoreCase(
                    login.trim()
                );
    }


    /*
     * ==================================================
     * VERIFICAR E-MAIL
     * ==================================================
     */

    public boolean emailJaCadastrado(
            String email) {

        if (email == null
                || email.isBlank()) {

            return false;
        }

        return usuarioRepository
                .existsByEmailIgnoreCase(
                    email.trim()
                );
    }


    /*
     * ==================================================
     * CADASTRAR USUÁRIO
     * ==================================================
     *
     * Cadastro realizado pelo ADMIN.
     *
     * A senha não é escolhida pelo administrador.
     *
     * Todo novo usuário recebe:
     *
     * Senha temporária: CarpintariaReis
     * Ativo: SIM
     * Troca obrigatória: SIM
     *
     * Usuários comuns recebem inicialmente
     * o acesso correspondente ao seu
     * perfil principal.
     *
     * Depois o administrador poderá conceder
     * acessos adicionais pela edição.
     * ==================================================
     */

    @Transactional
    public Usuario cadastrar(
            CadastroUsuarioDTO dto) {


        /*
         * ==================================================
         * NOME
         * ==================================================
         */

        if (dto.getNome() == null
                || dto.getNome().isBlank()) {

            throw new IllegalArgumentException(
                "O nome é obrigatório."
            );
        }


        String nome =
                dto.getNome()
                    .trim();


        /*
         * ==================================================
         * LOGIN
         * ==================================================
         */

        if (dto.getLogin() == null
                || dto.getLogin().isBlank()) {

            throw new IllegalArgumentException(
                "O login é obrigatório."
            );
        }


        String login =
                dto.getLogin()
                    .trim()
                    .toLowerCase();


        if (loginJaCadastrado(
                login)) {

            throw new IllegalArgumentException(
                "Já existe um usuário cadastrado com este login."
            );
        }


        /*
         * ==================================================
         * E-MAIL OPCIONAL
         * ==================================================
         */

        String email =
                normalizarEmail(
                    dto.getEmail()
                );


        if (email != null
                && emailJaCadastrado(
                    email)) {

            throw new IllegalArgumentException(
                "Já existe um usuário cadastrado com este e-mail."
            );
        }


        /*
         * ==================================================
         * PERFIL
         * ==================================================
         */

        if (dto.getPerfil() == null) {

            throw new IllegalArgumentException(
                "Selecione o perfil do usuário."
            );
        }


        /*
         * ==================================================
         * CRIAR USUÁRIO
         * ==================================================
         */

        Usuario usuario =
                new Usuario();


        usuario.setNome(
            nome
        );


        usuario.setLogin(
            login
        );


        usuario.setEmail(
            email
        );


        usuario.setPerfil(
            dto.getPerfil()
        );


        /*
         * ==================================================
         * ACESSO INICIAL
         * ==================================================
         *
         * Usuários comuns recebem automaticamente
         * o acesso correspondente ao perfil principal.
         *
         * Exemplo:
         *
         * Perfil principal:
         * VENDEDOR
         *
         * Acesso inicial:
         * VENDEDOR
         *
         * ADMIN não depende da tabela de acessos,
         * pois possui acesso total automaticamente.
         */

        if (dto.getPerfil()
                != PerfilUsuario.ADMIN) {


            Set<PerfilUsuario> acessosIniciais =
                    new LinkedHashSet<>();


            acessosIniciais.add(
                dto.getPerfil()
            );


            usuario.setAcessos(
                acessosIniciais
            );


        } else {


            usuario.setAcessos(
                new LinkedHashSet<>()
            );
        }


        /*
         * Senha temporária.
         *
         * Apenas o BCrypt é armazenado
         * no banco de dados.
         */

        usuario.setSenha(

            passwordEncoder.encode(
                SENHA_TEMPORARIA
            )

        );


        /*
         * Usuário criado pelo administrador
         * já pode realizar o primeiro login.
         */

        usuario.setAtivo(
            true
        );


        /*
         * Antes de utilizar o sistema,
         * precisa criar uma senha definitiva.
         */

        usuario.setTrocaSenhaObrigatoria(
            true
        );


        return usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * CRIAR ADMINISTRADOR INICIAL
     * ==================================================
     */

    @Transactional
    public void criarAdministradorInicial() {


        /*
         * Se o administrador novo já existe,
         * não altera absolutamente nada.
         *
         * Isso impede que sua senha seja
         * redefinida ao reiniciar o sistema.
         */

        if (usuarioRepository
                .existsByLoginIgnoreCase(
                    LOGIN_ADMINISTRADOR
                )) {

            return;
        }


        /*
         * Procura o administrador
         * existente do sistema antigo.
         *
         * Se existir, reaproveita o registro.
         * Caso contrário, cria um novo.
         */

        Usuario administrador =
                usuarioRepository
                    .findByEmailIgnoreCase(
                        "admin@carpintaria.com"
                    )
                    .orElseGet(
                        Usuario::new
                    );


        administrador.setNome(
            "Administrador"
        );


        administrador.setLogin(
            LOGIN_ADMINISTRADOR
        );


        /*
         * Caso seja um registro novo
         * e ainda não possua e-mail.
         */

        if (administrador.getEmail() == null
                || administrador.getEmail()
                    .isBlank()) {

            administrador.setEmail(
                "admin@carpintaria.com"
            );
        }


        /*
         * Senha temporária inicial.
         */

        administrador.setSenha(

            passwordEncoder.encode(
                SENHA_TEMPORARIA
            )

        );


        administrador.setPerfil(
            PerfilUsuario.ADMIN
        );


        /*
         * O ADMIN não precisa possuir registros
         * na tabela usuario_acessos.
         *
         * O método possuiAcesso() da entidade
         * concede acesso total automaticamente
         * ao perfil ADMIN.
         */

        administrador.setAcessos(
            new LinkedHashSet<>()
        );


        administrador.setAtivo(
            true
        );


        administrador.setTrocaSenhaObrigatoria(
            true
        );


        usuarioRepository.save(
            administrador
        );
    }


    /*
     * ==================================================
     * BUSCAR POR E-MAIL
     * ==================================================
     */

    public Usuario buscarPorEmail(
            String email) {


        return usuarioRepository
                .findByEmailIgnoreCase(
                    email
                )
                .orElseThrow(() ->

                    new RuntimeException(
                        "Usuário não encontrado."
                    )

                );
    }


    /*
     * ==================================================
     * BUSCAR POR LOGIN
     * ==================================================
     */

    public Usuario buscarPorLogin(
            String login) {


        return usuarioRepository
                .findByLoginIgnoreCase(
                    login
                )
                .orElseThrow(() ->

                    new RuntimeException(
                        "Usuário não encontrado."
                    )

                );
    }


    /*
     * ==================================================
     * BUSCAR POR ID
     * ==================================================
     */

    public Usuario buscarPorId(
            Long id) {


        return usuarioRepository
                .findById(
                    id
                )
                .orElseThrow(() ->

                    new RuntimeException(
                        "Usuário não encontrado."
                    )

                );
    }


    /*
     * ==================================================
     * LISTAR USUÁRIOS ATIVOS
     * ==================================================
     */

    public List<Usuario> listarTodos() {


        return usuarioRepository
                .findAll()
                .stream()
                .filter(
                    Usuario::estaAtivo
                )
                .toList();
    }


    /*
     * ==================================================
     * LISTAR TODOS OS USUÁRIOS
     * ==================================================
     */

    public List<Usuario> listarTodosUsuarios() {

        return usuarioRepository
                .findAll();
    }


    /*
     * ==================================================
     * EDITAR USUÁRIO PELO ADMINISTRADOR
     * ==================================================
     *
     * Permite alterar:
     *
     * nome
     * login
     * e-mail
     * perfil principal
     * acessos
     *
     * Não altera:
     *
     * senha
     * status
     * foto
     * primeiro acesso
     * ==================================================
     */

    @Transactional
    public Usuario editarUsuario(
            Long usuarioId,
            EdicaoUsuarioDTO dto) {


        Usuario usuario =
                buscarPorId(
                    usuarioId
                );


        /*
         * ==================================================
         * NOME
         * ==================================================
         */

        if (dto.getNome() == null
                || dto.getNome().isBlank()) {

            throw new IllegalArgumentException(
                "O nome é obrigatório."
            );
        }


        String nome =
                dto.getNome()
                    .trim();


        /*
         * ==================================================
         * LOGIN
         * ==================================================
         */

        if (dto.getLogin() == null
                || dto.getLogin().isBlank()) {

            throw new IllegalArgumentException(
                "O login é obrigatório."
            );
        }


        String novoLogin =
                dto.getLogin()
                    .trim()
                    .toLowerCase();


        /*
         * Verifica se existe outro usuário
         * utilizando o mesmo login.
         */

        Usuario usuarioComLogin =
                usuarioRepository
                    .findByLoginIgnoreCase(
                        novoLogin
                    )
                    .orElse(
                        null
                    );


        if (usuarioComLogin != null
                && !usuarioComLogin
                    .getId()
                    .equals(
                        usuario.getId()
                    )) {

            throw new IllegalArgumentException(
                "Este login já está sendo utilizado por outro usuário."
            );
        }


        /*
         * ==================================================
         * E-MAIL OPCIONAL
         * ==================================================
         */

        String novoEmail =
                normalizarEmail(
                    dto.getEmail()
                );


        /*
         * Só verifica duplicidade quando
         * algum e-mail foi informado.
         */

        if (novoEmail != null) {


            Usuario usuarioComEmail =
                    usuarioRepository
                        .findByEmailIgnoreCase(
                            novoEmail
                        )
                        .orElse(
                            null
                        );


            if (usuarioComEmail != null
                    && !usuarioComEmail
                        .getId()
                        .equals(
                            usuario.getId()
                        )) {

                throw new IllegalArgumentException(
                    "Este e-mail já está sendo utilizado por outro usuário."
                );
            }
        }


        /*
         * ==================================================
         * PERFIL PRINCIPAL
         * ==================================================
         */

        if (dto.getPerfil() == null) {

            throw new IllegalArgumentException(
                "Selecione o perfil principal do usuário."
            );
        }


        /*
         * ==================================================
         * ACESSOS
         * ==================================================
         *
         * O ADMIN é tratado separadamente.
         *
         * Para usuários comuns:
         *
         * 1. Copia os acessos escolhidos.
         * 2. Remove ADMIN da lista.
         * 3. Garante o acesso correspondente
         *    ao perfil principal.
         *
         * Dessa forma:
         *
         * Perfil principal:
         * VENDEDOR
         *
         * Acessos escolhidos:
         * SUPERVISOR_PRODUCAO
         * FINANCEIRO
         *
         * Resultado:
         * VENDEDOR
         * SUPERVISOR_PRODUCAO
         * FINANCEIRO
         */

        Set<PerfilUsuario> novosAcessos =
                new LinkedHashSet<>();


        if (dto.getPerfil()
                != PerfilUsuario.ADMIN) {


            if (dto.getAcessos() != null) {

                novosAcessos.addAll(
                    dto.getAcessos()
                );
            }


            /*
             * ADMIN nunca pode ser concedido
             * apenas como um acesso adicional.
             *
             * Para ser administrador, o perfil
             * principal precisa ser ADMIN.
             */

            novosAcessos.remove(
                PerfilUsuario.ADMIN
            );


            /*
             * O perfil principal sempre concede
             * também o acesso correspondente.
             */

            novosAcessos.add(
                dto.getPerfil()
            );


            /*
             * Proteção adicional.
             *
             * Na prática, como o perfil principal
             * é adicionado acima, sempre existirá
             * ao menos um acesso.
             */

            if (novosAcessos.isEmpty()) {

                throw new IllegalArgumentException(
                    "Selecione pelo menos um acesso para o usuário."
                );
            }


        } else {


            /*
             * ADMIN possui acesso total
             * automaticamente.
             *
             * Não precisa gravar todos os perfis
             * em usuario_acessos.
             */

            novosAcessos.clear();
        }


        /*
         * ==================================================
         * APLICAR ALTERAÇÕES
         * ==================================================
         */

        usuario.setNome(
            nome
        );


        usuario.setLogin(
            novoLogin
        );


        usuario.setEmail(
            novoEmail
        );


        usuario.setPerfil(
            dto.getPerfil()
        );


        usuario.setAcessos(
            novosAcessos
        );


        /*
         * Nenhuma alteração é feita em:
         *
         * senha
         * ativo
         * trocaSenhaObrigatoria
         * fotoPerfilPath
         */

        return usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * ATUALIZAR MEU PERFIL
     * ==================================================
     */

    @Transactional
    public Usuario atualizarPerfil(
            Long usuarioId,
            AtualizacaoPerfilDTO dto) {


        Usuario usuario =
                buscarPorId(
                    usuarioId
                );


        /*
         * ==================================================
         * NOME
         * ==================================================
         */

        usuario.setNome(
            dto.getNome()
                .trim()
        );


        /*
         * ==================================================
         * E-MAIL
         * ==================================================
         *
         * Agora o e-mail pode ser nulo.
         */

        String novoEmail =
                normalizarEmail(
                    dto.getEmail()
                );


        boolean emailAlterado;


        if (usuario.getEmail() == null) {

            emailAlterado =
                    novoEmail != null;

        } else {

            emailAlterado =
                    novoEmail == null
                    || !usuario
                        .getEmail()
                        .equalsIgnoreCase(
                            novoEmail
                        );
        }


        if (emailAlterado) {


            /*
             * Se o novo e-mail não for nulo,
             * verifica duplicidade.
             */

            if (novoEmail != null) {


                Usuario usuarioComEmail =
                        usuarioRepository
                            .findByEmailIgnoreCase(
                                novoEmail
                            )
                            .orElse(
                                null
                            );


                if (usuarioComEmail != null
                        && !usuarioComEmail
                            .getId()
                            .equals(
                                usuario.getId()
                            )) {

                    throw new IllegalArgumentException(
                        "Este e-mail já está sendo utilizado por outro usuário."
                    );
                }
            }


            usuario.setEmail(
                novoEmail
            );
        }


        /*
         * ==================================================
         * NOVA SENHA
         * ==================================================
         *
         * Se o campo ficar vazio,
         * mantém a senha atual.
         */

        if (dto.getNovaSenha() != null
                && !dto.getNovaSenha()
                    .isBlank()) {


            if (dto.getNovaSenha()
                    .length() < 6) {

                throw new IllegalArgumentException(
                    "A nova senha deve possuir pelo menos 6 caracteres."
                );
            }


            if (dto.getConfirmarNovaSenha() == null
                    || dto.getConfirmarNovaSenha()
                        .isBlank()) {

                throw new IllegalArgumentException(
                    "Confirme a nova senha."
                );
            }


            if (!dto.getNovaSenha()
                    .equals(
                        dto.getConfirmarNovaSenha()
                    )) {

                throw new IllegalArgumentException(
                    "A nova senha e a confirmação não coincidem."
                );
            }


            usuario.setSenha(

                passwordEncoder.encode(
                    dto.getNovaSenha()
                )

            );
        }


        return usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * DEFINIR SENHA NO PRIMEIRO ACESSO
     * ==================================================
     */

    @Transactional
    public void definirSenhaPrimeiroAcesso(
            String login,
            String novaSenha,
            String confirmarSenha) {


        Usuario usuario =
                buscarPorLogin(
                    login
                );


        /*
         * Só permite utilizar esta função
         * quando o usuário realmente precisa
         * realizar a troca obrigatória.
         */

        if (!usuario.precisaTrocarSenha()) {

            throw new IllegalStateException(
                "Este usuário não precisa realizar troca de senha."
            );
        }


        /*
         * Nova senha obrigatória.
         */

        if (novaSenha == null
                || novaSenha.isBlank()) {

            throw new IllegalArgumentException(
                "Informe a nova senha."
            );
        }


        /*
         * Tamanho mínimo.
         */

        if (novaSenha.length() < 6) {

            throw new IllegalArgumentException(
                "A nova senha deve possuir pelo menos 6 caracteres."
            );
        }


        /*
         * A senha definitiva precisa ser
         * diferente da senha temporária atual.
         */

        if (passwordEncoder.matches(
                novaSenha,
                usuario.getSenha())) {

            throw new IllegalArgumentException(
                "A nova senha deve ser diferente da senha temporária."
            );
        }


        /*
         * Confirmação obrigatória.
         */

        if (confirmarSenha == null
                || confirmarSenha.isBlank()) {

            throw new IllegalArgumentException(
                "Confirme a nova senha."
            );
        }


        /*
         * As duas senhas precisam
         * ser iguais.
         */

        if (!novaSenha.equals(
                confirmarSenha)) {

            throw new IllegalArgumentException(
                "A nova senha e a confirmação não coincidem."
            );
        }


        /*
         * Armazena somente o BCrypt
         * da nova senha.
         */

        usuario.setSenha(

            passwordEncoder.encode(
                novaSenha
            )

        );


        /*
         * Primeiro acesso concluído.
         */

        usuario.setTrocaSenhaObrigatoria(
            false
        );


        usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * REDEFINIR SENHA TEMPORÁRIA
     * ==================================================
     *
     * Utilizado pelo administrador.
     *
     * A senha do usuário volta para:
     *
     * CarpintariaReis
     *
     * O usuário será obrigado a definir
     * uma nova senha no próximo acesso.
     *
     * Nenhum outro dado da conta é alterado.
     * ==================================================
     */

    @Transactional
    public Usuario redefinirSenhaTemporaria(
            Long usuarioId) {


        Usuario usuario =
                buscarPorId(
                    usuarioId
                );


        /*
         * Armazena somente o BCrypt
         * da senha temporária.
         */

        usuario.setSenha(

            passwordEncoder.encode(
                SENHA_TEMPORARIA
            )

        );


        /*
         * Obriga o usuário a cadastrar
         * uma nova senha.
         */

        usuario.setTrocaSenhaObrigatoria(
            true
        );


        return usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * ATIVAR USUÁRIO
     * ==================================================
     */

    @Transactional
    public Usuario ativar(
            Long usuarioId) {


        Usuario usuario =
                buscarPorId(
                    usuarioId
                );


        usuario.setAtivo(
            true
        );


        return usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * INATIVAR USUÁRIO
     * ==================================================
     */

    @Transactional
    public Usuario inativar(
            Long usuarioId) {


        Usuario usuario =
                buscarPorId(
                    usuarioId
                );


        usuario.setAtivo(
            false
        );


        return usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * ALTERAR STATUS
     * ==================================================
     */

    @Transactional
    public Usuario alterarStatus(
            Long usuarioId) {


        Usuario usuario =
                buscarPorId(
                    usuarioId
                );


        usuario.setAtivo(
            !usuario.estaAtivo()
        );


        return usuarioRepository.save(
            usuario
        );
    }


    /*
     * ==================================================
     * NORMALIZAR E-MAIL OPCIONAL
     * ==================================================
     */

    private String normalizarEmail(
            String email) {


        if (email == null) {

            return null;
        }


        String emailNormalizado =
                email.trim()
                    .toLowerCase();


        if (emailNormalizado.isBlank()) {

            return null;
        }


        return emailNormalizado;
    }
}