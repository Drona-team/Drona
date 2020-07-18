package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.transition.BitmapTransitionFactory;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory.Builder;
import com.bumptech.glide.request.transition.TransitionFactory;

public final class BitmapTransitionOptions
  extends TransitionOptions<BitmapTransitionOptions, Bitmap>
{
  public BitmapTransitionOptions() {}
  
  public static BitmapTransitionOptions with(TransitionFactory paramTransitionFactory)
  {
    return (BitmapTransitionOptions)new BitmapTransitionOptions().transition(paramTransitionFactory);
  }
  
  public static BitmapTransitionOptions withCrossFade()
  {
    return new BitmapTransitionOptions().crossFade();
  }
  
  public static BitmapTransitionOptions withCrossFade(int paramInt)
  {
    return new BitmapTransitionOptions().crossFade(paramInt);
  }
  
  public static BitmapTransitionOptions withCrossFade(DrawableCrossFadeFactory.Builder paramBuilder)
  {
    return new BitmapTransitionOptions().crossFade(paramBuilder);
  }
  
  public static BitmapTransitionOptions withCrossFade(DrawableCrossFadeFactory paramDrawableCrossFadeFactory)
  {
    return new BitmapTransitionOptions().crossFade(paramDrawableCrossFadeFactory);
  }
  
  public static BitmapTransitionOptions withWrapped(TransitionFactory paramTransitionFactory)
  {
    return new BitmapTransitionOptions().transitionUsing(paramTransitionFactory);
  }
  
  public BitmapTransitionOptions crossFade()
  {
    return crossFade(new DrawableCrossFadeFactory.Builder());
  }
  
  public BitmapTransitionOptions crossFade(int paramInt)
  {
    return crossFade(new DrawableCrossFadeFactory.Builder(paramInt));
  }
  
  public BitmapTransitionOptions crossFade(DrawableCrossFadeFactory.Builder paramBuilder)
  {
    return transitionUsing(paramBuilder.build());
  }
  
  public BitmapTransitionOptions crossFade(DrawableCrossFadeFactory paramDrawableCrossFadeFactory)
  {
    return transitionUsing(paramDrawableCrossFadeFactory);
  }
  
  public BitmapTransitionOptions transitionUsing(TransitionFactory paramTransitionFactory)
  {
    return (BitmapTransitionOptions)transition(new BitmapTransitionFactory(paramTransitionFactory));
  }
}
