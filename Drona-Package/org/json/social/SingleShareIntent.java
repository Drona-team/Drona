package org.json.social;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import java.io.PrintStream;

public abstract class SingleShareIntent
  extends ShareIntent
{
  protected String appStoreURL = null;
  protected String playStoreURL = null;
  
  public SingleShareIntent(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  public void open(ReadableMap paramReadableMap)
    throws ActivityNotFoundException
  {
    System.out.println(getPackage());
    if ((getPackage() != null) || (getDefaultWebLink() != null) || (getPlayStoreLink() != null)) {
      if (ShareIntent.isPackageInstalled(getPackage(), reactContext))
      {
        System.out.println("INSTALLED");
        getIntent().setPackage(getPackage());
        super.open(paramReadableMap);
      }
      else
      {
        System.out.println("NOT INSTALLED");
        String str = "";
        if (getDefaultWebLink() != null) {
          str = getDefaultWebLink().replace("{url}", ShareIntent.urlEncode(paramReadableMap.getString("url"))).replace("{message}", ShareIntent.urlEncode(paramReadableMap.getString("message")));
        } else if (getPlayStoreLink() != null) {
          str = getPlayStoreLink();
        }
        setIntent(new Intent(new Intent("android.intent.action.VIEW", Uri.parse(str))));
      }
    }
    super.open(paramReadableMap);
  }
  
  protected void openIntentChooser()
    throws ActivityNotFoundException
  {
    if ((options.hasKey("forceDialog")) && (options.getBoolean("forceDialog")))
    {
      Activity localActivity = reactContext.getCurrentActivity();
      if (localActivity == null)
      {
        TargetChosenReceiver.sendCallback(false, new Object[] { "Something went wrong" });
        return;
      }
      if (TargetChosenReceiver.isSupported())
      {
        localObject = TargetChosenReceiver.getSharingSenderIntent(reactContext);
        localObject = Intent.createChooser(getIntent(), chooserTitle, (IntentSender)localObject);
        ((Intent)localObject).setFlags(1073741824);
        localActivity.startActivityForResult((Intent)localObject, 16845);
        return;
      }
      Object localObject = Intent.createChooser(getIntent(), chooserTitle);
      ((Intent)localObject).setFlags(1073741824);
      localActivity.startActivityForResult((Intent)localObject, 16845);
      TargetChosenReceiver.sendCallback(true, new Object[] { Boolean.valueOf(true), "OK" });
      return;
    }
    getIntent().setFlags(268435456);
    reactContext.startActivity(getIntent());
    TargetChosenReceiver.sendCallback(true, new Object[] { Boolean.valueOf(true), getIntent().getPackage() });
  }
}
