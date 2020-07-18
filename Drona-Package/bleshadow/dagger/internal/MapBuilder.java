package bleshadow.dagger.internal;

import java.util.Collections;
import java.util.Map;

public final class MapBuilder<K, V>
{
  private final Map<K, V> contributions;
  
  private MapBuilder(int paramInt)
  {
    contributions = DaggerCollections.newLinkedHashMapWithExpectedSize(paramInt);
  }
  
  public static MapBuilder newMapBuilder(int paramInt)
  {
    return new MapBuilder(paramInt);
  }
  
  public Map build()
  {
    if (contributions.size() != 0) {
      return Collections.unmodifiableMap(contributions);
    }
    return Collections.emptyMap();
  }
  
  public MapBuilder set(Object paramObject1, Object paramObject2)
  {
    contributions.put(paramObject1, paramObject2);
    return this;
  }
}
