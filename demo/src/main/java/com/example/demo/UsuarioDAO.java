package com.example.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Conversa com a tabela "usuarios" (quem pode fazer login). */
public class UsuarioDAO {

    /** @return o hash salvo, ou null se o email não existir */
    public String buscarHashPorEmail(String email) throws SQLException {
        String sql = "SELECT senha_hash FROM usuarios WHERE email = ?";

        try (Connection conn = ConexaoDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email); // o "?" evita SQL Injection

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("senha_hash") : null;
            }
        }

    }
}