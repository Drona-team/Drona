package com.bumptech.glide;

import com.bumptech.glide.request.transition.NoTransition;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.request.transition.ViewAnimationFactory;
import com.bumptech.glide.request.transition.ViewPropertyAnimationFactory;
import com.bumptech.glide.request.transition.ViewPropertyTransition.Animator;
import com.bumptech.glide.util.Preconditions;

public abstract class TransitionOptions<CHILD extends TransitionOptions<CHILD, TranscodeType>, TranscodeType>
  implements Cloneable
{
  private TransitionFactory<? super TranscodeType> transitionFactory = NoTransition.getFactory();
  
  public TransitionOptions() {}
  
  private TransitionOptions self()
  {
    return this;
  }
  
  public final TransitionOptions clone()
  {
    try
    {
      Object localObject = super.clone();
      return (TransitionOptions)localObject;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException);
    }
  }
  
  public final TransitionOptions dontTransition()
  {
    return transition(NoTransition.getFactory());
  }
  
  final TransitionFactory getTransitionFactory()
  {
    return transitionFactory;
  }
  
  public final TransitionOptions transition(int paramInt)
  {
    return transition(new ViewAnimationFactory(paramInt));
  }
  
  public final TransitionOptions transition(TransitionFactory paramTransitionFactory)
  {
    transitionFactory = ((TransitionFactory)Preconditions.checkNotNull(paramTransitionFactory));
    return self();
  }
  
  public final TransitionOptions transition(ViewPropertyTransition.Animator paramAnimator)
  {
    return transition(new ViewPropertyAnimationFactory(paramAnimator));
  }
}
