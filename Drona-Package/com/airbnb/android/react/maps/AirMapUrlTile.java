package com.airbnb.android.react.maps;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

public class AirMapUrlTile
  extends AirMapFeature
{
  private boolean flipY;
  private float maximumZ;
  private float minimumZ;
  private TileOverlay tileOverlay;
  private TileOverlayOptions tileOverlayOptions;
  private AIRMapUrlTileProvider tileProvider;
  private String urlTemplate;
  private float zIndex;
  
  public AirMapUrlTile(Context paramContext)
  {
    super(paramContext);
  }
  
  private TileOverlayOptions createTileOverlayOptions()
  {
    TileOverlayOptions localTileOverlayOptions = new TileOverlayOptions();
    localTileOverlayOptions.zIndex(zIndex);
    tileProvider = new AIRMapUrlTileProvider(256, 256, urlTemplate);
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
  
  public void setFlipY(boolean paramBoolean)
  {
    flipY = paramBoolean;
    if (tileOverlay != null) {
      tileOverlay.clearTileCache();
    }
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
  
  class AIRMapUrlTileProvider
    extends UrlTileProvider
  {
    private String urlTemplate;
    
    public AIRMapUrlTileProvider(int paramInt1, int paramInt2, String paramString)
    {
      super(paramInt2);
      urlTemplate = paramString;
    }
    
    /* Error */
    public java.net.URL getTileUrl(int paramInt1, int paramInt2, int paramInt3)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: astore 7
      //   5: aload_0
      //   6: getfield 15	com/airbnb/android/react/maps/AirMapUrlTile$AIRMapUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapUrlTile;
      //   9: astore 9
      //   11: aload_0
      //   12: astore 8
      //   14: iload_2
      //   15: istore 6
      //   17: aload 8
      //   19: astore 7
      //   21: aload 9
      //   23: invokestatic 31	com/airbnb/android/react/maps/AirMapUrlTile:access$000	(Lcom/airbnb/android/react/maps/AirMapUrlTile;)Z
      //   26: iconst_1
      //   27: if_icmpne +12 -> 39
      //   30: iconst_1
      //   31: iload_3
      //   32: ishl
      //   33: iload_2
      //   34: isub
      //   35: iconst_1
      //   36: isub
      //   37: istore 6
      //   39: aload 8
      //   41: astore 7
      //   43: aload 8
      //   45: getfield 20	com/airbnb/android/react/maps/AirMapUrlTile$AIRMapUrlTileProvider:urlTemplate	Ljava/lang/String;
      //   48: ldc 33
      //   50: iload_1
      //   51: invokestatic 39	java/lang/Integer:toString	(I)Ljava/lang/String;
      //   54: invokevirtual 45	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   57: ldc 47
      //   59: iload 6
      //   61: invokestatic 39	java/lang/Integer:toString	(I)Ljava/lang/String;
      //   64: invokevirtual 45	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   67: ldc 49
      //   69: iload_3
      //   70: invokestatic 39	java/lang/Integer:toString	(I)Ljava/lang/String;
      //   73: invokevirtual 45	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      //   76: astore 10
      //   78: aload 8
      //   80: astore 7
      //   82: aload 8
      //   84: getfield 15	com/airbnb/android/react/maps/AirMapUrlTile$AIRMapUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapUrlTile;
      //   87: astore 9
      //   89: aload 8
      //   91: astore 7
      //   93: aload 9
      //   95: invokestatic 53	com/airbnb/android/react/maps/AirMapUrlTile:access$100	(Lcom/airbnb/android/react/maps/AirMapUrlTile;)F
      //   98: fconst_0
      //   99: fcmpl
      //   100: ifle +42 -> 142
      //   103: iload_3
      //   104: i2f
      //   105: fstore 4
      //   107: aload 8
      //   109: astore 7
      //   111: aload 8
      //   113: getfield 15	com/airbnb/android/react/maps/AirMapUrlTile$AIRMapUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapUrlTile;
      //   116: astore 9
      //   118: aload 8
      //   120: astore 7
      //   122: aload 9
      //   124: invokestatic 53	com/airbnb/android/react/maps/AirMapUrlTile:access$100	(Lcom/airbnb/android/react/maps/AirMapUrlTile;)F
      //   127: fstore 5
      //   129: fload 4
      //   131: fload 5
      //   133: fcmpl
      //   134: ifle +8 -> 142
      //   137: aload 8
      //   139: monitorexit
      //   140: aconst_null
      //   141: areturn
      //   142: aload_0
      //   143: astore 8
      //   145: aload 8
      //   147: astore 7
      //   149: aload 8
      //   151: getfield 15	com/airbnb/android/react/maps/AirMapUrlTile$AIRMapUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapUrlTile;
      //   154: astore 11
      //   156: aload 8
      //   158: astore 9
      //   160: aload 9
      //   162: astore 7
      //   164: aload 11
      //   166: invokestatic 56	com/airbnb/android/react/maps/AirMapUrlTile:access$200	(Lcom/airbnb/android/react/maps/AirMapUrlTile;)F
      //   169: fconst_0
      //   170: fcmpl
      //   171: ifle +42 -> 213
      //   174: iload_3
      //   175: i2f
      //   176: fstore 4
      //   178: aload 9
      //   180: astore 7
      //   182: aload 9
      //   184: getfield 15	com/airbnb/android/react/maps/AirMapUrlTile$AIRMapUrlTileProvider:this$0	Lcom/airbnb/android/react/maps/AirMapUrlTile;
      //   187: astore 11
      //   189: aload 9
      //   191: astore 7
      //   193: aload 11
      //   195: invokestatic 56	com/airbnb/android/react/maps/AirMapUrlTile:access$200	(Lcom/airbnb/android/react/maps/AirMapUrlTile;)F
      //   198: fstore 5
      //   200: fload 4
      //   202: fload 5
      //   204: fcmpg
      //   205: ifge +8 -> 213
      //   208: aload 9
      //   210: monitorexit
      //   211: aconst_null
      //   212: areturn
      //   213: aload 8
      //   215: astore 7
      //   217: new 58	java/net/URL
      //   220: dup
      //   221: aload 10
      //   223: invokespecial 61	java/net/URL:<init>	(Ljava/lang/String;)V
      //   226: astore 9
      //   228: aload 8
      //   230: monitorexit
      //   231: aload 9
      //   233: areturn
      //   234: astore 9
      //   236: aload 8
      //   238: astore 7
      //   240: new 63	java/lang/AssertionError
      //   243: dup
      //   244: aload 9
      //   246: invokespecial 66	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   249: athrow
      //   250: astore 8
      //   252: aload 7
      //   254: monitorexit
      //   255: aload 8
      //   257: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	258	0	this	AIRMapUrlTileProvider
      //   0	258	1	paramInt1	int
      //   0	258	2	paramInt2	int
      //   0	258	3	paramInt3	int
      //   105	96	4	f1	float
      //   127	76	5	f2	float
      //   15	45	6	i	int
      //   3	250	7	localObject1	Object
      //   12	225	8	localAIRMapUrlTileProvider	AIRMapUrlTileProvider
      //   250	6	8	localThrowable	Throwable
      //   9	223	9	localObject2	Object
      //   234	11	9	localMalformedURLException	java.net.MalformedURLException
      //   76	146	10	str	String
      //   154	40	11	localAirMapUrlTile	AirMapUrlTile
      // Exception table:
      //   from	to	target	type
      //   217	228	234	java/net/MalformedURLException
      //   5	11	250	java/lang/Throwable
      //   21	30	250	java/lang/Throwable
      //   43	78	250	java/lang/Throwable
      //   82	89	250	java/lang/Throwable
      //   93	103	250	java/lang/Throwable
      //   111	118	250	java/lang/Throwable
      //   122	129	250	java/lang/Throwable
      //   149	156	250	java/lang/Throwable
      //   164	174	250	java/lang/Throwable
      //   182	189	250	java/lang/Throwable
      //   193	200	250	java/lang/Throwable
      //   217	228	250	java/lang/Throwable
      //   240	250	250	java/lang/Throwable
    }
    
    public void setUrlTemplate(String paramString)
    {
      urlTemplate = paramString;
    }
  }
}
