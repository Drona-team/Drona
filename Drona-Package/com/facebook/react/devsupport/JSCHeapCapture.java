package com.facebook.react.devsupport;

import androidx.annotation.Nullable;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;
import java.io.File;

@ReactModule(name="JSCHeapCapture", needsEagerInit=true)
public class JSCHeapCapture
  extends ReactContextBaseJavaModule
{
  @Nullable
  private CaptureCallback mCaptureInProgress = null;
  
  public JSCHeapCapture(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  public void captureComplete(String paramString1, String paramString2)
  {
    try
    {
      if (mCaptureInProgress != null)
      {
        if (paramString2 == null) {
          mCaptureInProgress.onSuccess(new File(paramString1));
        } else {
          mCaptureInProgress.onFailure(new CaptureException(paramString2));
        }
        mCaptureInProgress = null;
      }
      return;
    }
    catch (Throwable paramString1)
    {
      throw paramString1;
    }
  }
  
  public void captureHeap(String paramString, CaptureCallback paramCaptureCallback)
  {
    try
    {
      if (mCaptureInProgress != null)
      {
        paramCaptureCallback.onFailure(new CaptureException("Heap capture already in progress."));
        return;
      }
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append("/capture.json");
      paramString = new File(((StringBuilder)localObject).toString());
      paramString.delete();
      localObject = (HeapCapture)getReactApplicationContext().getJSModule(HeapCapture.class);
      if (localObject == null)
      {
        paramCaptureCallback.onFailure(new CaptureException("Heap capture js module not registered."));
        return;
      }
      mCaptureInProgress = paramCaptureCallback;
      ((HeapCapture)localObject).captureHeap(paramString.getPath());
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public String getName()
  {
    return "JSCHeapCapture";
  }
  
  public static abstract interface CaptureCallback
  {
    public abstract void onFailure(JSCHeapCapture.CaptureException paramCaptureException);
    
    public abstract void onSuccess(File paramFile);
  }
  
  public static class CaptureException
    extends Exception
  {
    CaptureException(String paramString)
    {
      super();
    }
    
    CaptureException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  public static abstract interface HeapCapture
    extends JavaScriptModule
  {
    public abstract void captureHeap(String paramString);
  }
}
