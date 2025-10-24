package com.team7.StemHub.dto.response;

import com.team7.StemHub.model.Document;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DocumentResponse {
    UUID documentId;
    String title;
    String description;
    String authorName;
    String category;
    String courseName;
    String fileUrl;
    String thumbnailUrl;
    LocalDateTime createdAt;
    Integer downloadCount;
    Integer favoriteCount;

    public DocumentResponse(Document document, int favoriteCount) {
        this.documentId = document != null ? document.getId() : null;
        this.title = document != null ? document.getTitle() : null;
        this.description = document != null ? document.getDescription() : null;
        if (document != null && document.getAuthor() != null) {
            // prefer fullname if available
            this.authorName = document.getAuthor().getFullname() != null ? document.getAuthor().getFullname() : document.getAuthor().getUsername();
        } else {
            this.authorName = "Anonymous";
        }
        this.category = document != null && document.getCategory() != null ? document.getCategory().toString() : null;
        this.courseName = (document != null && document.getCourse() != null) ? document.getCourse().getCourseName() : null;
        this.fileUrl = document != null ? document.getFilePath() : null;
        this.thumbnailUrl = document != null ? document.getThumbnailPath() : null;
        this.createdAt = document != null ? document.getCreateAt() : null;
        this.downloadCount = document != null ? document.getDownloadCount() : null;
        this.favoriteCount = favoriteCount;
    }
}
