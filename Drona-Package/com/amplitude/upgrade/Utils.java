package com.amplitude.upgrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils
{
  public static final String ACTION_NOTIFICATION_CLICKED = "com.amplitude.api.Utils";
  private static AmplitudeLog logger = ;
  
  public Utils() {}
  
  static JSONObject cloneJSONObject(JSONObject paramJSONObject)
  {
    if (paramJSONObject == null) {
      return null;
    }
    if (paramJSONObject.length() == 0) {
      return new JSONObject();
    }
    Object localObject;
    try
    {
      JSONArray localJSONArray = paramJSONObject.names();
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      logger.e("com.amplitude.api.Utils", localArrayIndexOutOfBoundsException.toString());
      localObject = null;
    }
    int j = 0;
    int i;
    if (localObject != null) {
      i = localObject.length();
    } else {
      i = 0;
    }
    String[] arrayOfString = new String[i];
    while (j < i)
    {
      arrayOfString[j] = localObject.optString(j);
      j += 1;
    }
    try
    {
      paramJSONObject = new JSONObject(paramJSONObject, arrayOfString);
      return paramJSONObject;
    }
    catch (JSONException paramJSONObject)
    {
      logger.e("com.amplitude.api.Utils", paramJSONObject.toString());
    }
    return null;
  }
  
  static boolean compareJSONObjects(JSONObject paramJSONObject1, JSONObject paramJSONObject2)
  {
    if (paramJSONObject1 == paramJSONObject2) {
      return true;
    }
    if ((paramJSONObject1 == null) || (paramJSONObject2 != null))
    {
      if ((paramJSONObject1 == null) && (paramJSONObject2 != null)) {
        return false;
      }
      try
      {
        int i = paramJSONObject1.length();
        int j = paramJSONObject2.length();
        if (i != j) {
          return false;
        }
        Iterator localIterator = paramJSONObject1.keys();
        boolean bool;
        do
        {
          Object localObject1;
          Object localObject2;
          do
          {
            bool = localIterator.hasNext();
            if (!bool) {
              break label191;
            }
            localObject1 = localIterator.next();
            localObject2 = (String)localObject1;
            bool = paramJSONObject2.has((String)localObject2);
            if (!bool) {
              return false;
            }
            localObject1 = paramJSONObject1.get((String)localObject2);
            localObject2 = paramJSONObject2.get((String)localObject2);
            bool = localObject1.getClass().equals(localObject2.getClass());
            if (!bool) {
              return false;
            }
            Class localClass = localObject1.getClass();
            if (localClass != JSONObject.class) {
              break;
            }
            localObject1 = (JSONObject)localObject1;
            localObject2 = (JSONObject)localObject2;
            bool = compareJSONObjects((JSONObject)localObject1, (JSONObject)localObject2);
          } while (bool);
          return false;
          bool = localObject1.equals(localObject2);
        } while (bool);
        return false;
        label191:
        return true;
      }
      catch (JSONException paramJSONObject1) {}
    }
    return false;
  }
  
  static SharedPreferences getAmplitudeSharedPreferences(Context paramContext, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("com.amplitude.api.");
    localStringBuilder.append(paramString);
    localStringBuilder.append(".");
    localStringBuilder.append(paramContext.getPackageName());
    return paramContext.getSharedPreferences(localStringBuilder.toString(), 4);
  }
  
  static String getStringFromSharedPreferences(Context paramContext, String paramString1, String paramString2)
  {
    return getAmplitudeSharedPreferences(paramContext, paramString1).getString(paramString2, null);
  }
  
  public static boolean isEmptyString(String paramString)
  {
    return (paramString == null) || (paramString.length() == 0);
  }
  
  static String normalizeInstanceName(String paramString)
  {
    String str = paramString;
    if (isEmptyString(paramString)) {
      str = "$default_instance";
    }
    return str.toLowerCase();
  }
  
  static void writeStringToSharedPreferences(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    paramContext = getAmplitudeSharedPreferences(paramContext, paramString1).edit();
    paramContext.putString(paramString2, paramString3);
    paramContext.apply();
  }
}
