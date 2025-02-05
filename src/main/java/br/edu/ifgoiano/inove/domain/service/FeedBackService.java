package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.domain.model.FeedBack;
import java.util.List;
import java.util.Optional;

public interface FeedBackService {

    FeedBack createFeedback(Long userId, Long courseId, String comment);

    FeedBack updateFeedback(Long feedbackId, Long userId, String newComment);

    void deleteFeedback(Long feedbackId, Long userId);

    List<FeedBack> getFeedbacksByCourse(Long courseId);

    Optional<FeedBack> getUserFeedbackForCourse(Long userId, Long courseId);
}
