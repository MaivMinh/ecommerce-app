package com.minh.common.exception;

import com.minh.common.constants.ErrorCode;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageCommon messageCommon;

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    protected ResponseEntity<ResponseData> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseData(ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ResponseData> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(BAD_REQUEST).body(new ResponseData(BAD_REQUEST.value(), messageCommon.getMessage(ErrorCode.INVALID_PARAMS), errors));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ResponseData> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ResponseData(BAD_REQUEST.value(), messageCommon.getMessage(ErrorCode.INVALID_PARAMS), ex));
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<ResponseData> handleMissingRequestValueException(MissingRequestValueException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ResponseData(HttpStatus.BAD_REQUEST.value(), messageCommon.getMessage(ErrorCode.INVALID_PARAMS), ex));
    }


    @ExceptionHandler({Exception.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<ResponseData> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(
                        ex.getMessage())
                );
    }

}
