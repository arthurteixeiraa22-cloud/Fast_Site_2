package com.example.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversa com a tabela "cadastros": salvar, listar e buscar.
 * Todas as consultas usam "?" (PreparedStatement) para evitar SQL Injection.
 */
public class CadastroDAO {

    // Colunas que voltam para a tela (senha_hash fica de fora de propósito)
    private static final String COLUNAS = "id, nome, endereco, cep, numero, telefone, observacao, email, criado_em";

    /** Salva um cadastro. A senha já chega aqui como HASH. @return o id gerado */
    public int salvar(CadastroRequest c, String senhaHash) throws SQLException {
        String sql = "INSERT INTO cadastros "
                + "(nome, endereco, cep, numero, telefone, observacao, email, senha_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.nome().trim());
            ps.setString(2, c.endereco().trim());
            ps.setString(3, c.cep().trim());
            ps.setString(4, c.numero().trim());
            ps.setString(5, c.telefone().trim());
            ps.setString(6, c.observacao() == null ? "" : c.observacao().trim());
            ps.setString(7, c.email().trim().toLowerCase());
            ps.setString(8, senhaHash);
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : 0;
            }
        }
    }

    /** Lista todos os cadastros, do mais novo para o mais antigo. */
    public List<Cadastro> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cadastros ORDER BY id DESC";
        List<Cadastro> lista = new ArrayList<>();

        try (Connection conn = ConexaoDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    /** Busca um cadastro pelo ID. @return o cadastro ou null */
    public Cadastro buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cadastros WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    /** Busca um cadastro pelo e-mail. @return o cadastro ou null */
    public Cadastro buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cadastros WHERE email = ?";
        try (Connection conn = ConexaoDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    // Transforma a linha atual do ResultSet em um objeto Cadastro
    private Cadastro montar(ResultSet rs) throws SQLException {
        return new Cadastro(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("endereco"),
                rs.getString("cep"),
                rs.getString("numero"),
                rs.getString("telefone"),
                rs.getString("observacao"),
                rs.getString("email"),
                rs.getString("criado_em"));
    }

    /** @return o hash da senha do cliente, ou null se o e-mail não existir */
    public String buscarSenhaHashPorEmail(String email) throws SQLException {
        String sql = "SELECT senha_hash FROM cadastros WHERE email = ?";
        try (Connection conn = ConexaoDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("senha_hash") : null;
            }
        }
    }
}
