package com.crg.mailservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crg.mailservice.model.Template;
import com.crg.mailservice.repository.TemplateRepository;
import com.crg.mailservice.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;

    // Create a new template
    @PostMapping
    public ResponseEntity<Template> createTemplate(@RequestBody Template template) {
        return ResponseEntity.ok(templateRepository.save(template));
    }

    // Get all templates
    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates() {
        return ResponseEntity.ok(templateRepository.findAll());
    }

    // Get template by ID
    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplateById(@PathVariable Long id) {
        return templateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update a template
    @PutMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable Long id, @RequestBody Template updatedTemplate) {
        return templateRepository.findById(id).map(template -> {
            template.setName(updatedTemplate.getName());
           // template.setSubject(updatedTemplate.getSubject());
            template.setContent(updatedTemplate.getContent());
            template.setCreatedBy(updatedTemplate.getCreatedBy());
            return ResponseEntity.ok(templateRepository.save(template));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a template
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        if (!templateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        templateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Get templates by owner (user)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Template>> getTemplatesByUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<Template> templates = templateRepository.findByCreatedBy(user);
                    return ResponseEntity.ok(templates);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
