package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.request.DocumentRequest;
import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.facade.DocumentFacade;
import com.team7.StemHub.model.Comment;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.service.CommentService;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/document")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentFacade documentFacade;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        // Get authenticated user ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            // Query database to get User entity
            User user = userService.findByUsername(username);
            if (user != null) {
                userId = user.getId();
            }
        }
        // Provide both names for compatibility; template expects user_id
        model.addAttribute("user_id", userId);
        model.addAttribute("userId", userId);
        return "home/upload"; // => templates/home/upload.html
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleDocumentUpload(@ModelAttribute DocumentRequest documentRequest,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Ensure authorId is set from authenticated session if not provided
            if (documentRequest.getAuthorId() == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()
                        && authentication.getPrincipal() instanceof UserDetails userDetails) {
                    User user = userService.findByUsername(userDetails.getUsername());
                    if (user != null) {
                        documentRequest.setAuthorId(user.getId());
                    }
                }
            }

            // Basic validation
            if (documentRequest.getFile() == null || documentRequest.getFile().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn tệp tài liệu.");
                return "redirect:/document/upload";
            }

            documentFacade.uploadDocument(documentRequest);
            redirectAttributes.addFlashAttribute("success", "Tải tài liệu lên thành công!");
            return "redirect:/";
        } catch (Exception e) {
            log.error("Error uploading document: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải tài liệu lên: " + e.getMessage());
            return "redirect:/document/upload";
        }
    }

    @GetMapping("/detail/{documentId}")
    public String viewDocumentDetail(@PathVariable UUID documentId, Model model) {
        Document document = documentService.getDocumentById(documentId);
        int countFavorites = documentService.countFavorites(documentId);
        List<Document> relativeDocument = documentService.getDocumentsByCourse(document.getCourse());
        List<Comment> lastComment = commentService.getLastCommentByDocumentId(documentId);
        // current user (if logged in) for commenting
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userService.findByUsername(userDetails.getUsername());
            if (user != null) currentUserId = user.getId();
        }
        int favorites = countFavorites;
        DocumentResponse dto = new DocumentResponse(document, favorites);
        model.addAttribute("document", dto);
        model.addAttribute("relativeDocument", relativeDocument);
        model.addAttribute("lastComment", lastComment);
        model.addAttribute("currentUserId", currentUserId); // legacy support
        model.addAttribute("user_id", currentUserId); // align with upload.html naming
        return "home/detail";
    }
}
