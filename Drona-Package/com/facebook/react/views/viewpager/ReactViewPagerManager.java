package com.facebook.react.views.viewpager;

import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import java.util.Map;

@ReactModule(name="AndroidViewPager")
public class ReactViewPagerManager
  extends ViewGroupManager<ReactViewPager>
{
  public static final int COMMAND_SET_PAGE = 1;
  public static final int COMMAND_SET_PAGE_WITHOUT_ANIMATION = 2;
  public static final String REACT_CLASS = "AndroidViewPager";
  
  public ReactViewPagerManager() {}
  
  public void addView(ReactViewPager paramReactViewPager, View paramView, int paramInt)
  {
    paramReactViewPager.addViewToAdapter(paramView, paramInt);
  }
  
  protected ReactViewPager createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactViewPager(paramThemedReactContext);
  }
  
  public View getChildAt(ReactViewPager paramReactViewPager, int paramInt)
  {
    return paramReactViewPager.getViewFromAdapter(paramInt);
  }
  
  public int getChildCount(ReactViewPager paramReactViewPager)
  {
    return paramReactViewPager.getViewCountInAdapter();
  }
  
  public Map getCommandsMap()
  {
    return MapBuilder.get("setPage", Integer.valueOf(1), "setPageWithoutAnimation", Integer.valueOf(2));
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.get("topPageScroll", MapBuilder.get("registrationName", "onPageScroll"), "topPageScrollStateChanged", MapBuilder.get("registrationName", "onPageScrollStateChanged"), "topPageSelected", MapBuilder.get("registrationName", "onPageSelected"));
  }
  
  public String getName()
  {
    return "AndroidViewPager";
  }
  
  public boolean needsCustomLayoutForChildren()
  {
    return true;
  }
  
  public void receiveCommand(ReactViewPager paramReactViewPager, int paramInt, ReadableArray paramReadableArray)
  {
    Assertions.assertNotNull(paramReactViewPager);
    Assertions.assertNotNull(paramReadableArray);
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException(String.format("Unsupported command %d received by %s.", new Object[] { Integer.valueOf(paramInt), getClass().getSimpleName() }));
    case 2: 
      paramReactViewPager.setCurrentItemFromJs(paramReadableArray.getInt(0), false);
      return;
    }
    paramReactViewPager.setCurrentItemFromJs(paramReadableArray.getInt(0), true);
  }
  
  public void receiveCommand(ReactViewPager paramReactViewPager, String paramString, ReadableArray paramReadableArray)
  {
    Assertions.assertNotNull(paramReactViewPager);
    Assertions.assertNotNull(paramReadableArray);
    int i = paramString.hashCode();
    if (i != -445763635)
    {
      if ((i == 1984860689) && (paramString.equals("setPage")))
      {
        i = 0;
        break label66;
      }
    }
    else if (paramString.equals("setPageWithoutAnimation"))
    {
      i = 1;
      break label66;
    }
    i = -1;
    switch (i)
    {
    default: 
      throw new IllegalArgumentException(String.format("Unsupported command %d received by %s.", new Object[] { paramString, getClass().getSimpleName() }));
    case 1: 
      label66:
      paramReactViewPager.setCurrentItemFromJs(paramReadableArray.getInt(0), false);
      return;
    }
    paramReactViewPager.setCurrentItemFromJs(paramReadableArray.getInt(0), true);
  }
  
  public void removeViewAt(ReactViewPager paramReactViewPager, int paramInt)
  {
    paramReactViewPager.removeViewFromAdapter(paramInt);
  }
  
  public void setPageMargin(ReactViewPager paramReactViewPager, float paramFloat)
  {
    paramReactViewPager.setPageMargin((int)PixelUtil.toPixelFromDIP(paramFloat));
  }
  
  public void setPeekEnabled(ReactViewPager paramReactViewPager, boolean paramBoolean)
  {
    paramReactViewPager.setClipToPadding(paramBoolean ^ true);
  }
  
  public void setScrollEnabled(ReactViewPager paramReactViewPager, boolean paramBoolean)
  {
    paramReactViewPager.setScrollEnabled(paramBoolean);
  }
}
