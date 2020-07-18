package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.package_5.Fragment;
import com.google.android.gms.common.annotation.KeepForSdk;

@KeepForSdk
public final class SupportFragmentWrapper
  extends IFragmentWrapper.Stub
{
  private Fragment zzie;
  
  private SupportFragmentWrapper(Fragment paramFragment)
  {
    zzie = paramFragment;
  }
  
  public static SupportFragmentWrapper wrap(Fragment paramFragment)
  {
    if (paramFragment != null) {
      return new SupportFragmentWrapper(paramFragment);
    }
    return null;
  }
  
  public final Bundle getArguments()
  {
    return zzie.getArguments();
  }
  
  public final int getId()
  {
    return zzie.getId();
  }
  
  public final boolean getRetainInstance()
  {
    return zzie.getRetainInstance();
  }
  
  public final String getTag()
  {
    return zzie.getTag();
  }
  
  public final int getTargetRequestCode()
  {
    return zzie.getTargetRequestCode();
  }
  
  public final boolean getUserVisibleHint()
  {
    return zzie.getUserVisibleHint();
  }
  
  public final boolean isAdded()
  {
    return zzie.isAdded();
  }
  
  public final boolean isDetached()
  {
    return zzie.isDetached();
  }
  
  public final boolean isHidden()
  {
    return zzie.isHidden();
  }
  
  public final boolean isInLayout()
  {
    return zzie.isInLayout();
  }
  
  public final boolean isRemoving()
  {
    return zzie.isRemoving();
  }
  
  public final boolean isResumed()
  {
    return zzie.isResumed();
  }
  
  public final boolean isVisible()
  {
    return zzie.isVisible();
  }
  
  public final void setHasOptionsMenu(boolean paramBoolean)
  {
    zzie.setHasOptionsMenu(paramBoolean);
  }
  
  public final void setMenuVisibility(boolean paramBoolean)
  {
    zzie.setMenuVisibility(paramBoolean);
  }
  
  public final void setRetainInstance(boolean paramBoolean)
  {
    zzie.setRetainInstance(paramBoolean);
  }
  
  public final void setUserVisibleHint(boolean paramBoolean)
  {
    zzie.setUserVisibleHint(paramBoolean);
  }
  
  public final void startActivity(Intent paramIntent)
  {
    zzie.startActivity(paramIntent);
  }
  
  public final void startActivityForResult(Intent paramIntent, int paramInt)
  {
    zzie.startActivityForResult(paramIntent, paramInt);
  }
  
  public final void zza(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)ObjectWrapper.unwrap(paramIObjectWrapper);
    zzie.registerForContextMenu(paramIObjectWrapper);
  }
  
  public final IObjectWrapper zzae()
  {
    return ObjectWrapper.wrap(zzie.getActivity());
  }
  
  public final IFragmentWrapper zzaf()
  {
    return wrap(zzie.getParentFragment());
  }
  
  public final IObjectWrapper zzag()
  {
    return ObjectWrapper.wrap(zzie.getResources());
  }
  
  public final IFragmentWrapper zzah()
  {
    return wrap(zzie.getTargetFragment());
  }
  
  public final IObjectWrapper zzai()
  {
    return ObjectWrapper.wrap(zzie.getView());
  }
  
  public final void zzb(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)ObjectWrapper.unwrap(paramIObjectWrapper);
    zzie.unregisterForContextMenu(paramIObjectWrapper);
  }
}
