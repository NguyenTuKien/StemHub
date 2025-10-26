package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.request.CommentRequest;
import com.team7.StemHub.service.CommentService;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class InteractionController {
    private final CommentService commentService;
    private final UserService userService;
    private final DocumentService documentService;

    @PostMapping("/comment")
    public String postComment(@RequestBody CommentRequest commentRequest) {
        commentService.addComment(commentRequest);
        return "redirect:/";
    }

    @PostMapping("/like")
    public String likeDocument(@RequestParam UUID userId, @RequestParam UUID documentId) {
        userService.likeDocument(userId, documentId);
        return "redirect:/";
    }

    @PostMapping("/download")
    public void  downloadDocument(@RequestParam UUID documentId) {
        documentService.downloadDocument(documentId);
    }
}
