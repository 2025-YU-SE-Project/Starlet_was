package com.example.starlet_be.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외처리
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> customExceptionHandler(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorDto errorDto = new ErrorDto(errorCode.getStatus(), errorCode.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(errorCode.getStatus()));
    }

    // 일반 예외처리
    @ExceptionHandler
    protected ResponseEntity<?> customServerException(Exception e){
        log.error("INTERNAL_SERVER_ERROR", e);
        ErrorDto errorDto = new ErrorDto(ErrorCode.INTERNAL_SERVER_ERROR.getStatus(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 메소드 인자 타당성 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for(FieldError error : e.getBindingResult().getFieldErrors())
            errors.put(error.getField(), error.getDefaultMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // @RequestParam 누락
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorDto> handleMissingParam(
            org.springframework.web.bind.MissingServletRequestParameterException ex) {
        String msg = ex.getParameterName() + " 파라미터는 필수입니다.";
        return ResponseEntity.badRequest().body(new ErrorDto(400, msg));
    }

    // 타입/형식 변환 실패
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorDto> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {

        Throwable cause = ex.getCause();
        if (cause instanceof java.time.format.DateTimeParseException) {
            return ResponseEntity.badRequest().body(new ErrorDto(400, "날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)"));
        }

        String name = ex.getName(); // year, month 등 파라미터 이름
        return ResponseEntity.badRequest().body(new ErrorDto(400, name + " 파라미터 형식이 올바르지 않습니다."));
    }


    // 이미지와 같은 파일을 올릴때 사용할 예외들(현재 비활성화)

//    // RequestPart에 대한 누락
//    @ExceptionHandler(MissingServletRequestPartException.class)
//    protected ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
//        log.error("MissingServletRequestPartException", e);
//        ErrorDto errorDto = new ErrorDto(ErrorCode.REQUIRED_SUBMISSION_INFO.getStatus(), ErrorCode.REQUIRED_SUBMISSION_INFO.getMessage());
//        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
//    }
//
//
//    // Multipart 요청의 형식이 잘못되었을 때 발생하는 예외
//    @ExceptionHandler(MultipartException.class)
//    protected ResponseEntity<?> handleMultipartException(MultipartException e) {
//        log.error("MultipartException", e);
//        ErrorDto errorDto = new ErrorDto(ErrorCode.REQUIRED_SUBMISSION_INFO.getStatus(), ErrorCode.REQUIRED_SUBMISSION_INFO.getMessage());
//        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
//    }

}
