package com.android.billingclient.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.billingclient.util.BillingHelper;

class BillingBroadcastManager
{
  private static final String ACTION = "com.android.vending.billing.PURCHASES_UPDATED";
  private static final String PAGE_KEY = "BillingBroadcastManager";
  private final Context mContext;
  private final BillingBroadcastReceiver mReceiver;
  
  BillingBroadcastManager(Context paramContext, PurchasesUpdatedListener paramPurchasesUpdatedListener)
  {
    mContext = paramContext;
    mReceiver = new BillingBroadcastReceiver(paramPurchasesUpdatedListener, null);
  }
  
  void destroy()
  {
    mReceiver.unRegister(mContext);
  }
  
  PurchasesUpdatedListener getListener()
  {
    return mReceiver.mListener;
  }
  
  void registerReceiver()
  {
    mReceiver.register(mContext, new IntentFilter("com.android.vending.billing.PURCHASES_UPDATED"));
  }
  
  class BillingBroadcastReceiver
    extends BroadcastReceiver
  {
    private boolean mIsRegistered;
    private final PurchasesUpdatedListener mListener;
    
    private BillingBroadcastReceiver(PurchasesUpdatedListener paramPurchasesUpdatedListener)
    {
      mListener = paramPurchasesUpdatedListener;
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = BillingHelper.getBillingResultFromIntent(paramIntent, "BillingBroadcastManager");
      paramIntent = BillingHelper.extractPurchases(paramIntent.getExtras());
      mListener.onPurchasesUpdated(paramContext, paramIntent);
    }
    
    public void register(Context paramContext, IntentFilter paramIntentFilter)
    {
      if (!mIsRegistered)
      {
        paramContext.registerReceiver(mReceiver, paramIntentFilter);
        mIsRegistered = true;
      }
    }
    
    public void unRegister(Context paramContext)
    {
      if (mIsRegistered)
      {
        paramContext.unregisterReceiver(mReceiver);
        mIsRegistered = false;
        return;
      }
      BillingHelper.logWarn("BillingBroadcastManager", "Receiver is not registered.");
    }
  }
}
