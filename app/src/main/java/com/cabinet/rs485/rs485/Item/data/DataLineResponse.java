package com.cabinet.rs485.rs485.Item.data;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2018/7/17
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class DataLineResponse {
    boolean isSuccess;

    public DataLineResponse(byte[] payload) {
        isSuccess = payload[0] == 0x01;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
