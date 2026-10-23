package com.example.demo;

/**
 * Classe TEMPORÁRIA: gera o comando SQL para criar o usuário que faz o PRIMEIRO login.
 *
 * Como usar:
 *   1) Troque EMAIL e SENHA abaixo.
 *   2) Rode este main.
 *   3) Copie o INSERT que aparecer no console e execute no MySQL.
 */
public class GerarHash {

    public static void main(String[] args) {
        String email = "adm@gmail.com";
        String senha = "minhaSenha123";

        String hash = SenhaUtil.criptografar(senha);

        System.out.println("USE meu_db;");
        System.out.println("INSERT INTO usuarios (email, senha_hash)");
        System.out.println("VALUES ('" + email.trim().toLowerCase() + "', '" + hash + "');");
    }
}
