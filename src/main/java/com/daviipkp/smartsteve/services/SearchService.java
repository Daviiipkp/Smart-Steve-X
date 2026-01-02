package com.daviipkp.smartsteve.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class SearchService {

    @Value("${hcsearch.api.key}")
    private String API_KEY;
    private static final String BASE_URL = "https://search.hackclub.com/res/v1/web/search";

    private final RestClient restClient;

    public SearchService() {
        this.restClient = RestClient.create();
    }

    public String searchAndSummarize(String query) {
        try {
            System.out.println(">>> Buscando na internet: " + query);
            BraveResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("search.hackclub.com")
                            .path("/res/v1/web/search")
                            .queryParam("q", query)
                            .queryParam("count", 5)
                            .build())
                    .header("Authorization", "Bearer " + API_KEY)
                    .retrieve()
                    .body(BraveResponse.class);

            if (response != null && response.web() != null && response.web().results() != null) {
                return formatResultsForLLM(response.web().results());
            }

            return "Sem resultados na internet.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao acessar a internet: " + e.getMessage();
        }
    }

    private String formatResultsForLLM(List<SearchResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("CONTEXTO DA INTERNET (Informação Atualizada):\n");

        for (SearchResult r : results) {
            sb.append("- [").append(r.title()).append("]\n");
            sb.append("  Resumo: ").append(r.description()).append("\n");
            // sb.append("  Link: ").append(r.url()).append("\n"); // Opcional, o LLM não clica em links
            sb.append("\n");
        }
        return sb.toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record BraveResponse(WebSearch web) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record WebSearch(List<SearchResult> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SearchResult(String title, String url, String description) {}


}
