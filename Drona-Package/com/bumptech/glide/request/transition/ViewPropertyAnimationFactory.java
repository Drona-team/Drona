package com.bumptech.glide.request.transition;

import com.bumptech.glide.load.DataSource;

public class ViewPropertyAnimationFactory<R>
  implements TransitionFactory<R>
{
  private ViewPropertyTransition<R> animation;
  private final ViewPropertyTransition.Animator animator;
  
  public ViewPropertyAnimationFactory(ViewPropertyTransition.Animator paramAnimator)
  {
    animator = paramAnimator;
  }
  
  public Transition build(DataSource paramDataSource, boolean paramBoolean)
  {
    if ((paramDataSource != DataSource.MEMORY_CACHE) && (paramBoolean))
    {
      if (animation == null) {
        animation = new ViewPropertyTransition(animator);
      }
      return animation;
    }
    return NoTransition.access$getNO_ANIMATION();
  }
}
