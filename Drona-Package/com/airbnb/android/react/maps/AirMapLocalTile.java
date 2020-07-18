package com.airbnb.android.react.maps;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

public class AirMapLocalTile
  extends AirMapFeature
{
  private String pathTemplate;
  private TileOverlay tileOverlay;
  private TileOverlayOptions tileOverlayOptions;
  private AIRMapLocalTileProvider tileProvider;
  private float tileSize;
  private float zIndex;
  
  public AirMapLocalTile(Context paramContext)
  {
    super(paramContext);
  }
  
  private TileOverlayOptions createTileOverlayOptions()
  {
    TileOverlayOptions localTileOverlayOptions = new TileOverlayOptions();
    localTileOverlayOptions.zIndex(zIndex);
    tileProvider = new AIRMapLocalTileProvider((int)tileSize, pathTemplate);
    localTileOverlayOptions.tileProvider(tileProvider);
    return localTileOverlayOptions;
  }
  
  public void addToMap(GoogleMap paramGoogleMap)
  {
    tileOverlay = paramGoogleMap.addTileOverlay(getTileOverlayOptions());
  }
  
  public Object getFeature()
  {
    return tileOverlay;
  }
  
  public TileOverlayOptions getTileOverlayOptions()
  {
    if (tileOverlayOptions == null) {
      tileOverlayOptions = createTileOverlayOptions();
    }
    return tileOverlayOptions;
  }
  
  public void removeFromMap(GoogleMap paramGoogleMap)
  {
    tileOverlay.remove();
  }
  
  public void setPathTemplate(String paramString)
  {
    pathTemplate = paramString;
    if (tileProvider != null) {
      tileProvider.setPathTemplate(paramString);
    }
    if (tileOverlay != null) {
      tileOverlay.clearTileCache();
    }
  }
  
  public void setTileSize(float paramFloat)
  {
    tileSize = paramFloat;
    if (tileProvider != null) {
      tileProvider.setTileSize((int)paramFloat);
    }
  }
  
  public void setZIndex(float paramFloat)
  {
    zIndex = paramFloat;
    if (tileOverlay != null) {
      tileOverlay.setZIndex(paramFloat);
    }
  }
  
  class AIRMapLocalTileProvider
    implements TileProvider
  {
    private static final int BUFFER_SIZE = 16384;
    private String pathTemplate;
    private int tileSize;
    
    public AIRMapLocalTileProvider(int paramInt, String paramString)
    {
      tileSize = paramInt;
      pathTemplate = paramString;
    }
    
    private String getTileFilename(int paramInt1, int paramInt2, int paramInt3)
    {
      return pathTemplate.replace("{x}", Integer.toString(paramInt1)).replace("{y}", Integer.toString(paramInt2)).replace("{z}", Integer.toString(paramInt3));
    }
    
    /* Error */
    private byte[] readTileImage(int paramInt1, int paramInt2, int paramInt3)
    {
      // Byte code:
      //   0: new 61	java/io/File
      //   3: dup
      //   4: aload_0
      //   5: iload_1
      //   6: iload_2
      //   7: iload_3
      //   8: invokespecial 63	com/airbnb/android/react/maps/AirMapLocalTile$AIRMapLocalTileProvider:getTileFilename	(III)Ljava/lang/String;
      //   11: invokespecial 66	java/io/File:<init>	(Ljava/lang/String;)V
      //   14: astore 4
      //   16: new 68	java/io/FileInputStream
      //   19: dup
      //   20: aload 4
      //   22: invokespecial 71	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   25: astore 4
      //   27: new 73	java/io/ByteArrayOutputStream
      //   30: dup
      //   31: invokespecial 74	java/io/ByteArrayOutputStream:<init>	()V
      //   34: astore 7
      //   36: sipush 16384
      //   39: newarray byte
      //   41: astore 8
      //   43: aload 4
      //   45: astore 5
      //   47: aload 7
      //   49: astore 6
      //   51: aload 4
      //   53: aload 8
      //   55: iconst_0
      //   56: sipush 16384
      //   59: invokevirtual 80	java/io/InputStream:read	([BII)I
      //   62: istore_1
      //   63: iload_1
      //   64: iconst_m1
      //   65: if_icmpeq +23 -> 88
      //   68: aload 4
      //   70: astore 5
      //   72: aload 7
      //   74: astore 6
      //   76: aload 7
      //   78: aload 8
      //   80: iconst_0
      //   81: iload_1
      //   82: invokevirtual 84	java/io/ByteArrayOutputStream:write	([BII)V
      //   85: goto -42 -> 43
      //   88: aload 4
      //   90: astore 5
      //   92: aload 7
      //   94: astore 6
      //   96: aload 7
      //   98: invokevirtual 87	java/io/ByteArrayOutputStream:flush	()V
      //   101: aload 4
      //   103: astore 5
      //   105: aload 7
      //   107: astore 6
      //   109: aload 7
      //   111: invokevirtual 91	java/io/ByteArrayOutputStream:toByteArray	()[B
      //   114: astore 8
      //   116: aload 4
      //   118: invokevirtual 94	java/io/InputStream:close	()V
      //   121: aload 7
      //   123: invokevirtual 95	java/io/ByteArrayOutputStream:close	()V
      //   126: aload 8
      //   128: areturn
      //   129: astore 5
      //   131: aload 4
      //   133: astore 8
      //   135: aload 7
      //   137: astore 4
      //   139: aload 5
      //   141: astore 7
      //   143: goto +79 -> 222
      //   146: astore 5
      //   148: aload 4
      //   150: astore 8
      //   152: aload 7
      //   154: astore 4
      //   156: aload 5
      //   158: astore 7
      //   160: goto +108 -> 268
      //   163: astore 7
      //   165: aconst_null
      //   166: astore 6
      //   168: goto +144 -> 312
      //   171: astore 7
      //   173: aconst_null
      //   174: astore 5
      //   176: aload 4
      //   178: astore 8
      //   180: aload 5
      //   182: astore 4
      //   184: goto +38 -> 222
      //   187: astore 7
      //   189: aconst_null
      //   190: astore 5
      //   192: aload 4
      //   194: astore 8
      //   196: aload 5
      //   198: astore 4
      //   200: goto +68 -> 268
      //   203: astore 7
      //   205: aconst_null
      //   206: astore 6
      //   208: aconst_null
      //   209: astore 4
      //   211: goto +101 -> 312
      //   214: astore 7
      //   216: aconst_null
      //   217: astore 8
      //   219: aconst_null
      //   220: astore 4
      //   222: aload 8
      //   224: astore 5
      //   226: aload 4
      //   228: astore 6
      //   230: aload 7
      //   232: invokevirtual 100	java/lang/VirtualMachineError:printStackTrace	()V
      //   235: aload 8
      //   237: ifnull +11 -> 248
      //   240: aload 8
      //   242: invokevirtual 94	java/io/InputStream:close	()V
      //   245: goto +3 -> 248
      //   248: aload 4
      //   250: ifnull +126 -> 376
      //   253: aload 4
      //   255: invokevirtual 95	java/io/ByteArrayOutputStream:close	()V
      //   258: aconst_null
      //   259: areturn
      //   260: astore 7
      //   262: aconst_null
      //   263: astore 8
      //   265: aconst_null
      //   266: astore 4
      //   268: aload 8
      //   270: astore 5
      //   272: aload 4
      //   274: astore 6
      //   276: aload 7
      //   278: invokevirtual 101	java/io/IOException:printStackTrace	()V
      //   281: aload 8
      //   283: ifnull +11 -> 294
      //   286: aload 8
      //   288: invokevirtual 94	java/io/InputStream:close	()V
      //   291: goto +3 -> 294
      //   294: aload 4
      //   296: ifnull +80 -> 376
      //   299: aload 4
      //   301: invokevirtual 95	java/io/ByteArrayOutputStream:close	()V
      //   304: aconst_null
      //   305: areturn
      //   306: astore 7
      //   308: aload 5
      //   310: astore 4
      //   312: aload 4
      //   314: ifnull +11 -> 325
      //   317: aload 4
      //   319: invokevirtual 94	java/io/InputStream:close	()V
      //   322: goto +3 -> 325
      //   325: aload 6
      //   327: ifnull +8 -> 335
      //   330: aload 6
      //   332: invokevirtual 95	java/io/ByteArrayOutputStream:close	()V
      //   335: aload 7
      //   337: athrow
      //   338: astore 4
      //   340: goto -219 -> 121
      //   343: astore 4
      //   345: aload 8
      //   347: areturn
      //   348: astore 5
      //   350: goto -102 -> 248
      //   353: astore 4
      //   355: aconst_null
      //   356: areturn
      //   357: astore 5
      //   359: goto -65 -> 294
      //   362: astore 4
      //   364: aconst_null
      //   365: areturn
      //   366: astore 4
      //   368: goto -43 -> 325
      //   371: astore 4
      //   373: goto -38 -> 335
      //   376: aconst_null
      //   377: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	378	0	this	AIRMapLocalTileProvider
      //   0	378	1	paramInt1	int
      //   0	378	2	paramInt2	int
      //   0	378	3	paramInt3	int
      //   14	304	4	localObject1	Object
      //   338	1	4	localException1	Exception
      //   343	1	4	localException2	Exception
      //   353	1	4	localException3	Exception
      //   362	1	4	localException4	Exception
      //   366	1	4	localException5	Exception
      //   371	1	4	localException6	Exception
      //   45	59	5	localObject2	Object
      //   129	11	5	localOutOfMemoryError1	OutOfMemoryError
      //   146	11	5	localIOException1	java.io.IOException
      //   174	135	5	localObject3	Object
      //   348	1	5	localException7	Exception
      //   357	1	5	localException8	Exception
      //   49	282	6	localObject4	Object
      //   34	125	7	localObject5	Object
      //   163	1	7	localThrowable1	Throwable
      //   171	1	7	localOutOfMemoryError2	OutOfMemoryError
      //   187	1	7	localIOException2	java.io.IOException
      //   203	1	7	localThrowable2	Throwable
      //   214	17	7	localOutOfMemoryError3	OutOfMemoryError
      //   260	17	7	localIOException3	java.io.IOException
      //   306	30	7	localThrowable3	Throwable
      //   41	305	8	localObject6	Object
      // Exception table:
      //   from	to	target	type
      //   51	63	129	java/lang/OutOfMemoryError
      //   76	85	129	java/lang/OutOfMemoryError
      //   96	101	129	java/lang/OutOfMemoryError
      //   109	116	129	java/lang/OutOfMemoryError
      //   51	63	146	java/io/IOException
      //   76	85	146	java/io/IOException
      //   96	101	146	java/io/IOException
      //   109	116	146	java/io/IOException
      //   27	36	163	java/lang/Throwable
      //   27	36	171	java/lang/OutOfMemoryError
      //   27	36	187	java/io/IOException
      //   16	27	203	java/lang/Throwable
      //   16	27	214	java/lang/OutOfMemoryError
      //   16	27	260	java/io/IOException
      //   51	63	306	java/lang/Throwable
      //   76	85	306	java/lang/Throwable
      //   96	101	306	java/lang/Throwable
      //   109	116	306	java/lang/Throwable
      //   230	235	306	java/lang/Throwable
      //   276	281	306	java/lang/Throwable
      //   116	121	338	java/lang/Exception
      //   121	126	343	java/lang/Exception
      //   240	245	348	java/lang/Exception
      //   253	258	353	java/lang/Exception
      //   286	291	357	java/lang/Exception
      //   299	304	362	java/lang/Exception
      //   317	322	366	java/lang/Exception
      //   330	335	371	java/lang/Exception
    }
    
    public Tile getTile(int paramInt1, int paramInt2, int paramInt3)
    {
      byte[] arrayOfByte = readTileImage(paramInt1, paramInt2, paramInt3);
      if (arrayOfByte == null) {
        return TileProvider.NO_TILE;
      }
      return new Tile(tileSize, tileSize, arrayOfByte);
    }
    
    public void setPathTemplate(String paramString)
    {
      pathTemplate = paramString;
    }
    
    public void setTileSize(int paramInt)
    {
      tileSize = paramInt;
    }
  }
}
