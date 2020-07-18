package com.google.android.exoplayer2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.analytics.AnalyticsCollector.Factory;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioAttributes.Builder;
import com.google.android.exoplayer2.audio.AudioFocusManager;
import com.google.android.exoplayer2.audio.AudioFocusManager.PlayerControl;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AuxEffectInfo;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upgrade.DefaultDrmSessionManager;
import com.google.android.exoplayer2.upgrade.DrmSessionManager;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.exoplayer2.video.spherical.CameraMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@TargetApi(16)
public class SimpleExoPlayer
  extends BasePlayer
  implements ExoPlayer, Player.AudioComponent, Player.VideoComponent, Player.TextComponent
{
  private static final String PAGE_KEY = "SimpleExoPlayer";
  private final AnalyticsCollector analyticsCollector;
  private AudioAttributes audioAttributes;
  private final CopyOnWriteArraySet<AudioRendererEventListener> audioDebugListeners;
  private DecoderCounters audioDecoderCounters;
  private final AudioFocusManager audioFocusManager;
  private Format audioFormat;
  private final CopyOnWriteArraySet<AudioListener> audioListeners;
  private int audioSessionId;
  private float audioVolume;
  private final BandwidthMeter bandwidthMeter;
  private CameraMotionListener cameraMotionListener;
  private final ComponentListener componentListener;
  private List<Cue> currentCues;
  private final Handler eventHandler;
  private boolean hasNotifiedFullWrongThreadWarning;
  private MediaSource mediaSource;
  private final CopyOnWriteArraySet<MetadataOutput> metadataOutputs;
  private boolean ownsSurface;
  private final ExoPlayerImpl player;
  protected final Renderer[] renderers;
  private Surface surface;
  private int surfaceHeight;
  private SurfaceHolder surfaceHolder;
  private int surfaceWidth;
  private final CopyOnWriteArraySet<TextOutput> textOutputs;
  private TextureView textureView;
  private final CopyOnWriteArraySet<VideoRendererEventListener> videoDebugListeners;
  private DecoderCounters videoDecoderCounters;
  private Format videoFormat;
  private VideoFrameMetadataListener videoFrameMetadataListener;
  private final CopyOnWriteArraySet<VideoListener> videoListeners;
  private int videoScalingMode;
  
  protected SimpleExoPlayer(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, BandwidthMeter paramBandwidthMeter, AnalyticsCollector.Factory paramFactory, Looper paramLooper)
  {
    this(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramBandwidthMeter, paramFactory, Clock.DEFAULT, paramLooper);
  }
  
  protected SimpleExoPlayer(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, BandwidthMeter paramBandwidthMeter, AnalyticsCollector.Factory paramFactory, Clock paramClock, Looper paramLooper)
  {
    bandwidthMeter = paramBandwidthMeter;
    componentListener = new ComponentListener(null);
    videoListeners = new CopyOnWriteArraySet();
    audioListeners = new CopyOnWriteArraySet();
    textOutputs = new CopyOnWriteArraySet();
    metadataOutputs = new CopyOnWriteArraySet();
    videoDebugListeners = new CopyOnWriteArraySet();
    audioDebugListeners = new CopyOnWriteArraySet();
    eventHandler = new Handler(paramLooper);
    renderers = paramRenderersFactory.createRenderers(eventHandler, componentListener, componentListener, componentListener, componentListener, paramDrmSessionManager);
    audioVolume = 1.0F;
    audioSessionId = 0;
    audioAttributes = AudioAttributes.DEFAULT;
    videoScalingMode = 1;
    currentCues = Collections.emptyList();
    player = new ExoPlayerImpl(renderers, paramTrackSelector, paramLoadControl, paramBandwidthMeter, paramClock, paramLooper);
    analyticsCollector = paramFactory.createAnalyticsCollector(player, paramClock);
    addListener(analyticsCollector);
    videoDebugListeners.add(analyticsCollector);
    videoListeners.add(analyticsCollector);
    audioDebugListeners.add(analyticsCollector);
    audioListeners.add(analyticsCollector);
    addMetadataOutput(analyticsCollector);
    paramBandwidthMeter.addEventListener(eventHandler, analyticsCollector);
    if ((paramDrmSessionManager instanceof DefaultDrmSessionManager)) {
      ((DefaultDrmSessionManager)paramDrmSessionManager).addListener(eventHandler, analyticsCollector);
    }
    audioFocusManager = new AudioFocusManager(paramContext, componentListener);
  }
  
  protected SimpleExoPlayer(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, BandwidthMeter paramBandwidthMeter, DrmSessionManager paramDrmSessionManager, Looper paramLooper)
  {
    this(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramBandwidthMeter, new AnalyticsCollector.Factory(), paramLooper);
  }
  
  private void maybeNotifySurfaceSizeChanged(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != surfaceWidth) || (paramInt2 != surfaceHeight))
    {
      surfaceWidth = paramInt1;
      surfaceHeight = paramInt2;
      Iterator localIterator = videoListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoListener)localIterator.next()).onSurfaceSizeChanged(paramInt1, paramInt2);
      }
    }
  }
  
  private void removeSurfaceCallbacks()
  {
    if (textureView != null)
    {
      if (textureView.getSurfaceTextureListener() != componentListener) {
        Log.w("SimpleExoPlayer", "SurfaceTextureListener already unset or replaced.");
      } else {
        textureView.setSurfaceTextureListener(null);
      }
      textureView = null;
    }
    if (surfaceHolder != null)
    {
      surfaceHolder.removeCallback(componentListener);
      surfaceHolder = null;
    }
  }
  
  private void sendVolumeToRenderers()
  {
    float f1 = audioVolume;
    float f2 = audioFocusManager.getVolumeMultiplier();
    Renderer[] arrayOfRenderer = renderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      Renderer localRenderer = arrayOfRenderer[i];
      if (localRenderer.getTrackType() == 1) {
        player.createMessage(localRenderer).setType(2).setPayload(Float.valueOf(f1 * f2)).send();
      }
      i += 1;
    }
  }
  
  private void setVideoSurfaceInternal(Surface paramSurface, boolean paramBoolean)
  {
    Object localObject1 = new ArrayList();
    Object localObject2 = renderers;
    int j = localObject2.length;
    int i = 0;
    while (i < j)
    {
      PlayerMessage.Target localTarget = localObject2[i];
      if (localTarget.getTrackType() == 2) {
        ((List)localObject1).add(player.createMessage(localTarget).setType(1).setPayload(paramSurface).send());
      }
      i += 1;
    }
    if ((surface != null) && (surface != paramSurface)) {}
    try
    {
      localObject1 = ((List)localObject1).iterator();
      for (;;)
      {
        boolean bool = ((Iterator)localObject1).hasNext();
        if (!bool) {
          break;
        }
        localObject2 = ((Iterator)localObject1).next();
        localObject2 = (PlayerMessage)localObject2;
        ((PlayerMessage)localObject2).blockUntilDelivered();
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
    Thread.currentThread().interrupt();
    if (ownsSurface) {
      surface.release();
    }
    surface = paramSurface;
    ownsSurface = paramBoolean;
  }
  
  private void updatePlayWhenReady(boolean paramBoolean, int paramInt)
  {
    ExoPlayerImpl localExoPlayerImpl = player;
    boolean bool = false;
    if ((paramBoolean) && (paramInt != -1)) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    if (paramInt != 1) {
      bool = true;
    }
    localExoPlayerImpl.setPlayWhenReady(paramBoolean, bool);
  }
  
  private void verifyApplicationThread()
  {
    if (Looper.myLooper() != getApplicationLooper())
    {
      IllegalStateException localIllegalStateException;
      if (hasNotifiedFullWrongThreadWarning) {
        localIllegalStateException = null;
      } else {
        localIllegalStateException = new IllegalStateException();
      }
      Log.w("SimpleExoPlayer", "Player is accessed on the wrong thread. See https://google.github.io/ExoPlayer/faqs.html#what-do-player-is-accessed-on-the-wrong-thread-warnings-mean", localIllegalStateException);
      hasNotifiedFullWrongThreadWarning = true;
    }
  }
  
  public void addAnalyticsListener(AnalyticsListener paramAnalyticsListener)
  {
    verifyApplicationThread();
    analyticsCollector.addListener(paramAnalyticsListener);
  }
  
  public void addAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    audioDebugListeners.add(paramAudioRendererEventListener);
  }
  
  public void addAudioListener(AudioListener paramAudioListener)
  {
    audioListeners.add(paramAudioListener);
  }
  
  public void addListener(Player.EventListener paramEventListener)
  {
    verifyApplicationThread();
    player.addListener(paramEventListener);
  }
  
  public void addMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    metadataOutputs.add(paramMetadataOutput);
  }
  
  public void addTextOutput(TextOutput paramTextOutput)
  {
    if (!currentCues.isEmpty()) {
      paramTextOutput.onCues(currentCues);
    }
    textOutputs.add(paramTextOutput);
  }
  
  public void addVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    videoDebugListeners.add(paramVideoRendererEventListener);
  }
  
  public void addVideoListener(VideoListener paramVideoListener)
  {
    videoListeners.add(paramVideoListener);
  }
  
  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    player.blockingSendMessages(paramVarArgs);
  }
  
  public void clearAuxEffectInfo()
  {
    setAuxEffectInfo(new AuxEffectInfo(0, 0.0F));
  }
  
  public void clearCameraMotionListener(CameraMotionListener paramCameraMotionListener)
  {
    verifyApplicationThread();
    if (cameraMotionListener != paramCameraMotionListener) {
      return;
    }
    paramCameraMotionListener = renderers;
    int j = paramCameraMotionListener.length;
    int i = 0;
    while (i < j)
    {
      PlayerMessage.Target localTarget = paramCameraMotionListener[i];
      if (localTarget.getTrackType() == 5) {
        player.createMessage(localTarget).setType(7).setPayload(null).send();
      }
      i += 1;
    }
  }
  
  public void clearMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    removeMetadataOutput(paramMetadataOutput);
  }
  
  public void clearTextOutput(TextOutput paramTextOutput)
  {
    removeTextOutput(paramTextOutput);
  }
  
  public void clearVideoFrameMetadataListener(VideoFrameMetadataListener paramVideoFrameMetadataListener)
  {
    verifyApplicationThread();
    if (videoFrameMetadataListener != paramVideoFrameMetadataListener) {
      return;
    }
    paramVideoFrameMetadataListener = renderers;
    int j = paramVideoFrameMetadataListener.length;
    int i = 0;
    while (i < j)
    {
      PlayerMessage.Target localTarget = paramVideoFrameMetadataListener[i];
      if (localTarget.getTrackType() == 2) {
        player.createMessage(localTarget).setType(6).setPayload(null).send();
      }
      i += 1;
    }
  }
  
  public void clearVideoListener(VideoListener paramVideoListener)
  {
    removeVideoListener(paramVideoListener);
  }
  
  public void clearVideoSurface()
  {
    verifyApplicationThread();
    setVideoSurface(null);
  }
  
  public void clearVideoSurface(Surface paramSurface)
  {
    verifyApplicationThread();
    if ((paramSurface != null) && (paramSurface == surface)) {
      setVideoSurface(null);
    }
  }
  
  public void clearVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder)
  {
    verifyApplicationThread();
    if ((paramSurfaceHolder != null) && (paramSurfaceHolder == surfaceHolder)) {
      setVideoSurfaceHolder(null);
    }
  }
  
  public void clearVideoSurfaceView(SurfaceView paramSurfaceView)
  {
    if (paramSurfaceView == null) {
      paramSurfaceView = null;
    } else {
      paramSurfaceView = paramSurfaceView.getHolder();
    }
    clearVideoSurfaceHolder(paramSurfaceView);
  }
  
  public void clearVideoTextureView(TextureView paramTextureView)
  {
    verifyApplicationThread();
    if ((paramTextureView != null) && (paramTextureView == textureView)) {
      setVideoTextureView(null);
    }
  }
  
  public PlayerMessage createMessage(PlayerMessage.Target paramTarget)
  {
    verifyApplicationThread();
    return player.createMessage(paramTarget);
  }
  
  public AnalyticsCollector getAnalyticsCollector()
  {
    return analyticsCollector;
  }
  
  public Looper getApplicationLooper()
  {
    return player.getApplicationLooper();
  }
  
  public AudioAttributes getAudioAttributes()
  {
    return audioAttributes;
  }
  
  public Player.AudioComponent getAudioComponent()
  {
    return this;
  }
  
  public DecoderCounters getAudioDecoderCounters()
  {
    return audioDecoderCounters;
  }
  
  public Format getAudioFormat()
  {
    return audioFormat;
  }
  
  public int getAudioSessionId()
  {
    return audioSessionId;
  }
  
  public int getAudioStreamType()
  {
    return Util.getStreamTypeForAudioUsage(audioAttributes.usage);
  }
  
  public long getBufferedPosition()
  {
    verifyApplicationThread();
    return player.getBufferedPosition();
  }
  
  public long getContentBufferedPosition()
  {
    verifyApplicationThread();
    return player.getContentBufferedPosition();
  }
  
  public long getContentPosition()
  {
    verifyApplicationThread();
    return player.getContentPosition();
  }
  
  public int getCurrentAdGroupIndex()
  {
    verifyApplicationThread();
    return player.getCurrentAdGroupIndex();
  }
  
  public int getCurrentAdIndexInAdGroup()
  {
    verifyApplicationThread();
    return player.getCurrentAdIndexInAdGroup();
  }
  
  public Object getCurrentManifest()
  {
    verifyApplicationThread();
    return player.getCurrentManifest();
  }
  
  public int getCurrentPeriodIndex()
  {
    verifyApplicationThread();
    return player.getCurrentPeriodIndex();
  }
  
  public long getCurrentPosition()
  {
    verifyApplicationThread();
    return player.getCurrentPosition();
  }
  
  public Timeline getCurrentTimeline()
  {
    verifyApplicationThread();
    return player.getCurrentTimeline();
  }
  
  public TrackGroupArray getCurrentTrackGroups()
  {
    verifyApplicationThread();
    return player.getCurrentTrackGroups();
  }
  
  public TrackSelectionArray getCurrentTrackSelections()
  {
    verifyApplicationThread();
    return player.getCurrentTrackSelections();
  }
  
  public int getCurrentWindowIndex()
  {
    verifyApplicationThread();
    return player.getCurrentWindowIndex();
  }
  
  public long getDuration()
  {
    verifyApplicationThread();
    return player.getDuration();
  }
  
  public boolean getPlayWhenReady()
  {
    verifyApplicationThread();
    return player.getPlayWhenReady();
  }
  
  public ExoPlaybackException getPlaybackError()
  {
    verifyApplicationThread();
    return player.getPlaybackError();
  }
  
  public Looper getPlaybackLooper()
  {
    return player.getPlaybackLooper();
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    verifyApplicationThread();
    return player.getPlaybackParameters();
  }
  
  public int getPlaybackState()
  {
    verifyApplicationThread();
    return player.getPlaybackState();
  }
  
  public int getRendererCount()
  {
    verifyApplicationThread();
    return player.getRendererCount();
  }
  
  public int getRendererType(int paramInt)
  {
    verifyApplicationThread();
    return player.getRendererType(paramInt);
  }
  
  public int getRepeatMode()
  {
    verifyApplicationThread();
    return player.getRepeatMode();
  }
  
  public SeekParameters getSeekParameters()
  {
    verifyApplicationThread();
    return player.getSeekParameters();
  }
  
  public boolean getShuffleModeEnabled()
  {
    verifyApplicationThread();
    return player.getShuffleModeEnabled();
  }
  
  public Player.TextComponent getTextComponent()
  {
    return this;
  }
  
  public long getTotalBufferedDuration()
  {
    verifyApplicationThread();
    return player.getTotalBufferedDuration();
  }
  
  public Player.VideoComponent getVideoComponent()
  {
    return this;
  }
  
  public DecoderCounters getVideoDecoderCounters()
  {
    return videoDecoderCounters;
  }
  
  public Format getVideoFormat()
  {
    return videoFormat;
  }
  
  public int getVideoScalingMode()
  {
    return videoScalingMode;
  }
  
  public float getVolume()
  {
    return audioVolume;
  }
  
  public boolean isLoading()
  {
    verifyApplicationThread();
    return player.isLoading();
  }
  
  public boolean isPlayingAd()
  {
    verifyApplicationThread();
    return player.isPlayingAd();
  }
  
  public void prepare(MediaSource paramMediaSource)
  {
    prepare(paramMediaSource, true, true);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    verifyApplicationThread();
    if (mediaSource != null)
    {
      mediaSource.removeEventListener(analyticsCollector);
      analyticsCollector.resetForNewMediaSource();
    }
    mediaSource = paramMediaSource;
    paramMediaSource.addEventListener(eventHandler, analyticsCollector);
    int i = audioFocusManager.handlePrepare(getPlayWhenReady());
    updatePlayWhenReady(getPlayWhenReady(), i);
    player.prepare(paramMediaSource, paramBoolean1, paramBoolean2);
  }
  
  public void release()
  {
    audioFocusManager.handleStop();
    player.release();
    removeSurfaceCallbacks();
    if (surface != null)
    {
      if (ownsSurface) {
        surface.release();
      }
      surface = null;
    }
    if (mediaSource != null)
    {
      mediaSource.removeEventListener(analyticsCollector);
      mediaSource = null;
    }
    bandwidthMeter.removeEventListener(analyticsCollector);
    currentCues = Collections.emptyList();
  }
  
  public void removeAnalyticsListener(AnalyticsListener paramAnalyticsListener)
  {
    verifyApplicationThread();
    analyticsCollector.removeListener(paramAnalyticsListener);
  }
  
  public void removeAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    audioDebugListeners.remove(paramAudioRendererEventListener);
  }
  
  public void removeAudioListener(AudioListener paramAudioListener)
  {
    audioListeners.remove(paramAudioListener);
  }
  
  public void removeListener(Player.EventListener paramEventListener)
  {
    verifyApplicationThread();
    player.removeListener(paramEventListener);
  }
  
  public void removeMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    metadataOutputs.remove(paramMetadataOutput);
  }
  
  public void removeTextOutput(TextOutput paramTextOutput)
  {
    textOutputs.remove(paramTextOutput);
  }
  
  public void removeVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    videoDebugListeners.remove(paramVideoRendererEventListener);
  }
  
  public void removeVideoListener(VideoListener paramVideoListener)
  {
    videoListeners.remove(paramVideoListener);
  }
  
  public void retry()
  {
    verifyApplicationThread();
    if ((mediaSource != null) && ((getPlaybackError() != null) || (getPlaybackState() == 1))) {
      prepare(mediaSource, false, false);
    }
  }
  
  public void seekTo(int paramInt, long paramLong)
  {
    verifyApplicationThread();
    analyticsCollector.notifySeekStarted();
    player.seekTo(paramInt, paramLong);
  }
  
  public void sendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    player.sendMessages(paramVarArgs);
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    setAudioAttributes(paramAudioAttributes, false);
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes, boolean paramBoolean)
  {
    verifyApplicationThread();
    if (!Util.areEqual(audioAttributes, paramAudioAttributes))
    {
      audioAttributes = paramAudioAttributes;
      localObject = renderers;
      int j = localObject.length;
      i = 0;
      while (i < j)
      {
        PlayerMessage.Target localTarget = localObject[i];
        if (localTarget.getTrackType() == 1) {
          player.createMessage(localTarget).setType(3).setPayload(paramAudioAttributes).send();
        }
        i += 1;
      }
      localObject = audioListeners.iterator();
      while (((Iterator)localObject).hasNext()) {
        ((AudioListener)((Iterator)localObject).next()).onAudioAttributesChanged(paramAudioAttributes);
      }
    }
    Object localObject = audioFocusManager;
    if (!paramBoolean) {
      paramAudioAttributes = null;
    }
    int i = ((AudioFocusManager)localObject).setAudioAttributes(paramAudioAttributes, getPlayWhenReady(), getPlaybackState());
    updatePlayWhenReady(getPlayWhenReady(), i);
  }
  
  public void setAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    audioDebugListeners.retainAll(Collections.singleton(analyticsCollector));
    if (paramAudioRendererEventListener != null) {
      addAudioDebugListener(paramAudioRendererEventListener);
    }
  }
  
  public void setAudioStreamType(int paramInt)
  {
    int i = Util.getAudioUsageForStreamType(paramInt);
    paramInt = Util.getAudioContentTypeForStreamType(paramInt);
    setAudioAttributes(new AudioAttributes.Builder().setUsage(i).setContentType(paramInt).build());
  }
  
  public void setAuxEffectInfo(AuxEffectInfo paramAuxEffectInfo)
  {
    verifyApplicationThread();
    Renderer[] arrayOfRenderer = renderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      Renderer localRenderer = arrayOfRenderer[i];
      if (localRenderer.getTrackType() == 1) {
        player.createMessage(localRenderer).setType(5).setPayload(paramAuxEffectInfo).send();
      }
      i += 1;
    }
  }
  
  public void setCameraMotionListener(CameraMotionListener paramCameraMotionListener)
  {
    verifyApplicationThread();
    cameraMotionListener = paramCameraMotionListener;
    Renderer[] arrayOfRenderer = renderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      Renderer localRenderer = arrayOfRenderer[i];
      if (localRenderer.getTrackType() == 5) {
        player.createMessage(localRenderer).setType(7).setPayload(paramCameraMotionListener).send();
      }
      i += 1;
    }
  }
  
  public void setMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    metadataOutputs.retainAll(Collections.singleton(analyticsCollector));
    if (paramMetadataOutput != null) {
      addMetadataOutput(paramMetadataOutput);
    }
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    verifyApplicationThread();
    updatePlayWhenReady(paramBoolean, audioFocusManager.handleSetPlayWhenReady(paramBoolean, getPlaybackState()));
  }
  
  public void setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    verifyApplicationThread();
    player.setPlaybackParameters(paramPlaybackParameters);
  }
  
  public void setPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    if (paramPlaybackParams != null)
    {
      paramPlaybackParams.allowDefaults();
      paramPlaybackParams = new PlaybackParameters(paramPlaybackParams.getSpeed(), paramPlaybackParams.getPitch());
    }
    else
    {
      paramPlaybackParams = null;
    }
    setPlaybackParameters(paramPlaybackParams);
  }
  
  public void setRepeatMode(int paramInt)
  {
    verifyApplicationThread();
    player.setRepeatMode(paramInt);
  }
  
  public void setSeekParameters(SeekParameters paramSeekParameters)
  {
    verifyApplicationThread();
    player.setSeekParameters(paramSeekParameters);
  }
  
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    verifyApplicationThread();
    player.setShuffleModeEnabled(paramBoolean);
  }
  
  public void setTextOutput(TextOutput paramTextOutput)
  {
    textOutputs.clear();
    if (paramTextOutput != null) {
      addTextOutput(paramTextOutput);
    }
  }
  
  public void setVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    videoDebugListeners.retainAll(Collections.singleton(analyticsCollector));
    if (paramVideoRendererEventListener != null) {
      addVideoDebugListener(paramVideoRendererEventListener);
    }
  }
  
  public void setVideoFrameMetadataListener(VideoFrameMetadataListener paramVideoFrameMetadataListener)
  {
    verifyApplicationThread();
    videoFrameMetadataListener = paramVideoFrameMetadataListener;
    Renderer[] arrayOfRenderer = renderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      Renderer localRenderer = arrayOfRenderer[i];
      if (localRenderer.getTrackType() == 2) {
        player.createMessage(localRenderer).setType(6).setPayload(paramVideoFrameMetadataListener).send();
      }
      i += 1;
    }
  }
  
  public void setVideoListener(VideoListener paramVideoListener)
  {
    videoListeners.clear();
    if (paramVideoListener != null) {
      addVideoListener(paramVideoListener);
    }
  }
  
  public void setVideoScalingMode(int paramInt)
  {
    verifyApplicationThread();
    videoScalingMode = paramInt;
    Renderer[] arrayOfRenderer = renderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      Renderer localRenderer = arrayOfRenderer[i];
      if (localRenderer.getTrackType() == 2) {
        player.createMessage(localRenderer).setType(4).setPayload(Integer.valueOf(paramInt)).send();
      }
      i += 1;
    }
  }
  
  public void setVideoSurface(Surface paramSurface)
  {
    verifyApplicationThread();
    removeSurfaceCallbacks();
    int i = 0;
    setVideoSurfaceInternal(paramSurface, false);
    if (paramSurface != null) {
      i = -1;
    }
    maybeNotifySurfaceSizeChanged(i, i);
  }
  
  public void setVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder)
  {
    verifyApplicationThread();
    removeSurfaceCallbacks();
    surfaceHolder = paramSurfaceHolder;
    if (paramSurfaceHolder == null)
    {
      setVideoSurfaceInternal(null, false);
      maybeNotifySurfaceSizeChanged(0, 0);
      return;
    }
    paramSurfaceHolder.addCallback(componentListener);
    Surface localSurface = paramSurfaceHolder.getSurface();
    if ((localSurface != null) && (localSurface.isValid()))
    {
      setVideoSurfaceInternal(localSurface, false);
      paramSurfaceHolder = paramSurfaceHolder.getSurfaceFrame();
      maybeNotifySurfaceSizeChanged(paramSurfaceHolder.width(), paramSurfaceHolder.height());
      return;
    }
    setVideoSurfaceInternal(null, false);
    maybeNotifySurfaceSizeChanged(0, 0);
  }
  
  public void setVideoSurfaceView(SurfaceView paramSurfaceView)
  {
    if (paramSurfaceView == null) {
      paramSurfaceView = null;
    } else {
      paramSurfaceView = paramSurfaceView.getHolder();
    }
    setVideoSurfaceHolder(paramSurfaceView);
  }
  
  public void setVideoTextureView(TextureView paramTextureView)
  {
    verifyApplicationThread();
    removeSurfaceCallbacks();
    textureView = paramTextureView;
    if (paramTextureView == null)
    {
      setVideoSurfaceInternal(null, true);
      maybeNotifySurfaceSizeChanged(0, 0);
      return;
    }
    if (paramTextureView.getSurfaceTextureListener() != null) {
      Log.w("SimpleExoPlayer", "Replacing existing SurfaceTextureListener.");
    }
    paramTextureView.setSurfaceTextureListener(componentListener);
    SurfaceTexture localSurfaceTexture;
    if (paramTextureView.isAvailable()) {
      localSurfaceTexture = paramTextureView.getSurfaceTexture();
    } else {
      localSurfaceTexture = null;
    }
    if (localSurfaceTexture == null)
    {
      setVideoSurfaceInternal(null, true);
      maybeNotifySurfaceSizeChanged(0, 0);
      return;
    }
    setVideoSurfaceInternal(new Surface(localSurfaceTexture), true);
    maybeNotifySurfaceSizeChanged(paramTextureView.getWidth(), paramTextureView.getHeight());
  }
  
  public void setVolume(float paramFloat)
  {
    verifyApplicationThread();
    paramFloat = Util.constrainValue(paramFloat, 0.0F, 1.0F);
    if (audioVolume == paramFloat) {
      return;
    }
    audioVolume = paramFloat;
    sendVolumeToRenderers();
    Iterator localIterator = audioListeners.iterator();
    while (localIterator.hasNext()) {
      ((AudioListener)localIterator.next()).onVolumeChanged(paramFloat);
    }
  }
  
  public void stop(boolean paramBoolean)
  {
    verifyApplicationThread();
    player.stop(paramBoolean);
    if (mediaSource != null)
    {
      mediaSource.removeEventListener(analyticsCollector);
      analyticsCollector.resetForNewMediaSource();
      if (paramBoolean) {
        mediaSource = null;
      }
    }
    audioFocusManager.handleStop();
    currentCues = Collections.emptyList();
  }
  
  private final class ComponentListener
    implements VideoRendererEventListener, AudioRendererEventListener, TextOutput, MetadataOutput, SurfaceHolder.Callback, TextureView.SurfaceTextureListener, AudioFocusManager.PlayerControl
  {
    private ComponentListener() {}
    
    public void executePlayerCommand(int paramInt)
    {
      SimpleExoPlayer.this.updatePlayWhenReady(getPlayWhenReady(), paramInt);
    }
    
    public void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      Iterator localIterator = audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioDecoderInitialized(paramString, paramLong1, paramLong2);
      }
    }
    
    public void onAudioDisabled(DecoderCounters paramDecoderCounters)
    {
      Iterator localIterator = audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioDisabled(paramDecoderCounters);
      }
      SimpleExoPlayer.access$1002(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$602(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, 0);
    }
    
    public void onAudioEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$602(SimpleExoPlayer.this, paramDecoderCounters);
      Iterator localIterator = audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioEnabled(paramDecoderCounters);
      }
    }
    
    public void onAudioInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$1002(SimpleExoPlayer.this, paramFormat);
      Iterator localIterator = audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioInputFormatChanged(paramFormat);
      }
    }
    
    public void onAudioSessionId(int paramInt)
    {
      if (audioSessionId == paramInt) {
        return;
      }
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, paramInt);
      Iterator localIterator = audioListeners.iterator();
      while (localIterator.hasNext())
      {
        AudioListener localAudioListener = (AudioListener)localIterator.next();
        if (!audioDebugListeners.contains(localAudioListener)) {
          localAudioListener.onAudioSessionId(paramInt);
        }
      }
      localIterator = audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioSessionId(paramInt);
      }
    }
    
    public void onAudioSinkUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      Iterator localIterator = audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioSinkUnderrun(paramInt, paramLong1, paramLong2);
      }
    }
    
    public void onCues(List paramList)
    {
      SimpleExoPlayer.access$1102(SimpleExoPlayer.this, paramList);
      Iterator localIterator = textOutputs.iterator();
      while (localIterator.hasNext()) {
        ((TextOutput)localIterator.next()).onCues(paramList);
      }
    }
    
    public void onDroppedFrames(int paramInt, long paramLong)
    {
      Iterator localIterator = videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onDroppedFrames(paramInt, paramLong);
      }
    }
    
    public void onMetadata(Metadata paramMetadata)
    {
      Iterator localIterator = metadataOutputs.iterator();
      while (localIterator.hasNext()) {
        ((MetadataOutput)localIterator.next()).onMetadata(paramMetadata);
      }
    }
    
    public void onRenderedFirstFrame(Surface paramSurface)
    {
      if (surface == paramSurface)
      {
        localIterator = videoListeners.iterator();
        while (localIterator.hasNext()) {
          ((VideoListener)localIterator.next()).onRenderedFirstFrame();
        }
      }
      Iterator localIterator = videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onRenderedFirstFrame(paramSurface);
      }
    }
    
    public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(new Surface(paramSurfaceTexture), true);
      SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(paramInt1, paramInt2);
    }
    
    public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
      SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(0, 0);
      return true;
    }
    
    public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(paramInt1, paramInt2);
    }
    
    public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture) {}
    
    public void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      Iterator localIterator = videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoDecoderInitialized(paramString, paramLong1, paramLong2);
      }
    }
    
    public void onVideoDisabled(DecoderCounters paramDecoderCounters)
    {
      Iterator localIterator = videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoDisabled(paramDecoderCounters);
      }
      SimpleExoPlayer.access$302(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$102(SimpleExoPlayer.this, null);
    }
    
    public void onVideoEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$102(SimpleExoPlayer.this, paramDecoderCounters);
      Iterator localIterator = videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoEnabled(paramDecoderCounters);
      }
    }
    
    public void onVideoInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$302(SimpleExoPlayer.this, paramFormat);
      Iterator localIterator = videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoInputFormatChanged(paramFormat);
      }
    }
    
    public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      Iterator localIterator = videoListeners.iterator();
      while (localIterator.hasNext())
      {
        VideoListener localVideoListener = (VideoListener)localIterator.next();
        if (!videoDebugListeners.contains(localVideoListener)) {
          localVideoListener.onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
        }
      }
      localIterator = videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
      }
    }
    
    public void setVolumeMultiplier(float paramFloat)
    {
      SimpleExoPlayer.this.sendVolumeToRenderers();
    }
    
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
    {
      SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(paramInt2, paramInt3);
    }
    
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(paramSurfaceHolder.getSurface(), false);
    }
    
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(null, false);
      SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(0, 0);
    }
  }
  
  @Deprecated
  public static abstract interface VideoListener
    extends VideoListener
  {}
}
