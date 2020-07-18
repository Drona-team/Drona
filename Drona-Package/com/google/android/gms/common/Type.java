package com.google.android.gms.common;

import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.LocationCallback;
import com.google.android.gms.common.internal.LocationCallback.Stub;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

abstract class Type
  extends LocationCallback.Stub
{
  private int hashCode;
  
  protected Type(byte[] paramArrayOfByte)
  {
    boolean bool;
    if (paramArrayOfByte.length == 25) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    hashCode = Arrays.hashCode(paramArrayOfByte);
  }
  
  protected static byte[] encode(String paramString)
  {
    try
    {
      paramString = paramString.getBytes("ISO-8859-1");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new AssertionError(paramString);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject != null)
    {
      if (!(paramObject instanceof LocationCallback)) {
        return false;
      }
      paramObject = (LocationCallback)paramObject;
      try
      {
        int i = paramObject.zzc();
        int j = hashCode();
        if (i != j) {
          return false;
        }
        paramObject = paramObject.zzb();
        if (paramObject == null) {
          return false;
        }
        paramObject = ObjectWrapper.unwrap(paramObject);
        paramObject = (byte[])paramObject;
        boolean bool = Arrays.equals(getBytes(), paramObject);
        return bool;
      }
      catch (RemoteException paramObject)
      {
        Log.e("GoogleCertificates", "Failed to get Google certificates from remote", paramObject);
      }
    }
    return false;
  }
  
  abstract byte[] getBytes();
  
  public int hashCode()
  {
    return hashCode;
  }
  
  public final IObjectWrapper zzb()
  {
    return ObjectWrapper.wrap(getBytes());
  }
  
  public final int zzc()
  {
    return hashCode();
  }
}
