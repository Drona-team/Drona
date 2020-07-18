package com.google.android.gms.dynamic;

import java.util.Iterator;
import java.util.LinkedList;

final class ContentSource
  implements OnDelegateCreatedListener<T>
{
  ContentSource(DeferredLifecycleHelper paramDeferredLifecycleHelper) {}
  
  public final void onDelegateCreated(LifecycleDelegate paramLifecycleDelegate)
  {
    DeferredLifecycleHelper.xor(zarj, paramLifecycleDelegate);
    paramLifecycleDelegate = DeferredLifecycleHelper.getResults(zarj).iterator();
    while (paramLifecycleDelegate.hasNext()) {
      ((DeferredLifecycleHelper.zaa)paramLifecycleDelegate.next()).makeView(DeferredLifecycleHelper.method_2(zarj));
    }
    DeferredLifecycleHelper.getResults(zarj).clear();
    DeferredLifecycleHelper.getSession(zarj, null);
  }
}
