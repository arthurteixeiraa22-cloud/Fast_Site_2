# Login + Cadastro — Guilherme Felberque Eucaristico

Spring Boot + MySQL + BCrypt, com telas em HTML/CSS/JS puro (dark mode).

## Estrutura

```
demo/
├── pom.xml
├── schema.sql                         ← script do banco
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java       ← liga o servidor
    │   ├── ConexaoDB.java             ← URL / usuário / senha do MySQL (ÚNICO lugar)
    │   ├── SenhaUtil.java             ← BCrypt (criptografar / conferir)
    │   ├── UsuarioDAO.java            ← tabela usuarios (login do administrador)
    │   ├── LoginController.java       ← /login, /usuario, /logout
    │   ├── Cadastro.java              ← dados que voltam para a tela (sem senha)
    │   ├── CadastroRequest.java       ← dados que o cliente envia ao salvar
    │   ├── CadastroDAO.java           ← tabela cadastros (salvar, listar, buscar)
    │   ├── CadastroController.java    ← /cadastros, /cadastros/{id}, /cadastros/busca
    │   └── GerarHash.java             ← temporário: cria o 1º usuário de login
    └── resources/static/
        ├── index.html                 ← redireciona para o login
        ├── criptografia.html          ← TELA 1: login + link para cadastro público
        ├── novo-cadastro.html         ← cadastro público do cliente (sem login)
        ├── cadastro.html              ← TELA ADMIN: lista e busca de clientes (exige login)
        └── style.css                  ← tema escuro das telas
```

> `home.html` não é mais usado. Pode apagar.

## Como rodar

1. **Banco** — rode o `schema.sql` no MySQL (Workbench, DBeaver ou terminal).
2. **Senha do MySQL** — em `ConexaoDB.java`, coloque sua senha em `PASS`.
3. **Usuário administrador** — rode o `main` do `GerarHash.java`, copie o `INSERT` do console e execute no MySQL. Esse é o único usuário da tabela `usuarios`, e é ele que acessa a tela de administrador.
4. **Servidor** — rode `DemoApplication` (ou `mvn spring-boot:run`).
5. Abra **http://localhost:8081** e entre com o e-mail e a senha do passo 3.

## Testando com 3 cadastros

Pela tela de login, clique em **"Não tem cadastro? Cadastre-se"** e salve três pessoas diferentes, por exemplo:

| Nome | E-mail | Senha |
|---|---|---|
| Ana Souza | ana@teste.com | ana123456 |
| Bruno Lima | bruno@teste.com | bruno123456 |
| Carla Dias | carla@teste.com | carla123456 |

- Cada salvamento mostra o alerta **"Cadastro realizado com sucesso!"**.
- Depois, entre como administrador para conferir a lista.
- **Listar todos**: clique em uma linha para ver os dados completos.
- **Buscar por ID / E-mail**: digite `2` ou `bruno@teste.com`.
- No MySQL, `SELECT email, senha_hash FROM cadastros;` mostra que só o hash (`$2a$10$...`) foi salvo.

## Endpoints

| Método | Endereço | Exige login? | O que faz |
|---|---|---|---|
| POST | `/login` | não | confere e-mail/senha e cria a sessão |
| GET | `/usuario` | não | diz quem está logado (401 se ninguém) |
| POST | `/logout` | não | encerra a sessão |
| POST | `/cadastros` | não | salva um cadastro de cliente (senha vira hash) |
| GET | `/cadastros` | sim | lista todos |
| GET | `/cadastros/{id}` | sim | busca por ID |
| GET | `/cadastros/busca?email=` | sim | busca por e-mail |

As rotas `GET /cadastros` exigem login. Sem sessão, respondem 401 e a tela volta para o login.