package com.example.demo.repositories;

import com.example.demo.TestcontainersConfiguration;
import com.example.demo.entities.MainIssueEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ TestcontainersConfiguration.class })
@Transactional
public class MainIssueRepositoryTestIT {

    @Autowired
    private MainIssueRepository mainIssueRepository;

    @Test
    void saveMainIssue() {
        // given
        final MainIssueEntity mainIssueEntity =  new MainIssueEntity();
        mainIssueEntity.setDescription("mainIssue_desc");
        mainIssueEntity.setReportable(true);
        // when
        mainIssueRepository.save(mainIssueEntity);
        // then
        final List<MainIssueEntity> mainIssueEntities = mainIssueRepository.findAll();
        assertThat(mainIssueEntities).hasSize(1);
        assertThat(mainIssueEntities).extracting(MainIssueEntity::getDescription).contains("mainIssue_desc");
        assertThat(mainIssueEntities).extracting(MainIssueEntity::isReportable).contains(true);
    }
}
