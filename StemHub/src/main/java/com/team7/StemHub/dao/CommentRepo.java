package com.team7.StemHub.dao;

import com.team7.StemHub.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepo extends JpaRepository<Comment, UUID> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user JOIN FETCH c.document WHERE c.document.id = :documentId ORDER BY c.createAt DESC")
    List<Comment> findAllByDocumentIdOrderByCreateAtDesc(@Param("documentId") UUID documentId);
}