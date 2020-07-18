package com.facebook.react.uimanager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import com.facebook.common.logging.FLog;
import com.facebook.react.R.id;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.uimanager.layoutanimation.LayoutAnimationController;
import com.facebook.react.uimanager.layoutanimation.LayoutAnimationListener;
import java.util.Arrays;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class NativeViewHierarchyManager
{
  private static final String appName = "NativeViewHierarchyManager";
  private final RectF mBoundingBox = new RectF();
  private final int[] mDroppedViewArray = new int[100];
  private int mDroppedViewIndex = 0;
  private final JSResponderHandler mJSResponderHandler = new JSResponderHandler();
  private boolean mLayoutAnimationEnabled;
  private final LayoutAnimationController mLayoutAnimator = new LayoutAnimationController();
  private PopupMenu mPopupMenu;
  private final SparseBooleanArray mRootTags;
  private final RootViewManager mRootViewManager;
  private final SparseArray<SparseIntArray> mTagsToPendingIndicesToDelete = new SparseArray();
  private final SparseArray<ViewManager> mTagsToViewManagers;
  private final SparseArray<View> mTagsToViews;
  private final ViewManagerRegistry mViewManagers;
  
  public NativeViewHierarchyManager(ViewManagerRegistry paramViewManagerRegistry)
  {
    this(paramViewManagerRegistry, new RootViewManager());
  }
  
  public NativeViewHierarchyManager(ViewManagerRegistry paramViewManagerRegistry, RootViewManager paramRootViewManager)
  {
    mViewManagers = paramViewManagerRegistry;
    mTagsToViews = new SparseArray();
    mTagsToViewManagers = new SparseArray();
    mRootTags = new SparseBooleanArray();
    mRootViewManager = paramRootViewManager;
  }
  
  private boolean arrayContains(int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfInt == null) {
      return false;
    }
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void cacheDroppedTag(int paramInt)
  {
    mDroppedViewArray[mDroppedViewIndex] = paramInt;
    mDroppedViewIndex = ((mDroppedViewIndex + 1) % 100);
  }
  
  private void computeBoundingBox(View paramView, int[] paramArrayOfInt)
  {
    mBoundingBox.set(0.0F, 0.0F, paramView.getWidth(), paramView.getHeight());
    mapRectFromViewToWindowCoords(paramView, mBoundingBox);
    paramArrayOfInt[0] = Math.round(mBoundingBox.left);
    paramArrayOfInt[1] = Math.round(mBoundingBox.top);
    paramArrayOfInt[2] = Math.round(mBoundingBox.right - mBoundingBox.left);
    paramArrayOfInt[3] = Math.round(mBoundingBox.bottom - mBoundingBox.top);
  }
  
  private static String constructManageChildrenErrorMessage(ViewGroup paramViewGroup, ViewGroupManager paramViewGroupManager, int[] paramArrayOfInt1, ViewAtIndex[] paramArrayOfViewAtIndex, int[] paramArrayOfInt2)
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    int i;
    int j;
    int k;
    if (paramViewGroup != null)
    {
      StringBuilder localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append("View tag:");
      localStringBuilder2.append(paramViewGroup.getId());
      localStringBuilder2.append("\n");
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append("  children(");
      localStringBuilder2.append(paramViewGroupManager.getChildCount(paramViewGroup));
      localStringBuilder2.append("): [\n");
      localStringBuilder1.append(localStringBuilder2.toString());
      i = 0;
      while (i < paramViewGroupManager.getChildCount(paramViewGroup))
      {
        j = 0;
        for (;;)
        {
          k = i + j;
          if ((k >= paramViewGroupManager.getChildCount(paramViewGroup)) || (j >= 16)) {
            break;
          }
          localStringBuilder2 = new StringBuilder();
          localStringBuilder2.append(paramViewGroupManager.getChildAt(paramViewGroup, k).getId());
          localStringBuilder2.append(",");
          localStringBuilder1.append(localStringBuilder2.toString());
          j += 1;
        }
        localStringBuilder1.append("\n");
        i += 16;
      }
      localStringBuilder1.append(" ],\n");
    }
    if (paramArrayOfInt1 != null)
    {
      paramViewGroup = new StringBuilder();
      paramViewGroup.append("  indicesToRemove(");
      paramViewGroup.append(paramArrayOfInt1.length);
      paramViewGroup.append("): [\n");
      localStringBuilder1.append(paramViewGroup.toString());
      i = 0;
      while (i < paramArrayOfInt1.length)
      {
        j = 0;
        for (;;)
        {
          k = i + j;
          if ((k >= paramArrayOfInt1.length) || (j >= 16)) {
            break;
          }
          paramViewGroup = new StringBuilder();
          paramViewGroup.append(paramArrayOfInt1[k]);
          paramViewGroup.append(",");
          localStringBuilder1.append(paramViewGroup.toString());
          j += 1;
        }
        localStringBuilder1.append("\n");
        i += 16;
      }
      localStringBuilder1.append(" ],\n");
    }
    if (paramArrayOfViewAtIndex != null)
    {
      paramViewGroup = new StringBuilder();
      paramViewGroup.append("  viewsToAdd(");
      paramViewGroup.append(paramArrayOfViewAtIndex.length);
      paramViewGroup.append("): [\n");
      localStringBuilder1.append(paramViewGroup.toString());
      i = 0;
      while (i < paramArrayOfViewAtIndex.length)
      {
        j = 0;
        for (;;)
        {
          k = i + j;
          if ((k >= paramArrayOfViewAtIndex.length) || (j >= 16)) {
            break;
          }
          paramViewGroup = new StringBuilder();
          paramViewGroup.append("[");
          paramViewGroup.append(mIndex);
          paramViewGroup.append(",");
          paramViewGroup.append(mTag);
          paramViewGroup.append("],");
          localStringBuilder1.append(paramViewGroup.toString());
          j += 1;
        }
        localStringBuilder1.append("\n");
        i += 16;
      }
      localStringBuilder1.append(" ],\n");
    }
    if (paramArrayOfInt2 != null)
    {
      paramViewGroup = new StringBuilder();
      paramViewGroup.append("  tagsToDelete(");
      paramViewGroup.append(paramArrayOfInt2.length);
      paramViewGroup.append("): [\n");
      localStringBuilder1.append(paramViewGroup.toString());
      i = 0;
      while (i < paramArrayOfInt2.length)
      {
        j = 0;
        for (;;)
        {
          k = i + j;
          if ((k >= paramArrayOfInt2.length) || (j >= 16)) {
            break;
          }
          paramViewGroup = new StringBuilder();
          paramViewGroup.append(paramArrayOfInt2[k]);
          paramViewGroup.append(",");
          localStringBuilder1.append(paramViewGroup.toString());
          j += 1;
        }
        localStringBuilder1.append("\n");
        i += 16;
      }
      localStringBuilder1.append(" ]\n");
    }
    return localStringBuilder1.toString();
  }
  
  private static String constructSetChildrenErrorMessage(ViewGroup paramViewGroup, ViewGroupManager paramViewGroupManager, ReadableArray paramReadableArray)
  {
    ViewAtIndex[] arrayOfViewAtIndex = new ViewAtIndex[paramReadableArray.size()];
    int i = 0;
    while (i < paramReadableArray.size())
    {
      arrayOfViewAtIndex[i] = new ViewAtIndex(paramReadableArray.getInt(i), i);
      i += 1;
    }
    return constructManageChildrenErrorMessage(paramViewGroup, paramViewGroupManager, null, arrayOfViewAtIndex, null);
  }
  
  private SparseIntArray getOrCreatePendingIndicesToDelete(int paramInt)
  {
    SparseIntArray localSparseIntArray2 = (SparseIntArray)mTagsToPendingIndicesToDelete.get(paramInt);
    SparseIntArray localSparseIntArray1 = localSparseIntArray2;
    if (localSparseIntArray2 == null)
    {
      localSparseIntArray1 = new SparseIntArray();
      mTagsToPendingIndicesToDelete.put(paramInt, localSparseIntArray1);
    }
    return localSparseIntArray1;
  }
  
  private ThemedReactContext getReactContextForView(int paramInt)
  {
    Object localObject = (View)mTagsToViews.get(paramInt);
    if (localObject != null) {
      return (ThemedReactContext)((View)localObject).getContext();
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Could not find view with tag ");
    ((StringBuilder)localObject).append(paramInt);
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  private void mapRectFromViewToWindowCoords(View paramView, RectF paramRectF)
  {
    Matrix localMatrix = paramView.getMatrix();
    if (!localMatrix.isIdentity()) {
      localMatrix.mapRect(paramRectF);
    }
    paramRectF.offset(paramView.getLeft(), paramView.getTop());
    for (paramView = paramView.getParent(); (paramView instanceof View); paramView = paramView.getParent())
    {
      paramView = (View)paramView;
      paramRectF.offset(-paramView.getScrollX(), -paramView.getScrollY());
      localMatrix = paramView.getMatrix();
      if (!localMatrix.isIdentity()) {
        localMatrix.mapRect(paramRectF);
      }
      paramRectF.offset(paramView.getLeft(), paramView.getTop());
    }
  }
  
  private int normalizeIndex(int paramInt, SparseIntArray paramSparseIntArray)
  {
    int i = 0;
    int j = paramInt;
    while (i <= paramInt)
    {
      j += paramSparseIntArray.get(i);
      i += 1;
    }
    return j;
  }
  
  private void updateInstanceHandle(View paramView, long paramLong)
  {
    UiThreadUtil.assertOnUiThread();
    paramView.setTag(R.id.view_tag_instance_handle, Long.valueOf(paramLong));
  }
  
  private void updateLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((mLayoutAnimationEnabled) && (mLayoutAnimator.shouldAnimateLayout(paramView)))
    {
      mLayoutAnimator.applyLayoutUpdate(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    paramView.layout(paramInt1, paramInt2, paramInt3 + paramInt1, paramInt4 + paramInt2);
  }
  
  public void addRootView(int paramInt, View paramView)
  {
    try
    {
      addRootViewGroup(paramInt, paramView);
      return;
    }
    catch (Throwable paramView)
    {
      throw paramView;
    }
  }
  
  protected final void addRootViewGroup(int paramInt, View paramView)
  {
    try
    {
      if (paramView.getId() != -1)
      {
        String str = appName;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Trying to add a root view with an explicit id (");
        localStringBuilder.append(paramView.getId());
        localStringBuilder.append(") already set. React Native uses the id field to track react tags and will overwrite this field. If that is fine, explicitly overwrite the id field to View.NO_ID before calling addRootView.");
        FLog.e(str, localStringBuilder.toString());
      }
      mTagsToViews.put(paramInt, paramView);
      mTagsToViewManagers.put(paramInt, mRootViewManager);
      mRootTags.put(paramInt, true);
      paramView.setId(paramInt);
      return;
    }
    catch (Throwable paramView)
    {
      throw paramView;
    }
  }
  
  public void clearJSResponder()
  {
    mJSResponderHandler.clearJSResponder();
  }
  
  void clearLayoutAnimation()
  {
    mLayoutAnimator.reset();
  }
  
  void configureLayoutAnimation(ReadableMap paramReadableMap, Callback paramCallback)
  {
    mLayoutAnimator.initializeFromConfig(paramReadableMap, paramCallback);
  }
  
  /* Error */
  public void createView(ThemedReactContext paramThemedReactContext, int paramInt, String paramString, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: invokestatic 283	com/facebook/react/bridge/UiThreadUtil:assertOnUiThread	()V
    //   5: lconst_0
    //   6: ldc_w 353
    //   9: invokestatic 359	com/facebook/systrace/SystraceMessage:beginSection	(JLjava/lang/String;)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   12: ldc_w 361
    //   15: iload_2
    //   16: invokevirtual 367	com/facebook/systrace/SystraceMessage$Builder:getStream	(Ljava/lang/String;I)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   19: ldc_w 369
    //   22: aload_3
    //   23: invokevirtual 372	com/facebook/systrace/SystraceMessage$Builder:put	(Ljava/lang/String;Ljava/lang/Object;)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   26: invokevirtual 375	com/facebook/systrace/SystraceMessage$Builder:flush	()V
    //   29: aload_0
    //   30: getfield 80	com/facebook/react/uimanager/NativeViewHierarchyManager:mViewManagers	Lcom/facebook/react/uimanager/ViewManagerRegistry;
    //   33: aload_3
    //   34: invokevirtual 381	com/facebook/react/uimanager/ViewManagerRegistry:loadClass	(Ljava/lang/String;)Lcom/facebook/react/uimanager/ViewManager;
    //   37: astore_3
    //   38: aload_3
    //   39: aload_1
    //   40: aconst_null
    //   41: aconst_null
    //   42: aload_0
    //   43: getfield 59	com/facebook/react/uimanager/NativeViewHierarchyManager:mJSResponderHandler	Lcom/facebook/react/touch/JSResponderHandler;
    //   46: invokevirtual 386	com/facebook/react/uimanager/ViewManager:createView	(Lcom/facebook/react/uimanager/ThemedReactContext;Lcom/facebook/react/uimanager/ReactStylesDiffMap;Lcom/facebook/react/uimanager/StateWrapper;Lcom/facebook/react/touch/JSResponderHandler;)Landroid/view/View;
    //   49: astore_1
    //   50: aload_0
    //   51: getfield 82	com/facebook/react/uimanager/NativeViewHierarchyManager:mTagsToViews	Landroid/util/SparseArray;
    //   54: iload_2
    //   55: aload_1
    //   56: invokevirtual 223	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   59: aload_0
    //   60: getfield 84	com/facebook/react/uimanager/NativeViewHierarchyManager:mTagsToViewManagers	Landroid/util/SparseArray;
    //   63: iload_2
    //   64: aload_3
    //   65: invokevirtual 223	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   68: aload_1
    //   69: iload_2
    //   70: invokevirtual 337	android/view/View:setId	(I)V
    //   73: aload 4
    //   75: ifnull +10 -> 85
    //   78: aload_3
    //   79: aload_1
    //   80: aload 4
    //   82: invokevirtual 390	com/facebook/react/uimanager/ViewManager:updateProperties	(Landroid/view/View;Lcom/facebook/react/uimanager/ReactStylesDiffMap;)V
    //   85: lconst_0
    //   86: invokestatic 396	com/facebook/systrace/Systrace:endSection	(J)V
    //   89: aload_0
    //   90: monitorexit
    //   91: return
    //   92: astore_1
    //   93: lconst_0
    //   94: invokestatic 396	com/facebook/systrace/Systrace:endSection	(J)V
    //   97: aload_1
    //   98: athrow
    //   99: astore_1
    //   100: aload_0
    //   101: monitorexit
    //   102: aload_1
    //   103: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	104	0	this	NativeViewHierarchyManager
    //   0	104	1	paramThemedReactContext	ThemedReactContext
    //   0	104	2	paramInt	int
    //   0	104	3	paramString	String
    //   0	104	4	paramReactStylesDiffMap	ReactStylesDiffMap
    // Exception table:
    //   from	to	target	type
    //   29	73	92	java/lang/Throwable
    //   78	85	92	java/lang/Throwable
    //   2	29	99	java/lang/Throwable
    //   85	89	99	java/lang/Throwable
    //   93	99	99	java/lang/Throwable
  }
  
  public void dismissPopupMenu()
  {
    if (mPopupMenu != null) {
      mPopupMenu.dismiss();
    }
  }
  
  public void dispatchCommand(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      View localView = (View)mTagsToViews.get(paramInt1);
      if (localView != null)
      {
        resolveViewManager(paramInt1).receiveCommand(localView, paramInt2, paramReadableArray);
        return;
      }
      paramReadableArray = new StringBuilder();
      paramReadableArray.append("Trying to send command to a non-existing view with tag ");
      paramReadableArray.append(paramInt1);
      throw new IllegalViewOperationException(paramReadableArray.toString());
    }
    catch (Throwable paramReadableArray)
    {
      throw paramReadableArray;
    }
  }
  
  public void dispatchCommand(int paramInt, String paramString, ReadableArray paramReadableArray)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      View localView = (View)mTagsToViews.get(paramInt);
      if (localView != null)
      {
        resolveViewManager(paramInt).receiveCommand(localView, paramString, paramReadableArray);
        return;
      }
      paramString = new StringBuilder();
      paramString.append("Trying to send command to a non-existing view with tag ");
      paramString.append(paramInt);
      throw new IllegalViewOperationException(paramString.toString());
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  protected void dropView(View paramView)
  {
    for (;;)
    {
      int i;
      try
      {
        UiThreadUtil.assertOnUiThread();
        if (paramView == null) {
          return;
        }
        if (ReactFeatureFlags.logDroppedViews) {
          cacheDroppedTag(paramView.getId());
        }
        Object localObject1 = mTagsToViewManagers.get(paramView.getId());
        if (localObject1 == null) {
          return;
        }
        if (!mRootTags.get(paramView.getId())) {
          resolveViewManager(paramView.getId()).onDropViewInstance(paramView);
        }
        Object localObject2 = (ViewManager)mTagsToViewManagers.get(paramView.getId());
        if (((paramView instanceof ViewGroup)) && ((localObject2 instanceof ViewGroupManager)))
        {
          localObject1 = (ViewGroup)paramView;
          localObject2 = (ViewGroupManager)localObject2;
          i = ((ViewGroupManager)localObject2).getChildCount((ViewGroup)localObject1) - 1;
          if (i >= 0)
          {
            View localView = ((ViewGroupManager)localObject2).getChildAt((ViewGroup)localObject1, i);
            if (localView == null) {
              FLog.e(appName, "Unable to drop null child view");
            } else if (mTagsToViews.get(localView.getId()) != null) {
              dropView(localView);
            }
          }
          else
          {
            ((ViewGroupManager)localObject2).removeAllViews((ViewGroup)localObject1);
          }
        }
        else
        {
          mTagsToPendingIndicesToDelete.remove(paramView.getId());
          mTagsToViews.remove(paramView.getId());
          mTagsToViewManagers.remove(paramView.getId());
          return;
        }
      }
      catch (Throwable paramView)
      {
        throw paramView;
      }
      i -= 1;
    }
  }
  
  public int findTargetTagForTouch(int paramInt, float paramFloat1, float paramFloat2)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      Object localObject = (View)mTagsToViews.get(paramInt);
      if (localObject != null)
      {
        paramInt = TouchTargetHelper.findTargetTagForTouch(paramFloat1, paramFloat2, (ViewGroup)localObject);
        return paramInt;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Could not find view with tag ");
      ((StringBuilder)localObject).append(paramInt);
      throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getInstanceHandle(int paramInt)
  {
    Object localObject = (View)mTagsToViews.get(paramInt);
    if (localObject != null)
    {
      localObject = (Long)((View)localObject).getTag(R.id.view_tag_instance_handle);
      if (localObject != null) {
        return ((Long)localObject).longValue();
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Unable to find instanceHandle for tag: ");
      ((StringBuilder)localObject).append(paramInt);
      throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to find view for tag: ");
    ((StringBuilder)localObject).append(paramInt);
    throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
  }
  
  public void manageChildren(int paramInt, int[] paramArrayOfInt1, ViewAtIndex[] paramArrayOfViewAtIndex, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
  {
    for (;;)
    {
      int i;
      int k;
      try
      {
        UiThreadUtil.assertOnUiThread();
        Object localObject = getOrCreatePendingIndicesToDelete(paramInt);
        final ViewGroup localViewGroup = (ViewGroup)mTagsToViews.get(paramInt);
        final ViewGroupManager localViewGroupManager = (ViewGroupManager)resolveViewManager(paramInt);
        if (localViewGroup != null)
        {
          j = localViewGroupManager.getChildCount(localViewGroup);
          if (paramArrayOfInt1 == null) {
            break label825;
          }
          i = paramArrayOfInt1.length - 1;
          if (i < 0) {
            break label825;
          }
          k = paramArrayOfInt1[i];
          final View localView;
          if (k >= 0)
          {
            if (k >= localViewGroupManager.getChildCount(localViewGroup))
            {
              if (mRootTags.get(paramInt))
              {
                i = localViewGroupManager.getChildCount(localViewGroup);
                if (i == 0) {
                  return;
                }
              }
              paramArrayOfInt3 = new StringBuilder();
              paramArrayOfInt3.append("Trying to remove a view index above child count ");
              paramArrayOfInt3.append(k);
              paramArrayOfInt3.append(" view tag: ");
              paramArrayOfInt3.append(paramInt);
              paramArrayOfInt3.append("\n detail: ");
              paramArrayOfInt3.append(constructManageChildrenErrorMessage(localViewGroup, localViewGroupManager, paramArrayOfInt1, paramArrayOfViewAtIndex, paramArrayOfInt2));
              throw new IllegalViewOperationException(paramArrayOfInt3.toString());
            }
            if (k < j)
            {
              j = normalizeIndex(k, (SparseIntArray)localObject);
              localView = localViewGroupManager.getChildAt(localViewGroup, j);
              if ((!mLayoutAnimationEnabled) || (!mLayoutAnimator.shouldAnimateLayout(localView)) || (!arrayContains(paramArrayOfInt2, localView.getId()))) {
                localViewGroupManager.removeViewAt(localViewGroup, j);
              }
            }
            else
            {
              paramArrayOfInt3 = new StringBuilder();
              paramArrayOfInt3.append("Trying to remove an out of order view index:");
              paramArrayOfInt3.append(k);
              paramArrayOfInt3.append(" view tag: ");
              paramArrayOfInt3.append(paramInt);
              paramArrayOfInt3.append("\n detail: ");
              paramArrayOfInt3.append(constructManageChildrenErrorMessage(localViewGroup, localViewGroupManager, paramArrayOfInt1, paramArrayOfViewAtIndex, paramArrayOfInt2));
              throw new IllegalViewOperationException(paramArrayOfInt3.toString());
            }
          }
          else
          {
            paramArrayOfInt3 = new StringBuilder();
            paramArrayOfInt3.append("Trying to remove a negative view index:");
            paramArrayOfInt3.append(k);
            paramArrayOfInt3.append(" view tag: ");
            paramArrayOfInt3.append(paramInt);
            paramArrayOfInt3.append("\n detail: ");
            paramArrayOfInt3.append(constructManageChildrenErrorMessage(localViewGroup, localViewGroupManager, paramArrayOfInt1, paramArrayOfViewAtIndex, paramArrayOfInt2));
            throw new IllegalViewOperationException(paramArrayOfInt3.toString());
            if (paramInt >= paramArrayOfInt2.length) {
              break label842;
            }
            i = paramArrayOfInt2[paramInt];
            j = paramArrayOfInt3[paramInt];
            localView = (View)mTagsToViews.get(i);
            if (localView != null)
            {
              if ((mLayoutAnimationEnabled) && (mLayoutAnimator.shouldAnimateLayout(localView)))
              {
                ((SparseIntArray)localObject).put(j, ((SparseIntArray)localObject).get(j, 0) + 1);
                mLayoutAnimator.deleteView(localView, new LayoutAnimationListener()
                {
                  public void onAnimationEnd()
                  {
                    localViewGroupManager.removeView(localViewGroup, localView);
                    dropView(localView);
                    int i = val$pendingIndicesToDelete.get(j, 0);
                    val$pendingIndicesToDelete.put(j, Math.max(0, i - 1));
                  }
                });
                break label835;
              }
              dropView(localView);
              break label835;
            }
            paramArrayOfInt3 = new StringBuilder();
            paramArrayOfInt3.append("Trying to destroy unknown view tag: ");
            paramArrayOfInt3.append(i);
            paramArrayOfInt3.append("\n detail: ");
            paramArrayOfInt3.append(constructManageChildrenErrorMessage(localViewGroup, localViewGroupManager, paramArrayOfInt1, paramArrayOfViewAtIndex, paramArrayOfInt2));
            throw new IllegalViewOperationException(paramArrayOfInt3.toString());
            if (paramInt < paramArrayOfViewAtIndex.length)
            {
              paramArrayOfInt3 = paramArrayOfViewAtIndex[paramInt];
              localView = (View)mTagsToViews.get(mTag);
              if (localView != null)
              {
                localViewGroupManager.addView(localViewGroup, localView, normalizeIndex(mIndex, (SparseIntArray)localObject));
                paramInt += 1;
                continue;
              }
              localObject = new StringBuilder();
              ((StringBuilder)localObject).append("Trying to add unknown view tag: ");
              ((StringBuilder)localObject).append(mTag);
              ((StringBuilder)localObject).append("\n detail: ");
              ((StringBuilder)localObject).append(constructManageChildrenErrorMessage(localViewGroup, localViewGroupManager, paramArrayOfInt1, paramArrayOfViewAtIndex, paramArrayOfInt2));
              throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
            }
          }
        }
        else
        {
          paramArrayOfInt3 = new StringBuilder();
          paramArrayOfInt3.append("Trying to manageChildren view with tag ");
          paramArrayOfInt3.append(paramInt);
          paramArrayOfInt3.append(" which doesn't exist\n detail: ");
          paramArrayOfInt3.append(constructManageChildrenErrorMessage(localViewGroup, localViewGroupManager, paramArrayOfInt1, paramArrayOfViewAtIndex, paramArrayOfInt2));
          throw new IllegalViewOperationException(paramArrayOfInt3.toString());
        }
      }
      catch (Throwable paramArrayOfInt1)
      {
        throw paramArrayOfInt1;
      }
      i -= 1;
      final int j = k;
      continue;
      label825:
      if (paramArrayOfInt2 != null)
      {
        paramInt = 0;
        continue;
        label835:
        paramInt += 1;
      }
      else
      {
        label842:
        if (paramArrayOfViewAtIndex != null) {
          paramInt = 0;
        }
      }
    }
  }
  
  public void measure(int paramInt, int[] paramArrayOfInt)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      View localView1 = (View)mTagsToViews.get(paramInt);
      if (localView1 != null)
      {
        View localView2 = (View)RootViewUtil.getRootView(localView1);
        if (localView2 != null)
        {
          computeBoundingBox(localView2, paramArrayOfInt);
          paramInt = paramArrayOfInt[0];
          int i = paramArrayOfInt[1];
          computeBoundingBox(localView1, paramArrayOfInt);
          paramArrayOfInt[0] -= paramInt;
          paramArrayOfInt[1] -= i;
          return;
        }
        paramArrayOfInt = new StringBuilder();
        paramArrayOfInt.append("Native view ");
        paramArrayOfInt.append(paramInt);
        paramArrayOfInt.append(" is no longer on screen");
        throw new NoSuchNativeViewException(paramArrayOfInt.toString());
      }
      paramArrayOfInt = new StringBuilder();
      paramArrayOfInt.append("No native view for ");
      paramArrayOfInt.append(paramInt);
      paramArrayOfInt.append(" currently exists");
      throw new NoSuchNativeViewException(paramArrayOfInt.toString());
    }
    catch (Throwable paramArrayOfInt)
    {
      throw paramArrayOfInt;
    }
  }
  
  public void measureInWindow(int paramInt, int[] paramArrayOfInt)
  {
    Object localObject1 = this;
    try
    {
      UiThreadUtil.assertOnUiThread();
      localObject1 = this;
      Object localObject2 = mTagsToViews;
      NativeViewHierarchyManager localNativeViewHierarchyManager = this;
      localObject1 = localNativeViewHierarchyManager;
      localObject2 = (View)((SparseArray)localObject2).get(paramInt);
      if (localObject2 != null)
      {
        localObject1 = localNativeViewHierarchyManager;
        ((View)localObject2).getLocationOnScreen(paramArrayOfInt);
        localObject1 = localNativeViewHierarchyManager;
        Resources localResources = ((View)localObject2).getContext().getResources();
        localObject1 = localNativeViewHierarchyManager;
        paramInt = localResources.getIdentifier("status_bar_height", "dimen", "android");
        if (paramInt > 0)
        {
          localObject1 = localNativeViewHierarchyManager;
          paramInt = (int)localResources.getDimension(paramInt);
          paramArrayOfInt[1] -= paramInt;
        }
        localObject1 = localNativeViewHierarchyManager;
        paramArrayOfInt[2] = ((View)localObject2).getWidth();
        localObject1 = localNativeViewHierarchyManager;
        paramArrayOfInt[3] = ((View)localObject2).getHeight();
        return;
      }
      localObject1 = localNativeViewHierarchyManager;
      paramArrayOfInt = new StringBuilder();
      localObject1 = localNativeViewHierarchyManager;
      paramArrayOfInt.append("No native view for ");
      localObject1 = localNativeViewHierarchyManager;
      paramArrayOfInt.append(paramInt);
      localObject1 = localNativeViewHierarchyManager;
      paramArrayOfInt.append(" currently exists");
      localObject1 = localNativeViewHierarchyManager;
      throw new NoSuchNativeViewException(paramArrayOfInt.toString());
    }
    catch (Throwable paramArrayOfInt)
    {
      throw paramArrayOfInt;
    }
  }
  
  public void removeRootView(int paramInt)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      if (!mRootTags.get(paramInt))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("View with tag ");
        localStringBuilder.append(paramInt);
        localStringBuilder.append(" is not registered as a root view");
        SoftAssertions.assertUnreachable(localStringBuilder.toString());
      }
      dropView((View)mTagsToViews.get(paramInt));
      mRootTags.delete(paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final View resolveView(int paramInt)
  {
    try
    {
      Object localObject = (View)mTagsToViews.get(paramInt);
      if (localObject != null) {
        return localObject;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Trying to resolve view with tag ");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(" which doesn't exist");
      throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final ViewManager resolveViewManager(int paramInt)
  {
    Object localObject1 = this;
    try
    {
      Object localObject2 = mTagsToViewManagers;
      NativeViewHierarchyManager localNativeViewHierarchyManager = this;
      localObject1 = localNativeViewHierarchyManager;
      localObject2 = (ViewManager)((SparseArray)localObject2).get(paramInt);
      if (localObject2 != null) {
        return localObject2;
      }
      localObject1 = localNativeViewHierarchyManager;
      boolean bool = Arrays.asList(new int[][] { mDroppedViewArray }).contains(Integer.valueOf(paramInt));
      localObject1 = localNativeViewHierarchyManager;
      localObject2 = new StringBuilder();
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append("ViewManager for tag ");
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append(paramInt);
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append(" could not be found.\n View already dropped? ");
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append(bool);
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append(".\nLast index ");
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append(mDroppedViewIndex);
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append(" in last 100 views");
      localObject1 = localNativeViewHierarchyManager;
      ((StringBuilder)localObject2).append(mDroppedViewArray.toString());
      localObject1 = localNativeViewHierarchyManager;
      throw new IllegalViewOperationException(((StringBuilder)localObject2).toString());
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void sendAccessibilityEvent(int paramInt1, int paramInt2)
  {
    Object localObject = (View)mTagsToViews.get(paramInt1);
    if (localObject != null)
    {
      ((View)localObject).sendAccessibilityEvent(paramInt2);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Could not find view with tag ");
    ((StringBuilder)localObject).append(paramInt1);
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void setChildren(int paramInt, ReadableArray paramReadableArray)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      ViewGroup localViewGroup = (ViewGroup)mTagsToViews.get(paramInt);
      ViewGroupManager localViewGroupManager = (ViewGroupManager)resolveViewManager(paramInt);
      paramInt = 0;
      while (paramInt < paramReadableArray.size())
      {
        Object localObject = (View)mTagsToViews.get(paramReadableArray.getInt(paramInt));
        if (localObject != null)
        {
          localViewGroupManager.addView(localViewGroup, (View)localObject, paramInt);
          paramInt += 1;
        }
        else
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Trying to add unknown view tag: ");
          ((StringBuilder)localObject).append(paramReadableArray.getInt(paramInt));
          ((StringBuilder)localObject).append("\n detail: ");
          ((StringBuilder)localObject).append(constructSetChildrenErrorMessage(localViewGroup, localViewGroupManager, paramReadableArray));
          throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
        }
      }
      return;
    }
    catch (Throwable paramReadableArray)
    {
      throw paramReadableArray;
    }
  }
  
  public void setJSResponder(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    View localView;
    if (!paramBoolean)
    {
      try
      {
        mJSResponderHandler.setJSResponder(paramInt2, null);
        return;
      }
      catch (Throwable localThrowable) {}
    }
    else
    {
      localView = (View)mTagsToViews.get(paramInt1);
      if ((paramInt2 != paramInt1) && ((localView instanceof ViewParent)))
      {
        mJSResponderHandler.setJSResponder(paramInt2, (ViewParent)localView);
        return;
      }
      if (mRootTags.get(paramInt1))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Cannot block native responder on ");
        localStringBuilder.append(paramInt1);
        localStringBuilder.append(" that is a root view");
        SoftAssertions.assertUnreachable(localStringBuilder.toString());
      }
      mJSResponderHandler.setJSResponder(paramInt2, localView.getParent());
      return;
    }
    throw localView;
  }
  
  public void setLayoutAnimationEnabled(boolean paramBoolean)
  {
    mLayoutAnimationEnabled = paramBoolean;
  }
  
  public void showPopupMenu(int paramInt, ReadableArray paramReadableArray, Callback paramCallback1, Callback paramCallback2)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      View localView = (View)mTagsToViews.get(paramInt);
      if (localView == null)
      {
        paramReadableArray = new StringBuilder();
        paramReadableArray.append("Can't display popup. Could not find view with tag ");
        paramReadableArray.append(paramInt);
        paramCallback2.invoke(new Object[] { paramReadableArray.toString() });
        return;
      }
      mPopupMenu = new PopupMenu(getReactContextForView(paramInt), localView);
      paramCallback2 = mPopupMenu.getMenu();
      paramInt = 0;
      while (paramInt < paramReadableArray.size())
      {
        paramCallback2.add(0, 0, paramInt, paramReadableArray.getString(paramInt));
        paramInt += 1;
      }
      paramReadableArray = new PopupMenuCallbackHandler(paramCallback1, null);
      mPopupMenu.setOnMenuItemClickListener(paramReadableArray);
      mPopupMenu.setOnDismissListener(paramReadableArray);
      mPopupMenu.show();
      return;
    }
    catch (Throwable paramReadableArray)
    {
      throw paramReadableArray;
    }
  }
  
  public void updateInstanceHandle(int paramInt, long paramLong)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      try
      {
        updateInstanceHandle(resolveView(paramInt), paramLong);
      }
      catch (IllegalViewOperationException localIllegalViewOperationException)
      {
        String str = appName;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unable to update properties for view tag ");
        localStringBuilder.append(paramInt);
        FLog.e(str, localStringBuilder.toString(), localIllegalViewOperationException);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  /* Error */
  public void updateLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: invokestatic 283	com/facebook/react/bridge/UiThreadUtil:assertOnUiThread	()V
    //   5: lconst_0
    //   6: ldc_w 690
    //   9: invokestatic 359	com/facebook/systrace/SystraceMessage:beginSection	(JLjava/lang/String;)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   12: ldc_w 692
    //   15: iload_1
    //   16: invokevirtual 367	com/facebook/systrace/SystraceMessage$Builder:getStream	(Ljava/lang/String;I)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   19: ldc_w 361
    //   22: iload_2
    //   23: invokevirtual 367	com/facebook/systrace/SystraceMessage$Builder:getStream	(Ljava/lang/String;I)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   26: invokevirtual 375	com/facebook/systrace/SystraceMessage$Builder:flush	()V
    //   29: aload_0
    //   30: iload_2
    //   31: invokevirtual 680	com/facebook/react/uimanager/NativeViewHierarchyManager:resolveView	(I)Landroid/view/View;
    //   34: astore 7
    //   36: aload 7
    //   38: iload 5
    //   40: ldc_w 693
    //   43: invokestatic 698	android/view/View$MeasureSpec:makeMeasureSpec	(II)I
    //   46: iload 6
    //   48: ldc_w 693
    //   51: invokestatic 698	android/view/View$MeasureSpec:makeMeasureSpec	(II)I
    //   54: invokevirtual 700	android/view/View:measure	(II)V
    //   57: aload 7
    //   59: invokevirtual 266	android/view/View:getParent	()Landroid/view/ViewParent;
    //   62: astore 8
    //   64: aload 8
    //   66: instanceof 702
    //   69: ifeq +10 -> 79
    //   72: aload 8
    //   74: invokeinterface 705 1 0
    //   79: aload_0
    //   80: getfield 89	com/facebook/react/uimanager/NativeViewHierarchyManager:mRootTags	Landroid/util/SparseBooleanArray;
    //   83: iload_1
    //   84: invokevirtual 435	android/util/SparseBooleanArray:get	(I)Z
    //   87: ifne +109 -> 196
    //   90: aload_0
    //   91: getfield 84	com/facebook/react/uimanager/NativeViewHierarchyManager:mTagsToViewManagers	Landroid/util/SparseArray;
    //   94: iload_1
    //   95: invokevirtual 216	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   98: checkcast 383	com/facebook/react/uimanager/ViewManager
    //   101: astore 8
    //   103: aload 8
    //   105: instanceof 707
    //   108: ifeq +41 -> 149
    //   111: aload 8
    //   113: checkcast 707	com/facebook/react/uimanager/IViewManagerWithChildren
    //   116: astore 8
    //   118: aload 8
    //   120: ifnull +89 -> 209
    //   123: aload 8
    //   125: invokeinterface 710 1 0
    //   130: ifne +79 -> 209
    //   133: aload_0
    //   134: aload 7
    //   136: iload_3
    //   137: iload 4
    //   139: iload 5
    //   141: iload 6
    //   143: invokespecial 712	com/facebook/react/uimanager/NativeViewHierarchyManager:updateLayout	(Landroid/view/View;IIII)V
    //   146: goto +63 -> 209
    //   149: new 137	java/lang/StringBuilder
    //   152: dup
    //   153: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   156: astore 7
    //   158: aload 7
    //   160: ldc_w 714
    //   163: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: pop
    //   167: aload 7
    //   169: iload_1
    //   170: invokevirtual 150	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   173: pop
    //   174: aload 7
    //   176: ldc_w 716
    //   179: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: pop
    //   183: new 418	com/facebook/react/uimanager/IllegalViewOperationException
    //   186: dup
    //   187: aload 7
    //   189: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokespecial 419	com/facebook/react/uimanager/IllegalViewOperationException:<init>	(Ljava/lang/String;)V
    //   195: athrow
    //   196: aload_0
    //   197: aload 7
    //   199: iload_3
    //   200: iload 4
    //   202: iload 5
    //   204: iload 6
    //   206: invokespecial 712	com/facebook/react/uimanager/NativeViewHierarchyManager:updateLayout	(Landroid/view/View;IIII)V
    //   209: lconst_0
    //   210: invokestatic 396	com/facebook/systrace/Systrace:endSection	(J)V
    //   213: aload_0
    //   214: monitorexit
    //   215: return
    //   216: astore 7
    //   218: lconst_0
    //   219: invokestatic 396	com/facebook/systrace/Systrace:endSection	(J)V
    //   222: aload 7
    //   224: athrow
    //   225: astore 7
    //   227: aload_0
    //   228: monitorexit
    //   229: aload 7
    //   231: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	NativeViewHierarchyManager
    //   0	232	1	paramInt1	int
    //   0	232	2	paramInt2	int
    //   0	232	3	paramInt3	int
    //   0	232	4	paramInt4	int
    //   0	232	5	paramInt5	int
    //   0	232	6	paramInt6	int
    //   34	164	7	localObject1	Object
    //   216	7	7	localThrowable1	Throwable
    //   225	5	7	localThrowable2	Throwable
    //   62	62	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   29	79	216	java/lang/Throwable
    //   79	118	216	java/lang/Throwable
    //   123	146	216	java/lang/Throwable
    //   149	196	216	java/lang/Throwable
    //   196	209	216	java/lang/Throwable
    //   2	29	225	java/lang/Throwable
    //   209	213	225	java/lang/Throwable
    //   218	225	225	java/lang/Throwable
  }
  
  public void updateProperties(int paramInt, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      try
      {
        localObject1 = resolveViewManager(paramInt);
        localObject2 = resolveView(paramInt);
        if (paramReactStylesDiffMap != null) {
          ((ViewManager)localObject1).updateProperties((View)localObject2, paramReactStylesDiffMap);
        }
      }
      catch (IllegalViewOperationException paramReactStylesDiffMap)
      {
        Object localObject1 = appName;
        Object localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Unable to update properties for view tag ");
        ((StringBuilder)localObject2).append(paramInt);
        FLog.e((String)localObject1, ((StringBuilder)localObject2).toString(), paramReactStylesDiffMap);
      }
      return;
    }
    catch (Throwable paramReactStylesDiffMap)
    {
      throw paramReactStylesDiffMap;
    }
  }
  
  public void updateViewExtraData(int paramInt, Object paramObject)
  {
    try
    {
      UiThreadUtil.assertOnUiThread();
      resolveViewManager(paramInt).updateExtraData(resolveView(paramInt), paramObject);
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  private static class PopupMenuCallbackHandler
    implements PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener
  {
    boolean mConsumed = false;
    final Callback mSuccess;
    
    private PopupMenuCallbackHandler(Callback paramCallback)
    {
      mSuccess = paramCallback;
    }
    
    public void onDismiss(PopupMenu paramPopupMenu)
    {
      if (!mConsumed)
      {
        mSuccess.invoke(new Object[] { "dismissed" });
        mConsumed = true;
      }
    }
    
    public boolean onMenuItemClick(MenuItem paramMenuItem)
    {
      if (!mConsumed)
      {
        mSuccess.invoke(new Object[] { "itemSelected", Integer.valueOf(paramMenuItem.getOrder()) });
        mConsumed = true;
        return true;
      }
      return false;
    }
  }
}