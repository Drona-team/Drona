package com.dooboolab.RNIap;

import android.util.Log;
import com.facebook.react.bridge.ObjectAlreadyConsumedException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DoobooUtils
{
  public static final String E_ALREADY_OWNED = "E_ALREADY_OWNED";
  public static final String E_BILLING_RESPONSE_JSON_PARSE_ERROR = "E_BILLING_RESPONSE_JSON_PARSE_ERROR";
  public static final String E_DEVELOPER_ERROR = "E_DEVELOPER_ERROR";
  public static final String E_ITEM_UNAVAILABLE = "E_ITEM_UNAVAILABLE";
  public static final String E_NETWORK_ERROR = "E_NETWORK_ERROR";
  public static final String E_NOT_ENDED = "E_NOT_ENDED";
  public static final String E_NOT_PREPARED = "E_NOT_PREPARED";
  public static final String E_REMOTE_ERROR = "E_REMOTE_ERROR";
  public static final String E_SERVICE_ERROR = "E_SERVICE_ERROR";
  public static final String E_UNKNOWN = "E_UNKNOWN";
  public static final String E_USER_CANCELLED = "E_USER_CANCELLED";
  public static final String E_USER_ERROR = "E_USER_ERROR";
  private static final String PAGE_KEY = "DoobooUtils";
  private static DoobooUtils instance = new DoobooUtils();
  private HashMap<String, ArrayList<Promise>> promises = new HashMap();
  
  public DoobooUtils() {}
  
  public static DoobooUtils getInstance()
  {
    return instance;
  }
  
  public void addPromiseForKey(String paramString, Promise paramPromise)
  {
    Object localObject = promises;
    try
    {
      boolean bool = ((HashMap)localObject).containsKey(paramString);
      if (bool)
      {
        localObject = promises;
        paramString = ((HashMap)localObject).get(paramString);
        paramString = (ArrayList)paramString;
      }
      else
      {
        localObject = new ArrayList();
        HashMap localHashMap = promises;
        localHashMap.put(paramString, localObject);
        paramString = (String)localObject;
      }
      paramString.add(paramPromise);
      return;
    }
    catch (ObjectAlreadyConsumedException paramString)
    {
      Log.e("DoobooUtils", paramString.getMessage());
    }
  }
  
  public JSONArray convertArrayToJson(ReadableArray paramReadableArray)
    throws JSONException
  {
    JSONArray localJSONArray = new JSONArray();
    int i = 0;
    while (i < paramReadableArray.size())
    {
      switch (1.$SwitchMap$com$facebook$react$bridge$ReadableType[paramReadableArray.getType(i).ordinal()])
      {
      default: 
        break;
      case 6: 
        localJSONArray.put(convertArrayToJson(paramReadableArray.getArray(i)));
        break;
      case 5: 
        localJSONArray.put(convertMapToJson(paramReadableArray.getMap(i)));
        break;
      case 4: 
        localJSONArray.put(paramReadableArray.getString(i));
        break;
      case 3: 
        localJSONArray.put(paramReadableArray.getDouble(i));
        break;
      case 2: 
        localJSONArray.put(paramReadableArray.getBoolean(i));
      }
      i += 1;
    }
    return localJSONArray;
  }
  
  public WritableArray convertJsonToArray(JSONArray paramJSONArray)
    throws JSONException
  {
    WritableNativeArray localWritableNativeArray = new WritableNativeArray();
    int i = 0;
    while (i < paramJSONArray.length())
    {
      Object localObject = paramJSONArray.get(i);
      if ((localObject instanceof JSONObject)) {
        localWritableNativeArray.pushMap(convertJsonToMap((JSONObject)localObject));
      } else if ((localObject instanceof JSONArray)) {
        localWritableNativeArray.pushArray(convertJsonToArray((JSONArray)localObject));
      } else if ((localObject instanceof Boolean)) {
        localWritableNativeArray.pushBoolean(((Boolean)localObject).booleanValue());
      } else if ((localObject instanceof Integer)) {
        localWritableNativeArray.pushInt(((Integer)localObject).intValue());
      } else if ((localObject instanceof Double)) {
        localWritableNativeArray.pushDouble(((Double)localObject).doubleValue());
      } else if ((localObject instanceof String)) {
        localWritableNativeArray.pushString((String)localObject);
      } else {
        localWritableNativeArray.pushString(localObject.toString());
      }
      i += 1;
    }
    return localWritableNativeArray;
  }
  
  public WritableMap convertJsonToMap(JSONObject paramJSONObject)
    throws JSONException
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    Iterator localIterator = paramJSONObject.keys();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = paramJSONObject.get(str);
      if ((localObject instanceof JSONObject)) {
        localWritableNativeMap.putMap(str, convertJsonToMap((JSONObject)localObject));
      } else if ((localObject instanceof JSONArray)) {
        localWritableNativeMap.putArray(str, convertJsonToArray((JSONArray)localObject));
      } else if ((localObject instanceof Boolean)) {
        localWritableNativeMap.putBoolean(str, ((Boolean)localObject).booleanValue());
      } else if ((localObject instanceof Integer)) {
        localWritableNativeMap.putInt(str, ((Integer)localObject).intValue());
      } else if ((localObject instanceof Double)) {
        localWritableNativeMap.putDouble(str, ((Double)localObject).doubleValue());
      } else if ((localObject instanceof String)) {
        localWritableNativeMap.putString(str, (String)localObject);
      } else {
        localWritableNativeMap.putString(str, localObject.toString());
      }
    }
    return localWritableNativeMap;
  }
  
  public JSONObject convertMapToJson(ReadableMap paramReadableMap)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    while (localReadableMapKeySetIterator.hasNextKey())
    {
      String str = localReadableMapKeySetIterator.nextKey();
      switch (1.$SwitchMap$com$facebook$react$bridge$ReadableType[paramReadableMap.getType(str).ordinal()])
      {
      default: 
        break;
      case 6: 
        localJSONObject.put(str, convertArrayToJson(paramReadableMap.getArray(str)));
        break;
      case 5: 
        localJSONObject.put(str, convertMapToJson(paramReadableMap.getMap(str)));
        break;
      case 4: 
        localJSONObject.put(str, paramReadableMap.getString(str));
        break;
      case 3: 
        localJSONObject.put(str, paramReadableMap.getDouble(str));
        break;
      case 2: 
        localJSONObject.put(str, paramReadableMap.getBoolean(str));
        break;
      case 1: 
        localJSONObject.put(str, JSONObject.NULL);
      }
    }
    return localJSONObject;
  }
  
  public String[] getBillingResponseData(int paramInt)
  {
    String[] arrayOfString = new String[2];
    switch (paramInt)
    {
    default: 
      arrayOfString[0] = "E_UNKNOWN";
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Purchase failed with code: ");
      localStringBuilder.append(paramInt);
      arrayOfString[1] = localStringBuilder.toString();
      break;
    case 7: 
      arrayOfString[0] = "E_ALREADY_OWNED";
      arrayOfString[1] = "You already own this item.";
      break;
    case 6: 
      arrayOfString[0] = "E_UNKNOWN";
      arrayOfString[1] = "An unknown or unexpected error has occured. Please try again later.";
      break;
    case 5: 
      arrayOfString[0] = "E_DEVELOPER_ERROR";
      arrayOfString[1] = "Google is indicating that we have some issue connecting to payment.";
      break;
    case 4: 
      arrayOfString[0] = "E_ITEM_UNAVAILABLE";
      arrayOfString[1] = "That item is unavailable.";
      break;
    case 3: 
      arrayOfString[0] = "E_SERVICE_ERROR";
      arrayOfString[1] = "Billing is unavailable. This may be a problem with your device, or the Play Store may be down.";
      break;
    case 2: 
      arrayOfString[0] = "E_SERVICE_ERROR";
      arrayOfString[1] = "The service is unreachable. This may be your internet connection, or the Play Store may be down.";
      break;
    case 1: 
      arrayOfString[0] = "E_USER_CANCELLED";
      arrayOfString[1] = "Payment is Cancelled.";
      break;
    case 0: 
      arrayOfString[0] = "OK";
      arrayOfString[1] = "";
      break;
    case -1: 
      arrayOfString[0] = "E_NETWORK_ERROR";
      arrayOfString[1] = "The service is disconnected (check your internet connection.)";
      break;
    case -2: 
      arrayOfString[0] = "E_SERVICE_ERROR";
      arrayOfString[1] = "This feature is not available on your device.";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Error Code : ");
    localStringBuilder.append(paramInt);
    Log.e("DoobooUtils", localStringBuilder.toString());
    return arrayOfString;
  }
  
  public void rejectPromiseWithBillingError(Promise paramPromise, int paramInt)
  {
    String[] arrayOfString = getBillingResponseData(paramInt);
    paramPromise.reject(arrayOfString[0], arrayOfString[1]);
  }
  
  public void rejectPromisesForKey(String paramString1, String paramString2, String paramString3, Exception paramException)
  {
    Object localObject1 = promises;
    try
    {
      boolean bool = ((HashMap)localObject1).containsKey(paramString1);
      if (bool)
      {
        localObject1 = promises;
        localObject1 = ((HashMap)localObject1).get(paramString1);
        localObject1 = (ArrayList)localObject1;
        localObject1 = ((ArrayList)localObject1).iterator();
        for (;;)
        {
          bool = ((Iterator)localObject1).hasNext();
          if (!bool) {
            break;
          }
          Object localObject2 = ((Iterator)localObject1).next();
          localObject2 = (Promise)localObject2;
          ((Promise)localObject2).reject(paramString2, paramString3, paramException);
        }
        paramString2 = promises;
        paramString2.remove(paramString1);
        return;
      }
    }
    catch (ObjectAlreadyConsumedException paramString1)
    {
      Log.e("DoobooUtils", paramString1.getMessage());
    }
  }
  
  public void rejectPromisesWithBillingError(String paramString, int paramInt)
  {
    Object localObject1 = promises;
    try
    {
      boolean bool = ((HashMap)localObject1).containsKey(paramString);
      if (bool)
      {
        localObject1 = promises;
        localObject1 = ((HashMap)localObject1).get(paramString);
        localObject1 = (ArrayList)localObject1;
        localObject1 = ((ArrayList)localObject1).iterator();
        for (;;)
        {
          bool = ((Iterator)localObject1).hasNext();
          if (!bool) {
            break;
          }
          Object localObject2 = ((Iterator)localObject1).next();
          localObject2 = (Promise)localObject2;
          rejectPromiseWithBillingError((Promise)localObject2, paramInt);
        }
        localObject1 = promises;
        ((HashMap)localObject1).remove(paramString);
        return;
      }
    }
    catch (ObjectAlreadyConsumedException paramString)
    {
      Log.e("DoobooUtils", paramString.getMessage());
    }
  }
  
  public void resolvePromisesForKey(String paramString, Object paramObject)
  {
    Object localObject1 = promises;
    try
    {
      boolean bool = ((HashMap)localObject1).containsKey(paramString);
      if (bool)
      {
        localObject1 = promises;
        localObject1 = ((HashMap)localObject1).get(paramString);
        localObject1 = (ArrayList)localObject1;
        localObject1 = ((ArrayList)localObject1).iterator();
        for (;;)
        {
          bool = ((Iterator)localObject1).hasNext();
          if (!bool) {
            break;
          }
          Object localObject2 = ((Iterator)localObject1).next();
          localObject2 = (Promise)localObject2;
          ((Promise)localObject2).resolve(paramObject);
        }
        paramObject = promises;
        paramObject.remove(paramString);
        return;
      }
    }
    catch (ObjectAlreadyConsumedException paramString)
    {
      Log.e("DoobooUtils", paramString.getMessage());
    }
  }
}