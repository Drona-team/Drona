package com.google.android.gms.package_8.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzacj;
import com.google.android.gms.internal.ads.zzacr;
import com.google.android.gms.internal.ads.zzacu;
import com.google.android.gms.internal.ads.zzagv;
import com.google.android.gms.internal.ads.zzagx;
import com.google.android.gms.internal.ads.zzaqb;
import com.google.android.gms.internal.ads.zzaqd;
import com.google.android.gms.internal.ads.zzaqh;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzawm;
import com.google.android.gms.internal.ads.zzawv;
import com.google.android.gms.internal.ads.zzawz;
import com.google.android.gms.internal.ads.zzaxi;
import com.google.android.gms.internal.ads.zzaxo;
import com.google.android.gms.internal.ads.zzbai;
import com.google.android.gms.internal.ads.zzbgz;
import com.google.android.gms.internal.ads.zzbhf;
import com.google.android.gms.internal.ads.zzbii;
import com.google.android.gms.internal.ads.zzbin;
import com.google.android.gms.internal.ads.zzwj;
import com.google.android.gms.internal.ads.zzxr;
import com.google.android.gms.internal.ads.zzyt;
import com.google.android.gms.package_8.internal.Delta;
import com.google.android.gms.package_8.internal.ObjectIdentifier;
import com.google.android.gms.package_8.internal.StatusBarPanelCustomTile;
import com.google.android.gms.package_8.internal.UserData;
import java.util.Collections;

