package com.facebook.react.uimanager;

import android.widget.ImageView.ScaleType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import com.facebook.react.uimanager.events.TouchEventType;
import java.util.HashMap;
import java.util.Map;

class UIManagerModuleConstants
{
  public static final String ACTION_DISMISSED = "dismissed";
  public static final String ACTION_ITEM_SELECTED = "itemSelected";
  
  UIManagerModuleConstants() {}
  
  static Map getBubblingEventTypeConstants()
  {
    return MapBuilder.builder().put("topChange", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onChange", "captured", "onChangeCapture"))).put("topSelect", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onSelect", "captured", "onSelectCapture"))).put(TouchEventType.getJSEventName(TouchEventType.START), MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onTouchStart", "captured", "onTouchStartCapture"))).put(TouchEventType.getJSEventName(TouchEventType.MOVE), MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onTouchMove", "captured", "onTouchMoveCapture"))).put(TouchEventType.getJSEventName(TouchEventType.PENCIL), MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onTouchEnd", "captured", "onTouchEndCapture"))).put(TouchEventType.getJSEventName(TouchEventType.CANCEL), MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onTouchCancel", "captured", "onTouchCancelCapture"))).build();
  }
  
  public static Map getConstants()
  {
    HashMap localHashMap = MapBuilder.newHashMap();
    localHashMap.put("UIView", MapBuilder.get("ContentMode", MapBuilder.get("ScaleAspectFit", Integer.valueOf(ImageView.ScaleType.FIT_CENTER.ordinal()), "ScaleAspectFill", Integer.valueOf(ImageView.ScaleType.CENTER_CROP.ordinal()), "ScaleAspectCenter", Integer.valueOf(ImageView.ScaleType.CENTER_INSIDE.ordinal()))));
    localHashMap.put("StyleConstants", MapBuilder.get("PointerEventsValues", MapBuilder.get("none", Integer.valueOf(PointerEvents.NONE.ordinal()), "boxNone", Integer.valueOf(PointerEvents.BOX_NONE.ordinal()), "boxOnly", Integer.valueOf(PointerEvents.BOX_ONLY.ordinal()), "unspecified", Integer.valueOf(PointerEvents.AUTO.ordinal()))));
    localHashMap.put("PopupMenu", MapBuilder.get("dismissed", "dismissed", "itemSelected", "itemSelected"));
    localHashMap.put("AccessibilityEventTypes", MapBuilder.get("typeWindowStateChanged", Integer.valueOf(32), "typeViewFocused", Integer.valueOf(8), "typeViewClicked", Integer.valueOf(1)));
    return localHashMap;
  }
  
  static Map getDirectEventTypeConstants()
  {
    return MapBuilder.builder().put("topContentSizeChange", MapBuilder.get("registrationName", "onContentSizeChange")).put("topLayout", MapBuilder.get("registrationName", "onLayout")).put("topLoadingError", MapBuilder.get("registrationName", "onLoadingError")).put("topLoadingFinish", MapBuilder.get("registrationName", "onLoadingFinish")).put("topLoadingStart", MapBuilder.get("registrationName", "onLoadingStart")).put("topSelectionChange", MapBuilder.get("registrationName", "onSelectionChange")).put("topMessage", MapBuilder.get("registrationName", "onMessage")).put("topClick", MapBuilder.get("registrationName", "onClick")).put("topScrollBeginDrag", MapBuilder.get("registrationName", "onScrollBeginDrag")).put("topScrollEndDrag", MapBuilder.get("registrationName", "onScrollEndDrag")).put("topScroll", MapBuilder.get("registrationName", "onScroll")).put("topMomentumScrollBegin", MapBuilder.get("registrationName", "onMomentumScrollBegin")).put("topMomentumScrollEnd", MapBuilder.get("registrationName", "onMomentumScrollEnd")).build();
  }
}
