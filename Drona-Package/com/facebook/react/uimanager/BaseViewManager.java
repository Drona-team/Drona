package com.facebook.react.uimanager;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.facebook.common.logging.FLog;
import com.facebook.react.R.id;
import com.facebook.react.R.string;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import com.facebook.react.uimanager.util.ReactFindViewUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseViewManager<T extends View, C extends LayoutShadowNode>
  extends ViewManager<T, C>
{
  private static final float CAMERA_DISTANCE_NORMALIZATION_MULTIPLIER = (float)Math.sqrt(5.0D);
  private static final int PERSPECTIVE_ARRAY_INVERTED_CAMERA_DISTANCE_INDEX = 2;
  private static final String STATE_BUSY = "busy";
  private static final String STATE_CHECKED = "checked";
  private static final String STATE_EXPANDED = "expanded";
  private static final String STATE_MIXED = "mixed";
  private static MatrixMathHelper.MatrixDecompositionContext sMatrixDecompositionContext = new MatrixMathHelper.MatrixDecompositionContext();
  public static final Map<String, Integer> sStateDescription;
  private static double[] sTransformDecompositionArray = new double[16];
  
  static
  {
    sStateDescription = new HashMap();
    sStateDescription.put("busy", Integer.valueOf(R.string.state_busy_description));
    sStateDescription.put("expanded", Integer.valueOf(R.string.state_expanded_description));
    sStateDescription.put("collapsed", Integer.valueOf(R.string.state_collapsed_description));
  }
  
  public BaseViewManager() {}
  
  private void logUnsupportedPropertyWarning(String paramString)
  {
    FLog.w("ReactNative", "%s doesn't support property '%s'", new Object[] { getName(), paramString });
  }
  
  private static void resetTransformProperty(View paramView)
  {
    paramView.setTranslationX(PixelUtil.toPixelFromDIP(0.0F));
    paramView.setTranslationY(PixelUtil.toPixelFromDIP(0.0F));
    paramView.setRotation(0.0F);
    paramView.setRotationX(0.0F);
    paramView.setRotationY(0.0F);
    paramView.setScaleX(1.0F);
    paramView.setScaleY(1.0F);
    paramView.setCameraDistance(0.0F);
  }
  
  private static void setTransformProperty(View paramView, ReadableArray paramReadableArray)
  {
    TransformHelper.processTransform(paramReadableArray, sTransformDecompositionArray);
    MatrixMathHelper.decomposeMatrix(sTransformDecompositionArray, sMatrixDecompositionContext);
    paramView.setTranslationX(PixelUtil.toPixelFromDIP((float)sMatrixDecompositionContexttranslation[0]));
    paramView.setTranslationY(PixelUtil.toPixelFromDIP((float)sMatrixDecompositionContexttranslation[1]));
    paramView.setRotation((float)sMatrixDecompositionContextrotationDegrees[2]);
    paramView.setRotationX((float)sMatrixDecompositionContextrotationDegrees[0]);
    paramView.setRotationY((float)sMatrixDecompositionContextrotationDegrees[1]);
    paramView.setScaleX((float)sMatrixDecompositionContextscale[0]);
    paramView.setScaleY((float)sMatrixDecompositionContextscale[1]);
    paramReadableArray = sMatrixDecompositionContextperspective;
    if (paramReadableArray.length > 2)
    {
      float f2 = (float)paramReadableArray[2];
      float f1 = f2;
      if (f2 == 0.0F) {
        f1 = 7.8125E-4F;
      }
      f1 = -1.0F / f1;
      f2 = getScreenDisplayMetricsdensity;
      paramView.setCameraDistance(f2 * f2 * f1 * CAMERA_DISTANCE_NORMALIZATION_MULTIPLIER);
    }
  }
  
  private void updateViewAccessibility(View paramView)
  {
    ReactAccessibilityDelegate.setDelegate(paramView);
  }
  
  private void updateViewContentDescription(View paramView)
  {
    Object localObject2 = (String)paramView.getTag(R.id.accessibility_label);
    Object localObject1 = (ReadableArray)paramView.getTag(R.id.accessibility_states);
    ReadableMap localReadableMap = (ReadableMap)paramView.getTag(R.id.accessibility_state);
    String str = (String)paramView.getTag(R.id.accessibility_hint);
    ArrayList localArrayList = new ArrayList();
    if (localObject2 != null) {
      localArrayList.add(localObject2);
    }
    int i;
    if (localObject1 != null)
    {
      i = 0;
      while (i < ((ReadableArray)localObject1).size())
      {
        localObject2 = ((ReadableArray)localObject1).getString(i);
        if (sStateDescription.containsKey(localObject2)) {
          localArrayList.add(paramView.getContext().getString(((Integer)sStateDescription.get(localObject2)).intValue()));
        }
        i += 1;
      }
    }
    if (localReadableMap != null)
    {
      localObject1 = localReadableMap.keySetIterator();
      while (((ReadableMapKeySetIterator)localObject1).hasNextKey())
      {
        Object localObject3 = ((ReadableMapKeySetIterator)localObject1).nextKey();
        localObject2 = localReadableMap.getDynamic((String)localObject3);
        if ((((String)localObject3).equals("checked")) && (((Dynamic)localObject2).getType() == ReadableType.String) && (((Dynamic)localObject2).asString().equals("mixed")))
        {
          localArrayList.add(paramView.getContext().getString(R.string.state_mixed_description));
        }
        else if ((((String)localObject3).equals("busy")) && (((Dynamic)localObject2).getType() == ReadableType.Boolean) && (((Dynamic)localObject2).asBoolean()))
        {
          localArrayList.add(paramView.getContext().getString(R.string.state_busy_description));
        }
        else if ((((String)localObject3).equals("expanded")) && (((Dynamic)localObject2).getType() == ReadableType.Boolean))
        {
          localObject3 = paramView.getContext();
          if (((Dynamic)localObject2).asBoolean()) {
            i = R.string.state_expanded_description;
          } else {
            i = R.string.state_collapsed_description;
          }
          localArrayList.add(((Context)localObject3).getString(i));
        }
      }
    }
    if (str != null) {
      localArrayList.add(str);
    }
    if (localArrayList.size() > 0) {
      paramView.setContentDescription(TextUtils.join(", ", localArrayList));
    }
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.builder().put("topAccessibilityAction", MapBuilder.get("registrationName", "onAccessibilityAction")).build();
  }
  
  protected void onAfterUpdateTransaction(View paramView)
  {
    super.onAfterUpdateTransaction(paramView);
    updateViewAccessibility(paramView);
  }
  
  public void setAccessibilityActions(View paramView, ReadableArray paramReadableArray)
  {
    if (paramReadableArray == null) {
      return;
    }
    paramView.setTag(R.id.accessibility_actions, paramReadableArray);
  }
  
  public void setAccessibilityHint(View paramView, String paramString)
  {
    paramView.setTag(R.id.accessibility_hint, paramString);
    updateViewContentDescription(paramView);
  }
  
  public void setAccessibilityLabel(View paramView, String paramString)
  {
    paramView.setTag(R.id.accessibility_label, paramString);
    updateViewContentDescription(paramView);
  }
  
  public void setAccessibilityLiveRegion(View paramView, String paramString)
  {
    if ((paramString != null) && (!paramString.equals("none")))
    {
      if (paramString.equals("polite"))
      {
        ViewCompat.setAccessibilityLiveRegion(paramView, 1);
        return;
      }
      if (paramString.equals("assertive")) {
        ViewCompat.setAccessibilityLiveRegion(paramView, 2);
      }
    }
    else
    {
      ViewCompat.setAccessibilityLiveRegion(paramView, 0);
    }
  }
  
  public void setAccessibilityRole(View paramView, String paramString)
  {
    if (paramString == null) {
      return;
    }
    paramView.setTag(R.id.accessibility_role, ReactAccessibilityDelegate.AccessibilityRole.fromValue(paramString));
  }
  
  public void setBackgroundColor(View paramView, int paramInt)
  {
    paramView.setBackgroundColor(paramInt);
  }
  
  protected void setBorderBottomLeftRadius(View paramView, float paramFloat)
  {
    logUnsupportedPropertyWarning("borderBottomLeftRadius");
  }
  
  protected void setBorderBottomRightRadius(View paramView, float paramFloat)
  {
    logUnsupportedPropertyWarning("borderBottomRightRadius");
  }
  
  protected void setBorderRadius(View paramView, float paramFloat)
  {
    logUnsupportedPropertyWarning("borderRadius");
  }
  
  protected void setBorderTopLeftRadius(View paramView, float paramFloat)
  {
    logUnsupportedPropertyWarning("borderTopLeftRadius");
  }
  
  protected void setBorderTopRightRadius(View paramView, float paramFloat)
  {
    logUnsupportedPropertyWarning("borderTopRightRadius");
  }
  
  public void setElevation(View paramView, float paramFloat)
  {
    ViewCompat.setElevation(paramView, PixelUtil.toPixelFromDIP(paramFloat));
  }
  
  public void setImportantForAccessibility(View paramView, String paramString)
  {
    if ((paramString != null) && (!paramString.equals("auto")))
    {
      if (paramString.equals("yes"))
      {
        ViewCompat.setImportantForAccessibility(paramView, 1);
        return;
      }
      if (paramString.equals("no"))
      {
        ViewCompat.setImportantForAccessibility(paramView, 2);
        return;
      }
      if (paramString.equals("no-hide-descendants")) {
        ViewCompat.setImportantForAccessibility(paramView, 4);
      }
    }
    else
    {
      ViewCompat.setImportantForAccessibility(paramView, 0);
    }
  }
  
  public void setNativeId(View paramView, String paramString)
  {
    paramView.setTag(R.id.view_tag_native_id, paramString);
    ReactFindViewUtil.notifyViewRendered(paramView);
  }
  
  public void setOpacity(View paramView, float paramFloat)
  {
    paramView.setAlpha(paramFloat);
  }
  
  public void setRenderToHardwareTexture(View paramView, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 2;
    } else {
      i = 0;
    }
    paramView.setLayerType(i, null);
  }
  
  public void setRotation(View paramView, float paramFloat)
  {
    paramView.setRotation(paramFloat);
  }
  
  public void setScaleX(View paramView, float paramFloat)
  {
    paramView.setScaleX(paramFloat);
  }
  
  public void setScaleY(View paramView, float paramFloat)
  {
    paramView.setScaleY(paramFloat);
  }
  
  public void setTestId(View paramView, String paramString)
  {
    paramView.setTag(R.id.react_test_id, paramString);
    paramView.setTag(paramString);
  }
  
  public void setTransform(View paramView, ReadableArray paramReadableArray)
  {
    if (paramReadableArray == null)
    {
      resetTransformProperty(paramView);
      return;
    }
    setTransformProperty(paramView, paramReadableArray);
  }
  
  public void setTranslateX(View paramView, float paramFloat)
  {
    paramView.setTranslationX(PixelUtil.toPixelFromDIP(paramFloat));
  }
  
  public void setTranslateY(View paramView, float paramFloat)
  {
    paramView.setTranslationY(PixelUtil.toPixelFromDIP(paramFloat));
  }
  
  public void setViewState(View paramView, ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null) {
      return;
    }
    paramView.setTag(R.id.accessibility_state, paramReadableMap);
    paramView.setSelected(false);
    paramView.setEnabled(true);
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    while (localReadableMapKeySetIterator.hasNextKey())
    {
      String str = localReadableMapKeySetIterator.nextKey();
      if ((str.equals("busy")) || (str.equals("expanded")) || ((str.equals("checked")) && (paramReadableMap.getType("checked") == ReadableType.String))) {
        updateViewContentDescription(paramView);
      }
    }
  }
  
  public void setViewStates(View paramView, ReadableArray paramReadableArray)
  {
    int i;
    if ((paramView.getTag(R.id.accessibility_states) != null) && (paramReadableArray == null)) {
      i = 1;
    } else {
      i = 0;
    }
    paramView.setTag(R.id.accessibility_states, paramReadableArray);
    paramView.setSelected(false);
    paramView.setEnabled(true);
    int k = i;
    if (paramReadableArray != null)
    {
      int j = 0;
      for (;;)
      {
        k = i;
        if (j >= paramReadableArray.size()) {
          break;
        }
        String str = paramReadableArray.getString(j);
        if (sStateDescription.containsKey(str)) {
          i = 1;
        }
        if ("selected".equals(str)) {
          paramView.setSelected(true);
        } else if ("disabled".equals(str)) {
          paramView.setEnabled(false);
        }
        j += 1;
      }
    }
    if (k != 0) {
      updateViewContentDescription(paramView);
    }
  }
  
  public void setZIndex(View paramView, float paramFloat)
  {
    ViewGroupManager.setViewZIndex(paramView, Math.round(paramFloat));
    paramView = paramView.getParent();
    if ((paramView instanceof ReactZIndexedViewGroup)) {
      ((ReactZIndexedViewGroup)paramView).updateDrawingOrder();
    }
  }
}
