package com.google.android.exoplayer2;

import android.os.Handler;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.upgrade.DrmSessionManager;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

public abstract interface RenderersFactory
{
  public abstract Renderer[] createRenderers(Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, AudioRendererEventListener paramAudioRendererEventListener, TextOutput paramTextOutput, MetadataOutput paramMetadataOutput, DrmSessionManager paramDrmSessionManager);
}
