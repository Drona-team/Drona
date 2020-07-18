package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class GooglePlusShare
  extends SingleShareIntent
{
  private static final String DEFAULT_WEB_LINK = "https://plus.google.com/share?url={url}";
  private static final String PACKAGE = "com.google.android.apps.plus";
  private static final String PLAY_STORE_LINK = "market://details?id=com.google.android.apps.plus";
  
  public GooglePlusShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return "https://plus.google.com/share?url={url}";
  }
  
  protected String getPackage()
  {
    return "com.google.android.apps.plus";
  }
  
  protected String getPlayStoreLink()
  {
    return "market://details?id=com.google.android.apps.plus";
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
