package com.bumptech.glide.manager;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.package_5.Fragment;
import androidx.fragment.package_5.FragmentActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SupportRequestManagerFragment
  extends Fragment
{
  private static final String EVENTLOG_URL = "SupportRMFragment";
  private final Set<SupportRequestManagerFragment> childRequestManagerFragments = new HashSet();
  private final ActivityFragmentLifecycle lifecycle;
  @Nullable
  private Fragment parentFragmentHint;
  @Nullable
  private RequestManager requestManager;
  private final RequestManagerTreeNode requestManagerTreeNode = new SupportFragmentRequestManagerTreeNode();
  @Nullable
  private SupportRequestManagerFragment rootRequestManagerFragment;
  
  public SupportRequestManagerFragment()
  {
    this(new ActivityFragmentLifecycle());
  }
  
  public SupportRequestManagerFragment(ActivityFragmentLifecycle paramActivityFragmentLifecycle)
  {
    lifecycle = paramActivityFragmentLifecycle;
  }
  
  private void addChildRequestManagerFragment(SupportRequestManagerFragment paramSupportRequestManagerFragment)
  {
    childRequestManagerFragments.add(paramSupportRequestManagerFragment);
  }
  
  private Fragment getParentFragmentUsingHint()
  {
    Fragment localFragment = getParentFragment();
    if (localFragment != null) {
      return localFragment;
    }
    return parentFragmentHint;
  }
  
  private boolean isDescendant(Fragment paramFragment)
  {
    Fragment localFragment1 = getParentFragmentUsingHint();
    for (;;)
    {
      Fragment localFragment2 = paramFragment.getParentFragment();
      if (localFragment2 == null) {
        break;
      }
      if (localFragment2.equals(localFragment1)) {
        return true;
      }
      paramFragment = paramFragment.getParentFragment();
    }
    return false;
  }
  
  private void registerFragmentWithRoot(FragmentActivity paramFragmentActivity)
  {
    unregisterFragmentWithRoot();
    rootRequestManagerFragment = Glide.get(paramFragmentActivity).getRequestManagerRetriever().getSupportRequestManagerFragment(paramFragmentActivity);
    if (!equals(rootRequestManagerFragment)) {
      rootRequestManagerFragment.addChildRequestManagerFragment(this);
    }
  }
  
  private void removeChildRequestManagerFragment(SupportRequestManagerFragment paramSupportRequestManagerFragment)
  {
    childRequestManagerFragments.remove(paramSupportRequestManagerFragment);
  }
  
  private void unregisterFragmentWithRoot()
  {
    if (rootRequestManagerFragment != null)
    {
      rootRequestManagerFragment.removeChildRequestManagerFragment(this);
      rootRequestManagerFragment = null;
    }
  }
  
  Set getDescendantRequestManagerFragments()
  {
    if (rootRequestManagerFragment == null) {
      return Collections.emptySet();
    }
    if (equals(rootRequestManagerFragment)) {
      return Collections.unmodifiableSet(childRequestManagerFragments);
    }
    HashSet localHashSet = new HashSet();
    Iterator localIterator = rootRequestManagerFragment.getDescendantRequestManagerFragments().iterator();
    while (localIterator.hasNext())
    {
      SupportRequestManagerFragment localSupportRequestManagerFragment = (SupportRequestManagerFragment)localIterator.next();
      if (isDescendant(localSupportRequestManagerFragment.getParentFragmentUsingHint())) {
        localHashSet.add(localSupportRequestManagerFragment);
      }
    }
    return Collections.unmodifiableSet(localHashSet);
  }
  
  ActivityFragmentLifecycle getGlideLifecycle()
  {
    return lifecycle;
  }
  
  public RequestManager getRequestManager()
  {
    return requestManager;
  }
  
  public RequestManagerTreeNode getRequestManagerTreeNode()
  {
    return requestManagerTreeNode;
  }
  
  public void onAttach(Context paramContext)
  {
    super.onAttach(paramContext);
    try
    {
      registerFragmentWithRoot(getActivity());
      return;
    }
    catch (IllegalStateException paramContext)
    {
      if (Log.isLoggable("SupportRMFragment", 5)) {
        Log.w("SupportRMFragment", "Unable to register fragment with root", paramContext);
      }
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    lifecycle.onDestroy();
    unregisterFragmentWithRoot();
  }
  
  public void onDetach()
  {
    super.onDetach();
    parentFragmentHint = null;
    unregisterFragmentWithRoot();
  }
  
  public void onStart()
  {
    super.onStart();
    lifecycle.onStart();
  }
  
  public void onStop()
  {
    super.onStop();
    lifecycle.onStop();
  }
  
  void setParentFragmentHint(Fragment paramFragment)
  {
    parentFragmentHint = paramFragment;
    if ((paramFragment != null) && (paramFragment.getActivity() != null)) {
      registerFragmentWithRoot(paramFragment.getActivity());
    }
  }
  
  public void setRequestManager(RequestManager paramRequestManager)
  {
    requestManager = paramRequestManager;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    localStringBuilder.append("{parent=");
    localStringBuilder.append(getParentFragmentUsingHint());
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  private class SupportFragmentRequestManagerTreeNode
    implements RequestManagerTreeNode
  {
    SupportFragmentRequestManagerTreeNode() {}
    
    public Set getDescendants()
    {
      Object localObject = getDescendantRequestManagerFragments();
      HashSet localHashSet = new HashSet(((Set)localObject).size());
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        SupportRequestManagerFragment localSupportRequestManagerFragment = (SupportRequestManagerFragment)((Iterator)localObject).next();
        if (localSupportRequestManagerFragment.getRequestManager() != null) {
          localHashSet.add(localSupportRequestManagerFragment.getRequestManager());
        }
      }
      return localHashSet;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(super.toString());
      localStringBuilder.append("{fragment=");
      localStringBuilder.append(SupportRequestManagerFragment.this);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}