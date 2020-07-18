package com.google.android.gms.dynamic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.ConnectionErrorMessages;
import java.util.LinkedList;

@KeepForSdk
public abstract class DeferredLifecycleHelper<T extends LifecycleDelegate>
{
  private T zarf;
  private Bundle zarg;
  private LinkedList<zaa> zarh;
  private final OnDelegateCreatedListener<T> zari = new ContentSource(this);
  
  public DeferredLifecycleHelper() {}
  
  private final void next(int paramInt)
  {
    while ((!zarh.isEmpty()) && (((zaa)zarh.getLast()).getState() >= paramInt)) {
      zarh.removeLast();
    }
  }
  
  private final void set(Bundle paramBundle, zaa paramZaa)
  {
    if (zarf != null)
    {
      paramZaa.makeView(zarf);
      return;
    }
    if (zarh == null) {
      zarh = new LinkedList();
    }
    zarh.add(paramZaa);
    if (paramBundle != null) {
      if (zarg == null) {
        zarg = ((Bundle)paramBundle.clone());
      } else {
        zarg.putAll(paramBundle);
      }
    }
    createDelegate(zari);
  }
  
  public static void showGooglePlayUnavailableMessage(FrameLayout paramFrameLayout)
  {
    Object localObject = GoogleApiAvailability.getInstance();
    Context localContext = paramFrameLayout.getContext();
    int i = ((GoogleApiAvailability)localObject).isGooglePlayServicesAvailable(localContext);
    String str2 = ConnectionErrorMessages.getErrorMessage(localContext, i);
    String str1 = ConnectionErrorMessages.getErrorDialogButtonMessage(localContext, i);
    LinearLayout localLinearLayout = new LinearLayout(paramFrameLayout.getContext());
    localLinearLayout.setOrientation(1);
    localLinearLayout.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
    paramFrameLayout.addView(localLinearLayout);
    paramFrameLayout = new TextView(paramFrameLayout.getContext());
    paramFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
    paramFrameLayout.setText(str2);
    localLinearLayout.addView(paramFrameLayout);
    paramFrameLayout = ((GoogleApiAvailability)localObject).getErrorResolutionIntent(localContext, i, null);
    if (paramFrameLayout != null)
    {
      localObject = new Button(localContext);
      ((View)localObject).setId(16908313);
      ((View)localObject).setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
      ((TextView)localObject).setText(str1);
      localLinearLayout.addView((View)localObject);
      ((View)localObject).setOnClickListener(new Utilities.1(localContext, paramFrameLayout));
    }
  }
  
  protected abstract void createDelegate(OnDelegateCreatedListener paramOnDelegateCreatedListener);
  
  public LifecycleDelegate getDelegate()
  {
    return zarf;
  }
  
  protected void handleGooglePlayUnavailable(FrameLayout paramFrameLayout)
  {
    showGooglePlayUnavailableMessage(paramFrameLayout);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    set(paramBundle, new AudioRecorder(this, paramBundle));
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    FrameLayout localFrameLayout = new FrameLayout(paramLayoutInflater.getContext());
    set(paramBundle, new PlaylistFragment.2(this, localFrameLayout, paramLayoutInflater, paramViewGroup, paramBundle));
    if (zarf == null) {
      handleGooglePlayUnavailable(localFrameLayout);
    }
    return localFrameLayout;
  }
  
  public void onDestroy()
  {
    if (zarf != null)
    {
      zarf.onDestroy();
      return;
    }
    next(1);
  }
  
  public void onDestroyView()
  {
    if (zarf != null)
    {
      zarf.onDestroyView();
      return;
    }
    next(2);
  }
  
  public void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
  {
    set(paramBundle2, new BackgroundService.1(this, paramActivity, paramBundle1, paramBundle2));
  }
  
  public void onLowMemory()
  {
    if (zarf != null) {
      zarf.onLowMemory();
    }
  }
  
  public void onPause()
  {
    if (zarf != null)
    {
      zarf.onPause();
      return;
    }
    next(5);
  }
  
  public void onResume()
  {
    set(null, new DayFragment(this));
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (zarf != null)
    {
      zarf.onSaveInstanceState(paramBundle);
      return;
    }
    if (zarg != null) {
      paramBundle.putAll(zarg);
    }
  }
  
  public void onStart()
  {
    set(null, new MainActivity.4(this));
  }
  
  public void onStop()
  {
    if (zarf != null)
    {
      zarf.onStop();
      return;
    }
    next(4);
  }
  
  private static abstract interface zaa
  {
    public abstract int getState();
    
    public abstract void makeView(LifecycleDelegate paramLifecycleDelegate);
  }
}
