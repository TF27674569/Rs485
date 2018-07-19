package com.cabinet.rs485.rs485.common;

/**
 * JIEGUI license
 * Created by zuguo.yu on 2018/7/14.
 */

public enum DeviceFunctionCode {

  NONE(0xFF, "NONE");

  private final byte functionCode;
  private final String desc;

  DeviceFunctionCode(int functionCode, String desc) {
    this.functionCode = (byte) functionCode;
    this.desc = desc;
  }

  public byte getFunctionCode() {
    return functionCode;
  }

  @Override
  public String toString() {
    return desc + "[ " + String.format("0x%02x", functionCode);
  }
}
