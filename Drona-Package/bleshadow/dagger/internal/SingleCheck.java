package bleshadow.dagger.internal;

import bleshadow.dagger.Lazy;
import bleshadow.javax.inject.Provider;

public final class SingleCheck<T>
  implements Provider<T>, Lazy<T>
{
  private static final Object UNINITIALIZED = new Object();
  private volatile Object instance = UNINITIALIZED;
  private volatile Provider<T> provider;
  
  private SingleCheck(Provider paramProvider)
  {
    provider = paramProvider;
  }
  
  public static Provider provider(Provider paramProvider)
  {
    if (!(paramProvider instanceof SingleCheck))
    {
      if ((paramProvider instanceof DoubleCheck)) {
        return paramProvider;
      }
      return new SingleCheck((Provider)Preconditions.checkNotNull(paramProvider));
    }
    return paramProvider;
  }
  
  public Object get()
  {
    Provider localProvider = provider;
    if (instance == UNINITIALIZED)
    {
      instance = localProvider.get();
      provider = null;
    }
    return instance;
  }
}
