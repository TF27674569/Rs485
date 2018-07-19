package com.cabinet.rs485.rs485.tool.error;

/**
 * PerceptIn license
 * Created by kerner on 7/10/18.
 */

public class PacketOverflowError extends RS485Error {
  public PacketOverflowError(String message) {
    super(message);
  }
}
