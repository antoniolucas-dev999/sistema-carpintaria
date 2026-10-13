package br.com.carpintaria.carpintaria.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.repository.UsuarioRepository;


@Service
public class FotoPerfilService {


    /*
     * Limite interno de segurança.
     *
     * Depois também configuraremos o limite
     * global do Spring para upload.
     */
    private static final long TAMANHO_MAXIMO =
            5L * 1024L * 1024L;


    private final UsuarioRepository usuarioRepository;

    private final Path diretorioBase;


    public FotoPerfilService(

            UsuarioRepository usuarioRepository,

            @Value(
                "${app.upload.perfil-dir:uploads/perfis}"
            )
            String diretorioPerfil) {


        this.usuarioRepository =
                usuarioRepository;


        this.diretorioBase =
                Path.of(
                    diretorioPerfil
                )
                .toAbsolutePath()
                .normalize();
    }


    /*
     * ==================================================
     * SALVAR FOTO
     * ==================================================
     */

    @Transactional
    public void salvarFotoPerfil(
            Usuario usuario,
            MultipartFile arquivo) {


        if (usuario == null
                || usuario.getId() == null) {

            throw new IllegalArgumentException(
                "Usuário inválido."
            );
        }


        if (arquivo == null
                || arquivo.isEmpty()) {

            throw new IllegalArgumentException(
                "Selecione uma imagem."
            );
        }


        if (arquivo.getSize()
                > TAMANHO_MAXIMO) {

            throw new IllegalArgumentException(
                "A imagem deve possuir no máximo 5 MB."
            );
        }


        byte[] conteudo;


        try {

            conteudo =
                    arquivo.getBytes();

        } catch (IOException e) {

            throw new IllegalStateException(
                "Não foi possível ler a imagem enviada.",
                e
            );
        }


        /*
         * Não confiamos apenas no nome
         * ou no Content-Type enviado
         * pelo navegador.
         *
         * O formato é identificado
         * pelos bytes reais do arquivo.
         */
        String extensao =
                detectarExtensao(
                    conteudo
                );


        if (extensao == null) {

            throw new IllegalArgumentException(
                "Formato de imagem inválido. Utilize PNG, JPG, JPEG ou WebP."
            );
        }


        String caminhoAnterior =
                usuario.getFotoPerfilPath();


        String nomeArquivo =
                UUID.randomUUID()
                + "."
                + extensao;


        Path diretorioUsuario =
                diretorioBase
                    .resolve(
                        usuario.getId()
                            .toString()
                    )
                    .normalize();


        validarCaminho(
            diretorioUsuario
        );


        Path destino =
                diretorioUsuario
                    .resolve(
                        nomeArquivo
                    )
                    .normalize();


        validarCaminho(
            destino
        );


        try {


            Files.createDirectories(
                diretorioUsuario
            );


            Files.write(
                destino,
                conteudo,
                StandardOpenOption.CREATE_NEW
            );


        } catch (IOException e) {

            throw new IllegalStateException(
                "Não foi possível salvar a foto de perfil.",
                e
            );
        }


        String caminhoRelativo =
                diretorioBase
                    .relativize(
                        destino
                    )
                    .toString()
                    .replace(
                        '\\',
                        '/'
                    );


        usuario.setFotoPerfilPath(
            caminhoRelativo
        );


        try {


            usuarioRepository
                .saveAndFlush(
                    usuario
                );


        } catch (RuntimeException e) {


            apagarArquivoSilenciosamente(
                destino
            );


            throw e;
        }


        /*
         * Só remove a foto antiga
         * depois da nova ter sido salva.
         */
        apagarFotoAnterior(
            caminhoAnterior,
            caminhoRelativo
        );
    }


    /*
     * ==================================================
     * CARREGAR FOTO
     * ==================================================
     */

