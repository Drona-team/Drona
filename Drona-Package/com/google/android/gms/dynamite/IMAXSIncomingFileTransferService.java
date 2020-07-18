package com.google.android.gms.dynamite;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public abstract interface IMAXSIncomingFileTransferService
  extends IInterface
{
  public abstract IObjectWrapper createChat(IObjectWrapper paramIObjectWrapper1, String paramString, int paramInt, IObjectWrapper paramIObjectWrapper2)
    throws RemoteException;
  
  public abstract IObjectWrapper getChat(IObjectWrapper paramIObjectWrapper1, String paramString, int paramInt, IObjectWrapper paramIObjectWrapper2)
    throws RemoteException;
}
