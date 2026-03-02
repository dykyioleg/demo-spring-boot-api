package com.example.demo.rest;

import com.example.demo.DemoApplication;
import com.example.demo.config.TestcontainersConfiguration;
import com.example.demo.config.TestSecurityConfig;
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
import org.springframework.test.context.ActiveProfiles;
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
@Import({ TestcontainersConfiguration.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles("test")
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

    @Test
    void getAllMainIssues_shouldReturnPaginatedResults() throws Exception {
        // given - Create 25 main issues
        for (int i = 1; i <= 25; i++) {
            dataFixture.mainIssue.givenMainIssue("Main Issue " + i, i % 2 == 0);
        }
        testEntityManager.flush();
        testEntityManager.clear();

        // when - Request first page with size 10
        this.mockMvc.perform(get("/api/main-issue")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "created,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.numberOfElements").value(10));
    }

    @Test
    void getAllMainIssues_shouldReturnSecondPage() throws Exception {
        // given - Create 25 main issues
        for (int i = 1; i <= 25; i++) {
            dataFixture.mainIssue.givenMainIssue("Issue " + i, true);
        }

        // when - Request second page
        this.mockMvc.perform(get("/api/main-issue")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "created,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void getAllMainIssues_shouldReturnLastPage() throws Exception {
        // given - Create 25 main issues
        for (int i = 1; i <= 25; i++) {
            dataFixture.mainIssue.givenMainIssue("Issue " + i, true);
        }

        // when - Request last page (page 2, which is the 3rd page)
        this.mockMvc.perform(get("/api/main-issue")
                        .param("page", "2")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5)) // Only 5 items on last page
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.numberOfElements").value(5));
    }

    @Test
    void getAllMainIssues_shouldSortByDescriptionAscending() throws Exception {
        // given - Create main issues with specific descriptions
        dataFixture.mainIssue.givenMainIssue("Zebra Issue", true);
        dataFixture.mainIssue.givenMainIssue("Alpha Issue", true);
        dataFixture.mainIssue.givenMainIssue("Beta Issue", true);

        // when - Sort by description ascending
        this.mockMvc.perform(get("/api/main-issue")
                        .param("sort", "description,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Alpha Issue"))
                .andExpect(jsonPath("$.content[1].description").value("Beta Issue"))
                .andExpect(jsonPath("$.content[2].description").value("Zebra Issue"));
    }

    @Test
    void getAllMainIssues_shouldSortByMultipleFields() throws Exception {
        // given - Create main issues with same reportable flag but different descriptions
        dataFixture.mainIssue.givenMainIssue("B Issue", true);
        dataFixture.mainIssue.givenMainIssue("A Issue", true);
        dataFixture.mainIssue.givenMainIssue("C Issue", false);

        // when - Sort by reportable desc, then description asc
        this.mockMvc.perform(get("/api/main-issue")
                        .param("sort", "reportable,desc")
                        .param("sort", "description,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reportable").value(true))
                .andExpect(jsonPath("$.content[0].description").value("A Issue"))
                .andExpect(jsonPath("$.content[1].reportable").value(true))
                .andExpect(jsonPath("$.content[1].description").value("B Issue"))
                .andExpect(jsonPath("$.content[2].reportable").value(false))
                .andExpect(jsonPath("$.content[2].description").value("C Issue"));
    }

    @Test
    void getAllMainIssues_shouldReturnEmptyPageWhenNoData() throws Exception {
        // given - No data (BeforeEach ensures clean state)

        // when
        this.mockMvc.perform(get("/api/main-issue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.empty").value(true));
    }

    @Test
    void getAllMainIssues_shouldUseDefaultPaginationParameters() throws Exception {
        // given - Create 30 main issues (more than default page size of 20)
        for (int i = 1; i <= 30; i++) {
            dataFixture.mainIssue.givenMainIssue("Issue " + i, true);
        }

        // when - No pagination parameters provided (should use defaults)
        this.mockMvc.perform(get("/api/main-issue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then - Should return first page with default size 20
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(20)) // Default size
                .andExpect(jsonPath("$.number").value(0)) // First page
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.totalElements").value(30))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getAllMainIssues_shouldHandleCustomPageSize() throws Exception {
        // given
        for (int i = 1; i <= 15; i++) {
            dataFixture.mainIssue.givenMainIssue("Issue " + i, true);
        }

        // when - Request custom page size of 5
        this.mockMvc.perform(get("/api/main-issue")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));
    }
}
