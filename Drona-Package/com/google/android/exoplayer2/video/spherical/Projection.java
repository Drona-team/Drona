package com.google.android.exoplayer2.video.spherical;

import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Projection
{
  public static final int DRAW_MODE_TRIANGLES = 0;
  public static final int DRAW_MODE_TRIANGLES_FAN = 2;
  public static final int DRAW_MODE_TRIANGLES_STRIP = 1;
  public static final int POSITION_COORDS_PER_VERTEX = 3;
  public static final int TEXTURE_COORDS_PER_VERTEX = 2;
  public final Mesh leftMesh;
  public final Mesh rightMesh;
  public final boolean singleMesh;
  public final int stereoMode;
  
  public Projection(Mesh paramMesh, int paramInt)
  {
    this(paramMesh, paramMesh, paramInt);
  }
  
  public Projection(Mesh paramMesh1, Mesh paramMesh2, int paramInt)
  {
    leftMesh = paramMesh1;
    rightMesh = paramMesh2;
    stereoMode = paramInt;
    boolean bool;
    if (paramMesh1 == paramMesh2) {
      bool = true;
    } else {
      bool = false;
    }
    singleMesh = bool;
  }
  
  public static Projection createEquirectangular(float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float paramFloat3, int paramInt3)
  {
    boolean bool;
    if (paramFloat1 > 0.0F) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    if (paramInt1 >= 1) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    if (paramInt2 >= 1) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    if ((paramFloat2 > 0.0F) && (paramFloat2 <= 180.0F)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    if ((paramFloat3 > 0.0F) && (paramFloat3 <= 360.0F)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    paramFloat2 = (float)Math.toRadians(paramFloat2);
    float f1 = (float)Math.toRadians(paramFloat3);
    float f2 = paramFloat2 / paramInt1;
    float f3 = f1 / paramInt2;
    int i4 = paramInt2 + 1;
    int i = (i4 * 2 + 2) * paramInt1;
    float[] arrayOfFloat1 = new float[i * 3];
    float[] arrayOfFloat2 = new float[i * 2];
    i = 0;
    int k = 0;
    int j = 0;
    while (i < paramInt1)
    {
      float f4 = i;
      float f5 = paramFloat2 / 2.0F;
      int i1 = i + 1;
      float f6 = i1;
      int m = j;
      j = 0;
      int n = i;
      i = m;
      while (j < i4)
      {
        int i2 = 0;
        m = j;
        j = n;
        n = i2;
        while (n < 2)
        {
          if (n == 0) {
            paramFloat3 = f4 * f2 - f5;
          } else {
            paramFloat3 = f6 * f2 - f5;
          }
          float f7 = m * f3;
          float f8 = f1 / 2.0F;
          i2 = k + 1;
          double d1 = paramFloat1;
          double d2 = f7 + 3.1415927F - f8;
          double d3 = Math.sin(d2);
          double d4 = paramFloat3;
          arrayOfFloat1[k] = (-(float)(d3 * d1 * Math.cos(d4)));
          k = i2 + 1;
          arrayOfFloat1[i2] = ((float)(Math.sin(d4) * d1));
          i2 = k + 1;
          arrayOfFloat1[k] = ((float)(d1 * Math.cos(d2) * Math.cos(d4)));
          k = i + 1;
          arrayOfFloat2[i] = (f7 / f1);
          int i3 = k + 1;
          arrayOfFloat2[k] = ((j + n) * f2 / paramFloat2);
          if ((m == 0) && (n == 0)) {
            break label474;
          }
          i = i3;
          k = i2;
          if (m == paramInt2)
          {
            i = i3;
            k = i2;
            if (n == 1)
            {
              label474:
              System.arraycopy(arrayOfFloat1, i2 - 3, arrayOfFloat1, i2, 3);
              k = i2 + 3;
              System.arraycopy(arrayOfFloat2, i3 - 2, arrayOfFloat2, i3, 2);
              i = i3 + 2;
            }
          }
          n += 1;
        }
        m += 1;
        n = j;
        j = m;
      }
      j = i;
      i = i1;
    }
    return new Projection(new Mesh(new SubMesh[] { new SubMesh(0, arrayOfFloat1, arrayOfFloat2, 1) }), paramInt3);
  }
  
  public static Projection createEquirectangular(int paramInt)
  {
    return createEquirectangular(50.0F, 36, 72, 180.0F, 360.0F, paramInt);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface DrawMode {}
  
  public static final class Mesh
  {
    private final Projection.SubMesh[] subMeshes;
    
    public Mesh(Projection.SubMesh... paramVarArgs)
    {
      subMeshes = paramVarArgs;
    }
    
    public Projection.SubMesh getSubMesh(int paramInt)
    {
      return subMeshes[paramInt];
    }
    
    public int getSubMeshCount()
    {
      return subMeshes.length;
    }
  }
  
  public static final class SubMesh
  {
    public static final int VIDEO_TEXTURE_ID = 0;
    public final int mode;
    public final float[] textureCoords;
    public final int textureId;
    public final float[] vertices;
    
    public SubMesh(int paramInt1, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt2)
    {
      textureId = paramInt1;
      boolean bool;
      if (paramArrayOfFloat1.length * 2L == paramArrayOfFloat2.length * 3L) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool);
      vertices = paramArrayOfFloat1;
      textureCoords = paramArrayOfFloat2;
      mode = paramInt2;
    }
    
    public int getVertexCount()
    {
      return vertices.length / 3;
    }
  }
}
