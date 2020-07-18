package com.facebook.react.modules.blob;

import com.facebook.react.bridge.JavaScriptContextHolder;
import com.facebook.react.bridge.ReactContext;
import com.facebook.soloader.SoLoader;

class BlobCollector
{
  static
  {
    SoLoader.loadLibrary("reactnativeblob");
  }
  
  BlobCollector() {}
  
  static void install(ReactContext paramReactContext, final BlobModule paramBlobModule)
  {
    paramReactContext.runOnJSQueueThread(new Runnable()
    {
      public void run()
      {
        JavaScriptContextHolder localJavaScriptContextHolder = val$reactContext.getJavaScriptContextHolder();
        if (localJavaScriptContextHolder.getIdentity() != 0L) {
          BlobCollector.nativeInstall(paramBlobModule, localJavaScriptContextHolder.getIdentity());
        }
      }
    });
  }
  
  private static native void nativeInstall(Object paramObject, long paramLong);
}
