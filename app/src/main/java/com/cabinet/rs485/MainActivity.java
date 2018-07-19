package com.cabinet.rs485;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cabinet.rs485.rs485.ControlRequest;
import com.cabinet.rs485.rs485.Item.data.DataLineResponse;
import com.cabinet.rs485.rs485.tool.Response;
import com.cabinet.rs485.rs485.tool.error.RS485Error;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ControlRequest.getInstance().dataLineRequest(new Response.Listener<DataLineResponse>() {
            @Override
            public void onResponse(DataLineResponse response) {
                // on response callback
            }

            @Override
            public void onErrorResponse(RS485Error error) {
                // on error callback
            }
        });
    }
}
