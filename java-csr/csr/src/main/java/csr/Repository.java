package com.crud;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    private static List<Personagem> lista = new ArrayList<>();

    public void adicionar(Personagem personagem) {
        lista.add(personagem);
    }

    public List<Personagem> getAll() {
        return lista;
    }

}