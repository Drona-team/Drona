package com.google.android.exoplayer2.source.configurations;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;

class Aes128DataSource
  implements DataSource
{
  @Nullable
  private CipherInputStream cipherInputStream;
  private final byte[] encryptionIv;
  private final byte[] encryptionKey;
  private final DataSource upstream;
  
  public Aes128DataSource(DataSource paramDataSource, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    upstream = paramDataSource;
    encryptionKey = paramArrayOfByte1;
    encryptionIv = paramArrayOfByte2;
  }
  
  public final void addTransferListener(TransferListener paramTransferListener)
  {
    upstream.addTransferListener(paramTransferListener);
  }
  
  public void close()
    throws IOException
  {
    if (cipherInputStream != null)
    {
      cipherInputStream = null;
      upstream.close();
    }
  }
  
  protected Cipher getCipherInstance()
    throws NoSuchPaddingException, NoSuchAlgorithmException
  {
    return Cipher.getInstance("AES/CBC/PKCS7Padding");
  }
  
  public final Map getResponseHeaders()
  {
    return upstream.getResponseHeaders();
  }
  
  public final Uri getUri()
  {
    return upstream.getUri();
  }
  
  /* Error */
  public final long open(com.google.android.exoplayer2.upstream.DataSpec paramDataSpec)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 68	com/google/android/exoplayer2/source/configurations/Aes128DataSource:getCipherInstance	()Ljavax/crypto/Cipher;
    //   4: astore_2
    //   5: new 70	javax/crypto/spec/SecretKeySpec
    //   8: dup
    //   9: aload_0
    //   10: getfield 23	com/google/android/exoplayer2/source/configurations/Aes128DataSource:encryptionKey	[B
    //   13: ldc 72
    //   15: invokespecial 75	javax/crypto/spec/SecretKeySpec:<init>	([BLjava/lang/String;)V
    //   18: astore_3
    //   19: new 77	javax/crypto/spec/IvParameterSpec
    //   22: dup
    //   23: aload_0
    //   24: getfield 25	com/google/android/exoplayer2/source/configurations/Aes128DataSource:encryptionIv	[B
    //   27: invokespecial 80	javax/crypto/spec/IvParameterSpec:<init>	([B)V
    //   30: astore 4
    //   32: aload_2
    //   33: iconst_2
    //   34: aload_3
    //   35: aload 4
    //   37: invokevirtual 84	javax/crypto/Cipher:init	(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V
    //   40: new 86	com/google/android/exoplayer2/upstream/DataSourceInputStream
    //   43: dup
    //   44: aload_0
    //   45: getfield 21	com/google/android/exoplayer2/source/configurations/Aes128DataSource:upstream	Lcom/google/android/exoplayer2/upstream/DataSource;
    //   48: aload_1
    //   49: invokespecial 89	com/google/android/exoplayer2/upstream/DataSourceInputStream:<init>	(Lcom/google/android/exoplayer2/upstream/DataSource;Lcom/google/android/exoplayer2/upstream/DataSpec;)V
    //   52: astore_1
    //   53: aload_0
    //   54: new 91	javax/crypto/CipherInputStream
    //   57: dup
    //   58: aload_1
    //   59: aload_2
    //   60: invokespecial 94	javax/crypto/CipherInputStream:<init>	(Ljava/io/InputStream;Ljavax/crypto/Cipher;)V
    //   63: putfield 35	com/google/android/exoplayer2/source/configurations/Aes128DataSource:cipherInputStream	Ljavax/crypto/CipherInputStream;
    //   66: aload_1
    //   67: invokevirtual 96	com/google/android/exoplayer2/upstream/DataSourceInputStream:open	()V
    //   70: ldc2_w 97
    //   73: lreturn
    //   74: astore_1
    //   75: new 100	java/lang/RuntimeException
    //   78: dup
    //   79: aload_1
    //   80: invokespecial 103	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   83: athrow
    //   84: astore_1
    //   85: new 100	java/lang/RuntimeException
    //   88: dup
    //   89: aload_1
    //   90: invokespecial 103	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   93: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	Aes128DataSource
    //   0	94	1	paramDataSpec	com.google.android.exoplayer2.upstream.DataSpec
    //   4	56	2	localCipher	Cipher
    //   18	17	3	localSecretKeySpec	javax.crypto.spec.SecretKeySpec
    //   30	6	4	localIvParameterSpec	javax.crypto.spec.IvParameterSpec
    // Exception table:
    //   from	to	target	type
    //   32	40	74	java/security/InvalidKeyException
    //   32	40	74	java/security/InvalidAlgorithmParameterException
    //   0	5	84	java/security/NoSuchAlgorithmException
    //   0	5	84	javax/crypto/NoSuchPaddingException
  }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    Assertions.checkNotNull(cipherInputStream);
    paramInt1 = cipherInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt1 < 0) {
      return -1;
    }
    return paramInt1;
  }
}
