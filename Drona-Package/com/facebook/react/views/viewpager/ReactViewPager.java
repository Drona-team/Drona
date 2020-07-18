package com.facebook.react.views.viewpager;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.NativeGestureUtil;
import java.util.ArrayList;
import java.util.List;

public class ReactViewPager
  extends ViewPager
{
  private final EventDispatcher mEventDispatcher;
  private boolean mIsCurrentItemFromJs;
  private boolean mScrollEnabled = true;
  private final Runnable measureAndLayout = new Runnable()
  {
    public void run()
    {
      measure(View.MeasureSpec.makeMeasureSpec(getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getHeight(), 1073741824));
      layout(getLeft(), getTop(), getRight(), getBottom());
    }
  };
  
  public ReactViewPager(ReactContext paramReactContext)
  {
    super(paramReactContext);
    mEventDispatcher = ((UIManagerModule)paramReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher();
    mIsCurrentItemFromJs = false;
    setOnPageChangeListener(new PageChangeListener(null));
    setAdapter(new Adapter(null));
  }
  
  void addViewToAdapter(View paramView, int paramInt)
  {
    getAdapter().addView(paramView, paramInt);
  }
  
  public Adapter getAdapter()
  {
    return (Adapter)super.getAdapter();
  }
  
  int getViewCountInAdapter()
  {
    return getAdapter().getCount();
  }
  
  View getViewFromAdapter(int paramInt)
  {
    return getAdapter().getViewAt(paramInt);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    requestLayout();
    post(measureAndLayout);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!mScrollEnabled) {
      return false;
    }
    try
    {
      boolean bool = super.onInterceptTouchEvent(paramMotionEvent);
      if (bool)
      {
        NativeGestureUtil.notifyNativeGestureStarted(this, paramMotionEvent);
        return true;
      }
    }
    catch (IllegalArgumentException paramMotionEvent)
    {
      FLog.w("ReactNative", "Error intercepting touch event.", paramMotionEvent);
    }
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!mScrollEnabled) {
      return false;
    }
    try
    {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      return bool;
    }
    catch (IllegalArgumentException paramMotionEvent)
    {
      FLog.w("ReactNative", "Error handling touch event.", paramMotionEvent);
    }
    return false;
  }
  
  public void removeAllViewsFromAdapter()
  {
    getAdapter().removeAllViewsFromAdapter(this);
  }
  
  void removeViewFromAdapter(int paramInt)
  {
    getAdapter().removeViewAt(paramInt);
  }
  
  public void setCurrentItemFromJs(int paramInt, boolean paramBoolean)
  {
    mIsCurrentItemFromJs = true;
    setCurrentItem(paramInt, paramBoolean);
    mIsCurrentItemFromJs = false;
  }
  
  public void setScrollEnabled(boolean paramBoolean)
  {
    mScrollEnabled = paramBoolean;
  }
  
  public void setViews(List paramList)
  {
    getAdapter().setViews(paramList);
  }
  
  private class Adapter
    extends PagerAdapter
  {
    private boolean mIsViewPagerInIntentionallyInconsistentState = false;
    private final List<View> mViews = new ArrayList();
    
    private Adapter() {}
    
    void addView(View paramView, int paramInt)
    {
      mViews.add(paramInt, paramView);
      notifyDataSetChanged();
      setOffscreenPageLimit(mViews.size());
    }
    
    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      paramViewGroup.removeView((View)paramObject);
    }
    
    public int getCount()
    {
      return mViews.size();
    }
    
    public int getItemPosition(Object paramObject)
    {
      if ((!mIsViewPagerInIntentionallyInconsistentState) && (mViews.contains(paramObject))) {
        return mViews.indexOf(paramObject);
      }
      return -2;
    }
    
    View getViewAt(int paramInt)
    {
      return (View)mViews.get(paramInt);
    }
    
    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      View localView = (View)mViews.get(paramInt);
      paramViewGroup.addView(localView, 0, generateDefaultLayoutParams());
      return localView;
    }
    
    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView == paramObject;
    }
    
    void removeAllViewsFromAdapter(ViewPager paramViewPager)
    {
      mViews.clear();
      paramViewPager.removeAllViews();
      mIsViewPagerInIntentionallyInconsistentState = true;
    }
    
    void removeViewAt(int paramInt)
    {
      mViews.remove(paramInt);
      notifyDataSetChanged();
      setOffscreenPageLimit(mViews.size());
    }
    
    void setViews(List paramList)
    {
      mViews.clear();
      mViews.addAll(paramList);
      notifyDataSetChanged();
      mIsViewPagerInIntentionallyInconsistentState = false;
    }
  }
  
  private class PageChangeListener
    implements ViewPager.OnPageChangeListener
  {
    private PageChangeListener() {}
    
    public void onPageScrollStateChanged(int paramInt)
    {
      String str;
      switch (paramInt)
      {
      default: 
        throw new IllegalStateException("Unsupported pageScrollState");
      case 2: 
        str = "settling";
        break;
      case 1: 
        str = "dragging";
        break;
      case 0: 
        str = "idle";
      }
      mEventDispatcher.dispatchEvent(new PageScrollStateChangedEvent(getId(), str));
    }
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
      mEventDispatcher.dispatchEvent(new PageScrollEvent(getId(), paramInt1, paramFloat));
    }
    
    public void onPageSelected(int paramInt)
    {
      if (!mIsCurrentItemFromJs) {
        mEventDispatcher.dispatchEvent(new PageSelectedEvent(getId(), paramInt));
      }
    }
  }
}
