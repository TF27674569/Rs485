package com.cabinet.rs485.rs485.tool;


import com.cabinet.rs485.rs485.protocol.Packet;

/**
 * PerceptIn license
 * Created by kerner on 7/10/18.
 */

public class RS485Response {

  private final Packet mPacket;
  public final long useTimeMs;

  public RS485Response(Packet packet, long useTimeMs) {
    mPacket = packet;
    this.useTimeMs = useTimeMs;
  }

  public byte[] getPayload() {
    return mPacket.getPayload();
  }
}
