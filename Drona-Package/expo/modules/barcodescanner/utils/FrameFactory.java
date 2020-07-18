package expo.modules.barcodescanner.utils;

import android.graphics.Bitmap;
import com.google.android.gms.vision.Frame.Builder;
import java.nio.ByteBuffer;

public class FrameFactory
{
  public FrameFactory() {}
  
  public static Frame buildFrame(Bitmap paramBitmap)
  {
    Frame.Builder localBuilder = new Frame.Builder();
    localBuilder.setBitmap(paramBitmap);
    paramBitmap = new ImageDimensions(paramBitmap.getWidth(), paramBitmap.getHeight());
    return new Frame(localBuilder.build(), paramBitmap);
  }
  
  public static Frame buildFrame(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    Frame.Builder localBuilder = new Frame.Builder();
    localBuilder.setImageData(ByteBuffer.wrap(paramArrayOfByte), paramInt1, paramInt2, 17);
    if (paramInt3 != 90)
    {
      if (paramInt3 != 180)
      {
        if (paramInt3 != 270) {
          localBuilder.setRotation(0);
        } else {
          localBuilder.setRotation(3);
        }
      }
      else {
        localBuilder.setRotation(2);
      }
    }
    else {
      localBuilder.setRotation(1);
    }
    paramArrayOfByte = new ImageDimensions(paramInt1, paramInt2, paramInt3);
    return new Frame(localBuilder.build(), paramArrayOfByte);
  }
}
