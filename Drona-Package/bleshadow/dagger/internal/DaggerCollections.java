package bleshadow.dagger.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public final class DaggerCollections
{
  private static final int MAX_POWER_OF_TWO = 1073741824;
  
  private DaggerCollections() {}
  
  private static int calculateInitialCapacity(int paramInt)
  {
    if (paramInt < 3) {
      return paramInt + 1;
    }
    if (paramInt < 1073741824) {
      return (int)(paramInt / 0.75F + 1.0F);
    }
    return Integer.MAX_VALUE;
  }
  
  public static boolean hasDuplicates(List paramList)
  {
    if (paramList.size() < 2) {
      return false;
    }
    HashSet localHashSet = new HashSet(paramList);
    return paramList.size() != localHashSet.size();
  }
  
  static HashSet newHashSetWithExpectedSize(int paramInt)
  {
    return new HashSet(calculateInitialCapacity(paramInt));
  }
  
  static LinkedHashMap newLinkedHashMapWithExpectedSize(int paramInt)
  {
    return new LinkedHashMap(calculateInitialCapacity(paramInt));
  }
  
  public static List presizedList(int paramInt)
  {
    if (paramInt == 0) {
      return Collections.emptyList();
    }
    return new ArrayList(paramInt);
  }
}
