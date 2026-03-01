package com.example.demo.testdata;

import com.example.demo.entities.MainIssueEntity;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class DataFixture {
    private final TestEntityManager entityManager;
    public final MainIssue mainIssue = new MainIssue();

    public DataFixture(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public final class MainIssue {
        public MainIssueEntity givenMainIssue(String description, boolean reportable) {
            MainIssueEntity mainIssueEntity = new MainIssueEntity();
            mainIssueEntity.setDescription(description);
            mainIssueEntity.setReportable(reportable);
            return entityManager.persistAndFlush(mainIssueEntity);
        }
    }
}
