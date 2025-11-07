package com.team7.StemHub.service;

import com.team7.StemHub.dao.DocumentRepo;
import com.team7.StemHub.dao.UserRepo;
import com.team7.StemHub.exception.NotFoundException;
import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final DocumentRepo documentRepo;

    public List<User> getTop10UsersOrderByDocument() {
        return userRepo.findTop10OrderByDocumentNumberDesc();
    }

    public User getUserByIdWithUploadFile(UUID id){
        return userRepo.findByIdWithUploads(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public User getUserById(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Transactional
    public void likeDocument(UUID userId, UUID documentId) {
        User user = userRepo.findByIdWithFavorites(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        Set<Document> favorites = user.getFavoritesDocuments();
        if (favorites.contains(document)) {
            favorites.remove(document);
        } else {
            favorites.add(document);
        }
    }

    public Set<User> searchUsers(String keyword){
        Set<User> result = new java.util.HashSet<>();
        List<User> byFullName = userRepo.findByFullnameContainingIgnoreCase(keyword);
        List<User> byUsername = userRepo.findByUsernameContainingIgnoreCase(keyword);
        result.addAll(byFullName);
        result.addAll(byUsername);
        return result;
    }

    public User findByUsernameWithAllData(String username) {
        return userRepo.findByUsernameWithAllData(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }
}
