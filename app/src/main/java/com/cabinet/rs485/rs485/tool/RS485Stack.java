package com.cabinet.rs485.rs485.tool;


import com.cabinet.rs485.rs485.tool.error.RS485Error;

/**
 * JIEGUI license
 * Created by kerner on 7/11/18.
 */

public interface RS485Stack {
  byte[] performRequest(Request<?> request) throws RS485Error, InterruptedException;
}
