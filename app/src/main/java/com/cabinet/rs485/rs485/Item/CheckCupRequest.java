package com.cabinet.rs485.rs485.Item;


import com.cabinet.rs485.rs485.Item.data.CheckCupResponse;
import com.cabinet.rs485.rs485.protocol.Packet;
import com.cabinet.rs485.rs485.tool.RS485Response;
import com.cabinet.rs485.rs485.tool.Request;
import com.cabinet.rs485.rs485.tool.Response;

/**
 * JIEGUI license
 * Created by zuguo.yu on 2018/7/14.
 */

public class CheckCupRequest extends Request<CheckCupResponse> {

  public CheckCupRequest(Response.Listener<CheckCupResponse> listener) {
    super(createPacket(), listener);
  }

  @Override
  protected Response<CheckCupResponse> parseNetworkResponse(RS485Response response) {
    CheckCupResponse checkCupResponse = new CheckCupResponse(response.getPayload());
    return Response.success(checkCupResponse);
  }

  private static Packet createPacket() {
    // TODO implement
    return null;
  }
}
