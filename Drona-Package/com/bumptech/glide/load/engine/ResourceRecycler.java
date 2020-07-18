package com.bumptech.glide.load.engine;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

class ResourceRecycler
{
  private final Handler handler = new Handler(Looper.getMainLooper(), new ResourceRecyclerCallback());
  private boolean isRecycling;
  
  ResourceRecycler() {}
  
  void recycle(Resource paramResource)
  {
    try
    {
      if (isRecycling)
      {
        handler.obtainMessage(1, paramResource).sendToTarget();
      }
      else
      {
        isRecycling = true;
        paramResource.recycle();
        isRecycling = false;
      }
      return;
    }
    catch (Throwable paramResource)
    {
      throw paramResource;
    }
  }
  
  private static final class ResourceRecyclerCallback
    implements Handler.Callback
  {
    static final int RECYCLE_RESOURCE = 1;
    
    ResourceRecyclerCallback() {}
    
    public boolean handleMessage(Message paramMessage)
    {
      if (what == 1)
      {
        ((Resource)obj).recycle();
        return true;
      }
      return false;
    }
  }
}
