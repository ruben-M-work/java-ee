/*CRUD
Criar uma classe chamada Personagem. Esta classe deve ter os seguintes campos (com getters, setters e construtores):
        private String nome;
        private String especie;
        private String comidaFavorita;
 
 
Criar um endpoint (“/personagem”) que permita fazer operações CRUD:
        Usar o método POST para criar uma nova personagem.
        Usar o método GET para ler todas as personagens.
*/
 
package com.crud;
 
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
 
@Path("/personagem")
public class App {
    
    private static List<Personagem> lista = new ArrayList<>();

    @POST
    public void personagem(Personagem personagem) {
        lista.add(personagem);
    }

    @GET
    public List<Personagem> getAll() {
        return lista;
    }
 
}