package com.solidos.caia.api.common.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonResponse<T> {

  /**
   * Código de estado HTTP (ej. 200 para éxito, 400 para error de cliente, etc.).
   */
  private int status;

  /**
   * Mensaje de error, si ocurre alguno. Será null si no hay error.
   */
  private String error;

  /**
   * Mensaje informativo o de éxito.
   */
  private String message;

  /**
   * Datos asociados con la respuesta, puede ser de cualquier tipo.
   */
  private T data;

  /**
   * Método de fábrica para una respuesta exitosa.
   */
  public static <T> CommonResponse<T> success(String message) {
    return CommonResponse.<T>builder()
        .status(200) // o cualquier otro código de éxito
        .message(message)
        .build();
  }

  /**
   * Método de fábrica para una respuesta exitosa.
   */
  public static <T> CommonResponse<T> success(T data, String message) {
    return CommonResponse.<T>builder()
        .status(200) // o cualquier otro código de éxito
        .message(message)
        .data(data)
        .build();
  }

  /**
   * Método de fábrica para una respuesta con error.
   */
  public static <T> CommonResponse<T> errorResponse(int status, String error) {
    return CommonResponse.<T>builder()
        .status(status)
        .error(error)
        .build();
  }
}
