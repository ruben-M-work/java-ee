package com.crud;

import java.util.List;

public class Service {

    private Repository repository = new Repository();

    public void criar(Personagem personagem) {

        if (personagem.getComidaFavorita() == null ||
            personagem.getComidaFavorita().isEmpty()) {

            personagem.setComidaFavorita("Pizza");
        }

        repository.adicionar(personagem);
    }

    public List<Personagem> getAll() {
        return repository.getAll();
    }

}