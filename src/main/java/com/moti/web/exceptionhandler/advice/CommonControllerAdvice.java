package com.moti.web.exceptionhandler.advice;

import com.moti.web.exceptionhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class CommonControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult methodExHandle(MethodArgumentNotValidException e) {
        log.error("[exceptionHandle] exception", e);

        ErrorResult errorResult = new ErrorResult("400", "잘못된 요청입니다.");
        for (FieldError fieldError : e.getFieldErrors()) {
            errorResult.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errorResult;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] exception", e);
        return new ErrorResult("500", "서버 오류");
    }

}
