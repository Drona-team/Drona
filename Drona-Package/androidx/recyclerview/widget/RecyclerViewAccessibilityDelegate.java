package androidx.recyclerview.widget;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat;
import java.util.Map;
import java.util.WeakHashMap;

public class RecyclerViewAccessibilityDelegate
  extends AccessibilityDelegateCompat
{
  private final ItemDelegate mItemDelegate;
  final RecyclerView mRecyclerView;
  
  public RecyclerViewAccessibilityDelegate(RecyclerView paramRecyclerView)
  {
    mRecyclerView = paramRecyclerView;
    paramRecyclerView = getItemDelegate();
    if ((paramRecyclerView != null) && ((paramRecyclerView instanceof ItemDelegate)))
    {
      mItemDelegate = ((ItemDelegate)paramRecyclerView);
      return;
    }
    mItemDelegate = new ItemDelegate(this);
  }
  
  public AccessibilityDelegateCompat getItemDelegate()
  {
    return mItemDelegate;
  }
  
  public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    if (((paramView instanceof RecyclerView)) && (!shouldIgnore()))
    {
      paramView = (RecyclerView)paramView;
      if (paramView.getLayoutManager() != null) {
        paramView.getLayoutManager().onInitializeAccessibilityEvent(paramAccessibilityEvent);
      }
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
    if ((!shouldIgnore()) && (mRecyclerView.getLayoutManager() != null)) {
      mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfoCompat);
    }
  }
  
  public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
      return true;
    }
    if ((!shouldIgnore()) && (mRecyclerView.getLayoutManager() != null)) {
      return mRecyclerView.getLayoutManager().performAccessibilityAction(paramInt, paramBundle);
    }
    return false;
  }
  
  boolean shouldIgnore()
  {
    return mRecyclerView.hasPendingAdapterUpdates();
  }
  
  public static class ItemDelegate
    extends AccessibilityDelegateCompat
  {
    private Map<View, AccessibilityDelegateCompat> mOriginalItemDelegates = new WeakHashMap();
    final RecyclerViewAccessibilityDelegate mRecyclerViewDelegate;
    
    public ItemDelegate(RecyclerViewAccessibilityDelegate paramRecyclerViewAccessibilityDelegate)
    {
      mRecyclerViewDelegate = paramRecyclerViewAccessibilityDelegate;
    }
    
    public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
      if (localAccessibilityDelegateCompat != null) {
        return localAccessibilityDelegateCompat.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
      }
      return super.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
      if (localAccessibilityDelegateCompat != null) {
        return localAccessibilityDelegateCompat.getAccessibilityNodeProvider(paramView);
      }
      return super.getAccessibilityNodeProvider(paramView);
    }
    
    AccessibilityDelegateCompat getAndRemoveOriginalDelegateForItem(View paramView)
    {
      return (AccessibilityDelegateCompat)mOriginalItemDelegates.remove(paramView);
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
      if (localAccessibilityDelegateCompat != null)
      {
        localAccessibilityDelegateCompat.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
        return;
      }
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      if ((!mRecyclerViewDelegate.shouldIgnore()) && (mRecyclerViewDelegate.mRecyclerView.getLayoutManager() != null))
      {
        mRecyclerViewDelegate.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
        AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
        if (localAccessibilityDelegateCompat != null)
        {
          localAccessibilityDelegateCompat.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
          return;
        }
        super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
        return;
      }
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
    }
    
    public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
      if (localAccessibilityDelegateCompat != null)
      {
        localAccessibilityDelegateCompat.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
        return;
      }
      super.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramViewGroup);
      if (localAccessibilityDelegateCompat != null) {
        return localAccessibilityDelegateCompat.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
      }
      return super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if ((!mRecyclerViewDelegate.shouldIgnore()) && (mRecyclerViewDelegate.mRecyclerView.getLayoutManager() != null))
      {
        AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
        if (localAccessibilityDelegateCompat != null)
        {
          if (localAccessibilityDelegateCompat.performAccessibilityAction(paramView, paramInt, paramBundle)) {
            return true;
          }
        }
        else if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
          return true;
        }
        return mRecyclerViewDelegate.mRecyclerView.getLayoutManager().performAccessibilityActionForItem(paramView, paramInt, paramBundle);
      }
      return super.performAccessibilityAction(paramView, paramInt, paramBundle);
    }
    
    void saveOriginalDelegate(View paramView)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = ViewCompat.getAccessibilityDelegate(paramView);
      if ((localAccessibilityDelegateCompat != null) && (localAccessibilityDelegateCompat != this)) {
        mOriginalItemDelegates.put(paramView, localAccessibilityDelegateCompat);
      }
    }
    
    public void sendAccessibilityEvent(View paramView, int paramInt)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
      if (localAccessibilityDelegateCompat != null)
      {
        localAccessibilityDelegateCompat.sendAccessibilityEvent(paramView, paramInt);
        return;
      }
      super.sendAccessibilityEvent(paramView, paramInt);
    }
    
    public void sendAccessibilityEventUnchecked(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      AccessibilityDelegateCompat localAccessibilityDelegateCompat = (AccessibilityDelegateCompat)mOriginalItemDelegates.get(paramView);
      if (localAccessibilityDelegateCompat != null)
      {
        localAccessibilityDelegateCompat.sendAccessibilityEventUnchecked(paramView, paramAccessibilityEvent);
        return;
      }
      super.sendAccessibilityEventUnchecked(paramView, paramAccessibilityEvent);
    }
  }
}
