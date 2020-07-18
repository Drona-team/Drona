package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public abstract class BaseViewManagerDelegate<T extends View, U extends BaseViewManager<T, ? extends LayoutShadowNode>>
  implements ViewManagerDelegate<T>
{
  protected final U mViewManager;
  
  public BaseViewManagerDelegate(BaseViewManager paramBaseViewManager)
  {
    mViewManager = paramBaseViewManager;
  }
  
  public void setProperty(View paramView, String paramString, Object paramObject)
  {
    int i = paramString.hashCode();
    int j = 0;
    boolean bool = false;
    switch (i)
    {
    default: 
      break;
    case 2045685618: 
      if (paramString.equals("nativeID")) {
        i = 15;
      }
      break;
    case 1505602511: 
      if (paramString.equals("accessibilityActions")) {
        i = 0;
      }
      break;
    case 1410320624: 
      if (paramString.equals("accessibilityStates")) {
        i = 6;
      }
      break;
    case 1349188574: 
      if (paramString.equals("borderRadius")) {
        i = 8;
      }
      break;
    case 1287124693: 
      if (paramString.equals("backgroundColor")) {
        i = 7;
      }
      break;
    case 1153872867: 
      if (paramString.equals("accessibilityState")) {
        i = 5;
      }
      break;
    case 1146842694: 
      if (paramString.equals("accessibilityLabel")) {
        i = 2;
      }
      break;
    case 1052666732: 
      if (paramString.equals("transform")) {
        i = 22;
      }
      break;
    case 746986311: 
      if (paramString.equals("importantForAccessibility")) {
        i = 14;
      }
      break;
    case 588239831: 
      if (paramString.equals("borderBottomRightRadius")) {
        i = 10;
      }
      break;
    case 581268560: 
      if (paramString.equals("borderBottomLeftRadius")) {
        i = 9;
      }
      break;
    case 333432965: 
      if (paramString.equals("borderTopRightRadius")) {
        i = 12;
      }
      break;
    case 36255470: 
      if (paramString.equals("accessibilityLiveRegion")) {
        i = 3;
      }
      break;
    case -4379043: 
      if (paramString.equals("elevation")) {
        i = 13;
      }
      break;
    case -40300674: 
      if (paramString.equals("rotation")) {
        i = 18;
      }
      break;
    case -80891667: 
      if (paramString.equals("renderToHardwareTextureAndroid")) {
        i = 17;
      }
      break;
    case -101359900: 
      if (paramString.equals("accessibilityRole")) {
        i = 4;
      }
      break;
    case -101663499: 
      if (paramString.equals("accessibilityHint")) {
        i = 1;
      }
      break;
    case -731417480: 
      if (paramString.equals("zIndex")) {
        i = 25;
      }
      break;
    case -877170387: 
      if (paramString.equals("testID")) {
        i = 21;
      }
      break;
    case -908189617: 
      if (paramString.equals("scaleY")) {
        i = 20;
      }
      break;
    case -908189618: 
      if (paramString.equals("scaleX")) {
        i = 19;
      }
      break;
    case -1228066334: 
      if (paramString.equals("borderTopLeftRadius")) {
        i = 11;
      }
      break;
    case -1267206133: 
      if (paramString.equals("opacity")) {
        i = 16;
      }
      break;
    case -1721943861: 
      if (paramString.equals("translateY")) {
        i = 24;
      }
      break;
    case -1721943862: 
      if (paramString.equals("translateX")) {
        i = 23;
      }
      break;
    }
    i = -1;
    float f2 = 1.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    float f7 = 0.0F;
    float f3 = 0.0F;
    float f1 = NaN.0F;
    switch (i)
    {
    default: 
      return;
    case 25: 
      paramString = mViewManager;
      if (paramObject == null) {
        f1 = f3;
      } else {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setZIndex(paramView, f1);
      return;
    case 24: 
      paramString = mViewManager;
      if (paramObject == null) {
        f1 = f4;
      } else {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setTranslateY(paramView, f1);
      return;
    case 23: 
      paramString = mViewManager;
      if (paramObject == null) {
        f1 = f5;
      } else {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setTranslateX(paramView, f1);
      return;
    case 22: 
      mViewManager.setTransform(paramView, (ReadableArray)paramObject);
      return;
    case 21: 
      mViewManager.setTestId(paramView, (String)paramObject);
      return;
    case 20: 
      paramString = mViewManager;
      if (paramObject != null) {
        f2 = ((Double)paramObject).floatValue();
      }
      paramString.setScaleY(paramView, f2);
      return;
    case 19: 
      paramString = mViewManager;
      if (paramObject != null) {
        f2 = ((Double)paramObject).floatValue();
      }
      paramString.setScaleX(paramView, f2);
      return;
    case 18: 
      paramString = mViewManager;
      if (paramObject == null) {
        f1 = f6;
      } else {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setRotation(paramView, f1);
      return;
    case 17: 
      paramString = mViewManager;
      if (paramObject != null) {
        bool = ((Boolean)paramObject).booleanValue();
      }
      paramString.setRenderToHardwareTexture(paramView, bool);
      return;
    case 16: 
      paramString = mViewManager;
      if (paramObject != null) {
        f2 = ((Double)paramObject).floatValue();
      }
      paramString.setOpacity(paramView, f2);
      return;
    case 15: 
      mViewManager.setNativeId(paramView, (String)paramObject);
      return;
    case 14: 
      mViewManager.setImportantForAccessibility(paramView, (String)paramObject);
      return;
    case 13: 
      paramString = mViewManager;
      if (paramObject == null) {
        f1 = f7;
      } else {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setElevation(paramView, f1);
      return;
    case 12: 
      paramString = mViewManager;
      if (paramObject != null) {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setBorderTopRightRadius(paramView, f1);
      return;
    case 11: 
      paramString = mViewManager;
      if (paramObject != null) {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setBorderTopLeftRadius(paramView, f1);
      return;
    case 10: 
      paramString = mViewManager;
      if (paramObject != null) {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setBorderBottomRightRadius(paramView, f1);
      return;
    case 9: 
      paramString = mViewManager;
      if (paramObject != null) {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setBorderBottomLeftRadius(paramView, f1);
      return;
    case 8: 
      paramString = mViewManager;
      if (paramObject != null) {
        f1 = ((Double)paramObject).floatValue();
      }
      paramString.setBorderRadius(paramView, f1);
      return;
    case 7: 
      paramString = mViewManager;
      if (paramObject == null) {
        i = j;
      } else {
        i = ((Double)paramObject).intValue();
      }
      paramString.setBackgroundColor(paramView, i);
      return;
    case 6: 
      mViewManager.setViewStates(paramView, (ReadableArray)paramObject);
      return;
    case 5: 
      mViewManager.setViewState(paramView, (ReadableMap)paramObject);
      return;
    case 4: 
      mViewManager.setAccessibilityRole(paramView, (String)paramObject);
      return;
    case 3: 
      mViewManager.setAccessibilityLiveRegion(paramView, (String)paramObject);
      return;
    case 2: 
      mViewManager.setAccessibilityLabel(paramView, (String)paramObject);
      return;
    case 1: 
      mViewManager.setAccessibilityHint(paramView, (String)paramObject);
      return;
    }
    mViewManager.setAccessibilityActions(paramView, (ReadableArray)paramObject);
  }
}
