package com.bumptech.glide;

import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.request.transition.ViewPropertyTransition.Animator;

public final class GenericTransitionOptions<TranscodeType>
  extends TransitionOptions<GenericTransitionOptions<TranscodeType>, TranscodeType>
{
  public GenericTransitionOptions() {}
  
  public static GenericTransitionOptions with(int paramInt)
  {
    return (GenericTransitionOptions)new GenericTransitionOptions().transition(paramInt);
  }
  
  public static GenericTransitionOptions with(TransitionFactory paramTransitionFactory)
  {
    return (GenericTransitionOptions)new GenericTransitionOptions().transition(paramTransitionFactory);
  }
  
  public static GenericTransitionOptions with(ViewPropertyTransition.Animator paramAnimator)
  {
    return (GenericTransitionOptions)new GenericTransitionOptions().transition(paramAnimator);
  }
  
  public static GenericTransitionOptions withNoTransition()
  {
    return (GenericTransitionOptions)new GenericTransitionOptions().dontTransition();
  }
}
