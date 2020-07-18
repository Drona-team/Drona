package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class LinkedinShare
  extends SingleShareIntent
{
  private static final String PACKAGE = "com.linkedin.android";
  private static final String PLAY_STORE_LINK = "market://details?id=com.linkedin.android";
  
  public LinkedinShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return null;
  }
  
  protected String getPackage()
  {
    return "com.linkedin.android";
  }
  
  protected String getPlayStoreLink()
  {
    return "market://details?id=com.linkedin.android";
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
