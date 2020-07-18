package org.json.social;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import org.json.ShareFile;
import org.json.ShareFiles;

public abstract class ShareIntent
{
  protected String chooserTitle = "Share";
  protected ShareFile fileShare;
  protected Intent intent;
  protected ReadableMap options;
  protected final ReactApplicationContext reactContext;
  
  public ShareIntent(ReactApplicationContext paramReactApplicationContext)
  {
    reactContext = paramReactApplicationContext;
    setIntent(new Intent("android.intent.action.SEND"));
    getIntent().setType("text/plain");
  }
  
  public static boolean hasValidKey(String paramString, ReadableMap paramReadableMap)
  {
    return (paramReadableMap != null) && (paramReadableMap.hasKey(paramString)) && (!paramReadableMap.isNull(paramString));
  }
  
  public static boolean isPackageInstalled(String paramString, Context paramContext)
  {
    paramContext = paramContext.getPackageManager();
    try
    {
      paramContext.getPackageInfo(paramString, 1);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      for (;;) {}
    }
    return false;
  }
  
  protected static String urlEncode(String paramString)
  {
    try
    {
      localObject = URLEncoder.encode(paramString, "UTF-8");
      return localObject;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      Object localObject;
      for (;;) {}
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("URLEncoder.encode() failed for ");
    ((StringBuilder)localObject).append(paramString);
    throw new RuntimeException(((StringBuilder)localObject).toString());
  }
  
  protected abstract String getDefaultWebLink();
  
  protected ShareFile getFileShare(ReadableMap paramReadableMap)
  {
    if (hasValidKey("type", paramReadableMap)) {
      return new ShareFile(paramReadableMap.getString("url"), paramReadableMap.getString("type"), reactContext);
    }
    return new ShareFile(paramReadableMap.getString("url"), reactContext);
  }
  
  protected ShareFiles getFileShares(ReadableMap paramReadableMap)
  {
    if (hasValidKey("type", paramReadableMap)) {
      return new ShareFiles(paramReadableMap.getArray("urls"), paramReadableMap.getString("type"), reactContext);
    }
    return new ShareFiles(paramReadableMap.getArray("urls"), reactContext);
  }
  
  protected Intent getIntent()
  {
    return intent;
  }
  
  protected Intent[] getIntentsToViewFile(Intent paramIntent, Uri paramUri)
  {
    Object localObject = reactContext.getPackageManager();
    int i = 0;
    localObject = ((PackageManager)localObject).queryIntentActivities(paramIntent, 0);
    Intent[] arrayOfIntent = new Intent[((List)localObject).size()];
    while (i < ((List)localObject).size())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((List)localObject).get(i);
      String str = activityInfo.packageName;
      Intent localIntent = new Intent();
      localIntent.setComponent(new ComponentName(str, activityInfo.name));
      localIntent.setAction("android.intent.action.VIEW");
      localIntent.setDataAndType(paramUri, paramIntent.getType());
      localIntent.addFlags(1);
      arrayOfIntent[i] = new Intent(localIntent);
      i += 1;
    }
    return arrayOfIntent;
  }
  
  protected abstract String getPackage();
  
