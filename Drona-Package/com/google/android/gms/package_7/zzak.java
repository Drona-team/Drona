package com.google.android.gms.package_7;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import androidx.annotation.GuardedBy;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import com.google.android.gms.iid.zzo;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzak
{
  private Context mContext;
  private SharedPreferences zzde;
  private final Cache zzdf;
  @GuardedBy("this")
  private final Map<String, zzo> zzdg = new ArrayMap();
  
  public zzak(Context paramContext)
  {
    this(paramContext, new Cache());
  }
  
  private zzak(Context paramContext, Cache paramCache)
  {
    mContext = paramContext;
    zzde = paramContext.getSharedPreferences("com.google.android.gms.appid", 0);
    zzdf = paramCache;
    paramContext = new File(ContextCompat.getNoBackupFilesDir(mContext), "com.google.android.gms.appid-no-backup");
    if (!paramContext.exists()) {
      try
      {
        boolean bool = paramContext.createNewFile();
        if (bool)
        {
          bool = isEmpty();
          if (!bool)
          {
            Log.i("InstanceID/Store", "App restored, clearing state");
            paramContext = mContext;
            InstanceIDListenerService.send(paramContext, this);
            return;
          }
        }
      }
      catch (IOException paramContext)
      {
        if (Log.isLoggable("InstanceID/Store", 3))
        {
          paramContext = String.valueOf(paramContext.getMessage());
          if (paramContext.length() != 0) {
            paramContext = "Error creating file in no backup dir: ".concat(paramContext);
          } else {
            paramContext = new String("Error creating file in no backup dir: ");
          }
          Log.d("InstanceID/Store", paramContext);
        }
      }
    }
  }
  
  private static String getString(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramString1).length() + 4 + String.valueOf(paramString2).length() + String.valueOf(paramString3).length());
    localStringBuilder.append(paramString1);
    localStringBuilder.append("|T|");
    localStringBuilder.append(paramString2);
    localStringBuilder.append("|");
    localStringBuilder.append(paramString3);
    return localStringBuilder.toString();
  }
  
  static String getValue(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramString1).length() + 3 + String.valueOf(paramString2).length());
    localStringBuilder.append(paramString1);
    localStringBuilder.append("|S|");
    localStringBuilder.append(paramString2);
    return localStringBuilder.toString();
  }
  
  private static String toString(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramString1).length() + 14 + String.valueOf(paramString2).length() + String.valueOf(paramString3).length());
    localStringBuilder.append(paramString1);
    localStringBuilder.append("|T-timestamp|");
    localStringBuilder.append(paramString2);
    localStringBuilder.append("|");
    localStringBuilder.append(paramString3);
    return localStringBuilder.toString();
  }
  
  final void add(String paramString)
  {
    try
    {
      zzdg.remove(paramString);
      Cache.initialize(mContext, paramString);
      delete(String.valueOf(paramString).concat("|"));
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  final long addCategory(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = toString(paramString1, paramString2, paramString3);
      long l = zzde.getLong(paramString1, -1L);
      return l;
    }
    catch (Throwable paramString1)
    {
      throw paramString1;
    }
  }
  
  public final void clear()
  {
    try
    {
      zzdg.clear();
      Cache.initialize(mContext);
      zzde.edit().clear().commit();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void delete(String paramString)
  {
    try
    {
      SharedPreferences.Editor localEditor = zzde.edit();
      Iterator localIterator = zzde.getAll().keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.startsWith(paramString)) {
          localEditor.remove(str);
        }
      }
      localEditor.commit();
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public final void deleteAccount(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      SharedPreferences.Editor localEditor = zzde.edit();
      localEditor.remove(getString(paramString1, paramString2, paramString3));
      localEditor.remove(toString(paramString1, paramString2, paramString3));
      localEditor.commit();
      return;
    }
    catch (Throwable paramString1)
    {
      throw paramString1;
    }
  }
  
  final String getString(String paramString)
  {
    try
    {
      paramString = zzde.getString(paramString, null);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public final String getToken(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = getString(paramString1, paramString2, paramString3);
      paramString1 = zzde.getString(paramString1, null);
      return paramString1;
    }
    catch (Throwable paramString1)
    {
      throw paramString1;
    }
  }
  
  public final boolean isEmpty()
  {
    return zzde.getAll().isEmpty();
  }
  
  public final Buffer save(String paramString)
  {
    for (;;)
    {
      try
      {
        localObject = (Buffer)zzdg.get(paramString);
        if (localObject != null) {
          return localObject;
        }
        localObject = zzdf;
        localContext = mContext;
      }
      catch (Throwable paramString)
      {
        Object localObject;
        Context localContext;
        throw paramString;
      }
      try
      {
        localObject = ((Cache)localObject).save(localContext, paramString);
      }
      catch (DigesterLoadingException localDigesterLoadingException) {}
    }
    Log.w("InstanceID/Store", "Stored data is corrupt, generating new identity");
    InstanceIDListenerService.send(mContext, this);
    localObject = zzdf.write(mContext, paramString);
    zzdg.put(paramString, localObject);
    return localObject;
  }
  
  public final void sendEmail(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    try
    {
      String str = getString(paramString1, paramString2, paramString3);
      paramString1 = toString(paramString1, paramString2, paramString3);
      paramString2 = zzde.edit();
      paramString2.putString(str, paramString4);
      paramString2.putLong(paramString1, System.currentTimeMillis());
      paramString2.putString("appVersion", paramString5);
      paramString2.commit();
      return;
    }
    catch (Throwable paramString1)
    {
      throw paramString1;
    }
  }
}
