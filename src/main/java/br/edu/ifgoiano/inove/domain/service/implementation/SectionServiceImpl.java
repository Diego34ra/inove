package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.request.section.SectionRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceInUseException;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.model.Section;
import br.edu.ifgoiano.inove.domain.repository.SectionRepository;
import br.edu.ifgoiano.inove.domain.repository.CourseRepository;
import br.edu.ifgoiano.inove.domain.service.SectionService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {
    @Autowired
    private SectionRepository sectionRespository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MyModelMapper mapper;

    @Autowired
    private InoveUtils inoveUtils;

    @Override
    @Transactional(readOnly = true)
    public List<SectionSimpleResponseDTO> list(Long courseId) {
        return mapper.toList(sectionRespository.findByCourseId(courseId), SectionSimpleResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Section findById(Long sectionId) {
        return sectionRespository.findById(sectionId)
                .orElseThrow(()-> new ResourceNotFoundException("Não foi possível encontrar nenhum conteudo com esse id."));
    }

    @Override
    @Transactional(readOnly = true)
    public SectionResponseDTO findOne(Long courseId, Long sectionId) {
        Section section = sectionRespository.findByIdAndCourseId(sectionId, courseId)
                .orElseThrow(()-> new ResourceNotFoundException("Não foi possível encontrar nenhuma seção com esse id."));
        return mapper.mapTo(section, SectionResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Section findByIdAndCursoId(Long courseId, Long sectionId) {
        return sectionRespository.findByIdAndCourseId(sectionId, courseId)
                .orElseThrow(()-> new ResourceNotFoundException("Não foi possível encontrar nenhuma seção com esse id."));
    }

    @Override
    @Transactional
    public SectionResponseDTO create(Long courseId, SectionRequestDTO newSectionDTO) {
        Section section = mapper.mapTo(newSectionDTO, Section.class);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));
        section.setCourse(course);
        course.setLastUpdateDate(LocalDateTime.now());
        courseRepository.save(course);

        return mapper.mapTo(sectionRespository.save(section), SectionResponseDTO.class);
    }

    @Override
    @Transactional
    public SectionResponseDTO update(Long courseId, Long sectionId, SectionRequestDTO newSectionDTO) {
        Section newSection = mapper.mapTo(newSectionDTO, Section.class);
        Section section = findByIdAndCursoId(courseId, sectionId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));
        course.setLastUpdateDate(LocalDateTime.now());
        courseRepository.save(course);

        String[] nullPropertyNames = inoveUtils.getNullPropertyNames(newSection);
        String[] ignoredProperties = new String[nullPropertyNames.length + 3];
        System.arraycopy(nullPropertyNames, 0, ignoredProperties, 0, nullPropertyNames.length);
        ignoredProperties[nullPropertyNames.length] = "id";
        ignoredProperties[nullPropertyNames.length + 1] = "course";
        ignoredProperties[nullPropertyNames.length + 2] = "contents";

        BeanUtils.copyProperties(newSection, section, ignoredProperties);
        return mapper.mapTo(sectionRespository.save(section), SectionResponseDTO.class);
    }

    @Override
    @Transactional
    public void deleteById(Long courseId, Long sectionId) {
        try{
            Section section = findByIdAndCursoId(courseId, sectionId);
            sectionRespository.delete(section);
        } catch (DataIntegrityViolationException ex){
            throw new ResourceInUseException("O usuário de ID %d esta em uso e não pode ser removido.");
        }
    }
}
