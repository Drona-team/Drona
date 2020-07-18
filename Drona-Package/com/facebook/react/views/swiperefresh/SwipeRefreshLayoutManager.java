package com.facebook.react.views.swiperefresh;

import android.view.View;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.events.EventDispatcher;
import java.util.Map;

@ReactModule(name="AndroidSwipeRefreshLayout")
public class SwipeRefreshLayoutManager
  extends ViewGroupManager<ReactSwipeRefreshLayout>
{
  public static final String REACT_CLASS = "AndroidSwipeRefreshLayout";
  
  public SwipeRefreshLayoutManager() {}
  
  protected void addEventEmitters(final ThemedReactContext paramThemedReactContext, final ReactSwipeRefreshLayout paramReactSwipeRefreshLayout)
  {
    paramReactSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
    {
      public void onRefresh()
      {
        ((UIManagerModule)paramThemedReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher().dispatchEvent(new RefreshEvent(paramReactSwipeRefreshLayout.getId()));
      }
    });
  }
  
  protected ReactSwipeRefreshLayout createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactSwipeRefreshLayout(paramThemedReactContext);
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.builder().put("topRefresh", MapBuilder.get("registrationName", "onRefresh")).build();
  }
  
  public Map getExportedViewConstants()
  {
    return MapBuilder.get("SIZE", MapBuilder.get("DEFAULT", Integer.valueOf(1), "LARGE", Integer.valueOf(0)));
  }
  
  public String getName()
  {
    return "AndroidSwipeRefreshLayout";
  }
  
  public void setColors(ReactSwipeRefreshLayout paramReactSwipeRefreshLayout, ReadableArray paramReadableArray)
  {
    int i = 0;
    if (paramReadableArray != null)
    {
      int[] arrayOfInt = new int[paramReadableArray.size()];
      while (i < paramReadableArray.size())
      {
        arrayOfInt[i] = paramReadableArray.getInt(i);
        i += 1;
      }
      paramReactSwipeRefreshLayout.setColorSchemeColors(arrayOfInt);
      return;
    }
    paramReactSwipeRefreshLayout.setColorSchemeColors(new int[0]);
  }
  
  public void setEnabled(ReactSwipeRefreshLayout paramReactSwipeRefreshLayout, boolean paramBoolean)
  {
    paramReactSwipeRefreshLayout.setEnabled(paramBoolean);
  }
  
  public void setProgressBackgroundColor(ReactSwipeRefreshLayout paramReactSwipeRefreshLayout, int paramInt)
  {
    paramReactSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(paramInt);
  }
  
  public void setProgressViewOffset(ReactSwipeRefreshLayout paramReactSwipeRefreshLayout, float paramFloat)
  {
    paramReactSwipeRefreshLayout.setProgressViewOffset(paramFloat);
  }
  
  public void setRefreshing(ReactSwipeRefreshLayout paramReactSwipeRefreshLayout, boolean paramBoolean)
  {
    paramReactSwipeRefreshLayout.setRefreshing(paramBoolean);
  }
  
  public void setSize(ReactSwipeRefreshLayout paramReactSwipeRefreshLayout, Dynamic paramDynamic)
  {
    if (paramDynamic.isNull())
    {
      paramReactSwipeRefreshLayout.setSize(1);
      return;
    }
    if (paramDynamic.getType() == ReadableType.Number)
    {
      paramReactSwipeRefreshLayout.setSize(paramDynamic.asInt());
      return;
    }
    if (paramDynamic.getType() == ReadableType.String)
    {
      paramDynamic = paramDynamic.asString();
      if (paramDynamic.equals("default"))
      {
        paramReactSwipeRefreshLayout.setSize(1);
        return;
      }
      if (paramDynamic.equals("large"))
      {
        paramReactSwipeRefreshLayout.setSize(0);
        return;
      }
      paramReactSwipeRefreshLayout = new StringBuilder();
      paramReactSwipeRefreshLayout.append("Size must be 'default' or 'large', received: ");
      paramReactSwipeRefreshLayout.append(paramDynamic);
      throw new IllegalArgumentException(paramReactSwipeRefreshLayout.toString());
    }
    throw new IllegalArgumentException("Size must be 'default' or 'large'");
  }
}
