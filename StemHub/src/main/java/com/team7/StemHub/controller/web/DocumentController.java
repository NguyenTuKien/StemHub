package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.request.DocumentRequest;
import com.team7.StemHub.dto.response.CommentResponse;
import com.team7.StemHub.dto.response.CourseResponse;
import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.facade.R2StorageFacade;
import com.team7.StemHub.model.Comment;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.model.enums.Category;
import com.team7.StemHub.service.CommentService;
import com.team7.StemHub.service.CourseService;
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
    private final R2StorageFacade r2StorageFacade;
    private final UserService userService;
    private final CommentService commentService;
    private final CourseService courseService;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username);
            if (user != null) {
                userId = user.getId();
            }
        }
        List<CourseResponse> courses = courseService.getAllCourses().stream()
                .map(CourseResponse::new)
                .toList();
    // Provide enum values so the form can submit the correct Category name while showing display names
    model.addAttribute("categories", Category.values());
        model.addAttribute("userId", userId);
        model.addAttribute("courses", courses);
        return "home/upload"; // => templates/home/upload.html
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleDocumentUpload(@ModelAttribute DocumentRequest documentRequest,
                                       RedirectAttributes redirectAttributes) {
        try {
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
            if (documentRequest.getFile() == null || documentRequest.getFile().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn tệp tài liệu.");
                return "redirect:/document/upload";
            }
            r2StorageFacade.uploadDocument(documentRequest);
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
        Long countFavorites = documentService.countFavorites(documentId);
        List<Document> relativeDocument = documentService.getDocumentsByCourse(document.getCourse());
        List<Comment> lastComment = commentService.getLastCommentByDocumentId(documentId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = null;
        boolean liked = false;
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser != null) {
                currentUserId = currentUser.getId();
                // determine like state using ID comparison
                try {
                    liked = currentUser.getFavoritesDocuments() != null &&
                            currentUser.getFavoritesDocuments().stream()
                                    .anyMatch(d -> d != null && d.getId() != null && d.getId().equals(documentId));
                } catch (Exception ex) {
                    liked = false;
                }
            }
        }
        DocumentResponse documentDTO = new DocumentResponse(document, countFavorites);
        List <DocumentResponse> relativeDocumentDTO = relativeDocument.stream().map(DocumentResponse::new).toList();
        List < CommentResponse> lastCommentDTO = lastComment.stream().map(CommentResponse::new).toList();
        model.addAttribute("document", documentDTO);
        model.addAttribute("relativeDocument", relativeDocumentDTO);
        model.addAttribute("lastComment", lastCommentDTO);
        model.addAttribute("currentUserId", currentUserId); // legacy support
        model.addAttribute("liked", liked);
        return "home/detail";
    }
}
