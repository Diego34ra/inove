package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.ConteudoSimpleOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.SecaoSimpleOutputDTO;
import br.edu.ifgoiano.inove.controller.exceptions.EscolaNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Conteudo;
import br.edu.ifgoiano.inove.domain.model.Secao;
import br.edu.ifgoiano.inove.domain.service.ConteudoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos")
public class ConteudoController {
    @Autowired
    private ConteudoService contentService;

    @GetMapping
    public List<ConteudoSimpleOutputDTO> list(@PathVariable Long sectionId){
        return contentService.list(sectionId);
    }

    @GetMapping("/{contentId}")
    public ResponseEntity<?> findOne(@PathVariable Long sectionId, @PathVariable Long contentId){
        try {
            Conteudo savedContent = contentService.findById(sectionId, contentId);

            return ResponseEntity.status(HttpStatus.OK).body(savedContent);
        }catch(EscolaNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping
    public Conteudo create(@PathVariable Long courseId,
                           @PathVariable Long sectionId,
                           @RequestBody @Validated  Conteudo newContent){
        return contentService.create(courseId, sectionId, newContent);
    }

    @PutMapping("/{contentId}")
    public ResponseEntity<?> update(@PathVariable Long sectionId,
                                    @PathVariable Long contentId,
                                    @RequestBody Conteudo newContent){
        Conteudo updatedContent = contentService.update(sectionId, contentId, newContent);

        return ResponseEntity.status(HttpStatus.OK).body(updatedContent);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{contentId}")
    public ResponseEntity<?> delete(@PathVariable Long sectionId,@PathVariable Long contentId){
            contentService.deleteById(sectionId, contentId);
            return ResponseEntity.noContent().build();
    }
}
