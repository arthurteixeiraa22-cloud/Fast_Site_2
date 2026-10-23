# Sistema de Login e Cadastro de Clientes

Projeto com Spring Boot e MySQL. Tem uma tela de login para o administrador, uma tela pública de cadastro de clientes e uma tela administrativa para listar e buscar esses clientes. As senhas são guardadas no banco como hash **BCrypt**, nunca em texto puro. As telas são feitas com HTML, CSS e JavaScript puros, com tema escuro.

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
| Login do administrador | `/criptografia.html` | público |
| Cadastro de cliente | `/novo-cadastro.html` | público |
| Clientes cadastrados (lista e busca) | `/cadastro.html` | somente logado |

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
| [LoginController.java](src/main/java/com/example/demo/LoginController.java) | Endpoints `/login`, `/usuario` e `/logout` |
| [Cadastro.java](src/main/java/com/example/demo/Cadastro.java) | Dados de um cliente que voltam para a tela. Não inclui a senha |
| [CadastroRequest.java](src/main/java/com/example/demo/CadastroRequest.java) | Dados que a tela de cadastro envia, incluindo a senha pura |
| [CadastroDAO.java](src/main/java/com/example/demo/CadastroDAO.java) | Acesso à tabela `cadastros`: salvar, listar e buscar |
| [CadastroController.java](src/main/java/com/example/demo/CadastroController.java) | Endpoints `/cadastros`, `/cadastros/{id}` e `/cadastros/busca` |
| [GerarHash.java](src/main/java/com/example/demo/GerarHash.java) | Utilitário temporário que gera o `INSERT` do primeiro administrador |

### Telas (`src/main/resources/static/`)

| Arquivo | Descrição |
|---|---|
| [index.html](src/main/resources/static/index.html) | Redireciona para a tela de login |
| [criptografia.html](src/main/resources/static/criptografia.html) | Tela de login, com link para o cadastro público |
| [novo-cadastro.html](src/main/resources/static/novo-cadastro.html) | Formulário público de cadastro de cliente |
| [cadastro.html](src/main/resources/static/cadastro.html) | Tela administrativa: lista e busca de clientes |
| [style.css](src/main/resources/static/style.css) | Tema escuro compartilhado pelas telas |

> Os caminhos seguem o padrão do Maven e do Spring Boot. Se seus arquivos estiverem em outra pasta, ajuste os links.

## Como funciona

1. O administrador entra em `criptografia.html`. O JavaScript envia e-mail e senha para `POST /login`.
2. O `LoginController` busca o hash no banco pelo `UsuarioDAO` e confere a senha com o `SenhaUtil`.
3. Se estiver correto, o e-mail é guardado na sessão e a tela abre `cadastro.html`.
4. Qualquer pessoa pode preencher `novo-cadastro.html`. Ao salvar, o `CadastroController` valida os campos, transforma a senha em hash com o `SenhaUtil` e grava no banco pelo `CadastroDAO`.
5. Em `cadastro.html`, o administrador lista todos os clientes, clica em um para ver os detalhes ou busca por ID ou e-mail. Essas rotas exigem sessão ativa. Sem login, respondem `401` e a tela volta para o login.
6. O botão **Sair** chama `POST /logout`, que encerra a sessão.

## Endpoints

| Método | Endereço | Exige login? | O que faz |
|---|---|---|---|
| `POST` | `/login` | não | Confere e-mail e senha e cria a sessão |
| `GET` | `/usuario` | não | Diz quem está logado (`401` se ninguém) |
| `POST` | `/logout` | não | Encerra a sessão |
| `POST` | `/cadastros` | não | Salva um cadastro de cliente (a senha vira hash) |
| `GET` | `/cadastros` | sim | Lista todos os cadastros |
| `GET` | `/cadastros/{id}` | sim | Busca um cadastro pelo ID |
| `GET` | `/cadastros/busca?email=` | sim | Busca um cadastro pelo e-mail |

## Banco de dados

O [schema.sql](schema.sql) cria duas tabelas no banco `meu_db`:

- **`usuarios`**: administradores que podem fazer login (`email`, `senha_hash`).
- **`cadastros`**: clientes cadastrados pela tela pública (nome, endereço, CEP, número, telefone, observação, e-mail, `senha_hash` e data de criação).

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
http://localhost:8081
```

Entre com o e-mail e a senha definidos no passo 3.

### Testando com 3 cadastros

Na tela de login, clique em **"Não tem cadastro? Cadastre-se"** e salve três clientes:

| Nome | E-mail | Senha |
|---|---|---|
| Ana Souza | ana@teste.com | ana123456 |
| Bruno Lima | bruno@teste.com | bruno123456 |
| Carla Dias | carla@teste.com | carla123456 |

Depois, entre como administrador e confira a lista. Para ver que a senha foi criptografada, rode no MySQL:

```sql
SELECT email, senha_hash FROM cadastros;
```

## Observações de segurança

Este projeto é para estudo. Antes de usar em produção, considere:

- **Não deixe senhas no código.** A senha do MySQL está em `ConexaoDB.java` e a senha do administrador de teste está em `GerarHash.java`. Use variáveis de ambiente ou um arquivo de configuração fora do repositório.
- **Não use o usuário `root` na aplicação.** Crie um usuário específico com permissões mínimas.
- **Use HTTPS** para proteger as credenciais em trânsito.
- **Não suba arquivos compilados** (`*.class`) nem a pasta `target/` para o GitHub. Use um `.gitignore`.
