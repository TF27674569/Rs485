/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cabinet.rs485.rs485.tool;


import com.cabinet.rs485.rs485.tool.error.RS485Error;

/**
 * Encapsulates a parsed response for delivery.
 *
 * @param <T> Parsed type of this response
 */
public class Response<T> {

  /** Callback interface for delivering parsed responses. */
  public interface Listener<T> {
    /** Called when a response is received. */
    void onResponse(T response);

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     */
    void onErrorResponse(RS485Error error);
  }

  /** Returns a successful response containing the parsed result. */
  public static <T> Response<T> success(T result) {
    return new Response<>(result);
  }

  /**
   * Returns a failed response containing the given error code and an optional localized message
   * displayed to the user.
   */
  public static <T> Response<T> error(RS485Error error) {
    return new Response<>(error);
  }

  /** Parsed response, or null in the case of error. */
  public final T result;

  /** Detailed error information if <code>errorCode != OK</code>. */
  public final RS485Error error;

  /** Returns whether this response is considered successful. */
  public boolean isSuccess() {
    return error == null;
  }

  private Response(T result) {
    this.result = result;
    this.error = null;
  }

  private Response(RS485Error error) {
    this.result = null;
    this.error = error;
  }
}
