package client.testing.android;

import android.content.Context;
import androidx.core.content.ContextCompat;
import client.testing.AIListener;
import client.testing.AIServiceException;
import client.testing.RequestExtras;
import client.testing.model.AIError;
import client.testing.model.AIRequest;
import client.testing.model.AIResponse;
import client.testing.model.Entity;
import client.testing.services.GoogleRecognitionServiceImpl;
import client.testing.services.SpeaktoitRecognitionServiceImpl;
import java.util.Collection;
import java.util.List;

public abstract class AIService
{
  private static final String PAGE_KEY = "ai.api.android.AIService";
  protected final AIDataService aiDataService;
  protected final AIConfiguration config;
  protected final Context context;
  private AIListener listener;
  
  protected AIService(AIConfiguration paramAIConfiguration, Context paramContext)
  {
    config = paramAIConfiguration;
    context = paramContext;
    aiDataService = new AIDataService(paramContext, paramAIConfiguration);
  }
  
  public static AIService getService(Context paramContext, AIConfiguration paramAIConfiguration)
  {
    if (paramAIConfiguration.getRecognitionEngine() == AIConfiguration.RecognitionEngine.Google) {
      return new GoogleRecognitionServiceImpl(paramContext, paramAIConfiguration);
    }
    if (paramAIConfiguration.getRecognitionEngine() == AIConfiguration.RecognitionEngine.System) {
      return new GoogleRecognitionServiceImpl(paramContext, paramAIConfiguration);
    }
    if (paramAIConfiguration.getRecognitionEngine() == AIConfiguration.RecognitionEngine.Speaktoit) {
      return new SpeaktoitRecognitionServiceImpl(paramContext, paramAIConfiguration);
    }
    throw new UnsupportedOperationException("This engine still not supported");
  }
  
  public abstract void cancel();
  
  protected boolean checkPermissions()
  {
    try
    {
      int i = ContextCompat.checkSelfPermission(context, "android.permission.RECORD_AUDIO");
      return i == 0;
    }
    catch (Throwable localThrowable) {}
    return true;
  }
  
  protected void onAudioLevelChanged(float paramFloat)
  {
    if (listener != null) {
      listener.onAudioLevel(paramFloat);
    }
  }
  
  protected void onError(AIError paramAIError)
  {
    if (listener != null) {
      listener.onError(paramAIError);
    }
  }
  
  protected void onListeningCancelled()
  {
    if (listener != null) {
      listener.onListeningCanceled();
    }
  }
  
  protected void onListeningFinished()
  {
    if (listener != null) {
      listener.onListeningFinished();
    }
  }
  
  protected void onListeningStarted()
  {
    if (listener != null) {
      listener.onListeningStarted();
    }
  }
  
  protected void onResult(AIResponse paramAIResponse)
  {
    if (listener != null) {
      listener.onResult(paramAIResponse);
    }
  }
  
  public void pause() {}
  
  public boolean resetContexts()
  {
    return aiDataService.resetContexts();
  }
  
  public void resume() {}
  
  public void setListener(AIListener paramAIListener)
  {
    listener = paramAIListener;
  }
  
  public abstract void startListening();
  
  public abstract void startListening(RequestExtras paramRequestExtras);
  
  public abstract void startListening(List paramList);
  
  public abstract void stopListening();
  
  public AIResponse textRequest(AIRequest paramAIRequest)
    throws AIServiceException
  {
    return aiDataService.request(paramAIRequest);
  }
  
  public AIResponse textRequest(String paramString, RequestExtras paramRequestExtras)
    throws AIServiceException
  {
    paramString = new AIRequest(paramString);
    if (paramRequestExtras != null) {
      paramRequestExtras.copyTo(paramString);
    }
    return aiDataService.request(paramString);
  }
  
  public AIResponse uploadUserEntities(Collection paramCollection)
    throws AIServiceException
  {
    return aiDataService.uploadUserEntities(paramCollection);
  }
  
  public AIResponse uploadUserEntity(Entity paramEntity)
    throws AIServiceException
  {
    return aiDataService.uploadUserEntity(paramEntity);
  }
}
