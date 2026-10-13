# Sistema de Carpintaria

Sistema web desenvolvido para auxiliar no gerenciamento de uma carpintaria, permitindo o controle de usuários, clientes e orçamentos, além do cálculo automático de itens comercializados por metro linear, metro quadrado e metro cúbico.

Projeto acadêmico desenvolvido para a disciplina de **Programação Web**.

## Funcionalidades

- Cadastro e autenticação de usuários.
- Novos usuários são cadastrados inicialmente como inativos.
- Ativação e inativação de usuários.
- Alteração de nome, e-mail e senha do próprio usuário.
- Controle de acesso às páginas internas do sistema.
- Cadastro, consulta, edição e exclusão de clientes.
- Cadastro e gerenciamento de orçamentos.
- Inclusão, edição e exclusão de itens do orçamento.
- Seleção do vendedor responsável pelo orçamento.
- Controle de status do orçamento.
- Cálculo de frete e desconto.
- Cálculo automático dos itens por:
  - **ML** — Metro Linear.
  - **M²** — Metro Quadrado.
  - **M³** — Metro Cúbico.
- Impressão do orçamento em formato adequado para impressão ou PDF.
- Dashboard com informações gerais do sistema.
- Interface responsiva para computador e dispositivos móveis.

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring Data JPA
- Spring Security
- Bean Validation
- Hibernate
- Maven

### Frontend

- HTML5
- CSS3
- JavaScript
- Thymeleaf
- Bootstrap 5
- Bootstrap Icons

### Banco de dados

- H2 Database

## Segurança

O sistema utiliza **Spring Security** para proteger as páginas internas e **BCrypt** para o hash das senhas dos usuários.

Somente usuários autenticados podem acessar as funcionalidades internas.

Os novos usuários cadastrados pelo formulário público começam com o status **inativo** e precisam ser ativados na área de usuários antes de realizar login.

## Pré-requisitos

Para executar o projeto é necessário ter instalado:

- Java 21 ou superior.
- Git, caso o projeto seja clonado pelo GitHub.

Não é necessário instalar o Maven separadamente, pois o projeto possui o **Maven Wrapper**.

## Como executar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/Jadsonsouzadev7/sistema-carpintaria.git
```

### 2. Entrar na pasta do projeto

```bash
cd sistema-carpintaria
```

### 3. Executar no Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux ou macOS

```bash
./mvnw spring-boot:run
```

### 4. Acessar o sistema

Após a inicialização, acesse:

```text
http://localhost:8080/login
```

## Acesso inicial

Quando o sistema é iniciado com um banco de dados vazio, é criado um usuário inicial para permitir o primeiro acesso:

```text
E-mail: admin@carpintaria.com
Senha: Admin@123
```

> Este usuário é destinado ao primeiro acesso e à demonstração acadêmica do sistema.

Depois de entrar, é possível acessar a tela **Usuários** para ativar os novos usuários cadastrados.

## Fluxo de cadastro de usuário

1. Na tela de login, acessar a opção de criação de conta.
2. Informar nome, e-mail e senha.
3. O usuário será cadastrado como **inativo**.
4. Um usuário autenticado deve acessar a área **Usuários** e ativar a nova conta.
5. Após a ativação, o novo usuário poderá realizar login normalmente.

## Cálculo dos itens do orçamento

O sistema permite três critérios de cálculo.

### Metro Linear — ML

```text
Medida calculada = Comprimento × Quantidade
Valor total = Medida calculada × Preço por ML
```

### Metro Quadrado — M²

```text
Medida calculada = Comprimento × Largura × Quantidade
Valor total = Medida calculada × Preço por M²
```

### Metro Cúbico — M³

```text
Medida calculada = Comprimento × Largura × Espessura × Quantidade
Valor total = Medida calculada × Preço por M³
```

As medidas são informadas em metros.

## Estrutura do projeto

```text
src/main/java/br/com/carpintaria/carpintaria
├── config
├── controller
├── dto
├── model
├── repository
└── service

src/main/resources
├── static
│   └── css
├── templates
│   ├── clientes
│   ├── fragments
│   ├── orcamentos
│   └── usuarios
└── application.properties
```

A aplicação está organizada em camadas, separando controllers, regras de negócio, persistência de dados, modelos e interface do usuário.

## Banco de dados

O projeto utiliza banco de dados H2 persistido localmente.

O banco é criado automaticamente ao iniciar o sistema e fica armazenado na pasta:

```text
data/
```

Essa pasta não é enviada ao GitHub, pois está configurada no `.gitignore`.

Dessa forma, cada ambiente cria seu próprio banco local ao executar o projeto.

## Compilar o projeto

Para apenas compilar e verificar o projeto:

### Windows

```powershell
.\mvnw.cmd clean compile
```

### Linux ou macOS

```bash
./mvnw clean compile
```

## Encerrar a aplicação

No terminal em que o Spring Boot estiver executando:

```text
Ctrl + C
```

## Repositório

GitHub:

```text
https://github.com/Jadsonsouzadev7/sistema-carpintaria
```

---

**Sistema de Carpintaria — Projeto Final de Programação Web**
