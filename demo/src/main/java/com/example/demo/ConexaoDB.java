package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Dados de conexão com o MySQL. Se algo mudar (banco, senha), altere SÓ aqui. */
public class ConexaoDB {

    private static final String URL =
        "jdbc:mysql://localhost:3306/meu_db?useSSL=false&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}