package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.dto.response.UserResponse;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final DocumentService documentService;

    @GetMapping("/profile")
    public String uploadDocument(@RequestParam UUID userId, Model model) {
        User user = userService.getUserById(userId);
        UserResponse userResponse = new UserResponse(user);
        List<Document> documents = documentService.getAllUploadDocumentsByAuthor(user);
        List<DocumentResponse> documentDTO = documents.stream().map(DocumentResponse::new).toList();
        model.addAttribute("user", userResponse);
        model.addAttribute("documents", documentDTO);
        return "home/profile";
    }

    @GetMapping("/favorites")
    public String favoriteDocuments(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            // Query database to get User entity
            currentUser = userService.findByUsername(username);
        }
        
        Set<Document> favoriteDocuments = currentUser.getFavoritesDocuments();
        UserResponse userResponse = new UserResponse(currentUser);
        Set<DocumentResponse> favoriteDocumentsDTO = favoriteDocuments.stream()
                .map(DocumentResponse::new)
                .collect(toSet());
        model.addAttribute("user", userResponse);
        model.addAttribute("documents", favoriteDocumentsDTO);
        return "home/favorite";
    }
}

