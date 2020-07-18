package com.facebook.react.views.drawer;

import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.drawer.events.DrawerClosedEvent;
import com.facebook.react.views.drawer.events.DrawerOpenedEvent;
import com.facebook.react.views.drawer.events.DrawerSlideEvent;
import com.facebook.react.views.drawer.events.DrawerStateChangedEvent;
import java.lang.reflect.Method;
import java.util.Map;

@ReactModule(name="AndroidDrawerLayout")
public class ReactDrawerLayoutManager
  extends ViewGroupManager<ReactDrawerLayout>
{
  public static final int CLOSE_DRAWER = 2;
  public static final int OPEN_DRAWER = 1;
  protected static final String REACT_CLASS = "AndroidDrawerLayout";
  
  public ReactDrawerLayoutManager() {}
  
  protected void addEventEmitters(ThemedReactContext paramThemedReactContext, ReactDrawerLayout paramReactDrawerLayout)
  {
    paramReactDrawerLayout.setDrawerListener(new DrawerEventEmitter(paramReactDrawerLayout, ((UIManagerModule)paramThemedReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher()));
  }
  
  public void addView(ReactDrawerLayout paramReactDrawerLayout, View paramView, int paramInt)
  {
    if (getChildCount(paramReactDrawerLayout) < 2)
    {
      if ((paramInt != 0) && (paramInt != 1))
      {
        paramReactDrawerLayout = new StringBuilder();
        paramReactDrawerLayout.append("The only valid indices for drawer's child are 0 or 1. Got ");
        paramReactDrawerLayout.append(paramInt);
        paramReactDrawerLayout.append(" instead.");
        throw new JSApplicationIllegalArgumentException(paramReactDrawerLayout.toString());
      }
      paramReactDrawerLayout.addView(paramView, paramInt);
      paramReactDrawerLayout.setDrawerProperties();
      return;
    }
    throw new JSApplicationIllegalArgumentException("The Drawer cannot have more than two children");
  }
  
  protected ReactDrawerLayout createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactDrawerLayout(paramThemedReactContext);
  }
  
  public Map getCommandsMap()
  {
    return MapBuilder.get("openDrawer", Integer.valueOf(1), "closeDrawer", Integer.valueOf(2));
  }
  
  public void getDrawerWidth(ReactDrawerLayout paramReactDrawerLayout, float paramFloat)
  {
    int i;
    if (Float.isNaN(paramFloat)) {
      i = -1;
    } else {
      i = Math.round(PixelUtil.toPixelFromDIP(paramFloat));
    }
    paramReactDrawerLayout.setDrawerWidth(i);
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.get("topDrawerSlide", MapBuilder.get("registrationName", "onDrawerSlide"), "topDrawerOpen", MapBuilder.get("registrationName", "onDrawerOpen"), "topDrawerClose", MapBuilder.get("registrationName", "onDrawerClose"), "topDrawerStateChanged", MapBuilder.get("registrationName", "onDrawerStateChanged"));
  }
  
  public Map getExportedViewConstants()
  {
    return MapBuilder.get("DrawerPosition", MapBuilder.get("Left", Integer.valueOf(8388611), "Right", Integer.valueOf(8388613)));
  }
  
  public String getName()
  {
    return "AndroidDrawerLayout";
  }
  
  public boolean needsCustomLayoutForChildren()
  {
    return true;
  }
  
  public void receiveCommand(ReactDrawerLayout paramReactDrawerLayout, int paramInt, ReadableArray paramReadableArray)
  {
    switch (paramInt)
    {
    default: 
      return;
      return;
    case 2: 
      paramReactDrawerLayout.closeDrawer();
      return;
    }
    paramReactDrawerLayout.openDrawer();
  }
  
  public void receiveCommand(ReactDrawerLayout paramReactDrawerLayout, String paramString, ReadableArray paramReadableArray)
  {
    int i = paramString.hashCode();
    if (i != -258774775)
    {
      if ((i == -83186725) && (paramString.equals("openDrawer")))
      {
        i = 0;
        break label56;
      }
    }
    else if (paramString.equals("closeDrawer"))
    {
      i = 1;
      break label56;
    }
    i = -1;
    switch (i)
    {
    default: 
      return;
    case 1: 
      label56:
      paramReactDrawerLayout.closeDrawer();
      return;
    }
    paramReactDrawerLayout.openDrawer();
  }
  
  public void setDrawerLockMode(ReactDrawerLayout paramReactDrawerLayout, String paramString)
  {
    if ((paramString != null) && (!"unlocked".equals(paramString)))
    {
      if ("locked-closed".equals(paramString))
      {
        paramReactDrawerLayout.setDrawerLockMode(1);
        return;
      }
      if ("locked-open".equals(paramString))
      {
        paramReactDrawerLayout.setDrawerLockMode(2);
        return;
      }
      paramReactDrawerLayout = new StringBuilder();
      paramReactDrawerLayout.append("Unknown drawerLockMode ");
      paramReactDrawerLayout.append(paramString);
      throw new JSApplicationIllegalArgumentException(paramReactDrawerLayout.toString());
    }
    paramReactDrawerLayout.setDrawerLockMode(0);
  }
  
  public void setDrawerPosition(ReactDrawerLayout paramReactDrawerLayout, Dynamic paramDynamic)
  {
    if (paramDynamic.isNull())
    {
      paramReactDrawerLayout.setDrawerPosition(8388611);
      return;
    }
    if (paramDynamic.getType() == ReadableType.Number)
    {
      int i = paramDynamic.asInt();
      if ((8388611 != i) && (8388613 != i))
      {
        paramReactDrawerLayout = new StringBuilder();
        paramReactDrawerLayout.append("Unknown drawerPosition ");
        paramReactDrawerLayout.append(i);
        throw new JSApplicationIllegalArgumentException(paramReactDrawerLayout.toString());
      }
      paramReactDrawerLayout.setDrawerPosition(i);
      return;
    }
    if (paramDynamic.getType() == ReadableType.String)
    {
      paramDynamic = paramDynamic.asString();
      if (paramDynamic.equals("left"))
      {
        paramReactDrawerLayout.setDrawerPosition(8388611);
        return;
      }
      if (paramDynamic.equals("right"))
      {
        paramReactDrawerLayout.setDrawerPosition(8388613);
        return;
      }
      paramReactDrawerLayout = new StringBuilder();
      paramReactDrawerLayout.append("drawerPosition must be 'left' or 'right', received");
      paramReactDrawerLayout.append(paramDynamic);
      throw new JSApplicationIllegalArgumentException(paramReactDrawerLayout.toString());
    }
    throw new JSApplicationIllegalArgumentException("drawerPosition must be a string or int");
  }
  
  public void setElevation(ReactDrawerLayout paramReactDrawerLayout, float paramFloat)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      Object localObject = Float.TYPE;
      try
      {
        localObject = ReactDrawerLayout.class.getMethod("setDrawerElevation", new Class[] { localObject });
        paramFloat = PixelUtil.toPixelFromDIP(paramFloat);
        ((Method)localObject).invoke(paramReactDrawerLayout, new Object[] { Float.valueOf(paramFloat) });
        return;
      }
      catch (Exception paramReactDrawerLayout)
      {
        FLog.w("ReactNative", "setDrawerElevation is not available in this version of the support lib.", paramReactDrawerLayout);
      }
    }
  }
  
  public static class DrawerEventEmitter
    implements DrawerLayout.DrawerListener
  {
    private final DrawerLayout mDrawerLayout;
    private final EventDispatcher mEventDispatcher;
    
    public DrawerEventEmitter(DrawerLayout paramDrawerLayout, EventDispatcher paramEventDispatcher)
    {
      mDrawerLayout = paramDrawerLayout;
      mEventDispatcher = paramEventDispatcher;
    }
    
    public void onDrawerClosed(View paramView)
    {
      mEventDispatcher.dispatchEvent(new DrawerClosedEvent(mDrawerLayout.getId()));
    }
    
    public void onDrawerOpened(View paramView)
    {
      mEventDispatcher.dispatchEvent(new DrawerOpenedEvent(mDrawerLayout.getId()));
    }
    
    public void onDrawerSlide(View paramView, float paramFloat)
    {
      mEventDispatcher.dispatchEvent(new DrawerSlideEvent(mDrawerLayout.getId(), paramFloat));
    }
    
    public void onDrawerStateChanged(int paramInt)
    {
      mEventDispatcher.dispatchEvent(new DrawerStateChangedEvent(mDrawerLayout.getId(), paramInt));
    }
  }
}
