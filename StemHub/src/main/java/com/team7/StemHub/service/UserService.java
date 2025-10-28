package com.team7.StemHub.service;

import com.team7.StemHub.dao.DocumentRepo;
import com.team7.StemHub.dao.UserRepo;
import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
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

    public User getUserById(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public void likeDocument(UUID userId, UUID documentId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        if (!user.getFavoritesDocuments().contains(document)) {
            user.getFavoritesDocuments().add(document);
        } else {
            user.getFavoritesDocuments().remove(document);
        }
        userRepo.save(user);
    }

    public Set<User> searchUsers(String keyword){
        Set<User> result;
        List<User> byFullName = userRepo.findByFullNameContainingIgnoreCase(keyword);
        List<User> byUsername = userRepo.findByUsernameContainingIgnoreCase(keyword);
        result =  Set.copyOf(byFullName);
        result.addAll(byUsername);
        return result;
    }
}
