package com.facebook.react.devsupport;

import android.util.Pair;
import android.view.View;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;
import java.util.Map;

@ReactModule(name="JSDevSupport")
public class JSDevSupport
  extends ReactContextBaseJavaModule
{
  public static final int ERROR_CODE_EXCEPTION = 0;
  public static final int ERROR_CODE_VIEW_NOT_FOUND = 1;
  public static final String MODULE_NAME = "JSDevSupport";
  @Nullable
  private volatile DevSupportCallback mCurrentCallback = null;
  
  public JSDevSupport(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  public void computeDeepestJSHierarchy(View paramView, DevSupportCallback paramDevSupportCallback)
  {
    try
    {
      getJSHierarchy(Integer.valueOf(((View)getDeepestLeaffirst).getId()).intValue(), paramDevSupportCallback);
      return;
    }
    catch (Throwable paramView)
    {
      throw paramView;
    }
  }
  
  public Map getConstants()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("ERROR_CODE_EXCEPTION", Integer.valueOf(0));
    localHashMap.put("ERROR_CODE_VIEW_NOT_FOUND", Integer.valueOf(1));
    return localHashMap;
  }
  
  public void getJSHierarchy(int paramInt, DevSupportCallback paramDevSupportCallback)
  {
    try
    {
      JSDevSupportModule localJSDevSupportModule = (JSDevSupportModule)getReactApplicationContext().getJSModule(JSDevSupportModule.class);
      if (localJSDevSupportModule == null)
      {
        paramDevSupportCallback.onFailure(0, new JSCHeapCapture.CaptureException("JSDevSupport module not registered."));
        return;
      }
      mCurrentCallback = paramDevSupportCallback;
      localJSDevSupportModule.getJSHierarchy(paramInt);
      return;
    }
    catch (Throwable paramDevSupportCallback)
    {
      throw paramDevSupportCallback;
    }
  }
  
  public String getName()
  {
    return "JSDevSupport";
  }
  
  public void onFailure(int paramInt, String paramString)
  {
    try
    {
      if (mCurrentCallback != null) {
        mCurrentCallback.onFailure(paramInt, new RuntimeException(paramString));
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void onSuccess(String paramString)
  {
    try
    {
      if (mCurrentCallback != null) {
        mCurrentCallback.onSuccess(paramString);
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public static abstract interface DevSupportCallback
  {
    public abstract void onFailure(int paramInt, Exception paramException);
    
    public abstract void onSuccess(String paramString);
  }
  
  public static abstract interface JSDevSupportModule
    extends JavaScriptModule
  {
    public abstract void getJSHierarchy(int paramInt);
  }
}
