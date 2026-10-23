package com.example.demo;

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Login / sessão. Endereços: POST /login, GET /usuario, GET /sessao, POST /logout, POST /registro */
@RestController
public class LoginController {

    private final UsuarioDAO usuarioDao = new UsuarioDAO();
    private final CadastroDAO cadastroDao = new CadastroDAO();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email,
            @RequestParam String senha,
            HttpSession session) {

        String emailLimpo = email.trim().toLowerCase();

        try {
            // 1) Administrador (tabela usuarios)
            String hashAdmin = usuarioDao.buscarHashPorEmail(emailLimpo);
            if (hashAdmin != null && SenhaUtil.conferir(senha, hashAdmin)) {
                session.setAttribute("usuarioLogado", emailLimpo);
                session.setAttribute("papel", "admin");
                return ResponseEntity.ok("Login realizado!");
            }

            // 2) Cliente que se cadastrou (tabela cadastros)
            String hashCliente = cadastroDao.buscarSenhaHashPorEmail(emailLimpo);
            if (hashCliente != null && SenhaUtil.conferir(senha, hashCliente)) {
                Cadastro c = cadastroDao.buscarPorEmail(emailLimpo);
                session.setAttribute("usuarioLogado", emailLimpo);
                session.setAttribute("papel", "cliente");
                session.setAttribute("nomeUsuario", c.nome());
                return ResponseEntity.ok("Login realizado!");
            }

            return ResponseEntity.status(401).body("Usuário ou senha inválidos");

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro no banco de dados: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/usuario")
    public ResponseEntity<String> usuarioLogado(HttpSession session) {
        Object email = session.getAttribute("usuarioLogado");
        if (email == null) {
            return ResponseEntity.status(401).body("Não logado");
        }
        return ResponseEntity.ok(email.toString());
    }

    /** Diz quem está logado e qual o papel (admin ou cliente). */
    @GetMapping("/sessao")
    public ResponseEntity<?> sessao(HttpSession session) {
        Object email = session.getAttribute("usuarioLogado");
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Não logado"));
        }
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("email", email.toString());
        corpo.put("papel", session.getAttribute("papel"));
        corpo.put("nome", session.getAttribute("nomeUsuario"));
        return ResponseEntity.ok(corpo);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Saiu");
    }
}