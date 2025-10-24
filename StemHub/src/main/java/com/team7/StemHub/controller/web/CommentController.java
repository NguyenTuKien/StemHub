package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.request.CommentRequest;
import com.team7.StemHub.model.Comment;
import com.team7.StemHub.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("")
    public String postComment(@RequestBody CommentRequest commentRequest) {
        commentService.addComment(commentRequest);
        return "redirect:/";
    }
}
