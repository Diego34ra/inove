package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.domain.model.FeedBack;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.repository.FeedBackRepository;
import br.edu.ifgoiano.inove.domain.repository.CourseRepository;
import br.edu.ifgoiano.inove.domain.repository.UserRepository;
import br.edu.ifgoiano.inove.domain.service.FeedBackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FeedBackServiceImpl implements FeedBackService {

    private final FeedBackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public FeedBackServiceImpl(FeedBackRepository feedbackRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public FeedBack createFeedback(Long userId, Long courseId, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));
        Optional<FeedBack> existingFeedback = feedbackRepository.findByStudentIdAndCourseId(userId, courseId);
        if (existingFeedback.isPresent()) {
            throw new RuntimeException("Usuário já fez um comentário neste curso.");
        }

        FeedBack feedback = new FeedBack();
        feedback.setStudent(user);
        feedback.setCourse(course);
        feedback.setComment(comment);
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public FeedBack updateFeedback(Long feedbackId, Long userId, String newComment) {
        FeedBack feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));

        if (!feedback.getStudent().getId().equals(userId)) {
            throw new RuntimeException("Usuário não tem permissão para editar este comentário.");
        }

        feedback.setComment(newComment);
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void deleteFeedback(Long feedbackId, Long userId) {
        FeedBack feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));

        if (!feedback.getStudent().getId().equals(userId)) {
            throw new RuntimeException("Usuário não tem permissão para excluir este comentário.");
        }

        feedbackRepository.delete(feedback);
    }

    @Override
    public List<FeedBack> getFeedbacksByCourse(Long courseId) {
        return feedbackRepository.findByCourseId(courseId);
    }

    @Override
    public Optional<FeedBack> getUserFeedbackForCourse(Long userId, Long courseId) {
        return feedbackRepository.findByStudentIdAndCourseId(userId, courseId);
    }
}
