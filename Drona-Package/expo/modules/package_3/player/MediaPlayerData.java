package expo.modules.package_3.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import expo.modules.package_3.AVManagerInterface;
import expo.modules.package_3.AudioFocusNotAcquiredException;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.unimodules.core.ModuleRegistry;

class MediaPlayerData
  extends PlayerData
  implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener
{
  static final String IMPLEMENTATION_NAME = "MediaPlayer";
  private boolean mIsBuffering = false;
  private MediaPlayer mMediaPlayer = null;
  private boolean mMediaPlayerHasStartedEver = false;
  private ModuleRegistry mModuleRegistry = null;
  private Integer mPlayableDurationMillis = null;
  
  MediaPlayerData(AVManagerInterface paramAVManagerInterface, Context paramContext, Uri paramUri, Map paramMap)
  {
    super(paramAVManagerInterface, paramUri, paramMap);
    mModuleRegistry = paramAVManagerInterface.getModuleRegistry();
  }
  
  private List getHttpCookiesList()
  {
    Object localObject1;
    Object localObject2;
    if (mModuleRegistry != null)
    {
      localObject1 = (CookieHandler)mModuleRegistry.getModule(CookieHandler.class);
      if (localObject1 != null) {
        localObject2 = mUri;
      }
    }
    try
    {
      localObject1 = ((CookieHandler)localObject1).get(URI.create(((Uri)localObject2).toString()), null).get("Cookie");
      localObject2 = (List)localObject1;
      if (localObject2 != null)
      {
        localObject1 = new ArrayList();
        localObject2 = ((List)localObject2).iterator();
        for (;;)
        {
          boolean bool = ((Iterator)localObject2).hasNext();
          if (!bool) {
            break;
          }
          Object localObject3 = ((Iterator)localObject2).next();
          localObject3 = (String)localObject3;
          ((List)localObject1).addAll(HttpCookie.parse((String)localObject3));
        }
        return localObject1;
      }
      localObject1 = Collections.emptyList();
      return localObject1;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    return Collections.emptyList();
  }
  
  private void playMediaPlayerWithRateMAndHigher(float paramFloat)
  {
    PlaybackParams localPlaybackParams = mMediaPlayer.getPlaybackParams();
    float f;
    if (mShouldCorrectPitch) {
      f = 1.0F;
    } else {
      f = paramFloat;
    }
    localPlaybackParams.setPitch(f);
    localPlaybackParams.setSpeed(paramFloat);
    localPlaybackParams.setAudioFallbackMode(0);
    mMediaPlayer.setPlaybackParams(localPlaybackParams);
    mMediaPlayer.start();
  }
  
  void applyNewStatus(Integer paramInteger, Boolean paramBoolean)
    throws AudioFocusNotAcquiredException, IllegalStateException
  {
    if (mMediaPlayer != null)
    {
      if ((Build.VERSION.SDK_INT < 23) && (mRate != 1.0F))
      {
        Log.w("Expo MediaPlayerData", "Cannot set audio/video playback rate for Android SDK < 23.");
        mRate = 1.0F;
      }
      if (paramBoolean != null) {
        mMediaPlayer.setLooping(paramBoolean.booleanValue());
      }
      if (!shouldPlayerPlay())
      {
        if (mMediaPlayerHasStartedEver) {
          mMediaPlayer.pause();
        }
        stopUpdatingProgressIfNecessary();
      }
      updateVolumeMuteAndDuck();
      if ((paramInteger != null) && (paramInteger.intValue() != mMediaPlayer.getCurrentPosition())) {
        mMediaPlayer.seekTo(paramInteger.intValue());
      }
      playPlayerWithRateAndMuteIfNecessary();
      return;
    }
    throw new IllegalStateException("mMediaPlayer is null!");
  }
  
  public int getAudioSessionId()
  {
    if (mMediaPlayer != null) {
      return mMediaPlayer.getAudioSessionId();
    }
    return 0;
  }
  
  void getExtraStatusFields(Bundle paramBundle)
  {
    Integer localInteger2 = Integer.valueOf(mMediaPlayer.getDuration());
    Integer localInteger1 = localInteger2;
    if (localInteger2.intValue() < 0) {
      localInteger1 = null;
    }
    if (localInteger1 != null) {
      paramBundle.putInt("durationMillis", localInteger1.intValue());
    }
    paramBundle.putInt("positionMillis", getClippedIntegerForValue(Integer.valueOf(mMediaPlayer.getCurrentPosition()), Integer.valueOf(0), localInteger1));
    if (mPlayableDurationMillis != null) {
      paramBundle.putInt("playableDurationMillis", getClippedIntegerForValue(mPlayableDurationMillis, Integer.valueOf(0), localInteger1));
    }
    paramBundle.putBoolean("isPlaying", mMediaPlayer.isPlaying());
    paramBundle.putBoolean("isBuffering", mIsBuffering);
    paramBundle.putBoolean("isLooping", mMediaPlayer.isLooping());
  }
  
  String getImplementationName()
  {
    return "MediaPlayer";
  }
  
  public Pair getVideoWidthHeight()
  {
    if (mMediaPlayer == null) {
      return new Pair(Integer.valueOf(0), Integer.valueOf(0));
    }
    return new Pair(Integer.valueOf(mMediaPlayer.getVideoWidth()), Integer.valueOf(mMediaPlayer.getVideoHeight()));
  }
  
  boolean isLoaded()
  {
    return mMediaPlayer != null;
  }
  
  public void load(Bundle paramBundle, PlayerData.LoadCompletionListener paramLoadCompletionListener)
  {
    if (mMediaPlayer != null)
    {
      paramLoadCompletionListener.onLoadError("Load encountered an error: MediaPlayerData cannot be loaded twice.");
      return;
    }
    Object localObject1 = new MediaPlayer();
    try
    {
      int i = Build.VERSION.SDK_INT;
      if (i >= 26)
      {
        ((MediaPlayer)localObject1).setDataSource(mAVModule.getContext(), mUri, null, getHttpCookiesList());
      }
      else
      {
        HashMap localHashMap = new HashMap(1);
        Object localObject2 = new StringBuilder();
        Object localObject3 = getHttpCookiesList().iterator();
        boolean bool;
        for (;;)
        {
          bool = ((Iterator)localObject3).hasNext();
          if (!bool) {
            break;
          }
          HttpCookie localHttpCookie = (HttpCookie)((Iterator)localObject3).next();
          ((StringBuilder)localObject2).append(localHttpCookie.getName());
          ((StringBuilder)localObject2).append("=");
          ((StringBuilder)localObject2).append(localHttpCookie.getValue());
          ((StringBuilder)localObject2).append("; ");
        }
        ((StringBuilder)localObject2).append("\r\n");
        localHashMap.put("Cookie", ((StringBuilder)localObject2).toString());
        localObject2 = mRequestHeaders;
        if (localObject2 != null)
        {
          localObject2 = mRequestHeaders.entrySet().iterator();
          for (;;)
          {
            bool = ((Iterator)localObject2).hasNext();
            if (!bool) {
              break;
            }
            localObject3 = (Map.Entry)((Iterator)localObject2).next();
            bool = ((Map.Entry)localObject3).getValue() instanceof String;
            if (bool) {
              localHashMap.put(((Map.Entry)localObject3).getKey(), (String)((Map.Entry)localObject3).getValue());
            }
          }
        }
        ((MediaPlayer)localObject1).setDataSource(mAVModule.getContext(), mUri, localHashMap);
      }
      ((MediaPlayer)localObject1).setOnErrorListener(new MediaPlayerData.1(this, paramLoadCompletionListener));
      ((MediaPlayer)localObject1).setOnPreparedListener(new MediaPlayerData.2(this, paramBundle, paramLoadCompletionListener));
      try
      {
        ((MediaPlayer)localObject1).prepareAsync();
        return;
      }
      catch (Throwable paramBundle)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Load encountered an error: an exception was thrown from prepareAsync() with message: ");
        ((StringBuilder)localObject1).append(paramBundle.toString());
        paramLoadCompletionListener.onLoadError(((StringBuilder)localObject1).toString());
        return;
      }
      return;
    }
    catch (Throwable paramBundle)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Load encountered an error: setDataSource() threw an exception was thrown with message: ");
      ((StringBuilder)localObject1).append(paramBundle.toString());
      paramLoadCompletionListener.onLoadError(((StringBuilder)localObject1).toString());
    }
  }
  
  public void onBufferingUpdate(MediaPlayer paramMediaPlayer, int paramInt)
  {
    if (paramMediaPlayer.getDuration() >= 0) {
      mPlayableDurationMillis = Integer.valueOf((int)(paramMediaPlayer.getDuration() * (paramInt / 100.0D)));
    } else {
      mPlayableDurationMillis = null;
    }
    callStatusUpdateListener();
  }
  
  public void onCompletion(MediaPlayer paramMediaPlayer)
  {
    callStatusUpdateListenerWithDidJustFinish();
    if (!paramMediaPlayer.isLooping()) {
      mAVModule.abandonAudioFocusIfUnused();
    }
  }
  
  public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    release();
    if (mErrorListener != null)
    {
      paramMediaPlayer = mErrorListener;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("MediaPlayer failed with 'what' code ");
      localStringBuilder.append(paramInt1);
      localStringBuilder.append(" and 'extra' code ");
      localStringBuilder.append(paramInt2);
      localStringBuilder.append(".");
      paramMediaPlayer.onError(localStringBuilder.toString());
    }
    return true;
  }
  
  public boolean onInfo(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      break;
    case 702: 
      mIsBuffering = false;
      beginUpdatingProgressIfNecessary();
      break;
    case 701: 
      mIsBuffering = true;
      break;
    case 3: 
      if (mVideoSizeUpdateListener != null) {
        mVideoSizeUpdateListener.onVideoSizeUpdate(new Pair(Integer.valueOf(paramMediaPlayer.getVideoWidth()), Integer.valueOf(paramMediaPlayer.getVideoHeight())));
      }
      break;
    }
    callStatusUpdateListener();
    return true;
  }
  
  public void onSeekComplete(MediaPlayer paramMediaPlayer)
  {
    callStatusUpdateListener();
  }
  
  public void onVideoSizeChanged(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    if (mVideoSizeUpdateListener != null) {
      mVideoSizeUpdateListener.onVideoSizeUpdate(new Pair(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)));
    }
  }
  
  public void pauseImmediately()
  {
    if ((mMediaPlayer != null) && (mMediaPlayerHasStartedEver)) {
      mMediaPlayer.pause();
    }
    stopUpdatingProgressIfNecessary();
  }
  
  void playPlayerWithRateAndMuteIfNecessary()
    throws AudioFocusNotAcquiredException
  {
    if (mMediaPlayer != null)
    {
      if (!shouldPlayerPlay()) {
        return;
      }
      if (!mIsMuted) {
        mAVModule.acquireAudioFocus();
      }
      updateVolumeMuteAndDuck();
      if (Build.VERSION.SDK_INT < 23)
      {
        if (!mMediaPlayer.isPlaying())
        {
          mMediaPlayer.start();
          mMediaPlayerHasStartedEver = true;
        }
      }
      else {
        j = 0;
      }
    }
    try
    {
      PlaybackParams localPlaybackParams = mMediaPlayer.getPlaybackParams();
      float f1 = localPlaybackParams.getSpeed();
      float f2 = localPlaybackParams.getPitch();
      boolean bool1;
      if (f2 == 1.0F) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      f2 = mRate;
      i = j;
      if (f1 == f2)
      {
        boolean bool2 = mShouldCorrectPitch;
        i = j;
        if (bool1 == bool2) {
          i = 1;
        }
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        int i = j;
      }
    }
    if ((mRate != 0.0F) && ((!mMediaPlayer.isPlaying()) || (i == 0)))
    {
      if (Build.VERSION.SDK_INT >= 24)
      {
        playMediaPlayerWithRateMAndHigher(mRate);
      }
      else if (Build.VERSION.SDK_INT >= 23)
      {
        playMediaPlayerWithRateMAndHigher(2.0F);
        mMediaPlayer.pause();
        playMediaPlayerWithRateMAndHigher(mRate);
      }
      mMediaPlayerHasStartedEver = true;
    }
    beginUpdatingProgressIfNecessary();
  }
  
  public void release()
  {
    try
    {
      stopUpdatingProgressIfNecessary();
      if (mMediaPlayer != null)
      {
        mMediaPlayer.setOnBufferingUpdateListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnInfoListener(null);
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
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
    return (mMediaPlayer != null) && ((mMediaPlayer.isPlaying()) || (shouldPlayerPlay())) && (!mIsMuted);
  }
  
  boolean shouldContinueUpdatingProgress()
  {
    return (mMediaPlayer != null) && (!mIsBuffering);
  }
  
  public void tryUpdateVideoSurface(Surface paramSurface)
  {
    if (mMediaPlayer == null) {
      return;
    }
    mMediaPlayer.setSurface(paramSurface);
    if ((!mMediaPlayerHasStartedEver) && (!mShouldPlay))
    {
      mMediaPlayer.start();
      mMediaPlayer.pause();
      mMediaPlayerHasStartedEver = true;
    }
  }
  
  public void updateVolumeMuteAndDuck()
  {
    if (mMediaPlayer != null)
    {
      float f = mAVModule.getVolumeForDuckAndFocus(mIsMuted, mVolume);
      mMediaPlayer.setVolume(f, f);
    }
  }
}
