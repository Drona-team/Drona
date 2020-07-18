package com.facebook.react.fabric.mounting;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.fabric.events.EventEmitterWrapper;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.RootView;
import com.facebook.react.uimanager.RootViewManager;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.ViewManagerRegistry;
import com.facebook.yoga.YogaMeasureMode;
import java.util.concurrent.ConcurrentHashMap;

public class MountingManager
{
  private final JSResponderHandler mJSResponderHandler = new JSResponderHandler();
  private final RootViewManager mRootViewManager = new RootViewManager();
  private final ConcurrentHashMap<Integer, ViewState> mTagToViewState = new ConcurrentHashMap();
  private final ViewManagerRegistry mViewManagerRegistry;
  
  public MountingManager(ViewManagerRegistry paramViewManagerRegistry)
  {
    mViewManagerRegistry = paramViewManagerRegistry;
  }
  
  private void dropView(View paramView)
  {
    UiThreadUtil.assertOnUiThread();
    int j = paramView.getId();
    Object localObject1 = getViewState(j);
    Object localObject2 = mViewManager;
    if ((!mIsRoot) && (localObject2 != null)) {
      ((ViewManager)localObject2).onDropViewInstance(paramView);
    }
    if (((paramView instanceof ViewGroup)) && ((localObject2 instanceof ViewGroupManager)))
    {
      paramView = (ViewGroup)paramView;
      localObject1 = getViewGroupManager((ViewState)localObject1);
      int i = ((ViewGroupManager)localObject1).getChildCount(paramView) - 1;
      while (i >= 0)
      {
        localObject2 = ((ViewGroupManager)localObject1).getChildAt(paramView, i);
        if (mTagToViewState.get(Integer.valueOf(((View)localObject2).getId())) != null) {
          dropView((View)localObject2);
        }
        ((ViewGroupManager)localObject1).removeViewAt(paramView, i);
        i -= 1;
      }
    }
    mTagToViewState.remove(Integer.valueOf(j));
  }
  
