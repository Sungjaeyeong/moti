package com.moti.web.exceptionhandler.advice;

import com.moti.web.exception.NotFoundUserException;
import com.moti.web.exception.NotMatchUserException;
import com.moti.web.exceptionhandler.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CommonControllerAdvice {

    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult methodExHandle(MethodArgumentNotValidException e) {
        log.error("[exceptionHandle] exception", e);

        ErrorResult errorResult = ErrorResult.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            errorResult.addValidation(fieldError.getField(), getErrorMessage(fieldError));
        }

        return errorResult;
    }

    public String getErrorMessage(FieldError fieldError) {
        return Arrays.stream(Objects.requireNonNull(fieldError.getCodes()))
                .map(code -> {
                    Object[] arguments = fieldError.getArguments();
                    try {
                        return messageSource.getMessage(code, arguments, null);
                    } catch (NoSuchMessageException e) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .findFirst()
                .orElse(fieldError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    public ErrorResult methodExHandle(NotMatchUserException e) {
        log.error("[exceptionHandle] exception", e);

        return ErrorResult.builder()
                .code("401")
                .message("아이디 또는 비밀번호가 잘못되었습니다.")
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResult foundExHandle(NotFoundUserException e) {
        log.error("[exceptionHandle] exception", e);

        return ErrorResult.builder()
                .code("404")
                .message("존재하지 않는 유저입니다.")
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] exception", e);
        return ErrorResult.builder()
                .code("500")
                .message("서버 오류")
                .build();
    }

}
