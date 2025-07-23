package br.com.alura.screenmatch.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ConsultaTradutor {

    private static final String URL = "https://api.mymemory.translated.net/get";

    public String obterTraducao(String texto) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(URL)
                    .queryParam("q", URLEncoder.encode(texto, StandardCharsets.UTF_8))
                    .queryParam("langpair", "en|pt")
                    .build()
                    .toUri();

            Map response = restTemplate.getForObject(uri, Map.class);

            if (response != null && response.containsKey("responseData")) {
                Map responseData = (Map) response.get("responseData");
                return responseData.get("translatedText").toString();
            } else {
                return "[tradução indisponível]";
            }

        } catch (Exception e) {
            System.out.println("Erro ao traduzir com MyMemory: " + e.getMessage());
            return "[tradução indisponível]";
        }
    }
}
