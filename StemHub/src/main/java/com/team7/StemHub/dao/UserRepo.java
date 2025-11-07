package com.team7.StemHub.dao;

import com.team7.StemHub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Thêm import này
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.uploadFiles WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.uploadFiles WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.uploadFiles ORDER BY size(u.uploadFiles) DESC")
    List<User> findTop10OrderByDocumentNumberDesc();

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.uploadFiles WHERE lower(u.fullname) LIKE lower(concat('%', :fullname, '%'))")
    List<User> findByFullnameContainingIgnoreCase(@Param("fullname") String fullname);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.uploadFiles WHERE lower(u.username) LIKE lower(concat('%', :username, '%'))")
    List<User> findByUsernameContainingIgnoreCase(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favoritesDocuments WHERE u.id = :id")
    Optional<User> findByIdWithFavorites(@Param("id") UUID id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.uploadFiles WHERE u.id = :id")
    Optional<User> findByIdWithUploads(@Param("id") UUID id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.uploadFiles " +
            "LEFT JOIN FETCH u.favoritesDocuments fav " +
            "LEFT JOIN FETCH fav.author " +
            "LEFT JOIN FETCH fav.course " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithAllData(@Param("username") String username);
}