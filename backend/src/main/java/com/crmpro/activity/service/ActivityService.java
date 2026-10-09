package com.crmpro.activity.service;

import com.crmpro.activity.dto.ActivityRequest;
import com.crmpro.activity.dto.ActivityResponse;
import com.crmpro.activity.entity.Activity;
import com.crmpro.activity.repository.ActivityRepository;
import com.crmpro.common.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Transactional(readOnly = true)
    public List<ActivityResponse> findAll() {
        UUID tenantId = TenantContext.getTenantId();
        return activityRepository.findAllByOrganizationIdOrderByActivityDateDesc(tenantId).stream()
                .map(ActivityResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> findByDeal(UUID dealId) {
        UUID tenantId = TenantContext.getTenantId();
        return activityRepository.findAllByOrganizationIdAndDealIdOrderByActivityDateDesc(tenantId, dealId).stream()
                .map(ActivityResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ActivityResponse create(ActivityRequest request, UUID userId) {
        Activity activity = Activity.builder()
                .title(request.getTitle())
                .type(request.getType())
                .description(request.getDescription())
                .activityDate(request.getActivityDate() != null ? request.getActivityDate() : Instant.now())
                .dealId(request.getDealId())
                .contactId(request.getContactId())
                .userId(userId)
                .build();
        return ActivityResponse.fromEntity(activityRepository.save(activity));
    }
}
