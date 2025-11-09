package com.team7.StemHub.dao;

import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.model.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // <-- Đảm bảo bạn import
import java.util.UUID;

@Repository
public interface DocumentRepo extends JpaRepository<Document, UUID> {

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course WHERE d.id = :id")
    Optional<Document> findByIdWithAuthorAndCourse(@Param("id") UUID id);

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course ORDER BY d.createAt DESC")
    List<Document> findTop20ByOrderByCreateAtDesc();

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course ORDER BY d.downloadCount DESC")
    List<Document> findTop10ByOrderByDownloadCountDesc();

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u JOIN u.favoritesDocuments d WHERE d.id = :documentId")
    Long countFavoritesById(@Param("documentId") UUID documentId);

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course WHERE d.course = :course")
    List<Document> findByCourse(@Param("course") Course course);

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course WHERE d.author = :author")
    List<Document> findAllByAuthor(@Param("author") User user);

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course WHERE lower(d.title) LIKE lower(concat('%', :title, '%'))")
    List<Document> findByTitleContainingIgnoreCase(@Param("title") String title);

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course WHERE lower(d.description) LIKE lower(concat('%', :description, '%'))")
    List<Document> findByDescriptionContainingIgnoreCase(@Param("description") String description);

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course WHERE d.category = :category ORDER BY d.downloadCount DESC")
    List<Document> findTop15ByCategoryOrderByDownloadCountDesc(@Param("category") Category category);

    @Query("SELECT d FROM Document d JOIN FETCH d.author JOIN FETCH d.course WHERE d.category = :category ORDER BY d.createAt DESC")
    List<Document> findByCategoryOrderByCreateAtDesc(@Param("category") Category category);
}