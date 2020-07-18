package com.facebook.drawee.controller;

import android.graphics.drawable.Animatable;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ForwardingControllerListener<INFO>
  implements ControllerListener<INFO>
{
  private static final String PAGE_KEY = "FdingControllerListener";
  private final List<ControllerListener<? super INFO>> mListeners = new ArrayList(2);
  
  public ForwardingControllerListener() {}
  
  public static ForwardingControllerListener create()
  {
    return new ForwardingControllerListener();
  }
  
  public static ForwardingControllerListener merge0(ControllerListener paramControllerListener)
  {
    ForwardingControllerListener localForwardingControllerListener = create();
    localForwardingControllerListener.addListener(paramControllerListener);
    return localForwardingControllerListener;
  }
  
  public static ForwardingControllerListener merge0(ControllerListener paramControllerListener1, ControllerListener paramControllerListener2)
  {
    ForwardingControllerListener localForwardingControllerListener = create();
    localForwardingControllerListener.addListener(paramControllerListener1);
    localForwardingControllerListener.addListener(paramControllerListener2);
    return localForwardingControllerListener;
  }
  
  private void onException(String paramString, Throwable paramThrowable)
  {
    try
    {
      Log.e("FdingControllerListener", paramString, paramThrowable);
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void addListener(ControllerListener paramControllerListener)
  {
    try
    {
      mListeners.add(paramControllerListener);
      return;
    }
    catch (Throwable paramControllerListener)
    {
      throw paramControllerListener;
    }
  }
  
  public void clearListeners()
  {
    try
    {
      mListeners.clear();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void onFailure(String paramString, Throwable paramThrowable)
  {
    try
    {
      int j = mListeners.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = mListeners;
        try
        {
          localObject = ((List)localObject).get(i);
          localObject = (ControllerListener)localObject;
          if (localObject != null) {
            ((ControllerListener)localObject).onFailure(paramString, paramThrowable);
          }
        }
        catch (Exception localException)
        {
          onException("InternalListener exception in onFailure", localException);
        }
        i += 1;
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void onFinalImageSet(String paramString, Object paramObject, Animatable paramAnimatable)
  {
    try
    {
      int j = mListeners.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = mListeners;
        try
        {
          localObject = ((List)localObject).get(i);
          localObject = (ControllerListener)localObject;
          if (localObject != null) {
            ((ControllerListener)localObject).onFinalImageSet(paramString, paramObject, paramAnimatable);
          }
        }
        catch (Exception localException)
        {
          onException("InternalListener exception in onFinalImageSet", localException);
        }
        i += 1;
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void onIntermediateImageFailed(String paramString, Throwable paramThrowable)
  {
    int j = mListeners.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = mListeners;
      try
      {
        localObject = ((List)localObject).get(i);
        localObject = (ControllerListener)localObject;
        if (localObject != null) {
          ((ControllerListener)localObject).onIntermediateImageFailed(paramString, paramThrowable);
        }
      }
      catch (Exception localException)
      {
        onException("InternalListener exception in onIntermediateImageFailed", localException);
      }
      i += 1;
    }
  }
  
  public void onIntermediateImageSet(String paramString, Object paramObject)
  {
    int j = mListeners.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = mListeners;
      try
      {
        localObject = ((List)localObject).get(i);
        localObject = (ControllerListener)localObject;
        if (localObject != null) {
          ((ControllerListener)localObject).onIntermediateImageSet(paramString, paramObject);
        }
      }
      catch (Exception localException)
      {
        onException("InternalListener exception in onIntermediateImageSet", localException);
      }
      i += 1;
    }
  }
  
  public void onRelease(String paramString)
  {
    try
    {
      int j = mListeners.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = mListeners;
        try
        {
          localObject = ((List)localObject).get(i);
          localObject = (ControllerListener)localObject;
          if (localObject != null) {
            ((ControllerListener)localObject).onRelease(paramString);
          }
        }
        catch (Exception localException)
        {
          onException("InternalListener exception in onRelease", localException);
        }
        i += 1;
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void onSubmit(String paramString, Object paramObject)
  {
    try
    {
      int j = mListeners.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = mListeners;
        try
        {
          localObject = ((List)localObject).get(i);
          localObject = (ControllerListener)localObject;
          if (localObject != null) {
            ((ControllerListener)localObject).onSubmit(paramString, paramObject);
          }
        }
        catch (Exception localException)
        {
          onException("InternalListener exception in onSubmit", localException);
        }
        i += 1;
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void removeListener(ControllerListener paramControllerListener)
  {
    try
    {
      int i = mListeners.indexOf(paramControllerListener);
      if (i != -1) {
        mListeners.set(i, null);
      }
      return;
    }
    catch (Throwable paramControllerListener)
    {
      throw paramControllerListener;
    }
  }
}
