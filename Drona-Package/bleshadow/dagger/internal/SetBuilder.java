package bleshadow.dagger.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class SetBuilder<T>
{
  private static final String SET_CONTRIBUTIONS_CANNOT_BE_NULL = "Set contributions cannot be null";
  private final List<T> contributions;
  
  private SetBuilder(int paramInt)
  {
    contributions = new ArrayList(paramInt);
  }
  
  public static SetBuilder newSetBuilder(int paramInt)
  {
    return new SetBuilder(paramInt);
  }
  
  public SetBuilder add(Object paramObject)
  {
    contributions.add(Preconditions.checkNotNull(paramObject, "Set contributions cannot be null"));
    return this;
  }
  
  public SetBuilder addAll(Collection paramCollection)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext()) {
      Preconditions.checkNotNull(localIterator.next(), "Set contributions cannot be null");
    }
    contributions.addAll(paramCollection);
    return this;
  }
  
  public Set build()
  {
    switch (contributions.size())
    {
    default: 
      return Collections.unmodifiableSet(new HashSet(contributions));
    case 1: 
      return Collections.singleton(contributions.get(0));
    }
    return Collections.emptySet();
  }
}
