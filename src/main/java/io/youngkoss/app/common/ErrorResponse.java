package io.youngkoss.app.common;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * ErrorResponse builder pattern for errors list
 */
@Data
@Builder
public class ErrorResponse {
   @Singular
   private List<Error> errors;

   @Data
   @Builder
   public static class Error {
      private String code;

      private String description;

      private String details;
   }

   public static enum ErrorCode {
      API_01("Network id value is invalid"), API_00("Unknown new error"); //$NON-NLS-1$//$NON-NLS-2$
      // TODO: make tests and find another type of errors add it here

      private String description;

      private ErrorCode(String description) {
         this.description = description;
      }

      public String getDescription() {
         return description;
      }

      public String getCode() {
         return name();
      }
   }
}
