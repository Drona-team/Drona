package com.facebook.react.views.scroll;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.yoga.YogaConstants;
import java.util.ArrayList;
import java.util.List;

@ReactModule(name="AndroidHorizontalScrollView")
public class ReactHorizontalScrollViewManager
  extends ViewGroupManager<ReactHorizontalScrollView>
  implements ReactScrollViewCommandHelper.ScrollCommandHandler<ReactHorizontalScrollView>
{
  public static final String REACT_CLASS = "AndroidHorizontalScrollView";
  private static final int[] SPACING_TYPES = { 8, 0, 2, 1, 3 };
  @Nullable
  private FpsListener mFpsListener = null;
  
  public ReactHorizontalScrollViewManager()
  {
    this(null);
  }
  
  public ReactHorizontalScrollViewManager(FpsListener paramFpsListener)
  {
    mFpsListener = paramFpsListener;
  }
  
  public ReactHorizontalScrollView createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactHorizontalScrollView(paramThemedReactContext, mFpsListener);
  }
  
  public void flashScrollIndicators(ReactHorizontalScrollView paramReactHorizontalScrollView)
  {
    paramReactHorizontalScrollView.flashScrollIndicators();
  }
  
  public String getName()
  {
    return "AndroidHorizontalScrollView";
  }
  
  public void receiveCommand(ReactHorizontalScrollView paramReactHorizontalScrollView, int paramInt, ReadableArray paramReadableArray)
  {
    ReactScrollViewCommandHelper.receiveCommand(this, paramReactHorizontalScrollView, paramInt, paramReadableArray);
  }
  
  public void receiveCommand(ReactHorizontalScrollView paramReactHorizontalScrollView, String paramString, ReadableArray paramReadableArray)
  {
    ReactScrollViewCommandHelper.receiveCommand(this, paramReactHorizontalScrollView, paramString, paramReadableArray);
  }
  
  public void scrollTo(ReactHorizontalScrollView paramReactHorizontalScrollView, ReactScrollViewCommandHelper.ScrollToCommandData paramScrollToCommandData)
  {
    if (mAnimated)
    {
      paramReactHorizontalScrollView.smoothScrollTo(mDestX, mDestY);
      return;
    }
    paramReactHorizontalScrollView.scrollTo(mDestX, mDestY);
  }
  
  public void scrollToEnd(ReactHorizontalScrollView paramReactHorizontalScrollView, ReactScrollViewCommandHelper.ScrollToEndCommandData paramScrollToEndCommandData)
  {
    int i = paramReactHorizontalScrollView.getChildAt(0).getWidth() + paramReactHorizontalScrollView.getPaddingRight();
    if (mAnimated)
    {
      paramReactHorizontalScrollView.smoothScrollTo(i, paramReactHorizontalScrollView.getScrollY());
      return;
    }
    paramReactHorizontalScrollView.scrollTo(i, paramReactHorizontalScrollView.getScrollY());
  }
  
  public void setBorderColor(ReactHorizontalScrollView paramReactHorizontalScrollView, int paramInt, Integer paramInteger)
  {
    float f2 = NaN.0F;
    float f1;
    if (paramInteger == null) {
      f1 = NaN.0F;
    } else {
      f1 = paramInteger.intValue() & 0xFFFFFF;
    }
    if (paramInteger != null) {
      f2 = paramInteger.intValue() >>> 24;
    }
    paramReactHorizontalScrollView.setBorderColor(SPACING_TYPES[paramInt], f1, f2);
  }
  
  public void setBorderRadius(ReactHorizontalScrollView paramReactHorizontalScrollView, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    if (paramInt == 0)
    {
      paramReactHorizontalScrollView.setBorderRadius(f);
      return;
    }
    paramReactHorizontalScrollView.setBorderRadius(f, paramInt - 1);
  }
  
  public void setBorderStyle(ReactHorizontalScrollView paramReactHorizontalScrollView, String paramString)
  {
    paramReactHorizontalScrollView.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(ReactHorizontalScrollView paramReactHorizontalScrollView, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    paramReactHorizontalScrollView.setBorderWidth(SPACING_TYPES[paramInt], f);
  }
  
  public void setBottomFillColor(ReactHorizontalScrollView paramReactHorizontalScrollView, int paramInt)
  {
    paramReactHorizontalScrollView.setEndFillColor(paramInt);
  }
  
  public void setDecelerationRate(ReactHorizontalScrollView paramReactHorizontalScrollView, float paramFloat)
  {
    paramReactHorizontalScrollView.setDecelerationRate(paramFloat);
  }
  
  public void setDisableIntervalMomentum(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setDisableIntervalMomentum(paramBoolean);
  }
  
  public void setNestedScrollEnabled(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    ViewCompat.setNestedScrollingEnabled(paramReactHorizontalScrollView, paramBoolean);
  }
  
  public void setOverScrollMode(ReactHorizontalScrollView paramReactHorizontalScrollView, String paramString)
  {
    paramReactHorizontalScrollView.setOverScrollMode(ReactScrollViewHelper.parseOverScrollMode(paramString));
  }
  
  public void setOverflow(ReactHorizontalScrollView paramReactHorizontalScrollView, String paramString)
  {
    paramReactHorizontalScrollView.setOverflow(paramString);
  }
  
  public void setPagingEnabled(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setPagingEnabled(paramBoolean);
  }
  
  public void setPersistentScrollbar(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setScrollbarFadingEnabled(paramBoolean ^ true);
  }
  
  public void setRemoveClippedSubviews(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setRemoveClippedSubviews(paramBoolean);
  }
  
  public void setScrollEnabled(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setScrollEnabled(paramBoolean);
  }
  
  public void setScrollPerfTag(ReactHorizontalScrollView paramReactHorizontalScrollView, String paramString)
  {
    paramReactHorizontalScrollView.setScrollPerfTag(paramString);
  }
  
  public void setSendMomentumEvents(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setSendMomentumEvents(paramBoolean);
  }
  
  public void setShowsHorizontalScrollIndicator(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setHorizontalScrollBarEnabled(paramBoolean);
  }
  
  public void setSnapToEnd(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setSnapToEnd(paramBoolean);
  }
  
  public void setSnapToInterval(ReactHorizontalScrollView paramReactHorizontalScrollView, float paramFloat)
  {
    paramReactHorizontalScrollView.setSnapInterval((int)(paramFloat * getScreenDisplayMetricsdensity));
  }
  
  public void setSnapToOffsets(ReactHorizontalScrollView paramReactHorizontalScrollView, ReadableArray paramReadableArray)
  {
    DisplayMetrics localDisplayMetrics = DisplayMetricsHolder.getScreenDisplayMetrics();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramReadableArray.size())
    {
      localArrayList.add(Integer.valueOf((int)(paramReadableArray.getDouble(i) * density)));
      i += 1;
    }
    paramReactHorizontalScrollView.setSnapOffsets(localArrayList);
  }
  
  public void setSnapToStart(ReactHorizontalScrollView paramReactHorizontalScrollView, boolean paramBoolean)
  {
    paramReactHorizontalScrollView.setSnapToStart(paramBoolean);
  }
}
