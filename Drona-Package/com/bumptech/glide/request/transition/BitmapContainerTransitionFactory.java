package com.bumptech.glide.request.transition;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.bumptech.glide.load.DataSource;

public abstract class BitmapContainerTransitionFactory<R>
  implements TransitionFactory<R>
{
  private final TransitionFactory<Drawable> realFactory;
  
  public BitmapContainerTransitionFactory(TransitionFactory paramTransitionFactory)
  {
    realFactory = paramTransitionFactory;
  }
  
  public Transition build(DataSource paramDataSource, boolean paramBoolean)
  {
    return new BitmapGlideAnimation(realFactory.build(paramDataSource, paramBoolean));
  }
  
  protected abstract Bitmap getBitmap(Object paramObject);
  
  private final class BitmapGlideAnimation
    implements Transition<R>
  {
    private final Transition<Drawable> transition;
    
    BitmapGlideAnimation(Transition paramTransition)
    {
      transition = paramTransition;
    }
    
    public boolean transition(Object paramObject, Transition.ViewAdapter paramViewAdapter)
    {
      paramObject = new BitmapDrawable(paramViewAdapter.getView().getResources(), getBitmap(paramObject));
      return transition.transition(paramObject, paramViewAdapter);
    }
  }
}
