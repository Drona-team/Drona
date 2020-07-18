package com.google.android.gms.auth.util.signin.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import androidx.fragment.package_5.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.auth.util.signin.SignInAccount;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.Status;

@KeepName
public class SignInHubActivity
  extends FragmentActivity
{
  private static boolean zzbt;
  private boolean zzbu = false;
  private SignInConfiguration zzbv;
  private boolean zzbw;
  private int zzbx;
  private Intent zzby;
  
  public SignInHubActivity() {}
  
  private final void fillData()
  {
    getSupportLoaderManager().initLoader(0, null, new zzc(null));
    zzbt = false;
  }
  
  private final void setStatus(int paramInt)
  {
    Status localStatus = new Status(paramInt);
    Intent localIntent = new Intent();
    localIntent.putExtra("googleSignInStatus", localStatus);
    setResult(0, localIntent);
    finish();
    zzbt = false;
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    return true;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (zzbu) {
      return;
    }
    setResult(0);
    if (paramInt1 != 40962) {
      return;
    }
    if (paramIntent != null)
    {
      Object localObject = (SignInAccount)paramIntent.getParcelableExtra("signInAccount");
      if ((localObject != null) && (((SignInAccount)localObject).getGoogleSignInAccount() != null))
      {
        localObject = ((SignInAccount)localObject).getGoogleSignInAccount();
        Addon.get(this).makeCall(zzbv.getActiveAccount(), (GoogleSignInAccount)localObject);
        paramIntent.removeExtra("signInAccount");
        paramIntent.putExtra("googleSignInAccount", (Parcelable)localObject);
        zzbw = true;
        zzbx = paramInt2;
        zzby = paramIntent;
        fillData();
        return;
      }
      if (paramIntent.hasExtra("errorCode"))
      {
        paramInt2 = paramIntent.getIntExtra("errorCode", 8);
        paramInt1 = paramInt2;
        if (paramInt2 == 13) {
          paramInt1 = 12501;
        }
        setStatus(paramInt1);
        return;
      }
    }
    setStatus(8);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = getIntent();
    String str = localIntent.getAction();
    if ("com.google.android.gms.auth.NO_IMPL".equals(str))
    {
      setStatus(12500);
      return;
    }
    if ((!str.equals("com.google.android.gms.auth.GOOGLE_SIGN_IN")) && (!str.equals("com.google.android.gms.auth.APPAUTH_SIGN_IN")))
    {
      paramBundle = String.valueOf(localIntent.getAction());
      if (paramBundle.length() != 0) {
        paramBundle = "Unknown action: ".concat(paramBundle);
      } else {
        paramBundle = new String("Unknown action: ");
      }
      Log.e("AuthSignInClient", paramBundle);
      finish();
      return;
    }
    zzbv = ((SignInConfiguration)localIntent.getBundleExtra("config").getParcelable("config"));
    if (zzbv == null)
    {
      Log.e("AuthSignInClient", "Activity started with invalid configuration.");
      setResult(0);
      finish();
      return;
    }
    int i;
    if (paramBundle == null) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0)
    {
      if (zzbt)
      {
        setResult(0);
        setStatus(12502);
        return;
      }
      zzbt = true;
      paramBundle = new Intent(str);
      if (str.equals("com.google.android.gms.auth.GOOGLE_SIGN_IN")) {
        paramBundle.setPackage("com.google.android.gms");
      } else {
        paramBundle.setPackage(getPackageName());
      }
      paramBundle.putExtra("config", zzbv);
    }
    try
    {
      startActivityForResult(paramBundle, 40962);
      return;
    }
    catch (ActivityNotFoundException paramBundle)
    {
      for (;;) {}
    }
    zzbu = true;
    Log.w("AuthSignInClient", "Could not launch sign in Intent. Google Play Service is probably being updated...");
    setStatus(17);
    return;
    zzbw = paramBundle.getBoolean("signingInGoogleApiClients");
    if (zzbw)
    {
      zzbx = paramBundle.getInt("signInResultCode");
      zzby = ((Intent)paramBundle.getParcelable("signInResultData"));
      fillData();
      return;
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("signingInGoogleApiClients", zzbw);
    if (zzbw)
    {
      paramBundle.putInt("signInResultCode", zzbx);
      paramBundle.putParcelable("signInResultData", zzby);
    }
  }
  
  final class zzc
    implements LoaderManager.LoaderCallbacks<Void>
  {
    private zzc() {}
    
    public final Loader onCreateLoader(int paramInt, Bundle paramBundle)
    {
      return new FileLoader(SignInHubActivity.this, GoogleApiClient.getAllClients());
    }
    
    public final void onLoaderReset(Loader paramLoader) {}
  }
}
