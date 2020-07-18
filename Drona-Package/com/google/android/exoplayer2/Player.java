package com.google.android.exoplayer2;

import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.audio.AuxEffectInfo;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.spherical.CameraMotionListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract interface Player
{
  public static final int DISCONTINUITY_REASON_AD_INSERTION = 3;
  public static final int DISCONTINUITY_REASON_INTERNAL = 4;
  public static final int DISCONTINUITY_REASON_PERIOD_TRANSITION = 0;
  public static final int DISCONTINUITY_REASON_SEEK = 1;
  public static final int DISCONTINUITY_REASON_SEEK_ADJUSTMENT = 2;
  public static final int REPEAT_MODE_ALL = 2;
  public static final int REPEAT_MODE_OFF = 0;
  public static final int REPEAT_MODE_ONE = 1;
  public static final int STATE_BUFFERING = 2;
  public static final int STATE_ENDED = 4;
  public static final int STATE_IDLE = 1;
  public static final int STATE_READY = 3;
  public static final int TIMELINE_CHANGE_REASON_DYNAMIC = 2;
  public static final int TIMELINE_CHANGE_REASON_PREPARED = 0;
  public static final int TIMELINE_CHANGE_REASON_RESET = 1;
  
  public abstract void addListener(EventListener paramEventListener);
  
  public abstract Looper getApplicationLooper();
  
  public abstract AudioComponent getAudioComponent();
  
  public abstract int getBufferedPercentage();
  
  public abstract long getBufferedPosition();
  
  public abstract long getContentBufferedPosition();
  
  public abstract long getContentDuration();
  
  public abstract long getContentPosition();
  
  public abstract int getCurrentAdGroupIndex();
  
  public abstract int getCurrentAdIndexInAdGroup();
  
  public abstract Object getCurrentManifest();
  
  public abstract int getCurrentPeriodIndex();
  
  public abstract long getCurrentPosition();
  
  public abstract Object getCurrentTag();
  
  public abstract Timeline getCurrentTimeline();
  
  public abstract TrackGroupArray getCurrentTrackGroups();
  
  public abstract TrackSelectionArray getCurrentTrackSelections();
  
  public abstract int getCurrentWindowIndex();
  
  public abstract long getDuration();
  
  public abstract int getNextWindowIndex();
  
  public abstract boolean getPlayWhenReady();
  
  public abstract ExoPlaybackException getPlaybackError();
  
  public abstract PlaybackParameters getPlaybackParameters();
  
  public abstract int getPlaybackState();
  
  public abstract int getPreviousWindowIndex();
  
  public abstract int getRendererCount();
  
  public abstract int getRendererType(int paramInt);
  
  public abstract int getRepeatMode();
  
  public abstract boolean getShuffleModeEnabled();
  
  public abstract TextComponent getTextComponent();
  
  public abstract long getTotalBufferedDuration();
  
  public abstract VideoComponent getVideoComponent();
  
  public abstract boolean hasNext();
  
  public abstract boolean hasPrevious();
  
  public abstract boolean isCurrentWindowDynamic();
  
  public abstract boolean isCurrentWindowSeekable();
  
  public abstract boolean isLoading();
  
  public abstract boolean isPlayingAd();
  
  public abstract void next();
  
  public abstract void previous();
  
  public abstract void release();
  
  public abstract void removeListener(EventListener paramEventListener);
  
  public abstract void seekTo(int paramInt, long paramLong);
  
  public abstract void seekTo(long paramLong);
  
  public abstract void seekToDefaultPosition();
  
  public abstract void seekToDefaultPosition(int paramInt);
  
  public abstract void setPlayWhenReady(boolean paramBoolean);
  
  public abstract void setPlaybackParameters(PlaybackParameters paramPlaybackParameters);
  
  public abstract void setRepeatMode(int paramInt);
  
  public abstract void setShuffleModeEnabled(boolean paramBoolean);
  
  public abstract void stop();
  
  public abstract void stop(boolean paramBoolean);
  
  public static abstract interface AudioComponent
  {
    public abstract void addAudioListener(AudioListener paramAudioListener);
    
    public abstract void clearAuxEffectInfo();
    
    public abstract AudioAttributes getAudioAttributes();
    
    public abstract int getAudioSessionId();
    
    public abstract float getVolume();
    
    public abstract void removeAudioListener(AudioListener paramAudioListener);
    
    public abstract void setAudioAttributes(AudioAttributes paramAudioAttributes);
    
    public abstract void setAudioAttributes(AudioAttributes paramAudioAttributes, boolean paramBoolean);
    
    public abstract void setAuxEffectInfo(AuxEffectInfo paramAuxEffectInfo);
    
    public abstract void setVolume(float paramFloat);
  }
  
  @Deprecated
  public static abstract class DefaultEventListener
    implements Player.EventListener
  {
    public DefaultEventListener() {}
    
    public void onTimelineChanged(Timeline paramTimeline, Object paramObject) {}
    
    public void onTimelineChanged(Timeline paramTimeline, Object paramObject, int paramInt)
    {
      onTimelineChanged(paramTimeline, paramObject);
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface DiscontinuityReason {}
  
  public static abstract interface EventListener
  {
    public abstract void onLoadingChanged(boolean paramBoolean);
    
    public abstract void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters);
    
    public abstract void onPlayerError(ExoPlaybackException paramExoPlaybackException);
    
    public abstract void onPlayerStateChanged(boolean paramBoolean, int paramInt);
    
    public abstract void onPositionDiscontinuity(int paramInt);
    
    public abstract void onRepeatModeChanged(int paramInt);
    
    public abstract void onSeekProcessed();
    
    public abstract void onShuffleModeEnabledChanged(boolean paramBoolean);
    
    public abstract void onTimelineChanged(Timeline paramTimeline, Object paramObject, int paramInt);
    
    public abstract void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface RepeatMode {}
  
  public static abstract interface TextComponent
  {
    public abstract void addTextOutput(TextOutput paramTextOutput);
    
    public abstract void removeTextOutput(TextOutput paramTextOutput);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface TimelineChangeReason {}
  
  public static abstract interface VideoComponent
  {
    public abstract void addVideoListener(VideoListener paramVideoListener);
    
    public abstract void clearCameraMotionListener(CameraMotionListener paramCameraMotionListener);
    
    public abstract void clearVideoFrameMetadataListener(VideoFrameMetadataListener paramVideoFrameMetadataListener);
    
    public abstract void clearVideoSurface();
    
    public abstract void clearVideoSurface(Surface paramSurface);
    
    public abstract void clearVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder);
    
    public abstract void clearVideoSurfaceView(SurfaceView paramSurfaceView);
    
    public abstract void clearVideoTextureView(TextureView paramTextureView);
    
    public abstract int getVideoScalingMode();
    
    public abstract void removeVideoListener(VideoListener paramVideoListener);
    
    public abstract void setCameraMotionListener(CameraMotionListener paramCameraMotionListener);
    
    public abstract void setVideoFrameMetadataListener(VideoFrameMetadataListener paramVideoFrameMetadataListener);
    
    public abstract void setVideoScalingMode(int paramInt);
    
    public abstract void setVideoSurface(Surface paramSurface);
    
    public abstract void setVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder);
    
    public abstract void setVideoSurfaceView(SurfaceView paramSurfaceView);
    
    public abstract void setVideoTextureView(TextureView paramTextureView);
  }
}
