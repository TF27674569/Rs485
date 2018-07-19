package com.cabinet.rs485.rs485.common;

/**
 * JIEGUI license
 * Created by zuguo.yu on 2018/7/14.
 */

public enum RS485DeviceAddress {

  NONE(0xFF, "NONE");

  private final byte address;
  private final String desc;

  RS485DeviceAddress(int address, String desc) {
    this.address = (byte) address;
    this.desc = desc;
  }

  public byte getAddress() {
    return address;
  }

  @Override
  public String toString() {
    return desc + "[ " + String.format("0x%02x", address);
  }
}
