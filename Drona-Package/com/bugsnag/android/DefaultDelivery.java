package com.bugsnag.android;

class DefaultDelivery
  implements Delivery
{
  private static final int HTTP_REQUEST_FAILED = 0;
  private final Connectivity connectivity;
  
  DefaultDelivery(Connectivity paramConnectivity)
  {
    connectivity = paramConnectivity;
  }
  
  /* Error */
  int deliver(String paramString, JsonStream.Streamable paramStreamable, java.util.Map paramMap)
    throws DeliveryFailureException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 18	com/bugsnag/android/DefaultDelivery:connectivity	Lcom/bugsnag/android/Connectivity;
    //   4: astore 6
    //   6: aconst_null
    //   7: astore 10
    //   9: aconst_null
    //   10: astore 8
    //   12: aconst_null
    //   13: astore 9
    //   15: aconst_null
    //   16: astore 7
    //   18: aload 6
    //   20: ifnull +29 -> 49
    //   23: aload_0
    //   24: getfield 18	com/bugsnag/android/DefaultDelivery:connectivity	Lcom/bugsnag/android/Connectivity;
    //   27: invokeinterface 35 1 0
    //   32: ifeq +6 -> 38
    //   35: goto +14 -> 49
    //   38: new 23	com/bugsnag/android/DeliveryFailureException
    //   41: dup
    //   42: ldc 37
    //   44: aconst_null
    //   45: invokespecial 40	com/bugsnag/android/DeliveryFailureException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   48: athrow
    //   49: aload 10
    //   51: astore 6
    //   53: new 42	java/net/URL
    //   56: dup
    //   57: aload_1
    //   58: invokespecial 45	java/net/URL:<init>	(Ljava/lang/String;)V
    //   61: invokevirtual 49	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   64: astore_1
    //   65: aload 10
    //   67: astore 6
    //   69: aload_1
    //   70: checkcast 51	java/net/HttpURLConnection
    //   73: astore_1
    //   74: aload_1
    //   75: checkcast 51	java/net/HttpURLConnection
    //   78: astore 6
    //   80: aload 6
    //   82: iconst_1
    //   83: invokevirtual 55	java/net/HttpURLConnection:setDoOutput	(Z)V
    //   86: aload_1
    //   87: checkcast 51	java/net/HttpURLConnection
    //   90: astore 6
    //   92: aload 6
    //   94: iconst_0
    //   95: invokevirtual 59	java/net/HttpURLConnection:setChunkedStreamingMode	(I)V
    //   98: aload_1
    //   99: checkcast 51	java/net/HttpURLConnection
    //   102: astore 6
    //   104: aload 6
    //   106: ldc 61
    //   108: ldc 63
    //   110: invokevirtual 67	java/net/HttpURLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   113: aload_3
    //   114: invokeinterface 73 1 0
    //   119: invokeinterface 79 1 0
    //   124: astore_3
    //   125: aload_3
    //   126: invokeinterface 84 1 0
    //   131: istore 5
    //   133: iload 5
    //   135: ifeq +68 -> 203
    //   138: aload_3
    //   139: invokeinterface 88 1 0
    //   144: astore 6
    //   146: aload 6
    //   148: checkcast 90	java/util/Map$Entry
    //   151: astore 8
    //   153: aload 8
    //   155: invokeinterface 93 1 0
    //   160: astore 6
    //   162: aload 6
    //   164: checkcast 95	java/lang/String
    //   167: astore 6
    //   169: aload 8
    //   171: invokeinterface 98 1 0
    //   176: astore 8
    //   178: aload 8
    //   180: checkcast 95	java/lang/String
    //   183: astore 8
    //   185: aload_1
    //   186: checkcast 51	java/net/HttpURLConnection
    //   189: astore 9
    //   191: aload 9
    //   193: aload 6
    //   195: aload 8
    //   197: invokevirtual 67	java/net/HttpURLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   200: goto -75 -> 125
    //   203: aload_1
    //   204: checkcast 51	java/net/HttpURLConnection
    //   207: invokevirtual 102	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
    //   210: astore_3
    //   211: ldc 104
    //   213: invokestatic 110	java/nio/charset/Charset:forName	(Ljava/lang/String;)Ljava/nio/charset/Charset;
    //   216: astore 6
    //   218: new 112	java/io/BufferedWriter
    //   221: dup
    //   222: new 114	java/io/OutputStreamWriter
    //   225: dup
    //   226: aload_3
    //   227: aload 6
    //   229: invokespecial 117	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V
    //   232: invokespecial 120	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   235: astore_3
    //   236: new 122	com/bugsnag/android/JsonStream
    //   239: dup
    //   240: aload_3
    //   241: invokespecial 123	com/bugsnag/android/JsonStream:<init>	(Ljava/io/Writer;)V
    //   244: astore_3
    //   245: aload_2
    //   246: aload_3
    //   247: invokeinterface 129 2 0
    //   252: aload_3
    //   253: invokestatic 135	com/bugsnag/android/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   256: aload_1
    //   257: checkcast 51	java/net/HttpURLConnection
    //   260: astore_2
    //   261: aload_2
    //   262: invokevirtual 139	java/net/HttpURLConnection:getResponseCode	()I
    //   265: istore 4
    //   267: aload_1
    //   268: checkcast 141	java/net/URLConnection
    //   271: invokestatic 145	com/bugsnag/android/IOUtils:close	(Ljava/net/URLConnection;)V
    //   274: iload 4
    //   276: ireturn
    //   277: astore_2
    //   278: goto +7 -> 285
    //   281: astore_2
    //   282: aload 7
    //   284: astore_3
    //   285: aload_3
    //   286: checkcast 147	java/io/Closeable
    //   289: astore_3
    //   290: aload_3
    //   291: invokestatic 135	com/bugsnag/android/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   294: aload_2
    //   295: athrow
    //   296: astore_2
    //   297: goto +61 -> 358
    //   300: astore_2
    //   301: goto +23 -> 324
    //   304: astore_2
    //   305: aload_1
    //   306: astore 6
    //   308: aload_2
    //   309: astore_1
    //   310: goto +37 -> 347
    //   313: astore_2
    //   314: aload 6
    //   316: astore_1
    //   317: goto +41 -> 358
    //   320: astore_2
    //   321: aload 8
    //   323: astore_1
    //   324: aload_1
    //   325: astore 6
    //   327: ldc -107
    //   329: aload_2
    //   330: invokestatic 154	com/bugsnag/android/Logger:warn	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   333: aload_1
    //   334: checkcast 141	java/net/URLConnection
    //   337: invokestatic 145	com/bugsnag/android/IOUtils:close	(Ljava/net/URLConnection;)V
    //   340: iconst_0
    //   341: ireturn
    //   342: astore_1
    //   343: aload 9
    //   345: astore 6
    //   347: new 23	com/bugsnag/android/DeliveryFailureException
    //   350: dup
    //   351: ldc -100
    //   353: aload_1
    //   354: invokespecial 40	com/bugsnag/android/DeliveryFailureException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   357: athrow
    //   358: aload_1
    //   359: checkcast 141	java/net/URLConnection
    //   362: invokestatic 145	com/bugsnag/android/IOUtils:close	(Ljava/net/URLConnection;)V
    //   365: aload_2
    //   366: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	367	0	this	DefaultDelivery
    //   0	367	1	paramString	String
    //   0	367	2	paramStreamable	JsonStream.Streamable
    //   0	367	3	paramMap	java.util.Map
    //   265	10	4	i	int
    //   131	3	5	bool	boolean
    //   4	342	6	localObject1	Object
    //   16	267	7	localObject2	Object
    //   10	312	8	localObject3	Object
    //   13	331	9	localHttpURLConnection	java.net.HttpURLConnection
    //   7	59	10	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   245	252	277	java/lang/Throwable
    //   203	218	281	java/lang/Throwable
    //   218	236	281	java/lang/Throwable
    //   236	245	281	java/lang/Throwable
    //   74	80	296	java/lang/Throwable
    //   80	86	296	java/lang/Throwable
    //   86	92	296	java/lang/Throwable
    //   92	98	296	java/lang/Throwable
    //   104	125	296	java/lang/Throwable
    //   125	133	296	java/lang/Throwable
    //   138	146	296	java/lang/Throwable
    //   146	153	296	java/lang/Throwable
    //   153	162	296	java/lang/Throwable
    //   162	169	296	java/lang/Throwable
    //   169	178	296	java/lang/Throwable
    //   191	200	296	java/lang/Throwable
    //   252	256	296	java/lang/Throwable
    //   261	267	296	java/lang/Throwable
    //   290	296	296	java/lang/Throwable
    //   80	86	300	java/lang/Exception
    //   92	98	300	java/lang/Exception
    //   104	125	300	java/lang/Exception
    //   125	133	300	java/lang/Exception
    //   138	146	300	java/lang/Exception
    //   153	162	300	java/lang/Exception
    //   169	178	300	java/lang/Exception
    //   191	200	300	java/lang/Exception
    //   252	256	300	java/lang/Exception
    //   261	267	300	java/lang/Exception
    //   290	296	300	java/lang/Exception
    //   80	86	304	java/io/IOException
    //   92	98	304	java/io/IOException
    //   104	125	304	java/io/IOException
    //   125	133	304	java/io/IOException
    //   138	146	304	java/io/IOException
    //   153	162	304	java/io/IOException
    //   169	178	304	java/io/IOException
    //   191	200	304	java/io/IOException
    //   252	256	304	java/io/IOException
    //   261	267	304	java/io/IOException
    //   290	296	304	java/io/IOException
    //   53	65	313	java/lang/Throwable
    //   69	74	313	java/lang/Throwable
    //   327	333	313	java/lang/Throwable
    //   347	358	313	java/lang/Throwable
    //   53	65	320	java/lang/Exception
    //   53	65	342	java/io/IOException
  }
  
  public void deliver(Report paramReport, Configuration paramConfiguration)
    throws DeliveryFailureException
  {
    int i = deliver(paramConfiguration.getEndpoint(), paramReport, paramConfiguration.getErrorApiHeaders());
    if (i / 100 != 2)
    {
      paramReport = new StringBuilder();
      paramReport.append("Error API request failed with status ");
      paramReport.append(i);
      Logger.warn(paramReport.toString(), null);
      return;
    }
    Logger.info("Completed error API request");
  }
  
  public void deliver(SessionTrackingPayload paramSessionTrackingPayload, Configuration paramConfiguration)
    throws DeliveryFailureException
  {
    int i = deliver(paramConfiguration.getSessionEndpoint(), paramSessionTrackingPayload, paramConfiguration.getSessionApiHeaders());
    if (i != 202)
    {
      paramSessionTrackingPayload = new StringBuilder();
      paramSessionTrackingPayload.append("Session API request failed with status ");
      paramSessionTrackingPayload.append(i);
      Logger.warn(paramSessionTrackingPayload.toString(), null);
      return;
    }
    Logger.info("Completed session tracking request");
  }
}
