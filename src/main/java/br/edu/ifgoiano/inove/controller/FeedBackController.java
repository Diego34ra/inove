package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.request.course.FeedBackOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.StudentSimpleResponseDTO;
import br.edu.ifgoiano.inove.domain.model.FeedBack;
import br.edu.ifgoiano.inove.domain.service.FeedBackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inove/feedbacks")
public class FeedBackController {

    private final FeedBackService feedBackService;

    public FeedBackController(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }

    @PostMapping
    public ResponseEntity<FeedBackOutputDTO> createFeedback(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestBody String comment) {
        FeedBack feedback = feedBackService.createFeedback(userId, courseId, comment);
        return ResponseEntity.ok(toOutputDTO(feedback));
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<FeedBackOutputDTO> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestParam Long userId,
            @RequestBody String newComment) {
        FeedBack feedback = feedBackService.updateFeedback(feedbackId, userId, newComment);
        return ResponseEntity.ok(toOutputDTO(feedback));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Long feedbackId,
            @RequestParam Long userId) {
        feedBackService.deleteFeedback(feedbackId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<FeedBackOutputDTO>> getFeedbacksByCourse(@PathVariable Long courseId) {
        List<FeedBack> feedbacks = feedBackService.getFeedbacksByCourse(courseId);
        List<FeedBackOutputDTO> feedbackDTOs = feedbacks.stream()
                .map(this::toOutputDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
    }

    @GetMapping("/user")
    public ResponseEntity<FeedBackOutputDTO> getUserFeedbackForCourse(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        Optional<FeedBack> feedback = feedBackService.getUserFeedbackForCourse(userId, courseId);
        return feedback.map(fb -> ResponseEntity.ok(toOutputDTO(fb)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    private FeedBackOutputDTO toOutputDTO(FeedBack feedback) {
        FeedBackOutputDTO dto = new FeedBackOutputDTO();
        dto.setId(feedback.getId());
        dto.setComment(feedback.getComment());

        if (feedback.getStudent() != null) {
            StudentSimpleResponseDTO studentDTO = new StudentSimpleResponseDTO();
            studentDTO.setId(feedback.getStudent().getId());
            studentDTO.setName(feedback.getStudent().getName());
            studentDTO.setEmail(feedback.getStudent().getEmail());
            studentDTO.setSchool(null);
            dto.setStudent(studentDTO);
        }

        return dto;
    }
}
