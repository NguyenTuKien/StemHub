package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.dto.response.UserResponse;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;

import static java.util.stream.Collectors.toSet;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final DocumentService documentService;

    @GetMapping("/profile")
    public String uploadDocument(@RequestParam UUID userId, @RequestParam (defaultValue = "1") int page, Model model) {
        User user = userService.getUserByIdWithUploadFile(userId);
        UserResponse userResponse = new UserResponse(user);
        int pageIndex = (page < 1) ? 0 : page - 1;
        Page<Document> documents = documentService.getAllUploadDocumentsByAuthor(user, pageIndex);
        Page<DocumentResponse> documentPage = documents.map(DocumentResponse::new);
        // Determine current authenticated user and liked documents for initial state
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = null;
        Set<UUID> likedIds = Set.of();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User currentUser = userService.findByUsernameWithAllData(userDetails.getUsername());
            if (currentUser != null) {
                currentUserId = currentUser.getId();
                if (currentUser.getFavoritesDocuments() != null) {
                    likedIds = currentUser.getFavoritesDocuments().stream()
                            .filter(Objects::nonNull)
                            .map(Document::getId)
                            .collect(toSet());
                }
            }
        }
        model.addAttribute("user", userResponse);
        model.addAttribute("documents", documentPage);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("likedIds", likedIds);
        return "home/profile";
    }

    @GetMapping("/favorites")
    public String favoriteDocuments(@RequestParam(defaultValue = "1") int page, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            currentUser = userService.findByUsernameWithAllData(username);
        }
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        UserResponse userResponse = new UserResponse(currentUser);
        Set<Document> favoriteDocuments = currentUser.getFavoritesDocuments();
        List<DocumentResponse> favoriteDocumentsDTO = favoriteDocuments.stream()
                .map(DocumentResponse::new)
                .sorted(Comparator.comparing(DocumentResponse::getCreatedAt).reversed())
                .toList();
        int pageIndex = (page < 1) ? 0 : page - 1;
        Pageable pageRequest = PageRequest.of(pageIndex, 6);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), favoriteDocumentsDTO.size());
        List<DocumentResponse> pageContent;
        if (start > favoriteDocumentsDTO.size()) {
            pageContent = List.of();
        } else {
            pageContent = favoriteDocumentsDTO.subList(start, end);
        }
        Page<DocumentResponse> favoriteDocumentsPage = new PageImpl<>(
                pageContent,
                pageRequest,
                favoriteDocumentsDTO.size()
        );
        model.addAttribute("user", userResponse);
        model.addAttribute("documents", favoriteDocumentsPage);

        return "home/favorite";
    }
}
