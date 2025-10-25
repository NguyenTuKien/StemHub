package com.team7.StemHub.dao;

import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepo extends JpaRepository<Document, UUID> {
    List<Document> findTop20ByOrderByCreateAtDesc();

    List<Document> findTop10ByOrderByDownloadCountDesc();

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u JOIN u.favoritesDocuments d WHERE d.id = :documentId")
    Long countFavoritesById(@Param("documentId") UUID documentId);

    List<Document> findByCourse(Course course);
}
