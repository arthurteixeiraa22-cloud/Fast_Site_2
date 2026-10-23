package com.example.demo;

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Login / sessão. Endereços: POST /login, GET /usuario, POST /logout */
@RestController
public class LoginController {

    private final UsuarioDAO dao = new UsuarioDAO();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email,
            @RequestParam String senha,
            HttpSession session) {

        String emailLimpo = email.trim().toLowerCase();

        try {
            String hashSalvo = dao.buscarHashPorEmail(emailLimpo);

            if (SenhaUtil.conferir(senha, hashSalvo)) {
                session.setAttribute("usuarioLogado", emailLimpo);
                return ResponseEntity.ok("Login realizado!");
            }
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");

        } catch (SQLException e) {
            // Problema de CONEXÃO com o MySQL (driver, senha do root, banco desligado)
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Saiu");
    }
}