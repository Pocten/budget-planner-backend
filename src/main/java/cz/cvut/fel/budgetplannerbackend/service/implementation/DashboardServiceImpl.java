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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;


    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto> getAllDashboardsByUserId(Long userId) {
        LOG.info("Getting all dashboards for user id: {}", userId);
        List<Dashboard> dashboards = dashboardRepository.findAllByUserId(userId);
        return dashboards.stream()
                .map(dashboardMapper::toDto)
                .collect(Collectors.toList());
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
        Dashboard existingDashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new DashboardNotFoundException(id));

        //existingDashboard.setTitle(dashboardDto.getTitle());
        //existingDashboard.setDescription(dashboardDto.getDescription());
        // Update other fields as necessary

        Dashboard updatedDashboard = dashboardRepository.save(existingDashboard);
        return dashboardMapper.toDto(updatedDashboard);
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
