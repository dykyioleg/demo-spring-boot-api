package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "main_issue", schema = "demo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MainIssueEntity extends AbstractEntity<UUID>{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "REPORTABLE")
    private boolean reportable;
}
