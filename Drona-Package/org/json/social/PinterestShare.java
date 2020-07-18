package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class PinterestShare
  extends SingleShareIntent
{
  private static final String DEFAULT_WEB_LINK = "https://pinterest.com/pin/create/button/?url={url}&media=$media&description={message}";
  private static final String PACKAGE = "com.pinterest";
  private static final String PLAY_STORE_LINK = "market://details?id=com.pinterest";
  
  public PinterestShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return "https://pinterest.com/pin/create/button/?url={url}&media=$media&description={message}";
  }
  
  protected String getPackage()
  {
    return "com.pinterest";
  }
  
  protected String getPlayStoreLink()
  {
    return "market://details?id=com.pinterest";
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    super.open(paramReadableMap);
    openIntentChooser();
  }
}
