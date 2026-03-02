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

    /**
     * Fetch defects for specific main issues using JOIN FETCH.
     * This avoids the N+1 problem by loading both defects and main issues in a single query.
     *
     * Without JOIN FETCH: 1 query for defects + N queries for each main issue (N+1 problem)
     * With JOIN FETCH: 1 query for both defects and main issues
     *
     * @param mainIssueIds List of main issue IDs to fetch defects for
     * @return List of defects with eagerly loaded main issues
     */
    @Query("SELECT d FROM DefectEntity d LEFT JOIN FETCH d.mainIssue mi WHERE mi.id IN :mainIssueIds")
    List<DefectEntity> findByMainIssueIdIn(List<UUID> mainIssueIds);
}
