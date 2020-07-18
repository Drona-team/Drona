package com.google.android.gms.package_7;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.GuardedBy;
import androidx.collection.ArrayMap;
import com.google.android.gms.common.internal.ShowFirstParty;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.tasks.Tasks;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ShowFirstParty
public class zzaf
{
  private static int zzcp = 0;
  private static final com.google.android.gms.iid.zzaj<Boolean> zzct = zzai.getCryptoKeys().getString("gcm_iid_use_messenger_ipc", true);
  private static String zzcu = null;
  private static boolean zzcv = false;
  private static int zzcw = 0;
  private static int zzcx = 0;
  @GuardedBy("Rpc.class")
  private static BroadcastReceiver zzcy = null;
  private Context mContext;
  private PendingIntent zzaf;
  private Messenger zzaj;
  private Map<String, Object> zzcz = new ArrayMap();
  private Messenger zzda;
  private MessengerCompat zzdb;
  
  public zzaf(Context paramContext)
  {
    mContext = paramContext;
  }
  
  private final Bundle doInBackground(Bundle paramBundle)
    throws IOException
  {
    ConditionVariable localConditionVariable = new ConditionVariable();
    Object localObject2 = unescapeHTML();
    Object localObject1 = getClass();
    for (;;)
    {
      try
      {
        zzcz.put(localObject2, localConditionVariable);
        if (zzaj == null)
        {
          doInBackground(mContext);
          zzaj = new Messenger((Handler)new zzag(this, Looper.getMainLooper()));
        }
        if (zzcu != null)
        {
          if (zzcv) {
            localObject1 = "com.google.iid.TOKEN_REQUEST";
          } else {
            localObject1 = "com.google.android.c2dm.intent.REGISTER";
          }
          localObject1 = new Intent((String)localObject1);
          ((Intent)localObject1).setPackage(zzcu);
          ((Intent)localObject1).putExtras(paramBundle);
          setAlarm((Intent)localObject1);
          paramBundle = new StringBuilder(String.valueOf(localObject2).length() + 5);
          paramBundle.append("|ID|");
          paramBundle.append((String)localObject2);
          paramBundle.append("|");
          ((Intent)localObject1).putExtra("kid", paramBundle.toString());
          paramBundle = new StringBuilder(String.valueOf(localObject2).length() + 5);
          paramBundle.append("|ID|");
          paramBundle.append((String)localObject2);
          paramBundle.append("|");
          ((Intent)localObject1).putExtra("X-kid", paramBundle.toString());
          bool = "com.google.android.gsf".equals(zzcu);
          paramBundle = ((Intent)localObject1).getStringExtra("useGsf");
          if (paramBundle != null) {
            bool = "1".equals(paramBundle);
          }
          if (Log.isLoggable("InstanceID", 3))
          {
            paramBundle = String.valueOf(((Intent)localObject1).getExtras());
            localObject3 = new StringBuilder(String.valueOf(paramBundle).length() + 8);
            ((StringBuilder)localObject3).append("Sending ");
            ((StringBuilder)localObject3).append(paramBundle);
            Log.d("InstanceID", ((StringBuilder)localObject3).toString());
          }
          if (zzda != null)
          {
            ((Intent)localObject1).putExtra("google.messenger", zzaj);
            paramBundle = Message.obtain();
            obj = localObject1;
            localObject3 = zzda;
          }
        }
      }
      catch (Throwable paramBundle)
      {
        boolean bool;
        Object localObject3;
        throw paramBundle;
      }
      try
      {
        ((Messenger)localObject3).send(paramBundle);
      }
      catch (RemoteException paramBundle) {}
    }
    if (Log.isLoggable("InstanceID", 3)) {
      Log.d("InstanceID", "Messenger failed, fallback to startService");
    }
    if (bool) {
      try
      {
        if (zzcy == null)
        {
          zzcy = new zzah(this);
          if (Log.isLoggable("InstanceID", 3)) {
            Log.d("InstanceID", "Registered GSF callback receiver");
          }
          paramBundle = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
          paramBundle.addCategory(mContext.getPackageName());
          mContext.registerReceiver(zzcy, paramBundle, "com.google.android.c2dm.permission.SEND", null);
        }
        mContext.sendBroadcast((Intent)localObject1);
      }
      catch (Throwable paramBundle)
      {
        throw paramBundle;
      }
    }
    ((Intent)localObject1).putExtra("google.messenger", zzaj);
    ((Intent)localObject1).putExtra("messenger2", "1");
    if (zzdb != null)
    {
      paramBundle = Message.obtain();
      obj = localObject1;
      localObject3 = zzdb;
    }
    try
    {
      ((MessengerCompat)localObject3).send(paramBundle);
    }
    catch (RemoteException paramBundle)
    {
      for (;;) {}
    }
    if (Log.isLoggable("InstanceID", 3)) {
      Log.d("InstanceID", "Messenger failed, fallback to startService");
    }
    if (zzcv) {
      mContext.sendBroadcast((Intent)localObject1);
    } else {
      mContext.startService((Intent)localObject1);
    }
    localConditionVariable.block(30000L);
    paramBundle = getClass();
    try
    {
      localObject1 = zzcz.remove(localObject2);
      if ((localObject1 instanceof Bundle))
      {
        localObject1 = (Bundle)localObject1;
        return localObject1;
      }
      if ((localObject1 instanceof String)) {
        throw new IOException((String)localObject1);
      }
      localObject1 = String.valueOf(localObject1);
      localObject2 = new StringBuilder(String.valueOf(localObject1).length() + 12);
      ((StringBuilder)localObject2).append("No response ");
      ((StringBuilder)localObject2).append((String)localObject1);
      Log.w("InstanceID", ((StringBuilder)localObject2).toString());
      throw new IOException("TIMEOUT");
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
    throw new IOException("MISSING_INSTANCEID_SERVICE");
  }
  
  public static String doInBackground(Context paramContext)
  {
    if (zzcu != null) {
      return zzcu;
    }
    zzcw = Process.myUid();
    paramContext = paramContext.getPackageManager();
    boolean bool = PlatformVersion.isAtLeastO();
    int j = 1;
    if (!bool)
    {
      localIterator = paramContext.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0).iterator();
      while (localIterator.hasNext()) {
        if (get(paramContext, nextserviceInfo.packageName, "com.google.android.c2dm.intent.REGISTER"))
        {
          zzcv = false;
          i = 1;
          break label98;
        }
      }
      i = 0;
      label98:
      if (i != 0) {
        return zzcu;
      }
    }
    Iterator localIterator = paramContext.queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0).iterator();
    while (localIterator.hasNext()) {
      if (get(paramContext, nextactivityInfo.packageName, "com.google.iid.TOKEN_REQUEST"))
      {
        zzcv = true;
        i = j;
        break label173;
      }
    }
    int i = 0;
    label173:
    if (i != 0) {
      return zzcu;
    }
    Log.w("InstanceID", "Failed to resolve IID implementation package, falling back");
    if (register(paramContext, "com.google.android.gms"))
    {
      zzcv = PlatformVersion.isAtLeastO();
      return zzcu;
    }
    if ((!PlatformVersion.isAtLeastLollipop()) && (register(paramContext, "com.google.android.gsf")))
    {
      zzcv = false;
      return zzcu;
    }
    Log.w("InstanceID", "Google Play services is missing, unable to get tokens");
    return null;
  }
  
  private final Bundle editComment(Bundle paramBundle)
    throws IOException
  {
    Bundle localBundle = doInBackground(paramBundle);
    if ((localBundle != null) && (localBundle.containsKey("google.messenger")))
    {
      paramBundle = doInBackground(paramBundle);
      if ((paramBundle != null) && (paramBundle.containsKey("google.messenger"))) {
        return null;
      }
    }
    else
    {
      return localBundle;
    }
    return paramBundle;
  }
  
  static String get(Bundle paramBundle)
    throws IOException
  {
    Object localObject;
    if (paramBundle != null)
    {
      String str = paramBundle.getString("registration_id");
      localObject = str;
      if (str == null) {
        localObject = paramBundle.getString("unregistered");
      }
      if (localObject == null)
      {
        localObject = paramBundle.getString("error");
        if (localObject != null) {
          throw new IOException((String)localObject);
        }
        paramBundle = String.valueOf(paramBundle);
        localObject = new StringBuilder(String.valueOf(paramBundle).length() + 29);
        ((StringBuilder)localObject).append("Unexpected response from GCM ");
        ((StringBuilder)localObject).append(paramBundle);
        Log.w("InstanceID", ((StringBuilder)localObject).toString(), new Throwable());
        throw new IOException("SERVICE_NOT_AVAILABLE");
      }
    }
    else
    {
      throw new IOException("SERVICE_NOT_AVAILABLE");
    }
    return localObject;
  }
  
  private static boolean get(PackageManager paramPackageManager, String paramString1, String paramString2)
  {
    if (paramPackageManager.checkPermission("com.google.android.c2dm.permission.SEND", paramString1) == 0) {
      return register(paramPackageManager, paramString1);
    }
    paramPackageManager = new StringBuilder(String.valueOf(paramString1).length() + 56 + String.valueOf(paramString2).length());
    paramPackageManager.append("Possible malicious package ");
    paramPackageManager.append(paramString1);
    paramPackageManager.append(" declares ");
    paramPackageManager.append(paramString2);
    paramPackageManager.append(" without permission");
    Log.w("InstanceID", paramPackageManager.toString());
    return false;
  }
  
  private static int getVersionCode(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      paramContext = localPackageManager.getPackageInfo(doInBackground(paramContext), 0);
      return versionCode;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    return -1;
  }
  
  private final void init(String paramString, Object paramObject)
  {
    Class localClass = getClass();
    try
    {
      Object localObject = zzcz.get(paramString);
      zzcz.put(paramString, paramObject);
      reset(localObject, paramObject);
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public static boolean isInheritable(Context paramContext)
  {
    if (zzcu != null) {
      doInBackground(paramContext);
    }
    return zzcv;
  }
  
  private static boolean register(PackageManager paramPackageManager, String paramString)
  {
    try
    {
      paramPackageManager = paramPackageManager.getApplicationInfo(paramString, 0);
      zzcu = packageName;
      zzcx = uid;
      return true;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager) {}
    return false;
  }
  
  private static void reset(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 instanceof ConditionVariable)) {
      ((ConditionVariable)paramObject1).open();
    }
    if ((paramObject1 instanceof Messenger))
    {
      paramObject1 = (Messenger)paramObject1;
      Message localMessage = Message.obtain();
      obj = paramObject2;
      try
      {
        paramObject1.send(localMessage);
        return;
      }
      catch (RemoteException paramObject1)
      {
        paramObject1 = String.valueOf(paramObject1);
        paramObject2 = new StringBuilder(String.valueOf(paramObject1).length() + 24);
        paramObject2.append("Failed to send response ");
        paramObject2.append(paramObject1);
        Log.w("InstanceID", paramObject2.toString());
      }
    }
  }
  
  private final void setAlarm(Intent paramIntent)
  {
    try
    {
      if (zzaf == null)
      {
        Intent localIntent = new Intent();
        localIntent.setPackage("com.google.example.invalidpackage");
        zzaf = PendingIntent.getBroadcast(mContext, 0, localIntent, 0);
      }
      paramIntent.putExtra("app", zzaf);
      return;
    }
    catch (Throwable paramIntent)
    {
      throw paramIntent;
    }
  }
  
  private static String unescapeHTML()
  {
    try
    {
      int i = zzcp;
      zzcp = i + 1;
      String str = Integer.toString(i);
      return str;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  final Bundle doInBackground(Bundle paramBundle, KeyPair paramKeyPair)
    throws IOException
  {
    int i = getVersionCode(mContext);
    paramBundle.putString("gmsv", Integer.toString(i));
    paramBundle.putString("osv", Integer.toString(Build.VERSION.SDK_INT));
    paramBundle.putString("app_ver", Integer.toString(InstanceID.getVersionNumber(mContext)));
    paramBundle.putString("app_ver_name", InstanceID.getVersion(mContext));
    paramBundle.putString("cliv", "iid-12451000");
    paramBundle.putString("appid", InstanceID.encode(paramKeyPair));
    if ((i >= 12000000) && (((Boolean)zzct.setDoOutput()).booleanValue()))
    {
      paramKeyPair = new DownloadFile(mContext).download(1, paramBundle);
      try
      {
        paramKeyPair = Tasks.await(paramKeyPair);
        return (Bundle)paramKeyPair;
      }
      catch (InterruptedException|ExecutionException paramKeyPair)
      {
        if (Log.isLoggable("InstanceID", 3))
        {
          String str = String.valueOf(paramKeyPair);
          StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 22);
          localStringBuilder.append("Error making request: ");
          localStringBuilder.append(str);
          Log.d("InstanceID", localStringBuilder.toString());
        }
        if (((((Exception)paramKeyPair).getCause() instanceof zzaa)) && (((zzaa)((Exception)paramKeyPair).getCause()).getErrorCode() == 4)) {
          return editComment(paramBundle);
        }
        return null;
      }
    }
    return editComment(paramBundle);
  }
  
  public final void parse(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      if (Log.isLoggable("InstanceID", 3)) {
        Log.d("InstanceID", "Unexpected response: null");
      }
    }
    else
    {
      Object localObject1 = paramIntent.getAction();
      if ((!"com.google.android.c2dm.intent.REGISTRATION".equals(localObject1)) && (!"com.google.android.gms.iid.InstanceID".equals(localObject1)))
      {
        if (Log.isLoggable("InstanceID", 3))
        {
          paramIntent = String.valueOf(paramIntent.getAction());
          if (paramIntent.length() != 0) {
            paramIntent = "Unexpected response ".concat(paramIntent);
          } else {
            paramIntent = new String("Unexpected response ");
          }
          Log.d("InstanceID", paramIntent);
        }
      }
      else
      {
        Object localObject2 = paramIntent.getStringExtra("registration_id");
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = paramIntent.getStringExtra("unregistered");
        }
        if (localObject1 == null)
        {
          Object localObject4 = paramIntent.getStringExtra("error");
          localObject1 = localObject4;
          if (localObject4 == null)
          {
            paramIntent = String.valueOf(paramIntent.getExtras());
            localObject1 = new StringBuilder(String.valueOf(paramIntent).length() + 49);
            ((StringBuilder)localObject1).append("Unexpected response, no error or registration id ");
            ((StringBuilder)localObject1).append(paramIntent);
            Log.w("InstanceID", ((StringBuilder)localObject1).toString());
            return;
          }
          if (Log.isLoggable("InstanceID", 3))
          {
            localObject2 = String.valueOf(localObject4);
            if (((String)localObject2).length() != 0) {
              localObject2 = "Received InstanceID error ".concat((String)localObject2);
            } else {
              localObject2 = new String("Received InstanceID error ");
            }
            Log.d("InstanceID", (String)localObject2);
          }
          localObject2 = null;
          Object localObject3 = null;
          if (((String)localObject4).startsWith("|"))
          {
            String[] arrayOfString = ((String)localObject4).split("\\|");
            if (!"ID".equals(arrayOfString[1]))
            {
              localObject1 = String.valueOf(localObject4);
              if (((String)localObject1).length() != 0) {
                localObject1 = "Unexpected structured response ".concat((String)localObject1);
              } else {
                localObject1 = new String("Unexpected structured response ");
              }
              Log.w("InstanceID", (String)localObject1);
            }
            if (arrayOfString.length > 2)
            {
              localObject2 = arrayOfString[2];
              localObject3 = arrayOfString[3];
              localObject1 = localObject3;
              if (((String)localObject3).startsWith(":")) {
                localObject1 = ((String)localObject3).substring(1);
              }
            }
            for (;;)
            {
              localObject3 = localObject1;
              break;
              localObject1 = "UNKNOWN";
              localObject2 = localObject3;
            }
            paramIntent.putExtra("error", (String)localObject1);
            localObject1 = localObject3;
          }
          if (localObject2 == null)
          {
            paramIntent = getClass();
            try
            {
              localObject2 = zzcz.keySet().iterator();
              while (((Iterator)localObject2).hasNext())
              {
                localObject3 = (String)((Iterator)localObject2).next();
                localObject4 = zzcz.get(localObject3);
                zzcz.put(localObject3, localObject1);
                reset(localObject4, localObject1);
              }
              return;
            }
            catch (Throwable localThrowable)
            {
              throw localThrowable;
            }
          }
          init((String)localObject2, localThrowable);
          return;
        }
        localObject2 = Pattern.compile("\\|ID\\|([^|]+)\\|:?+(.*)").matcher(localThrowable);
        if (!((Matcher)localObject2).matches())
        {
          if (Log.isLoggable("InstanceID", 3))
          {
            paramIntent = String.valueOf(localThrowable);
            if (paramIntent.length() != 0) {
              paramIntent = "Unexpected response string: ".concat(paramIntent);
            } else {
              paramIntent = new String("Unexpected response string: ");
            }
            Log.d("InstanceID", paramIntent);
          }
        }
        else
        {
          String str = ((Matcher)localObject2).group(1);
          localObject2 = ((Matcher)localObject2).group(2);
          paramIntent = paramIntent.getExtras();
          paramIntent.putString("registration_id", (String)localObject2);
          init(str, paramIntent);
        }
      }
    }
  }
  
  public final void processMessage(Message paramMessage)
  {
    if (paramMessage == null) {
      return;
    }
    if ((obj instanceof Intent))
    {
      Object localObject = (Intent)obj;
      ((Intent)localObject).setExtrasClassLoader(com.google.android.gms.iid.MessengerCompat.class.getClassLoader());
      if (((Intent)localObject).hasExtra("google.messenger"))
      {
        localObject = ((Intent)localObject).getParcelableExtra("google.messenger");
        if ((localObject instanceof MessengerCompat)) {
          zzdb = ((MessengerCompat)localObject);
        }
        if ((localObject instanceof Messenger)) {
          zzda = ((Messenger)localObject);
        }
      }
      parse((Intent)obj);
      return;
    }
    Log.w("InstanceID", "Dropping invalid message");
  }
}
