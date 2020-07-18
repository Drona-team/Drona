package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public final class UdpDataSource
  extends BaseDataSource
{
  public static final int DEFAULT_MAX_PACKET_SIZE = 2000;
  public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 8000;
  @Nullable
  private InetAddress address;
  @Nullable
  private MulticastSocket multicastSocket;
  private boolean opened;
  private final DatagramPacket packet;
  private final byte[] packetBuffer;
  private int packetRemaining;
  @Nullable
  private DatagramSocket socket;
  @Nullable
  private InetSocketAddress socketAddress;
  private final int socketTimeoutMillis;
  @Nullable
  private Uri uri;
  
  public UdpDataSource()
  {
    this(2000);
  }
  
  public UdpDataSource(int paramInt)
  {
    this(paramInt, 8000);
  }
  
  public UdpDataSource(int paramInt1, int paramInt2)
  {
    super(true);
    socketTimeoutMillis = paramInt2;
    packetBuffer = new byte[paramInt1];
    packet = new DatagramPacket(packetBuffer, 0, paramInt1);
  }
  
  public UdpDataSource(TransferListener paramTransferListener)
  {
    this(paramTransferListener, 2000);
  }
  
  public UdpDataSource(TransferListener paramTransferListener, int paramInt)
  {
    this(paramTransferListener, paramInt, 8000);
  }
  
  public UdpDataSource(TransferListener paramTransferListener, int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2);
    if (paramTransferListener != null) {
      addTransferListener(paramTransferListener);
    }
  }
  
  public void close()
  {
    uri = null;
    MulticastSocket localMulticastSocket;
    InetAddress localInetAddress;
    if (multicastSocket != null)
    {
      localMulticastSocket = multicastSocket;
      localInetAddress = address;
    }
    try
    {
      localMulticastSocket.leaveGroup(localInetAddress);
      multicastSocket = null;
      if (socket != null)
      {
        socket.close();
        socket = null;
      }
      address = null;
      socketAddress = null;
      packetRemaining = 0;
      if (opened)
      {
        opened = false;
        transferEnded();
        return;
      }
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
  
  public Uri getUri()
  {
    return uri;
  }
  
  /* Error */
  public long open(DataSpec paramDataSpec)
    throws UdpDataSource.UdpDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: getfield 103	com/google/android/exoplayer2/upstream/DataSpec:uri	Landroid/net/Uri;
    //   5: putfield 69	com/google/android/exoplayer2/upstream/UdpDataSource:uri	Landroid/net/Uri;
    //   8: aload_0
    //   9: getfield 69	com/google/android/exoplayer2/upstream/UdpDataSource:uri	Landroid/net/Uri;
    //   12: invokevirtual 109	android/net/Uri:getHost	()Ljava/lang/String;
    //   15: astore 4
    //   17: aload_0
    //   18: getfield 69	com/google/android/exoplayer2/upstream/UdpDataSource:uri	Landroid/net/Uri;
    //   21: invokevirtual 113	android/net/Uri:getPort	()I
    //   24: istore_2
    //   25: aload_0
    //   26: aload_1
    //   27: invokevirtual 117	com/google/android/exoplayer2/upstream/BaseDataSource:transferInitializing	(Lcom/google/android/exoplayer2/upstream/DataSpec;)V
    //   30: aload 4
    //   32: invokestatic 123	java/net/InetAddress:getByName	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   35: astore 4
    //   37: aload_0
    //   38: aload 4
    //   40: putfield 73	com/google/android/exoplayer2/upstream/UdpDataSource:address	Ljava/net/InetAddress;
    //   43: aload_0
    //   44: getfield 73	com/google/android/exoplayer2/upstream/UdpDataSource:address	Ljava/net/InetAddress;
    //   47: astore 4
    //   49: new 125	java/net/InetSocketAddress
    //   52: dup
    //   53: aload 4
    //   55: iload_2
    //   56: invokespecial 128	java/net/InetSocketAddress:<init>	(Ljava/net/InetAddress;I)V
    //   59: astore 4
    //   61: aload_0
    //   62: aload 4
    //   64: putfield 87	com/google/android/exoplayer2/upstream/UdpDataSource:socketAddress	Ljava/net/InetSocketAddress;
    //   67: aload_0
    //   68: getfield 73	com/google/android/exoplayer2/upstream/UdpDataSource:address	Ljava/net/InetAddress;
    //   71: astore 4
    //   73: aload 4
    //   75: invokevirtual 132	java/net/InetAddress:isMulticastAddress	()Z
    //   78: istore_3
    //   79: iload_3
    //   80: ifeq +56 -> 136
    //   83: aload_0
    //   84: getfield 87	com/google/android/exoplayer2/upstream/UdpDataSource:socketAddress	Ljava/net/InetSocketAddress;
    //   87: astore 4
    //   89: new 75	java/net/MulticastSocket
    //   92: dup
    //   93: aload 4
    //   95: invokespecial 135	java/net/MulticastSocket:<init>	(Ljava/net/SocketAddress;)V
    //   98: astore 4
    //   100: aload_0
    //   101: aload 4
    //   103: putfield 71	com/google/android/exoplayer2/upstream/UdpDataSource:multicastSocket	Ljava/net/MulticastSocket;
    //   106: aload_0
    //   107: getfield 71	com/google/android/exoplayer2/upstream/UdpDataSource:multicastSocket	Ljava/net/MulticastSocket;
    //   110: astore 4
    //   112: aload_0
    //   113: getfield 73	com/google/android/exoplayer2/upstream/UdpDataSource:address	Ljava/net/InetAddress;
    //   116: astore 5
    //   118: aload 4
    //   120: aload 5
    //   122: invokevirtual 138	java/net/MulticastSocket:joinGroup	(Ljava/net/InetAddress;)V
    //   125: aload_0
    //   126: aload_0
    //   127: getfield 71	com/google/android/exoplayer2/upstream/UdpDataSource:multicastSocket	Ljava/net/MulticastSocket;
    //   130: putfield 81	com/google/android/exoplayer2/upstream/UdpDataSource:socket	Ljava/net/DatagramSocket;
    //   133: goto +26 -> 159
    //   136: aload_0
    //   137: getfield 87	com/google/android/exoplayer2/upstream/UdpDataSource:socketAddress	Ljava/net/InetSocketAddress;
    //   140: astore 4
    //   142: new 83	java/net/DatagramSocket
    //   145: dup
    //   146: aload 4
    //   148: invokespecial 139	java/net/DatagramSocket:<init>	(Ljava/net/SocketAddress;)V
    //   151: astore 4
    //   153: aload_0
    //   154: aload 4
    //   156: putfield 81	com/google/android/exoplayer2/upstream/UdpDataSource:socket	Ljava/net/DatagramSocket;
    //   159: aload_0
    //   160: getfield 81	com/google/android/exoplayer2/upstream/UdpDataSource:socket	Ljava/net/DatagramSocket;
    //   163: astore 4
    //   165: aload_0
    //   166: getfield 45	com/google/android/exoplayer2/upstream/UdpDataSource:socketTimeoutMillis	I
    //   169: istore_2
    //   170: aload 4
    //   172: iload_2
    //   173: invokevirtual 142	java/net/DatagramSocket:setSoTimeout	(I)V
    //   176: aload_0
    //   177: iconst_1
    //   178: putfield 91	com/google/android/exoplayer2/upstream/UdpDataSource:opened	Z
    //   181: aload_0
    //   182: aload_1
    //   183: invokevirtual 145	com/google/android/exoplayer2/upstream/BaseDataSource:transferStarted	(Lcom/google/android/exoplayer2/upstream/DataSpec;)V
    //   186: ldc2_w 146
    //   189: lreturn
    //   190: astore_1
    //   191: new 6	com/google/android/exoplayer2/upstream/UdpDataSource$UdpDataSourceException
    //   194: dup
    //   195: aload_1
    //   196: invokespecial 150	com/google/android/exoplayer2/upstream/UdpDataSource$UdpDataSourceException:<init>	(Ljava/io/IOException;)V
    //   199: athrow
    //   200: astore_1
    //   201: new 6	com/google/android/exoplayer2/upstream/UdpDataSource$UdpDataSourceException
    //   204: dup
    //   205: aload_1
    //   206: invokespecial 150	com/google/android/exoplayer2/upstream/UdpDataSource$UdpDataSourceException:<init>	(Ljava/io/IOException;)V
    //   209: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	210	0	this	UdpDataSource
    //   0	210	1	paramDataSpec	DataSpec
    //   24	149	2	i	int
    //   78	2	3	bool	boolean
    //   15	156	4	localObject	Object
    //   116	5	5	localInetAddress	InetAddress
    // Exception table:
    //   from	to	target	type
    //   170	176	190	java/net/SocketException
    //   30	37	200	java/io/IOException
    //   49	61	200	java/io/IOException
    //   73	79	200	java/io/IOException
    //   89	100	200	java/io/IOException
    //   118	125	200	java/io/IOException
    //   142	153	200	java/io/IOException
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws UdpDataSource.UdpDataSourceException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (packetRemaining == 0)
    {
      DatagramSocket localDatagramSocket = socket;
      DatagramPacket localDatagramPacket = packet;
      try
      {
        localDatagramSocket.receive(localDatagramPacket);
        packetRemaining = packet.getLength();
        bytesTransferred(packetRemaining);
      }
      catch (IOException paramArrayOfByte)
      {
        throw new UdpDataSourceException(paramArrayOfByte);
      }
    }
    int i = packet.getLength();
    int j = packetRemaining;
    paramInt2 = Math.min(packetRemaining, paramInt2);
    System.arraycopy(packetBuffer, i - j, paramArrayOfByte, paramInt1, paramInt2);
    packetRemaining -= paramInt2;
    return paramInt2;
  }
  
  public static final class UdpDataSourceException
    extends IOException
  {
    public UdpDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}