    public Optional<Resource> carregarFotoPerfil(
            Usuario usuario) {


        if (usuario == null
                || !usuario.possuiFotoPerfil()) {

            return Optional.empty();
        }


        Path arquivo =
                resolverCaminho(
                    usuario.getFotoPerfilPath()
                );


        if (!Files.exists(
                arquivo)
                || !Files.isRegularFile(
                    arquivo)) {

            return Optional.empty();
        }


        try {


            Resource resource =
                    new UrlResource(
                        arquivo.toUri()
                    );


            if (!resource.exists()
                    || !resource.isReadable()) {

                return Optional.empty();
            }


            return Optional.of(
                resource
            );


        } catch (Exception e) {

            return Optional.empty();
        }
    }


    /*
     * ==================================================
     * CONTENT TYPE
     * ==================================================
     */

    public String obterContentType(
            Usuario usuario) {


        if (usuario == null
                || usuario.getFotoPerfilPath() == null) {

            return "application/octet-stream";
        }


        String caminho =
                usuario.getFotoPerfilPath()
                    .toLowerCase();


        if (caminho.endsWith(
                ".png")) {

            return "image/png";
        }


        if (caminho.endsWith(
                ".jpg")
                || caminho.endsWith(
                    ".jpeg")) {

            return "image/jpeg";
        }


        if (caminho.endsWith(
                ".webp")) {

            return "image/webp";
        }


        return "application/octet-stream";
    }


    /*
     * ==================================================
     * REMOVER FOTO
     * ==================================================
     */

    @Transactional
    public void removerFotoPerfil(
            Usuario usuario) {


        if (usuario == null
                || !usuario.possuiFotoPerfil()) {

            return;
        }


        String caminhoAnterior =
                usuario.getFotoPerfilPath();


        usuario.setFotoPerfilPath(
            null
        );


        usuarioRepository
            .saveAndFlush(
                usuario
            );


        Path arquivo =
                resolverCaminho(
                    caminhoAnterior
                );


        apagarArquivoSilenciosamente(
            arquivo
        );
    }


    /*
     * ==================================================
     * DETECTAR FORMATO REAL
     * ==================================================
     */

    private String detectarExtensao(
            byte[] bytes) {


        /*
         * PNG
         *
         * 89 50 4E 47 0D 0A 1A 0A
         */
        if (bytes.length >= 8

                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == 0x1A
                && bytes[7] == 0x0A) {

            return "png";
        }


        /*
         * JPEG / JPG
         *
         * FF D8 FF
         */
        if (bytes.length >= 3

                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {

            return "jpg";
        }


        /*
         * WEBP
         *
         * RIFF .... WEBP
         */
        if (bytes.length >= 12

                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'

                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P') {

            return "webp";
        }


        return null;
    }


    /*
     * ==================================================
     * RESOLVER CAMINHO
     * ==================================================
     */

    private Path resolverCaminho(
            String caminhoRelativo) {


        Path caminho =
                diretorioBase
                    .resolve(
                        caminhoRelativo
                    )
                    .normalize();


        validarCaminho(
            caminho
        );


        return caminho;
    }


    /*
     * ==================================================
     * PROTEÇÃO CONTRA PATH TRAVERSAL
     * ==================================================
     */

    private void validarCaminho(
            Path caminho) {


        if (!caminho.startsWith(
                diretorioBase)) {

            throw new IllegalStateException(
                "Caminho de arquivo inválido."
            );
        }
    }


    /*
     * ==================================================
     * APAGAR FOTO ANTERIOR
     * ==================================================
     */

    private void apagarFotoAnterior(
            String caminhoAnterior,
            String caminhoNovo) {


        if (caminhoAnterior == null
                || caminhoAnterior.isBlank()
                || caminhoAnterior.equals(
                    caminhoNovo
                )) {

            return;
        }


        Path arquivoAnterior =
                resolverCaminho(
                    caminhoAnterior
                );


        apagarArquivoSilenciosamente(
            arquivoAnterior
        );
    }


    /*
     * ==================================================
     * APAGAR ARQUIVO
     * ==================================================
     */

    private void apagarArquivoSilenciosamente(
            Path arquivo) {


        try {

            Files.deleteIfExists(
                arquivo
            );

        } catch (IOException ignored) {

            /*
             * Não impede a operação principal
             * caso a limpeza do arquivo antigo falhe.
             */
        }
    }
}