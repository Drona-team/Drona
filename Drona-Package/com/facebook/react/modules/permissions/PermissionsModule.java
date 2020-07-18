package com.facebook.react.modules.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build.VERSION;
import android.os.Process;
import android.util.SparseArray;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import java.util.ArrayList;

@ReactModule(name="PermissionsAndroid")
public class PermissionsModule
  extends ReactContextBaseJavaModule
  implements PermissionListener
{
  private static final String ERROR_INVALID_ACTIVITY = "E_INVALID_ACTIVITY";
  public static final String NAME = "PermissionsAndroid";
  private final String DENIED = "denied";
  private final String GRANTED = "granted";
  private final String NEVER_ASK_AGAIN = "never_ask_again";
  private final SparseArray<Callback> mCallbacks = new SparseArray();
  private int mRequestCode = 0;
  
  public PermissionsModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private PermissionAwareActivity getPermissionAwareActivity()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity != null)
    {
      if ((localActivity instanceof PermissionAwareActivity)) {
        return (PermissionAwareActivity)localActivity;
      }
      throw new IllegalStateException("Tried to use permissions API but the host Activity doesn't implement PermissionAwareActivity.");
    }
    throw new IllegalStateException("Tried to use permissions API while not attached to an Activity.");
  }
  
  public void checkPermission(String paramString, Promise paramPromise)
  {
    Context localContext = getReactApplicationContext().getBaseContext();
    int i = Build.VERSION.SDK_INT;
    boolean bool2 = false;
    boolean bool1 = false;
    if (i < 23)
    {
      if (localContext.checkPermission(paramString, Process.myPid(), Process.myUid()) == 0) {
        bool1 = true;
      }
      paramPromise.resolve(Boolean.valueOf(bool1));
      return;
    }
    bool1 = bool2;
    if (localContext.checkSelfPermission(paramString) == 0) {
      bool1 = true;
    }
    paramPromise.resolve(Boolean.valueOf(bool1));
  }
  
  public String getName()
  {
    return "PermissionsAndroid";
  }
  
  public boolean onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    ((Callback)mCallbacks.get(paramInt)).invoke(new Object[] { paramArrayOfInt, getPermissionAwareActivity() });
    mCallbacks.remove(paramInt);
    return mCallbacks.size() == 0;
  }
  
  public void requestMultiplePermissions(ReadableArray paramReadableArray, final Promise paramPromise)
  {
    final WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    final ArrayList localArrayList = new ArrayList();
    Context localContext = getReactApplicationContext().getBaseContext();
    int j = 0;
    int i = 0;
    Object localObject;
    while (j < paramReadableArray.size())
    {
      String str = paramReadableArray.getString(j);
      if (Build.VERSION.SDK_INT < 23)
      {
        if (localContext.checkPermission(str, Process.myPid(), Process.myUid()) == 0) {
          localObject = "granted";
        } else {
          localObject = "denied";
        }
        localWritableNativeMap.putString(str, (String)localObject);
        i += 1;
      }
      else if (localContext.checkSelfPermission(str) == 0)
      {
        localWritableNativeMap.putString(str, "granted");
        i += 1;
      }
      else
      {
        localArrayList.add(str);
      }
      j += 1;
    }
    if (paramReadableArray.size() == i)
    {
      paramPromise.resolve(localWritableNativeMap);
      return;
    }
    try
    {
      paramReadableArray = getPermissionAwareActivity();
      localObject = mCallbacks;
      i = mRequestCode;
      ((SparseArray)localObject).put(i, new Callback()
      {
        public void invoke(Object... paramAnonymousVarArgs)
        {
          int i = 0;
          int[] arrayOfInt = (int[])paramAnonymousVarArgs[0];
          paramAnonymousVarArgs = (PermissionAwareActivity)paramAnonymousVarArgs[1];
          while (i < localArrayList.size())
          {
            String str = (String)localArrayList.get(i);
            if ((arrayOfInt.length > 0) && (arrayOfInt[i] == 0)) {
              localWritableNativeMap.putString(str, "granted");
            } else if (paramAnonymousVarArgs.shouldShowRequestPermissionRationale(str)) {
              localWritableNativeMap.putString(str, "denied");
            } else {
              localWritableNativeMap.putString(str, "never_ask_again");
            }
            i += 1;
          }
          paramPromise.resolve(localWritableNativeMap);
        }
      });
      localObject = localArrayList.toArray(new String[0]);
      localObject = (String[])localObject;
      i = mRequestCode;
      paramReadableArray.requestPermissions((String[])localObject, i, this);
      mRequestCode += 1;
      return;
    }
    catch (IllegalStateException paramReadableArray)
    {
      paramPromise.reject("E_INVALID_ACTIVITY", paramReadableArray);
    }
  }
  
  public void requestPermission(final String paramString, final Promise paramPromise)
  {
    Object localObject = getReactApplicationContext().getBaseContext();
    if (Build.VERSION.SDK_INT < 23)
    {
      if (((Context)localObject).checkPermission(paramString, Process.myPid(), Process.myUid()) == 0) {
        paramString = "granted";
      } else {
        paramString = "denied";
      }
      paramPromise.resolve(paramString);
      return;
    }
    if (((Context)localObject).checkSelfPermission(paramString) == 0)
    {
      paramPromise.resolve("granted");
      return;
    }
    try
    {
      localObject = getPermissionAwareActivity();
      SparseArray localSparseArray = mCallbacks;
      int i = mRequestCode;
      localSparseArray.put(i, new Callback()
      {
        public void invoke(Object... paramAnonymousVarArgs)
        {
          int[] arrayOfInt = (int[])paramAnonymousVarArgs[0];
          if ((arrayOfInt.length > 0) && (arrayOfInt[0] == 0))
          {
            paramPromise.resolve("granted");
            return;
          }
          if (((PermissionAwareActivity)paramAnonymousVarArgs[1]).shouldShowRequestPermissionRationale(paramString))
          {
            paramPromise.resolve("denied");
            return;
          }
          paramPromise.resolve("never_ask_again");
        }
      });
      i = mRequestCode;
      ((PermissionAwareActivity)localObject).requestPermissions(new String[] { paramString }, i, this);
      mRequestCode += 1;
      return;
    }
    catch (IllegalStateException paramString)
    {
      paramPromise.reject("E_INVALID_ACTIVITY", paramString);
    }
  }
  
  public void shouldShowRequestPermissionRationale(String paramString, Promise paramPromise)
  {
    if (Build.VERSION.SDK_INT < 23)
    {
      paramPromise.resolve(Boolean.valueOf(false));
      return;
    }
    try
    {
      paramPromise.resolve(Boolean.valueOf(getPermissionAwareActivity().shouldShowRequestPermissionRationale(paramString)));
      return;
    }
    catch (IllegalStateException paramString)
    {
      paramPromise.reject("E_INVALID_ACTIVITY", paramString);
    }
  }
}