package com.android.billingclient.upgrade;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.android.billingclient.util.BillingHelper;

public class ProxyBillingActivity
  extends Activity
{
  static final String KEY_RESULT_RECEIVER = "result_receiver";
  private static final String PAGE_KEY = "ProxyBillingActivity";
  private static final int REQUEST_CODE_LAUNCH_ACTIVITY = 100;
  private ResultReceiver mResultReceiver;
  
  public ProxyBillingActivity() {}
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 100)
    {
      paramInt1 = BillingHelper.getResponseCodeFromIntent(paramIntent, "ProxyBillingActivity");
      if ((paramInt2 != -1) || (paramInt1 != 0))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Activity finished with resultCode ");
        ((StringBuilder)localObject).append(paramInt2);
        ((StringBuilder)localObject).append(" and billing's responseCode: ");
        ((StringBuilder)localObject).append(paramInt1);
        BillingHelper.logWarn("ProxyBillingActivity", ((StringBuilder)localObject).toString());
      }
      Object localObject = mResultReceiver;
      if (paramIntent == null) {
        paramIntent = null;
      } else {
        paramIntent = paramIntent.getExtras();
      }
      ((ResultReceiver)localObject).send(paramInt1, paramIntent);
    }
    else
    {
      paramIntent = new StringBuilder();
      paramIntent.append("Got onActivityResult with wrong requestCode: ");
      paramIntent.append(paramInt1);
      paramIntent.append("; skipping...");
      BillingHelper.logWarn("ProxyBillingActivity", paramIntent.toString());
    }
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null)
    {
      BillingHelper.logVerbose("ProxyBillingActivity", "Launching Play Store billing flow");
      mResultReceiver = ((ResultReceiver)getIntent().getParcelableExtra("result_receiver"));
      if (getIntent().hasExtra("BUY_INTENT")) {
        paramBundle = (PendingIntent)getIntent().getParcelableExtra("BUY_INTENT");
      } else if (getIntent().hasExtra("SUBS_MANAGEMENT_INTENT")) {
        paramBundle = (PendingIntent)getIntent().getParcelableExtra("SUBS_MANAGEMENT_INTENT");
      } else {
        paramBundle = null;
      }
      try
      {
        paramBundle = paramBundle.getIntentSender();
        startIntentSenderForResult(paramBundle, 100, new Intent(), 0, 0, 0);
        return;
      }
      catch (IntentSender.SendIntentException paramBundle)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Got exception while trying to start a purchase flow: ");
        localStringBuilder.append(paramBundle);
        BillingHelper.logWarn("ProxyBillingActivity", localStringBuilder.toString());
        mResultReceiver.send(6, null);
        finish();
        return;
      }
    }
    BillingHelper.logVerbose("ProxyBillingActivity", "Launching Play Store billing flow from savedInstanceState");
    mResultReceiver = ((ResultReceiver)paramBundle.getParcelable("result_receiver"));
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putParcelable("result_receiver", mResultReceiver);
  }
}
