package bleshadow.dagger.internal;

import bleshadow.javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class SetFactory<T>
  implements Factory<Set<T>>
{
  private static final Factory<Set<Object>> EMPTY_FACTORY = InstanceFactory.create(Collections.emptySet());
  private final List<Provider<Collection<T>>> collectionProviders;
  private final List<Provider<T>> individualProviders;
  
  private SetFactory(List paramList1, List paramList2)
  {
    individualProviders = paramList1;
    collectionProviders = paramList2;
  }
  
  public static Builder builder(int paramInt1, int paramInt2)
  {
    return new Builder(paramInt1, paramInt2, null);
  }
  
  public static Factory empty()
  {
    return EMPTY_FACTORY;
  }
  
  public Set get()
  {
    int j = individualProviders.size();
    ArrayList localArrayList = new ArrayList(collectionProviders.size());
    Object localObject2 = collectionProviders;
    Object localObject1 = this;
    int m = ((List)localObject2).size();
    int k = 0;
    int i = 0;
    while (i < m)
    {
      localObject2 = collectionProviders;
      localObject2 = (Collection)((Provider)((List)localObject2).get(i)).get();
      j += ((Collection)localObject2).size();
      localArrayList.add(localObject2);
      i += 1;
    }
    localObject2 = DaggerCollections.newHashSetWithExpectedSize(j);
    List localList = individualProviders;
    j = localList.size();
    i = 0;
    while (i < j)
    {
      localList = individualProviders;
      ((Set)localObject2).add(Preconditions.checkNotNull(((Provider)localList.get(i)).get()));
      i += 1;
    }
    j = localArrayList.size();
    i = k;
    while (i < j)
    {
      localObject1 = ((Collection)localArrayList.get(i)).iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((Set)localObject2).add(Preconditions.checkNotNull(((Iterator)localObject1).next()));
      }
      i += 1;
    }
    return Collections.unmodifiableSet((Set)localObject2);
  }
  
  public static final class Builder<T>
  {
    private final List<Provider<Collection<T>>> collectionProviders;
    private final List<Provider<T>> individualProviders;
    
    private Builder(int paramInt1, int paramInt2)
    {
      individualProviders = DaggerCollections.presizedList(paramInt1);
      collectionProviders = DaggerCollections.presizedList(paramInt2);
    }
    
    public Builder addCollectionProvider(Provider paramProvider)
    {
      collectionProviders.add(paramProvider);
      return this;
    }
    
    public Builder addProvider(Provider paramProvider)
    {
      individualProviders.add(paramProvider);
      return this;
    }
    
    public SetFactory build()
    {
      return new SetFactory(individualProviders, collectionProviders, null);
    }
  }
}
