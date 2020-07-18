package com.google.android.exoplayer2.mediacodec;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecList;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({"InlinedApi"})
@TargetApi(16)
public final class MediaCodecUtil
{
  private static final SparseIntArray AVC_LEVEL_NUMBER_TO_CONST;
  private static final SparseIntArray AVC_PROFILE_NUMBER_TO_CONST;
  private static final String CODEC_ID_AVC1 = "avc1";
  private static final String CODEC_ID_AVC2 = "avc2";
  private static final String CODEC_ID_HEV1 = "hev1";
  private static final String CODEC_ID_HVC1 = "hvc1";
  private static final String CODEC_ID_MP4A = "mp4a";
  private static final Map<String, Integer> HEVC_CODEC_STRING_TO_PROFILE_LEVEL;
  private static final SparseIntArray MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE;
  private static final Pattern PROFILE_PATTERN = Pattern.compile("^\\D?(\\d+)$");
  private static final RawAudioCodecComparator RAW_AUDIO_CODEC_COMPARATOR = new RawAudioCodecComparator(null);
  private static final String TAG = "MediaCodecUtil";
  private static final HashMap<CodecKey, List<MediaCodecInfo>> decoderInfosCache = new HashMap();
  private static int maxH264DecodableFrameSize = -1;
  
