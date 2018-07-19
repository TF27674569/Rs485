package com.cabinet.rs485.rs485.tool.error;

/**
 * PerceptIn license
 * Created by kerner on 7/10/18.
 */

public class RS485Error extends Exception {

  private long mRS485TimeMs;

  public RS485Error(String message) {
    super(message);
  }

  public RS485Error(String message, Throwable e) {
    super(message, e);
  }

  public void setRS485TimeMs(long rs485TimeMs) {
    mRS485TimeMs = rs485TimeMs;
  }

  public RS485Error(Throwable cause) {
    super(cause);
  }

  public long getmRS485TimeMs() {
    return mRS485TimeMs;
  }
}
