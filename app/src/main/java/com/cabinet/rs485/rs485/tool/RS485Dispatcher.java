package com.cabinet.rs485.rs485.tool;

import android.os.Process;
import android.os.SystemClock;


import com.cabinet.rs485.rs485.tool.error.RS485Error;

import java.util.concurrent.BlockingQueue;

/**
 * JIEGUI license
 * Created by kerner on 7/11/18.
 */

public class RS485Dispatcher extends Thread {

  /**
   * The queue of requests to service.
   */
  private final BlockingQueue<Request<?>> mQueue;
  /**
   * The basic interface for processing requests.
   */
  private final BasicRS485 mBasicRS485;
  /**
   * For posting responses and errors.
   */
  private final ResponseDelivery mDelivery;
  /**
   * Used for telling us to die.
   */
  private volatile boolean mQuit = false;

  public RS485Dispatcher(BlockingQueue<Request<?>> queue, BasicRS485 basicRS485,
      ResponseDelivery delivery) {
    mQueue = queue;
    mBasicRS485 = basicRS485;
    mDelivery = delivery;
  }

  public void quit() {
    mQuit = true;
    interrupt();
  }

  @Override
  public void run() {
    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    while (true) {
      try {
        processRequest();
      } catch (InterruptedException e) {
        // We may have been interrupted because it was time to quit.
        if (mQuit) {
          Thread.currentThread().interrupt();
          return;
        }
      }
    }
  }

  private void processRequest() throws InterruptedException {
    // Take a request from the queue.
    Request<?> request = mQueue.take();
    processRequest(request);
  }

  private void processRequest(Request<?> request) {
    long startTimeMs = SystemClock.elapsedRealtime();
    try {
      // If the request was cancelled already, do not perform the
      // rs485 request.
      if (request.isCanceled()) {
        return;
      }

      // Perform the rs485 request.
      RS485Response rs485Response = mBasicRS485.performRequest(request);

      // Parse the response here on the worker thread.
      Response<?> response = request.parseNetworkResponse(rs485Response);

      mDelivery.postResponse(request, response);
    } catch (RS485Error error) {
      error.setRS485TimeMs(SystemClock.elapsedRealtime() - startTimeMs);
      mDelivery.postError(request, error);
    }
  }
}
