package com.x7ubi.indexcards.service.ai;

import com.x7ubi.indexcards.config.AiProperties;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Validates the user input before it is sent to the LLM. PDFBox is only used to check that the file is a readable,
 * unencrypted PDF and to count its pages; the PDF itself is sent to Gemini unchanged.
 */
@Component
public class CardGenerationInputValidator {

    private static final byte[] PDF_MAGIC = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final AiProperties properties;

    public CardGenerationInputValidator(AiProperties properties) {
        this.properties = properties;
    }

    public String validateNotes(String notes) throws AiGenerationException {
        String stripped = notes == null ? "" : notes.strip();
        if (stripped.length() > properties.maxNotesChars()) {
            throw new AiGenerationException(ErrorMessage.Ai.NOTES_TOO_LONG, HttpStatus.BAD_REQUEST);
        }
        return stripped;
    }

    /**
     * @return the PDF bytes, or null if no file was uploaded
     */
    public byte[] validatePdf(MultipartFile file) throws AiGenerationException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (file.getSize() > properties.maxPdfBytes()) {
            throw new AiGenerationException(ErrorMessage.Ai.PDF_TOO_LARGE, HttpStatus.BAD_REQUEST);
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new AiGenerationException(ErrorMessage.Ai.PDF_INVALID, HttpStatus.BAD_REQUEST);
        }
        if (bytes.length < PDF_MAGIC.length
                || !Arrays.equals(bytes, 0, PDF_MAGIC.length, PDF_MAGIC, 0, PDF_MAGIC.length)) {
            throw new AiGenerationException(ErrorMessage.Ai.PDF_INVALID, HttpStatus.BAD_REQUEST);
        }

        int pages;
        // Password-protected PDFs throw InvalidPasswordException (an IOException) here; Gemini cannot read them either.
        try (PDDocument document = Loader.loadPDF(bytes)) {
            pages = document.getNumberOfPages();
        } catch (IOException e) {
            throw new AiGenerationException(ErrorMessage.Ai.PDF_INVALID, HttpStatus.BAD_REQUEST);
        }
        if (pages > properties.maxPdfPages()) {
            throw new AiGenerationException(ErrorMessage.Ai.PDF_TOO_MANY_PAGES, HttpStatus.BAD_REQUEST);
        }
        return bytes;
    }
}
