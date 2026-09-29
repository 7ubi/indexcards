package com.x7ubi.indexcards.ai;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.service.ai.GeneratedCard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AiRestControllerTest extends AiTestConfig {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtils jwtUtils;

    private MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
    }

    private String bearer(String username) {
        return "Bearer " + this.jwtUtils.generateJwtTokenForUsername(username);
    }

    @Test
    public void generateReturnsCardsTest() throws Exception {
        saveApiKey();
        when(this.cardGenerationClient.generate(any(), any()))
                .thenReturn(List.of(new GeneratedCard("What is $x^2$?", "A square")));

        this.mockMvc.perform(multipart("/api/ai/generate")
                        .file(new MockMultipartFile("file", "n.pdf", "application/pdf",
                                CardGenerationInputValidatorTest.pdf(1, false)))
                        .param("projectId", String.valueOf(this.projects.getFirst().getId()))
                        .param("notes", "Some notes")
                        .param("cardCount", "5")
                        .header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards[0].question").value("What is $x^2$?"));
    }

    @Test
    public void generateWithoutTokenIsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(multipart("/api/ai/generate").param("projectId", "1").param("notes", "x"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void invalidGeminiKeyIsNot401Test() throws Exception {
        doThrow(new AiGenerationException(ErrorMessage.Ai.API_KEY_INVALID, HttpStatus.BAD_REQUEST))
                .when(this.cardGenerationClient).verifyApiKey(any());

        this.mockMvc.perform(put("/api/ai/apiKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"apiKey\":\"" + API_KEY + "\"}")
                        .header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ErrorMessage.Ai.API_KEY_INVALID));
    }

    @Test
    public void saveAndDeleteApiKeyNeverReturnsTheKeyTest() throws Exception {
        String saved = this.mockMvc.perform(put("/api/ai/apiKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"apiKey\":\"" + API_KEY + "\"}")
                        .header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiKeyConfigured").value(true))
                .andExpect(jsonPath("$.apiKeyHint").value("6789"))
                .andReturn().getResponse().getContentAsString();
        Assertions.assertFalse(saved.contains(API_KEY));

        String status = this.mockMvc.perform(get("/api/ai/status").header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andReturn().getResponse().getContentAsString();
        Assertions.assertFalse(status.contains(API_KEY));

        this.mockMvc.perform(delete("/api/ai/apiKey").header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiKeyConfigured").value(false));
    }

    @Test
    public void userResponseDoesNotContainTheKeyTest() throws Exception {
        saveApiKey();

        String user = this.mockMvc.perform(get("/api/user").header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertFalse(user.contains("v1:"));
        Assertions.assertFalse(user.contains("apiKey"));
    }

    @Test
    public void bulkSaveTest() throws Exception {
        this.mockMvc.perform(post("/api/indexCard/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\":" + this.projects.getFirst().getId()
                                + ",\"cards\":[{\"question\":\"Bulk via REST\",\"answer\":\"$\\\\frac{1}{2}$\"}]}")
                        .header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isCreated());

        IndexCard saved = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode("Bulk via REST").array());
        // Same byte encoding as every other create path (IndexCardMapper), including its trailing padding.
        Assertions.assertArrayEquals(StandardCharsets.UTF_8.encode("$\\frac{1}{2}$").array(), saved.getAnswer());
    }
}
