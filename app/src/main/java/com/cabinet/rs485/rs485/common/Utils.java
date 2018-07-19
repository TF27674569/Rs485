package com.cabinet.rs485.rs485.common;

/**
 * JIEGUI license
 * Created by kerner on 7/10/18.
 */

public class Utils {
  public static byte[] concat(byte[]... arrays) {
    // Determine the length of the result array
    int totalLength = 0;
    for (byte[] array : arrays) {
      totalLength += array.length;
    }

    // create the result array
    byte[] result = new byte[totalLength];

    // copy the source arrays into the result array
    int currentIndex = 0;
    for (byte[] array : arrays) {
      System.arraycopy(array, 0, result, currentIndex, array.length);
      currentIndex += array.length;
    }
    return result;
  }
}
