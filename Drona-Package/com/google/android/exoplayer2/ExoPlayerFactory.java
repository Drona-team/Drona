package com.google.android.exoplayer2;

import android.content.Context;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.analytics.AnalyticsCollector.Factory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upgrade.DrmSessionManager;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter.Builder;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Util;

public final class ExoPlayerFactory
{
  @Nullable
  private static BandwidthMeter singletonBandwidthMeter;
  
  private ExoPlayerFactory() {}
  
  private static BandwidthMeter getDefaultBandwidthMeter()
  {
    try
    {
      if (singletonBandwidthMeter == null) {
        singletonBandwidthMeter = new DefaultBandwidthMeter.Builder().build();
      }
      BandwidthMeter localBandwidthMeter = singletonBandwidthMeter;
      return localBandwidthMeter;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector)
  {
    return newInstance(paramArrayOfRenderer, paramTrackSelector, new DefaultLoadControl());
  }
  
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return newInstance(paramArrayOfRenderer, paramTrackSelector, paramLoadControl, Util.getLooper());
  }
  
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl, Looper paramLooper)
  {
    return newInstance(paramArrayOfRenderer, paramTrackSelector, paramLoadControl, getDefaultBandwidthMeter(), paramLooper);
  }
  
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl, BandwidthMeter paramBandwidthMeter, Looper paramLooper)
  {
    return new ExoPlayerImpl(paramArrayOfRenderer, paramTrackSelector, paramLoadControl, paramBandwidthMeter, Clock.DEFAULT, paramLooper);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext)
  {
    return newSimpleInstance(paramContext, new DefaultTrackSelector());
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, new DefaultLoadControl());
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, null, Util.getLooper());
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, Util.getLooper());
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, Looper paramLooper)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, new AnalyticsCollector.Factory(), paramLooper);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, AnalyticsCollector.Factory paramFactory)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramFactory, Util.getLooper());
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, AnalyticsCollector.Factory paramFactory, Looper paramLooper)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, getDefaultBandwidthMeter(), paramFactory, paramLooper);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, BandwidthMeter paramBandwidthMeter)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramBandwidthMeter, new AnalyticsCollector.Factory(), Util.getLooper());
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, BandwidthMeter paramBandwidthMeter, AnalyticsCollector.Factory paramFactory, Looper paramLooper)
  {
    return new SimpleExoPlayer(paramContext, paramRenderersFactory, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramBandwidthMeter, paramFactory, paramLooper);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, DrmSessionManager paramDrmSessionManager)
  {
    return newSimpleInstance(paramContext, paramRenderersFactory, paramTrackSelector, new DefaultLoadControl(), paramDrmSessionManager);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector)
  {
    return newSimpleInstance(paramContext, new DefaultRenderersFactory(paramContext), paramTrackSelector);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return newSimpleInstance(paramContext, new DefaultRenderersFactory(paramContext), paramTrackSelector, paramLoadControl);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager)
  {
    return newSimpleInstance(paramContext, new DefaultRenderersFactory(paramContext), paramTrackSelector, paramLoadControl, paramDrmSessionManager);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, int paramInt)
  {
    return newSimpleInstance(paramContext, new DefaultRenderersFactory(paramContext, paramInt), paramTrackSelector, paramLoadControl, paramDrmSessionManager);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager paramDrmSessionManager, int paramInt, long paramLong)
  {
    return newSimpleInstance(paramContext, new DefaultRenderersFactory(paramContext, paramInt, paramLong), paramTrackSelector, paramLoadControl, paramDrmSessionManager);
  }
  
  public static SimpleExoPlayer newSimpleInstance(RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector)
  {
    return newSimpleInstance(null, paramRenderersFactory, paramTrackSelector, new DefaultLoadControl());
  }
}
