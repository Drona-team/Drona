package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.upgrade.HybridData;

@DoNotStrip
public class ProxyJavaScriptExecutor
  extends JavaScriptExecutor
{
  @Nullable
  private JavaJSExecutor mJavaJSExecutor;
  
  static {}
  
  public ProxyJavaScriptExecutor(JavaJSExecutor paramJavaJSExecutor)
  {
    super(initHybrid(paramJavaJSExecutor));
    mJavaJSExecutor = paramJavaJSExecutor;
  }
  
  private static native HybridData initHybrid(JavaJSExecutor paramJavaJSExecutor);
  
  public void close()
  {
    if (mJavaJSExecutor != null)
    {
      mJavaJSExecutor.close();
      mJavaJSExecutor = null;
    }
  }
  
  public String getName()
  {
    return "ProxyJavaScriptExecutor";
  }
  
  public static class Factory
    implements JavaScriptExecutorFactory
  {
    private final JavaJSExecutor.Factory mJavaJSExecutorFactory;
    
    public Factory(JavaJSExecutor.Factory paramFactory)
    {
      mJavaJSExecutorFactory = paramFactory;
    }
    
    public JavaScriptExecutor create()
      throws Exception
    {
      return new ProxyJavaScriptExecutor(mJavaJSExecutorFactory.create());
    }
    
    public void startSamplingProfiler()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Starting sampling profiler not supported on ");
      localStringBuilder.append(toString());
      throw new UnsupportedOperationException(localStringBuilder.toString());
    }
    
    public void stopSamplingProfiler(String paramString)
    {
      paramString = new StringBuilder();
      paramString.append("Stopping sampling profiler not supported on ");
      paramString.append(toString());
      throw new UnsupportedOperationException(paramString.toString());
    }
  }
}
