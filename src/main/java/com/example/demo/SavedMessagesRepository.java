package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SavedMessagesRepository extends JpaRepository<SavedMessages, Long> {

    // 1. Get all messages for a user where received is false
    List<SavedMessages> findByReceivernameAndReceivedFalse(String receivername);

    // 2. Delete all messages that are marked as received
    @Transactional
    @Modifying
    @Query("DELETE FROM SavedMessages m WHERE m.received = true")
    void deleteAllReceivedMessages();

    // 3. Mark a message as received by its ID
    @Transactional
    @Modifying
    @Query("UPDATE SavedMessages m SET m.received = true WHERE m.id = :id")
    void markMessageAsReceived(Long id);

}
