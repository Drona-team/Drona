package com.google.android.gms.dynamite;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public abstract interface Notification
  extends IInterface
{
  public abstract IObjectWrapper getChat(IObjectWrapper paramIObjectWrapper, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int setTime(IObjectWrapper paramIObjectWrapper, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract IObjectWrapper setTime(IObjectWrapper paramIObjectWrapper, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int speak(IObjectWrapper paramIObjectWrapper, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int zzak()
    throws RemoteException;
}
