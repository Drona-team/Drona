package com.google.android.gms.package_7;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import androidx.collection.ArrayMap;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Deprecated
public class InstanceID
{
  public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";
  public static final String ERROR_MISSING_INSTANCEID_SERVICE = "MISSING_INSTANCEID_SERVICE";
  public static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
  public static final String ERROR_TIMEOUT = "TIMEOUT";
  private static final com.google.android.gms.iid.zzaj<Boolean> zzbu = zzai.getCryptoKeys().getString("gcm_check_for_different_iid_in_token", true);
  private static Map<String, com.google.android.gms.iid.InstanceID> zzbv = new ArrayMap();
  private static final long zzbw = TimeUnit.DAYS.toMillis(7L);
  private static zzak zzbx;
  private static zzaf zzby;
  private static String zzbz;
  private Context context;
  private String zzca = "";
  
  private InstanceID(Context paramContext, String paramString)
  {
    context = paramContext.getApplicationContext();
    zzca = paramString;
  }
  
  static String encode(KeyPair paramKeyPair)
  {
    paramKeyPair = paramKeyPair.getPublic().getEncoded();
    try
    {
      paramKeyPair = MessageDigest.getInstance("SHA1").digest(paramKeyPair);
      paramKeyPair[0] = ((byte)((paramKeyPair[0] & 0xF) + 112));
      paramKeyPair = Base64.encodeToString(paramKeyPair, 0, 8, 11);
      return paramKeyPair;
    }
    catch (NoSuchAlgorithmException paramKeyPair)
    {
      for (;;) {}
    }
    Log.w("InstanceID", "Unexpected error, device missing required algorithms");
    return null;
  }
  
  public static InstanceID getInstance(Context paramContext)
  {
    return getInstance(paramContext, null);
  }
  
  public static InstanceID getInstance(Context paramContext, Bundle paramBundle)
  {
    if (paramBundle == null) {
      paramBundle = "";
    }
    for (;;)
    {
      try
      {
        paramBundle = paramBundle.getString("subtype");
      }
      catch (Throwable paramContext)
      {
        Context localContext;
        continue;
      }
      localContext = paramContext.getApplicationContext();
      if (zzbx == null)
      {
        paramContext = localContext.getPackageName();
        paramBundle = new StringBuilder(String.valueOf(paramContext).length() + 73);
        paramBundle.append("Instance ID SDK is deprecated, ");
        paramBundle.append(paramContext);
        paramBundle.append(" should update to use Firebase Instance ID");
        Log.w("InstanceID", paramBundle.toString());
        zzbx = new zzak(localContext);
        zzby = new zzaf(localContext);
      }
      zzbz = Integer.toString(getVersionNumber(localContext));
      paramBundle = (InstanceID)zzbv.get(localObject);
      paramContext = paramBundle;
      if (paramBundle == null)
      {
        paramContext = new InstanceID(localContext, (String)localObject);
        zzbv.put(localObject, paramContext);
      }
      return paramContext;
      throw paramContext;
      Object localObject = paramBundle;
      if (paramBundle == null) {
        localObject = "";
      }
    }
  }
  
  private final KeyPair getKeyPair()
  {
    return zzbx.save(zzca).getKeyPair();
  }
  
  public static zzak getValues()
  {
    return zzbx;
  }
  
