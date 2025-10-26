package com.team7.StemHub.dto.response;

import com.team7.StemHub.model.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.username = comment.getUser().getUsername();
        this.title = comment.getDocument().getTitle();
        this.content = comment.getContent();
        this.createdAt = comment.getCreateAt();
    }
}
