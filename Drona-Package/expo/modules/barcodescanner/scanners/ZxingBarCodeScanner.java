package expo.modules.barcodescanner.scanners;

import android.content.Context;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerResult;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerSettings;

public class ZxingBarCodeScanner
  extends ExpoBarCodeScanner
{
  private static final Map<BarcodeFormat, Integer> GMV_FROM_ZXING = Collections.unmodifiableMap(new HashMap() {});
  private static final Map<Integer, String> VALID_BARCODE_TYPES = Collections.unmodifiableMap(new HashMap() {});
  private final MultiFormatReader mMultiFormatReader = new MultiFormatReader();
  
  public ZxingBarCodeScanner(Context paramContext)
  {
    super(paramContext);
  }
  
  private LuminanceSource generateSourceFromImageData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new PlanarYUVLuminanceSource(paramArrayOfByte, paramInt1, paramInt2, 0, 0, paramInt1, paramInt2, false);
  }
  
  /* Error */
  private BarCodeScannerResult scan(LuminanceSource paramLuminanceSource)
  {
    // Byte code:
    //   0: new 55	com/google/zxing/BinaryBitmap
    //   3: dup
    //   4: new 57	com/google/zxing/common/HybridBinarizer
    //   7: dup
    //   8: aload_1
    //   9: invokespecial 60	com/google/zxing/common/HybridBinarizer:<init>	(Lcom/google/zxing/LuminanceSource;)V
    //   12: invokespecial 63	com/google/zxing/BinaryBitmap:<init>	(Lcom/google/zxing/Binarizer;)V
    //   15: astore_1
    //   16: aload_0
    //   17: getfield 40	expo/modules/barcodescanner/scanners/ZxingBarCodeScanner:mMultiFormatReader	Lcom/google/zxing/MultiFormatReader;
    //   20: astore_2
    //   21: aload_2
    //   22: aload_1
    //   23: invokevirtual 67	com/google/zxing/MultiFormatReader:decodeWithState	(Lcom/google/zxing/BinaryBitmap;)Lcom/google/zxing/Result;
    //   26: astore_2
    //   27: goto +21 -> 48
    //   30: astore_2
    //   31: goto +6 -> 37
    //   34: astore_2
    //   35: aconst_null
    //   36: astore_1
    //   37: aload_2
    //   38: invokevirtual 70	java/lang/Throwable:printStackTrace	()V
    //   41: goto +5 -> 46
    //   44: aconst_null
    //   45: astore_1
    //   46: aconst_null
    //   47: astore_2
    //   48: aload_1
    //   49: ifnull +56 -> 105
    //   52: aload_2
    //   53: ifnonnull +5 -> 58
    //   56: aconst_null
    //   57: areturn
    //   58: new 72	java/util/ArrayList
    //   61: dup
    //   62: invokespecial 73	java/util/ArrayList:<init>	()V
    //   65: astore_3
    //   66: new 75	org/unimodules/interfaces/barcodescanner/BarCodeScannerResult
    //   69: dup
    //   70: getstatic 31	expo/modules/barcodescanner/scanners/ZxingBarCodeScanner:GMV_FROM_ZXING	Ljava/util/Map;
    //   73: aload_2
    //   74: invokevirtual 81	com/google/zxing/Result:getBarcodeFormat	()Lcom/google/zxing/BarcodeFormat;
    //   77: invokeinterface 87 2 0
    //   82: checkcast 89	java/lang/Integer
    //   85: invokevirtual 93	java/lang/Integer:intValue	()I
    //   88: aload_2
    //   89: invokevirtual 97	com/google/zxing/Result:getText	()Ljava/lang/String;
    //   92: aload_3
    //   93: aload_1
    //   94: invokevirtual 100	com/google/zxing/BinaryBitmap:getHeight	()I
    //   97: aload_1
    //   98: invokevirtual 103	com/google/zxing/BinaryBitmap:getWidth	()I
    //   101: invokespecial 106	org/unimodules/interfaces/barcodescanner/BarCodeScannerResult:<init>	(ILjava/lang/String;Ljava/util/List;II)V
    //   104: areturn
    //   105: aconst_null
    //   106: areturn
    //   107: astore_1
    //   108: goto -64 -> 44
    //   111: astore_2
    //   112: goto -66 -> 46
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	115	0	this	ZxingBarCodeScanner
    //   0	115	1	paramLuminanceSource	LuminanceSource
    //   20	7	2	localObject1	Object
    //   30	1	2	localThrowable1	Throwable
    //   34	4	2	localThrowable2	Throwable
    //   47	42	2	localObject2	Object
    //   111	1	2	localNotFoundException	com.google.zxing.NotFoundException
    //   65	28	3	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   21	27	30	java/lang/Throwable
    //   0	16	34	java/lang/Throwable
    //   0	16	107	com/google/zxing/NotFoundException
    //   21	27	111	com/google/zxing/NotFoundException
  }
  
  public boolean isAvailable()
  {
    return true;
  }
  
  public BarCodeScannerResult scan(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    int j = paramInt1;
    int i = paramInt2;
    if (paramInt3 == 0)
    {
      arrayOfByte = new byte[paramArrayOfByte.length];
      paramInt3 = 0;
      while (paramInt3 < paramInt2)
      {
        i = 0;
        while (i < paramInt1)
        {
          arrayOfByte[(i * paramInt2 + paramInt2 - paramInt3 - 1)] = paramArrayOfByte[(paramInt3 * paramInt1 + i)];
          i += 1;
        }
        paramInt3 += 1;
      }
      paramInt1 += paramInt2;
      i = paramInt1 - paramInt2;
      j = paramInt1 - i;
    }
    return scan(generateSourceFromImageData(arrayOfByte, j, i));
  }
  
  public List scanMultiple(Bitmap paramBitmap)
  {
    int[] arrayOfInt = new int[paramBitmap.getWidth() * paramBitmap.getHeight()];
    paramBitmap.getPixels(arrayOfInt, 0, paramBitmap.getWidth(), 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
    paramBitmap = scan(new RGBLuminanceSource(paramBitmap.getWidth(), paramBitmap.getHeight(), arrayOfInt));
    if (paramBitmap == null) {
      return Collections.emptyList();
    }
    return Collections.singletonList(paramBitmap);
  }
  
  public void setSettings(BarCodeScannerSettings paramBarCodeScannerSettings)
  {
    if (areNewAndOldBarCodeTypesEqual(parseBarCodeTypesFromSettings(paramBarCodeScannerSettings))) {
      return;
    }
    paramBarCodeScannerSettings = new EnumMap(DecodeHintType.class);
    EnumSet localEnumSet = EnumSet.noneOf(BarcodeFormat.class);
    if (mBarCodeTypes != null)
    {
      Iterator localIterator = mBarCodeTypes.iterator();
      while (localIterator.hasNext())
      {
        int i = ((Integer)localIterator.next()).intValue();
        String str = (String)VALID_BARCODE_TYPES.get(Integer.valueOf(i));
        if (str != null) {
          localEnumSet.add(BarcodeFormat.valueOf(str));
        }
      }
    }
    paramBarCodeScannerSettings.put((Enum)DecodeHintType.POSSIBLE_FORMATS, localEnumSet);
    mMultiFormatReader.setHints(paramBarCodeScannerSettings);
  }
}
