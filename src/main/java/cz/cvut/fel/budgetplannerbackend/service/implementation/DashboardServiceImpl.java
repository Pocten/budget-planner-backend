package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.exceptions.dashboard.DashboardNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.DashboardMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);


    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto> getAllDashboardsByUserId(Long userId) {
        LOG.info("Getting all dashboards for user id: {}", userId);
        List<Dashboard> dashboards = dashboardRepository.findAllByUserId(userId);
        return dashboards.stream()
                .map(dashboardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDto getDashboardById(Long id) {
        LOG.info("Getting dashboard with id: {}", id);
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new DashboardNotFoundException(id));
        return dashboardMapper.toDto(dashboard);
    }

    @Override
    @Transactional
    public DashboardDto createDashboard(DashboardDto dashboardDto) {
        LOG.info("Creating a new dashboard");
        Dashboard dashboard = dashboardMapper.toEntity(dashboardDto);
        Dashboard savedDashboard = dashboardRepository.save(dashboard);
        return dashboardMapper.toDto(savedDashboard);
    }

    @Override
    @Transactional
    public DashboardDto updateDashboard(Long id, DashboardDto dashboardDto) {
        LOG.info("Updating dashboard with id: {}", id);
        return dashboardRepository.findById(id).map(existingDashboard -> {
            if (dashboardDto.title() != null) {
                existingDashboard.setTitle(dashboardDto.title());
            }
            if (dashboardDto.description() != null) {
                existingDashboard.setDescription(dashboardDto.description());
            }

            Dashboard updatedDashboard = dashboardRepository.save(existingDashboard);
            LOG.info("Updated dashboard with id: {}", id);
            return dashboardMapper.toDto(updatedDashboard);
        }).orElseThrow(() -> {
            LOG.warn("Dashboard with id {} not found", id);
            return new DashboardNotFoundException(id);
        });
    }

    @Override
    @Transactional
    public void deleteDashboard(Long id) {
        LOG.info("Deleting dashboard with id: {}", id);
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new DashboardNotFoundException(id));
        dashboardRepository.delete(dashboard);
    }
}