package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.common.annotation.KeepForSdk;

@SuppressLint({"NewApi"})
@KeepForSdk
public final class FragmentWrapper
  extends IFragmentWrapper.Stub
{
  private Fragment zzia;
  
  private FragmentWrapper(Fragment paramFragment)
  {
    zzia = paramFragment;
  }
  
  public static FragmentWrapper wrap(Fragment paramFragment)
  {
    if (paramFragment != null) {
      return new FragmentWrapper(paramFragment);
    }
    return null;
  }
  
  public final Bundle getArguments()
  {
    return zzia.getArguments();
  }
  
  public final int getId()
  {
    return zzia.getId();
  }
  
  public final boolean getRetainInstance()
  {
    return zzia.getRetainInstance();
  }
  
  public final String getTag()
  {
    return zzia.getTag();
  }
  
  public final int getTargetRequestCode()
  {
    return zzia.getTargetRequestCode();
  }
  
  public final boolean getUserVisibleHint()
  {
    return zzia.getUserVisibleHint();
  }
  
  public final boolean isAdded()
  {
    return zzia.isAdded();
  }
  
  public final boolean isDetached()
  {
    return zzia.isDetached();
  }
  
  public final boolean isHidden()
  {
    return zzia.isHidden();
  }
  
  public final boolean isInLayout()
  {
    return zzia.isInLayout();
  }
  
  public final boolean isRemoving()
  {
    return zzia.isRemoving();
  }
  
  public final boolean isResumed()
  {
    return zzia.isResumed();
  }
  
  public final boolean isVisible()
  {
    return zzia.isVisible();
  }
  
  public final void setHasOptionsMenu(boolean paramBoolean)
  {
    zzia.setHasOptionsMenu(paramBoolean);
  }
  
  public final void setMenuVisibility(boolean paramBoolean)
  {
    zzia.setMenuVisibility(paramBoolean);
  }
  
  public final void setRetainInstance(boolean paramBoolean)
  {
    zzia.setRetainInstance(paramBoolean);
  }
  
  public final void setUserVisibleHint(boolean paramBoolean)
  {
    zzia.setUserVisibleHint(paramBoolean);
  }
  
  public final void startActivity(Intent paramIntent)
  {
    zzia.startActivity(paramIntent);
  }
  
  public final void startActivityForResult(Intent paramIntent, int paramInt)
  {
    zzia.startActivityForResult(paramIntent, paramInt);
  }
  
  public final void zza(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)ObjectWrapper.unwrap(paramIObjectWrapper);
    zzia.registerForContextMenu(paramIObjectWrapper);
  }
  
  public final IObjectWrapper zzae()
  {
    return ObjectWrapper.wrap(zzia.getActivity());
  }
  
  public final IFragmentWrapper zzaf()
  {
    return wrap(zzia.getParentFragment());
  }
  
  public final IObjectWrapper zzag()
  {
    return ObjectWrapper.wrap(zzia.getResources());
  }
  
  public final IFragmentWrapper zzah()
  {
    return wrap(zzia.getTargetFragment());
  }
  
  public final IObjectWrapper zzai()
  {
    return ObjectWrapper.wrap(zzia.getView());
  }
  
  public final void zzb(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)ObjectWrapper.unwrap(paramIObjectWrapper);
    zzia.unregisterForContextMenu(paramIObjectWrapper);
  }
}