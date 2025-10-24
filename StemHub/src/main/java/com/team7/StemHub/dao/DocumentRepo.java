package com.team7.StemHub.dao;

import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepo extends JpaRepository<Document, UUID> {
    List<Document> findAllByOrderByCreateAtDesc();

    @Query("SELECT d FROM Document d ORDER BY SIZE(d.favoredByUsers) DESC")
    List<Document> findAllByOrderByFavoriteCountDesc();

    Integer countFavoritesById(UUID documentId);

    List<Document> findByCourse(Course course);
}
