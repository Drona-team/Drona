package com.amplitude.upgrade;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Build.VERSION;
import android.util.Pair;
import com.amplitude.security.MD4;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.Call;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AmplitudeClient
{
  public static final String DEVICE_ID_KEY = "device_id";
  public static final String END_SESSION_EVENT = "session_end";
  public static final String LAST_EVENT_ID_KEY = "last_event_id";
  public static final String LAST_EVENT_TIME_KEY = "last_event_time";
  public static final String LAST_IDENTIFY_ID_KEY = "last_identify_id";
  public static final String OPT_OUT_KEY = "opt_out";
  public static final String PAGE_KEY = "com.amplitude.api.AmplitudeClient";
  public static final String PREVIOUS_SESSION_ID_KEY = "previous_session_id";
  public static final String SEQUENCE_NUMBER_KEY = "sequence_number";
  public static final String START_SESSION_EVENT = "session_start";
  public static final String USER_ID_KEY = "user_id";
  private static final AmplitudeLog logger = ;
  String accessTokenUrl = "https://api.amplitude.com/";
  protected String apiKey;
  JSONObject apiPropertiesTrackingOptions;
  private boolean backoffUpload = false;
  private int backoffUploadBatchSize = eventUploadMaxBatchSize;
  protected Context context;
  protected DatabaseHelper dbHelper;
  protected String deviceId;
  private DeviceInfo deviceInfo;
  private int eventMaxCount = 1000;
  private int eventUploadMaxBatchSize = 50;
  private long eventUploadPeriodMillis = 30000L;
  private int eventUploadThreshold = 30;
  private boolean flushEventsOnClose = true;
  protected OkHttpClient httpClient;
  WorkerThread httpThread = new WorkerThread("httpThread");
  private boolean inForeground = false;
  protected boolean initialized = false;
  protected String instanceName;
  Throwable lastError;
  long lastEventId = -1L;
  long lastEventTime = -1L;
  long lastIdentifyId = -1L;
  WorkerThread logThread = new WorkerThread("logThread");
  private long minTimeBetweenSessionsMillis = 300000L;
  private boolean newDeviceIdPerInstall = false;
  private boolean offline = false;
  private boolean optOut = false;
  protected String platform;
  long previousSessionId = -1L;
  long sequenceNumber = 0L;
  long sessionId = -1L;
  private long sessionTimeoutMillis = 1800000L;
  TrackingOptions trackingOptions = new TrackingOptions();
  private boolean trackingSessionEvents = false;
  private AtomicBoolean updateScheduled = new AtomicBoolean(false);
  AtomicBoolean uploadingCurrently = new AtomicBoolean(false);
  private boolean useAdvertisingIdForDeviceId = false;
  protected String userId;
  private boolean usingForegroundTracking = false;
  
  public AmplitudeClient()
  {
    this(null);
  }
  
  public AmplitudeClient(String paramString)
  {
    instanceName = Utils.normalizeInstanceName(paramString);
    logThread.start();
    httpThread.start();
  }
  
  private Set getInvalidDeviceIds()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.add("");
    localHashSet.add("9774d56d682e549c");
    localHashSet.add("unknown");
    localHashSet.add("000000000000000");
    localHashSet.add("Android");
    localHashSet.add("DEFACE");
    localHashSet.add("00000000-0000-0000-0000-000000000000");
    return localHashSet;
  }
  
  private long getLongvalue(String paramString, long paramLong)
  {
    paramString = dbHelper.getLongValue(paramString);
    if (paramString == null) {
      return paramLong;
    }
    return paramString.longValue();
  }
  
  private boolean inSession()
  {
    return sessionId >= 0L;
  }
  
  private String initializeDeviceId()
  {
    Object localObject = getInvalidDeviceIds();
    String str1 = dbHelper.getValue("device_id");
    String str2 = Utils.getStringFromSharedPreferences(context, instanceName, "device_id");
    if ((!Utils.isEmptyString(str1)) && (!((Set)localObject).contains(str1)))
    {
      localObject = str1;
      if (!str1.equals(str2))
      {
        saveDeviceId(str1);
        return str1;
      }
    }
    else
    {
      if ((!Utils.isEmptyString(str2)) && (!((Set)localObject).contains(str2)))
      {
        saveDeviceId(str2);
        return str2;
      }
      if ((!newDeviceIdPerInstall) && (useAdvertisingIdForDeviceId) && (!deviceInfo.isLimitAdTrackingEnabled()))
      {
        str1 = deviceInfo.getAdvertisingId();
        if ((!Utils.isEmptyString(str1)) && (!((Set)localObject).contains(str1)))
        {
          saveDeviceId(str1);
          return str1;
        }
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(DeviceInfo.generateUUID());
      ((StringBuilder)localObject).append("R");
      localObject = ((StringBuilder)localObject).toString();
      saveDeviceId((String)localObject);
    }
    return localObject;
  }
  
  private boolean isWithinMinTimeBetweenSessions(long paramLong)
  {
    long l;
    if (usingForegroundTracking) {
      l = minTimeBetweenSessionsMillis;
    } else {
      l = sessionTimeoutMillis;
    }
    return paramLong - lastEventTime < l;
  }
  
  private static void migrateBooleanValue(SharedPreferences paramSharedPreferences, String paramString1, boolean paramBoolean, DatabaseHelper paramDatabaseHelper, String paramString2)
  {
    if (paramDatabaseHelper.getLongValue(paramString2) != null) {
      return;
    }
    long l;
    if (paramSharedPreferences.getBoolean(paramString1, paramBoolean)) {
      l = 1L;
    } else {
      l = 0L;
    }
    paramDatabaseHelper.insertOrReplaceKeyLongValue(paramString2, Long.valueOf(l));
    paramSharedPreferences.edit().remove(paramString1).apply();
  }
  
  private static void migrateLongValue(SharedPreferences paramSharedPreferences, String paramString1, long paramLong, DatabaseHelper paramDatabaseHelper, String paramString2)
  {
    if (paramDatabaseHelper.getLongValue(paramString2) != null) {
      return;
    }
    paramDatabaseHelper.insertOrReplaceKeyLongValue(paramString2, Long.valueOf(paramSharedPreferences.getLong(paramString1, paramLong)));
    paramSharedPreferences.edit().remove(paramString1).apply();
  }
  
  private static void migrateStringValue(SharedPreferences paramSharedPreferences, String paramString1, String paramString2, DatabaseHelper paramDatabaseHelper, String paramString3)
  {
    if (!Utils.isEmptyString(paramDatabaseHelper.getValue(paramString3))) {
      return;
    }
    paramString2 = paramSharedPreferences.getString(paramString1, paramString2);
    if (!Utils.isEmptyString(paramString2))
    {
      paramDatabaseHelper.insertOrReplaceKeyValue(paramString3, paramString2);
      paramSharedPreferences.edit().remove(paramString1).apply();
    }
  }
  
  private void saveDeviceId(String paramString)
  {
    dbHelper.insertOrReplaceKeyValue("device_id", paramString);
    Utils.writeStringToSharedPreferences(context, instanceName, "device_id", paramString);
  }
  
  private void sendSessionEvent(String paramString)
  {
    if (!contextAndApiKeySet(String.format("sendSessionEvent('%s')", new Object[] { paramString }))) {
      return;
    }
    if (!inSession()) {
      return;
    }
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("special", paramString);
      logEvent(paramString, null, localJSONObject, null, null, null, lastEventTime, false);
      return;
    }
    catch (JSONException localJSONException)
    {
      Diagnostics.getLogger().logError(String.format("Failed to generate API Properties JSON for session event %s", new Object[] { paramString }), localJSONException);
    }
  }
  
  private void setSessionId(long paramLong)
  {
    sessionId = paramLong;
    setPreviousSessionId(paramLong);
  }
  
  private void startNewSession(long paramLong)
  {
    if (trackingSessionEvents) {
      sendSessionEvent("session_end");
    }
    setSessionId(paramLong);
    refreshSessionTime(paramLong);
    if (trackingSessionEvents) {
      sendSessionEvent("session_start");
    }
  }
  
  public static String truncate(String paramString)
  {
    if (paramString.length() <= 1024) {
      return paramString;
    }
    return paramString.substring(0, 1024);
  }
  
  private void updateServerLater(long paramLong)
  {
    if (updateScheduled.getAndSet(true)) {
      return;
    }
    logThread.postDelayed(new AmplitudeClient.12(this), paramLong);
  }
  
  static boolean upgradePrefs(Context paramContext)
  {
    return upgradePrefs(paramContext, null, null);
  }
  
  /* Error */
  static boolean upgradePrefs(Context paramContext, String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: astore 5
    //   3: aload_1
    //   4: ifnonnull +31 -> 35
    //   7: ldc_w 466
    //   10: astore 5
    //   12: ldc_w 468
    //   15: invokevirtual 474	java/lang/Class:getPackage	()Ljava/lang/Package;
    //   18: invokevirtual 479	java/lang/Package:getName	()Ljava/lang/String;
    //   21: astore_1
    //   22: aload_1
    //   23: astore 5
    //   25: goto +10 -> 35
    //   28: goto +7 -> 35
    //   31: astore_0
    //   32: goto +605 -> 637
    //   35: aload_2
    //   36: astore_1
    //   37: aload_2
    //   38: ifnonnull +7 -> 45
    //   41: ldc_w 466
    //   44: astore_1
    //   45: aload_1
    //   46: aload 5
    //   48: invokevirtual 317	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   51: istore 4
    //   53: iload 4
    //   55: ifeq +5 -> 60
    //   58: iconst_0
    //   59: ireturn
    //   60: new 327	java/lang/StringBuilder
    //   63: dup
    //   64: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   67: astore_2
    //   68: aload_2
    //   69: aload 5
    //   71: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: pop
    //   75: aload_2
    //   76: ldc_w 481
    //   79: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: pop
    //   83: aload_2
    //   84: aload_0
    //   85: invokevirtual 486	android/content/Context:getPackageName	()Ljava/lang/String;
    //   88: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: pop
    //   92: aload_2
    //   93: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   96: astore_2
    //   97: aload_0
    //   98: aload_2
    //   99: iconst_0
    //   100: invokevirtual 490	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   103: astore 6
    //   105: aload 6
    //   107: invokeinterface 494 1 0
    //   112: invokeinterface 499 1 0
    //   117: istore_3
    //   118: iload_3
    //   119: ifne +5 -> 124
    //   122: iconst_0
    //   123: ireturn
    //   124: new 327	java/lang/StringBuilder
    //   127: dup
    //   128: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   131: astore 7
    //   133: aload 7
    //   135: aload_1
    //   136: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: pop
    //   140: aload 7
    //   142: ldc_w 481
    //   145: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: pop
    //   149: aload 7
    //   151: aload_0
    //   152: invokevirtual 486	android/content/Context:getPackageName	()Ljava/lang/String;
    //   155: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: pop
    //   159: aload 7
    //   161: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   164: astore_1
    //   165: aload_0
    //   166: aload_1
    //   167: iconst_0
    //   168: invokevirtual 490	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   171: invokeinterface 362 1 0
    //   176: astore_0
    //   177: new 327	java/lang/StringBuilder
    //   180: dup
    //   181: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   184: astore 7
    //   186: aload 7
    //   188: aload 5
    //   190: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   193: pop
    //   194: aload 7
    //   196: ldc_w 501
    //   199: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   202: pop
    //   203: aload 6
    //   205: aload 7
    //   207: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   210: invokeinterface 503 2 0
    //   215: istore 4
    //   217: iload 4
    //   219: ifeq +54 -> 273
    //   222: new 327	java/lang/StringBuilder
    //   225: dup
    //   226: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   229: astore 7
    //   231: aload 7
    //   233: aload 5
    //   235: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   238: pop
    //   239: aload 7
    //   241: ldc_w 501
    //   244: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: pop
    //   248: aload_0
    //   249: ldc_w 505
    //   252: aload 6
    //   254: aload 7
    //   256: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   259: ldc2_w 125
    //   262: invokeinterface 376 4 0
    //   267: invokeinterface 509 4 0
    //   272: pop
    //   273: new 327	java/lang/StringBuilder
    //   276: dup
    //   277: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   280: astore 7
    //   282: aload 7
    //   284: aload 5
    //   286: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   289: pop
    //   290: aload 7
    //   292: ldc_w 511
    //   295: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   298: pop
    //   299: aload 6
    //   301: aload 7
    //   303: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   306: invokeinterface 503 2 0
    //   311: istore 4
    //   313: iload 4
    //   315: ifeq +52 -> 367
    //   318: new 327	java/lang/StringBuilder
    //   321: dup
    //   322: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   325: astore 7
    //   327: aload 7
    //   329: aload 5
    //   331: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   334: pop
    //   335: aload 7
    //   337: ldc_w 511
    //   340: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   343: pop
    //   344: aload_0
    //   345: ldc_w 513
    //   348: aload 6
    //   350: aload 7
    //   352: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   355: aconst_null
    //   356: invokeinterface 382 3 0
    //   361: invokeinterface 517 3 0
    //   366: pop
    //   367: new 327	java/lang/StringBuilder
    //   370: dup
    //   371: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   374: astore 7
    //   376: aload 7
    //   378: aload 5
    //   380: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   383: pop
    //   384: aload 7
    //   386: ldc_w 519
    //   389: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   392: pop
    //   393: aload 6
    //   395: aload 7
    //   397: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   400: invokeinterface 503 2 0
    //   405: istore 4
    //   407: iload 4
    //   409: ifeq +52 -> 461
    //   412: new 327	java/lang/StringBuilder
    //   415: dup
    //   416: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   419: astore 7
    //   421: aload 7
    //   423: aload 5
    //   425: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   428: pop
    //   429: aload 7
    //   431: ldc_w 519
    //   434: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   437: pop
    //   438: aload_0
    //   439: ldc_w 521
    //   442: aload 6
    //   444: aload 7
    //   446: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   449: aconst_null
    //   450: invokeinterface 382 3 0
    //   455: invokeinterface 517 3 0
    //   460: pop
    //   461: new 327	java/lang/StringBuilder
    //   464: dup
    //   465: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   468: astore 7
    //   470: aload 7
    //   472: aload 5
    //   474: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   477: pop
    //   478: aload 7
    //   480: ldc_w 523
    //   483: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   486: pop
    //   487: aload 6
    //   489: aload 7
    //   491: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   494: invokeinterface 503 2 0
    //   499: istore 4
    //   501: iload 4
    //   503: ifeq +52 -> 555
    //   506: new 327	java/lang/StringBuilder
    //   509: dup
    //   510: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   513: astore 7
    //   515: aload 7
    //   517: aload 5
    //   519: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   522: pop
    //   523: aload 7
    //   525: ldc_w 523
    //   528: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   531: pop
    //   532: aload_0
    //   533: ldc_w 525
    //   536: aload 6
    //   538: aload 7
    //   540: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   543: iconst_0
    //   544: invokeinterface 350 3 0
    //   549: invokeinterface 529 3 0
    //   554: pop
    //   555: aload_0
    //   556: invokeinterface 371 1 0
    //   561: aload 6
    //   563: invokeinterface 362 1 0
    //   568: invokeinterface 532 1 0
    //   573: invokeinterface 371 1 0
    //   578: getstatic 102	com/amplitude/upgrade/AmplitudeClient:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   581: astore_0
    //   582: new 327	java/lang/StringBuilder
    //   585: dup
    //   586: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   589: astore 5
    //   591: aload 5
    //   593: ldc_w 534
    //   596: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   599: pop
    //   600: aload 5
    //   602: aload_2
    //   603: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   606: pop
    //   607: aload 5
    //   609: ldc_w 536
    //   612: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   615: pop
    //   616: aload 5
    //   618: aload_1
    //   619: invokevirtual 335	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   622: pop
    //   623: aload_0
    //   624: ldc 26
    //   626: aload 5
    //   628: invokevirtual 340	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   631: invokevirtual 540	com/amplitude/upgrade/AmplitudeLog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   634: pop
    //   635: iconst_1
    //   636: ireturn
    //   637: getstatic 102	com/amplitude/upgrade/AmplitudeClient:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   640: ldc 26
    //   642: ldc_w 542
    //   645: aload_0
    //   646: invokevirtual 546	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   649: pop
    //   650: invokestatic 421	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   653: ldc_w 548
    //   656: aload_0
    //   657: invokevirtual 427	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   660: pop
    //   661: iconst_0
    //   662: ireturn
    //   663: astore_1
    //   664: goto -636 -> 28
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	667	0	paramContext	Context
    //   0	667	1	paramString1	String
    //   0	667	2	paramString2	String
    //   117	2	3	i	int
    //   51	451	4	bool	boolean
    //   1	626	5	localObject	Object
    //   103	459	6	localSharedPreferences	SharedPreferences
    //   131	408	7	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   45	53	31	java/lang/Exception
    //   60	118	31	java/lang/Exception
    //   124	177	31	java/lang/Exception
    //   177	217	31	java/lang/Exception
    //   222	273	31	java/lang/Exception
    //   273	313	31	java/lang/Exception
    //   318	367	31	java/lang/Exception
    //   367	407	31	java/lang/Exception
    //   412	461	31	java/lang/Exception
    //   461	501	31	java/lang/Exception
    //   506	555	31	java/lang/Exception
    //   555	578	31	java/lang/Exception
    //   582	635	31	java/lang/Exception
    //   12	22	663	java/lang/Exception
  }
  
  static boolean upgradeSharedPrefsToDB(Context paramContext)
  {
    return upgradeSharedPrefsToDB(paramContext, null);
  }
  
  static boolean upgradeSharedPrefsToDB(Context paramContext, String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "com.amplitude.api";
    }
    paramString = DatabaseHelper.getDatabaseHelper(paramContext);
    Object localObject = paramString.getValue("device_id");
    Long localLong1 = paramString.getLongValue("previous_session_id");
    Long localLong2 = paramString.getLongValue("last_event_time");
    if ((!Utils.isEmptyString((String)localObject)) && (localLong1 != null) && (localLong2 != null)) {
      return true;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(str);
    ((StringBuilder)localObject).append(".");
    ((StringBuilder)localObject).append(paramContext.getPackageName());
    paramContext = paramContext.getSharedPreferences(((StringBuilder)localObject).toString(), 0);
    migrateStringValue(paramContext, "com.amplitude.api.deviceId", null, paramString, "device_id");
    migrateLongValue(paramContext, "com.amplitude.api.lastEventTime", -1L, paramString, "last_event_time");
    migrateLongValue(paramContext, "com.amplitude.api.lastEventId", -1L, paramString, "last_event_id");
    migrateLongValue(paramContext, "com.amplitude.api.lastIdentifyId", -1L, paramString, "last_identify_id");
    migrateLongValue(paramContext, "com.amplitude.api.previousSessionId", -1L, paramString, "previous_session_id");
    migrateStringValue(paramContext, "com.amplitude.api.userId", null, paramString, "user_id");
    migrateBooleanValue(paramContext, "com.amplitude.api.optOut", false, paramString, "opt_out");
    return true;
  }
  
  protected String bytesToHexString(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar1 = new char[16];
    char[] tmp8_6 = arrayOfChar1;
    tmp8_6[0] = 48;
    char[] tmp14_8 = tmp8_6;
    tmp14_8[1] = 49;
    char[] tmp20_14 = tmp14_8;
    tmp20_14[2] = 50;
    char[] tmp26_20 = tmp20_14;
    tmp26_20[3] = 51;
    char[] tmp32_26 = tmp26_20;
    tmp32_26[4] = 52;
    char[] tmp38_32 = tmp32_26;
    tmp38_32[5] = 53;
    char[] tmp44_38 = tmp38_32;
    tmp44_38[6] = 54;
    char[] tmp51_44 = tmp44_38;
    tmp51_44[7] = 55;
    char[] tmp58_51 = tmp51_44;
    tmp58_51[8] = 56;
    char[] tmp65_58 = tmp58_51;
    tmp65_58[9] = 57;
    char[] tmp72_65 = tmp65_58;
    tmp72_65[10] = 97;
    char[] tmp79_72 = tmp72_65;
    tmp79_72[11] = 98;
    char[] tmp86_79 = tmp79_72;
    tmp86_79[12] = 99;
    char[] tmp93_86 = tmp86_79;
    tmp93_86[13] = 100;
    char[] tmp100_93 = tmp93_86;
    tmp100_93[14] = 101;
    char[] tmp107_100 = tmp100_93;
    tmp107_100[15] = 102;
    tmp107_100;
    char[] arrayOfChar2 = new char[paramArrayOfByte.length * 2];
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      int k = i * 2;
      arrayOfChar2[k] = arrayOfChar1[(j >>> 4)];
      arrayOfChar2[(k + 1)] = arrayOfChar1[(j & 0xF)];
      i += 1;
    }
    return new String(arrayOfChar2);
  }
  
  public void clearUserProperties()
  {
    identify(new Identify().clearAll());
  }
  
  protected boolean contextAndApiKeySet(String paramString)
  {
    try
    {
      AmplitudeLog localAmplitudeLog;
      StringBuilder localStringBuilder;
      if (context == null)
      {
        localAmplitudeLog = logger;
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("context cannot be null, set context with initialize() before calling ");
        localStringBuilder.append(paramString);
        localAmplitudeLog.e("com.amplitude.api.AmplitudeClient", localStringBuilder.toString());
        return false;
      }
      if (Utils.isEmptyString(apiKey))
      {
        localAmplitudeLog = logger;
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("apiKey cannot be null or empty, set apiKey with initialize() before calling ");
        localStringBuilder.append(paramString);
        localAmplitudeLog.e("com.amplitude.api.AmplitudeClient", localStringBuilder.toString());
        return false;
      }
      return true;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public AmplitudeClient disableDiagnosticLogging()
  {
    Diagnostics.getLogger().disableLogging();
    return this;
  }
  
  public AmplitudeClient disableLocationListening()
  {
    runOnLogThread(new AmplitudeClient.3(this));
    return this;
  }
  
  public AmplitudeClient enableDiagnosticLogging()
  {
    if (!contextAndApiKeySet("enableDiagnosticLogging")) {
      return this;
    }
    Diagnostics.getLogger().enableLogging(httpClient, apiKey, deviceId);
    return this;
  }
  
  public AmplitudeClient enableForegroundTracking(Application paramApplication)
  {
    if (!usingForegroundTracking)
    {
      if (!contextAndApiKeySet("enableForegroundTracking()")) {
        return this;
      }
      if (Build.VERSION.SDK_INT >= 14) {
        paramApplication.registerActivityLifecycleCallbacks(new AmplitudeCallbacks(this));
      }
    }
    return this;
  }
  
  public AmplitudeClient enableLocationListening()
  {
    runOnLogThread(new AmplitudeClient.2(this));
    return this;
  }
  
  public AmplitudeClient enableLogging(boolean paramBoolean)
  {
    logger.setEnableLogging(paramBoolean);
    return this;
  }
  
  public AmplitudeClient enableNewDeviceIdPerInstall(boolean paramBoolean)
  {
    newDeviceIdPerInstall = paramBoolean;
    return this;
  }
  
  protected long getCurrentTimeMillis()
  {
    return System.currentTimeMillis();
  }
  
  public String getDeviceId()
  {
    return deviceId;
  }
  
  long getNextSequenceNumber()
  {
    sequenceNumber += 1L;
    dbHelper.insertOrReplaceKeyLongValue("sequence_number", Long.valueOf(sequenceNumber));
    return sequenceNumber;
  }
  
  public long getSessionId()
  {
    return sessionId;
  }
  
  public String getUserId()
  {
    return userId;
  }
  
  public void groupIdentify(String paramString, Object paramObject, Identify paramIdentify)
  {
    groupIdentify(paramString, paramObject, paramIdentify, false);
  }
  
  public void groupIdentify(String paramString, Object paramObject, Identify paramIdentify, boolean paramBoolean)
  {
    if ((paramIdentify != null) && (userPropertiesOperations.length() != 0) && (contextAndApiKeySet("groupIdentify()")))
    {
      if (Utils.isEmptyString(paramString)) {
        return;
      }
      try
      {
        paramObject = new JSONObject().put(paramString, paramObject);
        paramString = paramObject;
      }
      catch (JSONException paramObject)
      {
        logger.e("com.amplitude.api.AmplitudeClient", paramObject.toString());
        Diagnostics.getLogger().logError(String.format("Failed to generate Group Identify JSON Object for groupType %s", new Object[] { paramString }), paramObject);
        paramString = null;
      }
      logEventAsync("$groupidentify", null, null, null, paramString, userPropertiesOperations, getCurrentTimeMillis(), paramBoolean);
    }
  }
  
  public void identify(Identify paramIdentify)
  {
    identify(paramIdentify, false);
  }
  
  public void identify(Identify paramIdentify, boolean paramBoolean)
  {
    if ((paramIdentify != null) && (userPropertiesOperations.length() != 0))
    {
      if (!contextAndApiKeySet("identify()")) {
        return;
      }
      logEventAsync("$identify", null, null, userPropertiesOperations, null, null, getCurrentTimeMillis(), paramBoolean);
    }
  }
  
  public AmplitudeClient initialize(Context paramContext, String paramString)
  {
    return initialize(paramContext, paramString, null);
  }
  
  public AmplitudeClient initialize(Context paramContext, String paramString1, String paramString2)
  {
    return initialize(paramContext, paramString1, paramString2, null, false);
  }
  
  public AmplitudeClient initialize(Context paramContext, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (paramContext == null)
    {
      try
      {
        logger.e("com.amplitude.api.AmplitudeClient", "Argument context cannot be null in initialize()");
        return this;
      }
      catch (Throwable paramContext) {}
    }
    else
    {
      if (Utils.isEmptyString(paramString1))
      {
        logger.e("com.amplitude.api.AmplitudeClient", "Argument apiKey cannot be null or blank in initialize()");
        return this;
      }
      context = paramContext.getApplicationContext();
      apiKey = paramString1;
      dbHelper = DatabaseHelper.getDatabaseHelper(context, instanceName);
      String str = paramString3;
      if (Utils.isEmptyString(paramString3)) {
        str = "Android";
      }
      platform = str;
      runOnLogThread(new AmplitudeClient.1(this, paramContext, paramBoolean, paramString1, paramString2, this));
      return this;
    }
    throw paramContext;
  }
  
  boolean isInForeground()
  {
    return inForeground;
  }
  
  public boolean isOptedOut()
  {
    return optOut;
  }
  
  boolean isUsingForegroundTracking()
  {
    return usingForegroundTracking;
  }
  
  protected long logEvent(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2, JSONObject paramJSONObject3, JSONObject paramJSONObject4, JSONObject paramJSONObject5, long paramLong, boolean paramBoolean)
  {
    Object localObject1 = logger;
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("Logged event to Amplitude: ");
    ((StringBuilder)localObject2).append(paramString);
    ((AmplitudeLog)localObject1).d("com.amplitude.api.AmplitudeClient", ((StringBuilder)localObject2).toString());
    if (optOut) {
      return -1L;
    }
    int i;
    if ((trackingSessionEvents) && ((paramString.equals("session_start")) || (paramString.equals("session_end")))) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i == 0) && (!paramBoolean)) {
      if (!inForeground) {
        startNewSessionIfNeeded(paramLong);
      } else {
        refreshSessionTime(paramLong);
      }
    }
    localObject2 = new JSONObject();
    try
    {
      ((JSONObject)localObject2).put("event_type", replaceWithJSONNull(paramString));
      ((JSONObject)localObject2).put("timestamp", paramLong);
      localObject1 = userId;
      ((JSONObject)localObject2).put("user_id", replaceWithJSONNull(localObject1));
      localObject1 = deviceId;
      ((JSONObject)localObject2).put("device_id", replaceWithJSONNull(localObject1));
      if (paramBoolean) {
        paramLong = -1L;
      } else {
        paramLong = sessionId;
      }
      ((JSONObject)localObject2).put("session_id", paramLong);
      ((JSONObject)localObject2).put("uuid", UUID.randomUUID().toString());
      ((JSONObject)localObject2).put("sequence_number", getNextSequenceNumber());
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackVersionName();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("version_name", replaceWithJSONNull(((DeviceInfo)localObject1).getVersionName()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackOsName();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("os_name", replaceWithJSONNull(((DeviceInfo)localObject1).getOsName()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackOsVersion();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("os_version", replaceWithJSONNull(((DeviceInfo)localObject1).getOsVersion()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackDeviceBrand();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("device_brand", replaceWithJSONNull(((DeviceInfo)localObject1).getBrand()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackDeviceManufacturer();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("device_manufacturer", replaceWithJSONNull(((DeviceInfo)localObject1).getManufacturer()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackDeviceModel();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("device_model", replaceWithJSONNull(((DeviceInfo)localObject1).getModel()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackCarrier();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("carrier", replaceWithJSONNull(((DeviceInfo)localObject1).getCarrier()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackCountry();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("country", replaceWithJSONNull(((DeviceInfo)localObject1).getCountry()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackLanguage();
      if (paramBoolean)
      {
        localObject1 = deviceInfo;
        ((JSONObject)localObject2).put("language", replaceWithJSONNull(((DeviceInfo)localObject1).getLanguage()));
      }
      localObject1 = trackingOptions;
      paramBoolean = ((TrackingOptions)localObject1).shouldTrackPlatform();
      if (paramBoolean)
      {
        localObject1 = platform;
        ((JSONObject)localObject2).put("platform", localObject1);
      }
      localObject1 = new JSONObject();
      ((JSONObject)localObject1).put("name", "amplitude-android");
      ((JSONObject)localObject1).put("version", "2.23.2");
      ((JSONObject)localObject2).put("library", localObject1);
      localObject1 = paramJSONObject2;
      if (paramJSONObject2 == null) {
        localObject1 = new JSONObject();
      }
      if (apiPropertiesTrackingOptions != null)
      {
        paramJSONObject2 = apiPropertiesTrackingOptions;
        i = paramJSONObject2.length();
        if (i > 0)
        {
          paramJSONObject2 = apiPropertiesTrackingOptions;
          ((JSONObject)localObject1).put("tracking_options", paramJSONObject2);
        }
      }
      paramJSONObject2 = trackingOptions;
      paramBoolean = paramJSONObject2.shouldTrackLatLng();
      if (paramBoolean)
      {
        paramJSONObject2 = deviceInfo;
        paramJSONObject2 = paramJSONObject2.getMostRecentLocation();
        if (paramJSONObject2 != null)
        {
          JSONObject localJSONObject = new JSONObject();
          localJSONObject.put("lat", paramJSONObject2.getLatitude());
          localJSONObject.put("lng", paramJSONObject2.getLongitude());
          ((JSONObject)localObject1).put("location", localJSONObject);
        }
      }
      paramJSONObject2 = trackingOptions;
      paramBoolean = paramJSONObject2.shouldTrackAdid();
      if (paramBoolean)
      {
        paramJSONObject2 = deviceInfo;
        paramJSONObject2 = paramJSONObject2.getAdvertisingId();
        if (paramJSONObject2 != null)
        {
          paramJSONObject2 = deviceInfo;
          ((JSONObject)localObject1).put("androidADID", paramJSONObject2.getAdvertisingId());
        }
      }
      paramJSONObject2 = deviceInfo;
      ((JSONObject)localObject1).put("limit_ad_tracking", paramJSONObject2.isLimitAdTrackingEnabled());
      paramJSONObject2 = deviceInfo;
      ((JSONObject)localObject1).put("gps_enabled", paramJSONObject2.isGooglePlayServicesEnabled());
      ((JSONObject)localObject2).put("api_properties", localObject1);
      if (paramJSONObject1 == null) {
        paramJSONObject1 = new JSONObject();
      } else {
        paramJSONObject1 = truncate(paramJSONObject1);
      }
      ((JSONObject)localObject2).put("event_properties", paramJSONObject1);
      if (paramJSONObject3 == null) {
        paramJSONObject1 = new JSONObject();
      } else {
        paramJSONObject1 = truncate(paramJSONObject3);
      }
      ((JSONObject)localObject2).put("user_properties", paramJSONObject1);
      if (paramJSONObject4 == null) {
        paramJSONObject1 = new JSONObject();
      } else {
        paramJSONObject1 = truncate(paramJSONObject4);
      }
      ((JSONObject)localObject2).put("groups", paramJSONObject1);
      if (paramJSONObject5 == null) {
        paramJSONObject1 = new JSONObject();
      } else {
        paramJSONObject1 = truncate(paramJSONObject5);
      }
      ((JSONObject)localObject2).put("group_properties", paramJSONObject1);
      paramLong = saveEvent(paramString, (JSONObject)localObject2);
      return paramLong;
    }
    catch (JSONException paramJSONObject1)
    {
      logger.e("com.amplitude.api.AmplitudeClient", String.format("JSON Serialization of event type %s failed, skipping: %s", new Object[] { paramString, paramJSONObject1.toString() }));
      Diagnostics.getLogger().logError(String.format("Failed to JSON serialize event type %s", new Object[] { paramString }), paramJSONObject1);
    }
    return -1L;
  }
  
  public void logEvent(String paramString)
  {
    logEvent(paramString, null);
  }
  
  public void logEvent(String paramString, JSONObject paramJSONObject)
  {
    logEvent(paramString, paramJSONObject, false);
  }
  
  public void logEvent(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2)
  {
    logEvent(paramString, paramJSONObject1, paramJSONObject2, false);
  }
  
  public void logEvent(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2, long paramLong, boolean paramBoolean)
  {
    if (validateLogEvent(paramString)) {
      logEventAsync(paramString, paramJSONObject1, null, null, paramJSONObject2, null, paramLong, paramBoolean);
    }
  }
  
  public void logEvent(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2, boolean paramBoolean)
  {
    logEvent(paramString, paramJSONObject1, paramJSONObject2, getCurrentTimeMillis(), paramBoolean);
  }
  
  public void logEvent(String paramString, JSONObject paramJSONObject, boolean paramBoolean)
  {
    logEvent(paramString, paramJSONObject, null, paramBoolean);
  }
  
  protected void logEventAsync(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2, JSONObject paramJSONObject3, JSONObject paramJSONObject4, JSONObject paramJSONObject5, long paramLong, boolean paramBoolean)
  {
    JSONObject localJSONObject = paramJSONObject1;
    if (paramJSONObject1 != null) {
      localJSONObject = Utils.cloneJSONObject(paramJSONObject1);
    }
    paramJSONObject1 = paramJSONObject2;
    if (paramJSONObject2 != null) {
      paramJSONObject1 = Utils.cloneJSONObject(paramJSONObject2);
    }
    paramJSONObject2 = paramJSONObject3;
    if (paramJSONObject3 != null) {
      paramJSONObject2 = Utils.cloneJSONObject(paramJSONObject3);
    }
    paramJSONObject3 = paramJSONObject4;
    if (paramJSONObject4 != null) {
      paramJSONObject3 = Utils.cloneJSONObject(paramJSONObject4);
    }
    paramJSONObject4 = paramJSONObject5;
    if (paramJSONObject5 != null) {
      paramJSONObject4 = Utils.cloneJSONObject(paramJSONObject5);
    }
    runOnLogThread(new AmplitudeClient.5(this, paramString, localJSONObject, paramJSONObject1, paramJSONObject2, paramJSONObject3, paramJSONObject4, paramLong, paramBoolean));
  }
  
  public void logEventSync(String paramString)
  {
    logEventSync(paramString, null);
  }
  
  public void logEventSync(String paramString, JSONObject paramJSONObject)
  {
    logEventSync(paramString, paramJSONObject, false);
  }
  
  public void logEventSync(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2)
  {
    logEventSync(paramString, paramJSONObject1, paramJSONObject2, false);
  }
  
  public void logEventSync(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2, long paramLong, boolean paramBoolean)
  {
    if (validateLogEvent(paramString)) {
      logEvent(paramString, paramJSONObject1, null, null, paramJSONObject2, null, paramLong, paramBoolean);
    }
  }
  
  public void logEventSync(String paramString, JSONObject paramJSONObject1, JSONObject paramJSONObject2, boolean paramBoolean)
  {
    logEventSync(paramString, paramJSONObject1, paramJSONObject2, getCurrentTimeMillis(), paramBoolean);
  }
  
  public void logEventSync(String paramString, JSONObject paramJSONObject, boolean paramBoolean)
  {
    logEventSync(paramString, paramJSONObject, null, paramBoolean);
  }
  
  public void logRevenue(double paramDouble)
  {
    logRevenue(null, 1, paramDouble);
  }
  
  public void logRevenue(String paramString, int paramInt, double paramDouble)
  {
    logRevenue(paramString, paramInt, paramDouble, null, null);
  }
  
  public void logRevenue(String paramString1, int paramInt, double paramDouble, String paramString2, String paramString3)
  {
    if (!contextAndApiKeySet("logRevenue()")) {
      return;
    }
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("special", "revenue_amount");
      localJSONObject.put("productId", paramString1);
      localJSONObject.put("quantity", paramInt);
      localJSONObject.put("price", paramDouble);
      localJSONObject.put("receipt", paramString2);
      localJSONObject.put("receiptSig", paramString3);
    }
    catch (JSONException paramString1)
    {
      Diagnostics.getLogger().logError("Failed to generate API Properties JSON for revenue event", paramString1);
    }
    logEventAsync("revenue_amount", null, localJSONObject, null, null, null, getCurrentTimeMillis(), false);
  }
  
  public void logRevenueV2(Revenue paramRevenue)
  {
    if ((contextAndApiKeySet("logRevenueV2()")) && (paramRevenue != null))
    {
      if (!paramRevenue.isValidRevenue()) {
        return;
      }
      logEvent("revenue_amount", paramRevenue.toJSONObject());
    }
  }
  
  protected void makeEventUploadPostRequest(OkHttpClient paramOkHttpClient, String paramString, long paramLong1, long paramLong2)
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("");
    ((StringBuilder)localObject1).append(getCurrentTimeMillis());
    String str2 = ((StringBuilder)localObject1).toString();
    localObject1 = "";
    try
    {
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("2");
      String str3 = apiKey;
      ((StringBuilder)localObject2).append(str3);
      ((StringBuilder)localObject2).append(paramString);
      ((StringBuilder)localObject2).append(str2);
      localObject2 = ((StringBuilder)localObject2).toString();
      localObject2 = bytesToHexString(new MD4().digest(((String)localObject2).getBytes("UTF-8")));
      localObject1 = localObject2;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      logger.e("com.amplitude.api.AmplitudeClient", localUnsupportedEncodingException.toString());
      Diagnostics.getLogger().logError("Failed to compute checksum for upload request", localUnsupportedEncodingException);
    }
    paramString = new FormBody.Builder().add("v", "2").add("client", apiKey).add("e", paramString).add("upload_time", str2).add("checksum", (String)localObject1).build();
    try
    {
      localObject1 = new Request.Builder();
      String str1 = accessTokenUrl;
      paramString = ((Request.Builder)localObject1).url(str1).post(paramString).build();
      int j = 1;
      int k = 1;
      int m = 1;
      int n = 1;
      int i1 = 1;
      int i = 1;
      try
      {
        paramString = paramOkHttpClient.newCall(paramString).execute();
        paramOkHttpClient = paramString.body().string();
        boolean bool = paramOkHttpClient.equals("success");
        if (bool)
        {
          paramOkHttpClient = logThread;
          try
          {
            paramOkHttpClient.post(new AmplitudeClient.14(this, paramLong1, paramLong2));
            i = i1;
          }
          catch (Exception paramOkHttpClient)
          {
            break label657;
          }
          catch (AssertionError paramOkHttpClient)
          {
            i = j;
            break label693;
          }
          catch (IOException paramOkHttpClient)
          {
            i = k;
            break label729;
          }
          catch (UnknownHostException paramOkHttpClient)
          {
            i = m;
            break label768;
          }
          catch (ConnectException paramOkHttpClient)
          {
            i = n;
            break label791;
          }
        }
        else
        {
          bool = paramOkHttpClient.equals("invalid_api_key");
          if (bool)
          {
            paramOkHttpClient = logger;
            paramOkHttpClient.e("com.amplitude.api.AmplitudeClient", "Invalid API key, make sure your API key is correct in initialize()");
          }
          else
          {
            bool = paramOkHttpClient.equals("bad_checksum");
            if (bool)
            {
              paramOkHttpClient = logger;
              paramOkHttpClient.w("com.amplitude.api.AmplitudeClient", "Bad checksum, post request was mangled in transit, will attempt to reupload later");
            }
            else
            {
              bool = paramOkHttpClient.equals("request_db_write_failed");
              if (bool)
              {
                paramOkHttpClient = logger;
                paramOkHttpClient.w("com.amplitude.api.AmplitudeClient", "Couldn't write to request database on server, will attempt to reupload later");
              }
              else
              {
                i = paramString.code();
                if (i == 413)
                {
                  if ((backoffUpload) && (backoffUploadBatchSize == 1))
                  {
                    if (paramLong1 >= 0L)
                    {
                      paramOkHttpClient = dbHelper;
                      paramOkHttpClient.removeEvent(paramLong1);
                    }
                    if (paramLong2 >= 0L)
                    {
                      paramOkHttpClient = dbHelper;
                      paramOkHttpClient.removeIdentify(paramLong2);
                    }
                  }
                  backoffUpload = true;
                  paramOkHttpClient = dbHelper;
                  paramLong1 = paramOkHttpClient.getEventCount();
                  i = (int)paramLong1;
                  j = backoffUploadBatchSize;
                  i = Math.min(i, j);
                  double d = i / 2.0D;
                  d = Math.ceil(d);
                  backoffUploadBatchSize = ((int)d);
                  paramOkHttpClient = logger;
                  paramOkHttpClient.w("com.amplitude.api.AmplitudeClient", "Request too large, will decrease size and attempt to reupload");
                  paramOkHttpClient = logThread;
                  paramOkHttpClient.post(new AmplitudeClient.15(this));
                }
                else
                {
                  paramString = logger;
                  localObject1 = new StringBuilder();
                  ((StringBuilder)localObject1).append("Upload failed, ");
                  ((StringBuilder)localObject1).append(paramOkHttpClient);
                  ((StringBuilder)localObject1).append(", will attempt to reupload later");
                  paramString.w("com.amplitude.api.AmplitudeClient", ((StringBuilder)localObject1).toString());
                }
              }
            }
          }
          i = 0;
        }
      }
      catch (Exception paramOkHttpClient)
      {
        i = 0;
        logger.e("com.amplitude.api.AmplitudeClient", "Exception:", paramOkHttpClient);
        lastError = paramOkHttpClient;
        Diagnostics.getLogger().logError("Failed to post upload request", paramOkHttpClient);
      }
      catch (AssertionError paramOkHttpClient)
      {
        i = 0;
        logger.e("com.amplitude.api.AmplitudeClient", "Exception:", paramOkHttpClient);
        lastError = paramOkHttpClient;
        Diagnostics.getLogger().logError("Failed to post upload request", paramOkHttpClient);
      }
      catch (IOException paramOkHttpClient)
      {
        i = 0;
        logger.e("com.amplitude.api.AmplitudeClient", ((IOException)paramOkHttpClient).toString());
        lastError = paramOkHttpClient;
        Diagnostics.getLogger().logError("Failed to post upload request", paramOkHttpClient);
      }
      catch (UnknownHostException paramOkHttpClient)
      {
        i = 0;
        lastError = paramOkHttpClient;
        Diagnostics.getLogger().logError("Failed to post upload request", paramOkHttpClient);
      }
      catch (ConnectException paramOkHttpClient)
      {
        label657:
        label693:
        label729:
        label768:
        i = 0;
        label791:
        lastError = paramOkHttpClient;
        Diagnostics.getLogger().logError("Failed to post upload request", paramOkHttpClient);
      }
      if (i == 0)
      {
        uploadingCurrently.set(false);
        return;
      }
    }
    catch (IllegalArgumentException paramOkHttpClient)
    {
      logger.e("com.amplitude.api.AmplitudeClient", paramOkHttpClient.toString());
      uploadingCurrently.set(false);
      Diagnostics.getLogger().logError("Failed to build upload request", paramOkHttpClient);
    }
  }
  
  protected Pair mergeEventsAndIdentifys(List paramList1, List paramList2, long paramLong)
    throws JSONException
  {
    JSONArray localJSONArray = new JSONArray();
    long l3 = -1L;
    long l2 = -1L;
    if (localJSONArray.length() < paramLong)
    {
      boolean bool1 = paramList1.isEmpty();
      boolean bool2 = paramList2.isEmpty();
      if ((bool1) && (bool2))
      {
        logger.w("com.amplitude.api.AmplitudeClient", String.format("mergeEventsAndIdentifys: number of events and identifys less than expected by %d", new Object[] { Long.valueOf(paramLong - localJSONArray.length()) }));
      }
      else
      {
        JSONObject localJSONObject;
        long l1;
        if (bool2)
        {
          localJSONObject = (JSONObject)paramList1.remove(0);
          l1 = localJSONObject.getLong("event_id");
          localJSONArray.put(localJSONObject);
        }
        for (;;)
        {
          l3 = l1;
          break;
          if (bool1)
          {
            localJSONObject = (JSONObject)paramList2.remove(0);
            l1 = localJSONObject.getLong("event_id");
            localJSONArray.put(localJSONObject);
          }
          for (;;)
          {
            l2 = l1;
            break;
            if ((!((JSONObject)paramList1.get(0)).has("sequence_number")) || (((JSONObject)paramList1.get(0)).getLong("sequence_number") < ((JSONObject)paramList2.get(0)).getLong("sequence_number"))) {
              break label264;
            }
            localJSONObject = (JSONObject)paramList2.remove(0);
            l1 = localJSONObject.getLong("event_id");
            localJSONArray.put(localJSONObject);
          }
          label264:
          localJSONObject = (JSONObject)paramList1.remove(0);
          l1 = localJSONObject.getLong("event_id");
          localJSONArray.put(localJSONObject);
        }
      }
    }
    return new Pair(new Pair(Long.valueOf(l3), Long.valueOf(l2)), localJSONArray);
  }
  
  void onEnterForeground(long paramLong)
  {
    runOnLogThread(new AmplitudeClient.7(this, paramLong));
  }
  
  void onExitForeground(long paramLong)
  {
    runOnLogThread(new AmplitudeClient.6(this, paramLong));
  }
  
  void refreshSessionTime(long paramLong)
  {
    if (!inSession()) {
      return;
    }
    setLastEventTime(paramLong);
  }
  
  public AmplitudeClient regenerateDeviceId()
  {
    if (!contextAndApiKeySet("regenerateDeviceId()")) {
      return this;
    }
    runOnLogThread(new AmplitudeClient.10(this, this));
    return this;
  }
  
  protected Object replaceWithJSONNull(Object paramObject)
  {
    Object localObject = paramObject;
    if (paramObject == null) {
      localObject = JSONObject.NULL;
    }
    return localObject;
  }
  
  protected void runOnLogThread(Runnable paramRunnable)
  {
    if (Thread.currentThread() != logThread)
    {
      logThread.post(paramRunnable);
      return;
    }
    paramRunnable.run();
  }
  
  protected long saveEvent(String paramString, JSONObject paramJSONObject)
  {
    paramJSONObject = paramJSONObject.toString();
    if (Utils.isEmptyString(paramJSONObject))
    {
      logger.e("com.amplitude.api.AmplitudeClient", String.format("Detected empty event string for event type %s, skipping", new Object[] { paramString }));
      return -1L;
    }
    if ((!paramString.equals("$identify")) && (!paramString.equals("$groupidentify")))
    {
      lastEventId = dbHelper.addEvent(paramJSONObject);
      setLastEventId(lastEventId);
    }
    else
    {
      lastIdentifyId = dbHelper.addIdentify(paramJSONObject);
      setLastIdentifyId(lastIdentifyId);
    }
    int i = Math.min(Math.max(1, eventMaxCount / 10), 20);
    if (dbHelper.getEventCount() > eventMaxCount) {
      dbHelper.removeEvents(dbHelper.getNthEventId(i));
    }
    if (dbHelper.getIdentifyCount() > eventMaxCount) {
      dbHelper.removeIdentifys(dbHelper.getNthIdentifyId(i));
    }
    long l = dbHelper.getTotalEventCount();
    if ((l % eventUploadThreshold == 0L) && (l >= eventUploadThreshold)) {
      updateServer();
    } else {
      updateServerLater(eventUploadPeriodMillis);
    }
    if ((!paramString.equals("$identify")) && (!paramString.equals("$groupidentify"))) {
      return lastEventId;
    }
    return lastIdentifyId;
  }
  
  public AmplitudeClient setDeviceId(String paramString)
  {
    Set localSet = getInvalidDeviceIds();
    if ((contextAndApiKeySet("setDeviceId()")) && (!Utils.isEmptyString(paramString)))
    {
      if (localSet.contains(paramString)) {
        return this;
      }
      runOnLogThread(new AmplitudeClient.9(this, this, paramString));
    }
    return this;
  }
  
  public AmplitudeClient setDiagnosticEventMaxCount(int paramInt)
  {
    Diagnostics.getLogger().setDiagnosticEventMaxCount(paramInt);
    return this;
  }
  
  public AmplitudeClient setEventMaxCount(int paramInt)
  {
    eventMaxCount = paramInt;
    return this;
  }
  
  public AmplitudeClient setEventUploadMaxBatchSize(int paramInt)
  {
    eventUploadMaxBatchSize = paramInt;
    backoffUploadBatchSize = paramInt;
    return this;
  }
  
  public AmplitudeClient setEventUploadPeriodMillis(int paramInt)
  {
    eventUploadPeriodMillis = paramInt;
    return this;
  }
  
  public AmplitudeClient setEventUploadThreshold(int paramInt)
  {
    eventUploadThreshold = paramInt;
    return this;
  }
  
  public AmplitudeClient setFlushEventsOnClose(boolean paramBoolean)
  {
    flushEventsOnClose = paramBoolean;
    return this;
  }
  
  public void setGroup(String paramString, Object paramObject)
  {
    if (contextAndApiKeySet("setGroup()"))
    {
      if (Utils.isEmptyString(paramString)) {
        return;
      }
      JSONObject localJSONObject2;
      try
      {
        JSONObject localJSONObject1 = new JSONObject().put(paramString, paramObject);
      }
      catch (JSONException localJSONException)
      {
        logger.e("com.amplitude.api.AmplitudeClient", localJSONException.toString());
        Diagnostics.getLogger().logError(String.format("Failed to generate Group JSON for groupType: %s", new Object[] { paramString }), localJSONException);
        localJSONObject2 = null;
      }
      logEventAsync("$identify", null, null, IdentifysetUserPropertyuserPropertiesOperations, localJSONObject2, null, getCurrentTimeMillis(), false);
    }
  }
  
  void setLastEventId(long paramLong)
  {
    lastEventId = paramLong;
    dbHelper.insertOrReplaceKeyLongValue("last_event_id", Long.valueOf(paramLong));
  }
  
  void setLastEventTime(long paramLong)
  {
    lastEventTime = paramLong;
    dbHelper.insertOrReplaceKeyLongValue("last_event_time", Long.valueOf(paramLong));
  }
  
  void setLastIdentifyId(long paramLong)
  {
    lastIdentifyId = paramLong;
    dbHelper.insertOrReplaceKeyLongValue("last_identify_id", Long.valueOf(paramLong));
  }
  
  public AmplitudeClient setLogLevel(int paramInt)
  {
    logger.setLogLevel(paramInt);
    return this;
  }
  
  public AmplitudeClient setMinTimeBetweenSessionsMillis(long paramLong)
  {
    minTimeBetweenSessionsMillis = paramLong;
    return this;
  }
  
  public AmplitudeClient setOffline(boolean paramBoolean)
  {
    offline = paramBoolean;
    if (!paramBoolean) {
      uploadEvents();
    }
    return this;
  }
  
  public AmplitudeClient setOptOut(boolean paramBoolean)
  {
    if (!contextAndApiKeySet("setOptOut()")) {
      return this;
    }
    runOnLogThread(new AmplitudeClient.4(this, this, paramBoolean));
    return this;
  }
  
  void setPreviousSessionId(long paramLong)
  {
    previousSessionId = paramLong;
    dbHelper.insertOrReplaceKeyLongValue("previous_session_id", Long.valueOf(paramLong));
  }
  
  public AmplitudeClient setServerUrl(String paramString)
  {
    if (!Utils.isEmptyString(paramString)) {
      accessTokenUrl = paramString;
    }
    return this;
  }
  
  public AmplitudeClient setSessionTimeoutMillis(long paramLong)
  {
    sessionTimeoutMillis = paramLong;
    return this;
  }
  
  public AmplitudeClient setTrackingOptions(TrackingOptions paramTrackingOptions)
  {
    trackingOptions = paramTrackingOptions;
    apiPropertiesTrackingOptions = paramTrackingOptions.getApiPropertiesTrackingOptions();
    return this;
  }
  
  public AmplitudeClient setUserId(String paramString)
  {
    return setUserId(paramString, false);
  }
  
  public AmplitudeClient setUserId(String paramString, boolean paramBoolean)
  {
    if (!contextAndApiKeySet("setUserId()")) {
      return this;
    }
    runOnLogThread(new AmplitudeClient.8(this, this, paramBoolean, paramString));
    return this;
  }
  
  public void setUserProperties(JSONObject paramJSONObject)
  {
    if ((paramJSONObject != null) && (paramJSONObject.length() != 0))
    {
      if (!contextAndApiKeySet("setUserProperties")) {
        return;
      }
      paramJSONObject = truncate(paramJSONObject);
      if (paramJSONObject.length() == 0) {
        return;
      }
      Identify localIdentify = new Identify();
      Iterator localIterator = paramJSONObject.keys();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        try
        {
          localIdentify.setUserProperty(str, paramJSONObject.get(str));
        }
        catch (JSONException localJSONException)
        {
          logger.e("com.amplitude.api.AmplitudeClient", localJSONException.toString());
          Diagnostics.getLogger().logError(String.format("Failed to set user property %s", new Object[] { str }), localJSONException);
        }
      }
      identify(localIdentify);
    }
  }
  
  public void setUserProperties(JSONObject paramJSONObject, boolean paramBoolean)
  {
    setUserProperties(paramJSONObject);
  }
  
  public boolean startNewSessionIfNeeded(long paramLong)
  {
    if (inSession())
    {
      if (isWithinMinTimeBetweenSessions(paramLong))
      {
        refreshSessionTime(paramLong);
        return false;
      }
      startNewSession(paramLong);
      return true;
    }
    if (isWithinMinTimeBetweenSessions(paramLong))
    {
      if (previousSessionId == -1L)
      {
        startNewSession(paramLong);
        return true;
      }
      setSessionId(previousSessionId);
      refreshSessionTime(paramLong);
      return false;
    }
    startNewSession(paramLong);
    return true;
  }
  
  public AmplitudeClient trackSessionEvents(boolean paramBoolean)
  {
    trackingSessionEvents = paramBoolean;
    return this;
  }
  
  public JSONArray truncate(JSONArray paramJSONArray)
    throws JSONException
  {
    if (paramJSONArray == null) {
      return new JSONArray();
    }
    int i = 0;
    while (i < paramJSONArray.length())
    {
      Object localObject = paramJSONArray.get(i);
      if (localObject.getClass().equals(String.class)) {
        paramJSONArray.put(i, truncate((String)localObject));
      } else if (localObject.getClass().equals(JSONObject.class)) {
        paramJSONArray.put(i, truncate((JSONObject)localObject));
      } else if (localObject.getClass().equals(JSONArray.class)) {
        paramJSONArray.put(i, truncate((JSONArray)localObject));
      }
      i += 1;
    }
    return paramJSONArray;
  }
  
  public JSONObject truncate(JSONObject paramJSONObject)
  {
    if (paramJSONObject == null) {
      return new JSONObject();
    }
    if (paramJSONObject.length() > 1000)
    {
      logger.w("com.amplitude.api.AmplitudeClient", "Warning: too many properties (more than 1000), ignoring");
      return new JSONObject();
    }
    Iterator localIterator = paramJSONObject.keys();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      try
      {
        Object localObject = paramJSONObject.get(str);
        boolean bool = str.equals("$receipt");
        if (!bool)
        {
          bool = str.equals("$receiptSig");
          if (!bool)
          {
            bool = localObject.getClass().equals(String.class);
            if (bool)
            {
              localObject = (String)localObject;
              paramJSONObject.put(str, truncate((String)localObject));
              continue;
            }
            bool = localObject.getClass().equals(JSONObject.class);
            if (bool)
            {
              localObject = (JSONObject)localObject;
              paramJSONObject.put(str, truncate((JSONObject)localObject));
              continue;
            }
            bool = localObject.getClass().equals(JSONArray.class);
            if (!bool) {
              continue;
            }
            localObject = (JSONArray)localObject;
            paramJSONObject.put(str, truncate((JSONArray)localObject));
            continue;
          }
        }
        paramJSONObject.put(str, localObject);
      }
      catch (JSONException localJSONException)
      {
        logger.e("com.amplitude.api.AmplitudeClient", localJSONException.toString());
      }
    }
    return paramJSONObject;
  }
  
  protected void updateServer()
  {
    updateServer(false);
    Diagnostics.getLogger().flushEvents();
  }
  
  protected void updateServer(boolean paramBoolean)
  {
    if (!optOut)
    {
      if (offline) {
        return;
      }
      if (!uploadingCurrently.getAndSet(true))
      {
        long l1 = dbHelper.getTotalEventCount();
        if (paramBoolean) {}
        for (int i = backoffUploadBatchSize;; i = eventUploadMaxBatchSize)
        {
          l2 = i;
          break;
        }
        l1 = Math.min(l2, l1);
        if (l1 <= 0L)
        {
          uploadingCurrently.set(false);
          return;
        }
        Object localObject1 = dbHelper;
        long l2 = lastEventId;
        try
        {
          localObject1 = ((DatabaseHelper)localObject1).getEvents(l2, l1);
          Object localObject2 = dbHelper;
          l2 = lastIdentifyId;
          localObject1 = mergeEventsAndIdentifys((List)localObject1, ((DatabaseHelper)localObject2).getIdentifys(l2, l1), l1);
          localObject2 = (JSONArray)second;
          i = ((JSONArray)localObject2).length();
          if (i == 0)
          {
            localObject1 = uploadingCurrently;
            ((AtomicBoolean)localObject1).set(false);
            return;
          }
          localObject2 = (Long)first).first;
          l1 = ((Long)localObject2).longValue();
          localObject2 = (Long)first).second;
          l2 = ((Long)localObject2).longValue();
          localObject1 = (JSONArray)second;
          localObject1 = ((JSONArray)localObject1).toString();
          localObject2 = httpThread;
          ((WorkerThread)localObject2).post(new AmplitudeClient.13(this, (String)localObject1, l1, l2));
          return;
        }
        catch (CursorWindowAllocationException localCursorWindowAllocationException)
        {
          uploadingCurrently.set(false);
          logger.e("com.amplitude.api.AmplitudeClient", String.format("Caught Cursor window exception during event upload, deferring upload: %s", new Object[] { localCursorWindowAllocationException.getMessage() }));
          Diagnostics.getLogger().logError("Failed to update server", localCursorWindowAllocationException);
          return;
        }
        catch (JSONException localJSONException)
        {
          uploadingCurrently.set(false);
          logger.e("com.amplitude.api.AmplitudeClient", localJSONException.toString());
          Diagnostics.getLogger().logError("Failed to update server", localJSONException);
        }
      }
    }
  }
  
  public void uploadEvents()
  {
    if (!contextAndApiKeySet("uploadEvents()")) {
      return;
    }
    logThread.post(new AmplitudeClient.11(this));
  }
  
  public AmplitudeClient useAdvertisingIdForDeviceId()
  {
    useAdvertisingIdForDeviceId = true;
    return this;
  }
  
  void useForegroundTracking()
  {
    usingForegroundTracking = true;
  }
  
  protected boolean validateLogEvent(String paramString)
  {
    if (Utils.isEmptyString(paramString))
    {
      logger.e("com.amplitude.api.AmplitudeClient", "Argument eventType cannot be null or blank in logEvent()");
      return false;
    }
    return contextAndApiKeySet("logEvent()");
  }
}
