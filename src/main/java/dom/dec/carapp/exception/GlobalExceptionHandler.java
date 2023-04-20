package dom.dec.carapp.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> handlerResourceNotFound(ResourceNotFoundException e, WebRequest request) {
        CustomError error = new CustomError(LocalDate.now(), e.getMessage(), request.getDescription(false),
                HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<CustomError>> handleBadRequest(MethodArgumentNotValidException e, WebRequest request) {
        List<CustomError> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            CustomError customError = new CustomError(LocalDate.now(), error.getDefaultMessage(),
                    request.getDescription(false), HttpStatus.BAD_REQUEST.value());
            errors.add(customError);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomError> handlerConstraintError(ConstraintViolationException e, WebRequest request) {
        CustomError error = new CustomError(LocalDate.now(), e.getMessage(), request.getDescription(false),
                HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> handlerInternalServerError(Exception e, WebRequest request) {
        CustomError error = new CustomError(LocalDate.now(), e.getMessage(), request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
