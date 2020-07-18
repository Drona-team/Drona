package org.json;

import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ShareFiles
{
  private String intentType;
  private final ReactApplicationContext reactContext;
  private ArrayList<Uri> uris = new ArrayList();
  
  public ShareFiles(ReadableArray paramReadableArray, ReactApplicationContext paramReactApplicationContext)
  {
    int i = 0;
    while (i < paramReadableArray.size())
    {
      Object localObject = paramReadableArray.getString(i);
      if (localObject != null)
      {
        localObject = Uri.parse((String)localObject);
        uris.add(localObject);
      }
      i += 1;
    }
    reactContext = paramReactApplicationContext;
  }
  
  public ShareFiles(ReadableArray paramReadableArray, String paramString, ReactApplicationContext paramReactApplicationContext)
  {
    this(paramReadableArray, paramReactApplicationContext);
    intentType = paramString;
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
  
  private boolean isBase64File(Uri paramUri)
  {
    if ((paramUri.getScheme() != null) && (paramUri.getScheme().equals("data")))
    {
      paramUri = paramUri.getSchemeSpecificPart().substring(0, paramUri.getSchemeSpecificPart().indexOf(";"));
      if (intentType == null) {
        intentType = paramUri;
      } else if ((!intentType.equalsIgnoreCase(paramUri)) && (intentType.split("/")[0].equalsIgnoreCase(paramUri.split("/")[0]))) {
        intentType = intentType.split("/")[0].concat("/*");
      } else if (!intentType.equalsIgnoreCase(paramUri)) {
        intentType = "*/*";
      }
      return true;
    }
    return false;
  }
  
  private boolean isLocalFile(Uri paramUri)
  {
    if (((paramUri.getScheme() != null) && (paramUri.getScheme().equals("content"))) || ("file".equals(paramUri.getScheme())))
    {
      String str2 = getMimeType(paramUri.toString());
      String str1 = str2;
      if (str2 == null) {
        str1 = getMimeType(getRealPathFromURI(paramUri));
      }
      paramUri = str1;
      if (str1 == null) {
        paramUri = "*/*";
      }
      if (intentType == null) {
        intentType = paramUri;
      } else if ((!intentType.equalsIgnoreCase(paramUri)) && (intentType.split("/")[0].equalsIgnoreCase(paramUri.split("/")[0]))) {
        intentType = intentType.split("/")[0].concat("/*");
      } else if (!intentType.equalsIgnoreCase(paramUri)) {
        intentType = "*/*";
      }
      return true;
    }
    return false;
  }
  
  public String getType()
  {
    if (intentType == null) {
      return "*/*";
    }
    return intentType;
  }
  
  public ArrayList getURI()
  {
    MimeTypeMap localMimeTypeMap = MimeTypeMap.getSingleton();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = uris.iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (Uri)localIterator.next();
      if (isBase64File((Uri)localObject1))
      {
        Object localObject2 = localMimeTypeMap.getExtensionFromMimeType(((Uri)localObject1).getSchemeSpecificPart().substring(0, ((Uri)localObject1).getSchemeSpecificPart().indexOf(";")));
        localObject1 = ((Uri)localObject1).getSchemeSpecificPart().substring(((Uri)localObject1).getSchemeSpecificPart().indexOf(";base64,") + 8);
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
          ((StringBuilder)localObject4).append(System.currentTimeMillis());
          ((StringBuilder)localObject4).append(".");
          ((StringBuilder)localObject4).append((String)localObject2);
          localObject2 = new File((File)localObject3, ((StringBuilder)localObject4).toString());
          localObject3 = new FileOutputStream((File)localObject2);
          ((FileOutputStream)localObject3).write(Base64.decode((String)localObject1, 0));
          ((FileOutputStream)localObject3).flush();
          ((FileOutputStream)localObject3).close();
          localObject1 = reactContext;
          localArrayList.add(RNSharePathUtil.compatUriFromFile((ReactContext)localObject1, (File)localObject2));
        }
        catch (IOException localIOException)
        {
          localIOException.printStackTrace();
        }
      }
      else if ((isLocalFile(localIOException)) && (localIOException.getPath() != null))
      {
        localArrayList.add(RNSharePathUtil.compatUriFromFile(reactContext, new File(localIOException.getPath())));
      }
    }
    return localArrayList;
  }
  
  public boolean isFile()
  {
    Iterator localIterator = uris.iterator();
    boolean bool2 = true;
    boolean bool1;
    do
    {
      bool1 = bool2;
      if (!localIterator.hasNext()) {
        break;
      }
      Uri localUri = (Uri)localIterator.next();
      if ((!isBase64File(localUri)) && (!isLocalFile(localUri))) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      bool2 = bool1;
    } while (bool1);
    return bool1;
  }
}
