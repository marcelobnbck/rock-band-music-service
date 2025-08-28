package org.example.music.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LyricsClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public LyricsClient(RestTemplate restTemplate,
                        @Value("${lyrics.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public String fetchLyrics(String artist, String title) {
        String url = String.format("%s/%s/%s", apiUrl, artist, title);
        LyricsResponse response = restTemplate.getForObject(url, LyricsResponse.class);
        return response != null ? response.getLyrics() : "No lyrics found";
    }

    public static record LyricsResponse(String lyrics) {
        public String getLyrics() {
            return lyrics;
        }
    }

}
