package androidx.browser.browseractions;

import android.app.PendingIntent;
import androidx.annotation.DrawableRes;

public class BrowserActionItem
{
  private final PendingIntent mAction;
  @DrawableRes
  private final int mIconId;
  private final String mTitle;
  
  public BrowserActionItem(String paramString, PendingIntent paramPendingIntent)
  {
    this(paramString, paramPendingIntent, 0);
  }
  
  public BrowserActionItem(String paramString, PendingIntent paramPendingIntent, int paramInt)
  {
    mTitle = paramString;
    mAction = paramPendingIntent;
    mIconId = paramInt;
  }
  
  public PendingIntent getAction()
  {
    return mAction;
  }
  
  public int getIconId()
  {
    return mIconId;
  }
  
  public String getTitle()
  {
    return mTitle;
  }
}
