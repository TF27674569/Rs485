package com.cabinet.rs485.rs485.Item;


import com.cabinet.rs485.rs485.Item.data.OpenElectronicDoorResponse;
import com.cabinet.rs485.rs485.protocol.Packet;
import com.cabinet.rs485.rs485.tool.RS485Response;
import com.cabinet.rs485.rs485.tool.Request;
import com.cabinet.rs485.rs485.tool.Response;

/**
 * JIEGUI license
 * Created by zuguo.yu on 2018/7/14.
 */

public class OpenElectronicDoorRequest extends Request<OpenElectronicDoorResponse> {

  public OpenElectronicDoorRequest(Response.Listener<OpenElectronicDoorResponse> listener) {
    super(createPacket(), listener);
  }

  private static Packet createPacket() {
    // TODO, implement
    return null;
  }


  @Override
  protected Response<OpenElectronicDoorResponse> parseNetworkResponse(RS485Response response) {
    OpenElectronicDoorResponse openElectronicDoorResponse = new OpenElectronicDoorResponse(
        response.getPayload());
    return Response.success(openElectronicDoorResponse);
  }
}
