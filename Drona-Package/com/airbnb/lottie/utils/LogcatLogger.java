package com.airbnb.lottie.utils;

import android.util.Log;
import com.airbnb.lottie.LottieLogger;
import com.airbnb.lottie.Way;
import java.util.HashSet;
import java.util.Set;

public class LogcatLogger
  implements LottieLogger
{
  private static final Set<String> loggedMessages = new HashSet();
  
  public LogcatLogger() {}
  
  public void debug(String paramString)
  {
    debug(paramString, null);
  }
  
  public void debug(String paramString, Throwable paramThrowable)
  {
    if (Way.tags) {
      Log.d("LOTTIE", paramString, paramThrowable);
    }
  }
  
  public void warning(String paramString)
  {
    warning(paramString, null);
  }
  
  public void warning(String paramString, Throwable paramThrowable)
  {
    if (loggedMessages.contains(paramString)) {
      return;
    }
    Log.w("LOTTIE", paramString, paramThrowable);
    loggedMessages.add(paramString);
  }
}
