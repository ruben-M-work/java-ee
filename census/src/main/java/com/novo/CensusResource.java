package com.novo;
 
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
 
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
 
@Path("/census")
public class CensusResource {
 
        @GET
        @Produces({
                MediaType.TEXT_HTML,
                MediaType.APPLICATION_JSON
        })
        public Response executarCenso(
                @QueryParam("offset") String offsetParam,
                @QueryParam("limit") String limitParam,
                @QueryParam("showAlerts") String showAlertsParam) {
 
        int offset = 1;
        int limit = 20;
        boolean showAlerts = true;
 
        if (offsetParam != null && !offsetParam.isBlank()) {
                try {
                offset = Integer.parseInt(offsetParam);
                } catch (NumberFormatException e) {
                return enviarErro(
                        "O parâmetro 'offset' deve ser um número inteiro.");
                }
        }
 
        if (offset < 1) {
                return enviarErro(
                        "O parâmetro 'offset' deve ser um número inteiro maior ou igual a 1.");
        }
 
        if (limitParam != null && !limitParam.isBlank()) {
                try {
                limit = Integer.parseInt(limitParam);
                } catch (NumberFormatException e) {
                return enviarErro(
                        "O parâmetro 'limit' deve ser um número inteiro entre 1 e 50.");
                }
        }
 
        if (limit < 1 || limit > 50) {
                return enviarErro(
                        "O parâmetro 'limit' deve ser um número inteiro entre 1 e 50.");
        }
 
        if (showAlertsParam != null && !showAlertsParam.isBlank()) {
 
                if (showAlertsParam.equalsIgnoreCase("true")) {
                showAlerts = true;
 
                } else if (showAlertsParam.equalsIgnoreCase("false")) {
                showAlerts = false;
 
                } else {
                return enviarErro(
                        "O parâmetro 'showAlerts' deve ser 'true' ou 'false'.");
                }
        }
 
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
 
        int vivos = 0;
        int mortos = 0;
        int desconhecidos = 0;
 
        StringBuilder html = new StringBuilder();
 
        html.append("<!DOCTYPE html>");
        html.append("<html lang='pt'>");
 
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Censo Rick & Morty</title>");
        html.append("</head>");
 
        html.append("<body>");
        html.append("<h1>CENSO RICK & MORTY</h1>");
        html.append("<hr>");
 
        try {
 
                for (int i = offset; i < offset + limit; i++) {
 
                String url =
                        "https://rickandmortyapi.com/api/character/" + i;
 
                HttpRequest pedidoPersonagem =
                        HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .GET()
                                .build();
 
                HttpResponse<String> respostaPersonagem =
                        client.send(
                                pedidoPersonagem,
                                HttpResponse.BodyHandlers.ofString());
 
                if (respostaPersonagem.statusCode() != 200) {
                        continue;
                }
 
                JsonNode personagem =
                        mapper.readTree(respostaPersonagem.body());
 
                String status =
                        personagem.get("status").asText();
 
                String species =
                        personagem.get("species").asText();
 
                if (status.equals("Alive")) {
                        vivos++;
 
                } else if (status.equals("Dead")) {
                        mortos++;
 
                } else {
                        desconhecidos++;
                }
 
                if (showAlerts
                        && species.equals("Alien")
                        && status.equals("Dead")) {
 
                        html.append("<h3 style='color:red'>");
 
                        html.append(
                                        "[PERIGO] Um Alien foi encontrado morto com o ID ")
                                .append(i)
                                .append("!");
 
                        html.append("</h3>");
 
                        JsonNode episodios =
                                personagem.get("episode");
 
                        if (episodios != null && episodios.size() > 0) {
 
                        String ultimoEpisodio =
                                episodios
                                        .get(episodios.size() - 1)
                                        .asText();
 
                        HttpRequest pedidoEpisodio =
                                HttpRequest.newBuilder()
                                        .uri(URI.create(ultimoEpisodio))
                                        .GET()
                                        .build();
 
                        HttpResponse<String> respostaEpisodio =
                                client.send(
                                        pedidoEpisodio,
                                        HttpResponse.BodyHandlers.ofString());
 
                        if (respostaEpisodio.statusCode() == 200) {
 
                                JsonNode episodio =
                                        mapper.readTree(
                                                respostaEpisodio.body());
 
                                String nomeEpisodio =
                                        episodio.get("name").asText();
 
                                html.append("<p>");
 
                                html.append(
                                        "<b>[ALERTA FORENSE]</b> ");
 
                                html.append(
                                                "O último registo do alien morto foi no episódio: <i>")
                                        .append(nomeEpisodio)
                                        .append("</i>.");
 
                                html.append("</p>");
                        }
                        }
 
                        html.append("<hr>");
                }
                }
 
                html.append("<h2>Relatório Final</h2>");
 
                html.append("<p><b>Vivos:</b> ")
                        .append(vivos)
                        .append("</p>");
 
                html.append("<p><b>Mortos:</b> ")
                        .append(mortos)
                        .append("</p>");
 
                html.append("<p><b>Desconhecidos:</b> ")
                        .append(desconhecidos)
                        .append("</p>");
 
                html.append("</body>");
                html.append("</html>");
 
                escreverLog(
                        offset,
                        limit,
                        showAlerts,
                        vivos,
                        mortos,
                        desconhecidos);
 
                return Response
                        .ok(html.toString())
                        .type(MediaType.TEXT_HTML + ";charset=UTF-8")
                        .build();
 
        } catch (InterruptedException e) {
 
                Thread.currentThread().interrupt();
 
                return enviarErroInterno(
                        "O pedido HTTP foi interrompido.");
 
        } catch (IOException e) {
 
                return enviarErroInterno(
                        "Ocorreu um erro ao executar o censo.");
        }
        }
 
        private void escreverLog(
                int offset,
                int limit,
                boolean showAlerts,
                int vivos,
                int mortos,
                int desconhecidos)
                throws IOException {
 
        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS");
 
        String timestamp =
                LocalDateTime.now().format(formato);
 
        String linha =
                "["
                        + timestamp
                        + "] Endpoint /api/census executado com sucesso"
                        + " | offset="
                        + offset
                        + " | limit="
                        + limit
                        + " | showAlerts="
                        + showAlerts
                        + " | vivos="
                        + vivos
                        + " | mortos="
                        + mortos
                        + " | desconhecidos="
                        + desconhecidos
                        + System.lineSeparator();
 
        java.nio.file.Path caminho =
                java.nio.file.Path.of(
                        "..",
                        "..",
                        "citadela_audit.log")
                        .normalize();
 
        Files.writeString(
                caminho,
                linha,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        }
 
        private Response enviarErro(String mensagem) {
 
        String json = """
                {
                "status": 400,
                "error": "Bad Request",
                "message": "%s"
                }
                """.formatted(mensagem);
 
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                .entity(json)
                .build();
        }
 
        private Response enviarErroInterno(String mensagem) {
 
        String json = """
                {
                "status": 500,
                "error": "Internal Server Error",
                "message": "%s"
                }
                """.formatted(mensagem);
 
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                .entity(json)
                .build();
        }
}
 