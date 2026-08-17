package com.crud;

public class Personagem {

        private String nome;
        private String especie;
        private String comidaFavorita;
 
        public Personagem(String nome, String especie, String comidaFavorita) {
            this.nome = nome;
            this.especie = especie;
            this.comidaFavorita = comidaFavorita;
        }
 
        public Personagem() {}

        // getters e setters
        public String getNome() {
            return nome;
        }
 
        public void setNome(String nome) {
            this.nome = nome;
        }
 
        public String getEspecie() {
            return especie;
        }
 
        public void setEspecie(String especie) {
            this.especie = especie;
        }
 
        public String getComidaFavorita() {
            return comidaFavorita;
        }
 
        public void setComidaFavorita(String comidaFavorita) {
            this.comidaFavorita = comidaFavorita;
        }
 
}
