package com.google.android.exoplayer2.text.supplement;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region.Op;
import android.util.SparseArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DvbParser
{
  private static final int DATA_TYPE_24_TABLE_DATA = 32;
  private static final int DATA_TYPE_28_TABLE_DATA = 33;
  private static final int DATA_TYPE_2BP_CODE_STRING = 16;
  private static final int DATA_TYPE_48_TABLE_DATA = 34;
  private static final int DATA_TYPE_4BP_CODE_STRING = 17;
  private static final int DATA_TYPE_8BP_CODE_STRING = 18;
  private static final int DATA_TYPE_END_LINE = 240;
  private static final int OBJECT_CODING_PIXELS = 0;
  private static final int OBJECT_CODING_STRING = 1;
  private static final String PAGE_KEY = "DvbParser";
  private static final int PAGE_STATE_NORMAL = 0;
  private static final int REGION_DEPTH_4_BIT = 2;
  private static final int REGION_DEPTH_8_BIT = 3;
  private static final int SEGMENT_TYPE_CLUT_DEFINITION = 18;
  private static final int SEGMENT_TYPE_DISPLAY_DEFINITION = 20;
  private static final int SEGMENT_TYPE_OBJECT_DATA = 19;
  private static final int SEGMENT_TYPE_PAGE_COMPOSITION = 16;
  private static final int SEGMENT_TYPE_REGION_COMPOSITION = 17;
  private static final byte[] defaultMap2To4 = { 0, 7, 8, 15 };
  private static final byte[] defaultMap2To8 = { 0, 119, -120, -1 };
  private static final byte[] defaultMap4To8 = { 0, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1 };
  private Bitmap bitmap;
  private final Canvas canvas;
  private final ClutDefinition defaultClutDefinition;
  private final DisplayDefinition defaultDisplayDefinition;
  private final Paint defaultPaint = new Paint();
  private final Paint fillRegionPaint;
  private final SubtitleService subtitleService;
  
  public DvbParser(int paramInt1, int paramInt2)
  {
    defaultPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    defaultPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    defaultPaint.setPathEffect(null);
    fillRegionPaint = new Paint();
    fillRegionPaint.setStyle(Paint.Style.FILL);
    fillRegionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
    fillRegionPaint.setPathEffect(null);
    canvas = new Canvas();
    defaultDisplayDefinition = new DisplayDefinition(719, 575, 0, 719, 0, 575);
    defaultClutDefinition = new ClutDefinition(0, generateDefault2BitClutEntries(), generateDefault4BitClutEntries(), generateDefault8BitClutEntries());
    subtitleService = new SubtitleService(paramInt1, paramInt2);
  }
  
  private static byte[] buildClutMapTable(int paramInt1, int paramInt2, ParsableBitArray paramParsableBitArray)
  {
    byte[] arrayOfByte = new byte[paramInt1];
    int i = 0;
    while (i < paramInt1)
    {
      arrayOfByte[i] = ((byte)paramParsableBitArray.readBits(paramInt2));
      i += 1;
    }
    return arrayOfByte;
  }
  
  private static int[] generateDefault2BitClutEntries()
  {
    return new int[] { 0, -1, -16777216, -8421505 };
  }
  
  private static int[] generateDefault4BitClutEntries()
  {
    int[] arrayOfInt = new int[16];
    arrayOfInt[0] = 0;
    int i = 1;
    while (i < arrayOfInt.length)
    {
      int j;
      int k;
      int m;
      if (i < 8)
      {
        if ((i & 0x1) != 0) {
          j = 255;
        } else {
          j = 0;
        }
        if ((i & 0x2) != 0) {
          k = 255;
        } else {
          k = 0;
        }
        if ((i & 0x4) != 0) {
          m = 255;
        } else {
          m = 0;
        }
        arrayOfInt[i] = getColor(255, j, k, m);
      }
      else
      {
        m = 127;
        if ((i & 0x1) != 0) {
          j = 127;
        } else {
          j = 0;
        }
        if ((i & 0x2) != 0) {
          k = 127;
        } else {
          k = 0;
        }
        if ((i & 0x4) == 0) {
          m = 0;
        }
        arrayOfInt[i] = getColor(255, j, k, m);
      }
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static int[] generateDefault8BitClutEntries()
  {
    int[] arrayOfInt = new int['?'];
    arrayOfInt[0] = 0;
    int j = 0;
    while (j < arrayOfInt.length)
    {
      int m = 255;
      int i;
      int k;
      if (j < 8)
      {
        if ((j & 0x1) != 0) {
          i = 255;
        } else {
          i = 0;
        }
        if ((j & 0x2) != 0) {
          k = 255;
        } else {
          k = 0;
        }
        if ((j & 0x4) == 0) {
          m = 0;
        }
        arrayOfInt[j] = getColor(63, i, k, m);
      }
      else
      {
        m = j & 0x88;
        k = 170;
        i = 85;
        int n;
        int i1;
        int i2;
        if (m != 0)
        {
          if (m != 8)
          {
            k = 43;
            if (m != 128)
            {
              if (m == 136)
              {
                if ((j & 0x1) != 0) {
                  m = 43;
                } else {
                  m = 0;
                }
                if ((j & 0x10) != 0) {
                  n = 85;
                } else {
                  n = 0;
                }
                if ((j & 0x2) != 0) {
                  i1 = 43;
                } else {
                  i1 = 0;
                }
                if ((j & 0x20) != 0) {
                  i2 = 85;
                } else {
                  i2 = 0;
                }
                if ((j & 0x4) == 0) {
                  k = 0;
                }
                if ((j & 0x40) == 0) {
                  i = 0;
                }
                arrayOfInt[j] = getColor(255, m + n, i1 + i2, k + i);
              }
            }
            else
            {
              if ((j & 0x1) != 0) {
                m = 43;
              } else {
                m = 0;
              }
              if ((j & 0x10) != 0) {
                n = 85;
              } else {
                n = 0;
              }
              if ((j & 0x2) != 0) {
                i1 = 43;
              } else {
                i1 = 0;
              }
              if ((j & 0x20) != 0) {
                i2 = 85;
              } else {
                i2 = 0;
              }
              if ((j & 0x4) == 0) {
                k = 0;
              }
              if ((j & 0x40) == 0) {
                i = 0;
              }
              arrayOfInt[j] = getColor(255, m + 127 + n, i1 + 127 + i2, k + 127 + i);
            }
          }
          else
          {
            if ((j & 0x1) != 0) {
              m = 85;
            } else {
              m = 0;
            }
            if ((j & 0x10) != 0) {
              n = 170;
            } else {
              n = 0;
            }
            if ((j & 0x2) != 0) {
              i1 = 85;
            } else {
              i1 = 0;
            }
            if ((j & 0x20) != 0) {
              i2 = 170;
            } else {
              i2 = 0;
            }
            if ((j & 0x4) == 0) {
              i = 0;
            }
            if ((j & 0x40) == 0) {
              k = 0;
            }
            arrayOfInt[j] = getColor(127, m + n, i1 + i2, i + k);
          }
        }
        else
        {
          if ((j & 0x1) != 0) {
            m = 85;
          } else {
            m = 0;
          }
          if ((j & 0x10) != 0) {
            n = 170;
          } else {
            n = 0;
          }
          if ((j & 0x2) != 0) {
            i1 = 85;
          } else {
            i1 = 0;
          }
          if ((j & 0x20) != 0) {
            i2 = 170;
          } else {
            i2 = 0;
          }
          if ((j & 0x4) == 0) {
            i = 0;
          }
          if ((j & 0x40) == 0) {
            k = 0;
          }
          arrayOfInt[j] = getColor(255, m + n, i1 + i2, i + k);
        }
      }
      j += 1;
    }
    return arrayOfInt;
  }
  
  private static int getColor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return paramInt1 << 24 | paramInt2 << 16 | paramInt3 << 8 | paramInt4;
  }
  
  private static int paint2BitPixelCodeString(ParsableBitArray paramParsableBitArray, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, Paint paramPaint, Canvas paramCanvas)
  {
    int k = 0;
    int j = paramInt1;
    for (;;)
    {
      paramInt1 = paramParsableBitArray.readBits(2);
      int i;
      if (paramInt1 != 0)
      {
        i = 1;
      }
      else
      {
        if (paramParsableBitArray.readBit())
        {
          i = 3 + paramParsableBitArray.readBits(3);
          paramInt1 = paramParsableBitArray.readBits(2);
        }
        for (;;)
        {
          break label174;
          if (paramParsableBitArray.readBit())
          {
            paramInt1 = 0;
            break;
          }
          switch (paramParsableBitArray.readBits(2))
          {
          default: 
            paramInt1 = 0;
            i = 0;
            break;
          case 3: 
            i = paramParsableBitArray.readBits(8) + 29;
            paramInt1 = paramParsableBitArray.readBits(2);
            break;
          case 2: 
            i = paramParsableBitArray.readBits(4) + 12;
            paramInt1 = paramParsableBitArray.readBits(2);
            break;
          case 1: 
            paramInt1 = 0;
            i = 2;
            break;
          case 0: 
            paramInt1 = 0;
            i = 0;
            k = 1;
          }
        }
      }
      label174:
      if ((i != 0) && (paramPaint != null))
      {
        int m = paramInt1;
        if (paramArrayOfByte != null) {
          m = paramArrayOfByte[paramInt1];
        }
        paramPaint.setColor(paramArrayOfInt[m]);
        paramCanvas.drawRect(j, paramInt2, j + i, paramInt2 + 1, paramPaint);
      }
      j += i;
      if (k != 0) {
        return j;
      }
    }
  }
  
  private static int paint4BitPixelCodeString(ParsableBitArray paramParsableBitArray, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, Paint paramPaint, Canvas paramCanvas)
  {
    int k = 0;
    int j = paramInt1;
    for (;;)
    {
      paramInt1 = paramParsableBitArray.readBits(4);
      if (paramInt1 != 0) {}
      int i;
      for (;;)
      {
        i = 1;
        break;
        if (!paramParsableBitArray.readBit())
        {
          paramInt1 = paramParsableBitArray.readBits(3);
          if (paramInt1 != 0)
          {
            i = paramInt1 + 2;
            paramInt1 = 0;
            break;
          }
          paramInt1 = 0;
          i = 0;
          k = 1;
          break;
        }
        if (!paramParsableBitArray.readBit())
        {
          i = paramParsableBitArray.readBits(2) + 4;
          paramInt1 = paramParsableBitArray.readBits(4);
        }
        for (;;)
        {
          break;
          switch (paramParsableBitArray.readBits(2))
          {
          default: 
            paramInt1 = 0;
            i = 0;
            break;
          case 3: 
            i = paramParsableBitArray.readBits(8) + 25;
            paramInt1 = paramParsableBitArray.readBits(4);
            break;
          case 2: 
            i = paramParsableBitArray.readBits(4) + 9;
            paramInt1 = paramParsableBitArray.readBits(4);
          }
        }
        paramInt1 = 0;
        i = 2;
        break;
        paramInt1 = 0;
      }
      if ((i != 0) && (paramPaint != null))
      {
        int m = paramInt1;
        if (paramArrayOfByte != null) {
          m = paramArrayOfByte[paramInt1];
        }
        paramPaint.setColor(paramArrayOfInt[m]);
        paramCanvas.drawRect(j, paramInt2, j + i, paramInt2 + 1, paramPaint);
      }
      j += i;
      if (k != 0) {
        return j;
      }
    }
  }
  
  private static int paint8BitPixelCodeString(ParsableBitArray paramParsableBitArray, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, Paint paramPaint, Canvas paramCanvas)
  {
    int k = 0;
    int j = paramInt1;
    for (;;)
    {
      paramInt1 = paramParsableBitArray.readBits(8);
      int i;
      if (paramInt1 != 0)
      {
        i = 1;
      }
      else if (!paramParsableBitArray.readBit())
      {
        i = paramParsableBitArray.readBits(7);
        if (i != 0)
        {
          paramInt1 = 0;
        }
        else
        {
          paramInt1 = 0;
          i = 0;
          k = 1;
        }
      }
      else
      {
        i = paramParsableBitArray.readBits(7);
        paramInt1 = paramParsableBitArray.readBits(8);
      }
      if ((i != 0) && (paramPaint != null))
      {
        int m = paramInt1;
        if (paramArrayOfByte != null) {
          m = paramArrayOfByte[paramInt1];
        }
        paramPaint.setColor(paramArrayOfInt[m]);
        paramCanvas.drawRect(j, paramInt2, j + i, paramInt2 + 1, paramPaint);
      }
      j += i;
      if (k != 0) {
        return j;
      }
    }
  }
  
  private static void paintPixelDataSubBlock(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, Paint paramPaint, Canvas paramCanvas)
  {
    ParsableBitArray localParsableBitArray = new ParsableBitArray(paramArrayOfByte);
    int j = paramInt2;
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte1 = null;
    int i = paramInt3;
    paramInt3 = j;
    while (localParsableBitArray.bitsLeft() != 0)
    {
      j = localParsableBitArray.readBits(8);
      if (j != 240)
      {
        switch (j)
        {
        default: 
          switch (j)
          {
          default: 
            break;
          case 34: 
            arrayOfByte2 = buildClutMapTable(16, 8, localParsableBitArray);
            break;
          case 33: 
            arrayOfByte2 = buildClutMapTable(4, 8, localParsableBitArray);
            break;
          case 32: 
            arrayOfByte1 = buildClutMapTable(4, 4, localParsableBitArray);
          }
          break;
        case 18: 
          paramInt3 = paint8BitPixelCodeString(localParsableBitArray, paramArrayOfInt, null, paramInt3, i, paramPaint, paramCanvas);
        case 17: 
        case 16: 
          for (;;)
          {
            break;
            if (paramInt1 == 3) {
              paramArrayOfByte = defaultMap4To8;
            } else {
              paramArrayOfByte = null;
            }
            paramInt3 = paint4BitPixelCodeString(localParsableBitArray, paramArrayOfInt, paramArrayOfByte, paramInt3, i, paramPaint, paramCanvas);
            localParsableBitArray.byteAlign();
            continue;
            if (paramInt1 == 3) {
              if (arrayOfByte2 == null) {
                paramArrayOfByte = defaultMap2To8;
              } else {
                paramArrayOfByte = arrayOfByte2;
              }
            }
            for (;;)
            {
              break;
              if (paramInt1 == 2)
              {
                if (arrayOfByte1 == null) {
                  paramArrayOfByte = defaultMap2To4;
                } else {
                  paramArrayOfByte = arrayOfByte1;
                }
              }
              else {
                paramArrayOfByte = null;
              }
            }
            paramInt3 = paint2BitPixelCodeString(localParsableBitArray, paramArrayOfInt, paramArrayOfByte, paramInt3, i, paramPaint, paramCanvas);
            localParsableBitArray.byteAlign();
          }
        }
      }
      else
      {
        i += 2;
        paramInt3 = paramInt2;
      }
    }
  }
  
  private static void paintPixelDataSubBlocks(ObjectData paramObjectData, ClutDefinition paramClutDefinition, int paramInt1, int paramInt2, int paramInt3, Paint paramPaint, Canvas paramCanvas)
  {
    if (paramInt1 == 3) {
      paramClutDefinition = clutEntries8Bit;
    } else if (paramInt1 == 2) {
      paramClutDefinition = clutEntries4Bit;
    } else {
      paramClutDefinition = clutEntries2Bit;
    }
    paintPixelDataSubBlock(topFieldData, paramClutDefinition, paramInt1, paramInt2, paramInt3, paramPaint, paramCanvas);
    paintPixelDataSubBlock(bottomFieldData, paramClutDefinition, paramInt1, paramInt2, paramInt3 + 1, paramPaint, paramCanvas);
  }
  
  private static ClutDefinition parseClutDefinition(ParsableBitArray paramParsableBitArray, int paramInt)
  {
    int n = paramParsableBitArray.readBits(8);
    paramParsableBitArray.skipBits(8);
    paramInt -= 2;
    int[] arrayOfInt2 = generateDefault2BitClutEntries();
    int[] arrayOfInt3 = generateDefault4BitClutEntries();
    int[] arrayOfInt4 = generateDefault8BitClutEntries();
    while (paramInt > 0)
    {
      int i1 = paramParsableBitArray.readBits(8);
      int i = paramParsableBitArray.readBits(8);
      paramInt -= 2;
      int[] arrayOfInt1;
      if ((i & 0x80) != 0) {
        arrayOfInt1 = arrayOfInt2;
      } else if ((i & 0x40) != 0) {
        arrayOfInt1 = arrayOfInt3;
      } else {
        arrayOfInt1 = arrayOfInt4;
      }
      if ((i & 0x1) != 0)
      {
        m = paramParsableBitArray.readBits(8);
        j = paramParsableBitArray.readBits(8);
        k = paramParsableBitArray.readBits(8);
        i = paramParsableBitArray.readBits(8);
        paramInt -= 4;
      }
      else
      {
        m = paramParsableBitArray.readBits(6) << 2;
        j = paramParsableBitArray.readBits(4) << 4;
        k = paramParsableBitArray.readBits(4) << 4;
        i = paramParsableBitArray.readBits(2);
        paramInt -= 2;
        i <<= 6;
      }
      if (m == 0)
      {
        j = 0;
        k = 0;
        i = 255;
      }
      i = (byte)(255 - (i & 0xFF));
      double d1 = m;
      double d2 = j - 128;
      int j = (int)(d1 + 1.402D * d2);
      double d3 = k - 128;
      int k = (int)(d1 - 0.34414D * d3 - d2 * 0.71414D);
      int m = (int)(d1 + d3 * 1.772D);
      arrayOfInt1[i1] = getColor(i, Util.constrainValue(j, 0, 255), Util.constrainValue(k, 0, 255), Util.constrainValue(m, 0, 255));
    }
    return new ClutDefinition(n, arrayOfInt2, arrayOfInt3, arrayOfInt4);
  }
  
  private static DisplayDefinition parseDisplayDefinition(ParsableBitArray paramParsableBitArray)
  {
    paramParsableBitArray.skipBits(4);
    boolean bool = paramParsableBitArray.readBit();
    paramParsableBitArray.skipBits(3);
    int n = paramParsableBitArray.readBits(16);
    int i1 = paramParsableBitArray.readBits(16);
    int i;
    int j;
    int k;
    int m;
    if (bool)
    {
      i = paramParsableBitArray.readBits(16);
      j = paramParsableBitArray.readBits(16);
      k = paramParsableBitArray.readBits(16);
      m = paramParsableBitArray.readBits(16);
    }
    else
    {
      j = n;
      m = i1;
      i = 0;
      k = 0;
    }
    return new DisplayDefinition(n, i1, i, j, k, m);
  }
  
  private static ObjectData parseObjectData(ParsableBitArray paramParsableBitArray)
  {
    int i = paramParsableBitArray.readBits(16);
    paramParsableBitArray.skipBits(4);
    int j = paramParsableBitArray.readBits(2);
    boolean bool = paramParsableBitArray.readBit();
    paramParsableBitArray.skipBits(1);
    Object localObject = null;
    byte[] arrayOfByte1 = null;
    if (j == 1)
    {
      paramParsableBitArray.skipBits(paramParsableBitArray.readBits(8) * 16);
    }
    else if (j == 0)
    {
      j = paramParsableBitArray.readBits(16);
      int k = paramParsableBitArray.readBits(16);
      if (j > 0)
      {
        arrayOfByte1 = new byte[j];
        paramParsableBitArray.readBytes(arrayOfByte1, 0, j);
      }
      localObject = arrayOfByte1;
      if (k > 0)
      {
        byte[] arrayOfByte2 = new byte[k];
        paramParsableBitArray.readBytes(arrayOfByte2, 0, k);
        localObject = arrayOfByte1;
        paramParsableBitArray = arrayOfByte2;
        break label125;
      }
    }
    paramParsableBitArray = localObject;
    label125:
    return new ObjectData(i, bool, localObject, paramParsableBitArray);
  }
  
  private static PageComposition parsePageComposition(ParsableBitArray paramParsableBitArray, int paramInt)
  {
    int i = paramParsableBitArray.readBits(8);
    int j = paramParsableBitArray.readBits(4);
    int k = paramParsableBitArray.readBits(2);
    paramParsableBitArray.skipBits(2);
    paramInt -= 2;
    SparseArray localSparseArray = new SparseArray();
    while (paramInt > 0)
    {
      int m = paramParsableBitArray.readBits(8);
      paramParsableBitArray.skipBits(8);
      int n = paramParsableBitArray.readBits(16);
      int i1 = paramParsableBitArray.readBits(16);
      paramInt -= 6;
      localSparseArray.put(m, new PageRegion(n, i1));
    }
    return new PageComposition(i, j, k, localSparseArray);
  }
  
  private static RegionComposition parseRegionComposition(ParsableBitArray paramParsableBitArray, int paramInt)
  {
    int k = paramParsableBitArray.readBits(8);
    paramParsableBitArray.skipBits(4);
    boolean bool = paramParsableBitArray.readBit();
    paramParsableBitArray.skipBits(3);
    int m = paramParsableBitArray.readBits(16);
    int n = paramParsableBitArray.readBits(16);
    int i1 = paramParsableBitArray.readBits(3);
    int i2 = paramParsableBitArray.readBits(3);
    paramParsableBitArray.skipBits(2);
    int i3 = paramParsableBitArray.readBits(8);
    int i4 = paramParsableBitArray.readBits(8);
    int i5 = paramParsableBitArray.readBits(4);
    int i6 = paramParsableBitArray.readBits(2);
    paramParsableBitArray.skipBits(2);
    paramInt -= 10;
    SparseArray localSparseArray = new SparseArray();
    while (paramInt > 0)
    {
      int i7 = paramParsableBitArray.readBits(16);
      int i8 = paramParsableBitArray.readBits(2);
      int i9 = paramParsableBitArray.readBits(2);
      int i10 = paramParsableBitArray.readBits(12);
      paramParsableBitArray.skipBits(4);
      int i11 = paramParsableBitArray.readBits(12);
      paramInt -= 6;
      int i;
      int j;
      if ((i8 != 1) && (i8 != 2))
      {
        i = 0;
        j = 0;
      }
      else
      {
        paramInt -= 2;
        i = paramParsableBitArray.readBits(8);
        j = paramParsableBitArray.readBits(8);
      }
      localSparseArray.put(i7, new RegionObject(i8, i9, i10, i11, i, j));
    }
    return new RegionComposition(k, bool, m, n, i1, i2, i3, i4, i5, i6, localSparseArray);
  }
  
  private static void parseSubtitlingSegment(ParsableBitArray paramParsableBitArray, SubtitleService paramSubtitleService)
  {
    int i = paramParsableBitArray.readBits(8);
    int j = paramParsableBitArray.readBits(16);
    int k = paramParsableBitArray.readBits(16);
    int m = paramParsableBitArray.getBytePosition();
    if (k * 8 > paramParsableBitArray.bitsLeft())
    {
      Log.w("DvbParser", "Data field length exceeds limit");
      paramParsableBitArray.skipBits(paramParsableBitArray.bitsLeft());
      return;
    }
    Object localObject1;
    Object localObject2;
    switch (i)
    {
    default: 
      break;
    case 20: 
      if (j == subtitlePageId) {
        displayDefinition = parseDisplayDefinition(paramParsableBitArray);
      }
      break;
    case 19: 
      if (j == subtitlePageId)
      {
        localObject1 = parseObjectData(paramParsableBitArray);
        objects.put(increment, localObject1);
      }
      else if (j == ancillaryPageId)
      {
        localObject1 = parseObjectData(paramParsableBitArray);
        ancillaryObjects.put(increment, localObject1);
      }
      break;
    case 18: 
      if (j == subtitlePageId)
      {
        localObject1 = parseClutDefinition(paramParsableBitArray, k);
        cluts.put(languageIndex, localObject1);
      }
      else if (j == ancillaryPageId)
      {
        localObject1 = parseClutDefinition(paramParsableBitArray, k);
        ancillaryCluts.put(languageIndex, localObject1);
      }
      break;
    case 17: 
      localObject1 = pageComposition;
      if ((j == subtitlePageId) && (localObject1 != null))
      {
        localObject2 = parseRegionComposition(paramParsableBitArray, k);
        if (state == 0) {
          ((RegionComposition)localObject2).mergeFrom((RegionComposition)regions.get(regions));
        }
        regions.put(regions, localObject2);
      }
      break;
    case 16: 
      if (j == subtitlePageId)
      {
        localObject1 = pageComposition;
        localObject2 = parsePageComposition(paramParsableBitArray, k);
        if (state != 0)
        {
          pageComposition = ((PageComposition)localObject2);
          regions.clear();
          cluts.clear();
          objects.clear();
        }
        else if ((localObject1 != null) && (version != version))
        {
          pageComposition = ((PageComposition)localObject2);
        }
      }
      break;
    }
    paramParsableBitArray.skipBytes(m + k - paramParsableBitArray.getBytePosition());
  }
  
  public List decode(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte = new ParsableBitArray(paramArrayOfByte, paramInt);
    while ((paramArrayOfByte.bitsLeft() >= 48) && (paramArrayOfByte.readBits(8) == 15)) {
      parseSubtitlingSegment(paramArrayOfByte, subtitleService);
    }
    if (subtitleService.pageComposition == null) {
      return Collections.emptyList();
    }
    DisplayDefinition localDisplayDefinition;
    if (subtitleService.displayDefinition != null) {
      localDisplayDefinition = subtitleService.displayDefinition;
    } else {
      localDisplayDefinition = defaultDisplayDefinition;
    }
    if ((bitmap == null) || (width + 1 != bitmap.getWidth()) || (height + 1 != bitmap.getHeight()))
    {
      bitmap = Bitmap.createBitmap(width + 1, height + 1, Bitmap.Config.ARGB_8888);
      canvas.setBitmap(bitmap);
    }
    ArrayList localArrayList = new ArrayList();
    SparseArray localSparseArray1 = subtitleService.pageComposition.regions;
    int i = 0;
    while (i < localSparseArray1.size())
    {
      paramArrayOfByte = (PageRegion)localSparseArray1.valueAt(i);
      paramInt = localSparseArray1.keyAt(i);
      RegionComposition localRegionComposition = (RegionComposition)subtitleService.regions.get(paramInt);
      int j = horizontalAddress + horizontalPositionMinimum;
      int k = verticalAddress + verticalPositionMinimum;
      paramInt = Math.min(width + j, horizontalPositionMaximum);
      int m = Math.min(height + k, verticalPositionMaximum);
      paramArrayOfByte = canvas;
      float f1 = j;
      float f2 = k;
      paramArrayOfByte.clipRect(f1, f2, paramInt, m, Region.Op.REPLACE);
      Object localObject1 = (ClutDefinition)subtitleService.cluts.get(clutId);
      paramArrayOfByte = (byte[])localObject1;
      if (localObject1 == null)
      {
        localObject1 = (ClutDefinition)subtitleService.ancillaryCluts.get(clutId);
        paramArrayOfByte = (byte[])localObject1;
        if (localObject1 == null) {
          paramArrayOfByte = defaultClutDefinition;
        }
      }
      SparseArray localSparseArray2 = regionObjects;
      paramInt = 0;
      while (paramInt < localSparseArray2.size())
      {
        m = localSparseArray2.keyAt(paramInt);
        RegionObject localRegionObject = (RegionObject)localSparseArray2.valueAt(paramInt);
        Object localObject2 = (ObjectData)subtitleService.objects.get(m);
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = (ObjectData)subtitleService.ancillaryObjects.get(m);
        }
        if (localObject1 != null)
        {
          if (nonModifyingColorFlag) {}
          for (localObject2 = null;; localObject2 = defaultPaint) {
            break;
          }
          paintPixelDataSubBlocks((ObjectData)localObject1, paramArrayOfByte, depth, horizontalPosition + j, k + verticalPosition, (Paint)localObject2, canvas);
        }
        paramInt += 1;
      }
      if (fillFlag)
      {
        if (depth == 3) {
          paramInt = clutEntries8Bit[pixelCode8Bit];
        } else if (depth == 2) {
          paramInt = clutEntries4Bit[pixelCode4Bit];
        } else {
          paramInt = clutEntries2Bit[pixelCode2Bit];
        }
        fillRegionPaint.setColor(paramInt);
        canvas.drawRect(f1, f2, width + j, height + k, fillRegionPaint);
      }
      localArrayList.add(new Cue(Bitmap.createBitmap(bitmap, j, k, width, height), f1 / width, 0, f2 / height, 0, width / width, height / height));
      canvas.drawColor(0, PorterDuff.Mode.CLEAR);
      i += 1;
    }
    return localArrayList;
  }
  
  public void reset()
  {
    subtitleService.reset();
  }
  
  final class ClutDefinition
  {
    public final int[] clutEntries2Bit;
    public final int[] clutEntries4Bit;
    public final int[] clutEntries8Bit;
    public final int languageIndex;
    
    public ClutDefinition(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
    {
      languageIndex = this$1;
      clutEntries2Bit = paramArrayOfInt1;
      clutEntries4Bit = paramArrayOfInt2;
      clutEntries8Bit = paramArrayOfInt3;
    }
  }
  
  final class DisplayDefinition
  {
    public final int height;
    public final int horizontalPositionMaximum;
    public final int horizontalPositionMinimum;
    public final int verticalPositionMaximum;
    public final int verticalPositionMinimum;
    public final int width;
    
    public DisplayDefinition(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      width = this$1;
      height = paramInt1;
      horizontalPositionMinimum = paramInt2;
      horizontalPositionMaximum = paramInt3;
      verticalPositionMinimum = paramInt4;
      verticalPositionMaximum = paramInt5;
    }
  }
  
  final class ObjectData
  {
    public final byte[] bottomFieldData;
    public final int increment;
    public final boolean nonModifyingColorFlag;
    public final byte[] topFieldData;
    
    public ObjectData(boolean paramBoolean, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      increment = this$1;
      nonModifyingColorFlag = paramBoolean;
      topFieldData = paramArrayOfByte1;
      bottomFieldData = paramArrayOfByte2;
    }
  }
  
  final class PageComposition
  {
    public final SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser.PageRegion> regions;
    public final int state;
    public final int timeOutSecs;
    public final int version;
    
    public PageComposition(int paramInt1, int paramInt2, SparseArray paramSparseArray)
    {
      timeOutSecs = this$1;
      version = paramInt1;
      state = paramInt2;
      regions = paramSparseArray;
    }
  }
  
  final class PageRegion
  {
    public final int horizontalAddress;
    public final int verticalAddress;
    
    public PageRegion(int paramInt)
    {
      horizontalAddress = this$1;
      verticalAddress = paramInt;
    }
  }
  
  final class RegionComposition
  {
    public final int clutId;
    public final int depth;
    public final boolean fillFlag;
    public final int height;
    public final int levelOfCompatibility;
    public final int pixelCode2Bit;
    public final int pixelCode4Bit;
    public final int pixelCode8Bit;
    public final SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser.RegionObject> regionObjects;
    public final int regions;
    public final int width;
    
    public RegionComposition(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, SparseArray paramSparseArray)
    {
      regions = this$1;
      fillFlag = paramBoolean;
      width = paramInt1;
      height = paramInt2;
      levelOfCompatibility = paramInt3;
      depth = paramInt4;
      clutId = paramInt5;
      pixelCode8Bit = paramInt6;
      pixelCode4Bit = paramInt7;
      pixelCode2Bit = paramInt8;
      regionObjects = paramSparseArray;
    }
    
    public void mergeFrom(RegionComposition paramRegionComposition)
    {
      if (paramRegionComposition == null) {
        return;
      }
      paramRegionComposition = regionObjects;
      int i = 0;
      while (i < paramRegionComposition.size())
      {
        regionObjects.put(paramRegionComposition.keyAt(i), paramRegionComposition.valueAt(i));
        i += 1;
      }
    }
  }
  
  final class RegionObject
  {
    public final int backgroundPixelCode;
    public final int foregroundPixelCode;
    public final int horizontalPosition;
    public final int provider;
    public final int type;
    public final int verticalPosition;
    
    public RegionObject(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      type = this$1;
      provider = paramInt1;
      horizontalPosition = paramInt2;
      verticalPosition = paramInt3;
      foregroundPixelCode = paramInt4;
      backgroundPixelCode = paramInt5;
    }
  }
  
  final class SubtitleService
  {
    public final SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser.ClutDefinition> ancillaryCluts = new SparseArray();
    public final SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser.ObjectData> ancillaryObjects = new SparseArray();
    public final int ancillaryPageId;
    public final SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser.ClutDefinition> cluts = new SparseArray();
    public DvbParser.DisplayDefinition displayDefinition;
    public final SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser.ObjectData> objects = new SparseArray();
    public DvbParser.PageComposition pageComposition;
    public final SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser.RegionComposition> regions = new SparseArray();
    public final int subtitlePageId;
    
    public SubtitleService(int paramInt)
    {
      subtitlePageId = this$1;
      ancillaryPageId = paramInt;
    }
    
    public void reset()
    {
      regions.clear();
      cluts.clear();
      objects.clear();
      ancillaryCluts.clear();
      ancillaryObjects.clear();
      displayDefinition = null;
      pageComposition = null;
    }
  }
}
