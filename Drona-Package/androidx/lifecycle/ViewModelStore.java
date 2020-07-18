package androidx.lifecycle;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ViewModelStore
{
  private final HashMap<String, ViewModel> mMap = new HashMap();
  
  public ViewModelStore() {}
  
  final void addChild(String paramString, ViewModel paramViewModel)
  {
    paramString = (ViewModel)mMap.put(paramString, paramViewModel);
    if (paramString != null) {
      paramString.onCleared();
    }
  }
  
  public final void clear()
  {
    Iterator localIterator = mMap.values().iterator();
    while (localIterator.hasNext()) {
      ((ViewModel)localIterator.next()).clear();
    }
    mMap.clear();
  }
  
  final ViewModel getAll(String paramString)
  {
    return (ViewModel)mMap.get(paramString);
  }
  
  Set keys()
  {
    return new HashSet(mMap.keySet());
  }
}
