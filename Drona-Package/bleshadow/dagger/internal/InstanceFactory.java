package bleshadow.dagger.internal;

import bleshadow.dagger.Lazy;

public final class InstanceFactory<T>
  implements Factory<T>, Lazy<T>
{
  private static final InstanceFactory<Object> NULL_INSTANCE_FACTORY = new InstanceFactory(null);
  private final T instance;
  
  private InstanceFactory(Object paramObject)
  {
    instance = paramObject;
  }
  
  public static Factory create(Object paramObject)
  {
    return new InstanceFactory(Preconditions.checkNotNull(paramObject, "instance cannot be null"));
  }
  
  public static Factory createNullable(Object paramObject)
  {
    if (paramObject == null) {
      return nullInstanceFactory();
    }
    return new InstanceFactory(paramObject);
  }
  
  private static InstanceFactory nullInstanceFactory()
  {
    return NULL_INSTANCE_FACTORY;
  }
  
  public Object get()
  {
    return instance;
  }
}
