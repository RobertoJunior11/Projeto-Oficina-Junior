package com.impacta.oficina.configuration;

import com.impacta.oficina.exceptions.OficinaException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.impacta.oficina.exceptions.ExceptionResolver.getRootException;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.slf4j.MDC.get;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class ControlExceptionHandler {

    public static final String X_RD_TRACEID = "X-rd-traceid";
    public static final String CONSTRAINT_VALIDATION_FAILED = "Constraint validation failed";
    public static final String TRACE_ID_KEY = "traceId";


    @ExceptionHandler(value = {OficinaException.class})
    protected ResponseEntity<Object> handleConflict(OficinaException ex, WebRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode())
                .headers(responseHeaders)
                .body(ex.getOnlyBody());
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Object> handleException(Throwable eThrowable) {
        if (eThrowable instanceof ResponseStatusException) {
            return status(((ResponseStatusException) eThrowable).getStatusCode()).body(((ResponseStatusException) eThrowable).getBody().getDetail());
        }

        OficinaException ex = OficinaException.builder()
                .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ofNullable(eThrowable.getMessage()).orElse(eThrowable.toString()))
                .description(getRootException(eThrowable))
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode())
                .headers(responseHeaders)
                .body(ex.getOnlyBody());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exMethod,
                                                                   WebRequest request) {
        var error = exMethod.getName() + " should be " + of(exMethod)
                .map(MethodArgumentTypeMismatchException::getRequiredType).map(Class::getName).orElse(null);

        OficinaException ex = OficinaException.builder()
                .httpStatusCode(BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(error)
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode())
                .headers(responseHeaders)
                .body(ex.getOnlyBody());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exMethod, WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : exMethod.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
                    + violation.getMessage());
        }

        OficinaException ex = OficinaException.builder()
                .httpStatusCode(BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(errors.toString())
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode())
                .headers(responseHeaders)
                .body(ex.getOnlyBody());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validationError(MethodArgumentNotValidException exMethod) {

        BindingResult bindingResult = exMethod.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> fieldErrorDtos = fieldErrors.stream()
                .map(f -> f.getField().concat(":").concat(f.getDefaultMessage())).map(String::new)
                .collect(Collectors.toList());

        OficinaException ex = OficinaException.builder()
                .httpStatusCode(BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(fieldErrorDtos.toString())
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> validationError(HttpMessageNotReadableException exMethod) {
        Throwable mostSpecificCause = exMethod.getMostSpecificCause();

        OficinaException ex = OficinaException.builder()
                .httpStatusCode(BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(mostSpecificCause.getMessage())
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode())
                .headers(responseHeaders)
                .body(ex.getOnlyBody());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<Object> handleException(MissingServletRequestParameterException e) {

        OficinaException ex = OficinaException.builder()
                .httpStatusCode(BAD_REQUEST)
                .message(ofNullable(e.getMessage()).orElse(e.toString()))
                .description(getRootException(e))
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode())
                .headers(responseHeaders)
                .body(ex.getOnlyBody());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException e) {

        OficinaException ex = OficinaException.builder()
                .httpStatusCode(METHOD_NOT_ALLOWED)
                .message(ofNullable(e.getMessage()).orElse(e.toString()))
                .description(getRootException(e))
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return status(ex.getHttpStatusCode())
                .headers(responseHeaders)
                .body(ex.getOnlyBody());
    }

    private String getTraceID() {
        return ofNullable(get(TRACE_ID_KEY)).orElse("not available");
    }
}
