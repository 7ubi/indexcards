package com.x7ubi.indexcards.ai;

import com.x7ubi.indexcards.config.AiProperties;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.service.ai.CardGenerationInputValidator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CardGenerationInputValidatorTest {

    // maxNotesChars = 10, maxPdfBytes = 50 000, maxPdfPages = 2
    private final CardGenerationInputValidator validator = new CardGenerationInputValidator(new AiProperties(
            "", "gemini-3.8-flash", "low", 30, 10, 50_000, 2, 120, 1, 0, 16000, ""));

    static byte[] pdf(int pages, boolean encrypted) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (int i = 0; i < pages; i++) {
                document.addPage(new PDPage());
            }
            if (encrypted) {
                StandardProtectionPolicy policy = new StandardProtectionPolicy("owner", "user", new AccessPermission());
                policy.setEncryptionKeyLength(128);
                document.protect(policy);
            }
            document.save(out);
            return out.toByteArray();
        }
    }

    private static MockMultipartFile file(byte[] bytes) {
        return new MockMultipartFile("file", "notes.pdf", "application/pdf", bytes);
    }

    @Test
    public void notesAreStrippedTest() throws AiGenerationException {
        Assertions.assertEquals("abc", validator.validateNotes("  abc \n"));
        Assertions.assertEquals("", validator.validateNotes(null));
    }

    @Test
    public void notesTooLongTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class,
                () -> validator.validateNotes("12345678901"));

        Assertions.assertEquals(ErrorMessage.Ai.NOTES_TOO_LONG, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void noPdfReturnsNullTest() throws AiGenerationException {
        Assertions.assertNull(validator.validatePdf(null));
        Assertions.assertNull(validator.validatePdf(file(new byte[0])));
    }

    @Test
    public void validPdfTest() throws Exception {
        byte[] bytes = pdf(2, false);

        Assertions.assertArrayEquals(bytes, validator.validatePdf(file(bytes)));
    }

    @Test
    public void notAPdfTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class,
                () -> validator.validatePdf(file("hello".getBytes())));

        Assertions.assertEquals(ErrorMessage.Ai.PDF_INVALID, exception.getMessage());
    }

    @Test
    public void tooManyPagesTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class,
                () -> validator.validatePdf(file(pdf(3, false))));

        Assertions.assertEquals(ErrorMessage.Ai.PDF_TOO_MANY_PAGES, exception.getMessage());
    }

    @Test
    public void pdfTooLargeTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class,
                () -> validator.validatePdf(file(new byte[50_001])));

        Assertions.assertEquals(ErrorMessage.Ai.PDF_TOO_LARGE, exception.getMessage());
    }

    @Test
    public void encryptedPdfTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class,
                () -> validator.validatePdf(file(pdf(1, true))));

        Assertions.assertEquals(ErrorMessage.Ai.PDF_INVALID, exception.getMessage());
    }
}
