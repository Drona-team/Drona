package com.facebook.react.views.view;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.PointerEvents;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.yoga.YogaConstants;
import java.util.Locale;
import java.util.Map;

@ReactModule(name="RCTView")
public class ReactViewManager
  extends ViewGroupManager<ReactViewGroup>
{
  private static final int CMD_HOTSPOT_UPDATE = 1;
  private static final int CMD_SET_PRESSED = 2;
  private static final String HOTSPOT_UPDATE_KEY = "hotspotUpdate";
  @VisibleForTesting
  public static final String REACT_CLASS = "RCTView";
  private static final int[] SPACING_TYPES = { 8, 0, 2, 1, 3, 4, 5 };
  
  public ReactViewManager() {}
  
  private void handleHotspotUpdate(ReactViewGroup paramReactViewGroup, ReadableArray paramReadableArray)
  {
    if ((paramReadableArray != null) && (paramReadableArray.size() == 2))
    {
      if (Build.VERSION.SDK_INT >= 21) {
        paramReactViewGroup.drawableHotspotChanged(PixelUtil.toPixelFromDIP(paramReadableArray.getDouble(0)), PixelUtil.toPixelFromDIP(paramReadableArray.getDouble(1)));
      }
    }
    else {
      throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'updateHotspot' command");
    }
  }
  
  private void handleSetPressed(ReactViewGroup paramReactViewGroup, ReadableArray paramReadableArray)
  {
    if ((paramReadableArray != null) && (paramReadableArray.size() == 1))
    {
      paramReactViewGroup.setPressed(paramReadableArray.getBoolean(0));
      return;
    }
    throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'setPressed' command");
  }
  
  public void addView(ReactViewGroup paramReactViewGroup, View paramView, int paramInt)
  {
    if (paramReactViewGroup.getRemoveClippedSubviews())
    {
      paramReactViewGroup.addViewWithSubviewClippingEnabled(paramView, paramInt);
      return;
    }
    paramReactViewGroup.addView(paramView, paramInt);
  }
  
  public ReactViewGroup createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactViewGroup(paramThemedReactContext);
  }
  
  public View getChildAt(ReactViewGroup paramReactViewGroup, int paramInt)
  {
    if (paramReactViewGroup.getRemoveClippedSubviews()) {
      return paramReactViewGroup.getChildAtWithSubviewClippingEnabled(paramInt);
    }
    return paramReactViewGroup.getChildAt(paramInt);
  }
  
  public int getChildCount(ReactViewGroup paramReactViewGroup)
  {
    if (paramReactViewGroup.getRemoveClippedSubviews()) {
      return paramReactViewGroup.getAllChildrenCount();
    }
    return paramReactViewGroup.getChildCount();
  }
  
  public Map getCommandsMap()
  {
    return MapBuilder.get("hotspotUpdate", Integer.valueOf(1), "setPressed", Integer.valueOf(2));
  }
  
  public String getName()
  {
    return "RCTView";
  }
  
  public void nextFocusDown(ReactViewGroup paramReactViewGroup, int paramInt)
  {
    paramReactViewGroup.setNextFocusDownId(paramInt);
  }
  
  public void nextFocusForward(ReactViewGroup paramReactViewGroup, int paramInt)
  {
    paramReactViewGroup.setNextFocusForwardId(paramInt);
  }
  
  public void nextFocusLeft(ReactViewGroup paramReactViewGroup, int paramInt)
  {
    paramReactViewGroup.setNextFocusLeftId(paramInt);
  }
  
  public void nextFocusRight(ReactViewGroup paramReactViewGroup, int paramInt)
  {
    paramReactViewGroup.setNextFocusRightId(paramInt);
  }
  
  public void nextFocusUp(ReactViewGroup paramReactViewGroup, int paramInt)
  {
    paramReactViewGroup.setNextFocusUpId(paramInt);
  }
  
  public void receiveCommand(ReactViewGroup paramReactViewGroup, int paramInt, ReadableArray paramReadableArray)
  {
    switch (paramInt)
    {
    default: 
      return;
      return;
    case 2: 
      handleSetPressed(paramReactViewGroup, paramReadableArray);
      return;
    }
    handleHotspotUpdate(paramReactViewGroup, paramReadableArray);
  }
  
  public void receiveCommand(ReactViewGroup paramReactViewGroup, String paramString, ReadableArray paramReadableArray)
  {
    int i = paramString.hashCode();
    if (i != -1639565984)
    {
      if ((i == -399823752) && (paramString.equals("hotspotUpdate")))
      {
        i = 0;
        break label56;
      }
    }
    else if (paramString.equals("setPressed"))
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
      handleSetPressed(paramReactViewGroup, paramReadableArray);
      return;
    }
    handleHotspotUpdate(paramReactViewGroup, paramReadableArray);
  }
  
  public void removeAllViews(ReactViewGroup paramReactViewGroup)
  {
    if (paramReactViewGroup.getRemoveClippedSubviews())
    {
      paramReactViewGroup.removeAllViewsWithSubviewClippingEnabled();
      return;
    }
    paramReactViewGroup.removeAllViews();
  }
  
  public void removeViewAt(ReactViewGroup paramReactViewGroup, int paramInt)
  {
    if (paramReactViewGroup.getRemoveClippedSubviews())
    {
      View localView = getChildAt(paramReactViewGroup, paramInt);
      if (localView.getParent() != null) {
        paramReactViewGroup.removeView(localView);
      }
      paramReactViewGroup.removeViewWithSubviewClippingEnabled(localView);
      return;
    }
    paramReactViewGroup.removeViewAt(paramInt);
  }
  
  public void setAccessible(ReactViewGroup paramReactViewGroup, boolean paramBoolean)
  {
    paramReactViewGroup.setFocusable(paramBoolean);
  }
  
  public void setBackfaceVisibility(ReactViewGroup paramReactViewGroup, String paramString)
  {
    paramReactViewGroup.setBackfaceVisibility(paramString);
  }
  
  public void setBorderColor(ReactViewGroup paramReactViewGroup, int paramInt, Integer paramInteger)
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
    paramReactViewGroup.setBorderColor(SPACING_TYPES[paramInt], f1, f2);
  }
  
  public void setBorderRadius(ReactViewGroup paramReactViewGroup, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat))
    {
      f = paramFloat;
      if (paramFloat < 0.0F) {
        f = NaN.0F;
      }
    }
    paramFloat = f;
    if (!YogaConstants.isUndefined(f)) {
      paramFloat = PixelUtil.toPixelFromDIP(f);
    }
    if (paramInt == 0)
    {
      paramReactViewGroup.setBorderRadius(paramFloat);
      return;
    }
    paramReactViewGroup.setBorderRadius(paramFloat, paramInt - 1);
  }
  
  public void setBorderStyle(ReactViewGroup paramReactViewGroup, String paramString)
  {
    paramReactViewGroup.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(ReactViewGroup paramReactViewGroup, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat))
    {
      f = paramFloat;
      if (paramFloat < 0.0F) {
        f = NaN.0F;
      }
    }
    paramFloat = f;
    if (!YogaConstants.isUndefined(f)) {
      paramFloat = PixelUtil.toPixelFromDIP(f);
    }
    paramReactViewGroup.setBorderWidth(SPACING_TYPES[paramInt], paramFloat);
  }
  
  public void setCollapsable(ReactViewGroup paramReactViewGroup, boolean paramBoolean) {}
  
  public void setFocusable(final ReactViewGroup paramReactViewGroup, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramReactViewGroup.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ((UIManagerModule)((ReactContext)paramReactViewGroup.getContext()).getNativeModule(UIManagerModule.class)).getEventDispatcher().dispatchEvent(new ViewGroupClickEvent(paramReactViewGroup.getId()));
        }
      });
      paramReactViewGroup.setFocusable(true);
      return;
    }
    paramReactViewGroup.setOnClickListener(null);
    paramReactViewGroup.setClickable(false);
  }
  
  public void setHitSlop(ReactViewGroup paramReactViewGroup, ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null)
    {
      paramReactViewGroup.setHitSlopRect(null);
      return;
    }
    boolean bool = paramReadableMap.hasKey("left");
    int m = 0;
    int i;
    if (bool) {
      i = (int)PixelUtil.toPixelFromDIP(paramReadableMap.getDouble("left"));
    } else {
      i = 0;
    }
    int j;
    if (paramReadableMap.hasKey("top")) {
      j = (int)PixelUtil.toPixelFromDIP(paramReadableMap.getDouble("top"));
    } else {
      j = 0;
    }
    int k;
    if (paramReadableMap.hasKey("right")) {
      k = (int)PixelUtil.toPixelFromDIP(paramReadableMap.getDouble("right"));
    } else {
      k = 0;
    }
    if (paramReadableMap.hasKey("bottom")) {
      m = (int)PixelUtil.toPixelFromDIP(paramReadableMap.getDouble("bottom"));
    }
    paramReactViewGroup.setHitSlopRect(new Rect(i, j, k, m));
  }
  
  public void setNativeBackground(ReactViewGroup paramReactViewGroup, ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null) {
      paramReadableMap = null;
    } else {
      paramReadableMap = ReactDrawableHelper.createDrawableFromJSDescription(paramReactViewGroup.getContext(), paramReadableMap);
    }
    paramReactViewGroup.setTranslucentBackgroundDrawable(paramReadableMap);
  }
  
  public void setNativeForeground(ReactViewGroup paramReactViewGroup, ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null) {
      paramReadableMap = null;
    } else {
      paramReadableMap = ReactDrawableHelper.createDrawableFromJSDescription(paramReactViewGroup.getContext(), paramReadableMap);
    }
    paramReactViewGroup.setForeground(paramReadableMap);
  }
  
  public void setNeedsOffscreenAlphaCompositing(ReactViewGroup paramReactViewGroup, boolean paramBoolean)
  {
    paramReactViewGroup.setNeedsOffscreenAlphaCompositing(paramBoolean);
  }
  
  public void setOpacity(ReactViewGroup paramReactViewGroup, float paramFloat)
  {
    paramReactViewGroup.setOpacityIfPossible(paramFloat);
  }
  
  public void setOverflow(ReactViewGroup paramReactViewGroup, String paramString)
  {
    paramReactViewGroup.setOverflow(paramString);
  }
  
  public void setPointerEvents(ReactViewGroup paramReactViewGroup, String paramString)
  {
    if (paramString == null)
    {
      paramReactViewGroup.setPointerEvents(PointerEvents.AUTO);
      return;
    }
    paramReactViewGroup.setPointerEvents(PointerEvents.valueOf(paramString.toUpperCase(Locale.US).replace("-", "_")));
  }
  
  public void setRemoveClippedSubviews(ReactViewGroup paramReactViewGroup, boolean paramBoolean)
  {
    paramReactViewGroup.setRemoveClippedSubviews(paramBoolean);
  }
  
  public void setTVPreferredFocus(ReactViewGroup paramReactViewGroup, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramReactViewGroup.setFocusable(true);
      paramReactViewGroup.setFocusableInTouchMode(true);
      paramReactViewGroup.requestFocus();
    }
  }
  
  public void setTransform(ReactViewGroup paramReactViewGroup, ReadableArray paramReadableArray)
  {
    super.setTransform(paramReactViewGroup, paramReadableArray);
    paramReactViewGroup.setBackfaceVisibilityDependantOpacity();
  }
}
