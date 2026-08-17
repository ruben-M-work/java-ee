/*

CSR
 
Reestruturem o vosso código para respeitar o padrão de desenho Controller-Service-Repository.
- Controller só deve ser responsável por receber os pedidos HTTP e reencaminhar para o Service.
- Repository só deve ter código relacionado com a persistência de dados.
- Service deve estabelecer a comunicação entre os outros componentes e qualquer lógica de processamento que seja necessária.
 
Para o Service ter alguma lógica pela qual é responsável, façam com que qualquer criação de personagem que não inclua a "comidaFavorita" atribua automaticamente um valor a esse campo (uma comida ao vosso critério). 

*/

package com.crud;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/personagem")
public class App {

    private Service service = new Service();

    @POST
    public void personagem(Personagem personagem) {
        service.criar(personagem);
    }

    @GET
    public List<Personagem> getAll() {
        return service.getAll();
    }

}