package com.cabinet.rs485.rs485.tool;


import com.cabinet.rs485.rs485.tool.error.RS485Error;

/**
 * JIEGUI license
 * Created by kerner on 7/10/18.
 */

public class DefaultRetryPolicy {
  /** The current timeout in milliseconds. */
  private int mCurrentTimeoutMs;

  /** The current retry count. */
  private int mCurrentRetryCount;

  /** The maximum number of attempts. */
  private final int mMaxNumRetries;

  /** The backoff multiplier for the policy. */
  private final float mBackoffMultiplier;

  /** The default socket timeout in milliseconds */
  public static final int DEFAULT_TIMEOUT_MS = 1000;

  /** The default number of retries */
  public static final int DEFAULT_MAX_RETRIES = 1;

  /** The default backoff multiplier */
  public static final float DEFAULT_BACKOFF_MULT = 1f;

  /** Constructs a new retry policy using the default timeouts. */
  public DefaultRetryPolicy() {
    this(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
  }

  public DefaultRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
    mCurrentTimeoutMs = initialTimeoutMs;
    mMaxNumRetries = maxNumRetries;
    mBackoffMultiplier = backoffMultiplier;
  }

  /** Returns the current timeout. */
  public int getCurrentTimeout() {
    return mCurrentTimeoutMs;
  }

  /** Returns the current retry count. */
  public int getCurrentRetryCount() {
    return mCurrentRetryCount;
  }

  /** Returns the backoff multiplier for the policy. */
  public float getBackoffMultiplier() {
    return mBackoffMultiplier;
  }

  /**
   * Prepares for the next retry by applying a backoff to the timeout.
   *
   * @param error The error code of the last attempt.
   */
  public void retry(RS485Error error) throws RS485Error {
    mCurrentRetryCount++;
    mCurrentTimeoutMs += (int) (mCurrentTimeoutMs * mBackoffMultiplier);
    if (!hasAttemptRemaining()) {
      throw error;
    }
  }

  /** Returns true if this policy has attempts remaining, false otherwise. */
  protected boolean hasAttemptRemaining() {
    return mCurrentRetryCount <= mMaxNumRetries;
  }
}
