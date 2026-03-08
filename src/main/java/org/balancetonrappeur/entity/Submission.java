package org.balancetonrappeur.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submissions")
@Getter
@Setter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionType type;

    // Rappeur indexé (null si rappeur inconnu)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rapper_id")
    private Rapper rapper;

    // Rappeur non indexé
    @Column(name = "unknown_rapper_name", length = 255)
    private String unknownRapperName;

    // Accusation ciblée (EDIT uniquement)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accusation_id")
    private Accusation accusation;

    @Enumerated(EnumType.STRING)
    private AccusationCategory category;

    @Column(length = 500)
    private String title;

    @Enumerated(EnumType.STRING)
    private AccusationStatus status;

    @Column(name = "fact_date")
    private LocalDate factDate;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionSource> sources = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus submissionStatus = SubmissionStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
