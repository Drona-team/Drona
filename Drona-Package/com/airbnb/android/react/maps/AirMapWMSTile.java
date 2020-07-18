package com.airbnb.android.react.maps;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

public class AirMapWMSTile
  extends AirMapFeature
{
  private static final double FULL = 4.007501669578488E7D;
  private static final double[] mapBound = { -2.003750834789244E7D, 2.003750834789244E7D };
  private float maximumZ;
  private float minimumZ;
  private float opacity;
  private TileOverlay tileOverlay;
  private TileOverlayOptions tileOverlayOptions;
  private AIRMapGSUrlTileProvider tileProvider;
  private int tileSize;
  private String urlTemplate;
  private float zIndex;
  
  public AirMapWMSTile(Context paramContext)
  {
    super(paramContext);
  }
  
  private TileOverlayOptions createTileOverlayOptions()
  {
    TileOverlayOptions localTileOverlayOptions = new TileOverlayOptions();
    localTileOverlayOptions.zIndex(zIndex);
    localTileOverlayOptions.transparency(1.0F - opacity);
    tileProvider = new AIRMapGSUrlTileProvider(tileSize, tileSize, urlTemplate);
    localTileOverlayOptions.tileProvider((TileProvider)tileProvider);
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
  
  public void setMaximumZ(float paramFloat)
  {
    maximumZ = paramFloat;
    if (tileOverlay != null) {
      tileOverlay.clearTileCache();
    }
  }
  
  public void setMinimumZ(float paramFloat)
  {
    minimumZ = paramFloat;
    if (tileOverlay != null) {
      tileOverlay.clearTileCache();
    }
  }
  
  public void setOpacity(float paramFloat)
  {
    opacity = paramFloat;
    if (tileOverlay != null) {
      tileOverlay.setTransparency(1.0F - paramFloat);
    }
  }
  
  public void setTileSize(int paramInt)
  {
    tileSize = paramInt;
    if (tileOverlay != null) {
      tileOverlay.clearTileCache();
    }
  }
  
  public void setUrlTemplate(String paramString)
  {
    urlTemplate = paramString;
    if (tileProvider != null) {
      tileProvider.setUrlTemplate(paramString);
    }
    if (tileOverlay != null) {
      tileOverlay.clearTileCache();
    }
  }
  
  public void setZIndex(float paramFloat)
  {
    zIndex = paramFloat;
    if (tileOverlay != null) {
      tileOverlay.setZIndex(paramFloat);
    }
  }
  
  class AIRMapGSUrlTileProvider
    extends UrlTileProvider
  {
    private int height;
    private String urlTemplate;
    private int width;
    
    public AIRMapGSUrlTileProvider(int paramInt1, int paramInt2, String paramString)
    {
      super(paramInt2);
      urlTemplate = paramString;
      width = paramInt1;
      height = paramInt2;
    }
    
    private double[] getBoundingBox(int paramInt1, int paramInt2, int paramInt3)
    {
      double d = 4.007501669578488E7D / Math.pow(2.0D, paramInt3);
      return new double[] { AirMapWMSTile.mapBound[0] + paramInt1 * d, AirMapWMSTile.mapBound[1] - (paramInt2 + 1) * d, AirMapWMSTile.mapBound[0] + (paramInt1 + 1) * d, AirMapWMSTile.mapBound[1] - paramInt2 * d };
    }
    
    /* Error */
    public java.net.URL getTileUrl(int paramInt1, int paramInt2, int paramInt3)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 18	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapWMSTile;
      //   6: invokestatic 54	com/airbnb/android/react/maps/AirMapWMSTile:access$000	(Lcom/airbnb/android/react/maps/AirMapWMSTile;)F
      //   9: fconst_0
      //   10: fcmpl
      //   11: ifle +28 -> 39
      //   14: iload_3
      //   15: i2f
      //   16: fstore 4
      //   18: aload_0
      //   19: getfield 18	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapWMSTile;
      //   22: invokestatic 54	com/airbnb/android/react/maps/AirMapWMSTile:access$000	(Lcom/airbnb/android/react/maps/AirMapWMSTile;)F
      //   25: fstore 5
      //   27: fload 4
      //   29: fload 5
      //   31: fcmpl
      //   32: ifle +7 -> 39
      //   35: aload_0
      //   36: monitorexit
      //   37: aconst_null
      //   38: areturn
      //   39: aload_0
      //   40: getfield 18	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapWMSTile;
      //   43: invokestatic 57	com/airbnb/android/react/maps/AirMapWMSTile:access$100	(Lcom/airbnb/android/react/maps/AirMapWMSTile;)F
      //   46: fconst_0
      //   47: fcmpl
      //   48: ifle +28 -> 76
      //   51: iload_3
      //   52: i2f
      //   53: fstore 4
      //   55: aload_0
      //   56: getfield 18	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapWMSTile;
      //   59: invokestatic 57	com/airbnb/android/react/maps/AirMapWMSTile:access$100	(Lcom/airbnb/android/react/maps/AirMapWMSTile;)F
      //   62: fstore 5
      //   64: fload 4
      //   66: fload 5
      //   68: fcmpg
      //   69: ifge +7 -> 76
      //   72: aload_0
      //   73: monitorexit
      //   74: aconst_null
      //   75: areturn
      //   76: aload_0
      //   77: iload_1
      //   78: iload_2
      //   79: iload_3
      //   80: invokespecial 59	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:getBoundingBox	(III)[D
      //   83: astore 6
      //   85: aload_0
      //   86: getfield 23	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:urlTemplate	Ljava/lang/String;
      //   89: ldc 61
      //   91: aload 6
      //   93: iconst_0
      //   94: daload
      //   95: invokestatic 67	java/lang/Double:toString	(D)Ljava/lang/String;
      //   98: invokevirtual 73	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   101: ldc 75
      //   103: aload 6
      //   105: iconst_1
      //   106: daload
      //   107: invokestatic 67	java/lang/Double:toString	(D)Ljava/lang/String;
      //   110: invokevirtual 73	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   113: ldc 77
      //   115: aload 6
      //   117: iconst_2
      //   118: daload
      //   119: invokestatic 67	java/lang/Double:toString	(D)Ljava/lang/String;
      //   122: invokevirtual 73	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   125: ldc 79
      //   127: aload 6
      //   129: iconst_3
      //   130: daload
      //   131: invokestatic 67	java/lang/Double:toString	(D)Ljava/lang/String;
      //   134: invokevirtual 73	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   137: ldc 81
      //   139: aload_0
      //   140: getfield 25	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:width	I
      //   143: invokestatic 86	java/lang/Integer:toString	(I)Ljava/lang/String;
      //   146: invokevirtual 73	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   149: ldc 88
      //   151: aload_0
      //   152: getfield 27	com/airbnb/android/react/maps/AirMapWMSTile$AIRMapGSUrlTileProvider:height	I
      //   155: invokestatic 86	java/lang/Integer:toString	(I)Ljava/lang/String;
      //   158: invokevirtual 73	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   161: astore 6
      //   163: new 90	java/net/URL
      //   166: dup
      //   167: aload 6
      //   169: invokespecial 93	java/net/URL:<init>	(Ljava/lang/String;)V
      //   172: astore 6
      //   174: aload_0
      //   175: monitorexit
      //   176: aload 6
      //   178: areturn
      //   179: astore 6
      //   181: new 95	java/lang/AssertionError
      //   184: dup
      //   185: aload 6
      //   187: invokespecial 98	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   190: athrow
      //   191: astore 6
      //   193: aload_0
      //   194: monitorexit
      //   195: aload 6
      //   197: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	198	0	this	AIRMapGSUrlTileProvider
      //   0	198	1	paramInt1	int
      //   0	198	2	paramInt2	int
      //   0	198	3	paramInt3	int
      //   16	49	4	f1	float
      //   25	42	5	f2	float
      //   83	94	6	localObject	Object
      //   179	7	6	localMalformedURLException	java.net.MalformedURLException
      //   191	5	6	localThrowable	Throwable
      // Exception table:
      //   from	to	target	type
      //   163	174	179	java/net/MalformedURLException
      //   2	14	191	java/lang/Throwable
      //   18	27	191	java/lang/Throwable
      //   39	51	191	java/lang/Throwable
      //   55	64	191	java/lang/Throwable
      //   76	163	191	java/lang/Throwable
      //   163	174	191	java/lang/Throwable
      //   181	191	191	java/lang/Throwable
    }
    
    public void setUrlTemplate(String paramString)
    {
      urlTemplate = paramString;
    }
  }
}
