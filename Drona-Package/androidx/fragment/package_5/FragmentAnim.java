package androidx.fragment.package_5;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import androidx.core.opml.CancellationSignal;
import androidx.core.view.OneShotPreDrawListener;
import androidx.fragment.R.anim;
import androidx.fragment.R.id;

class FragmentAnim
{
  private FragmentAnim() {}
  
  static void animateRemoveFragment(Fragment paramFragment, AnimationOrAnimator paramAnimationOrAnimator, FragmentTransition.Callback paramCallback)
  {
    View localView = mView;
    ViewGroup localViewGroup = mContainer;
    localViewGroup.startViewTransition(localView);
    CancellationSignal localCancellationSignal = new CancellationSignal();
    localCancellationSignal.setOnCancelListener(new FragmentAnim.1(paramFragment));
    paramCallback.onStart(paramFragment, localCancellationSignal);
    if (animation != null)
    {
      paramAnimationOrAnimator = new EndViewTransitionAnimation(animation, localViewGroup, localView);
      paramFragment.setAnimatingAway(mView);
      paramAnimationOrAnimator.setAnimationListener(new FragmentAnim.2(localViewGroup, paramFragment, paramCallback, localCancellationSignal));
      mView.startAnimation(paramAnimationOrAnimator);
      return;
    }
    Animator localAnimator = animator;
    paramFragment.setAnimator(animator);
    localAnimator.addListener(new FragmentAnim.3(localViewGroup, localView, paramFragment, paramCallback, localCancellationSignal));
    localAnimator.setTarget(mView);
    localAnimator.start();
  }
  
  static AnimationOrAnimator loadAnimation(Context paramContext, FragmentContainer paramFragmentContainer, Fragment paramFragment, boolean paramBoolean)
  {
    int k = paramFragment.getNextTransition();
    int m = paramFragment.getNextAnim();
    j = 0;
    paramFragment.setNextAnim(0);
    paramFragmentContainer = paramFragmentContainer.onFindViewById(mContainerId);
    if ((paramFragmentContainer != null) && (paramFragmentContainer.getTag(R.id.visible_removing_fragment_view_tag) != null)) {
      paramFragmentContainer.setTag(R.id.visible_removing_fragment_view_tag, null);
    }
    if ((mContainer != null) && (mContainer.getLayoutTransition() != null)) {
      return null;
    }
    paramFragmentContainer = paramFragment.onCreateAnimation(k, paramBoolean, m);
    if (paramFragmentContainer != null) {
      return new AnimationOrAnimator(paramFragmentContainer);
    }
    paramFragmentContainer = paramFragment.onCreateAnimator(k, paramBoolean, m);
    if (paramFragmentContainer != null) {
      return new AnimationOrAnimator(paramFragmentContainer);
    }
    boolean bool;
    if (m != 0)
    {
      bool = "anim".equals(paramContext.getResources().getResourceTypeName(m));
      i = j;
      if (!bool) {}
    }
    try
    {
      try
      {
        paramFragmentContainer = AnimationUtils.loadAnimation(paramContext, m);
        if (paramFragmentContainer != null)
        {
          paramFragmentContainer = new AnimationOrAnimator(paramFragmentContainer);
          return paramFragmentContainer;
        }
        i = 1;
      }
      catch (Resources.NotFoundException paramContext)
      {
        throw paramContext;
      }
    }
    catch (RuntimeException paramFragmentContainer)
    {
      for (;;)
      {
        i = j;
      }
    }
    if (i == 0) {
      try
      {
        paramFragmentContainer = AnimatorInflater.loadAnimator(paramContext, m);
        if (paramFragmentContainer != null)
        {
          paramFragmentContainer = new AnimationOrAnimator(paramFragmentContainer);
          return paramFragmentContainer;
        }
      }
      catch (RuntimeException paramFragmentContainer)
      {
        if (!bool)
        {
          paramFragmentContainer = AnimationUtils.loadAnimation(paramContext, m);
          if (paramFragmentContainer != null) {
            return new AnimationOrAnimator(paramFragmentContainer);
          }
        }
        else
        {
          throw paramFragmentContainer;
        }
      }
    }
    if (k == 0) {
      return null;
    }
    i = transitToAnimResourceId(k, paramBoolean);
    if (i < 0) {
      return null;
    }
    return new AnimationOrAnimator(AnimationUtils.loadAnimation(paramContext, i));
  }
  
  private static int transitToAnimResourceId(int paramInt, boolean paramBoolean)
  {
    if (paramInt != 4097)
    {
      if (paramInt != 4099)
      {
        if (paramInt != 8194) {
          return -1;
        }
        if (paramBoolean) {
          return R.anim.fragment_close_enter;
        }
        return R.anim.fragment_close_exit;
      }
      if (paramBoolean) {
        return R.anim.fragment_fade_enter;
      }
      return R.anim.fragment_fade_exit;
    }
    if (paramBoolean) {
      return R.anim.fragment_open_enter;
    }
    return R.anim.fragment_open_exit;
  }
  
  class AnimationOrAnimator
  {
    public final Animation animation;
    public final Animator animator;
    
    AnimationOrAnimator()
    {
      animation = null;
      animator = this$1;
      if (this$1 != null) {
        return;
      }
      throw new IllegalStateException("Animator cannot be null");
    }
    
    AnimationOrAnimator()
    {
      animation = this$1;
      animator = null;
      if (this$1 != null) {
        return;
      }
      throw new IllegalStateException("Animation cannot be null");
    }
  }
  
  class EndViewTransitionAnimation
    extends AnimationSet
    implements Runnable
  {
    private boolean mAnimating = true;
    private final View mChild;
    private boolean mEnded;
    private final ViewGroup mParent;
    private boolean mTransitionEnded;
    
    EndViewTransitionAnimation(ViewGroup paramViewGroup, View paramView)
    {
      super();
      mParent = paramViewGroup;
      mChild = paramView;
      addAnimation(this$1);
      mParent.post(this);
    }
    
    public boolean getTransformation(long paramLong, Transformation paramTransformation)
    {
      mAnimating = true;
      if (mEnded) {
        return mTransitionEnded ^ true;
      }
      if (!super.getTransformation(paramLong, paramTransformation))
      {
        mEnded = true;
        OneShotPreDrawListener.a(mParent, this);
      }
      return true;
    }
    
    public boolean getTransformation(long paramLong, Transformation paramTransformation, float paramFloat)
    {
      mAnimating = true;
      if (mEnded) {
        return mTransitionEnded ^ true;
      }
      if (!super.getTransformation(paramLong, paramTransformation, paramFloat))
      {
        mEnded = true;
        OneShotPreDrawListener.a(mParent, this);
      }
      return true;
    }
    
    public void run()
    {
      if ((!mEnded) && (mAnimating))
      {
        mAnimating = false;
        mParent.post(this);
        return;
      }
      mParent.endViewTransition(mChild);
      mTransitionEnded = true;
    }
  }
}
