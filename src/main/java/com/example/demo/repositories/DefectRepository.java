package com.example.demo.repositories;

import com.example.demo.entities.DefectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DefectRepository extends JpaRepository<DefectEntity, UUID> {

    List<DefectEntity> findByMainIssueId(UUID mainIssueId);

    @Query("SELECT d FROM DefectEntity d LEFT JOIN FETCH d.mainIssue mi WHERE mi.id IN :mainIssueIds")
    List<DefectEntity> findByMainIssueIdIn(List<UUID> mainIssueIds);
}
