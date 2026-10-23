# Sistema de Login e Cadastro de Clientes

Projeto com Spring Boot e MySQL. Tem uma tela pública de cadastro de clientes, uma tela de login para clientes e administrador, uma tela de boas-vindas para o cliente e uma tela administrativa para listar e buscar os clientes. As senhas são guardadas no banco como hash **BCrypt**, nunca em texto puro. As telas são feitas com HTML, CSS e JavaScript puros, com tema escuro.

## Tecnologias

- Java
- Spring Boot (Spring Web)
- MySQL
- JDBC
- BCrypt (biblioteca `jbcrypt`)
- HTML, CSS e JavaScript puros

## Telas

| Tela | Endereço | Acesso |
|---|---|---|
| Login (administrador e clientes) | `/criptografia.html` | público |
| Cadastro de cliente | `/novo-cadastro.html` | público |
| Bem-vindo (tela do cliente) | `/bem-vindo.html` | somente cliente logado |
| Clientes cadastrados (lista e busca) | `/cadastro.html` | somente administrador logado |

## Estrutura do projeto

### Raiz

| Arquivo | Descrição |
|---|---|
| [pom.xml](pom.xml) | Configuração do Maven e dependências do projeto |
| [schema.sql](schema.sql) | Cria o banco `meu_db` e as tabelas `usuarios` e `cadastros` |
| [LEIA-ME.md](LEIA-ME.md) | Instruções detalhadas de execução e teste |

### Código Java (`src/main/java/com/example/demo/`)

| Arquivo | Descrição |
|---|---|
| [DemoApplication.java](src/main/java/com/example/demo/DemoApplication.java) | Classe principal que inicia o servidor Spring Boot |
| [ConexaoDB.java](src/main/java/com/example/demo/ConexaoDB.java) | URL, usuário e senha do MySQL. É o único lugar com essa configuração |
| [SenhaUtil.java](src/main/java/com/example/demo/SenhaUtil.java) | Criptografia BCrypt: gerar o hash e conferir a senha |
| [UsuarioDAO.java](src/main/java/com/example/demo/UsuarioDAO.java) | Acesso à tabela `usuarios` (login do administrador) |
| [LoginController.java](src/main/java/com/example/demo/LoginController.java) | Endpoints `/login`, `/usuario`, `/sessao`, `/logout`. Confere administrador e cliente |
| [Cadastro.java](src/main/java/com/example/demo/Cadastro.java) | Dados de um cliente que voltam para a tela. Não inclui a senha |
| [CadastroRequest.java](src/main/java/com/example/demo/CadastroRequest.java) | Dados que a tela de cadastro envia, incluindo a senha pura |
| [CadastroDAO.java](src/main/java/com/example/demo/CadastroDAO.java) | Acesso à tabela `cadastros`: salvar, listar, buscar e obter o hash da senha do cliente |
| [CadastroController.java](src/main/java/com/example/demo/CadastroController.java) | Endpoints `/cadastros`, `/cadastros/{id}` e `/cadastros/busca`. As consultas são só do administrador |
| [GerarHash.java](src/main/java/com/example/demo/GerarHash.java) | Utilitário temporário que gera o `INSERT` do primeiro administrador |

### Telas (`src/main/resources/static/`)

| Arquivo | Descrição |
|---|---|
| [index.html](src/main/resources/static/index.html) | Redireciona para a tela de login |
| [criptografia.html](src/main/resources/static/criptografia.html) | Tela de login, com link para o cadastro público. Leva cada pessoa para a tela certa |
| [novo-cadastro.html](src/main/resources/static/novo-cadastro.html) | Formulário público de cadastro de cliente |
| [bem-vindo.html](src/main/resources/static/bem-vindo.html) | Tela de boas-vindas do cliente, após o login |
| [cadastro.html](src/main/resources/static/cadastro.html) | Tela administrativa: lista e busca de clientes |
| [style.css](src/main/resources/static/style.css) | Tema escuro compartilhado pelas telas |

> Os caminhos seguem o padrão do Maven e do Spring Boot. Se seus arquivos estiverem em outra pasta, ajuste os links.

## Como funciona

1. A pessoa entra em `criptografia.html` com e-mail e senha. O JavaScript envia os dados para `POST /login`.
2. O `LoginController` procura primeiro na tabela `usuarios` (administrador). Se não achar, procura na tabela `cadastros` (cliente). Em ambos os casos confere a senha com o `SenhaUtil`.
3. Se estiver correto, o e-mail e o papel (`admin` ou `cliente`) são guardados na sessão.
4. A tela consulta `GET /sessao` e direciona cada pessoa:
   - **Administrador** → `cadastro.html`, com a lista de clientes.
   - **Cliente** → `bem-vindo.html`, com a mensagem de boas-vindas.
