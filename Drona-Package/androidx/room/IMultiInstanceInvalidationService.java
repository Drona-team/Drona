package androidx.room;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMultiInstanceInvalidationService
  extends IInterface
{
  public abstract void broadcastInvalidation(int paramInt, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract int registerCallback(IMultiInstanceInvalidationCallback paramIMultiInstanceInvalidationCallback, String paramString)
    throws RemoteException;
  
  public abstract void unregisterCallback(IMultiInstanceInvalidationCallback paramIMultiInstanceInvalidationCallback, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMultiInstanceInvalidationService
  {
    private static final String DESCRIPTOR = "androidx.room.IMultiInstanceInvalidationService";
    static final int TRANSACTION_broadcastInvalidation = 3;
    static final int TRANSACTION_registerCallback = 1;
    static final int TRANSACTION_unregisterCallback = 2;
    
    public Stub()
    {
      attachInterface(this, "androidx.room.IMultiInstanceInvalidationService");
    }
    
    public static IMultiInstanceInvalidationService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("androidx.room.IMultiInstanceInvalidationService");
      if ((localIInterface != null) && ((localIInterface instanceof IMultiInstanceInvalidationService))) {
        return (IMultiInstanceInvalidationService)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      if (paramInt1 != 1598968902)
      {
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 3: 
          paramParcel1.enforceInterface("androidx.room.IMultiInstanceInvalidationService");
          broadcastInvalidation(paramParcel1.readInt(), paramParcel1.createStringArray());
          return true;
        case 2: 
          paramParcel1.enforceInterface("androidx.room.IMultiInstanceInvalidationService");
          unregisterCallback(IMultiInstanceInvalidationCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
        paramParcel1.enforceInterface("androidx.room.IMultiInstanceInvalidationService");
        paramInt1 = registerCallback(IMultiInstanceInvalidationCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel2.writeString("androidx.room.IMultiInstanceInvalidationService");
      return true;
    }
    
    private static class Proxy
      implements IMultiInstanceInvalidationService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return mRemote;
      }
      
      public void broadcastInvalidation(int paramInt, String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("androidx.room.IMultiInstanceInvalidationService");
          localParcel.writeInt(paramInt);
          localParcel.writeStringArray(paramArrayOfString);
          mRemote.transact(3, localParcel, null, 1);
          localParcel.recycle();
          return;
        }
        catch (Throwable paramArrayOfString)
        {
          localParcel.recycle();
          throw paramArrayOfString;
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "androidx.room.IMultiInstanceInvalidationService";
      }
      
      public int registerCallback(IMultiInstanceInvalidationCallback paramIMultiInstanceInvalidationCallback, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("androidx.room.IMultiInstanceInvalidationService");
          if (paramIMultiInstanceInvalidationCallback != null) {
            paramIMultiInstanceInvalidationCallback = paramIMultiInstanceInvalidationCallback.asBinder();
          } else {
            paramIMultiInstanceInvalidationCallback = null;
          }
          localParcel1.writeStrongBinder(paramIMultiInstanceInvalidationCallback);
          localParcel1.writeString(paramString);
          mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          localParcel2.recycle();
          localParcel1.recycle();
          return i;
        }
        catch (Throwable paramIMultiInstanceInvalidationCallback)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramIMultiInstanceInvalidationCallback;
        }
      }
      
      public void unregisterCallback(IMultiInstanceInvalidationCallback paramIMultiInstanceInvalidationCallback, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("androidx.room.IMultiInstanceInvalidationService");
          if (paramIMultiInstanceInvalidationCallback != null) {
            paramIMultiInstanceInvalidationCallback = paramIMultiInstanceInvalidationCallback.asBinder();
          } else {
            paramIMultiInstanceInvalidationCallback = null;
          }
          localParcel1.writeStrongBinder(paramIMultiInstanceInvalidationCallback);
          localParcel1.writeInt(paramInt);
          mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          localParcel2.recycle();
          localParcel1.recycle();
          return;
        }
        catch (Throwable paramIMultiInstanceInvalidationCallback)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramIMultiInstanceInvalidationCallback;
        }
      }
    }
  }
}