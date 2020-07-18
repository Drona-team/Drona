package com.facebook.react.uimanager;

import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.WeakHashMap;

public abstract class ViewGroupManager<T extends ViewGroup>
  extends BaseViewManager<T, LayoutShadowNode>
  implements IViewManagerWithChildren
{
  private static WeakHashMap<View, Integer> mZIndexHash = new WeakHashMap();
  
  public ViewGroupManager() {}
  
  public static Integer getViewZIndex(View paramView)
  {
    return (Integer)mZIndexHash.get(paramView);
  }
  
  public static void setViewZIndex(View paramView, int paramInt)
  {
    mZIndexHash.put(paramView, Integer.valueOf(paramInt));
  }
  
  public void addView(ViewGroup paramViewGroup, View paramView, int paramInt)
  {
    paramViewGroup.addView(paramView, paramInt);
  }
  
  public void addViews(ViewGroup paramViewGroup, List paramList)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      addView(paramViewGroup, (View)paramList.get(i), i);
      i += 1;
    }
  }
  
  public LayoutShadowNode createShadowNodeInstance()
  {
    return new LayoutShadowNode();
  }
  
  public View getChildAt(ViewGroup paramViewGroup, int paramInt)
  {
    return paramViewGroup.getChildAt(paramInt);
  }
  
  public int getChildCount(ViewGroup paramViewGroup)
  {
    return paramViewGroup.getChildCount();
  }
  
  public Class getShadowNodeClass()
  {
    return LayoutShadowNode.class;
  }
  
  public boolean needsCustomLayoutForChildren()
  {
    return false;
  }
  
  public void removeAllViews(ViewGroup paramViewGroup)
  {
    int i = getChildCount(paramViewGroup) - 1;
    while (i >= 0)
    {
      removeViewAt(paramViewGroup, i);
      i -= 1;
    }
  }
  
  public void removeView(ViewGroup paramViewGroup, View paramView)
  {
    int i = 0;
    while (i < getChildCount(paramViewGroup))
    {
      if (getChildAt(paramViewGroup, i) == paramView)
      {
        removeViewAt(paramViewGroup, i);
        return;
      }
      i += 1;
    }
  }
  
  public void removeViewAt(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup.removeViewAt(paramInt);
  }
  
  public boolean shouldPromoteGrandchildren()
  {
    return false;
  }
  
  public void updateExtraData(ViewGroup paramViewGroup, Object paramObject) {}
}
