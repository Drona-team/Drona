package com.google.android.gms.dynamic;

import android.os.IBinder;
import android.os.IInterface;
import com.google.android.gms.common.annotation.KeepForSdk;
import java.lang.reflect.Field;

@KeepForSdk
public final class ObjectWrapper<T>
  extends IObjectWrapper.Stub
{
  private final T zzib;
  
  private ObjectWrapper(Object paramObject)
  {
    zzib = paramObject;
  }
  
  public static Object unwrap(IObjectWrapper paramIObjectWrapper)
  {
    if ((paramIObjectWrapper instanceof ObjectWrapper)) {
      return zzib;
    }
    IBinder localIBinder = paramIObjectWrapper.asBinder();
    Field[] arrayOfField = localIBinder.getClass().getDeclaredFields();
    int m = arrayOfField.length;
    int i = 0;
    paramIObjectWrapper = null;
    int k;
    for (int j = 0; i < m; j = k)
    {
      Field localField = arrayOfField[i];
      k = j;
      if (!localField.isSynthetic())
      {
        k = j + 1;
        paramIObjectWrapper = localField;
      }
      i += 1;
    }
    if (j == 1)
    {
      if (!paramIObjectWrapper.isAccessible())
      {
        paramIObjectWrapper.setAccessible(true);
        try
        {
          paramIObjectWrapper = paramIObjectWrapper.get(localIBinder);
          return paramIObjectWrapper;
        }
        catch (IllegalAccessException paramIObjectWrapper)
        {
          throw new IllegalArgumentException("Could not access the field in remoteBinder.", paramIObjectWrapper);
        }
        catch (NullPointerException paramIObjectWrapper)
        {
          throw new IllegalArgumentException("Binder object is null.", paramIObjectWrapper);
        }
      }
      throw new IllegalArgumentException("IObjectWrapper declared field not private!");
    }
    i = arrayOfField.length;
    paramIObjectWrapper = new StringBuilder(64);
    paramIObjectWrapper.append("Unexpected number of IObjectWrapper declared fields: ");
    paramIObjectWrapper.append(i);
    throw new IllegalArgumentException(paramIObjectWrapper.toString());
  }
  
  public static IObjectWrapper wrap(Object paramObject)
  {
    return new ObjectWrapper(paramObject);
  }
}
