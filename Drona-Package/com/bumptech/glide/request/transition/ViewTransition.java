package com.bumptech.glide.request.transition;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

public class ViewTransition<R>
  implements Transition<R>
{
  private final ViewTransitionAnimationFactory viewTransitionAnimationFactory;
  
  ViewTransition(ViewTransitionAnimationFactory paramViewTransitionAnimationFactory)
  {
    viewTransitionAnimationFactory = paramViewTransitionAnimationFactory;
  }
  
  public boolean transition(Object paramObject, Transition.ViewAdapter paramViewAdapter)
  {
    paramObject = paramViewAdapter.getView();
    if (paramObject != null)
    {
      paramObject.clearAnimation();
      paramObject.startAnimation(viewTransitionAnimationFactory.build(paramObject.getContext()));
    }
    return false;
  }
  
  static abstract interface ViewTransitionAnimationFactory
  {
    public abstract Animation build(Context paramContext);
  }
}
