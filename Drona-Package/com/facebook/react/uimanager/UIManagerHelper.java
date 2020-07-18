package com.facebook.react.uimanager;

import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.JSIModuleType;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.uimanager.common.ViewUtil;

public class UIManagerHelper
{
  public UIManagerHelper() {}
  
  public static UIManager getUIManager(ReactContext paramReactContext, int paramInt)
  {
    paramReactContext = paramReactContext.getCatalystInstance();
    if (paramInt == 2) {
      return (UIManager)paramReactContext.getJSIModule(JSIModuleType.UIManager);
    }
    return (UIManager)paramReactContext.getNativeModule(UIManagerModule.class);
  }
  
  public static UIManager getUIManagerForReactTag(ReactContext paramReactContext, int paramInt)
  {
    return getUIManager(paramReactContext, ViewUtil.getUIManagerType(paramInt));
  }
}