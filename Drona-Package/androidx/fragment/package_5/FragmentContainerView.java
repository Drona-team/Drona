package androidx.fragment.package_5;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.fragment.R.styleable;
import java.util.ArrayList;

public final class FragmentContainerView
  extends FrameLayout
{
  private ArrayList<View> mDisappearingFragmentChildren;
  private boolean mDrawDisappearingViewsFirst = true;
  private ArrayList<View> mTransitioningFragmentViews;
  
  public FragmentContainerView(Context paramContext)
  {
    super(paramContext);
  }
  
  public FragmentContainerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    throw new UnsupportedOperationException("FragmentContainerView must be within a FragmentActivity to be instantiated from XML.");
  }
  
  public FragmentContainerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    throw new UnsupportedOperationException("FragmentContainerView must be within a FragmentActivity to be instantiated from XML.");
  }
  
  FragmentContainerView(Context paramContext, AttributeSet paramAttributeSet, FragmentManager paramFragmentManager)
  {
    super(paramContext, paramAttributeSet);
    String str = paramAttributeSet.getClassAttribute();
    Object localObject1 = str;
    Object localObject2 = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FragmentContainerView);
    if (str == null) {
      localObject1 = ((TypedArray)localObject2).getString(R.styleable.FragmentContainerView_android_name);
    }
    str = ((TypedArray)localObject2).getString(R.styleable.FragmentContainerView_android_tag);
    ((TypedArray)localObject2).recycle();
    int i = getId();
    localObject2 = paramFragmentManager.findFragmentById(i);
    if ((localObject1 != null) && (localObject2 == null))
    {
      if (i <= 0)
      {
        if (str != null)
        {
          paramContext = new StringBuilder();
          paramContext.append(" with tag ");
          paramContext.append(str);
          paramContext = paramContext.toString();
        }
        else
        {
          paramContext = "";
        }
        paramAttributeSet = new StringBuilder();
        paramAttributeSet.append("FragmentContainerView must have an android:id to add Fragment ");
        paramAttributeSet.append((String)localObject1);
        paramAttributeSet.append(paramContext);
        throw new IllegalStateException(paramAttributeSet.toString());
      }
      localObject1 = paramFragmentManager.getFragmentFactory().instantiate(paramContext.getClassLoader(), (String)localObject1);
      ((Fragment)localObject1).onInflate(paramContext, paramAttributeSet, null);
      paramFragmentManager.beginTransaction().setReorderingAllowed(true).add(this, (Fragment)localObject1, str).commitNowAllowingStateLoss();
    }
  }
  
  private void addDisappearingFragmentView(View paramView)
  {
    if ((paramView.getAnimation() != null) || ((mTransitioningFragmentViews != null) && (mTransitioningFragmentViews.contains(paramView))))
    {
      if (mDisappearingFragmentChildren == null) {
        mDisappearingFragmentChildren = new ArrayList();
      }
      mDisappearingFragmentChildren.add(paramView);
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (FragmentManager.getViewFragment(paramView) != null)
    {
      super.addView(paramView, paramInt, paramLayoutParams);
      return;
    }
    paramLayoutParams = new StringBuilder();
    paramLayoutParams.append("Views added to a FragmentContainerView must be associated with a Fragment. View ");
    paramLayoutParams.append(paramView);
    paramLayoutParams.append(" is not associated with a Fragment.");
    throw new IllegalStateException(paramLayoutParams.toString());
  }
  
  protected boolean addViewInLayout(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (FragmentManager.getViewFragment(paramView) != null) {
      return super.addViewInLayout(paramView, paramInt, paramLayoutParams, paramBoolean);
    }
    paramLayoutParams = new StringBuilder();
    paramLayoutParams.append("Views added to a FragmentContainerView must be associated with a Fragment. View ");
    paramLayoutParams.append(paramView);
    paramLayoutParams.append(" is not associated with a Fragment.");
    throw new IllegalStateException(paramLayoutParams.toString());
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    if ((mDrawDisappearingViewsFirst) && (mDisappearingFragmentChildren != null))
    {
      int i = 0;
      while (i < mDisappearingFragmentChildren.size())
      {
        super.drawChild(paramCanvas, (View)mDisappearingFragmentChildren.get(i), getDrawingTime());
        i += 1;
      }
    }
    super.dispatchDraw(paramCanvas);
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    if ((mDrawDisappearingViewsFirst) && (mDisappearingFragmentChildren != null) && (mDisappearingFragmentChildren.size() > 0) && (mDisappearingFragmentChildren.contains(paramView))) {
      return false;
    }
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  public void endViewTransition(View paramView)
  {
    if (mTransitioningFragmentViews != null)
    {
      mTransitioningFragmentViews.remove(paramView);
      if ((mDisappearingFragmentChildren != null) && (mDisappearingFragmentChildren.remove(paramView))) {
        mDrawDisappearingViewsFirst = true;
      }
    }
    super.endViewTransition(paramView);
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    int i = 0;
    while (i < getChildCount())
    {
      getChildAt(i).dispatchApplyWindowInsets(new WindowInsets(paramWindowInsets));
      i += 1;
    }
    return paramWindowInsets;
  }
  
  public void removeAllViewsInLayout()
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      addDisappearingFragmentView(getChildAt(i));
      i -= 1;
    }
    super.removeAllViewsInLayout();
  }
  
  protected void removeDetachedView(View paramView, boolean paramBoolean)
  {
    if (paramBoolean) {
      addDisappearingFragmentView(paramView);
    }
    super.removeDetachedView(paramView, paramBoolean);
  }
  
  public void removeView(View paramView)
  {
    addDisappearingFragmentView(paramView);
    super.removeView(paramView);
  }
  
  public void removeViewAt(int paramInt)
  {
    addDisappearingFragmentView(getChildAt(paramInt));
    super.removeViewAt(paramInt);
  }
  
  public void removeViewInLayout(View paramView)
  {
    addDisappearingFragmentView(paramView);
    super.removeViewInLayout(paramView);
  }
  
  public void removeViews(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    while (i < paramInt1 + paramInt2)
    {
      addDisappearingFragmentView(getChildAt(i));
      i += 1;
    }
    super.removeViews(paramInt1, paramInt2);
  }
  
  public void removeViewsInLayout(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    while (i < paramInt1 + paramInt2)
    {
      addDisappearingFragmentView(getChildAt(i));
      i += 1;
    }
    super.removeViewsInLayout(paramInt1, paramInt2);
  }
  
  void setDrawDisappearingViewsLast(boolean paramBoolean)
  {
    mDrawDisappearingViewsFirst = paramBoolean;
  }
  
  public void setLayoutTransition(LayoutTransition paramLayoutTransition)
  {
    if (Build.VERSION.SDK_INT < 18)
    {
      super.setLayoutTransition(paramLayoutTransition);
      return;
    }
    throw new UnsupportedOperationException("FragmentContainerView does not support Layout Transitions or animateLayoutChanges=\"true\".");
  }
  
  public void startViewTransition(View paramView)
  {
    if (paramView.getParent() == this)
    {
      if (mTransitioningFragmentViews == null) {
        mTransitioningFragmentViews = new ArrayList();
      }
      mTransitioningFragmentViews.add(paramView);
    }
    super.startViewTransition(paramView);
  }
}
