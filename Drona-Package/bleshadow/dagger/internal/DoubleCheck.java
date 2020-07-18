package bleshadow.dagger.internal;

import bleshadow.dagger.Lazy;
import bleshadow.javax.inject.Provider;

public final class DoubleCheck<T>
  implements Provider<T>, Lazy<T>
{
  private static final Object UNINITIALIZED = new Object();
  private volatile Object instance = UNINITIALIZED;
  private volatile Provider<T> provider;
  
  private DoubleCheck(Provider paramProvider)
  {
    provider = paramProvider;
  }
  
  public static Lazy lazy(Provider paramProvider)
  {
    if ((paramProvider instanceof Lazy)) {
      return (Lazy)paramProvider;
    }
    return new DoubleCheck((Provider)Preconditions.checkNotNull(paramProvider));
  }
  
  public static Provider provider(Provider paramProvider)
  {
    Preconditions.checkNotNull(paramProvider);
    if ((paramProvider instanceof DoubleCheck)) {
      return paramProvider;
    }
    return new DoubleCheck(paramProvider);
  }
  
  public Object get()
  {
    Object localObject1 = instance;
    if (localObject1 == UNINITIALIZED) {
      try
      {
        Object localObject2 = instance;
        localObject1 = localObject2;
        if (localObject2 == UNINITIALIZED)
        {
          localObject2 = provider.get();
          localObject1 = localObject2;
          Object localObject3 = instance;
          if ((localObject3 != UNINITIALIZED) && (localObject3 != localObject2))
          {
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("Scoped provider was invoked recursively returning different results: ");
            ((StringBuilder)localObject1).append(localObject3);
            ((StringBuilder)localObject1).append(" & ");
            ((StringBuilder)localObject1).append(localObject2);
            ((StringBuilder)localObject1).append(". This is likely due to a circular dependency.");
            throw new IllegalStateException(((StringBuilder)localObject1).toString());
          }
          instance = localObject2;
          provider = null;
        }
        return localObject1;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    return localThrowable;
  }
}
