package org.json.social;

import android.content.ActivityNotFoundException;
import android.os.Build.VERSION;
import android.provider.Telephony.Sms;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class SMSShare
  extends SingleShareIntent
{
  private static final String PACKAGE = "com.android.mms";
  private static final String PLAY_STORE_LINK = "market://details?id=com.android.mms";
  private ReactApplicationContext reactContext = null;
  
  public SMSShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    reactContext = paramReactApplicationContext;
  }
  
  protected String getDefaultWebLink()
  {
    return null;
  }
  
  protected String getPackage()
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return Telephony.Sms.getDefaultSmsPackage(reactContext);
    }
    return "com.android.mms";
  }
  
  protected String getPlayStoreLink()
  {
    return "market://details?id=com.android.mms";
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
