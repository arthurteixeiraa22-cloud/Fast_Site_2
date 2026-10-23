package com.example.demo;

import org.mindrot.jbcrypt.BCrypt;

/** Classe responsável SOMENTE pela criptografia de senhas (BCrypt). */
public class SenhaUtil {

    public static String criptografar(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public static boolean conferir(String senhaDigitada, String hashSalvo) {
        if (senhaDigitada == null || hashSalvo == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(senhaDigitada, hashSalvo);
        } catch (IllegalArgumentException e) {
            // A coluna senha_hash não tem um hash BCrypt válido (ex.: senha pura)
            throw new IllegalStateException("A senha_hash no banco não é um hash BCrypt válido.");
        }
    }
}