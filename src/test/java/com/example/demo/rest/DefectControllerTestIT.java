package com.example.demo.rest;

import com.example.demo.DemoApplication;
import com.example.demo.TestcontainersConfiguration;
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
@Import({ TestcontainersConfiguration.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
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
}

