package com.google.android.gms.dynamic;

import android.content.Context;
import android.os.IBinder;
import com.google.android.gms.common.GooglePlayServicesUtilLight;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;

@KeepForSdk
public abstract class RemoteCreator<T>
{
  private final String zzic;
  private T zzid;
  
  protected RemoteCreator(String paramString)
  {
    zzic = paramString;
  }
  
  protected abstract Object getRemoteCreator(IBinder paramIBinder);
  
  protected final Object getRemoteCreatorInstance(Context paramContext)
    throws RemoteCreator.RemoteCreatorException
  {
    if (zzid == null)
    {
      Preconditions.checkNotNull(paramContext);
      paramContext = GooglePlayServicesUtilLight.getRemoteContext(paramContext);
      if (paramContext != null)
      {
        paramContext = paramContext.getClassLoader();
        String str = zzic;
        try
        {
          paramContext = paramContext.loadClass(str).newInstance();
          paramContext = (IBinder)paramContext;
          paramContext = getRemoteCreator(paramContext);
          zzid = paramContext;
        }
        catch (IllegalAccessException paramContext)
        {
          throw new RemoteCreatorException("Could not access creator.", paramContext);
        }
        catch (InstantiationException paramContext)
        {
          throw new RemoteCreatorException("Could not instantiate creator.", paramContext);
        }
        catch (ClassNotFoundException paramContext)
        {
          throw new RemoteCreatorException("Could not load creator class.", paramContext);
        }
      }
      else
      {
        throw new RemoteCreatorException("Could not get remote context.");
      }
    }
    return zzid;
  }
  
  @KeepForSdk
  public static class RemoteCreatorException
    extends Exception
  {
    public RemoteCreatorException(String paramString)
    {
      super();
    }
    
    public RemoteCreatorException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}
