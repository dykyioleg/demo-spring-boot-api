package com.example.demo.rest;

import com.example.demo.DemoApplication;
import com.example.demo.config.TestcontainersConfiguration;
import com.example.demo.config.TestSecurityConfig;
import com.example.demo.entities.DefectEntity;
import com.example.demo.entities.MainIssueEntity;
import com.example.demo.repositories.DefectRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { DemoApplication.class })
@Import({ TestcontainersConfiguration.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles("test")
public class DefectControllerTestIT {

    private DataFixture dataFixture;

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
    void saveNewDefect() throws Exception {
        //given
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("test main issue", true);
        String jsonContent = String.format("""
				{
					"mainIssueId": "%s"
				}
				""", mainIssue.getId());

        //when
        this.mockMvc.perform(post("/api/defect")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mainIssueId", equalTo(mainIssue.getId().toString())));

        final List<DefectEntity> defectEntities = defectRepository.findAll();
        assertThat(defectEntities).hasSize(1);
        assertThat(defectEntities.get(0).getMainIssue().getId()).isEqualTo(mainIssue.getId());
    }

    @Test
    void getDefectById() throws Exception {
        //given
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("test main issue", true);
        DefectEntity defect = dataFixture.defect.givenDefect(mainIssue);

        //when
        this.mockMvc.perform(get("/api/defect/{defectId}", defect.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(defect.getId().toString())))
                .andExpect(jsonPath("$.mainIssueId", equalTo(mainIssue.getId().toString())));
    }

    @Test
    void getDefectById_notFound() throws Exception {
        //given
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        //when
        this.mockMvc.perform(get("/api/defect/{defectId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDefect() throws Exception {
        //given
        MainIssueEntity mainIssue1 = dataFixture.mainIssue.givenMainIssue("main issue 1", true);
        MainIssueEntity mainIssue2 = dataFixture.mainIssue.givenMainIssue("main issue 2", false);
        DefectEntity defect = dataFixture.defect.givenDefect(mainIssue1);

        String jsonContent = String.format("""
				{
					"mainIssueId": "%s"
				}
				""", mainIssue2.getId());

        //when
        this.mockMvc.perform(put("/api/defect/{defectId}", defect.getId().toString())
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mainIssueId", equalTo(mainIssue2.getId().toString())));

        DefectEntity updatedDefect = defectRepository.findById(defect.getId()).get();
        assertThat(updatedDefect.getMainIssue().getId()).isEqualTo(mainIssue2.getId());
    }

    @Test
    void deleteDefect() throws Exception {
        //given
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("test main issue", true);
        DefectEntity defect = dataFixture.defect.givenDefect(mainIssue);

        //when
        this.mockMvc.perform(delete("/api/defect/{defectId}", defect.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isNoContent());

        Optional<DefectEntity> deletedDefect = defectRepository.findById(defect.getId());
        assertThat(deletedDefect).isEmpty();
    }

    @Test
    void saveDefectWithInvalidMainIssueId() throws Exception {
        //given
        String nonExistentMainIssueId = "00000000-0000-0000-0000-000000000000";
        String jsonContent = String.format("""
				{
					"mainIssueId": "%s"
				}
				""", nonExistentMainIssueId);

        //when
        this.mockMvc.perform(post("/api/defect")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    void getDefectsByMainIssueIds_shouldReturnDefectsForMultipleMainIssues() throws Exception {
        // given - Create multiple main issues
        MainIssueEntity mainIssue1 = dataFixture.mainIssue.givenMainIssue("Main Issue 1", true);
        MainIssueEntity mainIssue2 = dataFixture.mainIssue.givenMainIssue("Main Issue 2", true);
        MainIssueEntity mainIssue3 = dataFixture.mainIssue.givenMainIssue("Main Issue 3", false);

        // Create defects for each main issue
        DefectEntity defect1 = dataFixture.defect.givenDefect(mainIssue1);
        DefectEntity defect2 = dataFixture.defect.givenDefect(mainIssue1); // Second defect for mainIssue1
        DefectEntity defect3 = dataFixture.defect.givenDefect(mainIssue2);
        DefectEntity defect4 = dataFixture.defect.givenDefect(mainIssue3);

        testEntityManager.clear(); // Clear persistence context to ensure fresh query

        // when - Call endpoint with multiple main issue IDs (testing N+1 prevention)
        this.mockMvc.perform(get("/api/defect/by-main-issues")
                        .param("mainIssueIds", mainIssue1.getId().toString())
                        .param("mainIssueIds", mainIssue2.getId().toString())
                        .param("mainIssueIds", mainIssue3.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4)) // 4 defects total
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].mainIssueId").exists())
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].version").exists());

        // Verify data in database
        List<DefectEntity> foundDefects = defectRepository.findByMainIssueIdIn(
                List.of(mainIssue1.getId(), mainIssue2.getId(), mainIssue3.getId()));
        assertThat(foundDefects).hasSize(4);

        // Verify all defects have their main issues loaded (no lazy loading exception)
        foundDefects.forEach(defect -> {
            assertThat(defect.getMainIssue()).isNotNull();
            assertThat(defect.getMainIssue().getId()).isNotNull();
        });
    }

    @Test
    void getDefectsByMainIssueIds_shouldReturnEmptyListWhenNoDefectsFound() throws Exception {
        // given - Create main issue without defects
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("Main Issue Without Defects", true);

        // when
        this.mockMvc.perform(get("/api/defect/by-main-issues")
                        .param("mainIssueIds", mainIssue.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDefectsByMainIssueIds_shouldHandleSingleMainIssueId() throws Exception {
        // given
        MainIssueEntity mainIssue = dataFixture.mainIssue.givenMainIssue("Single Main Issue", true);

        DefectEntity defect1 = dataFixture.defect.givenDefect(mainIssue);
        DefectEntity defect2 = dataFixture.defect.givenDefect(mainIssue);

        // when
        this.mockMvc.perform(get("/api/defect/by-main-issues")
                        .param("mainIssueIds", mainIssue.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].mainIssueId").value(mainIssue.getId().toString()))
                .andExpect(jsonPath("$[1].mainIssueId").value(mainIssue.getId().toString()));
    }
}
