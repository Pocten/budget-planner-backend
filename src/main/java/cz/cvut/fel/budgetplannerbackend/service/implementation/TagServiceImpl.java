package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.TagDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.Tag;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.TagMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.TagRepository;
import cz.cvut.fel.budgetplannerbackend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final DashboardRepository dashboardRepository;
    private final TagMapper tagMapper;
    private static final Logger LOG = LoggerFactory.getLogger(TagServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> findAllTagsByDashboardId(Long dashboardId) {
        LOG.info("Fetching all tags for dashboard id: {}", dashboardId);
        List<Tag> tags = tagRepository.findAllByDashboardId(dashboardId);
        return tags.stream()
                .map(tagMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto findTagByIdAndDashboardId(Long id, Long dashboardId) {
        LOG.info("Fetching tag with id: {} for dashboard id: {}", id, dashboardId);
        Tag tag = tagRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id + " for dashboard id: " + dashboardId));
        return tagMapper.toDto(tag);
    }

    @Override
    @Transactional
    public TagDto createTag(Long dashboardId, TagDto tagDto) {
        LOG.info("Creating new tag for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        Tag tag = tagMapper.toEntity(tagDto);
        tag.setDashboard(dashboard);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDto(savedTag);
    }

    @Override
    @Transactional
    public TagDto updateTag(Long dashboardId, Long id, TagDto tagDto) {
        LOG.info("Updating tag with id: {} for dashboard id: {}", id, dashboardId);
        Tag tag = tagRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id + " for dashboard id: " + dashboardId));

        if (tagDto.name() != null) {
            tag.setName(tagDto.name());
        }
        if (tagDto.description() != null) {
            tag.setDescription(tagDto.description());
        }

        Tag updatedTag = tagRepository.save(tag);
        LOG.info("Updated tag with id: {} for dashboard id: {}", id, dashboardId);
        return tagMapper.toDto(updatedTag);
    }

    @Override
    @Transactional
    public void deleteTag(Long dashboardId, Long id) {
        LOG.info("Deleting tag with id: {} for dashboard id: {}", id, dashboardId);
        Tag tag = tagRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id + " for dashboard id: " + dashboardId));
        tagRepository.delete(tag);
    }
}