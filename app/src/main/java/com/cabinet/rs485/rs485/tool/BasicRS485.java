package com.cabinet.rs485.rs485.tool;

import android.os.SystemClock;

import com.cabinet.rs485.rs485.protocol.Packet;
import com.cabinet.rs485.rs485.tool.error.PacketAddressError;
import com.cabinet.rs485.rs485.tool.error.PacketChecksumError;
import com.cabinet.rs485.rs485.tool.error.PacketFunctionCodeError;
import com.cabinet.rs485.rs485.tool.error.RS485Error;


/**
 * JIEGUI license
 * Created by kerner on 7/10/18.
 */

public class BasicRS485 {

  private RS485Stack mRS485Stack;

  public BasicRS485(RS485Stack rs485Stack) {
    this.mRS485Stack = rs485Stack;
  }

  public RS485Response performRequest(Request<?> request) throws RS485Error {
    long requestStart = SystemClock.elapsedRealtime();
    while (true) {
      try {
        byte[] frame = mRS485Stack.performRequest(request);
        if (!Packet.isValidPacket(frame)) {
          throw new PacketChecksumError(
              "checksum error: " + String.format("0x%02x%02x", frame[frame.length - 2],
                  frame[frame.length - 1]));
        }

        Packet packet = new Packet(frame);
        // address
        byte requestAddress = request.getPacket().getAddress();
        if (packet.getAddress() != requestAddress) {
          throw new PacketAddressError(
              "address error(" + String.format("%02x", requestAddress) + "): "
                  + String.format("%02x", packet.getAddress()));
        }
        // functionCode
        byte requestFunctionCode = request.getPacket().getFunctionCode();
        if (packet.getFunctionCode() != requestFunctionCode) {
          throw new PacketFunctionCodeError(
              "function code error(" + String.format("%02x", requestFunctionCode)
                  + "): "
                  + String.format("%02x", packet.getFunctionCode()));
        }
        return new RS485Response(packet, SystemClock.elapsedRealtime() - requestStart);
      } catch (InterruptedException e) {
        throw new RS485Error(e.getMessage());
      } catch (RS485Error e) {
        // while error, retry
        attemptRetryOnException(request, e);
      }
    }
  }

  private static void attemptRetryOnException(Request<?> request, RS485Error exception)
      throws RS485Error {
    DefaultRetryPolicy retryPolicy = request.getRetryPolicy();
    retryPolicy.retry(exception);
    // retry to line, need request to retry, just update a packet
    request.retry();
  }
}
