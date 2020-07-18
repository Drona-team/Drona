package com.facebook.react.modules.share;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="ShareModule")
public class ShareModule
  extends ReactContextBaseJavaModule
{
  static final String ACTION_SHARED = "sharedAction";
  static final String ERROR_INVALID_CONTENT = "E_INVALID_CONTENT";
  static final String ERROR_UNABLE_TO_OPEN_DIALOG = "E_UNABLE_TO_OPEN_DIALOG";
  public static final String NAME = "ShareModule";
  
  public ShareModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  public String getName()
  {
    return "ShareModule";
  }
  
  public void share(ReadableMap paramReadableMap, String paramString, Promise paramPromise)
  {
    if (paramReadableMap == null)
    {
      paramPromise.reject("E_INVALID_CONTENT", "Content cannot be null");
      return;
    }
    try
    {
      Intent localIntent = new Intent("android.intent.action.SEND");
      localIntent.setTypeAndNormalize("text/plain");
      boolean bool = paramReadableMap.hasKey("title");
      if (bool) {
        localIntent.putExtra("android.intent.extra.SUBJECT", paramReadableMap.getString("title"));
      }
      bool = paramReadableMap.hasKey("message");
      if (bool) {
        localIntent.putExtra("android.intent.extra.TEXT", paramReadableMap.getString("message"));
      }
      paramReadableMap = Intent.createChooser(localIntent, paramString);
      paramReadableMap.addCategory("android.intent.category.DEFAULT");
      paramString = getCurrentActivity();
      if (paramString != null) {
        paramString.startActivity(paramReadableMap);
      } else {
        getReactApplicationContext().startActivity(paramReadableMap);
      }
      paramReadableMap = Arguments.createMap();
      paramReadableMap.putString("action", "sharedAction");
      paramPromise.resolve(paramReadableMap);
      return;
    }
    catch (Exception paramReadableMap)
    {
      for (;;) {}
    }
    paramPromise.reject("E_UNABLE_TO_OPEN_DIALOG", "Failed to open share dialog");
  }
}
