package com.bugsnag.android;

import android.util.JsonReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class ErrorReader
{
  ErrorReader() {}
  
  private static Object coerceSerializableFromJSON(JsonReader paramJsonReader)
    throws IOException
  {
    switch (1.$SwitchMap$android$util$JsonToken[paramJsonReader.peek().ordinal()])
    {
    default: 
      return null;
    case 5: 
      return jsonArrayToList(paramJsonReader);
    case 4: 
      return Boolean.valueOf(paramJsonReader.nextBoolean());
    case 3: 
      return jsonObjectToMap(paramJsonReader);
    case 2: 
      return deserializeNumber(paramJsonReader);
    }
    return paramJsonReader.nextString();
  }
  
  private static Object deserializeNumber(JsonReader paramJsonReader)
    throws IOException
  {
    try
    {
      int i = paramJsonReader.nextInt();
      return Integer.valueOf(i);
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      long l;
      label20:
      for (;;) {}
    }
    try
    {
      l = paramJsonReader.nextLong();
      return Long.valueOf(l);
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      break label20;
    }
    return Double.valueOf(paramJsonReader.nextDouble());
  }
  
  private static List jsonArrayToList(JsonReader paramJsonReader)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    paramJsonReader.beginArray();
    while (paramJsonReader.hasNext())
    {
      Object localObject = coerceSerializableFromJSON(paramJsonReader);
      if (localObject != null) {
        localArrayList.add(localObject);
      }
    }
    paramJsonReader.endArray();
    return localArrayList;
  }
  
  private static Map jsonObjectToMap(JsonReader paramJsonReader)
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    paramJsonReader.beginObject();
    while (paramJsonReader.hasNext())
    {
      String str = paramJsonReader.nextName();
      Object localObject = coerceSerializableFromJSON(paramJsonReader);
      if (localObject != null) {
        localHashMap.put(str, localObject);
      }
    }
    paramJsonReader.endObject();
    return localHashMap;
  }
  
  private static Breadcrumb readBreadcrumb(JsonReader paramJsonReader)
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    paramJsonReader.beginObject();
    String str2 = null;
    Date localDate = null;
    String str1 = null;
    while (paramJsonReader.hasNext())
    {
      String str3 = paramJsonReader.nextName();
      int i = -1;
      int j = str3.hashCode();
      if (j != -450957489)
      {
        if (j != 3373707)
        {
          if (j != 3575610)
          {
            if ((j == 55126294) && (str3.equals("timestamp"))) {
              i = 1;
            }
          }
          else if (str3.equals("type")) {
            i = 2;
          }
        }
        else if (str3.equals("name")) {
          i = 0;
        }
      }
      else if (str3.equals("metaData")) {
        i = 3;
      }
      switch (i)
      {
      default: 
        paramJsonReader.skipValue();
        break;
      case 3: 
        paramJsonReader.beginObject();
        while (paramJsonReader.hasNext()) {
          localHashMap.put(paramJsonReader.nextName(), paramJsonReader.nextString());
        }
        paramJsonReader.endObject();
        break;
      case 2: 
        str1 = paramJsonReader.nextString().toUpperCase(Locale.US);
        break;
      case 1: 
        try
        {
          localDate = DateUtils.fromIso8601(paramJsonReader.nextString());
        }
        catch (Exception paramJsonReader)
        {
          throw new IOException("Failed to parse breadcrumb timestamp: ", paramJsonReader);
        }
      case 0: 
        str2 = paramJsonReader.nextString();
      }
    }
    paramJsonReader.endObject();
    if ((str2 != null) && (localDate != null) && (str1 != null)) {
      return new Breadcrumb(str2, BreadcrumbType.valueOf(str1), localDate, localHashMap);
    }
    return null;
  }
  
  private static Breadcrumbs readBreadcrumbs(Configuration paramConfiguration, JsonReader paramJsonReader)
    throws IOException
  {
    paramConfiguration = new Breadcrumbs(paramConfiguration);
    paramJsonReader.beginArray();
    while (paramJsonReader.hasNext())
    {
      Breadcrumb localBreadcrumb = readBreadcrumb(paramJsonReader);
      if (localBreadcrumb != null) {
        paramConfiguration.i(localBreadcrumb);
      }
    }
    paramJsonReader.endArray();
    return paramConfiguration;
  }
  
  /* Error */
  static Error readError(Configuration paramConfiguration, java.io.File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: getstatic 204	com/bugsnag/android/Severity:ERROR	Lcom/bugsnag/android/Severity;
    //   3: astore 16
    //   5: invokestatic 210	java/util/Collections:emptyList	()Ljava/util/List;
    //   8: astore 15
    //   10: new 21	android/util/JsonReader
    //   13: dup
    //   14: new 212	java/io/BufferedReader
    //   17: dup
    //   18: new 214	java/io/FileReader
    //   21: dup
    //   22: aload_1
    //   23: invokespecial 217	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   26: invokespecial 220	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   29: invokespecial 221	android/util/JsonReader:<init>	(Ljava/io/Reader;)V
    //   32: astore_1
    //   33: aload_1
    //   34: invokevirtual 111	android/util/JsonReader:beginObject	()V
    //   37: aconst_null
    //   38: astore 17
    //   40: iconst_0
    //   41: istore_3
    //   42: aconst_null
    //   43: astore 13
    //   45: aconst_null
    //   46: astore 14
    //   48: aconst_null
    //   49: astore 12
    //   51: aconst_null
    //   52: astore 11
    //   54: aconst_null
    //   55: astore 10
    //   57: aconst_null
    //   58: astore 9
    //   60: aconst_null
    //   61: astore 8
    //   63: aconst_null
    //   64: astore 7
    //   66: aconst_null
    //   67: astore 6
    //   69: aconst_null
    //   70: astore 5
    //   72: aload_1
    //   73: invokevirtual 94	android/util/JsonReader:hasNext	()Z
    //   76: istore 4
    //   78: iload 4
    //   80: ifeq +695 -> 775
    //   83: aload_1
    //   84: invokevirtual 114	android/util/JsonReader:nextName	()Ljava/lang/String;
    //   87: astore 18
    //   89: aload 18
    //   91: invokevirtual 132	java/lang/String:hashCode	()I
    //   94: istore_2
    //   95: iload_2
    //   96: lookupswitch	default:+124->220, -1890362749:+384->480, -1337936983:+364->460, -1335157162:+345->441, -1314244092:+326->422, -450957489:+306->402, -68904783:+286->382, -51457840:+267->363, 96801:+248->344, 3599307:+228->324, 358603558:+209->305, 398106529:+189->285, 951530927:+170->266, 1478300413:+150->246, 1984987798:+130->226
    //   220: goto +3 -> 223
    //   223: goto +277 -> 500
    //   226: aload 18
    //   228: ldc -33
    //   230: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   233: istore 4
    //   235: iload 4
    //   237: ifeq +263 -> 500
    //   240: bipush 8
    //   242: istore_2
    //   243: goto +259 -> 502
    //   246: aload 18
    //   248: ldc -31
    //   250: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   253: istore 4
    //   255: iload 4
    //   257: ifeq +243 -> 500
    //   260: bipush 9
    //   262: istore_2
    //   263: goto +239 -> 502
    //   266: aload 18
    //   268: ldc -29
    //   270: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   273: istore 4
    //   275: iload 4
    //   277: ifeq +223 -> 500
    //   280: iconst_2
    //   281: istore_2
    //   282: goto +220 -> 502
    //   285: aload 18
    //   287: ldc -27
    //   289: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   292: istore 4
    //   294: iload 4
    //   296: ifeq +204 -> 500
    //   299: bipush 10
    //   301: istore_2
    //   302: goto +200 -> 502
    //   305: aload 18
    //   307: ldc -25
    //   309: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   312: istore 4
    //   314: iload 4
    //   316: ifeq +184 -> 500
    //   319: iconst_4
    //   320: istore_2
    //   321: goto +181 -> 502
    //   324: aload 18
    //   326: ldc -23
    //   328: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   331: istore 4
    //   333: iload 4
    //   335: ifeq +165 -> 500
    //   338: bipush 13
    //   340: istore_2
    //   341: goto +161 -> 502
    //   344: aload 18
    //   346: ldc -21
    //   348: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   351: istore 4
    //   353: iload 4
    //   355: ifeq +145 -> 500
    //   358: iconst_0
    //   359: istore_2
    //   360: goto +142 -> 502
    //   363: aload 18
    //   365: ldc -19
    //   367: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   370: istore 4
    //   372: iload 4
    //   374: ifeq +126 -> 500
    //   377: iconst_1
    //   378: istore_2
    //   379: goto +123 -> 502
    //   382: aload 18
    //   384: ldc -17
    //   386: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   389: istore 4
    //   391: iload 4
    //   393: ifeq +107 -> 500
    //   396: bipush 6
    //   398: istore_2
    //   399: goto +103 -> 502
    //   402: aload 18
    //   404: ldc -109
    //   406: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   409: istore 4
    //   411: iload 4
    //   413: ifeq +87 -> 500
    //   416: bipush 7
    //   418: istore_2
    //   419: goto +83 -> 502
    //   422: aload 18
    //   424: ldc -15
    //   426: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   429: istore 4
    //   431: iload 4
    //   433: ifeq +67 -> 500
    //   436: iconst_5
    //   437: istore_2
    //   438: goto +64 -> 502
    //   441: aload 18
    //   443: ldc -13
    //   445: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   448: istore 4
    //   450: iload 4
    //   452: ifeq +48 -> 500
    //   455: iconst_3
    //   456: istore_2
    //   457: goto +45 -> 502
    //   460: aload 18
    //   462: ldc -11
    //   464: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   467: istore 4
    //   469: iload 4
    //   471: ifeq +29 -> 500
    //   474: bipush 11
    //   476: istore_2
    //   477: goto +25 -> 502
    //   480: aload 18
    //   482: ldc -9
    //   484: invokevirtual 141	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   487: istore 4
    //   489: iload 4
    //   491: ifeq +9 -> 500
    //   494: bipush 12
    //   496: istore_2
    //   497: goto +5 -> 502
    //   500: iconst_m1
    //   501: istore_2
    //   502: iload_2
    //   503: lookupswitch	default:+121->624, 0:+259->762, 1:+249->752, 2:+240->743, 3:+231->734, 4:+222->725, 5:+212->715, 6:+203->706, 7:+187->690, 8:+178->681, 9:+166->669, 10:+157->660, 11:+148->651, 12:+140->643, 13:+131->634
    //   624: goto +3 -> 627
    //   627: aload_1
    //   628: invokevirtual 150	android/util/JsonReader:skipValue	()V
    //   631: goto -559 -> 72
    //   634: aload_1
    //   635: invokestatic 251	com/bugsnag/android/ErrorReader:readUser	(Landroid/util/JsonReader;)Lcom/bugsnag/android/User;
    //   638: astore 10
    //   640: goto -568 -> 72
    //   643: aload_1
    //   644: invokevirtual 39	android/util/JsonReader:nextBoolean	()Z
    //   647: istore_3
    //   648: goto -576 -> 72
    //   651: aload_1
    //   652: invokestatic 255	com/bugsnag/android/ErrorReader:readThreadState	(Landroid/util/JsonReader;)Lcom/bugsnag/android/ThreadState;
    //   655: astore 12
    //   657: goto -585 -> 72
    //   660: aload_1
    //   661: invokestatic 259	com/bugsnag/android/ErrorReader:readSeverityReason	(Landroid/util/JsonReader;)Ljava/util/ArrayList;
    //   664: astore 17
    //   666: goto -594 -> 72
    //   669: aload_1
    //   670: invokevirtual 56	android/util/JsonReader:nextString	()Ljava/lang/String;
    //   673: invokestatic 263	com/bugsnag/android/Severity:fromString	(Ljava/lang/String;)Lcom/bugsnag/android/Severity;
    //   676: astore 16
    //   678: goto -606 -> 72
    //   681: aload_1
    //   682: invokestatic 267	com/bugsnag/android/ErrorReader:readSession	(Landroid/util/JsonReader;)Lcom/bugsnag/android/Session;
    //   685: astore 14
    //   687: goto -615 -> 72
    //   690: new 269	com/bugsnag/android/MetaData
    //   693: dup
    //   694: aload_1
    //   695: invokestatic 49	com/bugsnag/android/ErrorReader:jsonObjectToMap	(Landroid/util/JsonReader;)Ljava/util/Map;
    //   698: invokespecial 272	com/bugsnag/android/MetaData:<init>	(Ljava/util/Map;)V
    //   701: astore 13
    //   703: goto -631 -> 72
    //   706: aload_1
    //   707: invokevirtual 56	android/util/JsonReader:nextString	()Ljava/lang/String;
    //   710: astore 8
    //   712: goto -640 -> 72
    //   715: aload_0
    //   716: aload_1
    //   717: invokestatic 276	com/bugsnag/android/ErrorReader:readExceptions	(Lcom/bugsnag/android/Configuration;Landroid/util/JsonReader;)Lcom/bugsnag/android/Exceptions;
    //   720: astore 11
    //   722: goto -650 -> 72
    //   725: aload_1
    //   726: invokestatic 35	com/bugsnag/android/ErrorReader:jsonArrayToList	(Landroid/util/JsonReader;)Ljava/util/List;
    //   729: astore 15
    //   731: goto -659 -> 72
    //   734: aload_1
    //   735: invokestatic 49	com/bugsnag/android/ErrorReader:jsonObjectToMap	(Landroid/util/JsonReader;)Ljava/util/Map;
    //   738: astore 6
    //   740: goto -668 -> 72
    //   743: aload_1
    //   744: invokevirtual 56	android/util/JsonReader:nextString	()Ljava/lang/String;
    //   747: astore 9
    //   749: goto -677 -> 72
    //   752: aload_0
    //   753: aload_1
    //   754: invokestatic 278	com/bugsnag/android/ErrorReader:readBreadcrumbs	(Lcom/bugsnag/android/Configuration;Landroid/util/JsonReader;)Lcom/bugsnag/android/Breadcrumbs;
    //   757: astore 5
    //   759: goto -687 -> 72
    //   762: aload_1
    //   763: invokestatic 49	com/bugsnag/android/ErrorReader:jsonObjectToMap	(Landroid/util/JsonReader;)Ljava/util/Map;
    //   766: astore 7
    //   768: goto -696 -> 72
    //   771: astore_0
    //   772: goto +194 -> 966
    //   775: aload_1
    //   776: invokevirtual 123	android/util/JsonReader:endObject	()V
    //   779: aload 17
    //   781: ifnull +163 -> 944
    //   784: aload 11
    //   786: ifnull +158 -> 944
    //   789: aload 17
    //   791: invokevirtual 281	java/util/ArrayList:size	()I
    //   794: istore_2
    //   795: iload_2
    //   796: iconst_1
    //   797: if_icmple +17 -> 814
    //   800: aload 17
    //   802: iconst_1
    //   803: invokevirtual 285	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   806: checkcast 129	java/lang/String
    //   809: astore 18
    //   811: goto +6 -> 817
    //   814: aconst_null
    //   815: astore 18
    //   817: new 287	com/bugsnag/android/HandledState
    //   820: dup
    //   821: aload 17
    //   823: iconst_0
    //   824: invokevirtual 285	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   827: checkcast 129	java/lang/String
    //   830: aload 16
    //   832: iload_3
    //   833: aload 18
    //   835: invokespecial 290	com/bugsnag/android/HandledState:<init>	(Ljava/lang/String;Lcom/bugsnag/android/Severity;ZLjava/lang/String;)V
    //   838: astore 17
    //   840: aload 11
    //   842: invokevirtual 296	com/bugsnag/android/Exceptions:getException	()Lcom/bugsnag/android/BugsnagException;
    //   845: astore 18
    //   847: new 298	com/bugsnag/android/Error
    //   850: dup
    //   851: aload_0
    //   852: aload 18
    //   854: aload 17
    //   856: aload 16
    //   858: aload 14
    //   860: aload 12
    //   862: invokespecial 301	com/bugsnag/android/Error:<init>	(Lcom/bugsnag/android/Configuration;Ljava/lang/Throwable;Lcom/bugsnag/android/HandledState;Lcom/bugsnag/android/Severity;Lcom/bugsnag/android/Session;Lcom/bugsnag/android/ThreadState;)V
    //   865: astore_0
    //   866: aload_0
    //   867: invokevirtual 305	com/bugsnag/android/Error:getExceptions	()Lcom/bugsnag/android/Exceptions;
    //   870: aload 11
    //   872: invokevirtual 308	com/bugsnag/android/Exceptions:getExceptionType	()Ljava/lang/String;
    //   875: invokevirtual 312	com/bugsnag/android/Exceptions:setExceptionType	(Ljava/lang/String;)V
    //   878: aload_0
    //   879: aload 15
    //   881: iconst_0
    //   882: anewarray 129	java/lang/String
    //   885: invokeinterface 316 2 0
    //   890: checkcast 318	[Ljava/lang/String;
    //   893: invokevirtual 322	com/bugsnag/android/Error:setProjectPackages	([Ljava/lang/String;)V
    //   896: aload_0
    //   897: aload 10
    //   899: invokevirtual 326	com/bugsnag/android/Error:setUser	(Lcom/bugsnag/android/User;)V
    //   902: aload_0
    //   903: aload 9
    //   905: invokevirtual 329	com/bugsnag/android/Error:setContext	(Ljava/lang/String;)V
    //   908: aload_0
    //   909: aload 8
    //   911: invokevirtual 332	com/bugsnag/android/Error:setGroupingHash	(Ljava/lang/String;)V
    //   914: aload_0
    //   915: aload 7
    //   917: invokevirtual 335	com/bugsnag/android/Error:setAppData	(Ljava/util/Map;)V
    //   920: aload_0
    //   921: aload 13
    //   923: invokevirtual 339	com/bugsnag/android/Error:setMetaData	(Lcom/bugsnag/android/MetaData;)V
    //   926: aload_0
    //   927: aload 6
    //   929: invokevirtual 342	com/bugsnag/android/Error:setDeviceData	(Ljava/util/Map;)V
    //   932: aload_0
    //   933: aload 5
    //   935: invokevirtual 346	com/bugsnag/android/Error:setBreadcrumbs	(Lcom/bugsnag/android/Breadcrumbs;)V
    //   938: aload_1
    //   939: invokevirtual 349	android/util/JsonReader:close	()V
    //   942: aload_0
    //   943: areturn
    //   944: new 15	java/io/IOException
    //   947: dup
    //   948: ldc_w 351
    //   951: invokespecial 353	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   954: athrow
    //   955: astore_0
    //   956: goto +10 -> 966
    //   959: astore_0
    //   960: goto +6 -> 966
    //   963: astore_0
    //   964: aconst_null
    //   965: astore_1
    //   966: aload_1
    //   967: ifnull +7 -> 974
    //   970: aload_1
    //   971: invokevirtual 349	android/util/JsonReader:close	()V
    //   974: aload_0
    //   975: athrow
    //   976: astore_1
    //   977: aload_0
    //   978: areturn
    //   979: astore_1
    //   980: goto -6 -> 974
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	983	0	paramConfiguration	Configuration
    //   0	983	1	paramFile	java.io.File
    //   94	704	2	i	int
    //   41	792	3	bool1	boolean
    //   76	414	4	bool2	boolean
    //   70	864	5	localBreadcrumbs	Breadcrumbs
    //   67	861	6	localMap1	Map
    //   64	852	7	localMap2	Map
    //   61	849	8	str1	String
    //   58	846	9	str2	String
    //   55	843	10	localUser	User
    //   52	819	11	localExceptions	Exceptions
    //   49	812	12	localThreadState	ThreadState
    //   43	879	13	localMetaData	MetaData
    //   46	813	14	localSession	Session
    //   8	872	15	localList	List
    //   3	854	16	localSeverity	Severity
    //   38	817	17	localObject1	Object
    //   87	766	18	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   83	95	771	java/lang/Throwable
    //   226	235	771	java/lang/Throwable
    //   246	255	771	java/lang/Throwable
    //   266	275	771	java/lang/Throwable
    //   285	294	771	java/lang/Throwable
    //   305	314	771	java/lang/Throwable
    //   324	333	771	java/lang/Throwable
    //   344	353	771	java/lang/Throwable
    //   363	372	771	java/lang/Throwable
    //   382	391	771	java/lang/Throwable
    //   402	411	771	java/lang/Throwable
    //   422	431	771	java/lang/Throwable
    //   441	450	771	java/lang/Throwable
    //   460	469	771	java/lang/Throwable
    //   480	489	771	java/lang/Throwable
    //   627	631	771	java/lang/Throwable
    //   634	640	771	java/lang/Throwable
    //   643	648	771	java/lang/Throwable
    //   651	657	771	java/lang/Throwable
    //   660	666	771	java/lang/Throwable
    //   669	678	771	java/lang/Throwable
    //   681	687	771	java/lang/Throwable
    //   690	703	771	java/lang/Throwable
    //   706	712	771	java/lang/Throwable
    //   715	722	771	java/lang/Throwable
    //   725	731	771	java/lang/Throwable
    //   734	740	771	java/lang/Throwable
    //   743	749	771	java/lang/Throwable
    //   752	759	771	java/lang/Throwable
    //   762	768	771	java/lang/Throwable
    //   800	811	771	java/lang/Throwable
    //   847	938	955	java/lang/Throwable
    //   944	955	955	java/lang/Throwable
    //   33	37	959	java/lang/Throwable
    //   72	78	959	java/lang/Throwable
    //   775	779	959	java/lang/Throwable
    //   789	795	959	java/lang/Throwable
    //   817	847	959	java/lang/Throwable
    //   0	33	963	java/lang/Throwable
    //   938	942	976	java/lang/Exception
    //   970	974	979	java/lang/Exception
  }
  
  private static BugsnagException readException(JsonReader paramJsonReader)
    throws IOException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a2 = a1\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  private static Exceptions readExceptions(Configuration paramConfiguration, JsonReader paramJsonReader)
    throws IOException
  {
    paramJsonReader.beginArray();
    BugsnagException localBugsnagException1 = readException(paramJsonReader);
    BugsnagException localBugsnagException2;
    for (Object localObject = localBugsnagException1; paramJsonReader.hasNext(); localObject = localBugsnagException2)
    {
      localBugsnagException2 = readException(paramJsonReader);
      ((Throwable)localObject).initCause(localBugsnagException2);
    }
    paramJsonReader.endArray();
    paramConfiguration = new Exceptions(paramConfiguration, localBugsnagException1);
    if (localBugsnagException1 != null) {
      paramConfiguration.setExceptionType(localBugsnagException1.getType());
    }
    return paramConfiguration;
  }
  
  private static Session readSession(JsonReader paramJsonReader)
    throws IOException
  {
    paramJsonReader.beginObject();
    String str1 = null;
    Date localDate = null;
    User localUser = null;
    int j = 0;
    int k = 0;
    while (paramJsonReader.hasNext())
    {
      String str2 = paramJsonReader.nextName();
      int i = str2.hashCode();
      if (i != -2128794476)
      {
        if (i != -1291329255)
        {
          if (i != 3355)
          {
            if ((i == 3599307) && (str2.equals("user")))
            {
              i = 3;
              break label132;
            }
          }
          else if (str2.equals("id"))
          {
            i = 0;
            break label132;
          }
        }
        else if (str2.equals("events"))
        {
          i = 2;
          break label132;
        }
      }
      else if (str2.equals("startedAt"))
      {
        i = 1;
        break label132;
      }
      i = -1;
      switch (i)
      {
      default: 
        paramJsonReader.skipValue();
        break;
      case 3: 
        localUser = readUser(paramJsonReader);
        break;
      case 2: 
        paramJsonReader.beginObject();
        while (paramJsonReader.hasNext())
        {
          str2 = paramJsonReader.nextName();
          i = str2.hashCode();
          if (i != -1890362749)
          {
            if ((i == 692803388) && (str2.equals("handled")))
            {
              i = 1;
              break label268;
            }
          }
          else if (str2.equals("unhandled"))
          {
            i = 0;
            break label268;
          }
          i = -1;
          switch (i)
          {
          default: 
            paramJsonReader.skipValue();
            break;
          case 1: 
            k = paramJsonReader.nextInt();
            break;
          case 0: 
            j = paramJsonReader.nextInt();
          }
        }
        paramJsonReader.endObject();
        break;
      case 1: 
        try
        {
          localDate = DateUtils.fromIso8601(paramJsonReader.nextString());
        }
        catch (Exception paramJsonReader)
        {
          throw new IOException("Unable to parse session startedAt: ", paramJsonReader);
        }
      case 0: 
        label132:
        label268:
        str1 = paramJsonReader.nextString();
      }
    }
    paramJsonReader.endObject();
    if ((str1 != null) && (localDate != null)) {
      return new Session(str1, localDate, localUser, j, k);
    }
    throw new IOException("Session data missing required fields");
  }
  
  private static ArrayList readSeverityReason(JsonReader paramJsonReader)
    throws IOException
  {
    paramJsonReader.beginObject();
    String str2 = null;
    String str1 = null;
    while (paramJsonReader.hasNext())
    {
      String str3 = paramJsonReader.nextName();
      int i = -1;
      int j = str3.hashCode();
      if (j != 3575610)
      {
        if ((j == 405645655) && (str3.equals("attributes"))) {
          i = 1;
        }
      }
      else if (str3.equals("type")) {
        i = 0;
      }
      switch (i)
      {
      default: 
        paramJsonReader.skipValue();
        break;
      case 1: 
        paramJsonReader.beginObject();
        paramJsonReader.nextName();
        str1 = paramJsonReader.nextString();
        paramJsonReader.endObject();
        break;
      case 0: 
        str2 = paramJsonReader.nextString();
      }
    }
    paramJsonReader.endObject();
    paramJsonReader = new ArrayList();
    if (str2 != null)
    {
      paramJsonReader.add(str2);
      if (str1 != null)
      {
        paramJsonReader.add(str1);
        return paramJsonReader;
      }
    }
    else
    {
      throw new IOException("Severity Reason type is required");
    }
    return paramJsonReader;
  }
  
  private static Map readStackFrame(JsonReader paramJsonReader)
    throws IOException
  {
    localHashMap = new HashMap();
    paramJsonReader.beginObject();
    try
    {
      for (;;)
      {
        boolean bool = paramJsonReader.hasNext();
        if (!bool) {
          break;
        }
        String str = paramJsonReader.nextName();
        int[] arrayOfInt = 1.$SwitchMap$android$util$JsonToken;
        int i = paramJsonReader.peek().ordinal();
        switch (arrayOfInt[i])
        {
        default: 
          paramJsonReader.skipValue();
          break;
        case 2: 
          localHashMap.put(str, deserializeNumber(paramJsonReader));
          break;
        case 1: 
          localHashMap.put(str, paramJsonReader.nextString());
        }
      }
      return localHashMap;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logger.warn("Failed to read stackframe", localIllegalStateException);
      paramJsonReader.endObject();
    }
  }
  
  private static List readStackFrames(JsonReader paramJsonReader)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    paramJsonReader.beginArray();
    while (paramJsonReader.hasNext()) {
      localArrayList.add(readStackFrame(paramJsonReader));
    }
    paramJsonReader.endArray();
    return localArrayList;
  }
  
  private static CachedThread readThread(JsonReader paramJsonReader)
    throws IOException
  {
    paramJsonReader.beginObject();
    String str2 = null;
    String str1 = null;
    List localList = null;
    long l = 0L;
    boolean bool = false;
    while (paramJsonReader.hasNext())
    {
      String str3 = paramJsonReader.nextName();
      int i = str3.hashCode();
      if (i != -767067472)
      {
        if (i != 3355)
        {
          if (i != 3373707)
          {
            if (i != 3575610)
            {
              if ((i == 2055832509) && (str3.equals("stacktrace")))
              {
                i = 3;
                break label153;
              }
            }
            else if (str3.equals("type"))
            {
              i = 2;
              break label153;
            }
          }
          else if (str3.equals("name"))
          {
            i = 1;
            break label153;
          }
        }
        else if (str3.equals("id"))
        {
          i = 0;
          break label153;
        }
      }
      else if (str3.equals("errorReportingThread"))
      {
        i = 4;
        break label153;
      }
      i = -1;
      switch (i)
      {
      default: 
        paramJsonReader.skipValue();
        break;
      case 4: 
        bool = paramJsonReader.nextBoolean();
        break;
      case 3: 
        localList = readStackFrames(paramJsonReader);
        break;
      case 2: 
        str1 = paramJsonReader.nextString();
        break;
      case 1: 
        str2 = paramJsonReader.nextString();
        break;
      case 0: 
        label153:
        l = paramJsonReader.nextLong();
      }
    }
    paramJsonReader.endObject();
    if ((str1 != null) && (localList != null)) {
      return new CachedThread(l, str2, str1, bool, localList);
    }
    return null;
  }
  
  private static ThreadState readThreadState(JsonReader paramJsonReader)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    paramJsonReader.beginArray();
    while (paramJsonReader.hasNext())
    {
      CachedThread localCachedThread = readThread(paramJsonReader);
      if (localCachedThread != null) {
        localArrayList.add(localCachedThread);
      }
    }
    paramJsonReader.endArray();
    return new ThreadState((CachedThread[])localArrayList.toArray(new CachedThread[0]));
  }
  
  private static User readUser(JsonReader paramJsonReader)
    throws IOException
  {
    User localUser = new User();
    paramJsonReader.beginObject();
    while (paramJsonReader.hasNext())
    {
      String str = paramJsonReader.nextName();
      int i = -1;
      int j = str.hashCode();
      if (j != 3355)
      {
        if (j != 3373707)
        {
          if ((j == 96619420) && (str.equals("email"))) {
            i = 2;
          }
        }
        else if (str.equals("name")) {
          i = 0;
        }
      }
      else if (str.equals("id")) {
        i = 1;
      }
      switch (i)
      {
      default: 
        paramJsonReader.skipValue();
        break;
      case 2: 
        localUser.setEmail(paramJsonReader.nextString());
        break;
      case 1: 
        localUser.setId(paramJsonReader.nextString());
        break;
      case 0: 
        localUser.setName(paramJsonReader.nextString());
      }
    }
    paramJsonReader.endObject();
    return localUser;
  }
}
