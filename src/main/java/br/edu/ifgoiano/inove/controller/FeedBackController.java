package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.domain.model.FeedBack;
import br.edu.ifgoiano.inove.domain.service.FeedBackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inove/feedbacks")
public class FeedBackController {

    private final FeedBackService feedBackService;

    public FeedBackController(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }

    @PostMapping
    public ResponseEntity<FeedBack> createFeedback(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestBody String comment) {
        return ResponseEntity.ok(feedBackService.createFeedback(userId, courseId, comment));
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<FeedBack> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestParam Long userId,
            @RequestBody String newComment) {
        return ResponseEntity.ok(feedBackService.updateFeedback(feedbackId, userId, newComment));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Long feedbackId,
            @RequestParam Long userId) {
        feedBackService.deleteFeedback(feedbackId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<FeedBack>> getFeedbacksByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(feedBackService.getFeedbacksByCourse(courseId));
    }

    @GetMapping("/user")
    public ResponseEntity<Optional<FeedBack>> getUserFeedbackForCourse(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(feedBackService.getUserFeedbackForCourse(userId, courseId));
    }
}
