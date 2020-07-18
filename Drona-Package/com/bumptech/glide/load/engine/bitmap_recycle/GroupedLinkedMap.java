package com.bumptech.glide.load.engine.bitmap_recycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GroupedLinkedMap<K extends Poolable, V>
{
  private final LinkedEntry<K, V> head = new LinkedEntry();
  private final Map<K, LinkedEntry<K, V>> keyToEntry = new HashMap();
  
  GroupedLinkedMap() {}
  
  private void makeHead(LinkedEntry paramLinkedEntry)
  {
    removeEntry(paramLinkedEntry);
    prev = head;
    next = head.next;
    updateEntry(paramLinkedEntry);
  }
  
  private void makeTail(LinkedEntry paramLinkedEntry)
  {
    removeEntry(paramLinkedEntry);
    prev = head.prev;
    next = head;
    updateEntry(paramLinkedEntry);
  }
  
  private static void removeEntry(LinkedEntry paramLinkedEntry)
  {
    prev.next = next;
    next.prev = prev;
  }
  
  private static void updateEntry(LinkedEntry paramLinkedEntry)
  {
    next.prev = paramLinkedEntry;
    prev.next = paramLinkedEntry;
  }
  
  public Object get(Poolable paramPoolable)
  {
    LinkedEntry localLinkedEntry = (LinkedEntry)keyToEntry.get(paramPoolable);
    if (localLinkedEntry == null)
    {
      localLinkedEntry = new LinkedEntry(paramPoolable);
      keyToEntry.put(paramPoolable, localLinkedEntry);
      paramPoolable = localLinkedEntry;
    }
    else
    {
      paramPoolable.offer();
      paramPoolable = localLinkedEntry;
    }
    makeHead(paramPoolable);
    return paramPoolable.removeLast();
  }
  
  public void put(Poolable paramPoolable, Object paramObject)
  {
    LinkedEntry localLinkedEntry = (LinkedEntry)keyToEntry.get(paramPoolable);
    if (localLinkedEntry == null)
    {
      localLinkedEntry = new LinkedEntry(paramPoolable);
      makeTail(localLinkedEntry);
      keyToEntry.put(paramPoolable, localLinkedEntry);
      paramPoolable = localLinkedEntry;
    }
    else
    {
      paramPoolable.offer();
      paramPoolable = localLinkedEntry;
    }
    paramPoolable.add(paramObject);
  }
  
  public Object removeLast()
  {
    for (LinkedEntry localLinkedEntry = head.prev; !localLinkedEntry.equals(head); localLinkedEntry = prev)
    {
      Object localObject = localLinkedEntry.removeLast();
      if (localObject != null) {
        return localObject;
      }
      removeEntry(localLinkedEntry);
      keyToEntry.remove(key);
      ((Poolable)key).offer();
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("GroupedLinkedMap( ");
    LinkedEntry localLinkedEntry = head.next;
    int i = 0;
    while (!localLinkedEntry.equals(head))
    {
      i = 1;
      localStringBuilder.append('{');
      localStringBuilder.append(key);
      localStringBuilder.append(':');
      localStringBuilder.append(localLinkedEntry.size());
      localStringBuilder.append("}, ");
      localLinkedEntry = next;
    }
    if (i != 0) {
      localStringBuilder.delete(localStringBuilder.length() - 2, localStringBuilder.length());
    }
    localStringBuilder.append(" )");
    return localStringBuilder.toString();
  }
  
  private static class LinkedEntry<K, V>
  {
    final K key;
    LinkedEntry<K, V> next = this;
    LinkedEntry<K, V> prev = this;
    private List<V> values;
    
    LinkedEntry()
    {
      this(null);
    }
    
    LinkedEntry(Object paramObject)
    {
      key = paramObject;
    }
    
    public void add(Object paramObject)
    {
      if (values == null) {
        values = new ArrayList();
      }
      values.add(paramObject);
    }
    
    public Object removeLast()
    {
      int i = size();
      if (i > 0) {
        return values.remove(i - 1);
      }
      return null;
    }
    
    public int size()
    {
      if (values != null) {
        return values.size();
      }
      return 0;
    }
  }
}
