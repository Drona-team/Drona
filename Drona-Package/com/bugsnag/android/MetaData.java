package com.bugsnag.android;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MetaData
  extends Observable
  implements JsonStream.Streamable
{
  final ObjectJsonStreamer jsonStreamer;
  @NonNull
  final Map<String, Object> store;
  
  public MetaData()
  {
    this(new ConcurrentHashMap());
  }
  
  public MetaData(Map paramMap)
  {
    store = new ConcurrentHashMap(paramMap);
    jsonStreamer = new ObjectJsonStreamer();
  }
  
  static MetaData merge(MetaData... paramVarArgs)
  {
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList1 = new ArrayList();
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      MetaData localMetaData = paramVarArgs[i];
      if (localMetaData != null)
      {
        localArrayList2.add(store);
        if (jsonStreamer.filters != null) {
          localArrayList1.addAll(Arrays.asList(jsonStreamer.filters));
        }
      }
      i += 1;
    }
    paramVarArgs = new MetaData(mergeMaps((Map[])localArrayList2.toArray(new Map[0])));
    paramVarArgs.setFilters((String[])localArrayList1.toArray(new String[0]));
    return paramVarArgs;
  }
  
  private static Map mergeMaps(Map... paramVarArgs)
  {
    ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      Map localMap = paramVarArgs[i];
      if (localMap != null)
      {
        Object localObject1 = new HashSet(localConcurrentHashMap.keySet());
        ((Set)localObject1).addAll(localMap.keySet());
        localObject1 = ((Set)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          String str = (String)((Iterator)localObject1).next();
          Object localObject2 = localConcurrentHashMap.get(str);
          Object localObject3 = localMap.get(str);
          if (localObject3 != null)
          {
            if (((localObject2 instanceof Map)) && ((localObject3 instanceof Map))) {
              localConcurrentHashMap.put(str, mergeMaps(new Map[] { (Map)localObject2, (Map)localObject3 }));
            } else {
              localConcurrentHashMap.put(str, localObject3);
            }
          }
          else if (localObject2 != null) {
            localConcurrentHashMap.put(str, localObject2);
          }
        }
      }
      i += 1;
    }
    return localConcurrentHashMap;
  }
  
  public void addToTab(String paramString1, String paramString2, Object paramObject)
  {
    Map localMap = getTab(paramString1);
    setChanged();
    if (paramObject != null)
    {
      localMap.put(paramString2, paramObject);
      notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.ADD_METADATA, Arrays.asList(new Object[] { paramString1, paramString2, paramObject })));
      return;
    }
    localMap.remove(paramString2);
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.REMOVE_METADATA, Arrays.asList(new String[] { paramString1, paramString2 })));
  }
  
  public void clearTab(String paramString)
  {
    store.remove(paramString);
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.CLEAR_METADATA_TAB, paramString));
  }
  
  String[] getFilters()
  {
    return jsonStreamer.filters;
  }
  
  Map getTab(String paramString)
  {
    Object localObject = (Map)store.get(paramString);
    if (localObject == null)
    {
      localObject = new ConcurrentHashMap();
      store.put(paramString, localObject);
      return localObject;
    }
    return localObject;
  }
  
  void setFilters(String... paramVarArgs)
  {
    jsonStreamer.filters = paramVarArgs;
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    jsonStreamer.objectToStream(store, paramJsonStream);
  }
}
