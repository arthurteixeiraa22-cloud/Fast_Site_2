package com.example.demo;

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tela de Cadastro. Só funciona com alguém LOGADO (confere a sessão).
 *
 * POST /cadastros -> salva um novo cadastro (JSON)
 * GET /cadastros -> lista todos
 * GET /cadastros/{id} -> busca um pelo ID
 * GET /cadastros/busca?email= -> busca um pelo e-mail
 */
@RestController
public class CadastroController {

    private final CadastroDAO dao = new CadastroDAO();

    @PostMapping("/cadastros")
    public ResponseEntity<?> salvar(@RequestBody CadastroRequest c) {

        // 1) Validação: todos obrigatórios, menos observação
        if (vazio(c.nome()) || vazio(c.endereco()) || vazio(c.cep()) || vazio(c.numero())
                || vazio(c.telefone()) || vazio(c.email()) || vazio(c.senha())) {
            return erro(400, "Preencha todos os campos obrigatórios.");
        }
        if (!c.email().contains("@")) {
            return erro(400, "E-mail inválido.");
        }
        if (c.senha().length() < 6) {
            return erro(400, "A senha precisa ter pelo menos 6 caracteres.");
        }

        try {
            // 2) A senha vira HASH antes de ir para o banco
            String hash = SenhaUtil.criptografar(c.senha());
            int id = dao.salvar(c, hash);
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Cadastro realizado com sucesso!",
                    "id", id));

        } catch (SQLIntegrityConstraintViolationException e) {
            return erro(409, "Já existe um cadastro com esse e-mail.");
        } catch (SQLException e) {
            e.printStackTrace();
            return erro(500, "Erro ao salvar no banco de dados.");
        }
    }

    @GetMapping("/cadastros")
    public ResponseEntity<?> listar(HttpSession session) {
        if (!logado(session))
            return naoLogado();
        try {
            return ResponseEntity.ok(dao.listarTodos());
        } catch (SQLException e) {
            e.printStackTrace();
            return erro(500, "Erro ao consultar o banco de dados.");
        }
    }

    @GetMapping("/cadastros/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, HttpSession session) {
        if (!logado(session))
            return naoLogado();
        try {
            Cadastro c = dao.buscarPorId(id);
            return c != null ? ResponseEntity.ok(c) : erro(404, "Nenhum cadastro com o ID " + id + ".");
        } catch (SQLException e) {
            e.printStackTrace();
            return erro(500, "Erro ao consultar o banco de dados.");
        }
    }

    @GetMapping("/cadastros/busca")
    public ResponseEntity<?> buscarPorEmail(@RequestParam String email, HttpSession session) {
        if (!logado(session))
            return naoLogado();
        try {
            Cadastro c = dao.buscarPorEmail(email);
            return c != null ? ResponseEntity.ok(c) : erro(404, "Nenhum cadastro com esse e-mail.");
        } catch (SQLException e) {
            e.printStackTrace();
            return erro(500, "Erro ao consultar o banco de dados.");
        }
    }

    // ---------- ajudantes ----------
    private boolean logado(HttpSession session) {
        return session.getAttribute("usuarioLogado") != null;
    }

    private ResponseEntity<?> naoLogado() {
        return erro(401, "Não logado");
    }

    private ResponseEntity<?> erro(int status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }

    private boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}
