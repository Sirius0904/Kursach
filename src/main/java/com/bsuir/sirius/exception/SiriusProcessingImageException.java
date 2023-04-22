package com.bsuir.sirius.exception;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
public class SiriusProcessingImageException extends Exception{
    private String title;
    public SiriusProcessingImageException() {
        super();
    }

    public SiriusProcessingImageException(String title, String message) {
        super(message);
        this.title = title;
    }

    public SiriusProcessingImageException(String message) {
        super(message);
    }

    public SiriusProcessingImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SiriusProcessingImageException(Throwable cause) {
        super(cause);
    }
}
