package bleshadow.dagger.internal;

import bleshadow.javax.inject.Provider;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class MapFactory<K, V>
  implements Factory<Map<K, V>>
{
  private static final Provider<Map<Object, Object>> EMPTY = InstanceFactory.create(Collections.emptyMap());
  private final Map<K, Provider<V>> contributingMap;
  
  private MapFactory(Map paramMap)
  {
    contributingMap = Collections.unmodifiableMap(paramMap);
  }
  
  public static Builder builder(int paramInt)
  {
    return new Builder(paramInt, null);
  }
  
  public static Provider emptyMapProvider()
  {
    return EMPTY;
  }
  
  public Map get()
  {
    LinkedHashMap localLinkedHashMap = DaggerCollections.newLinkedHashMapWithExpectedSize(contributingMap.size());
    Iterator localIterator = contributingMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localLinkedHashMap.put(localEntry.getKey(), ((Provider)localEntry.getValue()).get());
    }
    return Collections.unmodifiableMap(localLinkedHashMap);
  }
  
  public static final class Builder<K, V>
  {
    private final LinkedHashMap<K, Provider<V>> mapBuilder;
    
    private Builder(int paramInt)
    {
      mapBuilder = DaggerCollections.newLinkedHashMapWithExpectedSize(paramInt);
    }
    
    public MapFactory build()
    {
      return new MapFactory(mapBuilder, null);
    }
    
    public Builder put(Object paramObject, Provider paramProvider)
    {
      mapBuilder.put(Preconditions.checkNotNull(paramObject, "key"), Preconditions.checkNotNull(paramProvider, "provider"));
      return this;
    }
  }
}
