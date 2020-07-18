package com.facebook.react.bridge.queue;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MessageQueueThreadHandler
  extends Handler
{
  private final QueueThreadExceptionHandler mExceptionHandler;
  
  public MessageQueueThreadHandler(Looper paramLooper, QueueThreadExceptionHandler paramQueueThreadExceptionHandler)
  {
    super(paramLooper);
    mExceptionHandler = paramQueueThreadExceptionHandler;
  }
  
  public void dispatchMessage(Message paramMessage)
  {
    try
    {
      super.dispatchMessage(paramMessage);
      return;
    }
    catch (Exception paramMessage)
    {
      mExceptionHandler.handleException(paramMessage);
    }
  }
}
