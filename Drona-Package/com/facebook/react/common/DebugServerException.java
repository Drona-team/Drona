package com.facebook.react.common;

import android.net.Uri;
import android.text.TextUtils;
import com.facebook.common.logging.FLog;
import org.json.JSONException;
import org.json.JSONObject;

public class DebugServerException
  extends RuntimeException
{
  private static final String GENERIC_ERROR_MESSAGE = "\n\nTry the following to fix the issue:\n? Ensure that the packager server is running\n? Ensure that your device/emulator is connected to your machine and has USB debugging enabled - run 'adb devices' to see a list of connected devices\n? Ensure Airplane Mode is disabled\n? If you're on a physical device connected to the same machine, run 'adb reverse tcp:<PORT> tcp:<PORT>' to forward requests from your device\n? If your device is on the same Wi-Fi network, set 'Debug server host & port for device' in 'Dev settings' to your machine's IP address and the port of the local dev server - e.g. 10.0.1.1:<PORT>\n\n";
  
  public DebugServerException(String paramString)
  {
    super(paramString);
  }
  
  private DebugServerException(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    super(localStringBuilder.toString());
  }
  
  public DebugServerException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public static DebugServerException makeGeneric(String paramString1, String paramString2, String paramString3, Throwable paramThrowable)
  {
    paramString1 = "\n\nTry the following to fix the issue:\n? Ensure that the packager server is running\n? Ensure that your device/emulator is connected to your machine and has USB debugging enabled - run 'adb devices' to see a list of connected devices\n? Ensure Airplane Mode is disabled\n? If you're on a physical device connected to the same machine, run 'adb reverse tcp:<PORT> tcp:<PORT>' to forward requests from your device\n? If your device is on the same Wi-Fi network, set 'Debug server host & port for device' in 'Dev settings' to your machine's IP address and the port of the local dev server - e.g. 10.0.1.1:<PORT>\n\n".replace("<PORT>", String.valueOf(Uri.parse(paramString1).getPort()));
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString2);
    localStringBuilder.append(paramString1);
    localStringBuilder.append(paramString3);
    return new DebugServerException(localStringBuilder.toString(), paramThrowable);
  }
  
  public static DebugServerException makeGeneric(String paramString1, String paramString2, Throwable paramThrowable)
  {
    return makeGeneric(paramString1, paramString2, "", paramThrowable);
  }
  
  public static DebugServerException parse(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2)) {
      return null;
    }
    try
    {
      paramString1 = new JSONObject(paramString2);
      localObject = paramString1.getString("filename");
      paramString1 = new DebugServerException(paramString1.getString("message"), shortenFileName((String)localObject), paramString1.getInt("lineNumber"), paramString1.getInt("column"));
      return paramString1;
    }
    catch (JSONException paramString1)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Could not parse DebugServerException from: ");
      ((StringBuilder)localObject).append(paramString2);
      FLog.w("ReactNative", ((StringBuilder)localObject).toString(), paramString1);
    }
    return null;
  }
  
  private static String shortenFileName(String paramString)
  {
    paramString = paramString.split("/");
    return paramString[(paramString.length - 1)];
  }
}
