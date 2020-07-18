package com.bumptech.glide.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import androidx.annotation.VisibleForTesting;
import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import androidx.fragment.package_5.FragmentActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RequestManagerRetriever
  implements Handler.Callback
{
  private static final RequestManagerFactory DEFAULT_FACTORY = new RequestManagerFactory()
  {
    public RequestManager build(Glide paramAnonymousGlide, Lifecycle paramAnonymousLifecycle, RequestManagerTreeNode paramAnonymousRequestManagerTreeNode, Context paramAnonymousContext)
    {
      return new RequestManager(paramAnonymousGlide, paramAnonymousLifecycle, paramAnonymousRequestManagerTreeNode, paramAnonymousContext);
    }
  };
  private static final String FRAGMENT_INDEX_KEY = "key";
  @VisibleForTesting
  static final String FRAGMENT_TAG = "com.bumptech.glide.manager";
  private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
  private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;
  private static final String TAG = "RMRetriever";
  private volatile RequestManager applicationManager;
  private final RequestManagerFactory factory;
  private final Handler handler;
  @VisibleForTesting
  final Map<android.app.FragmentManager, RequestManagerFragment> pendingRequestManagerFragments = new HashMap();
  @VisibleForTesting
  final Map<androidx.fragment.app.FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap();
  private final Bundle tempBundle = new Bundle();
  private final ArrayMap<View, android.app.Fragment> tempViewToFragment = new ArrayMap();
  private final ArrayMap<View, androidx.fragment.app.Fragment> tempViewToSupportFragment = new ArrayMap();
  
  public RequestManagerRetriever(RequestManagerFactory paramRequestManagerFactory)
  {
    if (paramRequestManagerFactory == null) {
      paramRequestManagerFactory = DEFAULT_FACTORY;
    }
    factory = paramRequestManagerFactory;
    handler = new Handler(Looper.getMainLooper(), this);
  }
  
  private static void assertNotDestroyed(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      if (!paramActivity.isDestroyed()) {
        return;
      }
      throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
    }
  }
  
  private Activity findActivity(Context paramContext)
  {
    if ((paramContext instanceof Activity)) {
      return (Activity)paramContext;
    }
    if ((paramContext instanceof ContextWrapper)) {
      return findActivity(((ContextWrapper)paramContext).getBaseContext());
    }
    return null;
  }
  
  private void findAllFragmentsWithViews(android.app.FragmentManager paramFragmentManager, ArrayMap paramArrayMap)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      paramFragmentManager = paramFragmentManager.getFragments().iterator();
      while (paramFragmentManager.hasNext())
      {
        android.app.Fragment localFragment = (android.app.Fragment)paramFragmentManager.next();
        if (localFragment.getView() != null)
        {
          paramArrayMap.put(localFragment.getView(), localFragment);
          findAllFragmentsWithViews(localFragment.getChildFragmentManager(), paramArrayMap);
        }
      }
    }
    findAllFragmentsWithViewsPreO(paramFragmentManager, paramArrayMap);
  }
  
  private void findAllFragmentsWithViewsPreO(android.app.FragmentManager paramFragmentManager, ArrayMap paramArrayMap)
  {
    int i = 0;
    for (;;)
    {
      tempBundle.putInt("key", i);
      Object localObject1 = null;
      Object localObject2 = tempBundle;
      try
      {
        localObject2 = paramFragmentManager.getFragment((Bundle)localObject2, "key");
        localObject1 = localObject2;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
      if (localObject1 == null) {
        return;
      }
      if (localObject1.getView() != null)
      {
        paramArrayMap.put(localObject1.getView(), localObject1);
        if (Build.VERSION.SDK_INT >= 17) {
          findAllFragmentsWithViews(localObject1.getChildFragmentManager(), paramArrayMap);
        }
      }
      i += 1;
    }
  }
  
  private static void findAllSupportFragmentsWithViews(Collection paramCollection, Map paramMap)
  {
    if (paramCollection == null) {
      return;
    }
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      androidx.fragment.package_5.Fragment localFragment = (androidx.fragment.package_5.Fragment)paramCollection.next();
      if ((localFragment != null) && (localFragment.getView() != null))
      {
        paramMap.put(localFragment.getView(), localFragment);
        findAllSupportFragmentsWithViews(localFragment.getChildFragmentManager().getFragments(), paramMap);
      }
    }
  }
  
  private android.app.Fragment findFragment(View paramView, Activity paramActivity)
  {
    tempViewToFragment.clear();
    findAllFragmentsWithViews(paramActivity.getFragmentManager(), tempViewToFragment);
    View localView2 = paramActivity.findViewById(16908290);
    View localView1 = null;
    paramActivity = paramView;
    paramView = localView1;
    for (;;)
    {
      localView1 = paramView;
      if (paramActivity.equals(localView2)) {
        break;
      }
      paramView = (android.app.Fragment)tempViewToFragment.get(paramActivity);
      if (paramView != null)
      {
        localView1 = paramView;
        break;
      }
      localView1 = paramView;
      if (!(paramActivity.getParent() instanceof View)) {
        break;
      }
      paramActivity = (View)paramActivity.getParent();
    }
    tempViewToFragment.clear();
    return localView1;
  }
  
  private androidx.fragment.package_5.Fragment findSupportFragment(View paramView, FragmentActivity paramFragmentActivity)
  {
    tempViewToSupportFragment.clear();
    findAllSupportFragmentsWithViews(paramFragmentActivity.getSupportFragmentManager().getFragments(), tempViewToSupportFragment);
    View localView2 = paramFragmentActivity.findViewById(16908290);
    View localView1 = null;
    paramFragmentActivity = paramView;
    paramView = localView1;
    for (;;)
    {
      localView1 = paramView;
      if (paramFragmentActivity.equals(localView2)) {
        break;
      }
      paramView = (androidx.fragment.package_5.Fragment)tempViewToSupportFragment.get(paramFragmentActivity);
      if (paramView != null)
      {
        localView1 = paramView;
        break;
      }
      localView1 = paramView;
      if (!(paramFragmentActivity.getParent() instanceof View)) {
        break;
      }
      paramFragmentActivity = (View)paramFragmentActivity.getParent();
    }
    tempViewToSupportFragment.clear();
    return localView1;
  }
  
  private RequestManager fragmentGet(Context paramContext, android.app.FragmentManager paramFragmentManager, android.app.Fragment paramFragment, boolean paramBoolean)
  {
    RequestManagerFragment localRequestManagerFragment = getRequestManagerFragment(paramFragmentManager, paramFragment, paramBoolean);
    paramFragment = localRequestManagerFragment.getRequestManager();
    paramFragmentManager = paramFragment;
    if (paramFragment == null)
    {
      paramFragmentManager = Glide.get(paramContext);
      paramFragmentManager = factory.build(paramFragmentManager, localRequestManagerFragment.getGlideLifecycle(), localRequestManagerFragment.getRequestManagerTreeNode(), paramContext);
      localRequestManagerFragment.setRequestManager(paramFragmentManager);
    }
    return paramFragmentManager;
  }
  
  private RequestManager getApplicationManager(Context paramContext)
  {
    if (applicationManager == null) {
      try
      {
        if (applicationManager == null)
        {
          Glide localGlide = Glide.get(paramContext.getApplicationContext());
          applicationManager = factory.build(localGlide, new ApplicationLifecycle(), new EmptyRequestManagerTreeNode(), paramContext.getApplicationContext());
        }
      }
      catch (Throwable paramContext)
      {
        throw paramContext;
      }
    }
    return applicationManager;
  }
  
  private RequestManagerFragment getRequestManagerFragment(android.app.FragmentManager paramFragmentManager, android.app.Fragment paramFragment, boolean paramBoolean)
  {
    RequestManagerFragment localRequestManagerFragment2 = (RequestManagerFragment)paramFragmentManager.findFragmentByTag("com.bumptech.glide.manager");
    RequestManagerFragment localRequestManagerFragment1 = localRequestManagerFragment2;
    if (localRequestManagerFragment2 == null)
    {
      localRequestManagerFragment2 = (RequestManagerFragment)pendingRequestManagerFragments.get(paramFragmentManager);
      localRequestManagerFragment1 = localRequestManagerFragment2;
      if (localRequestManagerFragment2 == null)
      {
        localRequestManagerFragment1 = new RequestManagerFragment();
        localRequestManagerFragment1.setParentFragmentHint(paramFragment);
        if (paramBoolean) {
          localRequestManagerFragment1.getGlideLifecycle().onStart();
        }
        pendingRequestManagerFragments.put(paramFragmentManager, localRequestManagerFragment1);
        paramFragmentManager.beginTransaction().add(localRequestManagerFragment1, "com.bumptech.glide.manager").commitAllowingStateLoss();
        handler.obtainMessage(1, paramFragmentManager).sendToTarget();
      }
    }
    return localRequestManagerFragment1;
  }
  
  private SupportRequestManagerFragment getSupportRequestManagerFragment(androidx.fragment.package_5.FragmentManager paramFragmentManager, androidx.fragment.package_5.Fragment paramFragment, boolean paramBoolean)
  {
    SupportRequestManagerFragment localSupportRequestManagerFragment2 = (SupportRequestManagerFragment)paramFragmentManager.findFragmentByTag("com.bumptech.glide.manager");
    SupportRequestManagerFragment localSupportRequestManagerFragment1 = localSupportRequestManagerFragment2;
    if (localSupportRequestManagerFragment2 == null)
    {
      localSupportRequestManagerFragment2 = (SupportRequestManagerFragment)pendingSupportRequestManagerFragments.get(paramFragmentManager);
      localSupportRequestManagerFragment1 = localSupportRequestManagerFragment2;
      if (localSupportRequestManagerFragment2 == null)
      {
        localSupportRequestManagerFragment1 = new SupportRequestManagerFragment();
        localSupportRequestManagerFragment1.setParentFragmentHint(paramFragment);
        if (paramBoolean) {
          localSupportRequestManagerFragment1.getGlideLifecycle().onStart();
        }
        pendingSupportRequestManagerFragments.put(paramFragmentManager, localSupportRequestManagerFragment1);
        paramFragmentManager.beginTransaction().add(localSupportRequestManagerFragment1, "com.bumptech.glide.manager").commitAllowingStateLoss();
        handler.obtainMessage(2, paramFragmentManager).sendToTarget();
      }
    }
    return localSupportRequestManagerFragment1;
  }
  
  private static boolean isActivityVisible(Activity paramActivity)
  {
    return paramActivity.isFinishing() ^ true;
  }
  
  private RequestManager supportFragmentGet(Context paramContext, androidx.fragment.package_5.FragmentManager paramFragmentManager, androidx.fragment.package_5.Fragment paramFragment, boolean paramBoolean)
  {
    SupportRequestManagerFragment localSupportRequestManagerFragment = getSupportRequestManagerFragment(paramFragmentManager, paramFragment, paramBoolean);
    paramFragment = localSupportRequestManagerFragment.getRequestManager();
    paramFragmentManager = paramFragment;
    if (paramFragment == null)
    {
      paramFragmentManager = Glide.get(paramContext);
      paramFragmentManager = factory.build(paramFragmentManager, localSupportRequestManagerFragment.getGlideLifecycle(), localSupportRequestManagerFragment.getRequestManagerTreeNode(), paramContext);
      localSupportRequestManagerFragment.setRequestManager(paramFragmentManager);
    }
    return paramFragmentManager;
  }
  
  public RequestManager get(Activity paramActivity)
  {
    if (Util.isOnBackgroundThread()) {
      return get(paramActivity.getApplicationContext());
    }
    assertNotDestroyed(paramActivity);
    return fragmentGet(paramActivity, paramActivity.getFragmentManager(), null, isActivityVisible(paramActivity));
  }
  
  public RequestManager get(android.app.Fragment paramFragment)
  {
    if (paramFragment.getActivity() != null)
    {
      if ((!Util.isOnBackgroundThread()) && (Build.VERSION.SDK_INT >= 17))
      {
        android.app.FragmentManager localFragmentManager = paramFragment.getChildFragmentManager();
        return fragmentGet(paramFragment.getActivity(), localFragmentManager, paramFragment, paramFragment.isVisible());
      }
      return get(paramFragment.getActivity().getApplicationContext());
    }
    throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
  }
  
  public RequestManager get(Context paramContext)
  {
    if (paramContext != null)
    {
      if ((Util.isOnMainThread()) && (!(paramContext instanceof Application)))
      {
        if ((paramContext instanceof FragmentActivity)) {
          return get((FragmentActivity)paramContext);
        }
        if ((paramContext instanceof Activity)) {
          return get((Activity)paramContext);
        }
        if ((paramContext instanceof ContextWrapper)) {
          return get(((ContextWrapper)paramContext).getBaseContext());
        }
      }
      return getApplicationManager(paramContext);
    }
    throw new IllegalArgumentException("You cannot start a load on a null Context");
  }
  
  public RequestManager get(View paramView)
  {
    if (Util.isOnBackgroundThread()) {
      return get(paramView.getContext().getApplicationContext());
    }
    Preconditions.checkNotNull(paramView);
    Preconditions.checkNotNull(paramView.getContext(), "Unable to obtain a request manager for a view without a Context");
    Activity localActivity = findActivity(paramView.getContext());
    if (localActivity == null) {
      return get(paramView.getContext().getApplicationContext());
    }
    if ((localActivity instanceof FragmentActivity))
    {
      paramView = findSupportFragment(paramView, (FragmentActivity)localActivity);
      if (paramView != null) {
        return get(paramView);
      }
      return get(localActivity);
    }
    paramView = findFragment(paramView, localActivity);
    if (paramView == null) {
      return get(localActivity);
    }
    return get(paramView);
  }
  
  public RequestManager get(androidx.fragment.package_5.Fragment paramFragment)
  {
    Preconditions.checkNotNull(paramFragment.getActivity(), "You cannot start a load on a fragment before it is attached or after it is destroyed");
    if (Util.isOnBackgroundThread()) {
      return get(paramFragment.getActivity().getApplicationContext());
    }
    androidx.fragment.package_5.FragmentManager localFragmentManager = paramFragment.getChildFragmentManager();
    return supportFragmentGet(paramFragment.getActivity(), localFragmentManager, paramFragment, paramFragment.isVisible());
  }
  
  public RequestManager get(FragmentActivity paramFragmentActivity)
  {
    if (Util.isOnBackgroundThread()) {
      return get(paramFragmentActivity.getApplicationContext());
    }
    assertNotDestroyed(paramFragmentActivity);
    return supportFragmentGet(paramFragmentActivity, paramFragmentActivity.getSupportFragmentManager(), null, isActivityVisible(paramFragmentActivity));
  }
  
  RequestManagerFragment getRequestManagerFragment(Activity paramActivity)
  {
    return getRequestManagerFragment(paramActivity.getFragmentManager(), null, isActivityVisible(paramActivity));
  }
  
  SupportRequestManagerFragment getSupportRequestManagerFragment(FragmentActivity paramFragmentActivity)
  {
    return getSupportRequestManagerFragment(paramFragmentActivity.getSupportFragmentManager(), null, isActivityVisible(paramFragmentActivity));
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    int i = what;
    Object localObject1 = null;
    boolean bool = true;
    switch (i)
    {
    default: 
      bool = false;
      Object localObject2 = null;
      paramMessage = (Message)localObject1;
      localObject1 = localObject2;
      break;
    case 2: 
      paramMessage = (androidx.fragment.package_5.FragmentManager)obj;
      localObject1 = pendingSupportRequestManagerFragments.remove(paramMessage);
      break;
    case 1: 
      paramMessage = (android.app.FragmentManager)obj;
      localObject1 = pendingRequestManagerFragments.remove(paramMessage);
    }
    if ((bool) && (localObject1 == null) && (Log.isLoggable("RMRetriever", 5)))
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Failed to remove expected request manager fragment, manager: ");
      ((StringBuilder)localObject1).append(paramMessage);
      Log.w("RMRetriever", ((StringBuilder)localObject1).toString());
    }
    return bool;
  }
  
  public static abstract interface RequestManagerFactory
  {
    public abstract RequestManager build(Glide paramGlide, Lifecycle paramLifecycle, RequestManagerTreeNode paramRequestManagerTreeNode, Context paramContext);
  }
}
