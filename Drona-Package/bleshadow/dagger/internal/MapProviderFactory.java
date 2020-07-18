package bleshadow.dagger.internal;

import bleshadow.dagger.Lazy;
import bleshadow.javax.inject.Provider;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MapProviderFactory<K, V>
  implements Factory<Map<K, Provider<V>>>, Lazy<Map<K, Provider<V>>>
{
  private final Map<K, Provider<V>> contributingMap;
  
  private MapProviderFactory(Map paramMap)
  {
    contributingMap = Collections.unmodifiableMap(paramMap);
  }
  
  public static Builder builder(int paramInt)
  {
    return new Builder(paramInt, null);
  }
  
  public Map get()
  {
    return contributingMap;
  }
  
  public static final class Builder<K, V>
  {
    private final LinkedHashMap<K, Provider<V>> mapBuilder;
    
    private Builder(int paramInt)
    {
      mapBuilder = DaggerCollections.newLinkedHashMapWithExpectedSize(paramInt);
    }
    
    public MapProviderFactory build()
    {
      return new MapProviderFactory(mapBuilder, null);
    }
    
    public Builder put(Object paramObject, Provider paramProvider)
    {
      mapBuilder.put(Preconditions.checkNotNull(paramObject, "key"), Preconditions.checkNotNull(paramProvider, "provider"));
      return this;
    }
  }
}
