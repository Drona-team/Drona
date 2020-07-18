package bleshadow.dagger.internal;

import bleshadow.javax.inject.Provider;

public final class DelegateFactory<T>
  implements Factory<T>
{
  private Provider<T> delegate;
  
  public DelegateFactory() {}
  
  public Object get()
  {
    if (delegate != null) {
      return delegate.get();
    }
    throw new IllegalStateException();
  }
  
  public void setDelegatedProvider(Provider paramProvider)
  {
    if (paramProvider != null)
    {
      if (delegate == null)
      {
        delegate = paramProvider;
        return;
      }
      throw new IllegalStateException();
    }
    throw new IllegalArgumentException();
  }
}
