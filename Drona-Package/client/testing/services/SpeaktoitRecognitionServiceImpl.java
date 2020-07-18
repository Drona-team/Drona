package client.testing.services;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.AsyncTask;
import android.util.Log;
import client.testing.AIDataService;
import client.testing.AIServiceException;
import client.testing.RequestExtras;
import client.testing.android.AIConfiguration;
import client.testing.android.AIService;
import client.testing.model.AIError;
import client.testing.util.VoiceActivityDetector;
import client.testing.util.VoiceActivityDetector.SpeechEventsListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class SpeaktoitRecognitionServiceImpl
  extends AIService
  implements VoiceActivityDetector.SpeechEventsListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
{
  private static final int AUDIO_FORMAT = 2;
  private static final int CHANNEL_CONFIG = 16;
  private static final int SAMPLE_RATE_IN_HZ = 16000;
  public static final String TAG = "ai.api.services.SpeaktoitRecognitionServiceImpl";
  private AudioRecord audioRecord;
  private final ExecutorService eventsExecutor = Executors.newSingleThreadExecutor();
  private RequestExtras extras;
  private volatile boolean isRecording = false;
  private MediaPlayer mediaPlayer;
  private RecognizeTask recognizeTask;
  private final Object recognizerLock = new Object();
  private final VoiceActivityDetector this$0 = new VoiceActivityDetector(16000);
  
  public SpeaktoitRecognitionServiceImpl(Context paramContext, AIConfiguration paramAIConfiguration)
  {
    super(paramAIConfiguration, paramContext);
    init();
  }
  
  private void init()
  {
    Object localObject = recognizerLock;
    try
    {
      audioRecord = new AudioRecord(1, 16000, 16, 2, AudioRecord.getMinBufferSize(16000, 16, 2));
      this$0.setEnabled(config.isVoiceActivityDetectionEnabled());
      this$0.setSpeechListener(this);
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setOnErrorListener(this);
      mediaPlayer.setOnCompletionListener(this);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private boolean playSound(AssetFileDescriptor paramAssetFileDescriptor)
  {
    MediaPlayer localMediaPlayer = mediaPlayer;
    try
    {
      localMediaPlayer.stop();
      localMediaPlayer = mediaPlayer;
      localMediaPlayer.reset();
      localMediaPlayer = mediaPlayer;
      localMediaPlayer.setDataSource(paramAssetFileDescriptor.getFileDescriptor(), paramAssetFileDescriptor.getStartOffset(), paramAssetFileDescriptor.getLength());
      paramAssetFileDescriptor = mediaPlayer;
      paramAssetFileDescriptor.prepare();
      paramAssetFileDescriptor = mediaPlayer;
      paramAssetFileDescriptor.start();
      return true;
    }
    catch (IOException paramAssetFileDescriptor)
    {
      for (;;) {}
    }
    return false;
  }
  
  private void startRecording(RequestExtras paramRequestExtras)
  {
    this$0.reset();
    audioRecord.startRecording();
    onListeningStarted();
    recognizeTask = new RecognizeTask(new RecorderStream(audioRecord, null), paramRequestExtras, null);
    recognizeTask.execute(new Void[0]);
  }
  
  public void cancel()
  {
    Object localObject = recognizerLock;
    try
    {
      if (isRecording)
      {
        audioRecord.stop();
        isRecording = false;
        AssetFileDescriptor localAssetFileDescriptor = config.getRecognizerCancelSound();
        if (localAssetFileDescriptor != null) {
          playSound(localAssetFileDescriptor);
        }
      }
      if (recognizeTask != null) {
        recognizeTask.cancel(true);
      }
      onListeningCancelled();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void onCompletion(MediaPlayer paramMediaPlayer)
  {
    if (isRecording) {
      startRecording(extras);
    }
  }
  
  public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    if (isRecording) {
      startRecording(extras);
    }
    return false;
  }
  
  public void onSpeechBegin() {}
  
  public void onSpeechCancel()
  {
    eventsExecutor.submit(new SpeaktoitRecognitionServiceImpl.2(this));
  }
  
  public void onSpeechEnd()
  {
    eventsExecutor.submit(new SpeaktoitRecognitionServiceImpl.1(this));
  }
  
  public void pause()
  {
    Object localObject = recognizerLock;
    try
    {
      if (isRecording)
      {
        audioRecord.stop();
        isRecording = false;
      }
      audioRecord.release();
      audioRecord = null;
      mediaPlayer.stop();
      mediaPlayer.release();
      mediaPlayer = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void resume()
  {
    init();
  }
  
  public void startListening()
  {
    startListening(new RequestExtras());
  }
  
  public void startListening(RequestExtras paramRequestExtras)
  {
    Object localObject = recognizerLock;
    try
    {
      if (!isRecording)
      {
        if (!checkPermissions())
        {
          onError(new AIError("RECORD_AUDIO permission is denied. Please request permission from user."));
          return;
        }
        isRecording = true;
        extras = paramRequestExtras;
        paramRequestExtras = config.getRecognizerStartSound();
        if (paramRequestExtras != null)
        {
          if (!playSound(paramRequestExtras)) {
            startRecording(extras);
          }
        }
        else {
          startRecording(extras);
        }
      }
      else
      {
        Log.w(TAG, "Trying start listening when it already active");
      }
      return;
    }
    catch (Throwable paramRequestExtras)
    {
      throw paramRequestExtras;
    }
  }
  
  public void startListening(List paramList)
  {
    startListening(new RequestExtras(paramList, null));
  }
  
  public void stopListening()
  {
    localObject1 = recognizerLock;
    for (;;)
    {
      try
      {
        boolean bool = isRecording;
        if (bool) {
          localObject2 = audioRecord;
        }
      }
      catch (Throwable localThrowable)
      {
        Object localObject2;
        throw localThrowable;
      }
      try
      {
        ((AudioRecord)localObject2).stop();
        isRecording = false;
        localObject2 = config;
        localObject2 = ((AIConfiguration)localObject2).getRecognizerStopSound();
        if (localObject2 != null) {
          playSound((AssetFileDescriptor)localObject2);
        }
        onListeningFinished();
      }
      catch (IllegalStateException localIllegalStateException) {}
    }
    Log.w(TAG, "Attempt to stop audioRecord when it is stopped");
  }
  
  class RecognizeTask
    extends AsyncTask<Void, Void, ai.api.model.AIResponse>
  {
    private AIError aiError;
    private final SpeaktoitRecognitionServiceImpl.RecorderStream recorderStream;
    private final RequestExtras requestExtras;
    
    private RecognizeTask(SpeaktoitRecognitionServiceImpl.RecorderStream paramRecorderStream, RequestExtras paramRequestExtras)
    {
      recorderStream = paramRecorderStream;
      requestExtras = paramRequestExtras;
    }
    
    protected client.testing.model.AIResponse doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = SpeaktoitRecognitionServiceImpl.this;
      try
      {
        paramVarArgs = SpeaktoitRecognitionServiceImpl.access$500(paramVarArgs);
        SpeaktoitRecognitionServiceImpl.RecorderStream localRecorderStream = recorderStream;
        RequestExtras localRequestExtras = requestExtras;
        paramVarArgs = paramVarArgs.voiceRequest(localRecorderStream, localRequestExtras);
        return paramVarArgs;
      }
      catch (AIServiceException paramVarArgs)
      {
        aiError = new AIError(paramVarArgs);
      }
      return null;
    }
    
    protected void onPostExecute(client.testing.model.AIResponse paramAIResponse)
    {
      if (isCancelled()) {
        return;
      }
      if (paramAIResponse != null)
      {
        onResult(paramAIResponse);
        return;
      }
      cancel();
      onError(aiError);
    }
  }
  
  class RecorderStream
    extends InputStream
  {
    float alignment = 0.0F;
    private final AudioRecord audioRecord;
    private byte[] bytes;
    private final Object bytesLock = new Object();
    float count = 1.0F;
    private final float dbLevel = (float)Math.pow(10.0D, -0.05D);
    int extent;
    int max = 0;
    int min = 0;
    int offset = 0;
    
    private RecorderStream(AudioRecord paramAudioRecord)
    {
      audioRecord = paramAudioRecord;
    }
    
    private void normalize(byte[] paramArrayOfByte, int paramInt)
    {
      int k = 4800 - offset;
      if (paramInt >= k)
      {
        paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte, k, paramInt - k).order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer localShortBuffer = paramArrayOfByte.asShortBuffer();
        int j = 0;
        int i = 0;
        while (i < localShortBuffer.limit())
        {
          int m = localShortBuffer.get(i);
          max = Math.max(max, m);
          min = Math.min(min, m);
          alignment = ((count - 1.0F) / count * alignment + m / count);
          count += 1.0F;
          i += 1;
        }
        extent = Math.max(Math.abs(max), Math.abs(min));
        float f = dbLevel * 32767.0F / extent;
        i = j;
        while (i < localShortBuffer.limit())
        {
          paramArrayOfByte.putShort((short)(int)((localShortBuffer.get(i) - alignment) * f));
          i += 1;
        }
      }
      offset += Math.min(paramInt, k);
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      audioRecord.read(arrayOfByte, 0, 1);
      return arrayOfByte[0];
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      paramInt2 = audioRecord.read(paramArrayOfByte, paramInt1, paramInt2);
      Object localObject;
      if (paramInt2 > 0) {
        localObject = bytesLock;
      }
      for (;;)
      {
        try
        {
          if (SpeaktoitRecognitionServiceImpl.access$200(SpeaktoitRecognitionServiceImpl.this).isNormalizeInputSound()) {
            normalize(paramArrayOfByte, paramInt2);
          }
          byte[] arrayOfByte = bytes;
          if (arrayOfByte == null) {
            break label215;
          }
          paramInt1 = arrayOfByte.length;
          bytes = new byte[paramInt1 + paramInt2];
          if (paramInt1 > 0) {
            System.arraycopy(arrayOfByte, 0, bytes, 0, paramInt1);
          }
          System.arraycopy(paramArrayOfByte, 0, bytes, paramInt1, paramInt2);
          if (bytes.length >= 320)
          {
            paramArrayOfByte = new byte['?'];
            System.arraycopy(bytes, 0, paramArrayOfByte, 0, 320);
            this$0.processBuffer(paramArrayOfByte, 320);
            paramArrayOfByte = bytes;
            paramInt1 = paramArrayOfByte.length - 320;
            bytes = new byte[paramInt1];
            System.arraycopy(paramArrayOfByte, 320, bytes, 0, paramInt1);
            continue;
          }
          onAudioLevelChanged((float)this$0.calculateRms());
        }
        catch (Throwable paramArrayOfByte)
        {
          throw paramArrayOfByte;
        }
        if (paramInt2 != 0) {
          return paramInt2;
        }
        return -3;
        label215:
        paramInt1 = 0;
      }
    }
  }
}
