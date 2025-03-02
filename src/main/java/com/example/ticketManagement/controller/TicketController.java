package com.example.ticketManagement.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ticketManagement.entity.Ticket;
import com.example.ticketManagement.service.TicketService;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/create")
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    @GetMapping("/search")
    public List<Ticket> search(@RequestParam String status) {
        return ticketService.searchByStatus(status);
    }

    @PutMapping("/update-status/{id}")
    public Ticket updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ticketService.updateStatus(id, status);
    }

    @PostMapping("/add-comment/{id}")
    public Ticket addComment(@PathVariable Long id, @RequestParam String comment) {
        return ticketService.addComment(id, comment);
    }

    @PostMapping("/upload-document/{ticketId}")
    public ResponseEntity<String> uploadDocument(@PathVariable Long ticketId, @RequestParam("file") MultipartFile file) {
        return ticketService.uploadDocument(ticketId, file);
    }

    @DeleteMapping("/delete-document/{ticketId}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long ticketId) {
        return ticketService.deleteDocument(ticketId);
    }
    
    @GetMapping("/get-document/{ticketId}")
    public ResponseEntity<Resource> getDocument(@PathVariable Long ticketId) {
        return ticketService.getDocument(ticketId);
    }
}
