package com.facebook.react.uimanager;

import android.content.Context;
import android.view.View;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.touch.ReactInterceptingViewGroup;
import com.facebook.react.uimanager.annotations.ReactPropertyHolder;
import com.facebook.yoga.YogaMeasureMode;
import java.util.Map;

@ReactPropertyHolder
public abstract class ViewManager<T extends View, C extends ReactShadowNode>
  extends BaseJavaModule
{
  public ViewManager() {}
  
  private final View createView(ThemedReactContext paramThemedReactContext, JSResponderHandler paramJSResponderHandler)
  {
    return createView(paramThemedReactContext, null, null, paramJSResponderHandler);
  }
  
  protected void addEventEmitters(ThemedReactContext paramThemedReactContext, View paramView) {}
  
  public ReactShadowNode createShadowNodeInstance()
  {
    throw new RuntimeException("ViewManager subclasses must implement createShadowNodeInstance()");
  }
  
  public ReactShadowNode createShadowNodeInstance(ReactApplicationContext paramReactApplicationContext)
  {
    return createShadowNodeInstance();
  }
  
  public View createView(ThemedReactContext paramThemedReactContext, ReactStylesDiffMap paramReactStylesDiffMap, StateWrapper paramStateWrapper, JSResponderHandler paramJSResponderHandler)
  {
    paramReactStylesDiffMap = createViewInstance(paramThemedReactContext, paramReactStylesDiffMap, paramStateWrapper);
    addEventEmitters(paramThemedReactContext, paramReactStylesDiffMap);
    if ((paramReactStylesDiffMap instanceof ReactInterceptingViewGroup)) {
      ((ReactInterceptingViewGroup)paramReactStylesDiffMap).setOnInterceptTouchEventListener(paramJSResponderHandler);
    }
    return paramReactStylesDiffMap;
  }
  
  protected abstract View createViewInstance(ThemedReactContext paramThemedReactContext);
  
  protected View createViewInstance(ThemedReactContext paramThemedReactContext, ReactStylesDiffMap paramReactStylesDiffMap, StateWrapper paramStateWrapper)
  {
    paramThemedReactContext = createViewInstance(paramThemedReactContext);
    if (paramReactStylesDiffMap != null) {
      updateProperties(paramThemedReactContext, paramReactStylesDiffMap);
    }
    if (paramStateWrapper != null)
    {
      paramReactStylesDiffMap = updateState(paramThemedReactContext, paramReactStylesDiffMap, paramStateWrapper);
      if (paramReactStylesDiffMap != null) {
        updateExtraData(paramThemedReactContext, paramReactStylesDiffMap);
      }
    }
    return paramThemedReactContext;
  }
  
  public Map getCommandsMap()
  {
    return null;
  }
  
  protected ViewManagerDelegate getDelegate()
  {
    return null;
  }
  
  public Map getExportedCustomBubblingEventTypeConstants()
  {
    return null;
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return null;
  }
  
  public Map getExportedViewConstants()
  {
    return null;
  }
  
  public abstract String getName();
  
  public Map getNativeProps()
  {
    return ViewManagerPropertyUpdater.getNativeProps(getClass(), getShadowNodeClass());
  }
  
  public abstract Class getShadowNodeClass();
  
  public long measure(Context paramContext, ReadableMap paramReadableMap1, ReadableMap paramReadableMap2, ReadableMap paramReadableMap3, float paramFloat1, YogaMeasureMode paramYogaMeasureMode1, float paramFloat2, YogaMeasureMode paramYogaMeasureMode2)
  {
    return 0L;
  }
  
  protected void onAfterUpdateTransaction(View paramView) {}
  
  public void onDropViewInstance(View paramView) {}
  
  public void receiveCommand(View paramView, int paramInt, ReadableArray paramReadableArray) {}
  
  public void receiveCommand(View paramView, String paramString, ReadableArray paramReadableArray) {}
  
  public abstract void updateExtraData(View paramView, Object paramObject);
  
  public Object updateLocalData(View paramView, ReactStylesDiffMap paramReactStylesDiffMap1, ReactStylesDiffMap paramReactStylesDiffMap2)
  {
    return null;
  }
  
  public void updateProperties(View paramView, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    if (ReactFeatureFlags.useViewManagerDelegates)
    {
      ViewManagerDelegate localViewManagerDelegate = getDelegate();
      if (localViewManagerDelegate != null)
      {
        ViewManagerPropertyUpdater.updateProps(localViewManagerDelegate, paramView, paramReactStylesDiffMap);
        break label30;
      }
    }
    ViewManagerPropertyUpdater.updateProps(this, paramView, paramReactStylesDiffMap);
    label30:
    onAfterUpdateTransaction(paramView);
  }
  
  public Object updateState(View paramView, ReactStylesDiffMap paramReactStylesDiffMap, StateWrapper paramStateWrapper)
  {
    return null;
  }
}
