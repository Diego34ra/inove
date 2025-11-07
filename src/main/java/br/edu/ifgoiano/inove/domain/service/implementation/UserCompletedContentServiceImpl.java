package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.response.content.completedContent.CompletedContentMinDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.completedContent.CompletedContentResponseDTO;
import br.edu.ifgoiano.inove.domain.model.*;
import br.edu.ifgoiano.inove.domain.repository.UserCompletedContentRepository;
import br.edu.ifgoiano.inove.domain.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class UserCompletedContentServiceImpl implements UserCompletedContentService {
    private final UserCompletedContentRepository repository;
    private final ContentService contentService;
    private final CourseService courseService;
    private final SectionService sectionService;
    private final UserService userService;
    private final MyModelMapper mapper;

    public UserCompletedContentServiceImpl(UserCompletedContentRepository repository,
                                           ContentService contentService, CourseService courseService, SectionService sectionService,
                                           UserService userService,
                                           MyModelMapper mapper) {
        this.repository = repository;
        this.contentService = contentService;
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public CompletedContentMinDTO create (Long courseId, Long sectionId, Long contentId, Long userId){
        Course course = courseService.findById(courseId);
        Section section = sectionService.findById(sectionId);
        Content content = contentService.findById(courseId, sectionId, contentId);
        User user = userService.findById(userId);

        UserCompletedContent completedContent = new UserCompletedContent();
        completedContent.setCourse(course);
        completedContent.setSection(section);
        completedContent.setContent(content);
        completedContent.setUser(user);

        return new CompletedContentMinDTO(repository.save(completedContent));
    }

    @Override
    public List<CompletedContentMinDTO> listContentCompletedDTO(Long courseId, Long userId) {
        return mapper.toList(repository.findAllByCourseIdAndUserId(courseId, userId),  CompletedContentMinDTO.class);
    }

    @Override
    public CompletedContentResponseDTO getUserProgress(Long courseId, Long userId) {
        CompletedContentResponseDTO responseDTO = new CompletedContentResponseDTO();
        responseDTO.setCompletedContents(listContentCompletedDTO(courseId, userId));

        Long totalContents = contentService.getContentAmountByCourseId(courseId);
        Long completedContents = repository.countByCourseIdAndUserId(courseId, userId);

        if (totalContents > 0) {
            responseDTO.setCompletePercentage(BigDecimal.valueOf(completedContents)
                    .divide(BigDecimal.valueOf(totalContents), 2, RoundingMode.HALF_DOWN));
        } else {
            responseDTO.setCompletePercentage(BigDecimal.ZERO);
        }

        return responseDTO;
    }

    @Override
    @Transactional
    public void deleteByContentId(Long contentId) {
        repository.deleteByContentId(contentId);
    }

    @Override
    @Transactional
    public void resetCourseProgress(Long courseId) {
        repository.deleteByCourseId(courseId);
    }
}
