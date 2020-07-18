package com.android.vending.billing;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface IInAppBillingService
  extends IInterface
{
  public abstract Bundle acknowledgePurchaseExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract int consumePurchase(int paramInt, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract Bundle consumePurchaseExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract Bundle getBuyIntent(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
    throws RemoteException;
  
  public abstract Bundle getBuyIntentExtraParams(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, Bundle paramBundle)
    throws RemoteException;
  
  public abstract Bundle getBuyIntentToReplaceSkus(int paramInt, String paramString1, List paramList, String paramString2, String paramString3, String paramString4)
    throws RemoteException;
  
  public abstract Bundle getPurchaseHistory(int paramInt, String paramString1, String paramString2, String paramString3, Bundle paramBundle)
    throws RemoteException;
  
  public abstract Bundle getPurchases(int paramInt, String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract Bundle getPurchasesExtraParams(int paramInt, String paramString1, String paramString2, String paramString3, Bundle paramBundle)
    throws RemoteException;
  
  public abstract Bundle getSkuDetails(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract Bundle getSkuDetailsExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle1, Bundle paramBundle2)
    throws RemoteException;
  
  public abstract Bundle getSubscriptionManagementIntent(int paramInt, String paramString1, String paramString2, String paramString3, Bundle paramBundle)
    throws RemoteException;
  
  public abstract int isBillingSupported(int paramInt, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract int isBillingSupportedExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract int stub(int paramInt, String paramString1, String paramString2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IInAppBillingService
  {
    private static final String DESCRIPTOR = "com.android.vending.billing.IInAppBillingService";
    static final int TRANSACTION_acknowledgePurchaseExtraParams = 902;
    static final int TRANSACTION_consumePurchase = 5;
    static final int TRANSACTION_consumePurchaseExtraParams = 12;
    static final int TRANSACTION_getBuyIntent = 3;
    static final int TRANSACTION_getBuyIntentExtraParams = 8;
    static final int TRANSACTION_getBuyIntentToReplaceSkus = 7;
    static final int TRANSACTION_getPurchaseHistory = 9;
    static final int TRANSACTION_getPurchases = 4;
    static final int TRANSACTION_getPurchasesExtraParams = 11;
    static final int TRANSACTION_getSkuDetails = 2;
    static final int TRANSACTION_getSkuDetailsExtraParams = 901;
    static final int TRANSACTION_getSubscriptionManagementIntent = 801;
    static final int TRANSACTION_isBillingSupported = 1;
    static final int TRANSACTION_isBillingSupportedExtraParams = 10;
    static final int TRANSACTION_stub = 6;
    
    public Stub()
    {
      attachInterface(this, "com.android.vending.billing.IInAppBillingService");
    }
    
    public static IInAppBillingService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.android.vending.billing.IInAppBillingService");
      if ((localIInterface != null) && ((localIInterface instanceof IInAppBillingService))) {
        return (IInAppBillingService)localIInterface;
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
      String str1 = null;
      String str2 = null;
      String str3 = null;
      Object localObject = null;
      if (paramInt1 != 801)
      {
        if (paramInt1 != 1598968902)
        {
          switch (paramInt1)
          {
          default: 
            switch (paramInt1)
            {
            default: 
              return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
            case 902: 
              paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
              paramInt1 = paramParcel1.readInt();
              str1 = paramParcel1.readString();
              str2 = paramParcel1.readString();
              if (paramParcel1.readInt() != 0) {
                localObject = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
              }
              paramParcel1 = acknowledgePurchaseExtraParams(paramInt1, str1, str2, (Bundle)localObject);
              paramParcel2.writeNoException();
              if (paramParcel1 != null)
              {
                paramParcel2.writeInt(1);
                paramParcel1.writeToParcel(paramParcel2, 1);
                return true;
              }
              paramParcel2.writeInt(0);
              return true;
            }
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = paramParcel1.readInt();
            str1 = paramParcel1.readString();
            str2 = paramParcel1.readString();
            if (paramParcel1.readInt() != 0) {
              localObject = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            } else {
              localObject = null;
            }
            if (paramParcel1.readInt() != 0) {
              paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            } else {
              paramParcel1 = null;
            }
            paramParcel1 = getSkuDetailsExtraParams(paramInt1, str1, str2, (Bundle)localObject, paramParcel1);
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 12: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = paramParcel1.readInt();
            str2 = paramParcel1.readString();
            str3 = paramParcel1.readString();
            localObject = str1;
            if (paramParcel1.readInt() != 0) {
              localObject = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            }
            paramParcel1 = consumePurchaseExtraParams(paramInt1, str2, str3, (Bundle)localObject);
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 11: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = paramParcel1.readInt();
            localObject = paramParcel1.readString();
            str1 = paramParcel1.readString();
            str2 = paramParcel1.readString();
            if (paramParcel1.readInt() != 0) {
              paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            } else {
              paramParcel1 = null;
            }
            paramParcel1 = getPurchasesExtraParams(paramInt1, (String)localObject, str1, str2, paramParcel1);
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 10: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = paramParcel1.readInt();
            str1 = paramParcel1.readString();
            str3 = paramParcel1.readString();
            localObject = str2;
            if (paramParcel1.readInt() != 0) {
              localObject = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            }
            paramInt1 = isBillingSupportedExtraParams(paramInt1, str1, str3, (Bundle)localObject);
            paramParcel2.writeNoException();
            paramParcel2.writeInt(paramInt1);
            return true;
          case 9: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = paramParcel1.readInt();
            localObject = paramParcel1.readString();
            str1 = paramParcel1.readString();
            str2 = paramParcel1.readString();
            if (paramParcel1.readInt() != 0) {
              paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            } else {
              paramParcel1 = null;
            }
            paramParcel1 = getPurchaseHistory(paramInt1, (String)localObject, str1, str2, paramParcel1);
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 8: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = paramParcel1.readInt();
            localObject = paramParcel1.readString();
            str1 = paramParcel1.readString();
            str2 = paramParcel1.readString();
            str3 = paramParcel1.readString();
            if (paramParcel1.readInt() != 0) {
              paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            } else {
              paramParcel1 = null;
            }
            paramParcel1 = getBuyIntentExtraParams(paramInt1, (String)localObject, str1, str2, str3, paramParcel1);
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 7: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramParcel1 = getBuyIntentToReplaceSkus(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.createStringArrayList(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 6: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = stub(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString());
            paramParcel2.writeNoException();
            paramParcel2.writeInt(paramInt1);
            return true;
          case 5: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = consumePurchase(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString());
            paramParcel2.writeNoException();
            paramParcel2.writeInt(paramInt1);
            return true;
          case 4: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramParcel1 = getPurchases(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 3: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramParcel1 = getBuyIntent(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          case 2: 
            paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
            paramInt1 = paramParcel1.readInt();
            str1 = paramParcel1.readString();
            str2 = paramParcel1.readString();
            localObject = str3;
            if (paramParcel1.readInt() != 0) {
              localObject = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
            }
            paramParcel1 = getSkuDetails(paramInt1, str1, str2, (Bundle)localObject);
            paramParcel2.writeNoException();
            if (paramParcel1 != null)
            {
              paramParcel2.writeInt(1);
              paramParcel1.writeToParcel(paramParcel2, 1);
              return true;
            }
            paramParcel2.writeInt(0);
            return true;
          }
          paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
          paramInt1 = isBillingSupported(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
        paramParcel2.writeString("com.android.vending.billing.IInAppBillingService");
        return true;
      }
      paramParcel1.enforceInterface("com.android.vending.billing.IInAppBillingService");
      paramInt1 = paramParcel1.readInt();
      localObject = paramParcel1.readString();
      str1 = paramParcel1.readString();
      str2 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0) {
        paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel1 = null;
      }
      paramParcel1 = getSubscriptionManagementIntent(paramInt1, (String)localObject, str1, str2, paramParcel1);
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeInt(0);
      return true;
    }
    
    private static class Proxy
      implements IInAppBillingService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        mRemote = paramIBinder;
      }
      
      public Bundle acknowledgePurchaseExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(902, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public IBinder asBinder()
      {
        return mRemote;
      }
      
      public int consumePurchase(int paramInt, String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          localParcel2.recycle();
          localParcel1.recycle();
          return paramInt;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle consumePurchaseExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getBuyIntent(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          localParcel1.writeString(paramString4);
          mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getBuyIntentExtraParams(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          localParcel1.writeString(paramString4);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getBuyIntentToReplaceSkus(int paramInt, String paramString1, List paramList, String paramString2, String paramString3, String paramString4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeStringList(paramList);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          localParcel1.writeString(paramString4);
          mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.android.vending.billing.IInAppBillingService";
      }
      
      public Bundle getPurchaseHistory(int paramInt, String paramString1, String paramString2, String paramString3, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getPurchases(int paramInt, String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getPurchasesExtraParams(int paramInt, String paramString1, String paramString2, String paramString3, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getSkuDetails(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getSkuDetailsExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle1, Bundle paramBundle2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          if (paramBundle1 != null)
          {
            localParcel1.writeInt(1);
            paramBundle1.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if (paramBundle2 != null)
          {
            localParcel1.writeInt(1);
            paramBundle2.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(901, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public Bundle getSubscriptionManagementIntent(int paramInt, String paramString1, String paramString2, String paramString3, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(801, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString1 = null;
          }
          localParcel2.recycle();
          localParcel1.recycle();
          return paramString1;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public int isBillingSupported(int paramInt, String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          localParcel2.recycle();
          localParcel1.recycle();
          return paramInt;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public int isBillingSupportedExtraParams(int paramInt, String paramString1, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          localParcel2.recycle();
          localParcel1.recycle();
          return paramInt;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
      
      public int stub(int paramInt, String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          localParcel2.recycle();
          localParcel1.recycle();
          return paramInt;
        }
        catch (Throwable paramString1)
        {
          localParcel2.recycle();
          localParcel1.recycle();
          throw paramString1;
        }
      }
    }
  }
}
