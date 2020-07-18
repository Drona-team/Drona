package com.facebook.react.modules.intent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="IntentAndroid")
public class IntentModule
  extends ReactContextBaseJavaModule
{
  public static final String NAME = "IntentAndroid";
  
  public IntentModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  public void canOpenURL(String paramString, Promise paramPromise)
  {
    if ((paramString != null) && (!paramString.isEmpty())) {
      try
      {
        Object localObject = new Intent("android.intent.action.VIEW", Uri.parse(paramString));
        ((Intent)localObject).addFlags(268435456);
        localObject = ((Intent)localObject).resolveActivity(getReactApplicationContext().getPackageManager());
        boolean bool;
        if (localObject != null) {
          bool = true;
        } else {
          bool = false;
        }
        paramPromise.resolve(Boolean.valueOf(bool));
        return;
      }
      catch (Exception localException)
      {
        StringBuilder localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append("Could not check if URL '");
        localStringBuilder2.append(paramString);
        localStringBuilder2.append("' can be opened: ");
        localStringBuilder2.append(localException.getMessage());
        paramPromise.reject(new JSApplicationIllegalArgumentException(localStringBuilder2.toString()));
        return;
      }
    }
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("Invalid URL: ");
    localStringBuilder1.append(paramString);
    paramPromise.reject(new JSApplicationIllegalArgumentException(localStringBuilder1.toString()));
  }
  
  public void getInitialURL(Promise paramPromise)
  {
    try
    {
      Object localObject2 = getCurrentActivity();
      localStringBuilder = null;
      Object localObject1 = localStringBuilder;
      if (localObject2 != null)
      {
        localObject1 = ((Activity)localObject2).getIntent();
        localObject2 = ((Intent)localObject1).getAction();
        Uri localUri = ((Intent)localObject1).getData();
        localObject1 = localStringBuilder;
        if (localUri != null)
        {
          boolean bool = "android.intent.action.VIEW".equals(localObject2);
          if (!bool)
          {
            bool = "android.nfc.action.NDEF_DISCOVERED".equals(localObject2);
            localObject1 = localStringBuilder;
            if (!bool) {}
          }
          else
          {
            localObject1 = localUri.toString();
          }
        }
      }
      paramPromise.resolve(localObject1);
      return;
    }
    catch (Exception localException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Could not get the initial URL : ");
      localStringBuilder.append(localException.getMessage());
      paramPromise.reject(new JSApplicationIllegalArgumentException(localStringBuilder.toString()));
    }
  }
  
  public String getName()
  {
    return "IntentAndroid";
  }
  
  public void openSettings(Promise paramPromise)
  {
    try
    {
      Intent localIntent = new Intent();
      localObject = getCurrentActivity();
      String str = getReactApplicationContext().getPackageName();
      localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
      localIntent.addCategory("android.intent.category.DEFAULT");
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("package:");
      localStringBuilder.append(str);
      localIntent.setData(Uri.parse(localStringBuilder.toString()));
      localIntent.addFlags(268435456);
      localIntent.addFlags(1073741824);
      localIntent.addFlags(8388608);
      ((Activity)localObject).startActivity(localIntent);
      paramPromise.resolve(Boolean.valueOf(true));
      return;
    }
    catch (Exception localException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Could not open the Settings: ");
      ((StringBuilder)localObject).append(localException.getMessage());
      paramPromise.reject(new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString()));
    }
  }
  
  public void openURL(String paramString, Promise paramPromise)
  {
    if ((paramString != null) && (!paramString.isEmpty())) {
      try
      {
        localObject2 = getCurrentActivity();
        Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse(paramString).normalizeScheme());
        String str = getReactApplicationContext().getPackageName();
        Object localObject1 = localIntent.resolveActivity(getReactApplicationContext().getPackageManager());
        if (localObject1 != null) {
          localObject1 = ((ComponentName)localObject1).getPackageName();
        } else {
          localObject1 = "";
        }
        if (localObject2 != null)
        {
          boolean bool = str.equals(localObject1);
          if (bool) {}
        }
        else
        {
          localIntent.addFlags(268435456);
        }
        if (localObject2 != null) {
          ((Activity)localObject2).startActivity(localIntent);
        } else {
          getReactApplicationContext().startActivity(localIntent);
        }
        paramPromise.resolve(Boolean.valueOf(true));
        return;
      }
      catch (Exception localException)
      {
        Object localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Could not open URL '");
        ((StringBuilder)localObject2).append(paramString);
        ((StringBuilder)localObject2).append("': ");
        ((StringBuilder)localObject2).append(localException.getMessage());
        paramPromise.reject(new JSApplicationIllegalArgumentException(((StringBuilder)localObject2).toString()));
        return;
      }
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid URL: ");
    localStringBuilder.append(paramString);
    paramPromise.reject(new JSApplicationIllegalArgumentException(localStringBuilder.toString()));
  }
  
  public void sendIntent(String paramString, ReadableArray paramReadableArray, Promise paramPromise)
  {
    if ((paramString != null) && (!paramString.isEmpty()))
    {
      Intent localIntent = new Intent(paramString);
      if (localIntent.resolveActivity(getReactApplicationContext().getPackageManager()) == null)
      {
        paramReadableArray = new StringBuilder();
        paramReadableArray.append("Could not launch Intent with action ");
        paramReadableArray.append(paramString);
        paramReadableArray.append(".");
        paramPromise.reject(new JSApplicationIllegalArgumentException(paramReadableArray.toString()));
        return;
      }
      if (paramReadableArray != null)
      {
        int i = 0;
        while (i < paramReadableArray.size())
        {
          ReadableMap localReadableMap = paramReadableArray.getMap(i);
          paramString = localReadableMap.keySetIterator().nextKey();
          ReadableType localReadableType = localReadableMap.getType(paramString);
          switch (1.$SwitchMap$com$facebook$react$bridge$ReadableType[localReadableType.ordinal()])
          {
          default: 
            paramReadableArray = new StringBuilder();
            paramReadableArray.append("Extra type for ");
            paramReadableArray.append(paramString);
            paramReadableArray.append(" not supported.");
            paramPromise.reject(new JSApplicationIllegalArgumentException(paramReadableArray.toString()));
            return;
          case 3: 
            localIntent.putExtra(paramString, localReadableMap.getBoolean(paramString));
            break;
          case 2: 
            localIntent.putExtra(paramString, Double.valueOf(localReadableMap.getDouble(paramString)));
            break;
          case 1: 
            localIntent.putExtra(paramString, localReadableMap.getString(paramString));
          }
          i += 1;
        }
      }
      getReactApplicationContext().startActivity(localIntent);
      return;
    }
    paramReadableArray = new StringBuilder();
    paramReadableArray.append("Invalid Action: ");
    paramReadableArray.append(paramString);
    paramReadableArray.append(".");
    paramPromise.reject(new JSApplicationIllegalArgumentException(paramReadableArray.toString()));
  }
}
