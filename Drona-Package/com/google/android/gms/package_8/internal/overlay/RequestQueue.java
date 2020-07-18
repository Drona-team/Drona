package com.google.android.gms.package_8.internal.overlay;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.zzacr;
import com.google.android.gms.internal.ads.zzacu;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzawz;
import com.google.android.gms.internal.ads.zzaxi;
import com.google.android.gms.internal.ads.zzyt;
import com.google.android.gms.package_8.internal.UserData;

@zzard
public final class RequestQueue
{
  public RequestQueue() {}
  
  private static boolean start(Context paramContext, Intent paramIntent, Monitor paramMonitor)
  {
    try
    {
      String str = String.valueOf(paramIntent.toURI());
      int i = str.length();
      if (i != 0) {
        str = "Launching an intent: ".concat(str);
      } else {
        str = new String("Launching an intent: ");
      }
      zzawz.zzds(str);
      UserData.zzlg();
      zzaxi.zza(paramContext, paramIntent);
      if (paramMonitor != null) {
        paramMonitor.zztq();
      }
      return true;
    }
    catch (ActivityNotFoundException paramContext)
    {
      zzawz.zzep(paramContext.getMessage());
    }
    return false;
  }
  
  public static boolean start(Context paramContext, Attachment paramAttachment, Monitor paramMonitor)
  {
    if (paramAttachment == null)
    {
      zzawz.zzep("No intent data for launcher overlay.");
      return false;
    }
    zzacu.initialize(paramContext);
    if (intent != null) {
      return start(paramContext, intent, paramMonitor);
    }
    Intent localIntent = new Intent();
    if (TextUtils.isEmpty(uri))
    {
      zzawz.zzep("Open GMSG did not contain a URL.");
      return false;
    }
    if (!TextUtils.isEmpty(mimeType)) {
      localIntent.setDataAndType(Uri.parse(uri), mimeType);
    } else {
      localIntent.setData(Uri.parse(uri));
    }
    localIntent.setAction("android.intent.action.VIEW");
    if (!TextUtils.isEmpty(packageName)) {
      localIntent.setPackage(packageName);
    }
    if (!TextUtils.isEmpty(zzdjh))
    {
      String[] arrayOfString = zzdjh.split("/", 2);
      if (arrayOfString.length < 2)
      {
        paramContext = String.valueOf(zzdjh);
        if (paramContext.length() != 0) {
          paramContext = "Could not parse component name from open GMSG: ".concat(paramContext);
        } else {
          paramContext = new String("Could not parse component name from open GMSG: ");
        }
        zzawz.zzep(paramContext);
        return false;
      }
      localIntent.setClassName(arrayOfString[0], arrayOfString[1]);
    }
    paramAttachment = zzdji;
    if (!TextUtils.isEmpty(paramAttachment)) {}
    try
    {
      i = Integer.parseInt(paramAttachment);
    }
    catch (NumberFormatException paramAttachment)
    {
      int i;
      for (;;) {}
    }
    zzawz.zzep("Could not parse intent flags.");
    i = 0;
    localIntent.addFlags(i);
    paramAttachment = zzacu.zzctz;
    if (((Boolean)zzyt.zzpe().zzd(paramAttachment)).booleanValue())
    {
      localIntent.addFlags(268435456);
      localIntent.putExtra("androidx.browser.customtabs.extra.user_opt_out", true);
    }
    else
    {
      paramAttachment = zzacu.zzcty;
      if (((Boolean)zzyt.zzpe().zzd(paramAttachment)).booleanValue())
      {
        UserData.zzlg();
        zzaxi.zzb(paramContext, localIntent);
      }
    }
    return start(paramContext, localIntent, paramMonitor);
  }
}
