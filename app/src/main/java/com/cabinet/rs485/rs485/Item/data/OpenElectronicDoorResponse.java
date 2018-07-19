package com.cabinet.rs485.rs485.Item.data;

/**
 * JIEGUI license
 * Created by zuguo.yu on 2018/7/14.
 */

public class OpenElectronicDoorResponse {

  private boolean success;

  public OpenElectronicDoorResponse(byte[] data) {
    this.success = (data[0] == 0x01);
  }

  public boolean isSuccess() {
    return success;
  }
}
