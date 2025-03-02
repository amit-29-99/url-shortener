package com.example.ticketManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketManagement.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>  {

	 List<Ticket> findByStatus(String status);
}
