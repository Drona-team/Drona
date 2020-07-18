package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import androidx.fragment.package_5.Fragment;
import com.google.android.gms.common.package_6.internal.LifecycleFragment;

public abstract class DialogRedirect
  implements DialogInterface.OnClickListener
{
  public DialogRedirect() {}
  
  public static DialogRedirect getInstance(Activity paramActivity, Intent paramIntent, int paramInt)
  {
    return new UserAgent(paramIntent, paramActivity, paramInt);
  }
  
  public static DialogRedirect getInstance(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    return new Call(paramIntent, paramFragment, paramInt);
  }
  
  public static DialogRedirect getInstance(LifecycleFragment paramLifecycleFragment, Intent paramIntent, int paramInt)
  {
    return new AsyncHttpServerResponseImpl(paramIntent, paramLifecycleFragment, paramInt);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    try
    {
      redirect();
      paramDialogInterface.dismiss();
      return;
    }
    catch (Throwable localThrowable) {}catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("DialogRedirect", "Failed to start resolution intent", localActivityNotFoundException);
      paramDialogInterface.dismiss();
      return;
    }
    paramDialogInterface.dismiss();
    throw localActivityNotFoundException;
  }
  
  protected abstract void redirect();
}
