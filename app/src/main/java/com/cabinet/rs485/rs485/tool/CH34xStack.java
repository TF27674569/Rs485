package com.cabinet.rs485.rs485.tool;


import com.cabinet.rs485.rs485.protocol.Packet;
import com.cabinet.rs485.rs485.tool.error.PacketOverflowError;
import com.cabinet.rs485.rs485.tool.error.RS485Error;
import com.cabinet.rs485.rs485.tool.error.RS485NoConnectionError;
import com.cabinet.rs485.rs485.tool.error.RS485TimeoutError;
import com.cabinet.rs485.rs485.tool.error.RS485WriteError;

import java.util.Arrays;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

/**
 * JIEGUI license
 * Created by kerner on 7/10/18.
 */

public class CH34xStack implements RS485Stack {

    private static final int RECEIVE_MAX_BUF_SIZE = 256;
    private final byte[] frame = new byte[RECEIVE_MAX_BUF_SIZE];
    private int frameOffset = 0;

    private final byte[] receiveBuffer = new byte[RECEIVE_MAX_BUF_SIZE];

    public CH34xStack() {
    }

    @Override
    public byte[] performRequest(Request<?> request) throws RS485Error, InterruptedException {
        CH34xUARTDriver device = request.getDevice();
        if (!device.isConnected()) {
            // maybe need catch the exception to restart device
            throw new RS485NoConnectionError("RS485 no connection");
        }

        int result = device.WriteData(request.getFrame(), request.getFrame().length);

        if (result < 0) {
            // maybe need catch the exception to restart device
            throw new RS485WriteError("CH34x write error");
        }
        int timeoutMs = request.getTimeoutMs();

        int sleep_count = 0;
        final int sleep_time = 50; // 50ms for wait for receiving next data
        byte pre_c = 0;
        boolean payload_start = false;
        boolean frame_end = false;
        int crc_offset = 0;
        while (true) {
            if (sleep_count >= timeoutMs) {
                throw new RS485TimeoutError("RS485 timeout");
            }
            int length = device.ReadData(receiveBuffer, RECEIVE_MAX_BUF_SIZE);

            if (length <= 0) {
                Thread.sleep(sleep_time);
                sleep_count += sleep_time;
                continue;
            } else {
                // received data, reset wait count
                sleep_count = 0;
                // raw data, to print for debug
            }

            // packet data
            for (int i = 0; i < length; ++i) {
                // frame head
                if (pre_c == Packet.FRAME_HEAD[0] && receiveBuffer[i] == Packet.FRAME_HEAD[1]
                        && !frame_end) {
                    // load frame
                    frame[0] = pre_c;
                    frame[1] = receiveBuffer[i];
                    frameOffset = 2;

                    // frame start
                    payload_start = true;
                    frame_end = false;
                    pre_c = receiveBuffer[i];
                    continue;
                }
                if (payload_start) {
                    if (frameOffset >= RECEIVE_MAX_BUF_SIZE) {
                        throw new PacketOverflowError("[payload] Packet overflow" + frameOffset);
                    }
                    frame[frameOffset++] = receiveBuffer[i];
                }

                if (pre_c == Packet.FRAME_TAIL[0] && receiveBuffer[i] == Packet.FRAME_TAIL[1]) {
                    if (frameOffset >= RECEIVE_MAX_BUF_SIZE) {
                        throw new PacketOverflowError("[tail0] Packet overflow" + frameOffset);
                    }
                    frame[frameOffset++] = pre_c;
                    if (frameOffset >= RECEIVE_MAX_BUF_SIZE) {
                        throw new PacketOverflowError("[tail1] Packet overflow" + frameOffset);
                    }
                    frame[frameOffset++] = receiveBuffer[i];

                    // frame end
                    frame_end = true;
                    payload_start = false;

                    pre_c = receiveBuffer[i];
                    continue;
                }

                if (frame_end) {
                    if (crc_offset++ < 2) {
                        if (frameOffset >= RECEIVE_MAX_BUF_SIZE) {
                            throw new PacketOverflowError(
                                    "[crc" + crc_offset + "] Packet overflow" + frameOffset);
                        }
                        frame[frameOffset++] = receiveBuffer[i];
                    }
                    // crc receiving end
                    if (crc_offset == 2) {
                        return Arrays.copyOf(frame, frameOffset);
                    }
                }

                pre_c = receiveBuffer[i];
            }
        }
    }
}
