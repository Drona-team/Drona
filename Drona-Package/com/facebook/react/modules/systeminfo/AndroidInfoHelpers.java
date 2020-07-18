package com.facebook.react.modules.systeminfo;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;
import com.facebook.react.R.integer;
import java.util.Locale;

public class AndroidInfoHelpers
{
  public static final String DEVICE_LOCALHOST = "localhost";
  public static final String EMULATOR_LOCALHOST = "10.0.2.2";
  public static final String GENYMOTION_LOCALHOST = "10.0.3.2";
  public static final String METRO_HOST_PROP_NAME = "metro.host";
  private static final String mLogTag = "AndroidInfoHelpers";
  private static String metroHostPropValue;
  
  public AndroidInfoHelpers() {}
  
  public static String getAdbReverseTcpCommand(Context paramContext)
  {
    return getAdbReverseTcpCommand(getDevServerPort(paramContext));
  }
  
  public static String getAdbReverseTcpCommand(Integer paramInteger)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("adb reverse tcp:");
    localStringBuilder.append(paramInteger);
    localStringBuilder.append(" tcp:");
    localStringBuilder.append(paramInteger);
    return localStringBuilder.toString();
  }
  
  private static Integer getDevServerPort(Context paramContext)
  {
    return Integer.valueOf(paramContext.getResources().getInteger(R.integer.react_native_dev_server_port));
  }
  
  public static String getFriendlyDeviceName()
  {
    if (isRunningOnGenymotion()) {
      return Build.MODEL;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(Build.MODEL);
    localStringBuilder.append(" - ");
    localStringBuilder.append(Build.VERSION.RELEASE);
    localStringBuilder.append(" - API ");
    localStringBuilder.append(Build.VERSION.SDK_INT);
    return localStringBuilder.toString();
  }
  
  public static String getInspectorProxyHost(Context paramContext)
  {
    return getServerIpAddress(getInspectorProxyPort(paramContext).intValue());
  }
  
  private static Integer getInspectorProxyPort(Context paramContext)
  {
    return Integer.valueOf(paramContext.getResources().getInteger(R.integer.react_native_dev_server_port));
  }
  
  /* Error */
  private static String getMetroHostPropValue()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 122	com/facebook/react/modules/systeminfo/AndroidInfoHelpers:metroHostPropValue	Ljava/lang/String;
    //   6: ifnull +12 -> 18
    //   9: getstatic 122	com/facebook/react/modules/systeminfo/AndroidInfoHelpers:metroHostPropValue	Ljava/lang/String;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: aconst_null
    //   19: astore_2
    //   20: aconst_null
    //   21: astore_1
    //   22: aconst_null
    //   23: astore 5
    //   25: invokestatic 128	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
    //   28: astore_0
    //   29: aload_0
    //   30: iconst_2
    //   31: anewarray 130	java/lang/String
    //   34: dup
    //   35: iconst_0
    //   36: ldc -124
    //   38: aastore
    //   39: dup
    //   40: iconst_1
    //   41: ldc 17
    //   43: aastore
    //   44: invokevirtual 136	java/lang/Runtime:exec	([Ljava/lang/String;)Ljava/lang/Process;
    //   47: astore 4
    //   49: aload 4
    //   51: astore_0
    //   52: aload_2
    //   53: astore_1
    //   54: aload_0
    //   55: astore_2
    //   56: new 138	java/io/BufferedReader
    //   59: dup
    //   60: new 140	java/io/InputStreamReader
    //   63: dup
    //   64: aload 4
    //   66: invokevirtual 146	java/lang/Process:getInputStream	()Ljava/io/InputStream;
    //   69: ldc -108
    //   71: invokestatic 154	java/nio/charset/Charset:forName	(Ljava/lang/String;)Ljava/nio/charset/Charset;
    //   74: invokespecial 157	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
    //   77: invokespecial 160	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   80: astore_3
    //   81: ldc -94
    //   83: astore_1
    //   84: aload_3
    //   85: invokevirtual 165	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   88: astore_2
    //   89: aload_2
    //   90: ifnull +8 -> 98
    //   93: aload_2
    //   94: astore_1
    //   95: goto -11 -> 84
    //   98: aload_1
    //   99: putstatic 122	com/facebook/react/modules/systeminfo/AndroidInfoHelpers:metroHostPropValue	Ljava/lang/String;
    //   102: aload_3
    //   103: invokevirtual 168	java/io/BufferedReader:close	()V
    //   106: aload 4
    //   108: ifnull +82 -> 190
    //   111: aload_0
    //   112: invokevirtual 171	java/lang/Process:destroy	()V
    //   115: goto +75 -> 190
    //   118: astore_2
    //   119: aload_3
    //   120: astore_1
    //   121: aload_2
    //   122: astore_3
    //   123: goto +79 -> 202
    //   126: astore 4
    //   128: goto +24 -> 152
    //   131: astore 4
    //   133: aload 5
    //   135: astore_3
    //   136: goto +16 -> 152
    //   139: astore_3
    //   140: aconst_null
    //   141: astore_0
    //   142: goto +60 -> 202
    //   145: astore 4
    //   147: aconst_null
    //   148: astore_0
    //   149: aload 5
    //   151: astore_3
    //   152: aload_3
    //   153: astore_1
    //   154: aload_0
    //   155: astore_2
    //   156: getstatic 173	com/facebook/react/modules/systeminfo/AndroidInfoHelpers:mLogTag	Ljava/lang/String;
    //   159: ldc -81
    //   161: aload 4
    //   163: invokestatic 181	com/facebook/common/logging/FLog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   166: aload_3
    //   167: astore_1
    //   168: aload_0
    //   169: astore_2
    //   170: ldc -94
    //   172: putstatic 122	com/facebook/react/modules/systeminfo/AndroidInfoHelpers:metroHostPropValue	Ljava/lang/String;
    //   175: aload_3
    //   176: ifnull +7 -> 183
    //   179: aload_3
    //   180: invokevirtual 168	java/io/BufferedReader:close	()V
    //   183: aload_0
    //   184: ifnull +6 -> 190
    //   187: goto -76 -> 111
    //   190: getstatic 122	com/facebook/react/modules/systeminfo/AndroidInfoHelpers:metroHostPropValue	Ljava/lang/String;
    //   193: astore_0
    //   194: ldc 2
    //   196: monitorexit
    //   197: aload_0
    //   198: areturn
    //   199: astore_3
    //   200: aload_2
    //   201: astore_0
    //   202: aload_1
    //   203: ifnull +7 -> 210
    //   206: aload_1
    //   207: invokevirtual 168	java/io/BufferedReader:close	()V
    //   210: aload_0
    //   211: ifnull +7 -> 218
    //   214: aload_0
    //   215: invokevirtual 171	java/lang/Process:destroy	()V
    //   218: aload_3
    //   219: athrow
    //   220: astore_0
    //   221: ldc 2
    //   223: monitorexit
    //   224: aload_0
    //   225: athrow
    //   226: astore_1
    //   227: goto -121 -> 106
    //   230: astore_1
    //   231: goto -48 -> 183
    //   234: astore_1
    //   235: goto -25 -> 210
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	203	0	localObject1	Object
    //   220	5	0	localThrowable1	Throwable
    //   21	186	1	localObject2	Object
    //   226	1	1	localException1	Exception
    //   230	1	1	localException2	Exception
    //   234	1	1	localException3	Exception
    //   19	75	2	localObject3	Object
    //   118	4	2	localThrowable2	Throwable
    //   155	46	2	localObject4	Object
    //   80	56	3	localObject5	Object
    //   139	1	3	localThrowable3	Throwable
    //   151	29	3	localObject6	Object
    //   199	20	3	localThrowable4	Throwable
    //   47	60	4	localProcess	Process
    //   126	1	4	localException4	Exception
    //   131	1	4	localException5	Exception
    //   145	17	4	localException6	Exception
    //   23	127	5	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   84	89	118	java/lang/Throwable
    //   98	102	118	java/lang/Throwable
    //   84	89	126	java/lang/Exception
    //   56	81	131	java/lang/Exception
    //   25	29	139	java/lang/Throwable
    //   29	49	139	java/lang/Throwable
    //   25	29	145	java/lang/Exception
    //   29	49	145	java/lang/Exception
    //   56	81	199	java/lang/Throwable
    //   156	166	199	java/lang/Throwable
    //   170	175	199	java/lang/Throwable
    //   3	13	220	java/lang/Throwable
    //   102	106	220	java/lang/Throwable
    //   111	115	220	java/lang/Throwable
    //   179	183	220	java/lang/Throwable
    //   190	194	220	java/lang/Throwable
    //   206	210	220	java/lang/Throwable
    //   214	218	220	java/lang/Throwable
    //   218	220	220	java/lang/Throwable
    //   102	106	226	java/lang/Exception
    //   179	183	230	java/lang/Exception
    //   206	210	234	java/lang/Exception
  }
  
  public static String getServerHost(Context paramContext)
  {
    return getServerIpAddress(getDevServerPort(paramContext).intValue());
  }
  
  public static String getServerHost(Integer paramInteger)
  {
    return getServerIpAddress(paramInteger.intValue());
  }
  
  private static String getServerIpAddress(int paramInt)
  {
    String str2 = getMetroHostPropValue();
    String str1 = str2;
    if (str2.equals("")) {
      if (isRunningOnGenymotion()) {
        str1 = "10.0.3.2";
      } else if (isRunningOnStockEmulator()) {
        str1 = "10.0.2.2";
      } else {
        str1 = "localhost";
      }
    }
    return String.format(Locale.US, "%s:%d", new Object[] { str1, Integer.valueOf(paramInt) });
  }
  
  private static boolean isRunningOnGenymotion()
  {
    return Build.FINGERPRINT.contains("vbox");
  }
  
  private static boolean isRunningOnStockEmulator()
  {
    return Build.FINGERPRINT.contains("generic");
  }
}
