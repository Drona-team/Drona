package com.facebook.react.uimanager;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewGroupDrawingOrderHelper
{
  @Nullable
  private int[] mDrawingOrderIndices;
  private int mNumberOfChildrenWithZIndex = 0;
  private final ViewGroup mViewGroup;
  
  public ViewGroupDrawingOrderHelper(ViewGroup paramViewGroup)
  {
    mViewGroup = paramViewGroup;
  }
  
  public int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (mDrawingOrderIndices == null)
    {
      ArrayList localArrayList = new ArrayList();
      int j = 0;
      int i = 0;
      while (i < paramInt1)
      {
        localArrayList.add(mViewGroup.getChildAt(i));
        i += 1;
      }
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(View paramAnonymousView1, View paramAnonymousView2)
        {
          Integer localInteger = ViewGroupManager.getViewZIndex(paramAnonymousView1);
          paramAnonymousView1 = localInteger;
          if (localInteger == null) {
            paramAnonymousView1 = Integer.valueOf(0);
          }
          localInteger = ViewGroupManager.getViewZIndex(paramAnonymousView2);
          paramAnonymousView2 = localInteger;
          if (localInteger == null) {
            paramAnonymousView2 = Integer.valueOf(0);
          }
          return paramAnonymousView1.intValue() - paramAnonymousView2.intValue();
        }
      });
      mDrawingOrderIndices = new int[paramInt1];
      i = j;
      while (i < paramInt1)
      {
        View localView = (View)localArrayList.get(i);
        mDrawingOrderIndices[i] = mViewGroup.indexOfChild(localView);
        i += 1;
      }
    }
    return mDrawingOrderIndices[paramInt2];
  }
  
  public void handleAddView(View paramView)
  {
    if (ViewGroupManager.getViewZIndex(paramView) != null) {
      mNumberOfChildrenWithZIndex += 1;
    }
    mDrawingOrderIndices = null;
  }
  
  public void handleRemoveView(View paramView)
  {
    if (ViewGroupManager.getViewZIndex(paramView) != null) {
      mNumberOfChildrenWithZIndex -= 1;
    }
    mDrawingOrderIndices = null;
  }
  
  public boolean shouldEnableCustomDrawingOrder()
  {
    return mNumberOfChildrenWithZIndex > 0;
  }
  
  public void update()
  {
    int i = 0;
    mNumberOfChildrenWithZIndex = 0;
    while (i < mViewGroup.getChildCount())
    {
      if (ViewGroupManager.getViewZIndex(mViewGroup.getChildAt(i)) != null) {
        mNumberOfChildrenWithZIndex += 1;
      }
      i += 1;
    }
    mDrawingOrderIndices = null;
  }
}
