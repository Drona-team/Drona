package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class MessengerShare
  extends SingleShareIntent
{
  private static final String PACKAGE = "com.facebook.orca";
  private static final String PLAY_STORE_LINK = "market://details?id=com.facebook.orca";
  
  public MessengerShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return null;
  }
  
  protected String getPackage()
  {
    return "com.facebook.orca";
  }
  
  protected String getPlayStoreLink()
  {
    return "market://details?id=com.facebook.orca";
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
