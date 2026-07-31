package exemple;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

 
@Path("/census")
public class Ex5Path {
 
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String doGet(
        
            @DefaultValue("1")
            @QueryParam("offset")
            int offset,
            
            @DefaultValue("20")
            @QueryParam("limit")
            int limit,

            @DefaultValue("true")
            @QueryParam("showAlerts")
            boolean showAlerts)



            throws IOException {

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        int vivos = 0;
        int mortos = 0;

        StringBuilder html = new StringBuilder();

        try {
            for (int i = offset; i < offset + limit; i++) {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://rickandmortyapi.com/api/character/" + i))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode jsonNode = mapper.readTree(response.body());

                String status = jsonNode.get("status").asText();
                String url = jsonNode.get("episode").get(0).asText();
                String species = jsonNode.get("species").asText();

                if (status.equals("Dead")) {

                    mortos++;

                    if (species.equals("Alien")) {

                        HttpRequest request_name = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .GET()
                                .build();

                        HttpResponse<String> response_name = client.send(request_name,
                                HttpResponse.BodyHandlers.ofString());

                        JsonNode jsonNode2 = mapper.readTree(response_name.body());

                        String name = jsonNode2.get("name").asText();

                        if (showAlerts) {
                            html.append("[ALERTA FORENSE] O último registo do alien morto foi no episódio: ")
                                    .append(name)
                                    .append(".<br>");
                        }
                    }

                } else if (status.equals("Alive")) {
                    vivos++;
                }
            }

            html.append("Vivos: ").append(vivos).append("<br>");
            html.append("Mortos: ").append(mortos).append("<br>");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Pedido interrompido", e);
        }

        return html.toString();
    }

}



