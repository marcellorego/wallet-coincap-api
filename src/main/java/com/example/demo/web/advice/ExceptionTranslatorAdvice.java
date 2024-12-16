package com.example.demo.web.advice;

import com.example.demo.exception.KeyBasedException;
import com.example.demo.exception.OffendingValue;
import com.example.demo.exception.ValidationException;
import com.example.demo.web.exception.InternalServerErrorException;
import com.example.demo.web.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * This class is responsible for custom error message handling for REST API
 */

@Slf4j
@RestControllerAdvice
public class ExceptionTranslatorAdvice extends ResponseEntityExceptionHandler {

    public static final String ERROR_RUNTIME_EXCEPTION = "error.runtime.exception";

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode status,
                                                             WebRequest request) {

        final HttpStatus httpStatus =
            Optional.ofNullable(HttpStatus.resolve(status.value()))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        final InternalServerErrorException serverError =
                new InternalServerErrorException(ex.getMessage(), ex);

        return super.handleExceptionInternal(ex, serverError, headers, httpStatus, request);
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,
                                                                final HttpHeaders headers,
                                                                final HttpStatusCode status,
                                                                final WebRequest request) {

        final String defaultMessage = "No handler found for " + ex.getHttpMethod()
            + " " + ex.getRequestURL() + "\n" + ex.getLocalizedMessage();

        final String path = getRequestUrl(request);

        final OffendingValue offendingValue = OffendingValue.builder()
                .message(defaultMessage)
                .field("handler")
                .invalidValue(path)
                .build();

        final ClientError clientError = ClientError.builder()
                .key(NotFoundException.NOT_FOUND_KEY)
                .message(ex.getMessage())
                .offendingValues(Set.of(offendingValue))
                .path(getRequestUrl(request))
                .build();

        return new ResponseEntity<>(clientError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ClientError> handleRuntimeException(final HttpServletRequest request,
                                                              final RuntimeException ex) {

        final String path = request.getRequestURI();
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        final ClientError clientError = ClientError.builder()
                .key(ERROR_RUNTIME_EXCEPTION)
                .message(ex.getMessage())
                .path(path)
                .build();

        return new ResponseEntity<>(clientError, httpStatus);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ClientError> handleHttpStatusCodeException(final HttpServletRequest request,
                                                                     final HttpStatusCodeException ex) {
        final String path = request.getRequestURI();
        final HttpStatus httpStatus = Optional.ofNullable(HttpStatus.resolve(ex.getStatusCode().value()))
                .orElse(HttpStatus.BAD_REQUEST);
        final ClientError clientError = ClientError.builder()
                .key(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .path(path)
                .build();
        return new ResponseEntity<>(clientError, httpStatus);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ClientError> handleValidationException(final ValidationException ex,
                                                                 final HttpServletRequest request) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ClientError clientError = ClientError.builder()
                .key(ex.getKey())
                .message(ex.getMessage())
                .offendingValues(ex.getOffendingValues())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(clientError, httpStatus);
    }

    @ExceptionHandler(KeyBasedException.class)
    public ResponseEntity<ClientError> handleAppException(final KeyBasedException ex,
                                                          final HttpServletRequest request) {
        final HttpStatus httpStatus = findResponseStatus(ex.getClass());
        final ClientError clientError = ClientError.builder()
                .key(ex.getKey())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(clientError, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ClientError> handleException(final Exception ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ClientError clientError = ClientError.builder()
                .key(InternalServerErrorException.SERVER_ERROR_KEY)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(clientError, httpStatus);
    }

    private HttpStatus findResponseStatus(final Class<? extends Exception> exceptionClass) {

        HttpStatus result = HttpStatus.INTERNAL_SERVER_ERROR;

        final Annotation[] annotations = exceptionClass.getAnnotations();
        for (final Annotation annotation : annotations) {
            if (annotation instanceof ResponseStatus responseStatus) {
                result = responseStatus.value();
                break;
            }
        }

        return result;
    }

    private String getRequestUrl(final RequestAttributes request) {
        String path = "";
        if (Objects.nonNull(request) && request instanceof ServletWebRequest servletWebRequest) {
            path = servletWebRequest.getRequest().getRequestURI();
        }
        return path;
    }
}