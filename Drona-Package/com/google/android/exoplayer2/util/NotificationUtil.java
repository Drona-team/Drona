package com.google.android.exoplayer2.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressLint({"InlinedApi"})
public final class NotificationUtil
{
  public static final int IMPORTANCE_DEFAULT = 3;
  public static final int IMPORTANCE_HIGH = 4;
  public static final int IMPORTANCE_LOW = 2;
  public static final int IMPORTANCE_MIN = 1;
  public static final int IMPORTANCE_NONE = 0;
  public static final int IMPORTANCE_UNSPECIFIED = -1000;
  
  private NotificationUtil() {}
  
  public static void createNotificationChannel(Context paramContext, String paramString, int paramInt1, int paramInt2)
  {
    if (Util.SDK_INT >= 26) {
      ((NotificationManager)paramContext.getSystemService("notification")).createNotificationChannel(new NotificationChannel(paramString, paramContext.getString(paramInt1), paramInt2));
    }
  }
  
  public static void setNotification(Context paramContext, int paramInt, Notification paramNotification)
  {
    paramContext = (NotificationManager)paramContext.getSystemService("notification");
    if (paramNotification != null)
    {
      paramContext.notify(paramInt, paramNotification);
      return;
    }
    paramContext.cancel(paramInt);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Importance {}
}
