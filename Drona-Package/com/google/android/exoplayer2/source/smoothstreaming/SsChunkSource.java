package com.google.android.exoplayer2.source.smoothstreaming;

import com.google.android.exoplayer2.extractor.ClickListeners.TrackEncryptionBox;
import com.google.android.exoplayer2.source.chunk.ChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;

public abstract interface SsChunkSource
  extends ChunkSource
{
  public abstract void updateManifest(SsManifest paramSsManifest);
  
  public static abstract interface Factory
  {
    public abstract SsChunkSource createChunkSource(LoaderErrorThrower paramLoaderErrorThrower, SsManifest paramSsManifest, int paramInt, TrackSelection paramTrackSelection, TrackEncryptionBox[] paramArrayOfTrackEncryptionBox, TransferListener paramTransferListener);
  }
}
