package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class WhatsAppShare
  extends SingleShareIntent
{
  private static final String PACKAGE = "com.whatsapp";
  private static final String PLAY_STORE_LINK = "market://details?id=com.whatsapp";
  
  public WhatsAppShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return null;
  }
  
  protected String getPackage()
  {
    return "com.whatsapp";
  }
  
  protected String getPlayStoreLink()
  {
    return "market://details?id=com.whatsapp";
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
