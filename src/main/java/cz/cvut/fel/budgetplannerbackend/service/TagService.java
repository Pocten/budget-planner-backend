package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.TagDto;

import java.util.List;

public interface TagService {
    List<TagDto> findAllTagsByDashboardId(Long dashboardId);
    TagDto findTagByIdAndDashboardId(Long id, Long dashboardId);
    TagDto createTag(Long dashboardId, TagDto tagDto);
    TagDto updateTag(Long dashboardId, Long id, TagDto tagDto);
    void deleteTag(Long dashboardId, Long id);
}



