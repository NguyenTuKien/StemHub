package com.team7.StemHub.service;

import com.team7.StemHub.dao.DocumentRepo;
import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepo documentRepo;

    public int countFavorites(UUID documentId) {
        return documentRepo.countFavoritesById(documentId);
    }

    public Document saveDocument(Document document) {
        return documentRepo.save(document);
    }

    public List<Document> getTopDocument() {
        return documentRepo.findAllByOrderByFavoriteCountDesc().stream().limit(20).toList();
    }

    public List<Document> getNewestDocuments() {
        return documentRepo.findAllByOrderByCreateAtDesc().stream().limit(20).toList();
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
}
