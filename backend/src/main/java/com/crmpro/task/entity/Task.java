package com.crmpro.task.entity;

import com.crmpro.common.entity.TenantEntity;
import com.crmpro.contact.entity.Contact;
import com.crmpro.deal.entity.Deal;
import com.crmpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends TenantEntity {

    @Column(name = "assigned_to_id")
    private UUID assignedToId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", insertable = false, updatable = false)
    private User assignedTo;

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

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(nullable = false)
    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING"; // PENDING, COMPLETED, CANCELLED

    @Column(name = "completed_at")
    private Instant completedAt;
}