  static
  {
    AVC_PROFILE_NUMBER_TO_CONST = new SparseIntArray();
    AVC_PROFILE_NUMBER_TO_CONST.put(66, 1);
    AVC_PROFILE_NUMBER_TO_CONST.put(77, 2);
    AVC_PROFILE_NUMBER_TO_CONST.put(88, 4);
    AVC_PROFILE_NUMBER_TO_CONST.put(100, 8);
    AVC_PROFILE_NUMBER_TO_CONST.put(110, 16);
    AVC_PROFILE_NUMBER_TO_CONST.put(122, 32);
    AVC_PROFILE_NUMBER_TO_CONST.put(244, 64);
    AVC_LEVEL_NUMBER_TO_CONST = new SparseIntArray();
    AVC_LEVEL_NUMBER_TO_CONST.put(10, 1);
    AVC_LEVEL_NUMBER_TO_CONST.put(11, 4);
    AVC_LEVEL_NUMBER_TO_CONST.put(12, 8);
    AVC_LEVEL_NUMBER_TO_CONST.put(13, 16);
    AVC_LEVEL_NUMBER_TO_CONST.put(20, 32);
    AVC_LEVEL_NUMBER_TO_CONST.put(21, 64);
    AVC_LEVEL_NUMBER_TO_CONST.put(22, 128);
    AVC_LEVEL_NUMBER_TO_CONST.put(30, 256);
    AVC_LEVEL_NUMBER_TO_CONST.put(31, 512);
    AVC_LEVEL_NUMBER_TO_CONST.put(32, 1024);
    AVC_LEVEL_NUMBER_TO_CONST.put(40, 2048);
    AVC_LEVEL_NUMBER_TO_CONST.put(41, 4096);
    AVC_LEVEL_NUMBER_TO_CONST.put(42, 8192);
    AVC_LEVEL_NUMBER_TO_CONST.put(50, 16384);
    AVC_LEVEL_NUMBER_TO_CONST.put(51, 32768);
    AVC_LEVEL_NUMBER_TO_CONST.put(52, 65536);
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL = new HashMap();
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L30", Integer.valueOf(1));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L60", Integer.valueOf(4));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L63", Integer.valueOf(16));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L90", Integer.valueOf(64));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L93", Integer.valueOf(256));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L120", Integer.valueOf(1024));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L123", Integer.valueOf(4096));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L150", Integer.valueOf(16384));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L153", Integer.valueOf(65536));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L156", Integer.valueOf(262144));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L180", Integer.valueOf(1048576));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L183", Integer.valueOf(4194304));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L186", Integer.valueOf(16777216));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H30", Integer.valueOf(2));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H60", Integer.valueOf(8));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H63", Integer.valueOf(32));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H90", Integer.valueOf(128));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H93", Integer.valueOf(512));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H120", Integer.valueOf(2048));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H123", Integer.valueOf(8192));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H150", Integer.valueOf(32768));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H153", Integer.valueOf(131072));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H156", Integer.valueOf(524288));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H180", Integer.valueOf(2097152));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H183", Integer.valueOf(8388608));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H186", Integer.valueOf(33554432));
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE = new SparseIntArray();
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(1, 1);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(2, 2);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(3, 3);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(4, 4);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(5, 5);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(6, 6);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(17, 17);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(20, 20);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(23, 23);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(29, 29);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(39, 39);
    MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(42, 42);
  }
  
  private MediaCodecUtil() {}
  
  private static void applyWorkarounds(String paramString, List paramList)
  {
    if ("audio/raw".equals(paramString)) {
      Collections.sort(paramList, RAW_AUDIO_CODEC_COMPARATOR);
    }
  }
  
  private static int avcLevelToMaxFrameSize(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    case 32768: 
    case 65536: 
      return 9437184;
    case 16384: 
      return 5652480;
    case 8192: 
      return 2228224;
    case 2048: 
    case 4096: 
      return 2097152;
    case 1024: 
      return 1310720;
    case 512: 
      return 921600;
    case 128: 
    case 256: 
      return 414720;
    case 64: 
      return 202752;
    case 8: 
    case 16: 
    case 32: 
      return 101376;
    }
    return 25344;
  }
  
  private static boolean codecNeedsDisableAdaptationWorkaround(String paramString)
  {
    return (Util.SDK_INT <= 22) && (("ODROID-XU3".equals(Util.MODEL)) || ("Nexus 10".equals(Util.MODEL))) && (("OMX.Exynos.AVC.Decoder".equals(paramString)) || ("OMX.Exynos.AVC.Decoder.secure".equals(paramString)));
  }
  
  private static Pair getAacCodecProfileAndLevel(String paramString, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length != 3)
    {
      paramArrayOfString = new StringBuilder();
      paramArrayOfString.append("Ignoring malformed MP4A codec string: ");
      paramArrayOfString.append(paramString);
      Log.w("MediaCodecUtil", paramArrayOfString.toString());
      return null;
    }
    String str = paramArrayOfString[1];
    try
    {
      str = MimeTypes.getMimeTypeFromMp4ObjectType(Integer.parseInt(str, 16));
      boolean bool = "audio/mp4a-latm".equals(str);
      if (!bool) {
        break label147;
      }
      paramArrayOfString = paramArrayOfString[2];
      int i = Integer.parseInt(paramArrayOfString);
      paramArrayOfString = MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE;
      i = paramArrayOfString.get(i, -1);
      if (i == -1) {
        break label147;
      }
      paramArrayOfString = new Pair(Integer.valueOf(i), Integer.valueOf(0));
      return paramArrayOfString;
    }
    catch (NumberFormatException paramArrayOfString)
    {
      for (;;) {}
    }
    paramArrayOfString = new StringBuilder();
    paramArrayOfString.append("Ignoring malformed MP4A codec string: ");
    paramArrayOfString.append(paramString);
    Log.w("MediaCodecUtil", paramArrayOfString.toString());
    return null;
    label147:
    return null;
  }
  
  private static Pair getAvcProfileAndLevel(String paramString, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length < 2)
    {
      paramArrayOfString = new StringBuilder();
      paramArrayOfString.append("Ignoring malformed AVC codec string: ");
      paramArrayOfString.append(paramString);
      Log.w("MediaCodecUtil", paramArrayOfString.toString());
      return null;
    }
    Object localObject = paramArrayOfString[1];
    try
    {
      int i = ((String)localObject).length();
      if (i == 6)
      {
        localObject = paramArrayOfString[1];
        localObject = Integer.valueOf(Integer.parseInt(((String)localObject).substring(0, 2), 16));
        paramArrayOfString = paramArrayOfString[1];
        paramArrayOfString = Integer.valueOf(Integer.parseInt(paramArrayOfString.substring(4), 16));
        paramString = (String)localObject;
      }
      else
      {
        if (paramArrayOfString.length < 3) {
          break label254;
        }
        localObject = paramArrayOfString[1];
        localObject = Integer.valueOf(Integer.parseInt((String)localObject));
        paramArrayOfString = paramArrayOfString[2];
        paramArrayOfString = Integer.valueOf(Integer.parseInt(paramArrayOfString));
        paramString = (String)localObject;
      }
      i = AVC_PROFILE_NUMBER_TO_CONST.get(paramString.intValue(), -1);
      if (i == -1)
      {
        paramArrayOfString = new StringBuilder();
        paramArrayOfString.append("Unknown AVC profile: ");
        paramArrayOfString.append(paramString);
        Log.w("MediaCodecUtil", paramArrayOfString.toString());
        return null;
      }
      int j = AVC_LEVEL_NUMBER_TO_CONST.get(paramArrayOfString.intValue(), -1);
      if (j == -1)
      {
        paramString = new StringBuilder();
        paramString.append("Unknown AVC level: ");
        paramString.append(paramArrayOfString);
        Log.w("MediaCodecUtil", paramString.toString());
        return null;
      }
      return new Pair(Integer.valueOf(i), Integer.valueOf(j));
    }
    catch (NumberFormatException paramArrayOfString)
    {
      label254:
      label287:
      for (;;) {}
    }
    try
    {
      paramArrayOfString = new StringBuilder();
      paramArrayOfString.append("Ignoring malformed AVC codec string: ");
      paramArrayOfString.append(paramString);
      Log.w("MediaCodecUtil", paramArrayOfString.toString());
      return null;
    }
    catch (NumberFormatException paramArrayOfString)
    {
      break label287;
    }
    paramArrayOfString = new StringBuilder();
    paramArrayOfString.append("Ignoring malformed AVC codec string: ");
    paramArrayOfString.append(paramString);
    Log.w("MediaCodecUtil", paramArrayOfString.toString());
    return null;
  }
  
  public static Pair getCodecProfileAndLevel(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    String[] arrayOfString = paramString.split("\\.");
    int i = 0;
    String str = arrayOfString[0];
    switch (str.hashCode())
    {
    default: 
      break;
    case 3356560: 
      if (str.equals("mp4a")) {
        i = 4;
      }
      break;
    case 3214780: 
      if (str.equals("hvc1")) {
        i = 1;
      }
      break;
    case 3199032: 
      if (!str.equals("hev1")) {
        break;
      }
      break;
    case 3006244: 
      if (str.equals("avc2")) {
        i = 3;
      }
      break;
    case 3006243: 
      if (str.equals("avc1")) {
        i = 2;
      }
      break;
    }
    i = -1;
    switch (i)
    {
    default: 
      return null;
    case 4: 
      return getAacCodecProfileAndLevel(paramString, arrayOfString);
    case 2: 
    case 3: 
      return getAvcProfileAndLevel(paramString, arrayOfString);
    }
    return getHevcProfileAndLevel(paramString, arrayOfString);
  }
  
  public static MediaCodecInfo getDecoderInfo(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    paramString = getDecoderInfos(paramString, paramBoolean);
    if (paramString.isEmpty()) {
      return null;
    }
    return (MediaCodecInfo)paramString.get(0);
  }
  
  public static List getDecoderInfos(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a5 = a4\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  /* Error */
  private static java.util.ArrayList getDecoderInfosInternal(CodecKey paramCodecKey, MediaCodecListCompat paramMediaCodecListCompat, String paramString)
    throws MediaCodecUtil.DecoderQueryException
  {
    // Byte code:
    //   0: new 346	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 347	java/util/ArrayList:<init>	()V
    //   7: astore 13
    //   9: aload_0
    //   10: getfield 350	com/google/android/exoplayer2/mediacodec/MediaCodecUtil$CodecKey:mimeType	Ljava/lang/String;
    //   13: astore 14
    //   15: aload_1
    //   16: invokeinterface 353 1 0
    //   21: istore_3
    //   22: aload_1
    //   23: invokeinterface 356 1 0
    //   28: istore 8
    //   30: iconst_0
    //   31: istore 4
    //   33: iload 4
    //   35: iload_3
    //   36: if_icmpge +384 -> 420
    //   39: aload_1
    //   40: iload 4
    //   42: invokeinterface 360 2 0
    //   47: astore 15
    //   49: aload 15
    //   51: invokevirtual 365	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   54: astore 12
    //   56: aload 15
    //   58: aload 12
    //   60: iload 8
    //   62: aload_2
    //   63: invokestatic 369	com/google/android/exoplayer2/mediacodec/MediaCodecUtil:isCodecUsableDecoder	(Landroid/media/MediaCodecInfo;Ljava/lang/String;ZLjava/lang/String;)Z
    //   66: istore 9
    //   68: iload_3
    //   69: istore 5
    //   71: iload 9
    //   73: ifeq +335 -> 408
    //   76: aload 15
    //   78: invokevirtual 373	android/media/MediaCodecInfo:getSupportedTypes	()[Ljava/lang/String;
    //   81: astore 16
    //   83: aload 16
    //   85: arraylength
    //   86: istore 7
    //   88: iconst_0
    //   89: istore 6
    //   91: iload_3
    //   92: istore 5
    //   94: iload 6
    //   96: iload 7
    //   98: if_icmpge +310 -> 408
    //   101: aload 16
    //   103: iload 6
    //   105: aaload
    //   106: astore 17
    //   108: aload 17
    //   110: aload 14
    //   112: invokevirtual 376	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   115: istore 9
    //   117: iload 9
    //   119: ifeq +280 -> 399
    //   122: aload 15
    //   124: aload 17
    //   126: invokevirtual 380	android/media/MediaCodecInfo:getCapabilitiesForType	(Ljava/lang/String;)Landroid/media/MediaCodecInfo$CodecCapabilities;
    //   129: astore 11
    //   131: aload_1
    //   132: aload 14
    //   134: aload 11
    //   136: invokeinterface 384 3 0
    //   141: istore 9
    //   143: aload 12
    //   145: invokestatic 386	com/google/android/exoplayer2/mediacodec/MediaCodecUtil:codecNeedsDisableAdaptationWorkaround	(Ljava/lang/String;)Z
    //   148: istore 10
    //   150: iload 8
    //   152: ifeq +23 -> 175
    //   155: aload_0
    //   156: getfield 390	com/google/android/exoplayer2/mediacodec/MediaCodecUtil$CodecKey:secure	Z
    //   159: iload 9
    //   161: if_icmpeq +6 -> 167
    //   164: goto +11 -> 175
    //   167: goto +23 -> 190
    //   170: astore 11
    //   172: goto +101 -> 273
    //   175: iload 8
    //   177: ifne +34 -> 211
    //   180: aload_0
    //   181: getfield 390	com/google/android/exoplayer2/mediacodec/MediaCodecUtil$CodecKey:secure	Z
    //   184: ifne +27 -> 211
    //   187: goto -20 -> 167
    //   190: aload 13
    //   192: aload 12
    //   194: aload 14
    //   196: aload 11
    //   198: iload 10
    //   200: iconst_0
    //   201: invokestatic 394	com/google/android/exoplayer2/mediacodec/MediaCodecInfo:newInstance	(Ljava/lang/String;Ljava/lang/String;Landroid/media/MediaCodecInfo$CodecCapabilities;ZZ)Lcom/google/android/exoplayer2/mediacodec/MediaCodecInfo;
    //   204: invokevirtual 397	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   207: pop
    //   208: goto +191 -> 399
    //   211: iload 8
    //   213: ifne +186 -> 399
    //   216: iload 9
    //   218: ifeq +181 -> 399
    //   221: new 231	java/lang/StringBuilder
    //   224: dup
    //   225: invokespecial 232	java/lang/StringBuilder:<init>	()V
    //   228: astore 18
    //   230: aload 18
    //   232: aload 12
    //   234: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   237: pop
    //   238: aload 18
    //   240: ldc_w 399
    //   243: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: pop
    //   247: aload 13
    //   249: aload 18
    //   251: invokevirtual 242	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   254: aload 14
    //   256: aload 11
    //   258: iload 10
    //   260: iconst_1
    //   261: invokestatic 394	com/google/android/exoplayer2/mediacodec/MediaCodecInfo:newInstance	(Ljava/lang/String;Ljava/lang/String;Landroid/media/MediaCodecInfo$CodecCapabilities;ZZ)Lcom/google/android/exoplayer2/mediacodec/MediaCodecInfo;
    //   264: invokevirtual 397	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   267: pop
    //   268: aload 13
    //   270: areturn
    //   271: astore 11
    //   273: getstatic 214	com/google/android/exoplayer2/util/Util:SDK_INT	I
    //   276: bipush 23
    //   278: if_icmpgt +63 -> 341
    //   281: aload 13
    //   283: invokevirtual 400	java/util/ArrayList:isEmpty	()Z
    //   286: istore 9
    //   288: iload 9
    //   290: ifne +51 -> 341
    //   293: new 231	java/lang/StringBuilder
    //   296: dup
    //   297: invokespecial 232	java/lang/StringBuilder:<init>	()V
    //   300: astore 11
    //   302: aload 11
    //   304: ldc_w 402
    //   307: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   310: pop
    //   311: aload 11
    //   313: aload 12
    //   315: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   318: pop
    //   319: aload 11
    //   321: ldc_w 404
    //   324: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   327: pop
    //   328: ldc 59
    //   330: aload 11
    //   332: invokevirtual 242	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   335: invokestatic 407	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   338: goto +61 -> 399
    //   341: new 231	java/lang/StringBuilder
    //   344: dup
    //   345: invokespecial 232	java/lang/StringBuilder:<init>	()V
    //   348: astore_0
    //   349: aload_0
    //   350: ldc_w 409
    //   353: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   356: pop
    //   357: aload_0
    //   358: aload 12
    //   360: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: pop
    //   364: aload_0
    //   365: ldc_w 411
    //   368: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   371: pop
    //   372: aload_0
    //   373: aload 17
    //   375: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   378: pop
    //   379: aload_0
    //   380: ldc_w 413
    //   383: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   386: pop
    //   387: ldc 59
    //   389: aload_0
    //   390: invokevirtual 242	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   393: invokestatic 407	com/google/android/exoplayer2/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   396: aload 11
    //   398: athrow
    //   399: iload 6
    //   401: iconst_1
    //   402: iadd
    //   403: istore 6
    //   405: goto -314 -> 91
    //   408: iload 4
    //   410: iconst_1
    //   411: iadd
    //   412: istore 4
    //   414: iload 5
    //   416: istore_3
    //   417: goto -384 -> 33
    //   420: aload 13
    //   422: areturn
    //   423: astore_0
    //   424: new 11	com/google/android/exoplayer2/mediacodec/MediaCodecUtil$DecoderQueryException
    //   427: dup
    //   428: aload_0
    //   429: aconst_null
    //   430: invokespecial 416	com/google/android/exoplayer2/mediacodec/MediaCodecUtil$DecoderQueryException:<init>	(Ljava/lang/Throwable;Lcom/google/android/exoplayer2/mediacodec/MediaCodecUtil$1;)V
    //   433: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	434	0	paramCodecKey	CodecKey
    //   0	434	1	paramMediaCodecListCompat	MediaCodecListCompat
    //   0	434	2	paramString	String
    //   21	396	3	i	int
    //   31	382	4	j	int
    //   69	346	5	k	int
    //   89	315	6	m	int
    //   86	13	7	n	int
    //   28	184	8	bool1	boolean
    //   66	223	9	bool2	boolean
    //   148	111	10	bool3	boolean
    //   129	6	11	localCodecCapabilities	MediaCodecInfo.CodecCapabilities
    //   170	87	11	localException1	Exception
    //   271	1	11	localException2	Exception
    //   300	97	11	localStringBuilder1	StringBuilder
    //   54	305	12	str1	String
    //   7	414	13	localArrayList	java.util.ArrayList
    //   13	242	14	str2	String
    //   47	76	15	localMediaCodecInfo	android.media.MediaCodecInfo
    //   81	21	16	arrayOfString	String[]
    //   106	268	17	str3	String
    //   228	22	18	localStringBuilder2	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   190	208	170	java/lang/Exception
    //   221	268	170	java/lang/Exception
    //   122	150	271	java/lang/Exception
    //   0	9	423	java/lang/Exception
    //   15	22	423	java/lang/Exception
    //   22	30	423	java/lang/Exception
    //   39	68	423	java/lang/Exception
    //   76	83	423	java/lang/Exception
    //   108	117	423	java/lang/Exception
    //   281	288	423	java/lang/Exception
    //   293	338	423	java/lang/Exception
    //   341	399	423	java/lang/Exception
  }
  
  private static Pair getHevcProfileAndLevel(String paramString, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length < 4)
    {
      paramArrayOfString = new StringBuilder();
      paramArrayOfString.append("Ignoring malformed HEVC codec string: ");
      paramArrayOfString.append(paramString);
      Log.w("MediaCodecUtil", paramArrayOfString.toString());
      return null;
    }
    Matcher localMatcher = PROFILE_PATTERN.matcher(paramArrayOfString[1]);
    if (!localMatcher.matches())
    {
      paramArrayOfString = new StringBuilder();
      paramArrayOfString.append("Ignoring malformed HEVC codec string: ");
      paramArrayOfString.append(paramString);
      Log.w("MediaCodecUtil", paramArrayOfString.toString());
      return null;
    }
    paramString = localMatcher.group(1);
    int i;
    if ("1".equals(paramString))
    {
      i = 1;
    }
    else
    {
      if (!"2".equals(paramString)) {
        break label191;
      }
      i = 2;
    }
    paramString = (Integer)HEVC_CODEC_STRING_TO_PROFILE_LEVEL.get(paramArrayOfString[3]);
    if (paramString == null)
    {
      paramString = new StringBuilder();
      paramString.append("Unknown HEVC level string: ");
      paramString.append(localMatcher.group(1));
      Log.w("MediaCodecUtil", paramString.toString());
      return null;
    }
    return new Pair(Integer.valueOf(i), paramString);
    label191:
    paramArrayOfString = new StringBuilder();
    paramArrayOfString.append("Unknown HEVC profile string: ");
    paramArrayOfString.append(paramString);
    Log.w("MediaCodecUtil", paramArrayOfString.toString());
    return null;
  }
  
  public static MediaCodecInfo getPassthroughDecoderInfo()
    throws MediaCodecUtil.DecoderQueryException
  {
    MediaCodecInfo localMediaCodecInfo = getDecoderInfo("audio/raw", false);
    if (localMediaCodecInfo == null) {
      return null;
    }
    return MediaCodecInfo.newPassthroughInstance(name);
  }
  
  private static boolean isCodecUsableDecoder(android.media.MediaCodecInfo paramMediaCodecInfo, String paramString1, boolean paramBoolean, String paramString2)
  {
    if (!paramMediaCodecInfo.isEncoder())
    {
      if ((!paramBoolean) && (paramString1.endsWith(".secure"))) {
        return false;
      }
      if (Util.SDK_INT < 21)
      {
        if (("CIPAACDecoder".equals(paramString1)) || ("CIPMP3Decoder".equals(paramString1)) || ("CIPVorbisDecoder".equals(paramString1)) || ("CIPAMRNBDecoder".equals(paramString1)) || ("AACDecoder".equals(paramString1))) {
          break label711;
        }
        if ("MP3Decoder".equals(paramString1)) {
          return false;
        }
      }
      if ((Util.SDK_INT < 18) && ("OMX.SEC.MP3.Decoder".equals(paramString1))) {
        return false;
      }
      if (("OMX.SEC.mp3.dec".equals(paramString1)) && ("SM-T530".equals(Util.MODEL))) {
        return false;
      }
      if ((Util.SDK_INT < 18) && ("OMX.MTK.AUDIO.DECODER.AAC".equals(paramString1)))
      {
        if ("a70".equals(Util.DEVICE)) {
          break label711;
        }
        if (("Xiaomi".equals(Util.MANUFACTURER)) && (Util.DEVICE.startsWith("HM"))) {
          return false;
        }
      }
      if ((Util.SDK_INT == 16) && ("OMX.qcom.audio.decoder.mp3".equals(paramString1)))
      {
        if (("dlxu".equals(Util.DEVICE)) || ("protou".equals(Util.DEVICE)) || ("ville".equals(Util.DEVICE)) || ("villeplus".equals(Util.DEVICE)) || ("villec2".equals(Util.DEVICE)) || (Util.DEVICE.startsWith("gee")) || ("C6602".equals(Util.DEVICE)) || ("C6603".equals(Util.DEVICE)) || ("C6606".equals(Util.DEVICE)) || ("C6616".equals(Util.DEVICE)) || ("L36h".equals(Util.DEVICE))) {
          break label711;
        }
        if ("SO-02E".equals(Util.DEVICE)) {
          return false;
        }
      }
      if ((Util.SDK_INT == 16) && ("OMX.qcom.audio.decoder.aac".equals(paramString1)))
      {
        if (("C1504".equals(Util.DEVICE)) || ("C1505".equals(Util.DEVICE)) || ("C1604".equals(Util.DEVICE))) {
          break label711;
        }
        if ("C1605".equals(Util.DEVICE)) {
          return false;
        }
      }
      if ((Util.SDK_INT < 24) && (("OMX.SEC.aac.dec".equals(paramString1)) || ("OMX.Exynos.AAC.Decoder".equals(paramString1))) && ("samsung".equals(Util.MANUFACTURER)))
      {
        if ((Util.DEVICE.startsWith("zeroflte")) || (Util.DEVICE.startsWith("zerolte")) || (Util.DEVICE.startsWith("zenlte")) || ("SC-05G".equals(Util.DEVICE)) || ("marinelteatt".equals(Util.DEVICE)) || ("404SC".equals(Util.DEVICE)) || ("SC-04G".equals(Util.DEVICE))) {
          break label711;
        }
        if ("SCV31".equals(Util.DEVICE)) {
          return false;
        }
      }
      if ((Util.SDK_INT <= 19) && ("OMX.SEC.vp8.dec".equals(paramString1)) && ("samsung".equals(Util.MANUFACTURER)))
      {
        if ((Util.DEVICE.startsWith("d2")) || (Util.DEVICE.startsWith("serrano")) || (Util.DEVICE.startsWith("jflte")) || (Util.DEVICE.startsWith("santos"))) {
          break label711;
        }
        if (Util.DEVICE.startsWith("t0")) {
          return false;
        }
      }
      if ((Util.SDK_INT <= 19) && (Util.DEVICE.startsWith("jflte")) && ("OMX.qcom.video.decoder.vp8".equals(paramString1))) {
        return false;
      }
      return (!"audio/eac3-joc".equals(paramString2)) || (!"OMX.MTK.AUDIO.DECODER.DSPAC3".equals(paramString1));
    }
    label711:
    return false;
  }
  
  public static int maxH264DecodableFrameSize()
    throws MediaCodecUtil.DecoderQueryException
  {
    if (maxH264DecodableFrameSize == -1)
    {
      int i = 0;
      int j = 0;
      Object localObject = getDecoderInfo("video/avc", false);
      if (localObject != null)
      {
        localObject = ((MediaCodecInfo)localObject).getProfileLevels();
        int k = localObject.length;
        i = 0;
        while (j < k)
        {
          i = Math.max(avcLevelToMaxFrameSize(level), i);
          j += 1;
        }
        if (Util.SDK_INT >= 21) {
          j = 345600;
        } else {
          j = 172800;
        }
        i = Math.max(i, j);
      }
      maxH264DecodableFrameSize = i;
    }
    return maxH264DecodableFrameSize;
  }
  
  public static void warmDecoderInfoCache(String paramString, boolean paramBoolean)
  {
    try
    {
      getDecoderInfos(paramString, paramBoolean);
      return;
    }
    catch (DecoderQueryException paramString)
    {
      Log.e("MediaCodecUtil", "Codec warming failed", paramString);
    }
  }
  
  private static final class CodecKey
  {
    public final String mimeType;
    public final boolean secure;
    
    public CodecKey(String paramString, boolean paramBoolean)
    {
      mimeType = paramString;
      secure = paramBoolean;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject != null)
      {
        if (paramObject.getClass() != CodecKey.class) {
          return false;
        }
        paramObject = (CodecKey)paramObject;
        return (TextUtils.equals(mimeType, mimeType)) && (secure == secure);
      }
      return false;
    }
    
    public int hashCode()
    {
      int i;
      if (mimeType == null) {
        i = 0;
      } else {
        i = mimeType.hashCode();
      }
      int j;
      if (secure) {
        j = 1231;
      } else {
        j = 1237;
      }
      return (i + 31) * 31 + j;
    }
  }
  
  public static class DecoderQueryException
    extends Exception
  {
    private DecoderQueryException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  private static abstract interface MediaCodecListCompat
  {
    public abstract int getCodecCount();
    
    public abstract android.media.MediaCodecInfo getCodecInfoAt(int paramInt);
    
    public abstract boolean isSecurePlaybackSupported(String paramString, MediaCodecInfo.CodecCapabilities paramCodecCapabilities);
    
    public abstract boolean secureDecodersExplicit();
  }
  
  private static final class MediaCodecListCompatV16
    implements MediaCodecUtil.MediaCodecListCompat
  {
    private MediaCodecListCompatV16() {}
    
    public int getCodecCount()
    {
      return MediaCodecList.getCodecCount();
    }
    
    public android.media.MediaCodecInfo getCodecInfoAt(int paramInt)
    {
      return MediaCodecList.getCodecInfoAt(paramInt);
    }
    
    public boolean isSecurePlaybackSupported(String paramString, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      return "video/avc".equals(paramString);
    }
    
    public boolean secureDecodersExplicit()
    {
      return false;
    }
  }
  
  @TargetApi(21)
  private static final class MediaCodecListCompatV21
    implements MediaCodecUtil.MediaCodecListCompat
  {
    private final int codecKind;
    private android.media.MediaCodecInfo[] mediaCodecInfos;
    
    public MediaCodecListCompatV21(boolean paramBoolean) {}
    
    private void ensureMediaCodecInfosInitialized()
    {
      if (mediaCodecInfos == null) {
        mediaCodecInfos = new MediaCodecList(codecKind).getCodecInfos();
      }
    }
    
    public int getCodecCount()
    {
      ensureMediaCodecInfosInitialized();
      return mediaCodecInfos.length;
    }
    
    public android.media.MediaCodecInfo getCodecInfoAt(int paramInt)
    {
      ensureMediaCodecInfosInitialized();
      return mediaCodecInfos[paramInt];
    }
    
    public boolean isSecurePlaybackSupported(String paramString, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      return paramCodecCapabilities.isFeatureSupported("secure-playback");
    }
    
    public boolean secureDecodersExplicit()
    {
      return true;
    }
  }
  
  private static final class RawAudioCodecComparator
    implements Comparator<MediaCodecInfo>
  {
    private RawAudioCodecComparator() {}
    
    private static int scoreMediaCodecInfo(MediaCodecInfo paramMediaCodecInfo)
    {
      paramMediaCodecInfo = name;
      if ((!paramMediaCodecInfo.startsWith("OMX.google")) && (!paramMediaCodecInfo.startsWith("c2.android")))
      {
        if ((Util.SDK_INT < 26) && (paramMediaCodecInfo.equals("OMX.MTK.AUDIO.DECODER.RAW"))) {
          return 1;
        }
        return 0;
      }
      return -1;
    }
    
    public int compare(MediaCodecInfo paramMediaCodecInfo1, MediaCodecInfo paramMediaCodecInfo2)
    {
      return scoreMediaCodecInfo(paramMediaCodecInfo1) - scoreMediaCodecInfo(paramMediaCodecInfo2);
    }
  }
}
