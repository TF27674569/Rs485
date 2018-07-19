package com.cabinet.rs485.rs485.Item;


import com.cabinet.rs485.rs485.Item.data.DataLineResponse;
import com.cabinet.rs485.rs485.protocol.Packet;
import com.cabinet.rs485.rs485.tool.RS485Response;
import com.cabinet.rs485.rs485.tool.Request;
import com.cabinet.rs485.rs485.tool.Response;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2018/7/17
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class DataLineRequest extends Request<DataLineResponse> {
    public DataLineRequest(Response.Listener<DataLineResponse> listener) {
        super(createPacket(), listener);
    }

    private static Packet createPacket() {
        Packet packet = new Packet((byte) 0x02, (byte) 0x01, true, new byte[]{0x01}, (byte) 0x01);
        return packet;
    }

    @Override
    protected Response<DataLineResponse> parseNetworkResponse(RS485Response response) {
        byte[] payload = response.getPayload();
        return Response.success(new DataLineResponse(payload));
    }
}
