package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.DashboardMapper;
import cz.cvut.fel.budgetplannerbackend.repository.*;
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
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialGoalRepository financialGoalRepository;
    private final UserRepository userRepository;
    private final DashboardMapper dashboardMapper;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);


    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto> findAllDashboardsByUserId(Long userId) {
        LOG.info("Getting all dashboards for user id: {}", userId);
        List<Dashboard> dashboards = dashboardRepository.findAllByUserId(userId);
        return dashboards.stream()
                .map(dashboardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDto findUserDashboardById(Long userId, Long id) {
        LOG.info("Getting dashboard with id: {} for user id: {}", id, userId);
        Dashboard dashboard = dashboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", id));
        return dashboardMapper.toDto(dashboard);
    }

    @Override
    @Transactional
    public DashboardDto createDashboard(Long userId, DashboardDto dashboardDto) {
        LOG.info("Creating a new dashboard for user id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        Dashboard dashboard = dashboardMapper.toEntity(dashboardDto);
        dashboard.setUser(user);
        Dashboard savedDashboard = dashboardRepository.save(dashboard);
        return dashboardMapper.toDto(savedDashboard);
    }

    @Override
    @Transactional
    public DashboardDto updateDashboard(Long userId, Long id, DashboardDto dashboardDto) {
        LOG.info("Updating dashboard with id: {} for user id: {}", id, userId);
        Dashboard existingDashboard = dashboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", id));

        if (dashboardDto.title() != null) {
            existingDashboard.setTitle(dashboardDto.title());
        }
        if (dashboardDto.description() != null) {
            existingDashboard.setDescription(dashboardDto.description());
        }

        Dashboard updatedDashboard = dashboardRepository.save(existingDashboard);
        LOG.info("Updated dashboard with id: {} for user id: {}", updatedDashboard.getId(), userId);
        return dashboardMapper.toDto(updatedDashboard);
    }

    @Override
    @Transactional
    public void deleteDashboard(Long userId, Long id) {
        LOG.info("Initiating deletion of dashboard with id: {} for user id: {}", id, userId);
        Dashboard dashboard = dashboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", id));

        LOG.info("Removing associations between financial records and tags for dashboard id: {}", id);
        financialRecordRepository.findAllByDashboardId(id).forEach(financialRecord -> {
            financialRecord.getTags().clear();
            financialRecordRepository.save(financialRecord);
        });

        LOG.info("Deleting all financial goals, budgets, categories, tags, and financial records associated with dashboard id: {}", id);
        // TODO delete financial goals
        budgetRepository.deleteByDashboardId(id);
        categoryRepository.deleteByDashboardId(id);
        financialRecordRepository.deleteByDashboardId(id);
        tagRepository.deleteByDashboardId(id);

        LOG.info("Dashboard with id: {} successfully deleted, along with all its associated data.", id);
        dashboardRepository.delete(dashboard);
    }
}