package com.bumptech.glide.manager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build.VERSION;
import android.util.Log;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Deprecated
public class RequestManagerFragment
  extends Fragment
{
  private static final String EVENTLOG_URL = "RMFragment";
  private final Set<RequestManagerFragment> childRequestManagerFragments = new HashSet();
  private final ActivityFragmentLifecycle lifecycle;
  @Nullable
  private Fragment parentFragmentHint;
  @Nullable
  private RequestManager requestManager;
  private final RequestManagerTreeNode requestManagerTreeNode = new FragmentRequestManagerTreeNode();
  @Nullable
  private RequestManagerFragment rootRequestManagerFragment;
  
  public RequestManagerFragment()
  {
    this(new ActivityFragmentLifecycle());
  }
  
  RequestManagerFragment(ActivityFragmentLifecycle paramActivityFragmentLifecycle)
  {
    lifecycle = paramActivityFragmentLifecycle;
  }
  
  private void addChildRequestManagerFragment(RequestManagerFragment paramRequestManagerFragment)
  {
    childRequestManagerFragments.add(paramRequestManagerFragment);
  }
  
  private Fragment getParentFragmentUsingHint()
  {
    Fragment localFragment;
    if (Build.VERSION.SDK_INT >= 17) {
      localFragment = getParentFragment();
    } else {
      localFragment = null;
    }
    if (localFragment != null) {
      return localFragment;
    }
    return parentFragmentHint;
  }
  
  private boolean isDescendant(Fragment paramFragment)
  {
    Fragment localFragment1 = getParentFragment();
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
  
  private void registerFragmentWithRoot(Activity paramActivity)
  {
    unregisterFragmentWithRoot();
    rootRequestManagerFragment = Glide.get(paramActivity).getRequestManagerRetriever().getRequestManagerFragment(paramActivity);
    if (!equals(rootRequestManagerFragment)) {
      rootRequestManagerFragment.addChildRequestManagerFragment(this);
    }
  }
  
  private void removeChildRequestManagerFragment(RequestManagerFragment paramRequestManagerFragment)
  {
    childRequestManagerFragments.remove(paramRequestManagerFragment);
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
    if (equals(rootRequestManagerFragment)) {
      return Collections.unmodifiableSet(childRequestManagerFragments);
    }
    if ((rootRequestManagerFragment != null) && (Build.VERSION.SDK_INT >= 17))
    {
      HashSet localHashSet = new HashSet();
      Iterator localIterator = rootRequestManagerFragment.getDescendantRequestManagerFragments().iterator();
      while (localIterator.hasNext())
      {
        RequestManagerFragment localRequestManagerFragment = (RequestManagerFragment)localIterator.next();
        if (isDescendant(localRequestManagerFragment.getParentFragment())) {
          localHashSet.add(localRequestManagerFragment);
        }
      }
      return Collections.unmodifiableSet(localHashSet);
    }
    return Collections.emptySet();
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
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    try
    {
      registerFragmentWithRoot(paramActivity);
      return;
    }
    catch (IllegalStateException paramActivity)
    {
      if (Log.isLoggable("RMFragment", 5)) {
        Log.w("RMFragment", "Unable to register fragment with root", paramActivity);
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
  
  private class FragmentRequestManagerTreeNode
    implements RequestManagerTreeNode
  {
    FragmentRequestManagerTreeNode() {}
    
    public Set getDescendants()
    {
      Object localObject = getDescendantRequestManagerFragments();
      HashSet localHashSet = new HashSet(((Set)localObject).size());
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        RequestManagerFragment localRequestManagerFragment = (RequestManagerFragment)((Iterator)localObject).next();
        if (localRequestManagerFragment.getRequestManager() != null) {
          localHashSet.add(localRequestManagerFragment.getRequestManager());
        }
      }
      return localHashSet;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(super.toString());
      localStringBuilder.append("{fragment=");
      localStringBuilder.append(RequestManagerFragment.this);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}
