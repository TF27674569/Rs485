package com.cabinet.rs485.rs485.tool;

import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;


import com.cabinet.rs485.rs485.protocol.Packet;
import com.cabinet.rs485.rs485.tool.error.RS485Error;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

/**
 * JIEGUI license
 * Created by kerner on 7/9/18.
 */

public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * An opaque token tagging this request; used for bulk cancellation.
     */
    private Object mTag;

    /**
     * Lock to guard state which can be mutated after a request is added to the queue.
     */
    private final Object mLock = new Object();

    /**
     * Whether or not this request has been canceled.
     */
    @GuardedBy("mLock")
    private boolean mCanceled = false;

    private Packet mPacket;

    /**
     * The retry policy for this request.
     */
    private DefaultRetryPolicy mRetryPolicy;

    private Response.Listener<T> mListener;

    private CH34xUARTDriver mDevice;

    public Request(Packet packet, Response.Listener<T> listener) {
        mPacket = packet;
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy());
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    public void cancel() {
        synchronized (mLock) {
            if (mListener != null) {
                mListener = null;
            }
            mCanceled = true;
        }
    }

    /**
     * Returns true if this request has been canceled.
     */
    public boolean isCanceled() {
        synchronized (mLock) {
            return mCanceled;
        }
    }

    @Override
    public int compareTo(@NonNull Request<T> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();
        return right.ordinal() - left.ordinal();
    }

    public void deliverResponse(T result) {
        if (mListener != null) {
            mListener.onResponse(result);
        }
    }

    public void deliverError(RS485Error error) {
        if (mListener != null) {
            mListener.onErrorResponse(error);
        }
    }

    /**
     * Priority values. Requests will be processed from higher priorities to lower priorities, in
     * FIFO order.
     */
    public enum Priority {
        LOW, NORMAL, HIGH, IMMEDIATE
    }

    /**
     * Returns the {@link Priority} of this request; {@link Priority#NORMAL} by default.
     */
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public byte[] getFrame() {
        return mPacket.getFrame();
    }

    public void retry() {
        mPacket = new Packet(mPacket, true);
    }

    /**
     * Sets the retry policy for this request.
     *
     * @return This Request object to allow for chaining.
     */
    public void setRetryPolicy(DefaultRetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
    }

    public final int getTimeoutMs() {
        return getRetryPolicy().getCurrentTimeout();
    }

    /**
     * Returns the retry policy that should be used for this request.
     */
    public DefaultRetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    public Packet getPacket() {
        return mPacket;
    }

    public CH34xUARTDriver getDevice() {
        return mDevice;
    }

    public void setDevice(CH34xUARTDriver device) {
        this.mDevice = device;
    }

    protected abstract Response<T> parseNetworkResponse(RS485Response response);
}
