package com.crmpro.activity.entity;

import com.crmpro.common.entity.TenantEntity;
import com.crmpro.contact.entity.Contact;
import com.crmpro.deal.entity.Deal;
import com.crmpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity extends TenantEntity {

    @Column(name = "deal_id")
    private UUID dealId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", insertable = false, updatable = false)
    private Deal deal;

    @Column(name = "contact_id")
    private UUID contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", insertable = false, updatable = false)
    private Contact contact;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private String type; // CALL, EMAIL, MEETING, NOTE, TASK_COMPLETED

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "activity_date", nullable = false)
    @Builder.Default
    private Instant activityDate = Instant.now();
}
