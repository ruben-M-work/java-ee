import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LogicCount {
    public static void main(String [] args) throws Exception {
        int vivos = 0;
        int mortos = 0;

        HttpClient client = HttpClient.newHttpClient();

        for (int i = 1; i < 21; i++) {

    

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://rickandmortyapi.com/api/character/" +i))
            .GET()
            .build();
        System.out.println(i);

            
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString() );

        String json=response.body(); {
            if (json.contains("\"status\":\"Alive\"")) {
                vivos += 1;
            } else if (json.contains("\"status\":\"Dead\"")) {
                mortos += 1;
            }
        
        }


        }
    System.out.println("CENSO: detetados " + vivos + " personagens VIVOS e " + mortos + " personagens MORTOS nos primeiros 20 registos.");


    }

}