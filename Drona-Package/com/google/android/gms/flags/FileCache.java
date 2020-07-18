package com.google.android.gms.flags;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.LoadingException;
import com.google.android.gms.dynamite.DynamiteModule.VersionPolicy;

public final class FileCache
{
  private Object context = null;
  private boolean directory = false;
  
  public FileCache() {}
  
  public final java.lang.Object initialize(Flag paramFlag)
  {
    try
    {
      if (!directory)
      {
        paramFlag = paramFlag.get();
        return paramFlag;
      }
      return paramFlag.get(context);
    }
    catch (Throwable paramFlag)
    {
      throw paramFlag;
    }
  }
  
  public final void initialize(Context paramContext)
  {
    try
    {
      if (directory) {
        return;
      }
      java.lang.Object localObject = DynamiteModule.PREFER_HIGHEST_OR_LOCAL_VERSION_NO_FORCE_STAGING;
      try
      {
        localObject = IExtensionHost.Stub.asInterface(DynamiteModule.load(paramContext, (DynamiteModule.VersionPolicy)localObject, "com.google.android.gms.flags").instantiate("com.google.android.gms.flags.impl.FlagProviderImpl"));
        context = ((Object)localObject);
        localObject = context;
        ((Object)localObject).init(ObjectWrapper.wrap(paramContext));
        directory = true;
      }
      catch (DynamiteModule.LoadingException|RemoteException paramContext)
      {
        Log.w("FlagValueProvider", "Failed to initialize flags module.", paramContext);
      }
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
}
