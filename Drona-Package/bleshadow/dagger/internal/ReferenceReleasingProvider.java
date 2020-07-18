package bleshadow.dagger.internal;

import bleshadow.javax.inject.Provider;
import java.lang.ref.WeakReference;

@GwtIncompatible
public final class ReferenceReleasingProvider<T>
  implements Provider<T>
{
  private static final Object NULL = new Object();
  private final Provider<T> provider;
  private volatile Object strongReference;
  private volatile WeakReference<T> weakReference;
  
  private ReferenceReleasingProvider(Provider paramProvider)
  {
    provider = paramProvider;
  }
  
  public static ReferenceReleasingProvider create(Provider paramProvider, ReferenceReleasingProviderManager paramReferenceReleasingProviderManager)
  {
    paramProvider = new ReferenceReleasingProvider((Provider)Preconditions.checkNotNull(paramProvider));
    paramReferenceReleasingProviderManager.addProvider(paramProvider);
    return paramProvider;
  }
  
  private Object currentValue()
  {
    Object localObject = strongReference;
    if (localObject != null) {
      return localObject;
    }
    if (weakReference != null) {
      return weakReference.get();
    }
    return null;
  }
  
  public Object get()
  {
    Object localObject2 = currentValue();
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      try
      {
        localObject2 = currentValue();
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject2 = provider.get();
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = NULL;
          }
          strongReference = localObject1;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    if (localThrowable == NULL) {
      return null;
    }
    return localThrowable;
  }
  
  public void releaseStrongReference()
  {
    Object localObject = strongReference;
    if ((localObject != null) && (localObject != NULL)) {
      try
      {
        weakReference = new WeakReference(localObject);
        strongReference = null;
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  public void restoreStrongReference()
  {
    Object localObject = strongReference;
    if ((weakReference != null) && (localObject == null)) {
      try
      {
        localObject = strongReference;
        if ((weakReference != null) && (localObject == null))
        {
          localObject = weakReference.get();
          if (localObject != null)
          {
            strongReference = localObject;
            weakReference = null;
          }
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
}
