package com.team7.StemHub.service;

import com.team7.StemHub.dao.DocumentRepo;
import com.team7.StemHub.exception.NotFoundException;
import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepo documentRepo;

    public Long countFavorites(UUID documentId) {
        return documentRepo.countFavoritesById(documentId);
    }

    public Document saveDocument(Document document) {
        return documentRepo.save(document);
    }

    public List<Document> getTopDocument() {
        return documentRepo.findTop10ByOrderByDownloadCountDesc();
    }

    public List<Document> getNewestDocuments() {
        return documentRepo.findTop20ByOrderByCreateAtDesc();
    }

    public Document getDocumentById(UUID id) {
        return documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    public List<Document> getDocumentsByCourse(Course course) {
        return documentRepo.findByCourse(course);
    }

    public void downloadDocument(UUID documentId) {
        Document document = getDocumentById(documentId);
        document.setDownloadCount(document.getDownloadCount() + 1);
        documentRepo.save(document);
    }

    public List<Document> getAllUploadDocumentsByAuthor(User user) {
        return documentRepo.findAllByAuthor(user);
    }
}
