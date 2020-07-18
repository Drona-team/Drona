package expo.modules.barcodescanner.scanners;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.barcode.BarcodeDetector.Builder;
import expo.modules.barcodescanner.utils.Frame;
import expo.modules.barcodescanner.utils.FrameFactory;
import expo.modules.barcodescanner.utils.ImageDimensions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerResult;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerSettings;

public class GMVBarCodeScanner
  extends ExpoBarCodeScanner
{
  private BarcodeDetector mBarcodeDetector = new BarcodeDetector.Builder(mContext).setBarcodeFormats(0).build();
  private String source = GMVBarCodeScanner.class.getSimpleName();
  
  public GMVBarCodeScanner(Context paramContext)
  {
    super(paramContext);
  }
  
  private List scan(Frame paramFrame)
  {
    Object localObject1 = mBarcodeDetector;
    try
    {
      localObject1 = ((BarcodeDetector)localObject1).detect(paramFrame.getFrame());
      localObject2 = new ArrayList();
      int k = paramFrame.getDimensions().getWidth();
      int m = paramFrame.getDimensions().getHeight();
      int i = 0;
      for (;;)
      {
        int j = ((SparseArray)localObject1).size();
        if (i >= j) {
          break;
        }
        paramFrame = ((SparseArray)localObject1).get(((SparseArray)localObject1).keyAt(i));
        Object localObject3 = (Barcode)paramFrame;
        paramFrame = new ArrayList();
        Point[] arrayOfPoint = cornerPoints;
        int n = arrayOfPoint.length;
        j = 0;
        while (j < n)
        {
          Point localPoint = arrayOfPoint[j];
          int i1 = x;
          int i2 = y;
          paramFrame.addAll(Arrays.asList(new Integer[] { Integer.valueOf(i1), Integer.valueOf(i2) }));
          j += 1;
        }
        j = format;
        localObject3 = rawValue;
        ((List)localObject2).add(new BarCodeScannerResult(j, (String)localObject3, paramFrame, m, k));
        i += 1;
      }
      return localObject2;
    }
    catch (Exception paramFrame)
    {
      localObject1 = source;
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("Failed to detect barcode: ");
      ((StringBuilder)localObject2).append(paramFrame.getMessage());
      Log.e((String)localObject1, ((StringBuilder)localObject2).toString());
    }
    return Collections.emptyList();
  }
  
  public boolean isAvailable()
  {
    return mBarcodeDetector.isOperational();
  }
  
  public BarCodeScannerResult scan(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      paramArrayOfByte = scan(FrameFactory.buildFrame(paramArrayOfByte, paramInt1, paramInt2, paramInt3));
      paramInt1 = paramArrayOfByte.size();
      if (paramInt1 > 0)
      {
        paramArrayOfByte = paramArrayOfByte.get(0);
        return (BarCodeScannerResult)paramArrayOfByte;
      }
      return null;
    }
    catch (Exception paramArrayOfByte)
    {
      String str = source;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Failed to detect barcode: ");
      localStringBuilder.append(paramArrayOfByte.getMessage());
      Log.e(str, localStringBuilder.toString());
    }
    return null;
  }
  
  public List scanMultiple(Bitmap paramBitmap)
  {
    return scan(FrameFactory.buildFrame(paramBitmap));
  }
  
  public void setSettings(BarCodeScannerSettings paramBarCodeScannerSettings)
  {
    paramBarCodeScannerSettings = parseBarCodeTypesFromSettings(paramBarCodeScannerSettings);
    if (areNewAndOldBarCodeTypesEqual(paramBarCodeScannerSettings)) {
      return;
    }
    int i = 0;
    Iterator localIterator = paramBarCodeScannerSettings.iterator();
    while (localIterator.hasNext()) {
      i |= ((Integer)localIterator.next()).intValue();
    }
    mBarCodeTypes = paramBarCodeScannerSettings;
    if (mBarcodeDetector != null) {
      mBarcodeDetector.release();
    }
    mBarcodeDetector = new BarcodeDetector.Builder(mContext).setBarcodeFormats(i).build();
  }
}
