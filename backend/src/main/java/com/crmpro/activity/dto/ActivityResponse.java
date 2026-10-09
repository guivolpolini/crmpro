package com.crmpro.activity.dto;

import com.crmpro.activity.entity.Activity;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ActivityResponse {
    private UUID id;
    private UUID organizationId;
    private UUID dealId;
    private String dealTitle;
    private UUID contactId;
    private String contactName;
    private UUID userId;
    private String userName;
    private String type;
    private String title;
    private String description;
    private Instant activityDate;
    private Instant createdAt;

    public static ActivityResponse fromEntity(Activity activity) {
        if (activity == null) return null;
        return ActivityResponse.builder()
                .id(activity.getId())
                .organizationId(activity.getOrganizationId())
                .dealId(activity.getDealId())
                .dealTitle(activity.getDeal() != null ? activity.getDeal().getTitle() : null)
                .contactId(activity.getContactId())
                .contactName(activity.getContact() != null ? activity.getContact().getName() : null)
                .userId(activity.getUserId())
                .userName(activity.getUser() != null ? activity.getUser().getName() : null)
                .type(activity.getType())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .activityDate(activity.getActivityDate())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
