package com.impacta.oficina.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class OficinaException extends RuntimeException {

    private static final long serialVersionUID = -1811148676250520727L;

    private static final String DEFAULT_MESSAGE = "Dado ja existe no sistema";

    @JsonIgnore
    private HttpStatus httpStatusCode;

    private String message;
    private String description;

    public OficinaException() {
        this.httpStatusCode = BAD_REQUEST;
        this.message = DEFAULT_MESSAGE;
        this.description = DEFAULT_MESSAGE;
    }

    public OficinaExceptionBody getOnlyBody() {
        return OficinaExceptionBody.builder()
                .message(this.message)
                .description(this.description)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(NON_NULL)
    public static class OficinaExceptionBody {
        private String message;
        private String description;
    }
}