  protected abstract String getPlayStoreLink();
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    options = paramReadableMap;
    if (hasValidKey("subject", paramReadableMap)) {
      getIntent().putExtra("android.intent.extra.SUBJECT", paramReadableMap.getString("subject"));
    }
    if (hasValidKey("title", paramReadableMap)) {
      chooserTitle = paramReadableMap.getString("title");
    }
    String str = "";
    if (hasValidKey("message", paramReadableMap)) {
      str = paramReadableMap.getString("message");
    }
    Object localObject;
    StringBuilder localStringBuilder;
    if (hasValidKey("urls", paramReadableMap))
    {
      localObject = getFileShares(paramReadableMap);
      if (((ShareFiles)localObject).isFile())
      {
        paramReadableMap = ((ShareFiles)localObject).getURI();
        getIntent().setAction("android.intent.action.SEND_MULTIPLE");
        getIntent().setType(((ShareFiles)localObject).getType());
        getIntent().putParcelableArrayListExtra("android.intent.extra.STREAM", paramReadableMap);
        getIntent().addFlags(1);
        if (!TextUtils.isEmpty(str)) {
          getIntent().putExtra("android.intent.extra.TEXT", str);
        }
      }
      else
      {
        if (!TextUtils.isEmpty(str))
        {
          localObject = getIntent();
          localStringBuilder = new StringBuilder();
          localStringBuilder.append(str);
          localStringBuilder.append(" ");
          localStringBuilder.append(paramReadableMap.getArray("urls").toString());
          ((Intent)localObject).putExtra("android.intent.extra.TEXT", localStringBuilder.toString());
          return;
        }
        getIntent().putExtra("android.intent.extra.TEXT", paramReadableMap.getArray("urls").toString());
      }
    }
    else if (hasValidKey("url", paramReadableMap))
    {
      fileShare = getFileShare(paramReadableMap);
      if (fileShare.isFile())
      {
        paramReadableMap = fileShare.getURI();
        getIntent().setType(fileShare.getType());
        getIntent().putExtra("android.intent.extra.STREAM", paramReadableMap);
        getIntent().addFlags(1);
        if (!TextUtils.isEmpty(str)) {
          getIntent().putExtra("android.intent.extra.TEXT", str);
        }
      }
      else
      {
        if (!TextUtils.isEmpty(str))
        {
          localObject = getIntent();
          localStringBuilder = new StringBuilder();
          localStringBuilder.append(str);
          localStringBuilder.append(" ");
          localStringBuilder.append(paramReadableMap.getString("url"));
          ((Intent)localObject).putExtra("android.intent.extra.TEXT", localStringBuilder.toString());
          return;
        }
        getIntent().putExtra("android.intent.extra.TEXT", paramReadableMap.getString("url"));
      }
    }
    else if (!TextUtils.isEmpty(str))
    {
      getIntent().putExtra("android.intent.extra.TEXT", str);
    }
  }
  
  protected void openIntentChooser()
    throws ActivityNotFoundException
  {
    Object localObject2 = reactContext;
    Object localObject1 = this;
    Activity localActivity = ((ReactContext)localObject2).getCurrentActivity();
    if (localActivity == null)
    {
      TargetChosenReceiver.sendCallback(false, new Object[] { "Something went wrong" });
      return;
    }
    Object localObject3 = null;
    if (TargetChosenReceiver.isSupported())
    {
      localObject3 = reactContext;
      localObject2 = localObject1;
      localObject3 = TargetChosenReceiver.getSharingSenderIntent((ReactContext)localObject3);
      localObject1 = localObject3;
      localObject2 = Intent.createChooser(((ShareIntent)localObject2).getIntent(), chooserTitle, (IntentSender)localObject3);
    }
    else
    {
      localObject2 = Intent.createChooser(((ShareIntent)localObject1).getIntent(), chooserTitle);
      localObject1 = localObject3;
    }
    localObject3 = this;
    ((Intent)localObject2).setFlags(1073741824);
    if ((hasValidKey("showAppsToView", options)) && (hasValidKey("url", options)))
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.setType(fileShare.getType());
      ((Intent)localObject2).putExtra("android.intent.extra.INITIAL_INTENTS", ((ShareIntent)localObject3).getIntentsToViewFile(localIntent, fileShare.getURI()));
    }
    localActivity.startActivityForResult((Intent)localObject2, 16845);
    if (localObject1 == null) {
      TargetChosenReceiver.sendCallback(true, new Object[] { Boolean.valueOf(true), "OK" });
    }
  }
  
  protected void setIntent(Intent paramIntent)
  {
    intent = paramIntent;
  }
}
