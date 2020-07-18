package org.json.social;

import android.content.ActivityNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

public class EmailShare
  extends SingleShareIntent
{
  private static final String PACKAGE = "com.google.android.gm";
  
  public EmailShare(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  protected String getDefaultWebLink()
  {
    return null;
  }
  
  protected String getPackage()
  {
    return "com.google.android.gm";
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
