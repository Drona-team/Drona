package bleshadow.dagger.internal;

import bleshadow.dagger.Lazy;
import bleshadow.javax.inject.Provider;

public final class ProviderOfLazy<T>
  implements Provider<Lazy<T>>
{
  private final Provider<T> provider;
  
  private ProviderOfLazy(Provider paramProvider)
  {
    provider = paramProvider;
  }
  
  public static Provider create(Provider paramProvider)
  {
    return new ProviderOfLazy((Provider)Preconditions.checkNotNull(paramProvider));
  }
  
  public Lazy get()
  {
    return DoubleCheck.lazy(provider);
  }
}