  static String getVersion(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
      return versionName;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramContext).length() + 38);
      localStringBuilder.append("Never happens: can't find own package ");
      localStringBuilder.append(paramContext);
      Log.w("InstanceID", localStringBuilder.toString());
    }
    return null;
  }
  
  static int getVersionNumber(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
      return versionCode;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramContext).length() + 38);
      localStringBuilder.append("Never happens: can't find own package ");
      localStringBuilder.append(paramContext);
      Log.w("InstanceID", localStringBuilder.toString());
    }
    return 0;
  }
  
  public final void add(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (Looper.getMainLooper() != Looper.myLooper())
    {
      zzbx.deleteAccount(zzca, paramString1, paramString2);
      Bundle localBundle = paramBundle;
      if (paramBundle == null) {
        localBundle = new Bundle();
      }
      localBundle.putString("sender", paramString1);
      if (paramString2 != null) {
        localBundle.putString("scope", paramString2);
      }
      localBundle.putString("subscription", paramString1);
      localBundle.putString("delete", "1");
      localBundle.putString("X-delete", "1");
      if ("".equals(zzca)) {
        paramString2 = paramString1;
      } else {
        paramString2 = zzca;
      }
      localBundle.putString("subtype", paramString2);
      if (!"".equals(zzca)) {
        paramString1 = zzca;
      }
      localBundle.putString("X-subtype", paramString1);
      zzaf.get(zzby.doInBackground(localBundle, getKeyPair()));
      return;
    }
    throw new IOException("MAIN_THREAD");
  }
  
  public void deleteInstanceID()
    throws IOException
  {
    add("*", "*", null);
    setList();
  }
  
  public void deleteToken(String paramString1, String paramString2)
    throws IOException
  {
    add(paramString1, paramString2, null);
  }
  
  public long getCreationTime()
  {
    return zzbx.save(zzca).getCreationTime();
  }
  
  public String getId()
  {
    return encode(getKeyPair());
  }
  
  public final String getString(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (paramString2 != null) {
      paramBundle.putString("scope", paramString2);
    }
    paramBundle.putString("sender", paramString1);
    if ("".equals(zzca)) {
      paramString2 = paramString1;
    } else {
      paramString2 = zzca;
    }
    if (!paramBundle.containsKey("legacy.register"))
    {
      paramBundle.putString("subscription", paramString1);
      paramBundle.putString("subtype", paramString2);
      paramBundle.putString("X-subscription", paramString1);
      paramBundle.putString("X-subtype", paramString2);
    }
    paramString1 = zzaf.get(zzby.doInBackground(paramBundle, getKeyPair()));
    if ((!"RST".equals(paramString1)) && (!paramString1.startsWith("RST|"))) {
      return paramString1;
    }
    InstanceIDListenerService.send(context, zzbx);
    throw new IOException("SERVICE_NOT_AVAILABLE");
  }
  
  public String getSubtype()
  {
    return zzca;
  }
  
  public String getToken(String paramString1, String paramString2)
    throws IOException
  {
    return getToken(paramString1, paramString2, null);
  }
  
  public String getToken(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    Object localObject2;
    if (Looper.getMainLooper() != Looper.myLooper())
    {
      Object localObject1 = null;
      localObject2 = zzbx.getString("appVersion");
      int j = 1;
      int i = j;
      if (localObject2 != null) {
        if (!((String)localObject2).equals(zzbz))
        {
          i = j;
        }
        else
        {
          long l = zzbx.addCategory(zzca, paramString1, paramString2);
          if (l < 0L) {
            i = j;
          } else if (System.currentTimeMillis() - l >= zzbw) {
            i = j;
          } else {
            i = 0;
          }
        }
      }
      if (i == 0) {
        localObject1 = zzbx.getToken(zzca, paramString1, paramString2);
      }
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = paramBundle;
        if (paramBundle == null) {
          localObject1 = new Bundle();
        }
        localObject2 = getString(paramString1, paramString2, (Bundle)localObject1);
        if ((((Boolean)zzbu.setDoOutput()).booleanValue()) && (((String)localObject2).contains(":")) && (!((String)localObject2).startsWith(String.valueOf(getId()).concat(":"))))
        {
          InstanceIDListenerService.send(context, zzbx);
          throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        if (localObject2 != null) {
          zzbx.sendEmail(zzca, paramString1, paramString2, (String)localObject2, zzbz);
        }
      }
      else
      {
        return localObject2;
      }
    }
    else
    {
      throw new IOException("MAIN_THREAD");
    }
    return localObject2;
  }
  
  final void setList()
  {
    zzbx.add(zzca);
  }
}
