package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class FacebookPagesManagerShare
  extends SingleShareIntent
{
  private static final String DEFAULT_WEB_LINK = "https://www.facebook.com/sharer/sharer.php?u={url}";
  private static final String PACKAGE = "com.facebook.pages.app";
  
  public FacebookPagesManagerShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return "https://www.facebook.com/sharer/sharer.php?u={url}";
  }
  
  protected String getPackage()
  {
    return "com.facebook.pages.app";
  }
  
  protected String getPlayStoreLink()
  {
    return null;
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
