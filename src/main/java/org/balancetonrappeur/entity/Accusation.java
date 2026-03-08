package org.balancetonrappeur.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accusations", indexes = {
    @Index(name = "idx_accusation_rapper", columnList = "rapper_id"),
    @Index(name = "idx_accusation_category", columnList = "category"),
    @Index(name = "idx_accusation_status", columnList = "status"),
    @Index(name = "idx_accusation_date", columnList = "fact_date")
})
@Getter
@Setter
public class Accusation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rapper_id", nullable = false)
    private Rapper rapper;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccusationCategory category;

    @Column(nullable = false, length = 500)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccusationStatus status;

    @Column(name = "fact_date")
    private LocalDate factDate;

    @OneToMany(
        mappedBy = "accusation",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("sourceDate DESC NULLS LAST, id ASC")
    private List<Source> sources = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
