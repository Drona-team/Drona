package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.internal.base.zaa;
import com.google.android.gms.internal.base.zac;

public final class RealmObject
  extends zaa
  implements ISignInButtonCreator
{
  RealmObject(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.common.internal.ISignInButtonCreator");
  }
  
  public final IObjectWrapper newSignInButton(IObjectWrapper paramIObjectWrapper, int paramInt1, int paramInt2)
    throws RemoteException
  {
    Object localObject = zaa();
    zac.zaa((Parcel)localObject, paramIObjectWrapper);
    ((Parcel)localObject).writeInt(paramInt1);
    ((Parcel)localObject).writeInt(paramInt2);
    paramIObjectWrapper = zaa(1, (Parcel)localObject);
    localObject = IObjectWrapper.Stub.asInterface(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return localObject;
  }
  
  public final IObjectWrapper newSignInButtonFromConfig(IObjectWrapper paramIObjectWrapper, SignInButtonConfig paramSignInButtonConfig)
    throws RemoteException
  {
    Parcel localParcel = zaa();
    zac.zaa(localParcel, paramIObjectWrapper);
    zac.zaa(localParcel, paramSignInButtonConfig);
    paramIObjectWrapper = zaa(2, localParcel);
    paramSignInButtonConfig = IObjectWrapper.Stub.asInterface(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return paramSignInButtonConfig;
  }
}
