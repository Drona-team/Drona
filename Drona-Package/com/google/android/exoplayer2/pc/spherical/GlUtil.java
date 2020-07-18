package com.google.android.exoplayer2.pc.spherical;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.text.TextUtils;
import com.google.android.exoplayer2.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

final class GlUtil
{
  private static final String PAGE_KEY = "Spherical.Utils";
  
  private GlUtil() {}
  
  public static void checkGlError()
  {
    int j = GLES20.glGetError();
    int i = j;
    if (j != 0) {
      do
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("glError ");
        localStringBuilder.append(GLU.gluErrorString(i));
        Log.e("Spherical.Utils", localStringBuilder.toString());
        j = GLES20.glGetError();
        i = j;
      } while (j != 0);
    }
  }
  
  public static int compileProgram(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    checkGlError();
    int i = GLES20.glCreateShader(35633);
    GLES20.glShaderSource(i, TextUtils.join("\n", paramArrayOfString1));
    GLES20.glCompileShader(i);
    checkGlError();
    int j = GLES20.glCreateShader(35632);
    GLES20.glShaderSource(j, TextUtils.join("\n", paramArrayOfString2));
    GLES20.glCompileShader(j);
    checkGlError();
    int k = GLES20.glCreateProgram();
    GLES20.glAttachShader(k, i);
    GLES20.glAttachShader(k, j);
    GLES20.glLinkProgram(k);
    paramArrayOfString1 = new int[1];
    GLES20.glGetProgramiv(k, 35714, paramArrayOfString1, 0);
    if (paramArrayOfString1[0] != 1)
    {
      paramArrayOfString1 = new StringBuilder();
      paramArrayOfString1.append("Unable to link shader program: \n");
      paramArrayOfString1.append(GLES20.glGetProgramInfoLog(k));
      Log.e("Spherical.Utils", paramArrayOfString1.toString());
    }
    checkGlError();
    return k;
  }
  
  public static FloatBuffer createBuffer(float[] paramArrayOfFloat)
  {
    Object localObject = ByteBuffer.allocateDirect(paramArrayOfFloat.length * 4);
    ((ByteBuffer)localObject).order(ByteOrder.nativeOrder());
    localObject = ((ByteBuffer)localObject).asFloatBuffer();
    ((FloatBuffer)localObject).put(paramArrayOfFloat);
    ((FloatBuffer)localObject).position(0);
    return localObject;
  }
  
  public static int createExternalTexture()
  {
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, IntBuffer.wrap(arrayOfInt));
    GLES20.glBindTexture(36197, arrayOfInt[0]);
    GLES20.glTexParameteri(36197, 10241, 9729);
    GLES20.glTexParameteri(36197, 10240, 9729);
    GLES20.glTexParameteri(36197, 10242, 33071);
    GLES20.glTexParameteri(36197, 10243, 33071);
    checkGlError();
    return arrayOfInt[0];
  }
}
