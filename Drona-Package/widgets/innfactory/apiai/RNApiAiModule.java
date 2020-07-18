package widgets.innfactory.apiai;

import ai.api.model.AIContext;
import ai.api.model.Entity;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import client.testing.AIConfiguration.SupportedLanguages;
import client.testing.AIListener;
import client.testing.AIServiceException;
import client.testing.android.AIConfiguration;
import client.testing.android.AIConfiguration.RecognitionEngine;
import client.testing.android.AIDataService;
import client.testing.android.AIService;
import client.testing.android.SessionIdStorage;
import client.testing.model.AIError;
import client.testing.model.AIRequest;
import client.testing.model.AIResponse;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RNApiAiModule
  extends ReactContextBaseJavaModule
  implements AIListener
{
  private static final String LANG_CHINESE_CHINA = "LANG_CHINESE_CHINA";
  private static final String LANG_CHINESE_HONGKONG = "LANG_CHINESE_HONGKONG";
  private static final String LANG_CHINESE_TAIWAN = "LANG_CHINESE_TAIWAN";
  private static final String LANG_DUTCH = "LANG_DUTCH";
  private static final String LANG_ENGLISH = "LANG_ENGLISH";
  private static final String LANG_ENGLISH_GB = "LANG_ENGLISH_GB";
  private static final String LANG_ENGLISH_US = "LANG_ENGLISH_US";
  private static final String LANG_FRENCH = "LANG_FRENCH";
  private static final String LANG_GERMAN = "LANG_GERMAN";
  private static final String LANG_ITALIAN = "LANG_ITALIAN";
  private static final String LANG_JAPANESE = "LANG_JAPANESE";
  private static final String LANG_KOREAN = "LANG_KOREAN";
  private static final String LANG_PORTUGUESE = "LANG_PORTUGUESE";
  private static final String LANG_PORTUGUESE_BRAZIL = "LANG_PORTUGUESE_BRAZIL";
  private static final String LANG_RUSSIAN = "LANG_RUSSIAN";
  private static final String LANG_SPANISH = "LANG_SPANISH";
  private static final String LANG_UKRAINIAN = "LANG_UKRAINIAN";
  private static final String PAGE_KEY = "ApiAi";
  private String accessToken;
  private AIDataService aiDataService;
  private AIService aiService;
  private AIConfiguration config = new AIConfiguration("", AIConfiguration.SupportedLanguages.DEFAULT, AIConfiguration.RecognitionEngine.System);
  private List<AIContext> contexts;
  private List<Entity> entities;
  private String languageTag;
  private Callback onAudioLevelCallback;
  private Callback onErrorCallback;
  private Callback onListeningCanceledCallback;
  private Callback onListeningFinishedCallback;
  private Callback onListeningStartedCallback;
  private Callback onResultCallback;
  private List<AIContext> permantentContexts;
  
  public RNApiAiModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private List mergeContexts(List paramList1, List paramList2)
  {
    if (paramList1 == null) {
      return paramList2;
    }
    if (paramList2 == null) {
      return paramList1;
    }
    paramList1.addAll(paramList2);
    return paramList1;
  }
  
  public void cancel()
  {
    getCurrentActivity().runOnUiThread(new RNApiAiModule.6(this));
  }
  
  public void getAccessToken(Promise paramPromise)
  {
    paramPromise.resolve(accessToken);
  }
  
  public Map getConstants()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("LANG_CHINESE_CHINA", "zh-CN");
    localHashMap.put("LANG_CHINESE_HONGKONG", "zh-HK");
    localHashMap.put("LANG_CHINESE_TAIWAN", "zh-TW");
    localHashMap.put("LANG_DUTCH", "nl");
    localHashMap.put("LANG_ENGLISH", "en");
    localHashMap.put("LANG_ENGLISH_GB", "en-GB");
    localHashMap.put("LANG_ENGLISH_US", "en-US");
    localHashMap.put("LANG_FRENCH", "fr");
    localHashMap.put("LANG_GERMAN", "de");
    localHashMap.put("LANG_ITALIAN", "it");
    localHashMap.put("LANG_JAPANESE", "ja");
    localHashMap.put("LANG_KOREAN", "ko");
    localHashMap.put("LANG_PORTUGUESE", "pt");
    localHashMap.put("LANG_PORTUGUESE_BRAZIL", "pt-BR");
    localHashMap.put("LANG_RUSSIAN", "ru");
    localHashMap.put("LANG_SPANISH", "es");
    localHashMap.put("LANG_UKRAINIAN", "uk");
    return localHashMap;
  }
  
  public void getLanguage(Promise paramPromise)
  {
    paramPromise.resolve(languageTag);
  }
  
  public String getName()
  {
    return "ApiAi";
  }
  
  public void getSessionId(Promise paramPromise)
  {
    paramPromise.resolve(SessionIdStorage.getSessionId(getReactApplicationContext()));
  }
  
  public void onAudioLevel(float paramFloat)
  {
    if (onAudioLevelCallback != null)
    {
      Callback localCallback = onAudioLevelCallback;
      try
      {
        localCallback.invoke(new Object[] { Float.valueOf(paramFloat) });
        return;
      }
      catch (Exception localException)
      {
        Log.e("ApiAi", localException.getMessage(), localException);
      }
    }
  }
  
  public void onAudioLevel(Callback paramCallback)
  {
    onAudioLevelCallback = paramCallback;
  }
  
  public void onError(AIError paramAIError)
  {
    if (onErrorCallback != null)
    {
      Gson localGson = new Gson();
      Callback localCallback = onErrorCallback;
      try
      {
        paramAIError = localGson.toJson(paramAIError);
        localCallback.invoke(new Object[] { paramAIError });
        return;
      }
      catch (Exception paramAIError)
      {
        Log.e("ApiAi", paramAIError.getMessage(), paramAIError);
      }
    }
  }
  
  public void onListeningCanceled()
  {
    if (onListeningCanceledCallback != null)
    {
      Callback localCallback = onListeningCanceledCallback;
      try
      {
        localCallback.invoke(new Object[0]);
        return;
      }
      catch (Exception localException)
      {
        Log.e("ApiAi", localException.getMessage(), localException);
      }
    }
  }
  
  public void onListeningCanceled(Callback paramCallback)
  {
    onListeningCanceledCallback = paramCallback;
  }
  
  public void onListeningFinished()
  {
    if (onListeningFinishedCallback != null)
    {
      Callback localCallback = onListeningFinishedCallback;
      try
      {
        localCallback.invoke(new Object[0]);
        return;
      }
      catch (Exception localException)
      {
        Log.e("ApiAi", localException.getMessage(), localException);
      }
    }
  }
  
  public void onListeningFinished(Callback paramCallback)
  {
    onListeningFinishedCallback = paramCallback;
  }
  
  public void onListeningStarted()
  {
    if (onListeningStartedCallback != null)
    {
      Callback localCallback = onListeningStartedCallback;
      try
      {
        localCallback.invoke(new Object[0]);
        return;
      }
      catch (Exception localException)
      {
        Log.e("ApiAi", localException.getMessage(), localException);
      }
    }
  }
  
  public void onListeningStarted(Callback paramCallback)
  {
    onListeningStartedCallback = paramCallback;
  }
  
  public void onResult(AIResponse paramAIResponse)
  {
    if (onResultCallback != null)
    {
      Gson localGson = new Gson();
      Callback localCallback = onResultCallback;
      try
      {
        paramAIResponse = localGson.toJson(paramAIResponse);
        localCallback.invoke(new Object[] { paramAIResponse });
        return;
      }
      catch (Exception paramAIResponse)
      {
        Log.e("ApiAi", paramAIResponse.getMessage(), paramAIResponse);
      }
    }
  }
  
  public void requestQueryNative(String paramString, Callback paramCallback1, Callback paramCallback2)
  {
    onResultCallback = paramCallback1;
    onErrorCallback = paramCallback2;
    if (aiDataService == null) {
      aiDataService = new AIDataService(getReactApplicationContext(), config);
    }
    paramCallback1 = new AIRequest();
    paramCallback1.setQuery(paramString);
    new RNApiAiModule.7(this, paramCallback1).execute(new AIRequest[] { paramCallback1 });
  }
  
  public void setConfiguration(String paramString1, String paramString2)
  {
    accessToken = paramString1;
    languageTag = paramString2;
    config = new AIConfiguration(paramString1, AIConfiguration.SupportedLanguages.fromLanguageTag(paramString2), AIConfiguration.RecognitionEngine.System);
  }
  
  public void setContextsAsJson(String paramString)
  {
    contexts = ((List)new Gson().fromJson(paramString, new RNApiAiModule.1(this).getType()));
  }
  
  public void setEntitiesAsJson(String paramString)
    throws AIServiceException
  {
    entities = ((List)new Gson().fromJson(paramString, new RNApiAiModule.3(this).getType()));
  }
  
  public void setPermanentContextsAsJson(String paramString)
  {
    permantentContexts = ((List)new Gson().fromJson(paramString, new RNApiAiModule.2(this).getType()));
  }
  
  public void startListeningNative(Callback paramCallback1, Callback paramCallback2)
  {
    onResultCallback = paramCallback1;
    onErrorCallback = paramCallback2;
    getCurrentActivity().runOnUiThread(new RNApiAiModule.4(this));
  }
  
  public void stopListening()
  {
    getCurrentActivity().runOnUiThread(new RNApiAiModule.5(this));
  }
}
