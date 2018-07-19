package com.cabinet.rs485.rs485.tool;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * A request dispatch queue with a thread pool of dispatchers.
 *
 * <p>Calling {@link #add(Request)} will enqueue the given Request for dispatch, resolving from
 * either cache or network on a worker thread, and then delivering a parsed response on the main
 * thread.
 */
public class RequestQueue {

  /** The queue of requests that are actually going out to the network. */
  private final PriorityBlockingQueue<Request<?>> mRS485Queue = new PriorityBlockingQueue<>();

  /** rs485 interface for performing requests. */
  private final BasicRS485 mBasicRS485;

  /** Response delivery mechanism. */
  private final ResponseDelivery mDelivery;

  /** The rs485 dispatchers. */
  private RS485Dispatcher mDispatcher;

  /**
   * Creates the worker pool. Processing will not begin until {@link #start()} is called.
   *
   * @param basicRS485 A RS485 interface for performing RS485 requests
   * @param delivery A ResponseDelivery interface for posting responses and errors
   */
  public RequestQueue(BasicRS485 basicRS485, ResponseDelivery delivery) {
    mBasicRS485 = basicRS485;
    mDelivery = delivery;
  }

  /**
   * Creates the worker pool. Processing will not begin until {@link #start()} is called.
   *
   * @param basicRS485 A RS485 interface for performing RS485 requests
   */
  public RequestQueue(BasicRS485 basicRS485) {
    this(basicRS485, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
  }

  /** Starts the dispatchers in this queue. */
  public void start() {
    stop(); // Make sure any currently running dispatchers are stopped.

    // Create network dispatchers (and corresponding threads) up to the pool size.
    mDispatcher = new RS485Dispatcher(mRS485Queue, mBasicRS485, mDelivery);
    mDispatcher.start();
  }

  /** Stops the cache and network dispatchers. */
  public void stop() {
    if (mDispatcher != null) {
      mDispatcher.quit();
    }
  }

  /**
   * A simple predicate or filter interface for Requests, for use by {@link
   * RequestQueue#cancelAll(RequestFilter)}.
   */
  public interface RequestFilter {
    boolean apply(Request<?> request);
  }

  /**
   * Cancels all requests in this queue for which the given filter applies.
   *
   * @param filter The filtering function to use
   */
  public void cancelAll(RequestFilter filter) {
    synchronized (mRS485Queue) {
      for (Request<?> request : mRS485Queue) {
        if (filter.apply(request)) {
          request.cancel();
        }
      }
    }
  }

  /**
   * Cancels all requests in this queue with the given tag. Tag must be non-null and equality is
   * by identity.
   */
  public void cancelAll(final Object tag) {
    if (tag == null) {
      throw new IllegalArgumentException("Cannot cancelAll with a null tag");
    }
    cancelAll(new RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return request.getTag() == tag;
      }
    });
  }

  /**
   * Adds a Request to the dispatch queue.
   *
   * @param request The request to service
   * @return The passed-in request
   */
  public <T> Request<T> add(Request<T> request) {
    // If the request is uncacheable, skip the cache queue and go straight to the network.
    mRS485Queue.add(request);
    return request;
  }
}
