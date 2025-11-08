package com.shuttleverse.connect.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SVApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private String errorCode;

  public static <T> SVApiResponse<T> success(T data) {
    return new SVApiResponse<>(true, "Success", data, null);
  }

  public static <T> SVApiResponse<T> success(String message, T data) {
    return new SVApiResponse<>(true, message, data, null);
  }

  public static <T> SVApiResponse<T> error(String message) {
    return new SVApiResponse<>(false, message, null, "ERROR");
  }

  public static <T> SVApiResponse<T> error(String message, String errorCode) {
    return new SVApiResponse<>(false, message, null, errorCode);
  }
}
