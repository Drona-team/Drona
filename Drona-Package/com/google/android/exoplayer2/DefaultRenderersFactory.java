package com.google.android.exoplayer2;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.exoplayer2.video.spherical.CameraMotionRenderer;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class DefaultRenderersFactory
  implements RenderersFactory
{
  public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000L;
  public static final int EXTENSION_RENDERER_MODE_OFF = 0;
  public static final int EXTENSION_RENDERER_MODE_ON = 1;
  public static final int EXTENSION_RENDERER_MODE_PREFER = 2;
  protected static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
  private static final String PAGE_KEY = "DefaultRenderersFactory";
  private final long allowedVideoJoiningTimeMs;
  private final Context context;
  @Nullable
  private final com.google.android.exoplayer2.drm.DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
  private final int extensionRendererMode;
  
  public DefaultRenderersFactory(Context paramContext)
  {
    this(paramContext, 0);
  }
  
  public DefaultRenderersFactory(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, 5000L);
  }
  
  public DefaultRenderersFactory(Context paramContext, int paramInt, long paramLong)
  {
    context = paramContext;
    extensionRendererMode = paramInt;
    allowedVideoJoiningTimeMs = paramLong;
    drmSessionManager = null;
  }
  
  public DefaultRenderersFactory(Context paramContext, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager)
  {
    this(paramContext, paramDrmSessionManager, 0);
  }
  
  public DefaultRenderersFactory(Context paramContext, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, int paramInt)
  {
    this(paramContext, paramDrmSessionManager, paramInt, 5000L);
  }
  
  public DefaultRenderersFactory(Context paramContext, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, int paramInt, long paramLong)
  {
    context = paramContext;
    extensionRendererMode = paramInt;
    allowedVideoJoiningTimeMs = paramLong;
    drmSessionManager = paramDrmSessionManager;
  }
  
  protected AudioProcessor[] buildAudioProcessors()
  {
    return new AudioProcessor[0];
  }
  
  /* Error */
  protected void buildAudioRenderers(Context paramContext, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, AudioProcessor[] paramArrayOfAudioProcessor, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, int paramInt, ArrayList paramArrayList)
  {
    // Byte code:
    //   0: aload 7
    //   2: new 73	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer
    //   5: dup
    //   6: aload_1
    //   7: getstatic 79	com/google/android/exoplayer2/mediacodec/MediaCodecSelector:DEFAULT	Lcom/google/android/exoplayer2/mediacodec/MediaCodecSelector;
    //   10: aload_2
    //   11: iconst_0
    //   12: aload 4
    //   14: aload 5
    //   16: aload_1
    //   17: invokestatic 85	com/google/android/exoplayer2/audio/AudioCapabilities:getCapabilities	(Landroid/content/Context;)Lcom/google/android/exoplayer2/audio/AudioCapabilities;
    //   20: aload_3
    //   21: invokespecial 88	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:<init>	(Landroid/content/Context;Lcom/google/android/exoplayer2/mediacodec/MediaCodecSelector;Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;ZLandroid/os/Handler;Lcom/google/android/exoplayer2/audio/AudioRendererEventListener;Lcom/google/android/exoplayer2/audio/AudioCapabilities;[Lcom/google/android/exoplayer2/audio/AudioProcessor;)V
    //   24: invokevirtual 94	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   27: pop
    //   28: iload 6
    //   30: ifne +4 -> 34
    //   33: return
    //   34: aload 7
    //   36: invokevirtual 98	java/util/ArrayList:size	()I
    //   39: istore 9
    //   41: iload 9
    //   43: istore 8
    //   45: iload 6
    //   47: iconst_2
    //   48: if_icmpne +9 -> 57
    //   51: iload 9
    //   53: iconst_1
    //   54: isub
    //   55: istore 8
    //   57: ldc 100
    //   59: invokestatic 106	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   62: astore_1
    //   63: aload_1
    //   64: iconst_3
    //   65: anewarray 102	java/lang/Class
    //   68: dup
    //   69: iconst_0
    //   70: ldc 108
    //   72: aastore
    //   73: dup
    //   74: iconst_1
    //   75: ldc 110
    //   77: aastore
    //   78: dup
    //   79: iconst_2
    //   80: ldc 112
    //   82: aastore
    //   83: invokevirtual 116	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   86: astore_1
    //   87: aload_1
    //   88: iconst_3
    //   89: anewarray 4	java/lang/Object
    //   92: dup
    //   93: iconst_0
    //   94: aload 4
    //   96: aastore
    //   97: dup
    //   98: iconst_1
    //   99: aload 5
    //   101: aastore
    //   102: dup
    //   103: iconst_2
    //   104: aload_3
    //   105: aastore
    //   106: invokevirtual 122	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   109: astore_1
    //   110: aload_1
    //   111: checkcast 124	com/google/android/exoplayer2/Renderer
    //   114: astore_1
    //   115: iload 8
    //   117: iconst_1
    //   118: iadd
    //   119: istore 6
    //   121: aload 7
    //   123: iload 8
    //   125: aload_1
    //   126: invokevirtual 127	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   129: ldc 26
    //   131: ldc -127
    //   133: invokestatic 135	com/google/android/exoplayer2/util/Log:log	(Ljava/lang/String;Ljava/lang/String;)V
    //   136: goto +19 -> 155
    //   139: astore_1
    //   140: new 137	java/lang/RuntimeException
    //   143: dup
    //   144: ldc -117
    //   146: aload_1
    //   147: invokespecial 142	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   150: athrow
    //   151: iload 8
    //   153: istore 6
    //   155: ldc -112
    //   157: invokestatic 106	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   160: astore_1
    //   161: aload_1
    //   162: iconst_3
    //   163: anewarray 102	java/lang/Class
    //   166: dup
    //   167: iconst_0
    //   168: ldc 108
    //   170: aastore
    //   171: dup
    //   172: iconst_1
    //   173: ldc 110
    //   175: aastore
    //   176: dup
    //   177: iconst_2
    //   178: ldc 112
    //   180: aastore
    //   181: invokevirtual 116	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   184: astore_1
    //   185: aload_1
    //   186: iconst_3
    //   187: anewarray 4	java/lang/Object
    //   190: dup
    //   191: iconst_0
    //   192: aload 4
    //   194: aastore
    //   195: dup
    //   196: iconst_1
    //   197: aload 5
    //   199: aastore
    //   200: dup
    //   201: iconst_2
    //   202: aload_3
    //   203: aastore
    //   204: invokevirtual 122	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   207: astore_1
    //   208: aload_1
    //   209: checkcast 124	com/google/android/exoplayer2/Renderer
    //   212: astore_1
    //   213: iload 6
    //   215: iconst_1
    //   216: iadd
    //   217: istore 8
    //   219: aload 7
    //   221: iload 6
    //   223: aload_1
    //   224: invokevirtual 127	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   227: ldc 26
    //   229: ldc -110
    //   231: invokestatic 135	com/google/android/exoplayer2/util/Log:log	(Ljava/lang/String;Ljava/lang/String;)V
    //   234: iload 8
    //   236: istore 6
    //   238: goto +15 -> 253
    //   241: astore_1
    //   242: new 137	java/lang/RuntimeException
    //   245: dup
    //   246: ldc -108
    //   248: aload_1
    //   249: invokespecial 142	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   252: athrow
    //   253: ldc -106
    //   255: invokestatic 106	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   258: astore_1
    //   259: aload_1
    //   260: iconst_3
    //   261: anewarray 102	java/lang/Class
    //   264: dup
    //   265: iconst_0
    //   266: ldc 108
    //   268: aastore
    //   269: dup
    //   270: iconst_1
    //   271: ldc 110
    //   273: aastore
    //   274: dup
    //   275: iconst_2
    //   276: ldc 112
    //   278: aastore
    //   279: invokevirtual 116	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   282: astore_1
    //   283: aload_1
    //   284: iconst_3
    //   285: anewarray 4	java/lang/Object
    //   288: dup
    //   289: iconst_0
    //   290: aload 4
    //   292: aastore
    //   293: dup
    //   294: iconst_1
    //   295: aload 5
    //   297: aastore
    //   298: dup
    //   299: iconst_2
    //   300: aload_3
    //   301: aastore
    //   302: invokevirtual 122	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   305: astore_1
    //   306: aload_1
    //   307: checkcast 124	com/google/android/exoplayer2/Renderer
    //   310: astore_1
    //   311: aload 7
    //   313: iload 6
    //   315: aload_1
    //   316: invokevirtual 127	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   319: ldc 26
    //   321: ldc -104
    //   323: invokestatic 135	com/google/android/exoplayer2/util/Log:log	(Ljava/lang/String;Ljava/lang/String;)V
    //   326: return
    //   327: astore_1
    //   328: new 137	java/lang/RuntimeException
    //   331: dup
    //   332: ldc -102
    //   334: aload_1
    //   335: invokespecial 142	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   338: athrow
    //   339: astore_1
    //   340: goto -189 -> 151
    //   343: astore_1
    //   344: goto -189 -> 155
    //   347: astore_1
    //   348: goto -95 -> 253
    //   351: astore_1
    //   352: iload 8
    //   354: istore 6
    //   356: goto -103 -> 253
    //   359: astore_1
    //   360: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	361	0	this	DefaultRenderersFactory
    //   0	361	1	paramContext	Context
    //   0	361	2	paramDrmSessionManager	com.google.android.exoplayer2.upgrade.DrmSessionManager
    //   0	361	3	paramArrayOfAudioProcessor	AudioProcessor[]
    //   0	361	4	paramHandler	Handler
    //   0	361	5	paramAudioRendererEventListener	AudioRendererEventListener
    //   0	361	6	paramInt	int
    //   0	361	7	paramArrayList	ArrayList
    //   43	310	8	i	int
    //   39	16	9	j	int
    // Exception table:
    //   from	to	target	type
    //   57	63	139	java/lang/Exception
    //   63	87	139	java/lang/Exception
    //   87	110	139	java/lang/Exception
    //   121	136	139	java/lang/Exception
    //   155	161	241	java/lang/Exception
    //   161	185	241	java/lang/Exception
    //   185	208	241	java/lang/Exception
    //   219	234	241	java/lang/Exception
    //   253	259	327	java/lang/Exception
    //   259	283	327	java/lang/Exception
    //   283	306	327	java/lang/Exception
    //   311	326	327	java/lang/Exception
    //   57	63	339	java/lang/ClassNotFoundException
    //   63	87	339	java/lang/ClassNotFoundException
    //   87	110	339	java/lang/ClassNotFoundException
    //   121	136	343	java/lang/ClassNotFoundException
    //   155	161	347	java/lang/ClassNotFoundException
    //   161	185	347	java/lang/ClassNotFoundException
    //   185	208	347	java/lang/ClassNotFoundException
    //   219	234	351	java/lang/ClassNotFoundException
    //   253	259	359	java/lang/ClassNotFoundException
    //   259	283	359	java/lang/ClassNotFoundException
    //   283	306	359	java/lang/ClassNotFoundException
    //   311	326	359	java/lang/ClassNotFoundException
  }
  
  protected void buildCameraMotionRenderers(Context paramContext, int paramInt, ArrayList paramArrayList)
  {
    paramArrayList.add(new CameraMotionRenderer());
  }
  
  protected void buildMetadataRenderers(Context paramContext, MetadataOutput paramMetadataOutput, Looper paramLooper, int paramInt, ArrayList paramArrayList)
  {
    paramArrayList.add(new MetadataRenderer(paramMetadataOutput, paramLooper));
  }
  
  protected void buildMiscellaneousRenderers(Context paramContext, Handler paramHandler, int paramInt, ArrayList paramArrayList) {}
  
  protected void buildTextRenderers(Context paramContext, TextOutput paramTextOutput, Looper paramLooper, int paramInt, ArrayList paramArrayList)
  {
    paramArrayList.add(new TextRenderer(paramTextOutput, paramLooper));
  }
  
  protected void buildVideoRenderers(Context paramContext, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, long paramLong, Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, int paramInt, ArrayList paramArrayList)
  {
    paramArrayList.add(new MediaCodecVideoRenderer(paramContext, MediaCodecSelector.DEFAULT, paramLong, paramDrmSessionManager, false, paramHandler, paramVideoRendererEventListener, 50));
    if (paramInt == 0) {
      return;
    }
    int j = paramArrayList.size();
    int i = j;
    if (paramInt == 2) {
      i = j - 1;
    }
    try
    {
      paramContext = Class.forName("com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer");
      paramDrmSessionManager = Boolean.TYPE;
      Class localClass1 = Long.TYPE;
      Class localClass2 = Integer.TYPE;
      paramContext = paramContext.getConstructor(new Class[] { paramDrmSessionManager, localClass1, Handler.class, VideoRendererEventListener.class, localClass2 });
      paramContext = paramContext.newInstance(new Object[] { Boolean.valueOf(true), Long.valueOf(paramLong), paramHandler, paramVideoRendererEventListener, Integer.valueOf(50) });
      paramContext = (Renderer)paramContext;
      paramArrayList.add(i, paramContext);
      Log.log("DefaultRenderersFactory", "Loaded LibvpxVideoRenderer.");
      return;
    }
    catch (Exception paramContext)
    {
      throw new RuntimeException("Error instantiating VP9 extension", paramContext);
    }
    catch (ClassNotFoundException paramContext) {}
  }
  
  public Renderer[] createRenderers(Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, AudioRendererEventListener paramAudioRendererEventListener, TextOutput paramTextOutput, MetadataOutput paramMetadataOutput, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager)
  {
    com.google.android.exoplayer2.upgrade.DrmSessionManager localDrmSessionManager = paramDrmSessionManager;
    if (paramDrmSessionManager == null) {
      localDrmSessionManager = drmSessionManager;
    }
    paramDrmSessionManager = new ArrayList();
    buildVideoRenderers(context, localDrmSessionManager, allowedVideoJoiningTimeMs, paramHandler, paramVideoRendererEventListener, extensionRendererMode, paramDrmSessionManager);
    buildAudioRenderers(context, localDrmSessionManager, buildAudioProcessors(), paramHandler, paramAudioRendererEventListener, extensionRendererMode, paramDrmSessionManager);
    buildTextRenderers(context, paramTextOutput, paramHandler.getLooper(), extensionRendererMode, paramDrmSessionManager);
    buildMetadataRenderers(context, paramMetadataOutput, paramHandler.getLooper(), extensionRendererMode, paramDrmSessionManager);
    buildCameraMotionRenderers(context, extensionRendererMode, paramDrmSessionManager);
    buildMiscellaneousRenderers(context, paramHandler, extensionRendererMode, paramDrmSessionManager);
    return (Renderer[])paramDrmSessionManager.toArray(new Renderer[paramDrmSessionManager.size()]);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ExtensionRendererMode {}
}
