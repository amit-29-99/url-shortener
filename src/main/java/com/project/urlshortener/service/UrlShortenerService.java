package com.project.urlshortener.service;

import java.time.LocalDateTime;

public interface UrlShortenerService {

	public String shortenUrl(String originalUrl, LocalDateTime expirationTime);
	 public String getOriginalUrl(String shortUrl);
}