@zzard
public class AbstractGalleryActivity
  extends zzaqh
  implements LatLong
{
  @VisibleForTesting
  private static final int zzdjk = Color.argb(0, 0, 0, 0);
  protected final Activity mActivity;
  @VisibleForTesting
  AdOverlayInfoParcel zzdjl;
  @VisibleForTesting
  zzbgz zzdjm;
  @VisibleForTesting
  private Actor zzdjn;
  @VisibleForTesting
  private SettingsActivity.3 zzdjo;
  @VisibleForTesting
  private boolean zzdjp = false;
  @VisibleForTesting
  private FrameLayout zzdjq;
  @VisibleForTesting
  private WebChromeClient.CustomViewCallback zzdjr;
  @VisibleForTesting
  private boolean zzdjs = false;
  @VisibleForTesting
  private boolean zzdjt = false;
  @VisibleForTesting
  private TouchInterceptor zzdju;
  @VisibleForTesting
  private boolean zzdjv = false;
  @VisibleForTesting
  int zzdjw = 0;
  private final Object zzdjx = new Object();
  private Runnable zzdjy;
  private boolean zzdjz;
  private boolean zzdka;
  private boolean zzdkb = false;
  private boolean zzdkc = false;
  private boolean zzdkd = true;
  
  public AbstractGalleryActivity(Activity paramActivity)
  {
    mActivity = paramActivity;
  }
  
  private static void measureChild(IObjectWrapper paramIObjectWrapper, View paramView)
  {
    if ((paramIObjectWrapper != null) && (paramView != null)) {
      UserData.zzlv().zza(paramIObjectWrapper, paramView);
    }
  }
  
  private final void setFullscreen(Configuration paramConfiguration)
  {
    Object localObject = zzdjl.zzdkt;
    int m = 1;
    int n = 0;
    int i;
    if ((localObject != null) && (zzdjl.zzdkt.zzbrf)) {
      i = 1;
    } else {
      i = 0;
    }
    boolean bool = UserData.zzli().zza(mActivity, paramConfiguration);
    int k;
    int j;
    if (((!zzdjt) || (i != 0)) && (!bool))
    {
      k = m;
      j = n;
      if (Build.VERSION.SDK_INT >= 19)
      {
        k = m;
        j = n;
        if (zzdjl.zzdkt != null)
        {
          k = m;
          j = n;
          if (zzdjl.zzdkt.zzbrk)
          {
            j = 1;
            k = m;
          }
        }
      }
    }
    else
    {
      k = 0;
      j = n;
    }
    paramConfiguration = mActivity.getWindow();
    localObject = zzacu.zzcpg;
    if ((((Boolean)zzyt.zzpe().zzd((zzacj)localObject)).booleanValue()) && (Build.VERSION.SDK_INT >= 19))
    {
      paramConfiguration = paramConfiguration.getDecorView();
      i = 256;
      if (k != 0)
      {
        i = 5380;
        if (j != 0) {
          i = 5894;
        }
      }
      paramConfiguration.setSystemUiVisibility(i);
      return;
    }
    if (k != 0)
    {
      paramConfiguration.addFlags(1024);
      paramConfiguration.clearFlags(2048);
      if ((Build.VERSION.SDK_INT >= 19) && (j != 0)) {
        paramConfiguration.getDecorView().setSystemUiVisibility(4098);
      }
    }
    else
    {
      paramConfiguration.addFlags(2048);
      paramConfiguration.clearFlags(1024);
    }
  }
  
  private final void zzac(boolean paramBoolean)
  {
    Object localObject = zzacu.zzcuk;
    int i = ((Integer)zzyt.zzpe().zzd((zzacj)localObject)).intValue();
    localObject = new Card();
    size = 50;
    int j;
    if (paramBoolean) {
      j = i;
    } else {
      j = 0;
    }
    paddingLeft = j;
    if (paramBoolean) {
      j = 0;
    } else {
      j = i;
    }
    paddingRight = j;
    paddingTop = 0;
    paddingBottom = i;
    zzdjo = new SettingsActivity.3(mActivity, (Card)localObject, this);
    localObject = new RelativeLayout.LayoutParams(-2, -2);
    ((RelativeLayout.LayoutParams)localObject).addRule(10);
    if (paramBoolean) {
      i = 11;
    } else {
      i = 9;
    }
    ((RelativeLayout.LayoutParams)localObject).addRule(i);
    addFeed(paramBoolean, zzdjl.zzdko);
    zzdju.addView(zzdjo, (ViewGroup.LayoutParams)localObject);
  }
  
  private final void zzad(boolean paramBoolean)
    throws InvalidPatternException
  {
    if (!zzdka) {
      mActivity.requestWindowFeature(1);
    }
    Object localObject2 = mActivity.getWindow();
    if (localObject2 != null)
    {
      Object localObject1 = zzdjl.zzdbs;
      Object localObject4 = null;
      if (localObject1 != null) {
        localObject1 = zzdjl.zzdbs.zzaai();
      } else {
        localObject1 = null;
      }
      boolean bool3 = false;
      boolean bool2 = false;
      boolean bool1;
      if ((localObject1 != null) && (((zzbii)localObject1).zzaay())) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      zzdjv = false;
      if (bool1)
      {
        int i = zzdjl.orientation;
        UserData.zzli();
        if (i == 6)
        {
          if (mActivity.getResources().getConfiguration().orientation == 1) {
            bool2 = true;
          }
          zzdjv = bool2;
        }
        else
        {
          i = zzdjl.orientation;
          UserData.zzli();
          if (i == 7)
          {
            bool2 = bool3;
            if (mActivity.getResources().getConfiguration().orientation == 2) {
              bool2 = true;
            }
            zzdjv = bool2;
          }
        }
      }
      bool2 = zzdjv;
      localObject1 = new StringBuilder(46);
      ((StringBuilder)localObject1).append("Delay onShow to next orientation change: ");
      ((StringBuilder)localObject1).append(bool2);
      zzawz.zzdp(((StringBuilder)localObject1).toString());
      setRequestedOrientation(zzdjl.orientation);
      UserData.zzli();
      ((Window)localObject2).setFlags(16777216, 16777216);
      zzawz.zzdp("Hardware acceleration on the AdActivity window enabled.");
      if (!zzdjt) {
        zzdju.setBackgroundColor(-16777216);
      } else {
        zzdju.setBackgroundColor(zzdjk);
      }
      mActivity.setContentView(zzdju);
      zzdka = true;
      if (paramBoolean) {
        try
        {
          UserData.zzlh();
          Object localObject5 = mActivity;
          if (zzdjl.zzdbs != null)
          {
            localObject1 = zzdjl.zzdbs;
            localObject1 = ((zzbgz)localObject1).zzaag();
          }
          else
          {
            localObject1 = null;
          }
          if (zzdjl.zzdbs != null)
          {
            localObject2 = zzdjl.zzdbs;
            localObject2 = ((zzbgz)localObject2).zzaah();
          }
          else
          {
            localObject2 = null;
          }
          Object localObject6 = zzdjl.zzbtc;
          if (zzdjl.zzdbs != null)
          {
            localObject3 = zzdjl.zzdbs;
            localObject3 = ((zzbgz)localObject3).zzye();
          }
          else
          {
            localObject3 = null;
          }
          localObject1 = zzbhf.zza((Context)localObject5, (zzbin)localObject1, (String)localObject2, true, bool1, null, (zzbai)localObject6, null, null, (Delta)localObject3, zzwj.zznl());
          zzdjm = ((zzbgz)localObject1);
          localObject2 = zzdjm.zzaai();
          Object localObject3 = zzdjl.zzczo;
          localObject5 = zzdjl.zzczp;
          localObject6 = zzdjl.zzdkq;
          localObject1 = localObject4;
          if (zzdjl.zzdbs != null) {
            localObject1 = zzdjl.zzdbs.zzaai().zzaax();
          }
          ((zzbii)localObject2).zza(null, (zzagv)localObject3, null, (zzagx)localObject5, (Monitor)localObject6, true, null, (ObjectIdentifier)localObject1, null, null);
          zzdjm.zzaai().zza(new ActivityChooserModel.1(this));
          if (zzdjl.thumbUrl != null)
          {
            zzdjm.loadUrl(zzdjl.thumbUrl);
          }
          else
          {
            if (zzdjl.zzdkp == null) {
              break label682;
            }
            zzdjm.loadDataWithBaseURL(zzdjl.zzdkn, zzdjl.zzdkp, "text/html", "UTF-8", null);
          }
          if (zzdjl.zzdbs == null) {
            break label738;
          }
          zzdjl.zzdbs.zzb(this);
          break label738;
          label682:
          throw new InvalidPatternException("No URL or HTML to display in ad overlay.");
        }
        catch (Exception localException)
        {
          zzawz.zzc("Error obtaining webview.", localException);
          throw new InvalidPatternException("Could not obtain webview for the overlay.");
        }
      }
      zzdjm = zzdjl.zzdbs;
      zzdjm.zzbn(mActivity);
      label738:
      zzdjm.zza(this);
      if (zzdjl.zzdbs != null) {
        measureChild(zzdjl.zzdbs.zzaam(), zzdju);
      }
      ViewParent localViewParent = zzdjm.getParent();
      if ((localViewParent != null) && ((localViewParent instanceof ViewGroup))) {
        ((ViewGroup)localViewParent).removeView(zzdjm.getView());
      }
      if (zzdjt) {
        zzdjm.zzaau();
      }
      zzdju.addView(zzdjm.getView(), -1, -1);
      if ((!paramBoolean) && (!zzdjv)) {
        zztl();
      }
      zzac(bool1);
      if (zzdjm.zzaak()) {
        addFeed(bool1, true);
      }
    }
    else
    {
      throw new InvalidPatternException("Invalid activity, no window available.");
    }
  }
  
  private final void zzti()
  {
    if (mActivity.isFinishing())
    {
      if (zzdkb) {
        return;
      }
      zzdkb = true;
      if (zzdjm != null)
      {
        int i = zzdjw;
        zzdjm.zzdi(i);
        Object localObject = zzdjx;
        try
        {
          if ((!zzdjz) && (zzdjm.zzaaq()))
          {
            zzdjy = new FileBrowser.11(this);
            Handler localHandler = zzaxi.zzdvv;
            Runnable localRunnable = zzdjy;
            zzacj localZzacj = zzacu.zzcpd;
            localHandler.postDelayed(localRunnable, ((Long)zzyt.zzpe().zzd(localZzacj)).longValue());
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
      }
      zztj();
    }
  }
  
  private final void zztl()
  {
    zzdjm.zztl();
  }
  
  public final void addFeed(boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject = zzacu.zzcpe;
    boolean bool2 = ((Boolean)zzyt.zzpe().zzd((zzacj)localObject)).booleanValue();
    boolean bool1 = true;
    int i;
    if ((bool2) && (zzdjl != null) && (zzdjl.zzdkt != null) && (zzdjl.zzdkt.zzbrl)) {
      i = 1;
    } else {
      i = 0;
    }
    localObject = zzacu.zzcpf;
    int j;
    if ((((Boolean)zzyt.zzpe().zzd((zzacj)localObject)).booleanValue()) && (zzdjl != null) && (zzdjl.zzdkt != null) && (zzdjl.zzdkt.zzbrm)) {
      j = 1;
    } else {
      j = 0;
    }
    if ((paramBoolean1) && (paramBoolean2) && (i != 0) && (j == 0)) {
      new zzaqb(zzdjm, "useCustomClose").zzdh("Custom close has been disabled for interstitial ads in this ad slot.");
    }
    if (zzdjo != null)
    {
      localObject = zzdjo;
      paramBoolean1 = bool1;
      if (j == 0) {
        if ((paramBoolean2) && (i == 0)) {
          paramBoolean1 = bool1;
        } else {
          paramBoolean1 = false;
        }
      }
      ((SettingsActivity.3)localObject).zzaf(paramBoolean1);
    }
  }
  
  public final void close()
  {
    zzdjw = 2;
    mActivity.finish();
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  public final void onBackPressed()
  {
    zzdjw = 0;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    mActivity.requestWindowFeature(1);
    boolean bool;
    if ((paramBundle != null) && (paramBundle.getBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", false))) {
      bool = true;
    } else {
      bool = false;
    }
    zzdjs = bool;
    Object localObject = mActivity;
    try
    {
      localObject = AdOverlayInfoParcel.loadData(((Activity)localObject).getIntent());
      zzdjl = ((AdOverlayInfoParcel)localObject);
      if (zzdjl != null)
      {
        if (zzdjl.zzbtc.zzdzd > 7500000) {
          zzdjw = 3;
        }
        localObject = mActivity;
        localObject = ((Activity)localObject).getIntent();
        if (localObject != null)
        {
          localObject = mActivity;
          bool = ((Activity)localObject).getIntent().getBooleanExtra("shouldCallOnOverlayOpened", true);
          zzdkd = bool;
        }
        if (zzdjl.zzdkt != null) {
          zzdjt = zzdjl.zzdkt.zzbre;
        } else {
          zzdjt = false;
        }
        if ((zzdjt) && (zzdjl.zzdkt.zzbrj != -1)) {
          new HomeActivity.2(this, null).zzvi();
        }
        if (paramBundle == null)
        {
          if ((zzdjl.zzdkm != null) && (zzdkd))
          {
            paramBundle = zzdjl.zzdkm;
            paramBundle.zzta();
          }
          if ((zzdjl.zzdkr != 1) && (zzdjl.zzcgi != null))
          {
            paramBundle = zzdjl.zzcgi;
            paramBundle.onAdClicked();
          }
        }
        paramBundle = mActivity;
        localObject = zzdjl.zzdks;
        String str = zzdjl.zzbtc.zzbsx;
        paramBundle = new TouchInterceptor(paramBundle, (String)localObject, str);
        zzdju = paramBundle;
        paramBundle = zzdju;
        paramBundle.setId(1000);
        paramBundle = UserData.zzli();
        localObject = mActivity;
        paramBundle.zzg((Activity)localObject);
        switch (zzdjl.zzdkr)
        {
        default: 
          break;
        case 3: 
          zzad(true);
          return;
        case 2: 
          paramBundle = zzdjl.zzdbs;
          paramBundle = new Actor(paramBundle);
          zzdjn = paramBundle;
          zzad(false);
          return;
        case 1: 
          zzad(false);
          return;
        }
        throw new InvalidPatternException("Could not determine ad overlay type.");
      }
      throw new InvalidPatternException("Could not get info for ad overlay.");
    }
    catch (InvalidPatternException paramBundle)
    {
      zzawz.zzep(paramBundle.getMessage());
      zzdjw = 3;
      mActivity.finish();
    }
  }
  
  public final void onDestroy()
  {
    if (zzdjm != null) {
      zzdju.removeView(zzdjm.getView());
    }
    zzti();
  }
  
  public final void onPause()
  {
    zzte();
    if (zzdjl.zzdkm != null) {
      zzdjl.zzdkm.onPause();
    }
    zzacj localZzacj = zzacu.zzcui;
    if ((!((Boolean)zzyt.zzpe().zzd(localZzacj)).booleanValue()) && (zzdjm != null) && ((!mActivity.isFinishing()) || (zzdjn == null)))
    {
      UserData.zzli();
      zzaxo.zza(zzdjm);
    }
    zzti();
  }
  
  public final void onRestart() {}
  
  public final void onResume()
  {
    if (zzdjl.zzdkm != null) {
      zzdjl.zzdkm.onResume();
    }
    setFullscreen(mActivity.getResources().getConfiguration());
    zzacj localZzacj = zzacu.zzcui;
    if (!((Boolean)zzyt.zzpe().zzd(localZzacj)).booleanValue())
    {
      if ((zzdjm != null) && (!zzdjm.isDestroyed()))
      {
        UserData.zzli();
        zzaxo.zzb(zzdjm);
        return;
      }
      zzawz.zzep("The webview does not exist. Ignoring action.");
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", zzdjs);
  }
  
  public final void onStart()
  {
    zzacj localZzacj = zzacu.zzcui;
    if (((Boolean)zzyt.zzpe().zzd(localZzacj)).booleanValue())
    {
      if ((zzdjm != null) && (!zzdjm.isDestroyed()))
      {
        UserData.zzli();
        zzaxo.zzb(zzdjm);
        return;
      }
      zzawz.zzep("The webview does not exist. Ignoring action.");
    }
  }
  
  public final void onStop()
  {
    zzacj localZzacj = zzacu.zzcui;
    if ((((Boolean)zzyt.zzpe().zzd(localZzacj)).booleanValue()) && (zzdjm != null) && ((!mActivity.isFinishing()) || (zzdjn == null)))
    {
      UserData.zzli();
      zzaxo.zza(zzdjm);
    }
    zzti();
  }
  
  public final void setContentView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
  {
    zzdjq = new FrameLayout(mActivity);
    zzdjq.setBackgroundColor(-16777216);
    zzdjq.addView(paramView, -1, -1);
    mActivity.setContentView(zzdjq);
    zzdka = true;
    zzdjr = paramCustomViewCallback;
    zzdjp = true;
  }
  
  public final void setRequestedOrientation(int paramInt)
  {
    Object localObject2 = mActivity;
    Object localObject1 = this;
    int i = getApplicationInfotargetSdkVersion;
    localObject2 = zzacu.zzcwg;
    if (i >= ((Integer)zzyt.zzpe().zzd((zzacj)localObject2)).intValue())
    {
      i = mActivity.getApplicationInfo().targetSdkVersion;
      localObject1 = zzacu.zzcwh;
      if (i <= ((Integer)zzyt.zzpe().zzd((zzacj)localObject1)).intValue())
      {
        i = Build.VERSION.SDK_INT;
        localObject1 = zzacu.zzcwi;
        if (i >= ((Integer)zzyt.zzpe().zzd((zzacj)localObject1)).intValue())
        {
          i = Build.VERSION.SDK_INT;
          localObject1 = zzacu.zzcwj;
          if (i <= ((Integer)zzyt.zzpe().zzd((zzacj)localObject1)).intValue()) {
            return;
          }
        }
      }
    }
    try
    {
      mActivity.setRequestedOrientation(paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      UserData.zzlk().zzb(localThrowable, "AdOverlay.setRequestedOrientation");
    }
  }
  
  public final void zzac(IObjectWrapper paramIObjectWrapper)
  {
    setFullscreen((Configuration)ObjectWrapper.unwrap(paramIObjectWrapper));
  }
  
  public final void zzdd()
  {
    zzdka = true;
  }
  
  public final void zzte()
  {
    if ((zzdjl != null) && (zzdjp)) {
      setRequestedOrientation(zzdjl.orientation);
    }
    if (zzdjq != null)
    {
      mActivity.setContentView(zzdju);
      zzdka = true;
      zzdjq.removeAllViews();
      zzdjq = null;
    }
    if (zzdjr != null)
    {
      zzdjr.onCustomViewHidden();
      zzdjr = null;
    }
    zzdjp = false;
  }
  
  public final void zztf()
  {
    zzdjw = 1;
    mActivity.finish();
  }
  
  public final boolean zztg()
  {
    zzdjw = 0;
    if (zzdjm == null) {
      return true;
    }
    boolean bool = zzdjm.zzaap();
    if (!bool) {
      zzdjm.zza("onbackblocked", Collections.emptyMap());
    }
    return bool;
  }
  
  public final void zzth()
  {
    zzdju.removeView(zzdjo);
    zzac(true);
  }
  
  final void zztj()
  {
    if (zzdkc) {
      return;
    }
    zzdkc = true;
    if (zzdjm != null)
    {
      zzdju.removeView(zzdjm.getView());
      if (zzdjn != null)
      {
        zzdjm.zzbn(zzdjn.zzlj);
        zzdjm.zzaq(false);
        zzdjn.parent.addView(zzdjm.getView(), zzdjn.index, zzdjn.zzdkh);
        zzdjn = null;
      }
      else if (mActivity.getApplicationContext() != null)
      {
        zzdjm.zzbn(mActivity.getApplicationContext());
      }
      zzdjm = null;
    }
    if ((zzdjl != null) && (zzdjl.zzdkm != null)) {
      zzdjl.zzdkm.zzsz();
    }
    if ((zzdjl != null) && (zzdjl.zzdbs != null)) {
      measureChild(zzdjl.zzdbs.zzaam(), zzdjl.zzdbs.getView());
    }
  }
  
  public final void zztk()
  {
    if (zzdjv)
    {
      zzdjv = false;
      zztl();
    }
  }
  
  public final void zztm()
  {
    zzdju.zzdkg = true;
  }
  
  public final void zztn()
  {
    Object localObject = zzdjx;
    try
    {
      zzdjz = true;
      if (zzdjy != null)
      {
        zzaxi.zzdvv.removeCallbacks(zzdjy);
        zzaxi.zzdvv.post(zzdjy);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
