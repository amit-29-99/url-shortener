package com.example.ticketManagement.service;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ticketManagement.entity.Ticket;
import com.example.ticketManagement.repository.TicketRepository;

@Service
public class TicketService {
	@Autowired
	private TicketRepository ticketRepository;
	private static final String UPLOAD_DIR = "D:\\uploads\\";

	public Ticket createTicket(Ticket ticket) {
		ticket.setCreatedAt(new Date());
		return ticketRepository.save(ticket);
	}

	public List<Ticket> searchByStatus(String status) {
		return ticketRepository.findByStatus(status);
	}

	public Optional<Ticket> findById(Long id) {
		return ticketRepository.findById(id);
	}

	public Ticket updateStatus(Long id, String status) {
		Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
		ticket.setStatus(status);
		return ticketRepository.save(ticket);
	}

	public Ticket addComment(Long id, String comment) {
		Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
		ticket.setComments(comment == null || comment.isEmpty() ? ticket.getComments() : comment);
		return ticketRepository.save(ticket);
	}

	public ResponseEntity<String> uploadDocument(Long ticketId, MultipartFile file) {
		try {
			Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
			if (!optionalTicket.isPresent()) {
				return ResponseEntity.badRequest().body("Ticket not found");
			}

			Ticket ticket = optionalTicket.get();

			// Ensure uploads directory exists
			Files.createDirectories(Paths.get(UPLOAD_DIR));

			// Generate unique filename
			String filePath = UPLOAD_DIR + UUID.randomUUID() + "_" + file.getOriginalFilename();

			// Save file to disk
			file.transferTo(new File(filePath));

			// Update database with file path
			ticket.setDocumentUrl(filePath);
			ticketRepository.save(ticket);

			return ResponseEntity.ok("File uploaded successfully: " + filePath);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
		}
	}

	public ResponseEntity<String> deleteDocument(Long ticketId) {
		try {
			Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
			if (!optionalTicket.isPresent()) {
				return ResponseEntity.badRequest().body("Ticket not found");
			}

			Ticket ticket = optionalTicket.get();
			String filePath = ticket.getDocumentUrl();

			if (filePath != null) {
				File file = new File(filePath);

				// Delete the actual file
				if (file.exists() && file.delete()) {
					ticket.setDocumentUrl(null);
					ticketRepository.save(ticket);
					return ResponseEntity.ok("File deleted successfully");
				} else {
					return ResponseEntity.notFound().build();
				}
			} else {
				return ResponseEntity.badRequest().body("No document associated with this ticket");
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("File deletion failed: " + e.getMessage());
		}
	}

	public ResponseEntity<Resource> getDocument(Long ticketId) {
		try {
			Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
			if (!optionalTicket.isPresent()) {
				return ResponseEntity.badRequest().build();
			}

			Ticket ticket = optionalTicket.get();
			String filePath = ticket.getDocumentUrl();

			if (filePath == null) {
				return ResponseEntity.badRequest().build();
			}

			Path path = Paths.get(filePath);
			Resource resource = new UrlResource(path.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				return ResponseEntity.notFound().build();
			}

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + path.getFileName().toString() + "\"").body(resource);
		} catch (MalformedURLException e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}
