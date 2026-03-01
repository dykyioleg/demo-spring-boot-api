package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "defect", schema = "demo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefectEntity extends AbstractEntity<UUID>{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_issue_id")
    private MainIssueEntity mainIssue;
}
