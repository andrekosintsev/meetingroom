package io.youngkoss.app.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.youngkoss.app.common.ErrorResponse.ErrorCode;

/**
 * ControllerAdvice to handle all exceptions
 */
@ControllerAdvice
public class MeetingApplicationExceptionHandler extends ResponseEntityExceptionHandler {

   @ExceptionHandler(value = { Exception.class })
   public ResponseEntity<?> defaultErrorHandler(Exception e) {
      // TODO: here need to handle all POSSIBLE type of exception
      if (e.getCause() instanceof Exception) {
         return ResponseEntity.badRequest()
               .body(ErrorResponse.builder()
                     .error(ErrorResponse.Error.builder()
                           .code(ErrorCode.API_01.getCode())
                           .description(ErrorCode.API_01.getDescription())
                           .build())
                     .build());
      }
      return ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                  .error(ErrorResponse.Error.builder()
                        .code(ErrorCode.API_00.getCode())
                        .description(ErrorCode.API_00.getDescription())
                        .details(e.getMessage()) // FIXME : details should be not stack trace
                        .build())
                  .build());

   }

}
