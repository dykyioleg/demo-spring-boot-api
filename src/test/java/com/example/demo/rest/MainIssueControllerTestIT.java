package com.example.demo.rest;

import com.example.demo.DemoApplication;
import com.example.demo.TestcontainersConfiguration;
import com.example.demo.entities.DefectEntity;
import com.example.demo.entities.MainIssueEntity;
import com.example.demo.repositories.DefectRepository;
import com.example.demo.repositories.MainIssueRepository;
import com.example.demo.testdata.DataFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { DemoApplication.class })
@Import({ TestcontainersConfiguration.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class MainIssueControllerTestIT {

    private DataFixture dataFixture;

    @Autowired
    private MainIssueRepository mainIssueRepository;

    @Autowired
    private DefectRepository defectRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        dataFixture = new DataFixture(testEntityManager);
    }

    @Test
    void saveNewMainIssue() throws Exception {
        //given
        String jsonContent = """
				{
					"description": "super main issue",
					"reportable": true
				}
				""";
        //when
        this.mockMvc.perform(post("/api/main-issue")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)//
                )
                .andDo(print()) //
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", equalTo("super main issue")))
                .andExpect(jsonPath("$.reportable", equalTo(true)));

        final List<MainIssueEntity> mainIssueEntities = mainIssueRepository.findAll();
        assertThat(mainIssueEntities).hasSize(1);
        assertThat(mainIssueEntities).extracting(MainIssueEntity::getDescription).contains("super main issue");
        assertThat(mainIssueEntities).extracting(MainIssueEntity::isReportable).contains(true);
    }

    @Test
    void updateMainIssue() throws Exception {
        //given
        String jsonContent = """
				{
					"description": "super main issue for update",
					"reportable": false
				}
				""";
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("super main issue", true);
        //when
        this.mockMvc.perform(put("/api/main-issue/{mainIssueId}", mainIssue.getId().toString())
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)//
                )
                .andDo(print()) //
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", equalTo("super main issue for update")))
                .andExpect(jsonPath("$.reportable", equalTo(false)));
        MainIssueEntity updatedMainIssue = mainIssueRepository.findById(mainIssue.getId()).get();
        assertThat(updatedMainIssue.getVersion()).isZero();
    }

    @Test
    void getMainIssueById() throws Exception {
        //given
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("test main issue", true);

        //when
        this.mockMvc.perform(get("/api/main-issue/{mainIssueId}", mainIssue.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(mainIssue.getId().toString())))
                .andExpect(jsonPath("$.description", equalTo("test main issue")))
                .andExpect(jsonPath("$.reportable", equalTo(true)));
    }

    @Test
    void getMainIssueById_notFound() throws Exception {
        //given
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        //when
        this.mockMvc.perform(get("/api/main-issue/{mainIssueId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMainIssue() throws Exception {
        //given
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("main issue to delete", true);

        //when
        this.mockMvc.perform(delete("/api/main-issue/{mainIssueId}", mainIssue.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isNoContent());

        Optional<MainIssueEntity> deletedMainIssue = mainIssueRepository.findById(mainIssue.getId());
        assertThat(deletedMainIssue).isEmpty();
    }

    @Test
    void deleteMainIssue_withRelatedDefects() throws Exception {
        //given
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("main issue with defects", true);
        DefectEntity defect1 = dataFixture.defect.givenDefect(mainIssue);
        DefectEntity defect2 = dataFixture.defect.givenDefect(mainIssue);
        DefectEntity defect3 = dataFixture.defect.givenDefect(mainIssue);

        // Verify defects exist
        assertThat(defectRepository.findByMainIssueId(mainIssue.getId())).hasSize(3);

        //when
        this.mockMvc.perform(delete("/api/main-issue/{mainIssueId}", mainIssue.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isNoContent());

        // Verify main issue is deleted
        Optional<MainIssueEntity> deletedMainIssue = mainIssueRepository.findById(mainIssue.getId());
        assertThat(deletedMainIssue).isEmpty();

        // Verify all related defects are deleted
        List<DefectEntity> remainingDefects = defectRepository.findByMainIssueId(mainIssue.getId());
        assertThat(remainingDefects).isEmpty();

        // Verify defects are actually deleted from DB
        assertThat(defectRepository.findById(defect1.getId())).isEmpty();
        assertThat(defectRepository.findById(defect2.getId())).isEmpty();
        assertThat(defectRepository.findById(defect3.getId())).isEmpty();
    }

    @Test
    void deleteMainIssue_notFound() throws Exception {
        //given
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        //when
        this.mockMvc.perform(delete("/api/main-issue/{mainIssueId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isNotFound());
    }
}
