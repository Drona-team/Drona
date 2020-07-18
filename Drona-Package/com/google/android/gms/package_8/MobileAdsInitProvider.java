package com.google.android.gms.package_8;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import com.google.android.gms.common.annotation.KeepForSdkWithMembers;
import com.google.android.gms.internal.ads.zzabh;

@KeepForSdkWithMembers
public class MobileAdsInitProvider
  extends ContentProvider
{
  private final zzabh zzaau = new zzabh();
  
  public MobileAdsInitProvider() {}
  
  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    zzaau.attachInfo(paramContext, paramProviderInfo);
  }
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    return zzaau.delete(paramUri, paramString, paramArrayOfString);
  }
  
  public String getType(Uri paramUri)
  {
    return zzaau.getType(paramUri);
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    return zzaau.insert(paramUri, paramContentValues);
  }
  
  public boolean onCreate()
  {
    return zzaau.onCreate();
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    return zzaau.query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    return zzaau.update(paramUri, paramContentValues, paramString, paramArrayOfString);
  }
}