5. Qualquer pessoa pode preencher `novo-cadastro.html`. Ao salvar, o `CadastroController` valida os campos, transforma a senha em hash com o `SenhaUtil` e grava no banco pelo `CadastroDAO`.
6. As rotas `GET /cadastros` só funcionam para o administrador. Sem sessão, respondem `401` e a tela volta para o login. Com sessão de cliente, respondem `403`.
7. O botão **Sair** chama `POST /logout`, que encerra a sessão.

## Endpoints

| Método | Endereço | Exige login? | Quem pode | O que faz |
|---|---|---|---|---|
| `POST` | `/login` | não | todos | Confere e-mail e senha (administrador ou cliente) e cria a sessão |
| `GET` | `/usuario` | não | todos | Diz quem está logado (`401` se ninguém) |
| `GET` | `/sessao` | não | todos | Diz quem está logado, o papel (`admin` ou `cliente`) e o nome (`401` se ninguém) |
| `POST` | `/logout` | não | todos | Encerra a sessão |
| `POST` | `/cadastros` | não | todos | Salva um cadastro de cliente (a senha vira hash) |
| `GET` | `/cadastros` | sim | somente administrador | Lista todos os cadastros |
| `GET` | `/cadastros/{id}` | sim | somente administrador | Busca um cadastro pelo ID |
| `GET` | `/cadastros/busca?email=` | sim | somente administrador | Busca um cadastro pelo e-mail |

## Banco de dados

O [schema.sql](schema.sql) cria duas tabelas no banco `meu_db`:

- **`usuarios`**: administradores que podem fazer login (`email`, `senha_hash`).
- **`cadastros`**: clientes cadastrados pela tela pública (nome, endereço, CEP, número, telefone, observação, e-mail, `senha_hash` e data de criação). O cliente entra no sistema com o e-mail e a senha escolhidos nesse cadastro.

A senha nunca é salva em texto puro, e o campo de senha não aparece nas consultas que voltam para a tela.

## Como executar

### 1. Criar o banco de dados

Execute o [schema.sql](schema.sql) no MySQL (Workbench, DBeaver ou terminal).

### 2. Configurar a conexão

Em [ConexaoDB.java](src/main/java/com/example/demo/ConexaoDB.java), coloque a senha do seu MySQL no campo `PASS`.

### 3. Criar o administrador

Edite e-mail e senha em [GerarHash.java](src/main/java/com/example/demo/GerarHash.java) e execute a classe. Ela imprime um `INSERT` no console. Copie esse comando e execute no MySQL. Esse é o usuário que acessa a tela administrativa.

### 4. Iniciar o servidor

Execute a classe [DemoApplication.java](src/main/java/com/example/demo/DemoApplication.java), ou rode `mvn spring-boot:run` na raiz do projeto.

### 5. Acessar o sistema

Abra no navegador:

```
http://localhost:8081/criptografia.html
```

Entre com o e-mail e a senha do administrador (passo 3), ou com o e-mail e a senha de um cliente já cadastrado.

### Testando o fluxo completo

1. Na tela de login, clique em **"Não tem cadastro? Cadastre-se"** e salve um cliente, por exemplo:

   | Nome | E-mail | Senha |
   |---|---|---|
   | Ana Souza | ana@teste.com | ana123456 |
   | Bruno Lima | bruno@teste.com | bruno123456 |
   | Carla Dias | carla@teste.com | carla123456 |

2. Entre com `ana@teste.com` e `ana123456`. Você deve cair em **"Bem-vindo, Ana Souza!"**.
3. Clique em **Sair**, entre com o administrador e confira a lista com os três clientes.
4. Para ver que a senha foi criptografada, rode no MySQL:

```sql
   SELECT email, senha_hash FROM cadastros;
```

## Observações de segurança

Este projeto é para estudo. Antes de usar em produção, considere:

- **Não deixe senhas no código.** A senha do MySQL está em `ConexaoDB.java` e a senha do administrador de teste está em `GerarHash.java`. Use variáveis de ambiente ou um arquivo de configuração fora do repositório.
- **Não use o usuário `root` na aplicação.** Crie um usuário específico com permissões mínimas.
- **Use HTTPS** para proteger as credenciais em trânsito.
- **Proteja o cadastro público.** Qualquer pessoa pode criar uma conta de cliente. Em um sistema real, considere um código de convite ou confirmação por e-mail.
- **Não suba arquivos compilados** (`*.class`) nem a pasta `target/` para o GitHub. Use um `.gitignore`.
