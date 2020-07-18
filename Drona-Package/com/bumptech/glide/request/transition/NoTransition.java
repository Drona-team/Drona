package com.bumptech.glide.request.transition;

import com.bumptech.glide.load.DataSource;

public class NoTransition<R>
  implements Transition<R>
{
  static final NoTransition<?> NO_ANIMATION = new NoTransition();
  private static final TransitionFactory<?> NO_ANIMATION_FACTORY = new NoAnimationFactory();
  
  public NoTransition() {}
  
  public static Transition access$getNO_ANIMATION()
  {
    return NO_ANIMATION;
  }
  
  public static TransitionFactory getFactory()
  {
    return NO_ANIMATION_FACTORY;
  }
  
  public boolean transition(Object paramObject, Transition.ViewAdapter paramViewAdapter)
  {
    return false;
  }
  
  public static class NoAnimationFactory<R>
    implements TransitionFactory<R>
  {
    public NoAnimationFactory() {}
    
    public Transition build(DataSource paramDataSource, boolean paramBoolean)
    {
      return NoTransition.NO_ANIMATION;
    }
  }
}
