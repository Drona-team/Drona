package expo.modules.package_3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import expo.modules.av.video.VideoViewWrapper;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.core.arguments.ReadableArguments;
import org.unimodules.core.interfaces.InternalModule;
import org.unimodules.core.interfaces.LifecycleEventListener;
import org.unimodules.core.interfaces.services.EventEmitter;
import org.unimodules.core.interfaces.services.UIManager;
import org.unimodules.interfaces.permissions.Permissions;

public class AVManager
  implements LifecycleEventListener, AudioManager.OnAudioFocusChangeListener, MediaRecorder.OnInfoListener, AVManagerInterface, InternalModule
{
  private static final String AUDIO_MODE_INTERRUPTION_MODE_KEY = "interruptionModeAndroid";
  private static final String AUDIO_MODE_PLAY_THROUGH_EARPIECE = "playThroughEarpieceAndroid";
  private static final String AUDIO_MODE_SHOULD_DUCK_KEY = "shouldDuckAndroid";
  private static final String AUDIO_MODE_STAYS_ACTIVE_IN_BACKGROUND = "staysActiveInBackground";
  private static final String RECORDING_OPTIONS_KEY = "android";
  private static final String RECORDING_OPTION_AUDIO_ENCODER_KEY = "audioEncoder";
  private static final String RECORDING_OPTION_BIT_RATE_KEY = "bitRate";
  private static final String RECORDING_OPTION_EXTENSION_KEY = "extension";
  private static final String RECORDING_OPTION_MAX_FILE_SIZE_KEY = "maxFileSize";
  private static final String RECORDING_OPTION_NUMBER_OF_CHANNELS_KEY = "numberOfChannels";
  private static final String RECORDING_OPTION_OUTPUT_FORMAT_KEY = "outputFormat";
  private static final String RECORDING_OPTION_SAMPLE_RATE_KEY = "sampleRate";
  private boolean mAcquiredAudioFocus = false;
  private boolean mAppIsPaused = false;
  private AudioInterruptionMode mAudioInterruptionMode = AudioInterruptionMode.DUCK_OTHERS;
  private final AudioManager mAudioManager;
  private MediaRecorder mAudioRecorder = null;
  private long mAudioRecorderDurationAlreadyRecorded = 0L;
  private boolean mAudioRecorderIsPaused = false;
  private boolean mAudioRecorderIsRecording = false;
  private long mAudioRecorderUptimeOfLastStartResume = 0L;
  private String mAudioRecordingFilePath = null;
  private final Context mContext;
  private boolean mEnabled = true;
  private boolean mIsDuckingAudio = false;
  private boolean mIsRegistered = false;
  private ModuleRegistry mModuleRegistry;
  private final BroadcastReceiver mNoisyAudioStreamReceiver;
  private boolean mShouldDuckAudio = true;
  private boolean mShouldRouteThroughEarpiece = false;
  private final Map<Integer, expo.modules.av.player.PlayerData> mSoundMap = new HashMap();
  private int mSoundMapKeyCount = 0;
  private boolean mStaysActiveInBackground = false;
  private final Set<expo.modules.av.video.VideoView> mVideoViewSet = new HashSet();
  
  public AVManager(Context paramContext)
  {
    mContext = paramContext;
    mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    mNoisyAudioStreamReceiver = new AVManager.1(this);
    mContext.registerReceiver(mNoisyAudioStreamReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
    mIsRegistered = true;
  }
  
  private void abandonAudioFocus()
  {
    Iterator localIterator = getAllRegisteredAudioEventHandlers().iterator();
    while (localIterator.hasNext())
    {
      AudioEventHandler localAudioEventHandler = (AudioEventHandler)localIterator.next();
      if (localAudioEventHandler.requiresAudioFocus()) {
        localAudioEventHandler.pauseImmediately();
      }
    }
    mAcquiredAudioFocus = false;
    mAudioManager.abandonAudioFocus(this);
  }
  
  private boolean checkAudioRecorderExistsOrReject(Promise paramPromise)
  {
    if ((mAudioRecorder == null) && (paramPromise != null)) {
      paramPromise.reject("E_AUDIO_NORECORDER", "Recorder does not exist.");
    }
    return mAudioRecorder != null;
  }
  
  private static File ensureDirExists(File paramFile)
    throws IOException
  {
    if (!paramFile.isDirectory())
    {
      if (paramFile.mkdirs()) {
        return paramFile;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Couldn't create directory '");
      localStringBuilder.append(paramFile);
      localStringBuilder.append("'");
      throw new IOException(localStringBuilder.toString());
    }
    return paramFile;
  }
  
  private Set getAllRegisteredAudioEventHandlers()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(mVideoViewSet);
    localHashSet.addAll(mSoundMap.values());
    return localHashSet;
  }
  
  private long getAudioRecorderDurationMillis()
  {
    if (mAudioRecorder == null) {
      return 0L;
    }
    long l2 = mAudioRecorderDurationAlreadyRecorded;
    long l1 = l2;
    if (mAudioRecorderIsRecording)
    {
      l1 = l2;
      if (mAudioRecorderUptimeOfLastStartResume > 0L) {
        l1 = l2 + (SystemClock.uptimeMillis() - mAudioRecorderUptimeOfLastStartResume);
      }
    }
    return l1;
  }
  
  private Bundle getAudioRecorderStatus()
  {
    Bundle localBundle = new Bundle();
    if (mAudioRecorder != null)
    {
      localBundle.putBoolean("canRecord", true);
      localBundle.putBoolean("isRecording", mAudioRecorderIsRecording);
      localBundle.putInt("durationMillis", (int)getAudioRecorderDurationMillis());
    }
    return localBundle;
  }
  
  private boolean isMissingAudioRecordingPermissions()
  {
    return ((Permissions)mModuleRegistry.getModule(Permissions.class)).hasGrantedPermissions(new String[] { "android.permission.RECORD_AUDIO" }) ^ true;
  }
  
  private void removeAudioRecorder()
  {
    if (mAudioRecorder != null) {}
    try
    {
      mAudioRecorder.stop();
      mAudioRecorder.release();
      mAudioRecorder = null;
      mAudioRecordingFilePath = null;
      mAudioRecorderIsRecording = false;
      mAudioRecorderIsPaused = false;
      mAudioRecorderDurationAlreadyRecorded = 0L;
      mAudioRecorderUptimeOfLastStartResume = 0L;
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;) {}
    }
  }
  
  private void removeSoundForKey(Integer paramInteger)
  {
    paramInteger = (expo.modules.package_3.player.PlayerData)mSoundMap.remove(paramInteger);
    if (paramInteger != null)
    {
      paramInteger.release();
      abandonAudioFocusIfUnused();
    }
  }
  
  private void sendEvent(String paramString, Bundle paramBundle)
  {
    if (mModuleRegistry != null)
    {
      EventEmitter localEventEmitter = (EventEmitter)mModuleRegistry.getModule(EventEmitter.class);
      if (localEventEmitter != null) {
        localEventEmitter.emit(paramString, paramBundle);
      }
    }
  }
  
  private expo.modules.package_3.player.PlayerData tryGetSoundForKey(Integer paramInteger, Promise paramPromise)
  {
    paramInteger = (expo.modules.package_3.player.PlayerData)mSoundMap.get(paramInteger);
    if ((paramInteger == null) && (paramPromise != null)) {
      paramPromise.reject("E_AUDIO_NOPLAYER", "Player does not exist.");
    }
    return paramInteger;
  }
  
  private void tryRunWithVideoView(Integer paramInteger, VideoViewCallback paramVideoViewCallback, Promise paramPromise)
  {
    if (mModuleRegistry != null)
    {
      UIManager localUIManager = (UIManager)mModuleRegistry.getModule(UIManager.class);
      if (localUIManager != null) {
        localUIManager.addUIBlock(paramInteger.intValue(), new AVManager.5(this, paramVideoViewCallback, paramPromise), VideoViewWrapper.class);
      }
    }
  }
  
  private void updateDuckStatusForAllPlayersPlaying()
  {
    Iterator localIterator = getAllRegisteredAudioEventHandlers().iterator();
    while (localIterator.hasNext()) {
      ((AudioEventHandler)localIterator.next()).updateVolumeMuteAndDuck();
    }
  }
  
  private void updatePlaySoundThroughEarpiece(boolean paramBoolean)
  {
    AudioManager localAudioManager = mAudioManager;
    int i;
    if (paramBoolean) {
      i = 3;
    } else {
      i = 0;
    }
    localAudioManager.setMode(i);
    mAudioManager.setSpeakerphoneOn(paramBoolean ^ true);
  }
  
  public void abandonAudioFocusIfUnused()
  {
    Iterator localIterator = getAllRegisteredAudioEventHandlers().iterator();
    while (localIterator.hasNext()) {
      if (((AudioEventHandler)localIterator.next()).requiresAudioFocus()) {
        return;
      }
    }
    abandonAudioFocus();
  }
  
  public void acquireAudioFocus()
    throws AudioFocusNotAcquiredException
  {
    if (mEnabled)
    {
      if ((mAppIsPaused) && (!mStaysActiveInBackground)) {
        throw new AudioFocusNotAcquiredException("This experience is currently in the background, so audio focus could not be acquired.");
      }
      if (mAcquiredAudioFocus) {
        return;
      }
      AudioInterruptionMode localAudioInterruptionMode1 = mAudioInterruptionMode;
      AudioInterruptionMode localAudioInterruptionMode2 = AudioInterruptionMode.DO_NOT_MIX;
      boolean bool = true;
      int i;
      if (localAudioInterruptionMode1 == localAudioInterruptionMode2) {
        i = 1;
      } else {
        i = 3;
      }
      if (mAudioManager.requestAudioFocus(this, 3, i) != 1) {
        bool = false;
      }
      mAcquiredAudioFocus = bool;
      if (mAcquiredAudioFocus) {
        return;
      }
      throw new AudioFocusNotAcquiredException("Audio focus could not be acquired from the OS at this time.");
    }
    throw new AudioFocusNotAcquiredException("Expo Audio is disabled, so audio focus could not be acquired.");
  }
  
  public void getAudioRecordingStatus(Promise paramPromise)
  {
    if (checkAudioRecorderExistsOrReject(paramPromise)) {
      paramPromise.resolve(getAudioRecorderStatus());
    }
  }
  
  public Context getContext()
  {
    return mContext;
  }
  
  public List getExportedInterfaces()
  {
    return Collections.singletonList(expo.modules.av.AVManagerInterface.class);
  }
  
  public ModuleRegistry getModuleRegistry()
  {
    return mModuleRegistry;
  }
  
  public void getStatusForSound(Integer paramInteger, Promise paramPromise)
  {
    paramInteger = tryGetSoundForKey(paramInteger, paramPromise);
    if (paramInteger != null) {
      paramPromise.resolve(paramInteger.getStatus());
    }
  }
  
  public void getStatusForVideo(Integer paramInteger, Promise paramPromise)
  {
    tryRunWithVideoView(paramInteger, new AVManager.10(this, paramPromise), paramPromise);
  }
  
  public float getVolumeForDuckAndFocus(boolean paramBoolean, float paramFloat)
  {
    if ((mAcquiredAudioFocus) && (!paramBoolean))
    {
      if (mIsDuckingAudio) {
        return paramFloat / 2.0F;
      }
    }
    else {
      return 0.0F;
    }
    return paramFloat;
  }
  
  public void loadForSound(ReadableArguments paramReadableArguments1, ReadableArguments paramReadableArguments2, Promise paramPromise)
  {
    int i = mSoundMapKeyCount;
    mSoundMapKeyCount = (i + 1);
    paramReadableArguments1 = expo.modules.package_3.player.PlayerData.createUnloadedPlayerData(this, mContext, paramReadableArguments1, paramReadableArguments2.toBundle());
    paramReadableArguments1.setErrorListener(new AVManager.2(this, i));
    mSoundMap.put(Integer.valueOf(i), paramReadableArguments1);
    paramReadableArguments1.load(paramReadableArguments2.toBundle(), new AVManager.3(this, paramPromise, i));
    paramReadableArguments1.setStatusUpdateListener(new AVManager.4(this, i));
  }
  
  public void loadForVideo(Integer paramInteger, ReadableArguments paramReadableArguments1, ReadableArguments paramReadableArguments2, Promise paramPromise)
  {
    tryRunWithVideoView(paramInteger, new AVManager.6(this, paramReadableArguments1, paramReadableArguments2, paramPromise), paramPromise);
  }
  
  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt != 1)
    {
      switch (paramInt)
      {
      default: 
        return;
      case -3: 
        if (mShouldDuckAudio)
        {
          mIsDuckingAudio = true;
          mAcquiredAudioFocus = true;
          updateDuckStatusForAllPlayersPlaying();
          return;
        }
        break;
      }
      mIsDuckingAudio = false;
      mAcquiredAudioFocus = false;
      localIterator = getAllRegisteredAudioEventHandlers().iterator();
      while (localIterator.hasNext()) {
        ((AudioEventHandler)localIterator.next()).handleAudioFocusInterruptionBegan();
      }
    }
    mIsDuckingAudio = false;
    mAcquiredAudioFocus = true;
    Iterator localIterator = getAllRegisteredAudioEventHandlers().iterator();
    while (localIterator.hasNext()) {
      ((AudioEventHandler)localIterator.next()).handleAudioFocusGained();
    }
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    if (mModuleRegistry != null) {
      ((UIManager)mModuleRegistry.getModule(UIManager.class)).unregisterLifecycleEventListener(this);
    }
    mModuleRegistry = paramModuleRegistry;
    if (mModuleRegistry != null) {
      ((UIManager)mModuleRegistry.getModule(UIManager.class)).registerLifecycleEventListener(this);
    }
  }
  
  public void onHostDestroy()
  {
    if (mIsRegistered)
    {
      mContext.unregisterReceiver(mNoisyAudioStreamReceiver);
      mIsRegistered = false;
    }
    Iterator localIterator = mSoundMap.values().iterator();
    while (localIterator.hasNext())
    {
      expo.modules.package_3.player.PlayerData localPlayerData = (expo.modules.package_3.player.PlayerData)localIterator.next();
      localIterator.remove();
      if (localPlayerData != null) {
        localPlayerData.release();
      }
    }
    localIterator = mVideoViewSet.iterator();
    while (localIterator.hasNext()) {
      ((expo.modules.package_3.video.VideoView)localIterator.next()).unloadPlayerAndMediaController();
    }
    removeAudioRecorder();
    abandonAudioFocus();
  }
  
  public void onHostPause()
  {
    if (!mAppIsPaused)
    {
      mAppIsPaused = true;
      if (!mStaysActiveInBackground)
      {
        Iterator localIterator = getAllRegisteredAudioEventHandlers().iterator();
        while (localIterator.hasNext()) {
          ((AudioEventHandler)localIterator.next()).onPause();
        }
        abandonAudioFocus();
        if (mShouldRouteThroughEarpiece) {
          updatePlaySoundThroughEarpiece(false);
        }
      }
    }
  }
  
  public void onHostResume()
  {
    if (mAppIsPaused)
    {
      mAppIsPaused = false;
      if (!mStaysActiveInBackground)
      {
        Iterator localIterator = getAllRegisteredAudioEventHandlers().iterator();
        while (localIterator.hasNext()) {
          ((AudioEventHandler)localIterator.next()).onResume();
        }
        if (mShouldRouteThroughEarpiece) {
          updatePlaySoundThroughEarpiece(true);
        }
      }
    }
  }
  
  public void onInfo(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
  {
    if (paramInt1 != 801) {
      return;
    }
    removeAudioRecorder();
    if (mModuleRegistry != null)
    {
      paramMediaRecorder = (EventEmitter)mModuleRegistry.getModule(EventEmitter.class);
      if (paramMediaRecorder != null) {
        paramMediaRecorder.emit("Expo.Recording.recorderUnloaded", new Bundle());
      }
    }
  }
  
  public void pauseAudioRecording(Promise paramPromise)
  {
    if (checkAudioRecorderExistsOrReject(paramPromise))
    {
      if (Build.VERSION.SDK_INT < 24)
      {
        paramPromise.reject("E_AUDIO_VERSIONINCOMPATIBLE", "Pausing an audio recording is unsupported on Android devices running SDK < 24.");
        return;
      }
      MediaRecorder localMediaRecorder = mAudioRecorder;
      try
      {
        localMediaRecorder.pause();
        mAudioRecorderDurationAlreadyRecorded = getAudioRecorderDurationMillis();
        mAudioRecorderIsRecording = false;
        mAudioRecorderIsPaused = true;
        paramPromise.resolve(getAudioRecorderStatus());
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        paramPromise.reject("E_AUDIO_RECORDINGPAUSE", "Pause encountered an error: recording not paused", localIllegalStateException);
      }
    }
  }
  
  public void prepareAudioRecorder(ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    if (isMissingAudioRecordingPermissions())
    {
      paramPromise.reject("E_MISSING_PERMISSION", "Missing audio recording permissions.");
      return;
    }
    removeAudioRecorder();
    paramReadableArguments = paramReadableArguments.getArguments("android");
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("recording-");
    ((StringBuilder)localObject1).append(UUID.randomUUID().toString());
    ((StringBuilder)localObject1).append(paramReadableArguments.getString("extension"));
    localObject1 = ((StringBuilder)localObject1).toString();
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Object localObject2 = mContext;
      localStringBuilder.append(((Context)localObject2).getCacheDir());
      localObject2 = File.separator;
      localStringBuilder.append((String)localObject2);
      localStringBuilder.append("Audio");
      localObject2 = new File(localStringBuilder.toString());
      ensureDirExists((File)localObject2);
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(localObject2);
      localObject2 = File.separator;
      localStringBuilder.append((String)localObject2);
      localStringBuilder.append((String)localObject1);
      localObject1 = localStringBuilder.toString();
      mAudioRecordingFilePath = ((String)localObject1);
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    mAudioRecorder = new MediaRecorder();
    mAudioRecorder.setAudioSource(0);
    mAudioRecorder.setOutputFormat(paramReadableArguments.getInt("outputFormat"));
    mAudioRecorder.setAudioEncoder(paramReadableArguments.getInt("audioEncoder"));
    if (paramReadableArguments.containsKey("sampleRate")) {
      mAudioRecorder.setAudioSamplingRate(paramReadableArguments.getInt("sampleRate"));
    }
    if (paramReadableArguments.containsKey("numberOfChannels")) {
      mAudioRecorder.setAudioChannels(paramReadableArguments.getInt("numberOfChannels"));
    }
    if (paramReadableArguments.containsKey("bitRate")) {
      mAudioRecorder.setAudioEncodingBitRate(paramReadableArguments.getInt("bitRate"));
    }
    mAudioRecorder.setOutputFile(mAudioRecordingFilePath);
    if (paramReadableArguments.containsKey("maxFileSize"))
    {
      mAudioRecorder.setMaxFileSize(paramReadableArguments.getInt("maxFileSize"));
      mAudioRecorder.setOnInfoListener(this);
    }
    paramReadableArguments = mAudioRecorder;
    try
    {
      paramReadableArguments.prepare();
      paramReadableArguments = new Bundle();
      paramReadableArguments.putString("uri", Uri.fromFile(new File(mAudioRecordingFilePath)).toString());
      paramReadableArguments.putBundle("status", getAudioRecorderStatus());
      paramPromise.resolve(paramReadableArguments);
      return;
    }
    catch (Exception paramReadableArguments)
    {
      paramPromise.reject("E_AUDIO_RECORDERNOTCREATED", "Prepare encountered an error: recorder not prepared", paramReadableArguments);
      removeAudioRecorder();
      return;
    }
  }
  
  public void registerVideoViewForAudioLifecycle(expo.modules.package_3.video.VideoView paramVideoView)
  {
    mVideoViewSet.add(paramVideoView);
  }
  
  public void replaySound(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    paramInteger = tryGetSoundForKey(paramInteger, paramPromise);
    if (paramInteger != null) {
      paramInteger.setStatus(paramReadableArguments.toBundle(), paramPromise);
    }
  }
  
  public void replayVideo(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    tryRunWithVideoView(paramInteger, new AVManager.9(this, paramReadableArguments, paramPromise), paramPromise);
  }
  
  public void setAudioIsEnabled(Boolean paramBoolean)
  {
    mEnabled = paramBoolean.booleanValue();
    if (!paramBoolean.booleanValue()) {
      abandonAudioFocus();
    }
  }
  
  public void setAudioMode(ReadableArguments paramReadableArguments)
  {
    mShouldDuckAudio = paramReadableArguments.getBoolean("shouldDuckAndroid");
    if (!mShouldDuckAudio)
    {
      mIsDuckingAudio = false;
      updateDuckStatusForAllPlayersPlaying();
    }
    if (paramReadableArguments.containsKey("playThroughEarpieceAndroid"))
    {
      mShouldRouteThroughEarpiece = paramReadableArguments.getBoolean("playThroughEarpieceAndroid");
      updatePlaySoundThroughEarpiece(mShouldRouteThroughEarpiece);
    }
    if (paramReadableArguments.getInt("interruptionModeAndroid") == 1) {
      mAudioInterruptionMode = AudioInterruptionMode.DO_NOT_MIX;
    }
    mAudioInterruptionMode = AudioInterruptionMode.DUCK_OTHERS;
    mStaysActiveInBackground = paramReadableArguments.getBoolean("staysActiveInBackground");
  }
  
  public void setStatusForSound(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    paramInteger = tryGetSoundForKey(paramInteger, paramPromise);
    if (paramInteger != null) {
      paramInteger.setStatus(paramReadableArguments.toBundle(), paramPromise);
    }
  }
  
  public void setStatusForVideo(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    tryRunWithVideoView(paramInteger, new AVManager.8(this, paramReadableArguments, paramPromise), paramPromise);
  }
  
  public void startAudioRecording(Promise paramPromise)
  {
    if (isMissingAudioRecordingPermissions())
    {
      paramPromise.reject("E_MISSING_PERMISSION", "Missing audio recording permissions.");
      return;
    }
    if (checkAudioRecorderExistsOrReject(paramPromise))
    {
      MediaRecorder localMediaRecorder;
      if ((mAudioRecorderIsPaused) && (Build.VERSION.SDK_INT >= 24)) {
        localMediaRecorder = mAudioRecorder;
      }
      try
      {
        localMediaRecorder.resume();
        break label62;
        localMediaRecorder = mAudioRecorder;
        localMediaRecorder.start();
        label62:
        mAudioRecorderUptimeOfLastStartResume = SystemClock.uptimeMillis();
        mAudioRecorderIsRecording = true;
        mAudioRecorderIsPaused = false;
        paramPromise.resolve(getAudioRecorderStatus());
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        paramPromise.reject("E_AUDIO_RECORDING", "Start encountered an error: recording not started", localIllegalStateException);
      }
    }
  }
  
  public void stopAudioRecording(Promise paramPromise)
  {
    if (checkAudioRecorderExistsOrReject(paramPromise)) {
      try
      {
        mAudioRecorder.stop();
        mAudioRecorderDurationAlreadyRecorded = getAudioRecorderDurationMillis();
        mAudioRecorderIsRecording = false;
        mAudioRecorderIsPaused = false;
        paramPromise.resolve(getAudioRecorderStatus());
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        paramPromise.reject("E_AUDIO_RECORDINGSTOP", "Stop encountered an error: recording not stopped", localRuntimeException);
      }
    }
  }
  
  public void unloadAudioRecorder(Promise paramPromise)
  {
    if (checkAudioRecorderExistsOrReject(paramPromise))
    {
      removeAudioRecorder();
      paramPromise.resolve(null);
    }
  }
  
  public void unloadForSound(Integer paramInteger, Promise paramPromise)
  {
    if (tryGetSoundForKey(paramInteger, paramPromise) != null)
    {
      removeSoundForKey(paramInteger);
      paramPromise.resolve(expo.modules.package_3.player.PlayerData.getUnloadedStatus());
    }
  }
  
  public void unloadForVideo(Integer paramInteger, Promise paramPromise)
  {
    tryRunWithVideoView(paramInteger, new AVManager.7(this, paramPromise), paramPromise);
  }
  
  public void unregisterVideoViewForAudioLifecycle(expo.modules.package_3.video.VideoView paramVideoView)
  {
    mVideoViewSet.remove(paramVideoView);
  }
  
  enum AudioInterruptionMode
  {
    DO_NOT_MIX,  DUCK_OTHERS;
  }
  
  abstract interface VideoViewCallback
  {
    public abstract void runWithVideoView(expo.modules.package_3.video.VideoView paramVideoView);
  }
}
