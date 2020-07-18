package com.geektime.rnonesignalandroid;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RNUtils
{
  public RNUtils() {}
  
  public static Collection convertReableArrayIntoStringCollection(ReadableArray paramReadableArray)
  {
    ArrayList localArrayList = new ArrayList();
    paramReadableArray = paramReadableArray.toArrayList().iterator();
    while (paramReadableArray.hasNext())
    {
      Object localObject = paramReadableArray.next();
      if ((localObject instanceof String)) {
        localArrayList.add((String)localObject);
      }
    }
    return localArrayList;
  }
  
  public static WritableArray jsonArrayToWritableArray(JSONArray paramJSONArray)
  {
    WritableNativeArray localWritableNativeArray = new WritableNativeArray();
    if (paramJSONArray == null) {
      return null;
    }
    if (paramJSONArray.length() <= 0) {
      return null;
    }
    int i = 0;
    while (i < paramJSONArray.length())
    {
      try
      {
        Object localObject = paramJSONArray.get(i);
        if (localObject == null)
        {
          localWritableNativeArray.pushNull();
        }
        else if ((localObject instanceof Boolean))
        {
          localObject = (Boolean)localObject;
          localWritableNativeArray.pushBoolean(((Boolean)localObject).booleanValue());
        }
        else if ((localObject instanceof Integer))
        {
          localObject = (Integer)localObject;
          localWritableNativeArray.pushInt(((Integer)localObject).intValue());
        }
        else if ((!(localObject instanceof Double)) && (!(localObject instanceof Long)) && (!(localObject instanceof Float)))
        {
          if ((localObject instanceof String))
          {
            localWritableNativeArray.pushString(localObject.toString());
          }
          else if ((localObject instanceof JSONObject))
          {
            localObject = (JSONObject)localObject;
            localWritableNativeArray.pushMap(jsonToWritableMap((JSONObject)localObject));
          }
          else if ((localObject instanceof JSONArray))
          {
            localObject = (JSONArray)localObject;
            localWritableNativeArray.pushArray(jsonArrayToWritableArray((JSONArray)localObject));
          }
          else
          {
            boolean bool = localObject.getClass().isEnum();
            if (bool) {
              localWritableNativeArray.pushString(localObject.toString());
            }
          }
        }
        else
        {
          localWritableNativeArray.pushDouble(Double.parseDouble(String.valueOf(localObject)));
        }
      }
      catch (JSONException localJSONException)
      {
        for (;;) {}
      }
      i += 1;
    }
    return localWritableNativeArray;
  }
  
  public static WritableMap jsonToWritableMap(JSONObject paramJSONObject)
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    if (paramJSONObject == null) {
      return null;
    }
    Iterator localIterator = paramJSONObject.keys();
    if (!localIterator.hasNext()) {
      return null;
    }
    for (;;)
    {
      String str;
      if (localIterator.hasNext()) {
        str = (String)localIterator.next();
      }
      try
      {
        Object localObject = paramJSONObject.get(str);
        if (localObject == null)
        {
          localWritableNativeMap.putNull(str);
          continue;
        }
        if ((localObject instanceof Boolean))
        {
          localObject = (Boolean)localObject;
          localWritableNativeMap.putBoolean(str, ((Boolean)localObject).booleanValue());
          continue;
        }
        if ((localObject instanceof Integer))
        {
          localObject = (Integer)localObject;
          localWritableNativeMap.putInt(str, ((Integer)localObject).intValue());
          continue;
        }
        if ((!(localObject instanceof Double)) && (!(localObject instanceof Long)) && (!(localObject instanceof Float)))
        {
          if ((localObject instanceof String))
          {
            localWritableNativeMap.putString(str, localObject.toString());
            continue;
          }
          if ((localObject instanceof JSONObject))
          {
            localObject = (JSONObject)localObject;
            localWritableNativeMap.putMap(str, jsonToWritableMap((JSONObject)localObject));
            continue;
          }
          if ((localObject instanceof JSONArray))
          {
            localObject = (JSONArray)localObject;
            localWritableNativeMap.putArray(str, jsonArrayToWritableArray((JSONArray)localObject));
            continue;
          }
          boolean bool = localObject.getClass().isEnum();
          if (!bool) {
            continue;
          }
          localWritableNativeMap.putString(str, localObject.toString());
          continue;
        }
        localWritableNativeMap.putDouble(str, Double.parseDouble(String.valueOf(localObject)));
      }
      catch (JSONException localJSONException) {}
      return localWritableNativeMap;
    }
  }
  
  public static JSONObject readableMapToJson(ReadableMap paramReadableMap)
  {
    JSONObject localJSONObject = new JSONObject();
    if (paramReadableMap == null) {
      return null;
    }
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    if (!localReadableMapKeySetIterator.hasNextKey()) {
      return null;
    }
    for (;;)
    {
      String str;
      ReadableType localReadableType;
      int[] arrayOfInt;
      if (localReadableMapKeySetIterator.hasNextKey())
      {
        str = localReadableMapKeySetIterator.nextKey();
        localReadableType = paramReadableMap.getType(str);
        arrayOfInt = 1.$SwitchMap$com$facebook$react$bridge$ReadableType;
      }
      try
      {
        int i = localReadableType.ordinal();
        switch (arrayOfInt[i])
        {
        default: 
          break;
        case 6: 
          localJSONObject.put(str, paramReadableMap.getArray(str));
          break;
        case 5: 
          localJSONObject.put(str, readableMapToJson(paramReadableMap.getMap(str)));
          break;
        case 4: 
          localJSONObject.put(str, paramReadableMap.getString(str));
          break;
        case 3: 
          localJSONObject.put(str, paramReadableMap.getInt(str));
          break;
        case 2: 
          localJSONObject.put(str, paramReadableMap.getBoolean(str));
          break;
        case 1: 
          localJSONObject.put(str, null);
        }
      }
      catch (JSONException localJSONException) {}
      return localJSONObject;
    }
  }
}
