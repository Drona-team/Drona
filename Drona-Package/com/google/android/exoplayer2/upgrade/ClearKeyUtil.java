package com.google.android.exoplayer2.upgrade;

import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final class ClearKeyUtil
{
  private static final String PAGE_KEY = "ClearKeyUtil";
  
  private ClearKeyUtil() {}
  
  public static byte[] adjustRequestData(byte[] paramArrayOfByte)
  {
    if (Util.SDK_INT >= 27) {
      return paramArrayOfByte;
    }
    return Util.getUtf8Bytes(base64ToBase64Url(Util.fromUtf8Bytes(paramArrayOfByte)));
  }
  
  public static byte[] adjustResponseData(byte[] paramArrayOfByte)
  {
    if (Util.SDK_INT >= 27) {
      return paramArrayOfByte;
    }
    try
    {
      localObject2 = new JSONObject(Util.fromUtf8Bytes(paramArrayOfByte));
      Object localObject1 = new StringBuilder("{\"keys\":[");
      localObject2 = ((JSONObject)localObject2).getJSONArray("keys");
      int i = 0;
      for (;;)
      {
        int j = ((JSONArray)localObject2).length();
        if (i >= j) {
          break;
        }
        if (i != 0) {
          ((StringBuilder)localObject1).append(",");
        }
        JSONObject localJSONObject = ((JSONArray)localObject2).getJSONObject(i);
        ((StringBuilder)localObject1).append("{\"k\":\"");
        ((StringBuilder)localObject1).append(base64UrlToBase64(localJSONObject.getString("k")));
        ((StringBuilder)localObject1).append("\",\"kid\":\"");
        ((StringBuilder)localObject1).append(base64UrlToBase64(localJSONObject.getString("kid")));
        ((StringBuilder)localObject1).append("\",\"kty\":\"");
        ((StringBuilder)localObject1).append(localJSONObject.getString("kty"));
        ((StringBuilder)localObject1).append("\"}");
        i += 1;
      }
      ((StringBuilder)localObject1).append("]}");
      localObject1 = Util.getUtf8Bytes(((StringBuilder)localObject1).toString());
      return localObject1;
    }
    catch (JSONException localJSONException)
    {
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("Failed to adjust response data: ");
      ((StringBuilder)localObject2).append(Util.fromUtf8Bytes(paramArrayOfByte));
      Log.e("ClearKeyUtil", ((StringBuilder)localObject2).toString(), localJSONException);
    }
    return paramArrayOfByte;
  }
  
  private static String base64ToBase64Url(String paramString)
  {
    return paramString.replace('+', '-').replace('/', '_');
  }
  
  private static String base64UrlToBase64(String paramString)
  {
    return paramString.replace('-', '+').replace('_', '/');
  }
}
