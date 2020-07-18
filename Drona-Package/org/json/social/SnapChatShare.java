package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class SnapChatShare
  extends SingleShareIntent
{
  private static final String PACKAGE = "com.snapchat.android";
  private static final String PLAY_STORE_LINK = "market://details?id=com.snapchat.android";
  
  public SnapChatShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return null;
  }
  
  protected String getPackage()
  {
    return "com.snapchat.android";
  }
  
  protected String getPlayStoreLink()
  {
    return "market://details?id=com.snapchat.android";
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
