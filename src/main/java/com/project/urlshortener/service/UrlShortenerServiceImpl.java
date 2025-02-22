package com.project.urlshortener.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.urlshortener.entity.UrlMapping;
import com.project.urlshortener.exception.UrlNotFoundException;
import com.project.urlshortener.repository.UrlMappingRepository;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int SHORT_URL_LENGTH = 7;

	@Autowired
	private UrlMappingRepository urlMappingRepository;

	@Transactional
	public String shortenUrl(String originalUrl, LocalDateTime expirationTime) {
		String shortUrl = generateShortUrl();
		UrlMapping urlMapping = UrlMapping.builder().originalUrl(originalUrl).shortUrl(shortUrl)
				.createdAt(LocalDateTime.now()).expirationTime(expirationTime).build();
		urlMappingRepository.save(urlMapping);
		return shortUrl;
	}

	@Transactional(readOnly = true)
	public String getOriginalUrl(String shortUrl) {
		UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl)
				.orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortUrl));

		if (urlMapping.getExpirationTime() != null && urlMapping.getExpirationTime().isBefore(LocalDateTime.now())) {
			throw new UrlNotFoundException("Short URL has expired: " + shortUrl);
		}

		return urlMapping.getOriginalUrl();
	}

	private String generateShortUrl() {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
		for (int i = 0; i < SHORT_URL_LENGTH; i++) {
			sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}

}
