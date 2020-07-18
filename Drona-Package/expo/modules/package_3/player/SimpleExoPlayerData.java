package expo.modules.package_3.player;

import android.content.Context;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Surface;
import com.google.android.exoplayer2.BasePlayer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer.VideoListener;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource.EventListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.configurations.HlsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource.Factory;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource.Factory;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.Factory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import expo.modules.package_3.AVManagerInterface;
import expo.modules.package_3.AudioFocusNotAcquiredException;
import java.io.IOException;
import java.util.Map;
import org.unimodules.core.ModuleRegistry;

class SimpleExoPlayerData
  extends PlayerData
  implements Player.EventListener, ExtractorMediaSource.EventListener, SimpleExoPlayer.VideoListener, AdaptiveMediaSourceEventListener
{
  private static final String IMPLEMENTATION_NAME = "SimpleExoPlayer";
  private boolean mFirstFrameRendered = false;
  private boolean mIsLoading = true;
  private boolean mIsLooping = false;
  private Integer mLastPlaybackState = null;
  private PlayerData.LoadCompletionListener mLoadCompletionListener = null;
  private String mOverridingExtension;
  private Context mReactContext;
  private SimpleExoPlayer mSimpleExoPlayer = null;
  private Pair<Integer, Integer> mVideoWidthHeight = null;
  
  SimpleExoPlayerData(AVManagerInterface paramAVManagerInterface, Context paramContext, Uri paramUri, String paramString, Map paramMap)
  {
    super(paramAVManagerInterface, paramUri, paramMap);
    mReactContext = paramContext;
    mOverridingExtension = paramString;
  }
  
  private MediaSource buildMediaSource(Uri paramUri, String paramString, Handler paramHandler, DataSource.Factory paramFactory)
  {
    if (TextUtils.isEmpty(paramString)) {}
    int i;
    StringBuilder localStringBuilder;
    for (paramString = String.valueOf(paramUri);; paramString = localStringBuilder.toString())
    {
      i = Util.inferContentType(paramString);
      break;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(".");
      localStringBuilder.append(paramString);
    }
    switch (i)
    {
    default: 
      paramUri = new StringBuilder();
      paramUri.append("Content of this type is unsupported at the moment. Unsupported type: ");
      paramUri.append(i);
      throw new IllegalStateException(paramUri.toString());
    case 3: 
      return new ExtractorMediaSource(paramUri, paramFactory, new DefaultExtractorsFactory(), paramHandler, this);
    case 2: 
      return new HlsMediaSource(paramUri, paramFactory, paramHandler, this);
    case 1: 
      return new SsMediaSource(paramUri, paramFactory, new DefaultSsChunkSource.Factory(paramFactory), paramHandler, this);
    }
    return new DashMediaSource(paramUri, paramFactory, new DefaultDashChunkSource.Factory(paramFactory), paramHandler, this);
  }
  
  private void onFatalError(Throwable paramThrowable)
  {
    Object localObject;
    if (mLoadCompletionListener != null)
    {
      localObject = mLoadCompletionListener;
      mLoadCompletionListener = null;
      ((PlayerData.LoadCompletionListener)localObject).onLoadError(paramThrowable.toString());
    }
    else
    {
      localObject = mErrorListener;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Player error: ");
      localStringBuilder.append(paramThrowable.getMessage());
      ((PlayerData.ErrorListener)localObject).onError(localStringBuilder.toString());
    }
    release();
  }
  
  void applyNewStatus(Integer paramInteger, Boolean paramBoolean)
    throws AudioFocusNotAcquiredException, IllegalStateException
  {
    if (mSimpleExoPlayer != null)
    {
      if (paramBoolean != null)
      {
        mIsLooping = paramBoolean.booleanValue();
        if (mIsLooping) {
          mSimpleExoPlayer.setRepeatMode(2);
        } else {
          mSimpleExoPlayer.setRepeatMode(0);
        }
      }
      if (!shouldPlayerPlay())
      {
        mSimpleExoPlayer.setPlayWhenReady(false);
        stopUpdatingProgressIfNecessary();
      }
      updateVolumeMuteAndDuck();
      if (paramInteger != null) {
        mSimpleExoPlayer.seekTo(paramInteger.intValue());
      }
      playPlayerWithRateAndMuteIfNecessary();
      return;
    }
    throw new IllegalStateException("mSimpleExoPlayer is null!");
  }
  
  public int getAudioSessionId()
  {
    if (mSimpleExoPlayer != null) {
      return mSimpleExoPlayer.getAudioSessionId();
    }
    return 0;
  }
  
  void getExtraStatusFields(Bundle paramBundle)
  {
    int i = (int)mSimpleExoPlayer.getDuration();
    paramBundle.putInt("durationMillis", i);
    paramBundle.putInt("positionMillis", getClippedIntegerForValue(Integer.valueOf((int)mSimpleExoPlayer.getCurrentPosition()), Integer.valueOf(0), Integer.valueOf(i)));
    paramBundle.putInt("playableDurationMillis", getClippedIntegerForValue(Integer.valueOf((int)mSimpleExoPlayer.getBufferedPosition()), Integer.valueOf(0), Integer.valueOf(i)));
    boolean bool1 = mSimpleExoPlayer.getPlayWhenReady();
    boolean bool2 = true;
    if ((bool1) && (mSimpleExoPlayer.getPlaybackState() == 3)) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    paramBundle.putBoolean("isPlaying", bool1);
    bool1 = bool2;
    if (!mIsLoading) {
      if (mSimpleExoPlayer.getPlaybackState() == 2) {
        bool1 = bool2;
      } else {
        bool1 = false;
      }
    }
    paramBundle.putBoolean("isBuffering", bool1);
    paramBundle.putBoolean("isLooping", mIsLooping);
  }
  
  String getImplementationName()
  {
    return "SimpleExoPlayer";
  }
  
  public Pair getVideoWidthHeight()
  {
    if (mVideoWidthHeight != null) {
      return mVideoWidthHeight;
    }
    return new Pair(Integer.valueOf(0), Integer.valueOf(0));
  }
  
  boolean isLoaded()
  {
    return mSimpleExoPlayer != null;
  }
  
  public void load(Bundle paramBundle, PlayerData.LoadCompletionListener paramLoadCompletionListener)
  {
    mLoadCompletionListener = paramLoadCompletionListener;
    paramLoadCompletionListener = new Handler();
    Object localObject = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter()));
    mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mAVModule.getContext(), (TrackSelector)localObject);
    mSimpleExoPlayer.addListener(this);
    mSimpleExoPlayer.addVideoListener(this);
    localObject = ((expo.modules.package_3.player.datasource.DataSourceFactoryProvider)mAVModule.getModuleRegistry().getModule(expo.modules.av.player.datasource.DataSourceFactoryProvider.class)).createFactory(mReactContext, mAVModule.getModuleRegistry(), Util.getUserAgent(mAVModule.getContext(), "yourApplicationName"), mRequestHeaders);
    Uri localUri = mUri;
    String str = mOverridingExtension;
    try
    {
      paramLoadCompletionListener = buildMediaSource(localUri, str, paramLoadCompletionListener, (DataSource.Factory)localObject);
      localObject = mSimpleExoPlayer;
      ((SimpleExoPlayer)localObject).prepare(paramLoadCompletionListener);
      setStatus(paramBundle, null);
      return;
    }
    catch (IllegalStateException paramBundle)
    {
      onFatalError(paramBundle);
    }
  }
  
  public void onDownstreamFormatChanged(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onLoadCanceled(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onLoadCompleted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onLoadError(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData, IOException paramIOException, boolean paramBoolean) {}
  
  public void onLoadError(IOException paramIOException)
  {
    if (mLoadCompletionListener != null)
    {
      PlayerData.LoadCompletionListener localLoadCompletionListener = mLoadCompletionListener;
      mLoadCompletionListener = null;
      localLoadCompletionListener.onLoadError(paramIOException.toString());
    }
  }
  
  public void onLoadStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onLoadingChanged(boolean paramBoolean)
  {
    mIsLoading = paramBoolean;
    callStatusUpdateListener();
  }
  
  public void onMediaPeriodCreated(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId) {}
  
  public void onMediaPeriodReleased(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId) {}
  
  public void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters) {}
  
  public void onPlayerError(ExoPlaybackException paramExoPlaybackException)
  {
    onFatalError(paramExoPlaybackException.getCause());
  }
  
  public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
  {
    if ((paramInt == 3) && (mLoadCompletionListener != null))
    {
      PlayerData.LoadCompletionListener localLoadCompletionListener = mLoadCompletionListener;
      mLoadCompletionListener = null;
      localLoadCompletionListener.onLoadSuccess(getStatus());
    }
    if ((mLastPlaybackState != null) && (paramInt != mLastPlaybackState.intValue()) && (paramInt == 4)) {
      callStatusUpdateListenerWithDidJustFinish();
    } else {
      callStatusUpdateListener();
    }
    mLastPlaybackState = Integer.valueOf(paramInt);
  }
  
  public void onPositionDiscontinuity(int paramInt)
  {
    if (paramInt == 0) {
      callStatusUpdateListenerWithDidJustFinish();
    }
  }
  
  public void onReadingStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId) {}
  
  public void onRenderedFirstFrame()
  {
    if ((!mFirstFrameRendered) && (mVideoWidthHeight != null) && (mVideoSizeUpdateListener != null)) {
      mVideoSizeUpdateListener.onVideoSizeUpdate(mVideoWidthHeight);
    }
    mFirstFrameRendered = true;
  }
  
  public void onRepeatModeChanged(int paramInt) {}
  
  public void onSeekProcessed() {}
  
  public void onShuffleModeEnabledChanged(boolean paramBoolean) {}
  
  public void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray) {}
  
  public void onUpstreamDiscarded(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    mVideoWidthHeight = new Pair(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    if ((mFirstFrameRendered) && (mVideoSizeUpdateListener != null)) {
      mVideoSizeUpdateListener.onVideoSizeUpdate(mVideoWidthHeight);
    }
  }
  
  public void pauseImmediately()
  {
    if (mSimpleExoPlayer != null) {
      mSimpleExoPlayer.setPlayWhenReady(false);
    }
    stopUpdatingProgressIfNecessary();
  }
  
  void playPlayerWithRateAndMuteIfNecessary()
    throws AudioFocusNotAcquiredException
  {
    if (mSimpleExoPlayer != null)
    {
      if (!shouldPlayerPlay()) {
        return;
      }
      if (!mIsMuted) {
        mAVModule.acquireAudioFocus();
      }
      updateVolumeMuteAndDuck();
      SimpleExoPlayer localSimpleExoPlayer = mSimpleExoPlayer;
      float f2 = mRate;
      float f1;
      if (mShouldCorrectPitch) {
        f1 = 1.0F;
      } else {
        f1 = mRate;
      }
      localSimpleExoPlayer.setPlaybackParameters(new PlaybackParameters(f2, f1));
      mSimpleExoPlayer.setPlayWhenReady(mShouldPlay);
      beginUpdatingProgressIfNecessary();
    }
  }
  
  public void release()
  {
    try
    {
      if (mSimpleExoPlayer != null)
      {
        mSimpleExoPlayer.release();
        mSimpleExoPlayer = null;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean requiresAudioFocus()
  {
    return (mSimpleExoPlayer != null) && ((mSimpleExoPlayer.getPlayWhenReady()) || (shouldPlayerPlay())) && (!mIsMuted);
  }
  
  boolean shouldContinueUpdatingProgress()
  {
    return (mSimpleExoPlayer != null) && (mSimpleExoPlayer.getPlayWhenReady());
  }
  
  public void tryUpdateVideoSurface(Surface paramSurface)
  {
    if (mSimpleExoPlayer != null) {
      mSimpleExoPlayer.setVideoSurface(paramSurface);
    }
  }
  
  public void updateVolumeMuteAndDuck()
  {
    if (mSimpleExoPlayer != null) {
      mSimpleExoPlayer.setVolume(mAVModule.getVolumeForDuckAndFocus(mIsMuted, mVolume));
    }
  }
}
