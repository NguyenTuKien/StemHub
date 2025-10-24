package com.team7.StemHub.service;

import com.team7.StemHub.dao.DocumentRepo;
import com.team7.StemHub.dao.UserRepo;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final DocumentRepo documentRepo;

    public List<User> getAllUsersOrderByDocument() {
        return userRepo.findAllOrderByDocumentNumberDesc().stream().limit(10).toList();
    }

    public User getUserById(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // Giả sử bạn đã @Autowired DocumentRepository documentRepo;

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
}
