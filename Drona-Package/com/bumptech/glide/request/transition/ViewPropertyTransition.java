package com.bumptech.glide.request.transition;

import android.view.View;

public class ViewPropertyTransition<R>
  implements Transition<R>
{
  private final Animator animator;
  
  public ViewPropertyTransition(Animator paramAnimator)
  {
    animator = paramAnimator;
  }
  
  public boolean transition(Object paramObject, Transition.ViewAdapter paramViewAdapter)
  {
    if (paramViewAdapter.getView() != null) {
      animator.animate(paramViewAdapter.getView());
    }
    return false;
  }
  
  public static abstract interface Animator
  {
    public abstract void animate(View paramView);
  }
}
