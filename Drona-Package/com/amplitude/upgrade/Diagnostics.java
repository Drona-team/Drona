package com.amplitude.upgrade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

public class Diagnostics
{
  public static final int DIAGNOSTIC_EVENT_API_VERSION = 1;
  public static final String DIAGNOSTIC_EVENT_ENDPOINT = "https://api.amplitude.com/diagnostic";
  public static final int DIAGNOSTIC_EVENT_MAX_COUNT = 50;
  public static final int DIAGNOSTIC_EVENT_MIN_COUNT = 5;
  protected static Diagnostics instance;
  private volatile String apiKey;
  String baseUrl = "https://api.amplitude.com/diagnostic";
  private volatile String deviceId;
  int diagnosticEventMaxCount = 50;
  WorkerThread diagnosticThread = new WorkerThread("diagnosticThread");
  volatile boolean enabled = false;
  private volatile OkHttpClient httpClient;
  List<String> unsentErrorStrings = new ArrayList(diagnosticEventMaxCount);
  Map<String, JSONObject> unsentErrors = new HashMap(diagnosticEventMaxCount);
  
  private Diagnostics()
  {
    diagnosticThread.start();
  }
  
  static Diagnostics getLogger()
  {
    try
    {
      if (instance == null) {
        instance = new Diagnostics();
      }
      Diagnostics localDiagnostics = instance;
      return localDiagnostics;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  Diagnostics disableLogging()
  {
    enabled = false;
    return this;
  }
  
  Diagnostics enableLogging(OkHttpClient paramOkHttpClient, String paramString1, String paramString2)
  {
    enabled = true;
    apiKey = paramString1;
    httpClient = paramOkHttpClient;
    deviceId = paramString2;
    return this;
  }
  
  Diagnostics flushEvents()
  {
    if ((enabled) && (!Utils.isEmptyString(apiKey)) && (httpClient != null))
    {
      if (Utils.isEmptyString(deviceId)) {
        return this;
      }
      runOnBgThread(new Diagnostics.3(this));
    }
    return this;
  }
  
  Diagnostics logError(String paramString)
  {
    return logError(paramString, null);
  }
  
  Diagnostics logError(String paramString, Throwable paramThrowable)
  {
    if ((enabled) && (!Utils.isEmptyString(paramString)))
    {
      if (Utils.isEmptyString(deviceId)) {
        return this;
      }
      runOnBgThread(new Diagnostics.2(this, paramString, paramThrowable));
    }
    return this;
  }
  
  protected void makeEventUploadPostRequest(String paramString)
  {
    paramString = new FormBody.Builder().add("v", "1").add("client", apiKey).add("e", paramString);
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("");
    ((StringBuilder)localObject).append(System.currentTimeMillis());
    paramString = paramString.add("upload_time", ((StringBuilder)localObject).toString()).build();
    paramString = new Request.Builder().url(baseUrl).post(paramString).build();
    localObject = httpClient;
    try
    {
      boolean bool = ((OkHttpClient)localObject).newCall(paramString).execute().body().string().equals("success");
      if (bool)
      {
        paramString = unsentErrors;
        paramString.clear();
        paramString = unsentErrorStrings;
        paramString.clear();
        return;
      }
    }
    catch (IOException paramString) {}catch (AssertionError paramString) {}catch (Exception paramString) {}
  }
  
  protected void runOnBgThread(Runnable paramRunnable)
  {
    if (Thread.currentThread() != diagnosticThread)
    {
      diagnosticThread.post(paramRunnable);
      return;
    }
    paramRunnable.run();
  }
  
  Diagnostics setDiagnosticEventMaxCount(int paramInt)
  {
    runOnBgThread(new Diagnostics.1(this, this, paramInt));
    return this;
  }
}
