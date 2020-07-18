package client.testing.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import java.util.UUID;

public abstract class SessionIdStorage
{
  private static final String PREF_NAME = "APIAI_preferences";
  private static final String SESSION_ID = "sessionId";
  
  public SessionIdStorage() {}
  
  public static String getSessionId(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getSharedPreferences("APIAI_preferences", 0);
      String str = paramContext.getString("sessionId", "");
      boolean bool = TextUtils.isEmpty(str);
      if (!bool) {
        return str;
      }
      paramContext = paramContext.edit();
      str = UUID.randomUUID().toString();
      paramContext.putString("sessionId", str);
      paramContext.commit();
      return str;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static void resetSessionId(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getSharedPreferences("APIAI_preferences", 0).edit();
      paramContext.putString("sessionId", "");
      paramContext.commit();
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
}
