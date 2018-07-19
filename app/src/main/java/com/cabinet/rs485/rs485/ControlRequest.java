package com.cabinet.rs485.rs485;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Looper;

import com.cabinet.rs485.rs485.Item.CheckCupRequest;
import com.cabinet.rs485.rs485.Item.DataLineRequest;
import com.cabinet.rs485.rs485.Item.data.CheckCupResponse;
import com.cabinet.rs485.rs485.Item.data.DataLineResponse;
import com.cabinet.rs485.rs485.tool.BasicRS485;
import com.cabinet.rs485.rs485.tool.CH34xStack;
import com.cabinet.rs485.rs485.tool.RequestQueue;
import com.cabinet.rs485.rs485.tool.Response;
import com.cabinet.rs485.rs485.tool.error.RS485Error;
import com.cabinet.rs485.rs485.tool.error.RS485NoConnectionError;
import com.cabinet.rs485.rs485.tool.error.RS485NotSupportError;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;


/**
 * JIEGUI license
 * Created by kerner on 7/9/18.
 */

public class ControlRequest {

    private static ControlRequest instance;

    public static ControlRequest getInstance() {
        if (instance == null) {
            final BasicRS485 basicRS485 = new BasicRS485(new CH34xStack());
            RequestQueue requestQueue = new RequestQueue(basicRS485);
            instance = new ControlRequest(requestQueue);
        }
        return instance;
    }

    private RequestQueue mRequestQueue;
    private CH34xUARTDriver mDevice;

    private ControlRequest(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        // request queue start
        mRequestQueue.start();
    }

    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";

    public void initCH34x(Context context) throws RS485Error {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Must be invoked from the main thread.");
        }

        // close device
        if (mDevice != null) {
            mDevice.CloseDevice();
        }

        // create device
        mDevice = new CH34xUARTDriver(
                (UsbManager) context.getApplicationContext().getSystemService(Context.USB_SERVICE),
                context.getApplicationContext(), ACTION_USB_PERMISSION);

        // connect
        if (mDevice.isConnected()) {
            throw new RS485NoConnectionError("CH34x is connected");
        }

        // support
        if (!mDevice.UsbFeatureSupported()) {
            throw new RS485NotSupportError("CH34x is not supported");
        }

        // resume
        int rectal = mDevice.ResumeUsbList();
        if (rectal == -1) {
            throw new RS485Error("CH34x resume usb list error");
        }

        if (rectal != 0) {
            throw new RS485Error("CH34x resume usb list error, rectal: " + rectal);
        }
        // init
        boolean uartInit = false;
        try {
            uartInit = mDevice.UartInit();
        } catch (Exception e) {
            throw new RS485Error("CH34x uart init error", e);
        }

        if (!uartInit) {
            throw new RS485Error("CH34x uart init false error");
        }

        // config
        final int BAUD_RATE = 115200;
        final byte DATA_BIT = 8;
        final byte STOP_BIT = 1;
        final byte PARITY = 0; // NONE
        final byte FLOW_CONTROL = 0; // NONE
        mDevice.SetConfig(BAUD_RATE, DATA_BIT, STOP_BIT, PARITY, FLOW_CONTROL);
    }

    public CheckCupRequest checkCupRequest(Response.Listener<CheckCupResponse> listener) {
        if (mDevice == null) {
            throw new IllegalStateException("Must be call \"initCH34x\"");
        }
        CheckCupRequest request = new CheckCupRequest(listener);
        // update device, maybe device re-opened
        request.setDevice(mDevice);
        mRequestQueue.add(request);
        return request;
    }

    public DataLineRequest dataLineRequest(Response.Listener<DataLineResponse> listener) {
        if (mDevice == null) {
            throw new IllegalStateException("Must be call \"initCH34x\"");
        }
        DataLineRequest request = new DataLineRequest(listener);
        // update device, maybe device re-opened
        request.setDevice(mDevice);
        mRequestQueue.add(request);
        return request;
    }

}
