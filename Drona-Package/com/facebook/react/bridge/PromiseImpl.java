package com.facebook.react.bridge;

import androidx.annotation.Nullable;

public class PromiseImpl
  implements Promise
{
  private static final String ERROR_DEFAULT_CODE = "EUNSPECIFIED";
  private static final String ERROR_DEFAULT_MESSAGE = "Error not specified.";
  private static final String ERROR_MAP_KEY_CODE = "code";
  private static final String ERROR_MAP_KEY_MESSAGE = "message";
  private static final String ERROR_MAP_KEY_NATIVE_STACK = "nativeStackAndroid";
  private static final String ERROR_MAP_KEY_USER_INFO = "userInfo";
  private static final int ERROR_STACK_FRAME_LIMIT = 50;
  private static final String STACK_FRAME_KEY_CLASS = "class";
  private static final String STACK_FRAME_KEY_FILE = "file";
  private static final String STACK_FRAME_KEY_LINE_NUMBER = "lineNumber";
  private static final String STACK_FRAME_KEY_METHOD_NAME = "methodName";
  @Nullable
  private Callback mReject;
  @Nullable
  private Callback mResolve;
  
  public PromiseImpl(Callback paramCallback1, Callback paramCallback2)
  {
    mResolve = paramCallback1;
    mReject = paramCallback2;
  }
  
  public void reject(String paramString)
  {
    reject(null, paramString, null, null);
  }
  
  public void reject(String paramString, WritableMap paramWritableMap)
  {
    reject(paramString, null, null, paramWritableMap);
  }
  
  public void reject(String paramString1, String paramString2)
  {
    reject(paramString1, paramString2, null, null);
  }
  
  public void reject(String paramString1, String paramString2, WritableMap paramWritableMap)
  {
    reject(paramString1, paramString2, null, paramWritableMap);
  }
  
  public void reject(String paramString1, String paramString2, Throwable paramThrowable)
  {
    reject(paramString1, paramString2, paramThrowable, null);
  }
  
  public void reject(String paramString1, String paramString2, Throwable paramThrowable, WritableMap paramWritableMap)
  {
    if (mReject == null)
    {
      mResolve = null;
      return;
    }
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    if (paramString1 == null) {
      localWritableNativeMap.putString("code", "EUNSPECIFIED");
    } else {
      localWritableNativeMap.putString("code", paramString1);
    }
    if (paramString2 != null) {
      localWritableNativeMap.putString("message", paramString2);
    } else if (paramThrowable != null) {
      localWritableNativeMap.putString("message", paramThrowable.getMessage());
    } else {
      localWritableNativeMap.putString("message", "Error not specified.");
    }
    if (paramWritableMap != null) {
      localWritableNativeMap.putMap("userInfo", paramWritableMap);
    } else {
      localWritableNativeMap.putNull("userInfo");
    }
    if (paramThrowable != null)
    {
      paramString1 = paramThrowable.getStackTrace();
      paramString2 = new WritableNativeArray();
      int i = 0;
      while ((i < paramString1.length) && (i < 50))
      {
        paramThrowable = paramString1[i];
        paramWritableMap = new WritableNativeMap();
        paramWritableMap.putString("class", paramThrowable.getClassName());
        paramWritableMap.putString("file", paramThrowable.getFileName());
        paramWritableMap.putInt("lineNumber", paramThrowable.getLineNumber());
        paramWritableMap.putString("methodName", paramThrowable.getMethodName());
        paramString2.pushMap(paramWritableMap);
        i += 1;
      }
      localWritableNativeMap.putArray("nativeStackAndroid", paramString2);
    }
    else
    {
      localWritableNativeMap.putArray("nativeStackAndroid", new WritableNativeArray());
    }
    mReject.invoke(new Object[] { localWritableNativeMap });
    mResolve = null;
    mReject = null;
  }
  
  public void reject(String paramString, Throwable paramThrowable)
  {
    reject(paramString, null, paramThrowable, null);
  }
  
  public void reject(String paramString, Throwable paramThrowable, WritableMap paramWritableMap)
  {
    reject(paramString, null, paramThrowable, paramWritableMap);
  }
  
  public void reject(Throwable paramThrowable)
  {
    reject(null, null, paramThrowable, null);
  }
  
  public void reject(Throwable paramThrowable, WritableMap paramWritableMap)
  {
    reject(null, null, paramThrowable, paramWritableMap);
  }
  
  public void resolve(Object paramObject)
  {
    if (mResolve != null)
    {
      mResolve.invoke(new Object[] { paramObject });
      mResolve = null;
      mReject = null;
    }
  }
}
