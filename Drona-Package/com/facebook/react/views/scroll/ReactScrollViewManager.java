package com.facebook.react.views.scroll;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.yoga.YogaConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ReactModule(name="RCTScrollView")
public class ReactScrollViewManager
  extends ViewGroupManager<ReactScrollView>
  implements ReactScrollViewCommandHelper.ScrollCommandHandler<ReactScrollView>
{
  public static final String REACT_CLASS = "RCTScrollView";
  private static final int[] SPACING_TYPES = { 8, 0, 2, 1, 3 };
  @Nullable
  private FpsListener mFpsListener = null;
  
  public ReactScrollViewManager()
  {
    this(null);
  }
  
  public ReactScrollViewManager(FpsListener paramFpsListener)
  {
    mFpsListener = paramFpsListener;
  }
  
  public static Map createExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.builder().put(ScrollEventType.getJSEventName(ScrollEventType.SCROLL), MapBuilder.get("registrationName", "onScroll")).put(ScrollEventType.getJSEventName(ScrollEventType.BEGIN_DRAG), MapBuilder.get("registrationName", "onScrollBeginDrag")).put(ScrollEventType.getJSEventName(ScrollEventType.END_DRAG), MapBuilder.get("registrationName", "onScrollEndDrag")).put(ScrollEventType.getJSEventName(ScrollEventType.MOMENTUM_BEGIN), MapBuilder.get("registrationName", "onMomentumScrollBegin")).put(ScrollEventType.getJSEventName(ScrollEventType.MOMENTUM_END), MapBuilder.get("registrationName", "onMomentumScrollEnd")).build();
  }
  
  public ReactScrollView createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactScrollView(paramThemedReactContext, mFpsListener);
  }
  
  public void flashScrollIndicators(ReactScrollView paramReactScrollView)
  {
    paramReactScrollView.flashScrollIndicators();
  }
  
  public Map getCommandsMap()
  {
    return ReactScrollViewCommandHelper.getCommandsMap();
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return createExportedCustomDirectEventTypeConstants();
  }
  
  public String getName()
  {
    return "RCTScrollView";
  }
  
  public void receiveCommand(ReactScrollView paramReactScrollView, int paramInt, ReadableArray paramReadableArray)
  {
    ReactScrollViewCommandHelper.receiveCommand(this, paramReactScrollView, paramInt, paramReadableArray);
  }
  
  public void receiveCommand(ReactScrollView paramReactScrollView, String paramString, ReadableArray paramReadableArray)
  {
    ReactScrollViewCommandHelper.receiveCommand(this, paramReactScrollView, paramString, paramReadableArray);
  }
  
  public void scrollTo(ReactScrollView paramReactScrollView, ReactScrollViewCommandHelper.ScrollToCommandData paramScrollToCommandData)
  {
    if (mAnimated)
    {
      paramReactScrollView.smoothScrollTo(mDestX, mDestY);
      return;
    }
    paramReactScrollView.scrollTo(mDestX, mDestY);
  }
  
  public void scrollToEnd(ReactScrollView paramReactScrollView, ReactScrollViewCommandHelper.ScrollToEndCommandData paramScrollToEndCommandData)
  {
    int i = paramReactScrollView.getChildAt(0).getHeight() + paramReactScrollView.getPaddingBottom();
    if (mAnimated)
    {
      paramReactScrollView.smoothScrollTo(paramReactScrollView.getScrollX(), i);
      return;
    }
    paramReactScrollView.scrollTo(paramReactScrollView.getScrollX(), i);
  }
  
  public void setBorderColor(ReactScrollView paramReactScrollView, int paramInt, Integer paramInteger)
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
    paramReactScrollView.setBorderColor(SPACING_TYPES[paramInt], f1, f2);
  }
  
  public void setBorderRadius(ReactScrollView paramReactScrollView, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    if (paramInt == 0)
    {
      paramReactScrollView.setBorderRadius(f);
      return;
    }
    paramReactScrollView.setBorderRadius(f, paramInt - 1);
  }
  
  public void setBorderStyle(ReactScrollView paramReactScrollView, String paramString)
  {
    paramReactScrollView.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(ReactScrollView paramReactScrollView, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    paramReactScrollView.setBorderWidth(SPACING_TYPES[paramInt], f);
  }
  
  public void setBottomFillColor(ReactScrollView paramReactScrollView, int paramInt)
  {
    paramReactScrollView.setEndFillColor(paramInt);
  }
  
  public void setDecelerationRate(ReactScrollView paramReactScrollView, float paramFloat)
  {
    paramReactScrollView.setDecelerationRate(paramFloat);
  }
  
  public void setNestedScrollEnabled(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    ViewCompat.setNestedScrollingEnabled(paramReactScrollView, paramBoolean);
  }
  
  public void setOverScrollMode(ReactScrollView paramReactScrollView, String paramString)
  {
    paramReactScrollView.setOverScrollMode(ReactScrollViewHelper.parseOverScrollMode(paramString));
  }
  
  public void setOverflow(ReactScrollView paramReactScrollView, String paramString)
  {
    paramReactScrollView.setOverflow(paramString);
  }
  
  public void setPagingEnabled(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setPagingEnabled(paramBoolean);
  }
  
  public void setPersistentScrollbar(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setScrollbarFadingEnabled(paramBoolean ^ true);
  }
  
  public void setRemoveClippedSubviews(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setRemoveClippedSubviews(paramBoolean);
  }
  
  public void setScrollEnabled(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setScrollEnabled(paramBoolean);
    paramReactScrollView.setFocusable(paramBoolean);
  }
  
  public void setScrollPerfTag(ReactScrollView paramReactScrollView, String paramString)
  {
    paramReactScrollView.setScrollPerfTag(paramString);
  }
  
  public void setSendMomentumEvents(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setSendMomentumEvents(paramBoolean);
  }
  
  public void setShowsVerticalScrollIndicator(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setVerticalScrollBarEnabled(paramBoolean);
  }
  
  public void setSnapToEnd(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setSnapToEnd(paramBoolean);
  }
  
  public void setSnapToInterval(ReactScrollView paramReactScrollView, float paramFloat)
  {
    paramReactScrollView.setSnapInterval((int)(paramFloat * getScreenDisplayMetricsdensity));
  }
  
  public void setSnapToOffsets(ReactScrollView paramReactScrollView, ReadableArray paramReadableArray)
  {
    DisplayMetrics localDisplayMetrics = DisplayMetricsHolder.getScreenDisplayMetrics();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramReadableArray.size())
    {
      localArrayList.add(Integer.valueOf((int)(paramReadableArray.getDouble(i) * density)));
      i += 1;
    }
    paramReactScrollView.setSnapOffsets(localArrayList);
  }
  
  public void setSnapToStart(ReactScrollView paramReactScrollView, boolean paramBoolean)
  {
    paramReactScrollView.setSnapToStart(paramBoolean);
  }
}
