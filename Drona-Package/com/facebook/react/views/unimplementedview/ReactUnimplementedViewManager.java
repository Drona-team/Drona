package com.facebook.react.views.unimplementedview;

import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

@ReactModule(name="UnimplementedNativeView")
public class ReactUnimplementedViewManager
  extends ViewGroupManager<ReactUnimplementedView>
{
  public static final String REACT_CLASS = "UnimplementedNativeView";
  
  public ReactUnimplementedViewManager() {}
  
  protected ReactUnimplementedView createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactUnimplementedView(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "UnimplementedNativeView";
  }
  
  public void setName(ReactUnimplementedView paramReactUnimplementedView, String paramString)
  {
    paramReactUnimplementedView.setName(paramString);
  }
}
