package com.facebook.react.views.modal;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Point;
import android.view.View;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.events.EventDispatcher;
import java.util.Map;

@ReactModule(name="RCTModalHostView")
public class ReactModalHostManager
  extends ViewGroupManager<ReactModalHostView>
{
  public static final String REACT_CLASS = "RCTModalHostView";
  
  public ReactModalHostManager() {}
  
  protected void addEventEmitters(final ThemedReactContext paramThemedReactContext, final ReactModalHostView paramReactModalHostView)
  {
    paramThemedReactContext = ((UIManagerModule)paramThemedReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher();
    paramReactModalHostView.setOnRequestCloseListener(new ReactModalHostView.OnRequestCloseListener()
    {
      public void onRequestClose(DialogInterface paramAnonymousDialogInterface)
      {
        paramThemedReactContext.dispatchEvent(new RequestCloseEvent(paramReactModalHostView.getId()));
      }
    });
    paramReactModalHostView.setOnShowListener(new DialogInterface.OnShowListener()
    {
      public void onShow(DialogInterface paramAnonymousDialogInterface)
      {
        paramThemedReactContext.dispatchEvent(new ShowEvent(paramReactModalHostView.getId()));
      }
    });
  }
  
  public LayoutShadowNode createShadowNodeInstance()
  {
    return new ModalHostShadowNode();
  }
  
  protected ReactModalHostView createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactModalHostView(paramThemedReactContext);
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.builder().put("topRequestClose", MapBuilder.get("registrationName", "onRequestClose")).put("topShow", MapBuilder.get("registrationName", "onShow")).build();
  }
  
  public String getName()
  {
    return "RCTModalHostView";
  }
  
  public Class getShadowNodeClass()
  {
    return ModalHostShadowNode.class;
  }
  
  protected void onAfterUpdateTransaction(ReactModalHostView paramReactModalHostView)
  {
    super.onAfterUpdateTransaction(paramReactModalHostView);
    paramReactModalHostView.showOrUpdate();
  }
  
  public void onDropViewInstance(ReactModalHostView paramReactModalHostView)
  {
    super.onDropViewInstance(paramReactModalHostView);
    paramReactModalHostView.onDropInstance();
  }
  
  public void setAnimationType(ReactModalHostView paramReactModalHostView, String paramString)
  {
    paramReactModalHostView.setAnimationType(paramString);
  }
  
  public void setHardwareAccelerated(ReactModalHostView paramReactModalHostView, boolean paramBoolean)
  {
    paramReactModalHostView.setHardwareAccelerated(paramBoolean);
  }
  
  public void setTransparent(ReactModalHostView paramReactModalHostView, boolean paramBoolean)
  {
    paramReactModalHostView.setTransparent(paramBoolean);
  }
  
  public Object updateState(ReactModalHostView paramReactModalHostView, ReactStylesDiffMap paramReactStylesDiffMap, StateWrapper paramStateWrapper)
  {
    paramReactStylesDiffMap = ModalHostHelper.getModalHostSize(paramReactModalHostView.getContext());
    paramReactModalHostView.updateState(paramStateWrapper, x, y);
    return null;
  }
}
