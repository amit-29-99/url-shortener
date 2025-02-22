package com.project.urlshortener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.urlshortener.service.UrlShortenerService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/url")
public class UrlShortenerController {

	@Autowired
    private  UrlShortenerService urlShortenerService;

  

    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestParam String originalUrl,
                                                          @RequestParam(required = false) LocalDateTime expirationTime) {
        String shortUrl = urlShortenerService.shortenUrl(originalUrl, expirationTime);
        return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortUrl) {
        String originalUrl = urlShortenerService.getOriginalUrl(shortUrl);
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }
}

