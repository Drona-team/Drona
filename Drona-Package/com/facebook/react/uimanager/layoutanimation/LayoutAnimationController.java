package com.facebook.react.uimanager.layoutanimation;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LayoutAnimationController
{
  @Nullable
  private static Handler sCompletionHandler;
  @Nullable
  private Runnable mCompletionRunnable;
  private final AbstractLayoutAnimation mLayoutCreateAnimation = new LayoutCreateAnimation();
  private final AbstractLayoutAnimation mLayoutDeleteAnimation = new LayoutDeleteAnimation();
  private final SparseArray<LayoutHandlingAnimation> mLayoutHandlers = new SparseArray(0);
  private final AbstractLayoutAnimation mLayoutUpdateAnimation = new LayoutUpdateAnimation();
  private long mMaxAnimationDuration = -1L;
  private boolean mShouldAnimateLayout;
  
  public LayoutAnimationController() {}
  
  private void disableUserInteractions(View paramView)
  {
    int i = 0;
    paramView.setClickable(false);
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      while (i < paramView.getChildCount())
      {
        disableUserInteractions(paramView.getChildAt(i));
        i += 1;
      }
    }
  }
  
  private void scheduleCompletionCallback(long paramLong)
  {
    if (sCompletionHandler == null) {
      sCompletionHandler = new Handler(Looper.getMainLooper());
    }
    if (mCompletionRunnable != null)
    {
      sCompletionHandler.removeCallbacks(mCompletionRunnable);
      sCompletionHandler.postDelayed(mCompletionRunnable, paramLong);
    }
  }
  
  public void applyLayoutUpdate(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    UiThreadUtil.assertOnUiThread();
    final int i = paramView.getId();
    Object localObject = (LayoutHandlingAnimation)mLayoutHandlers.get(i);
    if (localObject != null)
    {
      ((LayoutHandlingAnimation)localObject).onLayoutUpdate(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    if ((paramView.getWidth() != 0) && (paramView.getHeight() != 0)) {
      localObject = mLayoutUpdateAnimation;
    } else {
      localObject = mLayoutCreateAnimation;
    }
    localObject = ((AbstractLayoutAnimation)localObject).createAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((localObject instanceof LayoutHandlingAnimation)) {
      ((Animation)localObject).setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          mLayoutHandlers.remove(i);
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation)
        {
          mLayoutHandlers.put(i, (LayoutHandlingAnimation)paramAnonymousAnimation);
        }
      });
    } else {
      paramView.layout(paramInt1, paramInt2, paramInt3 + paramInt1, paramInt4 + paramInt2);
    }
    if (localObject != null)
    {
      long l = ((Animation)localObject).getDuration();
      if (l > mMaxAnimationDuration)
      {
        mMaxAnimationDuration = l;
        scheduleCompletionCallback(l);
      }
      paramView.startAnimation((Animation)localObject);
    }
  }
  
  public void deleteView(View paramView, final LayoutAnimationListener paramLayoutAnimationListener)
  {
    UiThreadUtil.assertOnUiThread();
    Animation localAnimation = mLayoutDeleteAnimation.createAnimation(paramView, paramView.getLeft(), paramView.getTop(), paramView.getWidth(), paramView.getHeight());
    if (localAnimation != null)
    {
      disableUserInteractions(paramView);
      localAnimation.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          paramLayoutAnimationListener.onAnimationEnd();
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      long l = localAnimation.getDuration();
      if (l > mMaxAnimationDuration)
      {
        scheduleCompletionCallback(l);
        mMaxAnimationDuration = l;
      }
      paramView.startAnimation(localAnimation);
      return;
    }
    paramLayoutAnimationListener.onAnimationEnd();
  }
  
  public void initializeFromConfig(ReadableMap paramReadableMap, final Callback paramCallback)
  {
    if (paramReadableMap == null)
    {
      reset();
      return;
    }
    int i = 0;
    mShouldAnimateLayout = false;
    if (paramReadableMap.hasKey("duration")) {
      i = paramReadableMap.getInt("duration");
    }
    if (paramReadableMap.hasKey(LayoutAnimationType.toString(LayoutAnimationType.CREATE)))
    {
      mLayoutCreateAnimation.initializeFromConfig(paramReadableMap.getMap(LayoutAnimationType.toString(LayoutAnimationType.CREATE)), i);
      mShouldAnimateLayout = true;
    }
    if (paramReadableMap.hasKey(LayoutAnimationType.toString(LayoutAnimationType.UPDATE)))
    {
      mLayoutUpdateAnimation.initializeFromConfig(paramReadableMap.getMap(LayoutAnimationType.toString(LayoutAnimationType.UPDATE)), i);
      mShouldAnimateLayout = true;
    }
    if (paramReadableMap.hasKey(LayoutAnimationType.toString(LayoutAnimationType.DELETE)))
    {
      mLayoutDeleteAnimation.initializeFromConfig(paramReadableMap.getMap(LayoutAnimationType.toString(LayoutAnimationType.DELETE)), i);
      mShouldAnimateLayout = true;
    }
    if ((mShouldAnimateLayout) && (paramCallback != null)) {
      mCompletionRunnable = new Runnable()
      {
        public void run()
        {
          paramCallback.invoke(new Object[] { Boolean.TRUE });
        }
      };
    }
  }
  
  public void reset()
  {
    mLayoutCreateAnimation.reset();
    mLayoutUpdateAnimation.reset();
    mLayoutDeleteAnimation.reset();
    mCompletionRunnable = null;
    mShouldAnimateLayout = false;
    mMaxAnimationDuration = -1L;
  }
  
  public boolean shouldAnimateLayout(View paramView)
  {
    return ((mShouldAnimateLayout) && (paramView.getParent() != null)) || (mLayoutHandlers.get(paramView.getId()) != null);
  }
}
