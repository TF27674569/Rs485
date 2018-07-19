package com.cabinet.rs485.rs485.Item.data;

/**
 * Created by THINK on 2018/7/14.
 */

public class CheckCupResponse {
    private final boolean exist;

    public CheckCupResponse(byte[] data) {
        exist = (data[0] == 0x01);
    }

    public boolean isExist() {
        return exist;
    }
}
