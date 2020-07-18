package org.json;

import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareFile
{
  private String path;
  private final ReactApplicationContext reactContext;
  private String type;
  private Uri uri;
  
  public ShareFile(String paramString, ReactApplicationContext paramReactApplicationContext)
  {
    path = paramString;
    uri = Uri.parse(path);
    reactContext = paramReactApplicationContext;
  }
  
  public ShareFile(String paramString1, String paramString2, ReactApplicationContext paramReactApplicationContext)
  {
    this(paramString1, paramReactApplicationContext);
    type = paramString2;
  }
  
  private String getMimeType(String paramString)
  {
    paramString = MimeTypeMap.getFileExtensionFromUrl(paramString);
    if (paramString != null) {
      return MimeTypeMap.getSingleton().getMimeTypeFromExtension(paramString);
    }
    return null;
  }
  
  private String getRealPathFromURI(Uri paramUri)
  {
    return RNSharePathUtil.getRealPathFromURI(reactContext, paramUri);
  }
  
  private boolean isBase64File()
  {
    if ((uri.getScheme() != null) && (uri.getScheme().equals("data")))
    {
      type = uri.getSchemeSpecificPart().substring(0, uri.getSchemeSpecificPart().indexOf(";"));
      return true;
    }
    return false;
  }
  
  private boolean isLocalFile()
  {
    if ((uri.getScheme() != null) && ((uri.getScheme().equals("content")) || (uri.getScheme().equals("file"))))
    {
      if (type != null) {
        return true;
      }
      type = getMimeType(uri.toString());
      if (type == null)
      {
        String str = getRealPathFromURI(uri);
        if (str != null) {
          type = getMimeType(str);
        } else {
          return false;
        }
      }
      if (type == null)
      {
        type = "*/*";
        return true;
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  public String getType()
  {
    if (type == null) {
      return "*/*";
    }
    return type;
  }
  
  public Uri getURI()
  {
    Object localObject2 = MimeTypeMap.getSingleton().getExtensionFromMimeType(getType());
    if (isBase64File())
    {
      Object localObject1 = uri.getSchemeSpecificPart().substring(uri.getSchemeSpecificPart().indexOf(";base64,") + 8);
      try
      {
        Object localObject3 = Environment.getExternalStorageDirectory();
        Object localObject4 = Environment.DIRECTORY_DOWNLOADS;
        localObject3 = new File((File)localObject3, (String)localObject4);
        boolean bool = ((File)localObject3).exists();
        if (!bool)
        {
          bool = ((File)localObject3).mkdirs();
          if (!bool)
          {
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("mkdirs failed on ");
            ((StringBuilder)localObject1).append(((File)localObject3).getAbsolutePath());
            throw new IOException(((StringBuilder)localObject1).toString());
          }
        }
        localObject4 = new StringBuilder();
        ((StringBuilder)localObject4).append(System.nanoTime());
        ((StringBuilder)localObject4).append(".");
        ((StringBuilder)localObject4).append((String)localObject2);
        localObject2 = new File((File)localObject3, ((StringBuilder)localObject4).toString());
        localObject3 = new FileOutputStream((File)localObject2);
        ((FileOutputStream)localObject3).write(Base64.decode((String)localObject1, 0));
        ((FileOutputStream)localObject3).flush();
        ((FileOutputStream)localObject3).close();
        localObject1 = reactContext;
        localObject1 = RNSharePathUtil.compatUriFromFile((ReactContext)localObject1, (File)localObject2);
        return localObject1;
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        return null;
      }
    }
    if (isLocalFile())
    {
      Uri localUri = Uri.parse(path);
      if (localUri.getPath() == null) {
        return null;
      }
      return RNSharePathUtil.compatUriFromFile(reactContext, new File(localUri.getPath()));
    }
    return null;
  }
  
  public boolean isFile()
  {
    return (isBase64File()) || (isLocalFile());
  }
}
