package androidx.core.opml;

import android.os.Build.VERSION;
import android.os.Message;

public final class MessageCompat
{
  private static boolean sTryIsAsynchronous;
  private static boolean sTrySetAsynchronous;
  
  private MessageCompat() {}
  
  public static boolean isAsynchronous(Message paramMessage)
  {
    if (Build.VERSION.SDK_INT >= 22) {
      return paramMessage.isAsynchronous();
    }
    if ((sTryIsAsynchronous) && (Build.VERSION.SDK_INT >= 16))
    {
      try
      {
        boolean bool = paramMessage.isAsynchronous();
        return bool;
      }
      catch (NoSuchMethodError paramMessage)
      {
        for (;;) {}
      }
      sTryIsAsynchronous = false;
      return false;
    }
    return false;
  }
  
  public static void setAsynchronous(Message paramMessage, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 22)
    {
      paramMessage.setAsynchronous(paramBoolean);
      return;
    }
    if ((sTrySetAsynchronous) && (Build.VERSION.SDK_INT >= 16))
    {
      try
      {
        paramMessage.setAsynchronous(paramBoolean);
        return;
      }
      catch (NoSuchMethodError paramMessage)
      {
        for (;;) {}
      }
      sTrySetAsynchronous = false;
      return;
    }
  }
}
