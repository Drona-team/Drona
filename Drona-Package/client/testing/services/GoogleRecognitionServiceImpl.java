package client.testing.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import client.testing.PartialResultsListener;
import client.testing.RequestExtras;
import client.testing.android.AIService;
import client.testing.model.AIError;
import client.testing.model.AIRequest;
import client.testing.model.AIResponse;
import client.testing.util.RecognizerChecker;
import client.testing.util.VersionConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleRecognitionServiceImpl
  extends AIService
{
  private static final String LOGTAG = "ai.api.services.GoogleRecognitionServiceImpl";
  private static final long STOP_DELAY = 1000L;
  private final Map<Integer, String> errorMessages = new HashMap();
  private final Handler handler = new Handler();
  private PartialResultsListener partialResultsListener;
  private volatile boolean recognitionActive = false;
  private RequestExtras requestExtras;
  private SpeechRecognizer speechRecognizer;
  private final Object speechRecognizerLock = new Object();
  private Runnable stopRunnable;
  private final VersionConfig versionConfig;
  private volatile boolean wasReadyForSpeech;
  
  public GoogleRecognitionServiceImpl(Context paramContext, client.testing.android.AIConfiguration paramAIConfiguration)
  {
    super(paramAIConfiguration, paramContext);
    errorMessages.put(Integer.valueOf(1), "Network operation timed out.");
    errorMessages.put(Integer.valueOf(2), "Other network related errors.");
    errorMessages.put(Integer.valueOf(3), "Audio recording error.");
    errorMessages.put(Integer.valueOf(4), "Server sends error status.");
    errorMessages.put(Integer.valueOf(5), "Other client side errors.");
    errorMessages.put(Integer.valueOf(6), "No speech input.");
    errorMessages.put(Integer.valueOf(7), "No recognition result matched.");
    errorMessages.put(Integer.valueOf(8), "RecognitionService busy.");
    errorMessages.put(Integer.valueOf(9), "Insufficient permissions.");
    if (RecognizerChecker.findGoogleRecognizer(paramContext) == null) {
      Log.w(LOGTAG, "Google Recognizer application not found on device. Quality of the recognition may be low. Please check if Google Search application installed and enabled.");
    }
    versionConfig = VersionConfig.init(paramContext);
    if (versionConfig.isAutoStopRecognizer()) {
      stopRunnable = new GoogleRecognitionServiceImpl.1(this);
    }
  }
  
  private Intent createRecognitionIntent()
  {
    Intent localIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
    localIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
    String str = config.getLanguage().replace('-', '_');
    localIntent.putExtra("android.speech.extra.LANGUAGE", str);
    localIntent.putExtra("android.speech.extra.LANGUAGE_PREFERENCE", str);
    localIntent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
    localIntent.putExtra("calling_package", context.getPackageName());
    localIntent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[] { str });
    return localIntent;
  }
  
  /* Error */
  private void restartRecognition()
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: invokespecial 169	client/testing/services/GoogleRecognitionServiceImpl:updateStopRunnable	(I)V
    //   5: aload_0
    //   6: iconst_0
    //   7: putfield 50	client/testing/services/GoogleRecognitionServiceImpl:recognitionActive	Z
    //   10: aload_0
    //   11: getfield 48	client/testing/services/GoogleRecognitionServiceImpl:speechRecognizerLock	Ljava/lang/Object;
    //   14: astore_1
    //   15: aload_1
    //   16: monitorenter
    //   17: aload_0
    //   18: getfield 257	client/testing/services/GoogleRecognitionServiceImpl:speechRecognizer	Landroid/speech/SpeechRecognizer;
    //   21: ifnull +48 -> 69
    //   24: aload_0
    //   25: getfield 257	client/testing/services/GoogleRecognitionServiceImpl:speechRecognizer	Landroid/speech/SpeechRecognizer;
    //   28: astore_2
    //   29: aload_2
    //   30: invokevirtual 262	android/speech/SpeechRecognizer:cancel	()V
    //   33: aload_0
    //   34: invokespecial 264	client/testing/services/GoogleRecognitionServiceImpl:createRecognitionIntent	()Landroid/content/Intent;
    //   37: astore_2
    //   38: aload_0
    //   39: iconst_0
    //   40: putfield 180	client/testing/services/GoogleRecognitionServiceImpl:wasReadyForSpeech	Z
    //   43: aload_0
    //   44: getfield 257	client/testing/services/GoogleRecognitionServiceImpl:speechRecognizer	Landroid/speech/SpeechRecognizer;
    //   47: astore_3
    //   48: aload_3
    //   49: aload_2
    //   50: invokevirtual 268	android/speech/SpeechRecognizer:startListening	(Landroid/content/Intent;)V
    //   53: aload_0
    //   54: iconst_1
    //   55: putfield 50	client/testing/services/GoogleRecognitionServiceImpl:recognitionActive	Z
    //   58: goto +11 -> 69
    //   61: astore_2
    //   62: goto +10 -> 72
    //   65: aload_0
    //   66: invokevirtual 271	client/testing/services/GoogleRecognitionServiceImpl:stopListening	()V
    //   69: aload_1
    //   70: monitorexit
    //   71: return
    //   72: aload_1
    //   73: monitorexit
    //   74: aload_2
    //   75: athrow
    //   76: astore_2
    //   77: goto -12 -> 65
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	GoogleRecognitionServiceImpl
    //   14	59	1	localObject1	Object
    //   28	22	2	localObject2	Object
    //   61	14	2	localThrowable	Throwable
    //   76	1	2	localException	Exception
    //   47	2	3	localSpeechRecognizer	SpeechRecognizer
    // Exception table:
    //   from	to	target	type
    //   17	29	61	java/lang/Throwable
    //   29	38	61	java/lang/Throwable
    //   38	48	61	java/lang/Throwable
    //   48	53	61	java/lang/Throwable
    //   53	58	61	java/lang/Throwable
    //   65	69	61	java/lang/Throwable
    //   69	71	61	java/lang/Throwable
    //   72	74	61	java/lang/Throwable
    //   29	38	76	java/lang/Exception
    //   48	53	76	java/lang/Exception
  }
  
  private void sendRequest(AIRequest paramAIRequest, RequestExtras paramRequestExtras)
  {
    if (paramAIRequest != null)
    {
      new GoogleRecognitionServiceImpl.2(this, paramRequestExtras).execute(new AIRequest[] { paramAIRequest });
      return;
    }
    throw new IllegalArgumentException("aiRequest must be not null");
  }
  
  private void stopInternal()
  {
    updateStopRunnable(0);
    if (versionConfig.isDestroyRecognizer()) {
      clearRecognizer();
    }
    recognitionActive = false;
  }
  
  private void updateStopRunnable(int paramInt)
  {
    if (stopRunnable != null)
    {
      if (paramInt == 0)
      {
        handler.removeCallbacks(stopRunnable);
        return;
      }
      if (paramInt == 1)
      {
        handler.removeCallbacks(stopRunnable);
        handler.postDelayed(stopRunnable, 1000L);
      }
    }
  }
  
  public void cancel()
  {
    Object localObject = speechRecognizerLock;
    try
    {
      if (recognitionActive)
      {
        recognitionActive = false;
        if (speechRecognizer != null) {
          speechRecognizer.cancel();
        }
        onListeningCancelled();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected void clearRecognizer()
  {
    Log.d(LOGTAG, "clearRecognizer");
    if (speechRecognizer != null)
    {
      Object localObject = speechRecognizerLock;
      try
      {
        if (speechRecognizer != null)
        {
          speechRecognizer.destroy();
          speechRecognizer = null;
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  protected void initializeRecognizer()
  {
    if (speechRecognizer != null) {
      return;
    }
    Object localObject = speechRecognizerLock;
    try
    {
      if (speechRecognizer != null)
      {
        speechRecognizer.destroy();
        speechRecognizer = null;
      }
      ComponentName localComponentName = RecognizerChecker.findGoogleRecognizer(context);
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, localComponentName);
      speechRecognizer.setRecognitionListener(new InternalRecognitionListener(null));
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected void onPartialResults(List paramList)
  {
    if (partialResultsListener != null) {
      partialResultsListener.onPartialResults(paramList);
    }
  }
  
  public void pause()
  {
    clearRecognizer();
  }
  
  public void resume() {}
  
  public void setPartialResultsListener(PartialResultsListener paramPartialResultsListener)
  {
    partialResultsListener = paramPartialResultsListener;
  }
  
  public void startListening()
  {
    startListening(new RequestExtras());
  }
  
  /* Error */
  public void startListening(RequestExtras paramRequestExtras)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 50	client/testing/services/GoogleRecognitionServiceImpl:recognitionActive	Z
    //   4: ifne +76 -> 80
    //   7: aload_0
    //   8: getfield 48	client/testing/services/GoogleRecognitionServiceImpl:speechRecognizerLock	Ljava/lang/Object;
    //   11: astore_2
    //   12: aload_2
    //   13: monitorenter
    //   14: aload_0
    //   15: aload_1
    //   16: putfield 157	client/testing/services/GoogleRecognitionServiceImpl:requestExtras	Lclient/testing/RequestExtras;
    //   19: aload_0
    //   20: invokevirtual 348	client/testing/android/AIService:checkPermissions	()Z
    //   23: ifne +20 -> 43
    //   26: aload_0
    //   27: new 350	client/testing/model/AIError
    //   30: dup
    //   31: ldc_w 352
    //   34: invokespecial 353	client/testing/model/AIError:<init>	(Ljava/lang/String;)V
    //   37: invokevirtual 143	client/testing/android/AIService:onError	(Lclient/testing/model/AIError;)V
    //   40: aload_2
    //   41: monitorexit
    //   42: return
    //   43: aload_0
    //   44: invokevirtual 355	client/testing/services/GoogleRecognitionServiceImpl:initializeRecognizer	()V
    //   47: aload_0
    //   48: iconst_1
    //   49: putfield 50	client/testing/services/GoogleRecognitionServiceImpl:recognitionActive	Z
    //   52: aload_0
    //   53: invokespecial 264	client/testing/services/GoogleRecognitionServiceImpl:createRecognitionIntent	()Landroid/content/Intent;
    //   56: astore_1
    //   57: aload_0
    //   58: iconst_0
    //   59: putfield 180	client/testing/services/GoogleRecognitionServiceImpl:wasReadyForSpeech	Z
    //   62: aload_0
    //   63: getfield 257	client/testing/services/GoogleRecognitionServiceImpl:speechRecognizer	Landroid/speech/SpeechRecognizer;
    //   66: astore_3
    //   67: aload_3
    //   68: aload_1
    //   69: invokevirtual 268	android/speech/SpeechRecognizer:startListening	(Landroid/content/Intent;)V
    //   72: aload_2
    //   73: monitorexit
    //   74: return
    //   75: astore_1
    //   76: aload_2
    //   77: monitorexit
    //   78: aload_1
    //   79: athrow
    //   80: getstatic 98	client/testing/services/GoogleRecognitionServiceImpl:LOGTAG	Ljava/lang/String;
    //   83: ldc_w 357
    //   86: invokestatic 106	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   89: pop
    //   90: aload_0
    //   91: getfield 180	client/testing/services/GoogleRecognitionServiceImpl:wasReadyForSpeech	Z
    //   94: ifne +12 -> 106
    //   97: aload_0
    //   98: invokevirtual 358	client/testing/services/GoogleRecognitionServiceImpl:cancel	()V
    //   101: return
    //   102: astore_1
    //   103: goto -31 -> 72
    //   106: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	this	GoogleRecognitionServiceImpl
    //   0	107	1	paramRequestExtras	RequestExtras
    //   11	66	2	localObject	Object
    //   66	2	3	localSpeechRecognizer	SpeechRecognizer
    // Exception table:
    //   from	to	target	type
    //   14	42	75	java/lang/Throwable
    //   43	57	75	java/lang/Throwable
    //   67	72	75	java/lang/Throwable
    //   72	74	75	java/lang/Throwable
    //   76	78	75	java/lang/Throwable
    //   67	72	102	java/lang/SecurityException
  }
  
  public void startListening(List paramList)
  {
    startListening(new RequestExtras(paramList, null));
  }
  
  public void stopListening()
  {
    Object localObject = speechRecognizerLock;
    try
    {
      if (speechRecognizer != null) {
        speechRecognizer.stopListening();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  class InternalRecognitionListener
    implements RecognitionListener
  {
    private InternalRecognitionListener() {}
    
    public void onBeginningOfSpeech() {}
    
    public void onBufferReceived(byte[] paramArrayOfByte) {}
    
    public void onEndOfSpeech()
    {
      if (recognitionActive) {
        onListeningFinished();
      }
    }
    
    public void onError(int paramInt)
    {
      if ((paramInt == 7) && (!wasReadyForSpeech))
      {
        Log.d(GoogleRecognitionServiceImpl.LOGTAG, "SpeechRecognizer.ERROR_NO_MATCH, restartRecognition()");
        GoogleRecognitionServiceImpl.this.restartRecognition();
        return;
      }
      if (recognitionActive)
      {
        Object localObject;
        if (errorMessages.containsKey(Integer.valueOf(paramInt)))
        {
          localObject = (String)errorMessages.get(Integer.valueOf(paramInt));
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Speech recognition engine error: ");
          localStringBuilder.append((String)localObject);
          localObject = new AIError(localStringBuilder.toString());
        }
        else
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Speech recognition engine error: ");
          ((StringBuilder)localObject).append(paramInt);
          localObject = new AIError(((StringBuilder)localObject).toString());
        }
        onError((AIError)localObject);
      }
      GoogleRecognitionServiceImpl.this.stopInternal();
    }
    
    public void onEvent(int paramInt, Bundle paramBundle) {}
    
    public void onPartialResults(Bundle paramBundle)
    {
      if (recognitionActive)
      {
        GoogleRecognitionServiceImpl.this.updateStopRunnable(1);
        paramBundle = paramBundle.getStringArrayList("results_recognition");
        if ((paramBundle != null) && (!paramBundle.isEmpty())) {
          onPartialResults(paramBundle);
        }
      }
    }
    
    public void onReadyForSpeech(Bundle paramBundle)
    {
      if (recognitionActive) {
        onListeningStarted();
      }
      GoogleRecognitionServiceImpl.access$602(GoogleRecognitionServiceImpl.this, true);
    }
    
    public void onResults(Bundle paramBundle)
    {
      Object localObject = GoogleRecognitionServiceImpl.this;
      InternalRecognitionListener localInternalRecognitionListener = this;
      if (recognitionActive)
      {
        ArrayList localArrayList = paramBundle.getStringArrayList("results_recognition");
        localObject = null;
        if (Build.VERSION.SDK_INT >= 14) {
          localObject = paramBundle.getFloatArray("confidence_scores");
        }
        if ((localArrayList != null) && (!localArrayList.isEmpty()))
        {
          paramBundle = new AIRequest();
          if (localObject != null) {
            paramBundle.setQuery((String[])localArrayList.toArray(new String[localArrayList.size()]), (float[])localObject);
          } else {
            paramBundle.setQuery((String)localArrayList.get(0));
          }
          this$0.onPartialResults(localArrayList);
          this$0.sendRequest(paramBundle, this$0.requestExtras);
        }
        else
        {
          this$0.onResult(new AIResponse());
        }
      }
      GoogleRecognitionServiceImpl.this.stopInternal();
    }
    
    public void onRmsChanged(float paramFloat)
    {
      if (recognitionActive) {
        onAudioLevelChanged(paramFloat);
      }
    }
  }
}
