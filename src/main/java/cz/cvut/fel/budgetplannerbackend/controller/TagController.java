package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.TagDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.implementation.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagServiceImpl tagService;
    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTagsByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all tags for dashboard with id: {}", dashboardId);
        List<TagDto> tagDtos = tagService.findAllTagsByDashboardId(dashboardId);
        LOG.info("Returned all tags for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(tagDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTagByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to get tag with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            TagDto tagDto = tagService.findTagByIdAndDashboardId(dashboardId, id);
            LOG.info("Returned tag with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(tagDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting tag", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(@PathVariable Long dashboardId, @RequestBody TagDto tagDto) {
        LOG.info("Received request to create tag for dashboard with id: {}", dashboardId);
        TagDto createdTagDto = tagService.createTag(dashboardId, tagDto);
        LOG.info("Created tag for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdTagDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody TagDto tagDto) {
        LOG.info("Received request to update tag with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            TagDto updatedTagDto = tagService.updateTag(dashboardId, id, tagDto);
            LOG.info("Updated tag with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(updatedTagDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating tag", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to delete tag with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            tagService.deleteTag(dashboardId, id);
            LOG.info("Deleted tag with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting tag", e);
            return ResponseEntity.notFound().build();
        }
    }
}

