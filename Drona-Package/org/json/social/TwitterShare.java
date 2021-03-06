package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class TwitterShare
  extends SingleShareIntent
{
  private static final String DEFAULT_WEB_LINK = "https://twitter.com/intent/tweet?text={message}&url={url}";
  private static final String PACKAGE = "com.twitter.android";
  
  public TwitterShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return "https://twitter.com/intent/tweet?text={message}&url={url}";
  }
  
  protected String getPackage()
  {
    return "com.twitter.android";
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
