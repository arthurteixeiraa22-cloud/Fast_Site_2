package com.example.demo;

/**
 * Representa UM cadastro. O Spring converte isso para JSON automaticamente.
 *
 * Repare que NÃO existe campo de senha aqui: a senha só entra no cadastro
 * (CadastroRequest) e vai direto para o hash. Ela nunca volta para a tela.
 */
public record Cadastro(
        int id,
        String nome,
        String endereco,
        String cep,
        String numero,
        String telefone,
        String observacao,
        String email,
        String criadoEm) {
}
