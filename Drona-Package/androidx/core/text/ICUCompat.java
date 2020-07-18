package androidx.core.text;

import android.icu.util.ULocale;
import android.os.Build.VERSION;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public final class ICUCompat
{
  private static final String TAG = "ICUCompat";
  private static Method sAddLikelySubtagsMethod;
  private static Method sGetScriptMethod;
  
  static
  {
    if (Build.VERSION.SDK_INT < 21) {
      try
      {
        Object localObject1 = Class.forName("libcore.icu.ICU");
        if (localObject1 == null) {
          return;
        }
        Method localMethod = ((Class)localObject1).getMethod("getScript", new Class[] { String.class });
        sGetScriptMethod = localMethod;
        localObject1 = ((Class)localObject1).getMethod("addLikelySubtags", new Class[] { String.class });
        sAddLikelySubtagsMethod = (Method)localObject1;
        return;
      }
      catch (Exception localException1)
      {
        sGetScriptMethod = null;
        sAddLikelySubtagsMethod = null;
        Log.w("ICUCompat", localException1);
        return;
      }
    } else if (Build.VERSION.SDK_INT < 24) {
      try
      {
        Object localObject2 = Class.forName("libcore.icu.ICU");
        localObject2 = ((Class)localObject2).getMethod("addLikelySubtags", new Class[] { Locale.class });
        sAddLikelySubtagsMethod = (Method)localObject2;
        return;
      }
      catch (Exception localException2)
      {
        throw new IllegalStateException(localException2);
      }
    }
  }
  
  private ICUCompat() {}
  
  private static String addLikelySubtags(Locale paramLocale)
  {
    paramLocale = paramLocale.toString();
    if (sAddLikelySubtagsMethod != null)
    {
      Object localObject = sAddLikelySubtagsMethod;
      try
      {
        localObject = ((Method)localObject).invoke(null, new Object[] { paramLocale });
        return (String)localObject;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Log.w("ICUCompat", localInvocationTargetException);
        return paramLocale;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.w("ICUCompat", localIllegalAccessException);
      }
    }
    return paramLocale;
  }
  
  private static String getScript(String paramString)
  {
    if (sGetScriptMethod != null)
    {
      Method localMethod = sGetScriptMethod;
      try
      {
        paramString = localMethod.invoke(null, new Object[] { paramString });
        return (String)paramString;
      }
      catch (InvocationTargetException paramString)
      {
        Log.w("ICUCompat", paramString);
        return null;
      }
      catch (IllegalAccessException paramString)
      {
        Log.w("ICUCompat", paramString);
      }
    }
    return null;
  }
  
  public static String maximizeAndGetScript(Locale paramLocale)
  {
    if (Build.VERSION.SDK_INT >= 24) {
      return ULocale.addLikelySubtags(ULocale.forLocale(paramLocale)).getScript();
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      Object localObject = sAddLikelySubtagsMethod;
      try
      {
        localObject = ((Method)localObject).invoke(null, new Object[] { paramLocale });
        localObject = (Locale)localObject;
        localObject = ((Locale)localObject).getScript();
        return localObject;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.w("ICUCompat", localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Log.w("ICUCompat", localInvocationTargetException);
      }
      return paramLocale.getScript();
    }
    paramLocale = addLikelySubtags(paramLocale);
    if (paramLocale != null) {
      return getScript(paramLocale);
    }
    return null;
  }
}
