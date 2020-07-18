package androidx.activity;

import androidx.lifecycle.LifecycleOwner;

public abstract interface OnBackPressedDispatcherOwner
  extends LifecycleOwner
{
  public abstract OnBackPressedDispatcher getOnBackPressedDispatcher();
}