  private static ViewGroupManager getViewGroupManager(ViewState paramViewState)
  {
    if (mViewManager != null) {
      return (ViewGroupManager)mViewManager;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unable to find ViewManager for view: ");
    localStringBuilder.append(paramViewState);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  private ViewState getViewState(int paramInt)
  {
    Object localObject = (ViewState)mTagToViewState.get(Integer.valueOf(paramInt));
    if (localObject != null) {
      return localObject;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to find viewState view for tag ");
    ((StringBuilder)localObject).append(paramInt);
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public void addRootView(int paramInt, View paramView)
  {
    if (paramView.getId() == -1)
    {
      mTagToViewState.put(Integer.valueOf(paramInt), new ViewState(paramInt, paramView, mRootViewManager, true, null));
      paramView.setId(paramInt);
      return;
    }
    throw new IllegalViewOperationException("Trying to add a root view with an explicit id already set. React Native uses the id field to track react tags and will overwrite this field. If that is fine, explicitly overwrite the id field to View.NO_ID before calling addRootView.");
  }
  
  public void addViewAt(int paramInt1, int paramInt2, int paramInt3)
  {
    UiThreadUtil.assertOnUiThread();
    Object localObject = getViewState(paramInt1);
    ViewGroup localViewGroup = (ViewGroup)mView;
    ViewState localViewState = getViewState(paramInt2);
    View localView = mView;
    if (localView != null)
    {
      getViewGroupManager((ViewState)localObject).addView(localViewGroup, localView, paramInt3);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to find view for viewState ");
    ((StringBuilder)localObject).append(localViewState);
    ((StringBuilder)localObject).append(" and tag ");
    ((StringBuilder)localObject).append(paramInt2);
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public void clearJSResponder()
  {
    mJSResponderHandler.clearJSResponder();
  }
  
  public void createView(ThemedReactContext paramThemedReactContext, String paramString, int paramInt, ReadableMap paramReadableMap, StateWrapper paramStateWrapper, boolean paramBoolean)
  {
    if (mTagToViewState.get(Integer.valueOf(paramInt)) != null) {
      return;
    }
    Object localObject1 = null;
    if (paramReadableMap != null) {
      paramReadableMap = new ReactStylesDiffMap(paramReadableMap);
    } else {
      paramReadableMap = null;
    }
    if (paramBoolean)
    {
      Object localObject2 = mViewManagerRegistry.loadClass(paramString);
      paramString = (String)localObject2;
      localObject2 = ((ViewManager)localObject2).createView(paramThemedReactContext, paramReadableMap, paramStateWrapper, mJSResponderHandler);
      paramThemedReactContext = (ThemedReactContext)localObject2;
      ((View)localObject2).setId(paramInt);
    }
    else
    {
      paramThemedReactContext = null;
      paramString = null;
    }
    paramString = new ViewState(paramInt, paramThemedReactContext, paramString, null);
    mCurrentProps = paramReadableMap;
    paramThemedReactContext = localObject1;
    if (paramStateWrapper != null) {
      paramThemedReactContext = paramStateWrapper.getState();
    }
    mCurrentState = paramThemedReactContext;
    mTagToViewState.put(Integer.valueOf(paramInt), paramString);
  }
  
  public void deleteView(int paramInt)
  {
    UiThreadUtil.assertOnUiThread();
    View localView = getViewStatemView;
    if (localView != null)
    {
      dropView(localView);
      return;
    }
    mTagToViewState.remove(Integer.valueOf(paramInt));
  }
  
  public EventEmitterWrapper getEventEmitter(int paramInt)
  {
    ViewState localViewState = (ViewState)mTagToViewState.get(Integer.valueOf(paramInt));
    if (localViewState == null) {
      return null;
    }
    return mEventEmitter;
  }
  
  public long measure(Context paramContext, String paramString, ReadableMap paramReadableMap1, ReadableMap paramReadableMap2, ReadableMap paramReadableMap3, float paramFloat1, YogaMeasureMode paramYogaMeasureMode1, float paramFloat2, YogaMeasureMode paramYogaMeasureMode2)
  {
    return mViewManagerRegistry.loadClass(paramString).measure(paramContext, paramReadableMap1, paramReadableMap2, paramReadableMap3, paramFloat1, paramYogaMeasureMode1, paramFloat2, paramYogaMeasureMode2);
  }
  
  public void preallocateView(ThemedReactContext paramThemedReactContext, String paramString, int paramInt, ReadableMap paramReadableMap, StateWrapper paramStateWrapper, boolean paramBoolean)
  {
    if (mTagToViewState.get(Integer.valueOf(paramInt)) == null)
    {
      createView(paramThemedReactContext, paramString, paramInt, paramReadableMap, paramStateWrapper, paramBoolean);
      return;
    }
    paramThemedReactContext = new StringBuilder();
    paramThemedReactContext.append("View for component ");
    paramThemedReactContext.append(paramString);
    paramThemedReactContext.append(" with tag ");
    paramThemedReactContext.append(paramInt);
    paramThemedReactContext.append(" already exists.");
    throw new IllegalStateException(paramThemedReactContext.toString());
  }
  
  public void receiveCommand(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
  {
    ViewState localViewState = getViewState(paramInt1);
    if (mViewManager != null)
    {
      if (mView != null)
      {
        mViewManager.receiveCommand(mView, paramInt2, paramReadableArray);
        return;
      }
      paramReadableArray = new StringBuilder();
      paramReadableArray.append("Unable to find viewState view for tag ");
      paramReadableArray.append(paramInt1);
      throw new IllegalStateException(paramReadableArray.toString());
    }
    paramReadableArray = new StringBuilder();
    paramReadableArray.append("Unable to find viewState manager for tag ");
    paramReadableArray.append(paramInt1);
    throw new IllegalStateException(paramReadableArray.toString());
  }
  
  public void receiveCommand(int paramInt, String paramString, ReadableArray paramReadableArray)
  {
    ViewState localViewState = getViewState(paramInt);
    if (mViewManager != null)
    {
      if (mView != null)
      {
        mViewManager.receiveCommand(mView, paramString, paramReadableArray);
        return;
      }
      paramString = new StringBuilder();
      paramString.append("Unable to find viewState view for tag ");
      paramString.append(paramInt);
      throw new IllegalStateException(paramString.toString());
    }
    paramString = new StringBuilder();
    paramString.append("Unable to find viewState manager for tag ");
    paramString.append(paramInt);
    throw new IllegalStateException(paramString.toString());
  }
  
  public void removeViewAt(int paramInt1, int paramInt2)
  {
    UiThreadUtil.assertOnUiThread();
    Object localObject = getViewState(paramInt1);
    ViewGroup localViewGroup = (ViewGroup)mView;
    if (localViewGroup != null)
    {
      getViewGroupManager((ViewState)localObject).removeViewAt(localViewGroup, paramInt2);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to find view for tag ");
    ((StringBuilder)localObject).append(paramInt1);
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public void setJSResponder(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object localObject1;
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
      Object localObject2 = getViewState(paramInt1);
      localObject1 = mView;
      if ((paramInt2 != paramInt1) && ((localObject1 instanceof ViewParent)))
      {
        mJSResponderHandler.setJSResponder(paramInt2, (ViewParent)localObject1);
        return;
      }
      if (localObject1 == null)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Cannot find view for tag ");
        ((StringBuilder)localObject1).append(paramInt1);
        ((StringBuilder)localObject1).append(".");
        SoftAssertions.assertUnreachable(((StringBuilder)localObject1).toString());
        return;
      }
      if (mIsRoot)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Cannot block native responder on ");
        ((StringBuilder)localObject2).append(paramInt1);
        ((StringBuilder)localObject2).append(" that is a root view");
        SoftAssertions.assertUnreachable(((StringBuilder)localObject2).toString());
      }
      mJSResponderHandler.setJSResponder(paramInt2, ((View)localObject1).getParent());
      return;
    }
    throw ((Throwable)localObject1);
  }
  
  public void updateEventEmitter(int paramInt, EventEmitterWrapper paramEventEmitterWrapper)
  {
    UiThreadUtil.assertOnUiThread();
    getViewStatemEventEmitter = paramEventEmitterWrapper;
  }
  
  public void updateLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    UiThreadUtil.assertOnUiThread();
    Object localObject = getViewState(paramInt1);
    if (mIsRoot) {
      return;
    }
    localObject = mView;
    if (localObject != null)
    {
      ((View)localObject).measure(View.MeasureSpec.makeMeasureSpec(paramInt4, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt5, 1073741824));
      ViewParent localViewParent = ((View)localObject).getParent();
      if ((localViewParent instanceof RootView)) {
        localViewParent.requestLayout();
      }
      ((View)localObject).layout(paramInt2, paramInt3, paramInt4 + paramInt2, paramInt5 + paramInt3);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to find View for tag: ");
    ((StringBuilder)localObject).append(paramInt1);
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  public void updateLocalData(int paramInt, ReadableMap paramReadableMap)
  {
    UiThreadUtil.assertOnUiThread();
    ViewState localViewState = getViewState(paramInt);
    if (mCurrentProps != null)
    {
      if ((mCurrentLocalData != null) && (paramReadableMap.hasKey("hash")) && (mCurrentLocalData.getDouble("hash") == paramReadableMap.getDouble("hash")) && (mCurrentLocalData.equals(paramReadableMap))) {
        return;
      }
      mCurrentLocalData = paramReadableMap;
      paramReadableMap = mViewManager;
      if (paramReadableMap != null)
      {
        Object localObject = paramReadableMap.updateLocalData(mView, mCurrentProps, new ReactStylesDiffMap(mCurrentLocalData));
        if (localObject != null) {
          paramReadableMap.updateExtraData(mView, localObject);
        }
      }
      else
      {
        paramReadableMap = new StringBuilder();
        paramReadableMap.append("Unable to find ViewManager for view: ");
        paramReadableMap.append(localViewState);
        throw new IllegalStateException(paramReadableMap.toString());
      }
    }
    else
    {
      paramReadableMap = new StringBuilder();
      paramReadableMap.append("Can not update local data to view without props: ");
      paramReadableMap.append(paramInt);
      throw new IllegalStateException(paramReadableMap.toString());
    }
  }
  
  public void updateProps(int paramInt, ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null) {
      return;
    }
    UiThreadUtil.assertOnUiThread();
    ViewState localViewState = getViewState(paramInt);
    mCurrentProps = new ReactStylesDiffMap(paramReadableMap);
    paramReadableMap = mView;
    if (paramReadableMap != null)
    {
      ((ViewManager)Assertions.assertNotNull(mViewManager)).updateProperties(paramReadableMap, mCurrentProps);
      return;
    }
    paramReadableMap = new StringBuilder();
    paramReadableMap.append("Unable to find view for tag ");
    paramReadableMap.append(paramInt);
    throw new IllegalStateException(paramReadableMap.toString());
  }
  
  public void updateState(int paramInt, StateWrapper paramStateWrapper)
  {
    UiThreadUtil.assertOnUiThread();
    ViewState localViewState = getViewState(paramInt);
    Object localObject = paramStateWrapper.getState();
    if ((mCurrentState != null) && (mCurrentState.equals(localObject))) {
      return;
    }
    mCurrentState = ((ReadableMap)localObject);
    localObject = mViewManager;
    if (localObject != null)
    {
      paramStateWrapper = ((ViewManager)localObject).updateState(mView, mCurrentProps, paramStateWrapper);
      if (paramStateWrapper != null) {
        ((ViewManager)localObject).updateExtraData(mView, paramStateWrapper);
      }
    }
    else
    {
      paramStateWrapper = new StringBuilder();
      paramStateWrapper.append("Unable to find ViewManager for tag: ");
      paramStateWrapper.append(paramInt);
      throw new IllegalStateException(paramStateWrapper.toString());
    }
  }
  
  private static class ViewState
  {
    @Nullable
    public ReadableMap mCurrentLocalData = null;
    @Nullable
    public ReactStylesDiffMap mCurrentProps = null;
    @Nullable
    public ReadableMap mCurrentState = null;
    @Nullable
    public EventEmitterWrapper mEventEmitter = null;
    final boolean mIsRoot;
    final int mReactTag;
    @Nullable
    final View mView;
    @Nullable
    final ViewManager mViewManager;
    
    private ViewState(int paramInt, View paramView, ViewManager paramViewManager)
    {
      this(paramInt, paramView, paramViewManager, false);
    }
    
    private ViewState(int paramInt, View paramView, ViewManager paramViewManager, boolean paramBoolean)
    {
      mReactTag = paramInt;
      mView = paramView;
      mIsRoot = paramBoolean;
      mViewManager = paramViewManager;
    }
    
    public String toString()
    {
      boolean bool;
      if (mViewManager == null) {
        bool = true;
      } else {
        bool = false;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ViewState [");
      localStringBuilder.append(mReactTag);
      localStringBuilder.append("] - isRoot: ");
      localStringBuilder.append(mIsRoot);
      localStringBuilder.append(" - props: ");
      localStringBuilder.append(mCurrentProps);
      localStringBuilder.append(" - localData: ");
      localStringBuilder.append(mCurrentLocalData);
      localStringBuilder.append(" - viewManager: ");
      localStringBuilder.append(mViewManager);
      localStringBuilder.append(" - isLayoutOnly: ");
      localStringBuilder.append(bool);
      return localStringBuilder.toString();
    }
  }
}
