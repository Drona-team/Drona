package androidx.core.view;

import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import androidx.core.R.id;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class AccessibilityDelegateCompat
{
  private static final View.AccessibilityDelegate DEFAULT_DELEGATE = new View.AccessibilityDelegate();
  private final View.AccessibilityDelegate mBridge;
  private final View.AccessibilityDelegate mOriginalDelegate;
  
  public AccessibilityDelegateCompat()
  {
    this(DEFAULT_DELEGATE);
  }
  
  public AccessibilityDelegateCompat(View.AccessibilityDelegate paramAccessibilityDelegate)
  {
    mOriginalDelegate = paramAccessibilityDelegate;
    mBridge = new AccessibilityDelegateAdapter(this);
  }
  
  static List getActionList(View paramView)
  {
    List localList = (List)paramView.getTag(R.id.tag_accessibility_actions);
    paramView = localList;
    if (localList == null) {
      paramView = Collections.emptyList();
    }
    return paramView;
  }
  
  private boolean isSpanStillValid(ClickableSpan paramClickableSpan, View paramView)
  {
    if (paramClickableSpan != null)
    {
      paramView = AccessibilityNodeInfoCompat.getClickableSpans(paramView.createAccessibilityNodeInfo().getText());
      int i = 0;
      while ((paramView != null) && (i < paramView.length))
      {
        if (paramClickableSpan.equals(paramView[i])) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  private boolean performClickableSpanAction(int paramInt, View paramView)
  {
    Object localObject = (SparseArray)paramView.getTag(R.id.tag_accessibility_clickable_spans);
    if (localObject != null)
    {
      localObject = (WeakReference)((SparseArray)localObject).get(paramInt);
      if (localObject != null)
      {
        localObject = (ClickableSpan)((WeakReference)localObject).get();
        if (isSpanStillValid((ClickableSpan)localObject, paramView))
        {
          ((ClickableSpan)localObject).onClick(paramView);
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    return mOriginalDelegate.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
  }
  
  public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramView = mOriginalDelegate.getAccessibilityNodeProvider(paramView);
      if (paramView != null) {
        return new AccessibilityNodeProviderCompat(paramView);
      }
    }
    return null;
  }
  
  View.AccessibilityDelegate getBridge()
  {
    return mBridge;
  }
  
  public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    mOriginalDelegate.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
  }
  
  public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    mOriginalDelegate.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat.unwrap());
  }
  
  public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    mOriginalDelegate.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
  }
  
  public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    return mOriginalDelegate.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
  }
  
  public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    List localList = getActionList(paramView);
    boolean bool2 = false;
    int i = 0;
    for (;;)
    {
      bool1 = bool2;
      if (i >= localList.size()) {
        break;
      }
      AccessibilityNodeInfoCompat.AccessibilityActionCompat localAccessibilityActionCompat = (AccessibilityNodeInfoCompat.AccessibilityActionCompat)localList.get(i);
      if (localAccessibilityActionCompat.getId() == paramInt)
      {
        bool1 = localAccessibilityActionCompat.perform(paramView, paramBundle);
        break;
      }
      i += 1;
    }
    bool2 = bool1;
    if (!bool1)
    {
      bool2 = bool1;
      if (Build.VERSION.SDK_INT >= 16) {
        bool2 = mOriginalDelegate.performAccessibilityAction(paramView, paramInt, paramBundle);
      }
    }
    boolean bool1 = bool2;
    if (!bool2)
    {
      bool1 = bool2;
      if (paramInt == R.id.accessibility_action_clickable_span) {
        bool1 = performClickableSpanAction(paramBundle.getInt("ACCESSIBILITY_CLICKABLE_SPAN_ID", -1), paramView);
      }
    }
    return bool1;
  }
  
  public void sendAccessibilityEvent(View paramView, int paramInt)
  {
    mOriginalDelegate.sendAccessibilityEvent(paramView, paramInt);
  }
  
  public void sendAccessibilityEventUnchecked(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    mOriginalDelegate.sendAccessibilityEventUnchecked(paramView, paramAccessibilityEvent);
  }
  
  static final class AccessibilityDelegateAdapter
    extends View.AccessibilityDelegate
  {
    final AccessibilityDelegateCompat mCompat;
    
    AccessibilityDelegateAdapter(AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
    {
      mCompat = paramAccessibilityDelegateCompat;
    }
    
    public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      return mCompat.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public AccessibilityNodeProvider getAccessibilityNodeProvider(View paramView)
    {
      paramView = mCompat.getAccessibilityNodeProvider(paramView);
      if (paramView != null) {
        return (AccessibilityNodeProvider)paramView.getProvider();
      }
      return null;
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      mCompat.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.wrap(paramAccessibilityNodeInfo);
      localAccessibilityNodeInfoCompat.setScreenReaderFocusable(ViewCompat.isScreenReaderFocusable(paramView));
      localAccessibilityNodeInfoCompat.setHeading(ViewCompat.isAccessibilityHeading(paramView));
      localAccessibilityNodeInfoCompat.setPaneTitle(ViewCompat.getAccessibilityPaneTitle(paramView));
      mCompat.onInitializeAccessibilityNodeInfo(paramView, localAccessibilityNodeInfoCompat);
      localAccessibilityNodeInfoCompat.addSpansToExtras(paramAccessibilityNodeInfo.getText(), paramView);
      paramView = AccessibilityDelegateCompat.getActionList(paramView);
      int i = 0;
      while (i < paramView.size())
      {
        localAccessibilityNodeInfoCompat.addAction((AccessibilityNodeInfoCompat.AccessibilityActionCompat)paramView.get(i));
        i += 1;
      }
    }
    
    public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      mCompat.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      return mCompat.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      return mCompat.performAccessibilityAction(paramView, paramInt, paramBundle);
    }
    
    public void sendAccessibilityEvent(View paramView, int paramInt)
    {
      mCompat.sendAccessibilityEvent(paramView, paramInt);
    }
    
    public void sendAccessibilityEventUnchecked(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      mCompat.sendAccessibilityEventUnchecked(paramView, paramAccessibilityEvent);
    }
  }
}
