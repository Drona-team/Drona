package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public final class JavaScriptModuleRegistry
{
  private final HashMap<Class<? extends JavaScriptModule>, JavaScriptModule> mModuleInstances = new HashMap();
  
  public JavaScriptModuleRegistry() {}
  
  public JavaScriptModule getJavaScriptModule(CatalystInstance paramCatalystInstance, Class paramClass)
  {
    try
    {
      Object localObject = (JavaScriptModule)mModuleInstances.get(paramClass);
      if (localObject != null) {
        return localObject;
      }
      localObject = paramClass.getClassLoader();
      paramCatalystInstance = new JavaScriptModuleInvocationHandler(paramCatalystInstance, paramClass);
      paramCatalystInstance = (JavaScriptModule)Proxy.newProxyInstance((ClassLoader)localObject, new Class[] { paramClass }, paramCatalystInstance);
      mModuleInstances.put(paramClass, paramCatalystInstance);
      return paramCatalystInstance;
    }
    catch (Throwable paramCatalystInstance)
    {
      throw paramCatalystInstance;
    }
  }
  
  private static class JavaScriptModuleInvocationHandler
    implements InvocationHandler
  {
    private final CatalystInstance mCatalystInstance;
    private final Class<? extends JavaScriptModule> mModuleInterface;
    @Nullable
    private String mName;
    
    public JavaScriptModuleInvocationHandler(CatalystInstance paramCatalystInstance, Class paramClass)
    {
      mCatalystInstance = paramCatalystInstance;
      mModuleInterface = paramClass;
    }
    
    private String getJSModuleName()
    {
      if (mName == null)
      {
        String str2 = mModuleInterface.getSimpleName();
        String str1 = str2;
        int i = str2.lastIndexOf('$');
        if (i != -1) {
          str1 = str2.substring(i + 1);
        }
        mName = str1;
      }
      return mName;
    }
    
    public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
      throws Throwable
    {
      if (paramArrayOfObject != null) {
        paramObject = Arguments.fromJavaArgs(paramArrayOfObject);
      } else {
        paramObject = new WritableNativeArray();
      }
      mCatalystInstance.callFunction(getJSModuleName(), paramMethod.getName(), paramObject);
      return null;
    }
  }
}