package com.google.android.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaCodec.OnFrameRenderedListener;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.mediacodec.MediaFormatUtil;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData;
import com.google.android.exoplayer2.upgrade.DrmSessionManager;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.List;

@TargetApi(16)
public class MediaCodecVideoRenderer
  extends MediaCodecRenderer
{
  private static final float INITIAL_FORMAT_MAX_INPUT_SIZE_SCALE_FACTOR = 1.5F;
  private static final String KEY_CROP_BOTTOM = "crop-bottom";
  private static final String KEY_CROP_LEFT = "crop-left";
  private static final String KEY_CROP_RIGHT = "crop-right";
  private static final String KEY_CROP_TOP = "crop-top";
  private static final int MAX_PENDING_OUTPUT_STREAM_OFFSET_COUNT = 10;
  private static final String PAGE_KEY = "MediaCodecVideoRenderer";
  private static final int[] STANDARD_LONG_EDGE_VIDEO_PX = { 1920, 1600, 1440, 1280, 960, 854, 640, 540, 480 };
  private static boolean deviceNeedsSetOutputSurfaceWorkaround;
  private static boolean evaluatedDeviceNeedsSetOutputSurfaceWorkaround;
  private final long allowedJoiningTimeMs;
  private int buffersInCodecCount;
  private CodecMaxValues codecMaxValues;
  private boolean codecNeedsSetOutputSurfaceWorkaround;
  private int consecutiveDroppedFrameCount;
  private final Context context;
  private int currentHeight;
  private float currentPixelWidthHeightRatio;
  private int currentUnappliedRotationDegrees;
  private int currentWidth;
  private final boolean deviceNeedsAutoFrcWorkaround;
  private long droppedFrameAccumulationStartTimeMs;
  private int droppedFrames;
  private Surface dummySurface;
  private final VideoRendererEventListener.EventDispatcher eventDispatcher;
  @Nullable
  private VideoFrameMetadataListener frameMetadataListener;
  private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
  private long initialPositionUs;
  private long joiningDeadlineMs;
  private long lastInputTimeUs;
  private long lastRenderTimeUs;
  private final int maxDroppedFramesToNotify;
  private long outputStreamOffsetUs;
  private int pendingOutputStreamOffsetCount;
  private final long[] pendingOutputStreamOffsetsUs;
  private final long[] pendingOutputStreamSwitchTimesUs;
  private float pendingPixelWidthHeightRatio;
  private int pendingRotationDegrees;
  private boolean renderedFirstFrame;
  private int reportedHeight;
  private float reportedPixelWidthHeightRatio;
  private int reportedUnappliedRotationDegrees;
  private int reportedWidth;
  private int scalingMode;
  private Surface surface;
  private boolean tunneling;
  private int tunnelingAudioSessionId;
  OnFrameRenderedListenerV23 tunnelingOnFrameRenderedListener;
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector)
  {
    this(paramContext, paramMediaCodecSelector, 0L);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong)
  {
    this(paramContext, paramMediaCodecSelector, paramLong, null, null, -1);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong, Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, int paramInt)
  {
    this(paramContext, paramMediaCodecSelector, paramLong, null, false, paramHandler, paramVideoRendererEventListener, paramInt);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong, DrmSessionManager paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, int paramInt)
  {
    super(2, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, 30.0F);
    allowedJoiningTimeMs = paramLong;
    maxDroppedFramesToNotify = paramInt;
    context = paramContext.getApplicationContext();
    frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(context);
    eventDispatcher = new VideoRendererEventListener.EventDispatcher(paramHandler, paramVideoRendererEventListener);
    deviceNeedsAutoFrcWorkaround = deviceNeedsAutoFrcWorkaround();
    pendingOutputStreamOffsetsUs = new long[10];
    pendingOutputStreamSwitchTimesUs = new long[10];
    outputStreamOffsetUs = -9223372036854775807L;
    lastInputTimeUs = -9223372036854775807L;
    joiningDeadlineMs = -9223372036854775807L;
    currentWidth = -1;
    currentHeight = -1;
    currentPixelWidthHeightRatio = -1.0F;
    pendingPixelWidthHeightRatio = -1.0F;
    scalingMode = 1;
    clearReportedVideoSize();
  }
  
  private void clearRenderedFirstFrame()
  {
    renderedFirstFrame = false;
    if ((Util.SDK_INT >= 23) && (tunneling))
    {
      MediaCodec localMediaCodec = getCodec();
      if (localMediaCodec != null) {
        tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(localMediaCodec, null);
      }
    }
  }
  
  private void clearReportedVideoSize()
  {
    reportedWidth = -1;
    reportedHeight = -1;
    reportedPixelWidthHeightRatio = -1.0F;
    reportedUnappliedRotationDegrees = -1;
  }
  
  private static void configureTunnelingV21(MediaFormat paramMediaFormat, int paramInt)
  {
    paramMediaFormat.setFeatureEnabled("tunneled-playback", true);
    paramMediaFormat.setInteger("audio-session-id", paramInt);
  }
  
  private static boolean deviceNeedsAutoFrcWorkaround()
  {
    return (Util.SDK_INT <= 22) && ("foster".equals(Util.DEVICE)) && ("NVIDIA".equals(Util.MANUFACTURER));
  }
  
  private static int getCodecMaxInputSize(MediaCodecInfo paramMediaCodecInfo, String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 != -1)
    {
      if (paramInt2 == -1) {
        return -1;
      }
      int i = paramString.hashCode();
      int j = 4;
      switch (i)
      {
      default: 
        break;
      case 1599127257: 
        if (paramString.equals("video/x-vnd.on2.vp9")) {
          i = 5;
        }
        break;
      case 1599127256: 
        if (paramString.equals("video/x-vnd.on2.vp8")) {
          i = 3;
        }
        break;
      case 1331836730: 
        if (paramString.equals("video/avc")) {
          i = 2;
        }
        break;
      case 1187890754: 
        if (paramString.equals("video/mp4v-es")) {
          i = 1;
        }
        break;
      case -1662541442: 
        if (paramString.equals("video/hevc")) {
          i = 4;
        }
        break;
      case -1664118616: 
        if (paramString.equals("video/3gpp")) {
          i = 0;
        }
        break;
      }
      i = -1;
      switch (i)
      {
      default: 
        return -1;
      case 4: 
      case 5: 
        paramInt2 = paramInt1 * paramInt2;
        paramInt1 = j;
        break;
      case 3: 
        paramInt1 *= paramInt2;
        break;
      case 2: 
        if (!"BRAVIA 4K 2015".equals(Util.MODEL))
        {
          if ("Amazon".equals(Util.MANUFACTURER))
          {
            if ("KFSOWI".equals(Util.MODEL)) {
              break label360;
            }
            if (("AFTS".equals(Util.MODEL)) && (secure)) {
              return -1;
            }
          }
          paramInt1 = Util.ceilDivide(paramInt1, 16) * Util.ceilDivide(paramInt2, 16) * 16 * 16;
        }
        else
        {
          return -1;
        }
        break;
      case 0: 
      case 1: 
        paramInt1 *= paramInt2;
      }
      i = 2;
      paramInt2 = paramInt1;
      paramInt1 = i;
      return paramInt2 * 3 / (paramInt1 * 2);
    }
    label360:
    return -1;
  }
  
  private static Point getCodecMaxSize(MediaCodecInfo paramMediaCodecInfo, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    int i = height;
    int j = width;
    int m = 0;
    if (i > j) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      j = height;
    } else {
      j = width;
    }
    int k;
    if (i != 0) {
      k = width;
    } else {
      k = height;
    }
    float f1 = k / j;
    int[] arrayOfInt = STANDARD_LONG_EDGE_VIDEO_PX;
    int i3 = arrayOfInt.length;
    while (m < i3)
    {
      int n = arrayOfInt[m];
      int i1 = (int)(n * f1);
      if (n <= j) {
        break;
      }
      if (i1 <= k) {
        return null;
      }
      if (Util.SDK_INT >= 21)
      {
        int i2;
        if (i != 0) {
          i2 = i1;
        } else {
          i2 = n;
        }
        if (i == 0) {
          n = i1;
        }
        Point localPoint = paramMediaCodecInfo.alignVideoSizeV21(i2, n);
        float f2 = frameRate;
        if (paramMediaCodecInfo.isVideoSizeAndRateSupportedV21(x, y, f2)) {
          return localPoint;
        }
      }
      else
      {
        n = Util.ceilDivide(n, 16) * 16;
        i1 = Util.ceilDivide(i1, 16) * 16;
        if (n * i1 <= MediaCodecUtil.maxH264DecodableFrameSize())
        {
          if (i != 0) {
            j = i1;
          } else {
            j = n;
          }
          if (i != 0) {
            i1 = n;
          }
          return new Point(j, i1);
        }
      }
      m += 1;
    }
    return null;
  }
  
  private static int getMaxInputSize(MediaCodecInfo paramMediaCodecInfo, Format paramFormat)
  {
    if (maxInputSize != -1)
    {
      int k = initializationData.size();
      int i = 0;
      int j = 0;
      while (i < k)
      {
        j += ((byte[])initializationData.get(i)).length;
        i += 1;
      }
      return maxInputSize + j;
    }
    return getCodecMaxInputSize(paramMediaCodecInfo, sampleMimeType, width, height);
  }
  
  private static boolean isBufferLate(long paramLong)
  {
    return paramLong < -30000L;
  }
  
  private static boolean isBufferVeryLate(long paramLong)
  {
    return paramLong < -500000L;
  }
  
  private void maybeNotifyDroppedFrames()
  {
    if (droppedFrames > 0)
    {
      long l1 = SystemClock.elapsedRealtime();
      long l2 = droppedFrameAccumulationStartTimeMs;
      eventDispatcher.droppedFrames(droppedFrames, l1 - l2);
      droppedFrames = 0;
      droppedFrameAccumulationStartTimeMs = l1;
    }
  }
  
  private void maybeNotifyVideoSizeChanged()
  {
    if (((currentWidth != -1) || (currentHeight != -1)) && ((reportedWidth != currentWidth) || (reportedHeight != currentHeight) || (reportedUnappliedRotationDegrees != currentUnappliedRotationDegrees) || (reportedPixelWidthHeightRatio != currentPixelWidthHeightRatio)))
    {
      eventDispatcher.videoSizeChanged(currentWidth, currentHeight, currentUnappliedRotationDegrees, currentPixelWidthHeightRatio);
      reportedWidth = currentWidth;
      reportedHeight = currentHeight;
      reportedUnappliedRotationDegrees = currentUnappliedRotationDegrees;
      reportedPixelWidthHeightRatio = currentPixelWidthHeightRatio;
    }
  }
  
  private void maybeRenotifyRenderedFirstFrame()
  {
    if (renderedFirstFrame) {
      eventDispatcher.renderedFirstFrame(surface);
    }
  }
  
  private void maybeRenotifyVideoSizeChanged()
  {
    if ((reportedWidth != -1) || (reportedHeight != -1)) {
      eventDispatcher.videoSizeChanged(reportedWidth, reportedHeight, reportedUnappliedRotationDegrees, reportedPixelWidthHeightRatio);
    }
  }
  
  private void notifyFrameMetadataListener(long paramLong1, long paramLong2, Format paramFormat)
  {
    if (frameMetadataListener != null) {
      frameMetadataListener.onVideoFrameAboutToBeRendered(paramLong1, paramLong2, paramFormat);
    }
  }
  
  private void processOutputFormat(MediaCodec paramMediaCodec, int paramInt1, int paramInt2)
  {
    currentWidth = paramInt1;
    currentHeight = paramInt2;
    currentPixelWidthHeightRatio = pendingPixelWidthHeightRatio;
    if (Util.SDK_INT >= 21)
    {
      if ((pendingRotationDegrees == 90) || (pendingRotationDegrees == 270))
      {
        paramInt1 = currentWidth;
        currentWidth = currentHeight;
        currentHeight = paramInt1;
        currentPixelWidthHeightRatio = (1.0F / currentPixelWidthHeightRatio);
      }
    }
    else {
      currentUnappliedRotationDegrees = pendingRotationDegrees;
    }
    paramMediaCodec.setVideoScalingMode(scalingMode);
  }
  
  private void setJoiningDeadlineMs()
  {
    long l;
    if (allowedJoiningTimeMs > 0L) {
      l = SystemClock.elapsedRealtime() + allowedJoiningTimeMs;
    } else {
      l = -9223372036854775807L;
    }
    joiningDeadlineMs = l;
  }
  
  private static void setOutputSurfaceV23(MediaCodec paramMediaCodec, Surface paramSurface)
  {
    paramMediaCodec.setOutputSurface(paramSurface);
  }
  
  private void setSurface(Surface paramSurface)
    throws ExoPlaybackException
  {
    Surface localSurface = paramSurface;
    if (paramSurface == null) {
      if (dummySurface != null)
      {
        localSurface = dummySurface;
      }
      else
      {
        MediaCodecInfo localMediaCodecInfo = getCodecInfo();
        localSurface = paramSurface;
        if (localMediaCodecInfo != null)
        {
          localSurface = paramSurface;
          if (shouldUseDummySurface(localMediaCodecInfo))
          {
            dummySurface = DummySurface.newInstanceV17(context, secure);
            localSurface = dummySurface;
          }
        }
      }
    }
    if (surface != localSurface)
    {
      surface = localSurface;
      int i = getState();
      if ((i == 1) || (i == 2))
      {
        paramSurface = getCodec();
        if ((Util.SDK_INT >= 23) && (paramSurface != null) && (localSurface != null) && (!codecNeedsSetOutputSurfaceWorkaround))
        {
          setOutputSurfaceV23(paramSurface, localSurface);
        }
        else
        {
          releaseCodec();
          maybeInitCodec();
        }
      }
      if ((localSurface != null) && (localSurface != dummySurface))
      {
        maybeRenotifyVideoSizeChanged();
        clearRenderedFirstFrame();
        if (i == 2) {
          setJoiningDeadlineMs();
        }
      }
      else
      {
        clearReportedVideoSize();
        clearRenderedFirstFrame();
      }
    }
    else if ((localSurface != null) && (localSurface != dummySurface))
    {
      maybeRenotifyVideoSizeChanged();
      maybeRenotifyRenderedFirstFrame();
    }
  }
  
  private boolean shouldUseDummySurface(MediaCodecInfo paramMediaCodecInfo)
  {
    return (Util.SDK_INT >= 23) && (!tunneling) && (!codecNeedsSetOutputSurfaceWorkaround(name)) && ((!secure) || (DummySurface.isSecureSupported(context)));
  }
  
  protected int canKeepCodec(MediaCodec paramMediaCodec, MediaCodecInfo paramMediaCodecInfo, Format paramFormat1, Format paramFormat2)
  {
    if ((paramMediaCodecInfo.isSeamlessAdaptationSupported(paramFormat1, paramFormat2, true)) && (width <= codecMaxValues.width) && (height <= codecMaxValues.height) && (getMaxInputSize(paramMediaCodecInfo, paramFormat2) <= codecMaxValues.inputSize))
    {
      if (paramFormat1.initializationDataEquals(paramFormat2)) {
        return 1;
      }
      return 3;
    }
    return 0;
  }
  
  protected boolean codecNeedsSetOutputSurfaceWorkaround(String paramString)
  {
    int k = Util.SDK_INT;
    int i = 27;
    int j = 0;
    if (k < 27) {
      if (paramString.startsWith("OMX.google")) {
        return false;
      }
    }
    for (;;)
    {
      try
      {
        if (!evaluatedDeviceNeedsSetOutputSurfaceWorkaround) {
          paramString = Util.DEVICE;
        }
        switch (paramString.hashCode())
        {
        case 2048319463: 
          if (!paramString.equals("HWVNS-H")) {
            break;
          }
          i = 51;
          break;
        case 2047252157: 
          if (!paramString.equals("ELUGA_Prim")) {
            break;
          }
          i = 26;
          break;
        case 2047190025: 
          if (!paramString.equals("ELUGA_Note")) {
            break;
          }
          i = 25;
          break;
        case 2033393791: 
          if (!paramString.equals("ASUS_X00AD_2")) {
            break;
          }
          i = 11;
          break;
        case 2030379515: 
          if (!paramString.equals("HWCAM-H")) {
            break;
          }
          i = 50;
          break;
        case 2029784656: 
          if (!paramString.equals("HWBLN-H")) {
            break;
          }
          i = 49;
          break;
        case 1977196784: 
          if (!paramString.equals("Infinix-X572")) {
            break;
          }
          i = 54;
          break;
        case 1906253259: 
          if (!paramString.equals("PB2-670M")) {
            break;
          }
          i = 82;
          break;
        case 1865889110: 
          if (!paramString.equals("santoni")) {
            break;
          }
          i = 98;
          break;
        case 1709443163: 
          if (!paramString.equals("iball8735_9806")) {
            break;
          }
          i = 53;
          break;
        case 1691543273: 
          if (!paramString.equals("CPH1609")) {
            break;
          }
          i = 18;
          break;
        case 1522194893: 
          if (!paramString.equals("woods_f")) {
            break;
          }
          i = 114;
          break;
        case 1349174697: 
          if (!paramString.equals("htc_e56ml_dtul")) {
            break;
          }
          i = 47;
          break;
        case 1306947716: 
          if (!paramString.equals("EverStar_S")) {
            break;
          }
          i = 28;
          break;
        case 1280332038: 
          if (!paramString.equals("hwALE-H")) {
            break;
          }
          i = 48;
          break;
        case 1176899427: 
          if (!paramString.equals("itel_S41")) {
            break;
          }
          i = 56;
          break;
        case 1150207623: 
          if (!paramString.equals("LS-5017")) {
            break;
          }
          i = 62;
          break;
        case 1060579533: 
          if (!paramString.equals("panell_d")) {
            break;
          }
          i = 78;
          break;
        case 958008161: 
          if (!paramString.equals("j2xlteins")) {
            break;
          }
          i = 57;
          break;
        case 917340916: 
          if (!paramString.equals("A7000plus")) {
            break;
          }
          i = 7;
          break;
        case 835649806: 
          if (!paramString.equals("manning")) {
            break;
          }
          i = 64;
          break;
        case 794040393: 
          if (!paramString.equals("GIONEE_WBL7519")) {
            break;
          }
          i = 45;
          break;
        case 794038622: 
          if (!paramString.equals("GIONEE_WBL7365")) {
            break;
          }
          i = 44;
          break;
        case 793982701: 
          if (!paramString.equals("GIONEE_WBL5708")) {
            break;
          }
          i = 43;
          break;
        case 507412548: 
          if (!paramString.equals("QM16XE_U")) {
            break;
          }
          i = 96;
          break;
        case 407160593: 
          if (!paramString.equals("Pixi5-10_4G")) {
            break;
          }
          i = 88;
          break;
        case 316246818: 
          if (!paramString.equals("TB3-850M")) {
            break;
          }
          i = 106;
          break;
        case 316246811: 
          if (!paramString.equals("TB3-850F")) {
            break;
          }
          i = 105;
          break;
        case 316215116: 
          if (!paramString.equals("TB3-730X")) {
            break;
          }
          i = 104;
          break;
        case 316215098: 
          if (!paramString.equals("TB3-730F")) {
            break;
          }
          i = 103;
          break;
        case 308517133: 
          if (!paramString.equals("A7020a48")) {
            break;
          }
          i = 9;
          break;
        case 307593612: 
          if (!paramString.equals("A7010a48")) {
            break;
          }
          i = 8;
          break;
        case 287431619: 
          if (!paramString.equals("griffin")) {
            break;
          }
          i = 46;
          break;
        case 245388979: 
          if (!paramString.equals("marino_f")) {
            break;
          }
          i = 65;
          break;
        case 182191441: 
          if (!paramString.equals("CPY83_I00")) {
            break;
          }
          i = 19;
          break;
        case 165221241: 
          if (!paramString.equals("A2016a40")) {
            break;
          }
          i = 5;
          break;
        case 102844228: 
          if (!paramString.equals("le_x6")) {
            break;
          }
          i = 61;
          break;
        case 98715550: 
          if (!paramString.equals("i9031")) {
            break;
          }
          i = 52;
          break;
        case 82882791: 
          if (!paramString.equals("X3_HK")) {
            break;
          }
          i = 116;
          break;
        case 80963634: 
          if (!paramString.equals("V23GB")) {
            break;
          }
          i = 109;
          break;
        case 76404911: 
          if (!paramString.equals("Q4310")) {
            break;
          }
          i = 94;
          break;
        case 76404105: 
          if (!paramString.equals("Q4260")) {
            break;
          }
          i = 92;
          break;
        case 76402249: 
          if (!paramString.equals("PRO7S")) {
            break;
          }
          i = 90;
          break;
        case 66216390: 
          if (!paramString.equals("F3311")) {
            break;
          }
          i = 35;
          break;
        case 66215433: 
          if (!paramString.equals("F3215")) {
            break;
          }
          i = 34;
          break;
        case 66215431: 
          if (!paramString.equals("F3213")) {
            break;
          }
          i = 33;
          break;
        case 66215429: 
          if (!paramString.equals("F3211")) {
            break;
          }
          i = 32;
          break;
        case 66214473: 
          if (!paramString.equals("F3116")) {
            break;
          }
          i = 31;
          break;
        case 66214470: 
          if (!paramString.equals("F3113")) {
            break;
          }
          i = 30;
          break;
        case 66214468: 
          if (!paramString.equals("F3111")) {
            break;
          }
          i = 29;
          break;
        case 65355429: 
          if (!paramString.equals("E5643")) {
            break;
          }
          i = 23;
          break;
        case 61542055: 
          if (!paramString.equals("A1601")) {
            break;
          }
          i = 4;
          break;
        case 55178625: 
          if (!paramString.equals("Aura_Note_2")) {
            break;
          }
          i = 12;
          break;
        case 41325051: 
          if (!paramString.equals("MEIZU_M5")) {
            break;
          }
          i = 66;
          break;
        case 3386211: 
          if (!paramString.equals("p212")) {
            break;
          }
          i = 75;
          break;
        case 3351335: 
          if (!paramString.equals("mido")) {
            break;
          }
          i = 68;
          break;
        case 3284551: 
          if (!paramString.equals("kate")) {
            break;
          }
          i = 60;
          break;
        case 2689555: 
          if (!paramString.equals("XE2X")) {
            break;
          }
          i = 117;
          break;
        case 2464648: 
          if (!paramString.equals("Q427")) {
            break;
          }
          i = 93;
          break;
        case 2463773: 
          if (!paramString.equals("Q350")) {
            break;
          }
          i = 91;
          break;
        case 2436959: 
          if (!paramString.equals("P681")) {
            break;
          }
          i = 76;
          break;
        case 1514185: 
          if (!paramString.equals("1714")) {
            break;
          }
          i = 2;
          break;
        case 1514184: 
          if (!paramString.equals("1713")) {
            break;
          }
          i = 1;
          break;
        case 1513190: 
          if (!paramString.equals("1601")) {
            break;
          }
          i = 0;
          break;
        case 101481: 
          if (!paramString.equals("flo")) {
            break;
          }
          i = 36;
          break;
        case 99329: 
          if (!paramString.equals("deb")) {
            break;
          }
          i = 22;
          break;
        case 98848: 
          if (!paramString.equals("cv3")) {
            break;
          }
          i = 21;
          break;
        case 98846: 
          if (!paramString.equals("cv1")) {
            break;
          }
          i = 20;
          break;
        case 88274: 
          if (!paramString.equals("Z80")) {
            break;
          }
          i = 120;
          break;
        case 80618: 
          if (!paramString.equals("QX1")) {
            break;
          }
          i = 97;
          break;
        case 79305: 
          if (!paramString.equals("PLE")) {
            break;
          }
          i = 89;
          break;
        case 78669: 
          if (!paramString.equals("P85")) {
            break;
          }
          i = 77;
          break;
        case 76779: 
          if (!paramString.equals("MX6")) {
            break;
          }
          i = 69;
          break;
        case 75739: 
          if (!paramString.equals("M5c")) {
            break;
          }
          i = 63;
          break;
        case 73405: 
          if (!paramString.equals("JGZ")) {
            break;
          }
          i = 58;
          break;
        case 3483: 
          if (!paramString.equals("mh")) {
            break;
          }
          i = 67;
          break;
        case 2719: 
          if (!paramString.equals("V5")) {
            break;
          }
          i = 110;
          break;
        case 2715: 
          if (!paramString.equals("V1")) {
            break;
          }
          i = 108;
          break;
        case 2564: 
          if (!paramString.equals("Q5")) {
            break;
          }
          i = 95;
          break;
        case 2126: 
          if (!paramString.equals("C1")) {
            break;
          }
          i = 15;
          break;
        case -56598463: 
          if (!paramString.equals("woods_fn")) {
            break;
          }
          i = 115;
          break;
        case -173639913: 
          if (!paramString.equals("ELUGA_A3_Pro")) {
            break;
          }
          i = 24;
          break;
        case -277133239: 
          if (!paramString.equals("Z12_PRO")) {
            break;
          }
          i = 119;
          break;
        case -282781963: 
          if (!paramString.equals("BLACK-1X")) {
            break;
          }
          i = 13;
          break;
        case -290434366: 
          if (!paramString.equals("taido_row")) {
            break;
          }
          i = 102;
          break;
        case -430914369: 
          if (!paramString.equals("Pixi4-7_3G")) {
            break;
          }
          i = 87;
          break;
        case -521118391: 
          if (!paramString.equals("GIONEE_GBL7360")) {
            break;
          }
          i = 39;
          break;
        case -575125681: 
          if (!paramString.equals("GiONEE_CBL7513")) {
            break;
          }
          i = 37;
          break;
        case -782144577: 
          if (!paramString.equals("OnePlus5T")) {
            break;
          }
          i = 74;
          break;
        case -788334647: 
          if (!paramString.equals("whyred")) {
            break;
          }
          i = 113;
          break;
        case -794946968: 
          if (!paramString.equals("watson")) {
            break;
          }
          i = 112;
          break;
        case -797483286: 
          if (!paramString.equals("SVP-DTV15")) {
            break;
          }
          i = 100;
          break;
        case -821392978: 
          if (!paramString.equals("A7000-a")) {
            break;
          }
          i = 6;
          break;
        case -842500323: 
          if (!paramString.equals("nicklaus_f")) {
            break;
          }
          i = 71;
          break;
        case -879245230: 
          if (!paramString.equals("tcl_eu")) {
            break;
          }
          i = 107;
          break;
        case -958336948: 
          if (!paramString.equals("ELUGA_Ray_X")) {
            break;
          }
          break;
        case -965403638: 
          if (!paramString.equals("s905x018")) {
            break;
          }
          i = 101;
          break;
        case -993250464: 
          if (!paramString.equals("A10-70F")) {
            break;
          }
          i = 3;
          break;
        case -1052835013: 
          if (!paramString.equals("namath")) {
            break;
          }
          i = 70;
          break;
        case -1139198265: 
          if (!paramString.equals("Slate_Pro")) {
            break;
          }
          i = 99;
          break;
        case -1180384755: 
          if (!paramString.equals("iris60")) {
            break;
          }
          i = 55;
          break;
        case -1217592143: 
          if (!paramString.equals("BRAVIA_ATV2")) {
            break;
          }
          i = 14;
          break;
        case -1320080169: 
          if (!paramString.equals("GiONEE_GBL7319")) {
            break;
          }
          i = 38;
          break;
        case -1481772729: 
          if (!paramString.equals("panell_dt")) {
            break;
          }
          i = 81;
          break;
        case -1481772730: 
          if (!paramString.equals("panell_ds")) {
            break;
          }
          i = 80;
          break;
        case -1481772737: 
          if (!paramString.equals("panell_dl")) {
            break;
          }
          i = 79;
          break;
        case -1554255044: 
          if (!paramString.equals("vernee_M5")) {
            break;
          }
          i = 111;
          break;
        case -1615810839: 
          if (!paramString.equals("Phantom6")) {
            break;
          }
          i = 86;
          break;
        case -1680025915: 
          if (!paramString.equals("ComioS1")) {
            break;
          }
          i = 16;
          break;
        case -1696512866: 
          if (!paramString.equals("XT1663")) {
            break;
          }
          i = 118;
          break;
        case -1931988508: 
          if (!paramString.equals("AquaPowerM")) {
            break;
          }
          i = 10;
          break;
        case -1936688065: 
          if (!paramString.equals("PGN611")) {
            break;
          }
          i = 85;
          break;
        case -1936688066: 
          if (!paramString.equals("PGN610")) {
            break;
          }
          i = 84;
          break;
        case -1936688988: 
          if (!paramString.equals("PGN528")) {
            break;
          }
          i = 83;
          break;
        case -1978990237: 
          if (!paramString.equals("NX573J")) {
            break;
          }
          i = 73;
          break;
        case -1978993182: 
          if (!paramString.equals("NX541J")) {
            break;
          }
          i = 72;
          break;
        case -2022874474: 
          if (!paramString.equals("CP8676_I02")) {
            break;
          }
          i = 17;
          break;
        case -2097309513: 
          if (!paramString.equals("K50a40")) {
            break;
          }
          i = 59;
          break;
        case -2144781160: 
          if (!paramString.equals("GIONEE_SWW1631")) {
            break;
          }
          i = 42;
          break;
        case -2144781185: 
          if (!paramString.equals("GIONEE_SWW1627")) {
            break;
          }
          i = 41;
          break;
        case -2144781245: 
          if (!paramString.equals("GIONEE_SWW1609")) {
            break;
          }
          i = 40;
          break label3036;
          deviceNeedsSetOutputSurfaceWorkaround = true;
          paramString = Util.MODEL;
          i = paramString.hashCode();
          if (i != 2006354)
          {
            if ((i != 2006367) || (!paramString.equals("AFTN"))) {
              break label4019;
            }
            i = 1;
            break label4021;
          }
          if (!paramString.equals("AFTA")) {
            break label4019;
          }
          i = j;
          break label4021;
          deviceNeedsSetOutputSurfaceWorkaround = true;
          evaluatedDeviceNeedsSetOutputSurfaceWorkaround = true;
          return deviceNeedsSetOutputSurfaceWorkaround;
        }
      }
      catch (Throwable paramString)
      {
        throw paramString;
      }
      return false;
      i = -1;
      label3036:
      switch (i)
      {
      }
      continue;
      label4019:
      i = -1;
      label4021:
      switch (i)
      {
      }
    }
  }
  
  protected void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto, float paramFloat)
    throws MediaCodecUtil.DecoderQueryException
  {
    codecMaxValues = getCodecMaxValues(paramMediaCodecInfo, paramFormat, getStreamFormats());
    paramFormat = getMediaFormat(paramFormat, codecMaxValues, paramFloat, deviceNeedsAutoFrcWorkaround, tunnelingAudioSessionId);
    if (surface == null)
    {
      Assertions.checkState(shouldUseDummySurface(paramMediaCodecInfo));
      if (dummySurface == null) {
        dummySurface = DummySurface.newInstanceV17(context, secure);
      }
      surface = dummySurface;
    }
    paramMediaCodec.configure(paramFormat, surface, paramMediaCrypto, 0);
    if ((Util.SDK_INT >= 23) && (tunneling)) {
      tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(paramMediaCodec, null);
    }
  }
  
  protected void dropOutputBuffer(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    TraceUtil.beginSection("dropVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    updateDroppedBufferCounters(1);
  }
  
  protected void flushCodec()
    throws ExoPlaybackException
  {
    super.flushCodec();
    buffersInCodecCount = 0;
  }
  
  protected CodecMaxValues getCodecMaxValues(MediaCodecInfo paramMediaCodecInfo, Format paramFormat, Format[] paramArrayOfFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    int i1 = width;
    int i2 = height;
    int i = getMaxInputSize(paramMediaCodecInfo, paramFormat);
    int j = i;
    if (paramArrayOfFormat.length == 1)
    {
      k = j;
      if (i != -1)
      {
        m = getCodecMaxInputSize(paramMediaCodecInfo, sampleMimeType, width, height);
        k = j;
        if (m != -1) {
          k = Math.min((int)(i * 1.5F), m);
        }
      }
      return new CodecMaxValues(i1, i2, k);
    }
    int i5 = paramArrayOfFormat.length;
    int n = 0;
    int m = 0;
    j = i2;
    int k = i1;
    while (m < i5)
    {
      Format localFormat = paramArrayOfFormat[m];
      int i4 = k;
      int i3 = j;
      i2 = i;
      i1 = n;
      if (paramMediaCodecInfo.isSeamlessAdaptationSupported(paramFormat, localFormat, false))
      {
        if ((width != -1) && (height != -1)) {
          i1 = 0;
        } else {
          i1 = 1;
        }
        i1 = n | i1;
        i4 = Math.max(k, width);
        i3 = Math.max(j, height);
        i2 = Math.max(i, getMaxInputSize(paramMediaCodecInfo, localFormat));
      }
      m += 1;
      k = i4;
      j = i3;
      i = i2;
      n = i1;
    }
    i2 = k;
    i1 = j;
    m = i;
    if (n != 0)
    {
      paramArrayOfFormat = new StringBuilder();
      paramArrayOfFormat.append("Resolutions unknown. Codec max resolution: ");
      paramArrayOfFormat.append(k);
      paramArrayOfFormat.append("x");
      paramArrayOfFormat.append(j);
      Log.w("MediaCodecVideoRenderer", paramArrayOfFormat.toString());
      paramArrayOfFormat = getCodecMaxSize(paramMediaCodecInfo, paramFormat);
      i2 = k;
      i1 = j;
      m = i;
      if (paramArrayOfFormat != null)
      {
        m = Math.max(k, x);
        k = m;
        j = Math.max(j, y);
        i1 = j;
        i = Math.max(i, getCodecMaxInputSize(paramMediaCodecInfo, sampleMimeType, m, j));
        paramMediaCodecInfo = new StringBuilder();
        paramMediaCodecInfo.append("Codec max resolution adjusted to: ");
        paramMediaCodecInfo.append(m);
        paramMediaCodecInfo.append("x");
        paramMediaCodecInfo.append(j);
        Log.w("MediaCodecVideoRenderer", paramMediaCodecInfo.toString());
        m = i;
        i2 = k;
      }
    }
    return new CodecMaxValues(i2, i1, m);
  }
  
  protected boolean getCodecNeedsEosPropagation()
  {
    return tunneling;
  }
  
  protected float getCodecOperatingRate(float paramFloat, Format paramFormat, Format[] paramArrayOfFormat)
  {
    int j = paramArrayOfFormat.length;
    int i = 0;
    float f2;
    for (float f1 = -1.0F; i < j; f1 = f2)
    {
      float f3 = frameRate;
      f2 = f1;
      if (f3 != -1.0F) {
        f2 = Math.max(f1, f3);
      }
      i += 1;
    }
    if (f1 == -1.0F) {
      return -1.0F;
    }
    return f1 * paramFloat;
  }
  
  protected MediaFormat getMediaFormat(Format paramFormat, CodecMaxValues paramCodecMaxValues, float paramFloat, boolean paramBoolean, int paramInt)
  {
    MediaFormat localMediaFormat = new MediaFormat();
    localMediaFormat.setString("mime", sampleMimeType);
    localMediaFormat.setInteger("width", width);
    localMediaFormat.setInteger("height", height);
    MediaFormatUtil.setCsdBuffers(localMediaFormat, initializationData);
    MediaFormatUtil.maybeSetFloat(localMediaFormat, "frame-rate", frameRate);
    MediaFormatUtil.maybeSetInteger(localMediaFormat, "rotation-degrees", rotationDegrees);
    MediaFormatUtil.maybeSetColorInfo(localMediaFormat, colorInfo);
    localMediaFormat.setInteger("max-width", width);
    localMediaFormat.setInteger("max-height", height);
    MediaFormatUtil.maybeSetInteger(localMediaFormat, "max-input-size", inputSize);
    if (Util.SDK_INT >= 23)
    {
      localMediaFormat.setInteger("priority", 0);
      if (paramFloat != -1.0F) {
        localMediaFormat.setFloat("operating-rate", paramFloat);
      }
    }
    if (paramBoolean) {
      localMediaFormat.setInteger("auto-frc", 0);
    }
    if (paramInt != 0) {
      configureTunnelingV21(localMediaFormat, paramInt);
    }
    return localMediaFormat;
  }
  
  protected long getOutputStreamOffsetUs()
  {
    return outputStreamOffsetUs;
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (paramInt == 1)
    {
      setSurface((Surface)paramObject);
      return;
    }
    if (paramInt == 4)
    {
      scalingMode = ((Integer)paramObject).intValue();
      paramObject = getCodec();
      if (paramObject != null) {
        paramObject.setVideoScalingMode(scalingMode);
      }
    }
    else
    {
      if (paramInt == 6)
      {
        frameMetadataListener = ((VideoFrameMetadataListener)paramObject);
        return;
      }
      super.handleMessage(paramInt, paramObject);
    }
  }
  
  public boolean isReady()
  {
    if ((super.isReady()) && ((renderedFirstFrame) || ((dummySurface != null) && (surface == dummySurface)) || (getCodec() == null) || (tunneling)))
    {
      joiningDeadlineMs = -9223372036854775807L;
      return true;
    }
    if (joiningDeadlineMs == -9223372036854775807L) {
      return false;
    }
    if (SystemClock.elapsedRealtime() < joiningDeadlineMs) {
      return true;
    }
    joiningDeadlineMs = -9223372036854775807L;
    return false;
  }
  
  protected boolean maybeDropBuffersToKeyframe(MediaCodec paramMediaCodec, int paramInt, long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    paramInt = skipSource(paramLong2);
    if (paramInt == 0) {
      return false;
    }
    paramMediaCodec = decoderCounters;
    droppedToKeyframeCount += 1;
    updateDroppedBufferCounters(buffersInCodecCount + paramInt);
    flushCodec();
    return true;
  }
  
  void maybeNotifyRenderedFirstFrame()
  {
    if (!renderedFirstFrame)
    {
      renderedFirstFrame = true;
      eventDispatcher.renderedFirstFrame(surface);
    }
  }
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2)
  {
    eventDispatcher.decoderInitialized(paramString, paramLong1, paramLong2);
    codecNeedsSetOutputSurfaceWorkaround = codecNeedsSetOutputSurfaceWorkaround(paramString);
  }
  
  protected void onDisabled()
  {
    currentWidth = -1;
    currentHeight = -1;
    currentPixelWidthHeightRatio = -1.0F;
    pendingPixelWidthHeightRatio = -1.0F;
    outputStreamOffsetUs = -9223372036854775807L;
    lastInputTimeUs = -9223372036854775807L;
    pendingOutputStreamOffsetCount = 0;
    clearReportedVideoSize();
    clearRenderedFirstFrame();
    frameReleaseTimeHelper.disable();
    tunnelingOnFrameRenderedListener = null;
    tunneling = false;
    try
    {
      super.onDisabled();
      decoderCounters.ensureUpdated();
      eventDispatcher.disabled(decoderCounters);
      return;
    }
    catch (Throwable localThrowable)
    {
      decoderCounters.ensureUpdated();
      eventDispatcher.disabled(decoderCounters);
      throw localThrowable;
    }
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramBoolean);
    tunnelingAudioSessionId = getConfigurationtunnelingAudioSessionId;
    if (tunnelingAudioSessionId != 0) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    tunneling = paramBoolean;
    eventDispatcher.enabled(decoderCounters);
    frameReleaseTimeHelper.enable();
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    super.onInputFormatChanged(paramFormat);
    eventDispatcher.inputFormatChanged(paramFormat);
    pendingPixelWidthHeightRatio = pixelWidthHeightRatio;
    pendingRotationDegrees = rotationDegrees;
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
  {
    int j;
    if ((paramMediaFormat.containsKey("crop-right")) && (paramMediaFormat.containsKey("crop-left")) && (paramMediaFormat.containsKey("crop-bottom")) && (paramMediaFormat.containsKey("crop-top"))) {
      j = 1;
    } else {
      j = 0;
    }
    int i;
    if (j != 0) {
      i = paramMediaFormat.getInteger("crop-right") - paramMediaFormat.getInteger("crop-left") + 1;
    } else {
      i = paramMediaFormat.getInteger("width");
    }
    if (j != 0) {
      j = paramMediaFormat.getInteger("crop-bottom") - paramMediaFormat.getInteger("crop-top") + 1;
    } else {
      j = paramMediaFormat.getInteger("height");
    }
    processOutputFormat(paramMediaCodec, i, j);
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onPositionReset(paramLong, paramBoolean);
    clearRenderedFirstFrame();
    initialPositionUs = -9223372036854775807L;
    consecutiveDroppedFrameCount = 0;
    lastInputTimeUs = -9223372036854775807L;
    if (pendingOutputStreamOffsetCount != 0)
    {
      outputStreamOffsetUs = pendingOutputStreamOffsetsUs[(pendingOutputStreamOffsetCount - 1)];
      pendingOutputStreamOffsetCount = 0;
    }
    if (paramBoolean)
    {
      setJoiningDeadlineMs();
      return;
    }
    joiningDeadlineMs = -9223372036854775807L;
  }
  
  protected void onProcessedOutputBuffer(long paramLong)
  {
    buffersInCodecCount -= 1;
    while ((pendingOutputStreamOffsetCount != 0) && (paramLong >= pendingOutputStreamSwitchTimesUs[0]))
    {
      outputStreamOffsetUs = pendingOutputStreamOffsetsUs[0];
      pendingOutputStreamOffsetCount -= 1;
      System.arraycopy(pendingOutputStreamOffsetsUs, 1, pendingOutputStreamOffsetsUs, 0, pendingOutputStreamOffsetCount);
      System.arraycopy(pendingOutputStreamSwitchTimesUs, 1, pendingOutputStreamSwitchTimesUs, 0, pendingOutputStreamOffsetCount);
    }
  }
  
  protected void onProcessedTunneledBuffer(long paramLong)
  {
    Format localFormat = updateOutputFormatForTime(paramLong);
    if (localFormat != null) {
      processOutputFormat(getCodec(), width, height);
    }
    maybeNotifyVideoSizeChanged();
    maybeNotifyRenderedFirstFrame();
    onProcessedOutputBuffer(paramLong);
  }
  
  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer)
  {
    buffersInCodecCount += 1;
    lastInputTimeUs = Math.max(timeUs, lastInputTimeUs);
    if ((Util.SDK_INT < 23) && (tunneling)) {
      onProcessedTunneledBuffer(timeUs);
    }
  }
  
  protected void onStarted()
  {
    super.onStarted();
    droppedFrames = 0;
    droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
    lastRenderTimeUs = (SystemClock.elapsedRealtime() * 1000L);
  }
  
  protected void onStopped()
  {
    joiningDeadlineMs = -9223372036854775807L;
    maybeNotifyDroppedFrames();
    super.onStopped();
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat, long paramLong)
    throws ExoPlaybackException
  {
    if (outputStreamOffsetUs == -9223372036854775807L)
    {
      outputStreamOffsetUs = paramLong;
    }
    else
    {
      if (pendingOutputStreamOffsetCount == pendingOutputStreamOffsetsUs.length)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Too many stream changes, so dropping offset: ");
        localStringBuilder.append(pendingOutputStreamOffsetsUs[(pendingOutputStreamOffsetCount - 1)]);
        Log.w("MediaCodecVideoRenderer", localStringBuilder.toString());
      }
      else
      {
        pendingOutputStreamOffsetCount += 1;
      }
      pendingOutputStreamOffsetsUs[(pendingOutputStreamOffsetCount - 1)] = paramLong;
      pendingOutputStreamSwitchTimesUs[(pendingOutputStreamOffsetCount - 1)] = lastInputTimeUs;
    }
    super.onStreamChanged(paramArrayOfFormat, paramLong);
  }
  
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean, Format paramFormat)
    throws ExoPlaybackException
  {
    if (initialPositionUs == -9223372036854775807L) {
      initialPositionUs = paramLong1;
    }
    long l1 = paramLong3 - outputStreamOffsetUs;
    if (paramBoolean)
    {
      skipOutputBuffer(paramMediaCodec, paramInt1, l1);
      return true;
    }
    long l3 = paramLong3 - paramLong1;
    if (surface == dummySurface)
    {
      if (isBufferLate(l3))
      {
        skipOutputBuffer(paramMediaCodec, paramInt1, l1);
        return true;
      }
      return false;
    }
    long l4 = SystemClock.elapsedRealtime() * 1000L;
    if (getState() == 2) {
      paramInt2 = 1;
    } else {
      paramInt2 = 0;
    }
    if ((renderedFirstFrame) && ((paramInt2 == 0) || (!shouldForceRenderOutputBuffer(l3, l4 - lastRenderTimeUs)))) {
      if (paramInt2 != 0)
      {
        if (paramLong1 == initialPositionUs) {
          return false;
        }
        long l2 = System.nanoTime();
        paramLong3 = frameReleaseTimeHelper.adjustReleaseTime(paramLong3, (l3 - (l4 - paramLong2)) * 1000L + l2);
        l2 = (paramLong3 - l2) / 1000L;
        if ((shouldDropBuffersToKeyframe(l2, paramLong2)) && (maybeDropBuffersToKeyframe(paramMediaCodec, paramInt1, l1, paramLong1))) {
          return false;
        }
        if (shouldDropOutputBuffer(l2, paramLong2))
        {
          dropOutputBuffer(paramMediaCodec, paramInt1, l1);
          return true;
        }
        if (Util.SDK_INT >= 21)
        {
          if (l2 < 50000L)
          {
            notifyFrameMetadataListener(l1, paramLong3, paramFormat);
            renderOutputBufferV21(paramMediaCodec, paramInt1, l1, paramLong3);
            return true;
          }
        }
        else
        {
          if (l2 >= 30000L) {
            break label404;
          }
          if (l2 > 11000L) {
            paramLong1 = (l2 - 10000L) / 1000L;
          }
        }
      }
    }
    try
    {
      Thread.sleep(paramLong1);
    }
    catch (InterruptedException paramMediaCodec)
    {
      for (;;) {}
    }
    Thread.currentThread().interrupt();
    return false;
    notifyFrameMetadataListener(l1, paramLong3, paramFormat);
    renderOutputBuffer(paramMediaCodec, paramInt1, l1);
    return true;
    return false;
    paramLong1 = System.nanoTime();
    notifyFrameMetadataListener(l1, paramLong1, paramFormat);
    if (Util.SDK_INT >= 21) {
      renderOutputBufferV21(paramMediaCodec, paramInt1, l1, paramLong1);
    }
    for (;;)
    {
      return true;
      renderOutputBuffer(paramMediaCodec, paramInt1, l1);
    }
    label404:
    return false;
  }
  
  protected void releaseCodec()
  {
    try
    {
      super.releaseCodec();
      buffersInCodecCount = 0;
      if (dummySurface != null)
      {
        if (surface == dummySurface) {
          surface = null;
        }
        dummySurface.release();
        dummySurface = null;
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      buffersInCodecCount = 0;
      if (dummySurface != null)
      {
        if (surface == dummySurface) {
          surface = null;
        }
        dummySurface.release();
        dummySurface = null;
      }
      throw localThrowable;
    }
  }
  
  protected void renderOutputBuffer(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, true);
    TraceUtil.endSection();
    lastRenderTimeUs = (SystemClock.elapsedRealtime() * 1000L);
    paramMediaCodec = decoderCounters;
    renderedOutputBufferCount += 1;
    consecutiveDroppedFrameCount = 0;
    maybeNotifyRenderedFirstFrame();
  }
  
  protected void renderOutputBufferV21(MediaCodec paramMediaCodec, int paramInt, long paramLong1, long paramLong2)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, paramLong2);
    TraceUtil.endSection();
    lastRenderTimeUs = (SystemClock.elapsedRealtime() * 1000L);
    paramMediaCodec = decoderCounters;
    renderedOutputBufferCount += 1;
    consecutiveDroppedFrameCount = 0;
    maybeNotifyRenderedFirstFrame();
  }
  
  protected boolean shouldDropBuffersToKeyframe(long paramLong1, long paramLong2)
  {
    return isBufferVeryLate(paramLong1);
  }
  
  protected boolean shouldDropOutputBuffer(long paramLong1, long paramLong2)
  {
    return isBufferLate(paramLong1);
  }
  
  protected boolean shouldForceRenderOutputBuffer(long paramLong1, long paramLong2)
  {
    return (isBufferLate(paramLong1)) && (paramLong2 > 100000L);
  }
  
  protected boolean shouldInitCodec(MediaCodecInfo paramMediaCodecInfo)
  {
    return (surface != null) || (shouldUseDummySurface(paramMediaCodecInfo));
  }
  
  protected void skipOutputBuffer(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    TraceUtil.beginSection("skipVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    paramMediaCodec = decoderCounters;
    skippedOutputBufferCount += 1;
  }
  
  protected int supportsFormat(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    boolean bool1 = MimeTypes.isVideo(sampleMimeType);
    int j = 0;
    if (!bool1) {
      return 0;
    }
    DrmInitData localDrmInitData = drmInitData;
    int i;
    if (localDrmInitData != null)
    {
      i = 0;
      bool1 = false;
      for (;;)
      {
        bool2 = bool1;
        if (i >= schemeDataCount) {
          break;
        }
        bool1 |= getLanguagerequiresSecureDecryption;
        i += 1;
      }
    }
    boolean bool2 = false;
    List localList = paramMediaCodecSelector.getDecoderInfos(sampleMimeType, bool2);
    if (localList.isEmpty())
    {
      if ((bool2) && (!paramMediaCodecSelector.getDecoderInfos(sampleMimeType, false).isEmpty())) {
        return 2;
      }
      return 1;
    }
    if (!BaseRenderer.supportsFormatDrm(paramDrmSessionManager, localDrmInitData)) {
      return 2;
    }
    paramMediaCodecSelector = (MediaCodecInfo)localList.get(0);
    bool1 = paramMediaCodecSelector.isFormatSupported(paramFormat);
    if (paramMediaCodecSelector.isSeamlessAdaptationSupported(paramFormat)) {
      i = 16;
    } else {
      i = 8;
    }
    if (tunneling) {
      j = 32;
    }
    int k;
    if (bool1) {
      k = 4;
    } else {
      k = 3;
    }
    return k | i | j;
  }
  
  protected void updateDroppedBufferCounters(int paramInt)
  {
    DecoderCounters localDecoderCounters = decoderCounters;
    droppedBufferCount += paramInt;
    droppedFrames += paramInt;
    consecutiveDroppedFrameCount += paramInt;
    decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(consecutiveDroppedFrameCount, decoderCounters.maxConsecutiveDroppedBufferCount);
    if ((maxDroppedFramesToNotify > 0) && (droppedFrames >= maxDroppedFramesToNotify)) {
      maybeNotifyDroppedFrames();
    }
  }
  
  protected static final class CodecMaxValues
  {
    public final int height;
    public final int inputSize;
    public final int width;
    
    public CodecMaxValues(int paramInt1, int paramInt2, int paramInt3)
    {
      width = paramInt1;
      height = paramInt2;
      inputSize = paramInt3;
    }
  }
  
  @TargetApi(23)
  private final class OnFrameRenderedListenerV23
    implements MediaCodec.OnFrameRenderedListener
  {
    private OnFrameRenderedListenerV23(MediaCodec paramMediaCodec)
    {
      paramMediaCodec.setOnFrameRenderedListener(this, new Handler());
    }
    
    public void onFrameRendered(MediaCodec paramMediaCodec, long paramLong1, long paramLong2)
    {
      if (this != tunnelingOnFrameRenderedListener) {
        return;
      }
      onProcessedTunneledBuffer(paramLong1);
    }
  }
}