package client.testing.http;

import client.testing.util.IOUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpClient
{
  private static final int BUFFER_LENGTH = 4096;
  private static final int CHUNK_LENGTH = 2048;
  private static final Logger logger = LogManager.getLogger(ai.api.http.HttpClient.class);
  private final String boundary;
  private final HttpURLConnection connection;
  private final String delimiter = "--";
  private OutputStream out;
  private boolean writeSoundLog;
  
  public HttpClient(HttpURLConnection paramHttpURLConnection)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SwA");
    localStringBuilder.append(Long.toString(System.currentTimeMillis()));
    localStringBuilder.append("SwA");
    boundary = localStringBuilder.toString();
    if (paramHttpURLConnection != null)
    {
      connection = paramHttpURLConnection;
      return;
    }
    throw new IllegalArgumentException("Connection cannot be null");
  }
  
  public void addFilePart(String paramString1, String paramString2, InputStream paramInputStream)
    throws IOException
  {
    OutputStream localOutputStream = out;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("--");
    localStringBuilder.append(boundary);
    localStringBuilder.append("\r\n");
    localOutputStream.write(localStringBuilder.toString().getBytes());
    localOutputStream = out;
    localStringBuilder = new StringBuilder();
    localStringBuilder.append("Content-Disposition: form-data; name=\"");
    localStringBuilder.append(paramString1);
    localStringBuilder.append("\"; filename=\"");
    localStringBuilder.append(paramString2);
    localStringBuilder.append("\"\r\n");
    localOutputStream.write(localStringBuilder.toString().getBytes());
    out.write("Content-Type: audio/wav\r\n".getBytes());
    out.write("\r\n".getBytes());
    logger.debug("Sound write start");
    if (writeSoundLog)
    {
      paramString1 = new File(System.getProperty("java.io.tmpdir"));
      if (!paramString1.exists()) {
        paramString1.mkdirs();
      }
      logger.debug(paramString1.getAbsolutePath());
      paramString1 = new FileOutputStream(new File(paramString1, "log.wav"), false);
    }
    else
    {
      paramString1 = null;
    }
    paramString2 = new byte['?'];
    for (int i = paramInputStream.read(paramString2, 0, paramString2.length); i >= 0; i = paramInputStream.read(paramString2, 0, paramString2.length)) {
      if (i > 0)
      {
        out.write(paramString2, 0, i);
        if (writeSoundLog) {
          paramString1.write(paramString2, 0, i);
        }
      }
    }
    if (writeSoundLog) {
      paramString1.close();
    }
    logger.debug("Sound write finished");
    out.write("\r\n".getBytes());
  }
  
  public void addFormPart(String paramString1, String paramString2)
    throws IOException
  {
    Object localObject = out;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("--");
    localStringBuilder.append(boundary);
    localStringBuilder.append("\r\n");
    ((OutputStream)localObject).write(localStringBuilder.toString().getBytes());
    out.write("Content-Type: application/json\r\n".getBytes());
    localObject = out;
    localStringBuilder = new StringBuilder();
    localStringBuilder.append("Content-Disposition: form-data; name=\"");
    localStringBuilder.append(paramString1);
    localStringBuilder.append("\"\r\n");
    ((OutputStream)localObject).write(localStringBuilder.toString().getBytes());
    paramString1 = out;
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("\r\n");
    ((StringBuilder)localObject).append(paramString2);
    ((StringBuilder)localObject).append("\r\n");
    paramString1.write(((StringBuilder)localObject).toString().getBytes());
  }
  
  public void connectForMultipart()
    throws IOException
  {
    connection.setRequestProperty("Connection", "Keep-Alive");
    HttpURLConnection localHttpURLConnection = connection;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("multipart/form-data; boundary=");
    localStringBuilder.append(boundary);
    localHttpURLConnection.setRequestProperty("Content-Type", localStringBuilder.toString());
    connection.setChunkedStreamingMode(2048);
    connection.connect();
    out = connection.getOutputStream();
  }
  
  public void finishMultipart()
    throws IOException
  {
    OutputStream localOutputStream = out;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("--");
    localStringBuilder.append(boundary);
    localStringBuilder.append("--");
    localStringBuilder.append("\r\n");
    localOutputStream.write(localStringBuilder.toString().getBytes());
    out.close();
  }
  
  public String getErrorString()
  {
    Object localObject = connection;
    try
    {
      localObject = new BufferedInputStream(((HttpURLConnection)localObject).getErrorStream());
      String str = IOUtils.readAll((InputStream)localObject);
      ((InputStream)localObject).close();
      return str;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return null;
  }
  
  public String getResponse()
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(connection.getInputStream());
    String str = IOUtils.readAll(localBufferedInputStream);
    localBufferedInputStream.close();
    return str;
  }
  
  public void setWriteSoundLog(boolean paramBoolean)
  {
    writeSoundLog = paramBoolean;
  }
}
