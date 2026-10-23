package com.example.demo;

/** Os dados que a Tela de Cadastro envia ao clicar em "Salvar" (inclui a senha pura). */
public record CadastroRequest(
        String nome,
        String endereco,
        String cep,
        String numero,
        String telefone,
        String observacao,
        String email,
        String senha) {
}
