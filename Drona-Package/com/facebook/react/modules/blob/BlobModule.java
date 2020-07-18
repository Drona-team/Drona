package com.facebook.react.modules.blob;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.network.NetworkingModule;
import com.facebook.react.modules.network.NetworkingModule.RequestBodyHandler;
import com.facebook.react.modules.network.NetworkingModule.ResponseHandler;
import com.facebook.react.modules.network.NetworkingModule.UriHandler;
import com.facebook.react.modules.websocket.WebSocketModule;
import com.facebook.react.modules.websocket.WebSocketModule.ContentHandler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.ByteString;

@ReactModule(name="BlobModule")
public class BlobModule
  extends ReactContextBaseJavaModule
{
  public static final String NAME = "BlobModule";
  private final Map<String, byte[]> mBlobs = new HashMap();
  private final NetworkingModule.RequestBodyHandler mNetworkingRequestBodyHandler = new NetworkingModule.RequestBodyHandler()
  {
    public boolean supports(ReadableMap paramAnonymousReadableMap)
    {
      return paramAnonymousReadableMap.hasKey("blob");
    }
    
    public RequestBody toRequestBody(ReadableMap paramAnonymousReadableMap, String paramAnonymousString)
    {
      String str = paramAnonymousString;
      if (paramAnonymousReadableMap.hasKey("type"))
      {
        str = paramAnonymousString;
        if (!paramAnonymousReadableMap.getString("type").isEmpty()) {
          str = paramAnonymousReadableMap.getString("type");
        }
      }
      paramAnonymousString = str;
      if (str == null) {
        paramAnonymousString = "application/octet-stream";
      }
      paramAnonymousReadableMap = paramAnonymousReadableMap.getMap("blob");
      str = paramAnonymousReadableMap.getString("blobId");
      paramAnonymousReadableMap = resolve(str, paramAnonymousReadableMap.getInt("offset"), paramAnonymousReadableMap.getInt("size"));
      return RequestBody.create(MediaType.parse(paramAnonymousString), paramAnonymousReadableMap);
    }
  };
  private final NetworkingModule.ResponseHandler mNetworkingResponseHandler = new NetworkingModule.ResponseHandler()
  {
    public boolean supports(String paramAnonymousString)
    {
      return "blob".equals(paramAnonymousString);
    }
    
    public WritableMap toResponseData(ResponseBody paramAnonymousResponseBody)
      throws IOException
    {
      paramAnonymousResponseBody = paramAnonymousResponseBody.bytes();
      WritableMap localWritableMap = Arguments.createMap();
      localWritableMap.putString("blobId", store(paramAnonymousResponseBody));
      localWritableMap.putInt("offset", 0);
      localWritableMap.putInt("size", paramAnonymousResponseBody.length);
      return localWritableMap;
    }
  };
  private final NetworkingModule.UriHandler mNetworkingUriHandler = new NetworkingModule.UriHandler()
  {
    public WritableMap fetch(Uri paramAnonymousUri)
      throws IOException
    {
      byte[] arrayOfByte = BlobModule.this.getBytesFromUri(paramAnonymousUri);
      WritableMap localWritableMap = Arguments.createMap();
      localWritableMap.putString("blobId", store(arrayOfByte));
      localWritableMap.putInt("offset", 0);
      localWritableMap.putInt("size", arrayOfByte.length);
      localWritableMap.putString("type", BlobModule.this.getMimeTypeFromUri(paramAnonymousUri));
      localWritableMap.putString("name", BlobModule.this.getNameFromUri(paramAnonymousUri));
      localWritableMap.putDouble("lastModified", BlobModule.this.getLastModifiedFromUri(paramAnonymousUri));
      return localWritableMap;
    }
    
    public boolean supports(Uri paramAnonymousUri, String paramAnonymousString)
    {
      paramAnonymousUri = paramAnonymousUri.getScheme();
      int i;
      if ((!"http".equals(paramAnonymousUri)) && (!"https".equals(paramAnonymousUri))) {
        i = 0;
      } else {
        i = 1;
      }
      return (i == 0) && ("blob".equals(paramAnonymousString));
    }
  };
  private final WebSocketModule.ContentHandler mWebSocketContentHandler = new WebSocketModule.ContentHandler()
  {
    public void onMessage(String paramAnonymousString, WritableMap paramAnonymousWritableMap)
    {
      paramAnonymousWritableMap.putString("data", paramAnonymousString);
    }
    
    public void onMessage(ByteString paramAnonymousByteString, WritableMap paramAnonymousWritableMap)
    {
      paramAnonymousByteString = paramAnonymousByteString.toByteArray();
      WritableMap localWritableMap = Arguments.createMap();
      localWritableMap.putString("blobId", store(paramAnonymousByteString));
      localWritableMap.putInt("offset", 0);
      localWritableMap.putInt("size", paramAnonymousByteString.length);
      paramAnonymousWritableMap.putMap("data", localWritableMap);
      paramAnonymousWritableMap.putString("type", "blob");
    }
  };
  
  public BlobModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private byte[] getBytesFromUri(Uri paramUri)
    throws IOException
  {
    Object localObject = getReactApplicationContext().getContentResolver().openInputStream(paramUri);
    if (localObject != null)
    {
      paramUri = new ByteArrayOutputStream();
      byte[] arrayOfByte = new byte['?'];
      for (;;)
      {
        int i = ((InputStream)localObject).read(arrayOfByte);
        if (i == -1) {
          break;
        }
        paramUri.write(arrayOfByte, 0, i);
      }
      return paramUri.toByteArray();
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("File not found for ");
    ((StringBuilder)localObject).append(paramUri);
    throw new FileNotFoundException(((StringBuilder)localObject).toString());
  }
  
  private long getLastModifiedFromUri(Uri paramUri)
  {
    if ("file".equals(paramUri.getScheme())) {
      return new File(paramUri.toString()).lastModified();
    }
    return 0L;
  }
  
  private String getMimeTypeFromUri(Uri paramUri)
  {
    String str3 = getReactApplicationContext().getContentResolver().getType(paramUri);
    String str1 = str3;
    String str2 = str1;
    if (str3 == null)
    {
      paramUri = MimeTypeMap.getFileExtensionFromUrl(paramUri.getPath());
      str2 = str1;
      if (paramUri != null) {
        str2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(paramUri);
      }
    }
    if (str2 == null) {
      return "";
    }
    return str2;
  }
  
  private String getNameFromUri(Uri paramUri)
  {
    if ("file".equals(paramUri.getScheme())) {
      return paramUri.getLastPathSegment();
    }
    Cursor localCursor = getReactApplicationContext().getContentResolver().query(paramUri, new String[] { "_display_name" }, null, null, null);
    if (localCursor != null) {
      try
      {
        boolean bool = localCursor.moveToFirst();
        if (bool)
        {
          paramUri = localCursor.getString(0);
          localCursor.close();
          return paramUri;
        }
        localCursor.close();
      }
      catch (Throwable paramUri)
      {
        localCursor.close();
        throw paramUri;
      }
    }
    return paramUri.getLastPathSegment();
  }
  
  private WebSocketModule getWebSocketModule()
  {
    return (WebSocketModule)getReactApplicationContext().getNativeModule(WebSocketModule.class);
  }
  
  public void addNetworkingHandler()
  {
    NetworkingModule localNetworkingModule = (NetworkingModule)getReactApplicationContext().getNativeModule(NetworkingModule.class);
    localNetworkingModule.addUriHandler(mNetworkingUriHandler);
    localNetworkingModule.addRequestBodyHandler(mNetworkingRequestBodyHandler);
    localNetworkingModule.addResponseHandler(mNetworkingResponseHandler);
  }
  
  public void addWebSocketHandler(int paramInt)
  {
    getWebSocketModule().setContentHandler(paramInt, mWebSocketContentHandler);
  }
  
  public void createFromParts(ReadableArray paramReadableArray, String paramString)
  {
    Object localObject1 = new ArrayList(paramReadableArray.size());
    int j = 0;
    int i;
    for (int k = 0; j < paramReadableArray.size(); k = i)
    {
      Object localObject2 = paramReadableArray.getMap(j);
      String str = ((ReadableMap)localObject2).getString("type");
      i = str.hashCode();
      if (i != -891985903)
      {
        if ((i == 3026845) && (str.equals("blob")))
        {
          i = 0;
          break label110;
        }
      }
      else if (str.equals("string"))
      {
        i = 1;
        break label110;
      }
      i = -1;
      switch (i)
      {
      default: 
        paramReadableArray = new StringBuilder();
        paramReadableArray.append("Invalid type for blob: ");
        paramReadableArray.append(((ReadableMap)localObject2).getString("type"));
        throw new IllegalArgumentException(paramReadableArray.toString());
      case 1: 
        localObject2 = ((ReadableMap)localObject2).getString("data").getBytes(Charset.forName("UTF-8"));
        i = k + localObject2.length;
        ((ArrayList)localObject1).add(j, localObject2);
        break;
      case 0: 
        label110:
        localObject2 = ((ReadableMap)localObject2).getMap("data");
        i = k + ((ReadableMap)localObject2).getInt("size");
        ((ArrayList)localObject1).add(j, resolve((ReadableMap)localObject2));
      }
      j += 1;
    }
    paramReadableArray = ByteBuffer.allocate(k);
    localObject1 = ((ArrayList)localObject1).iterator();
    while (((Iterator)localObject1).hasNext()) {
      paramReadableArray.put((byte[])((Iterator)localObject1).next());
    }
    store(paramReadableArray.array(), paramString);
  }
  
  public Map getConstants()
  {
    Resources localResources = getReactApplicationContext().getResources();
    int i = localResources.getIdentifier("blob_provider_authority", "string", getReactApplicationContext().getPackageName());
    if (i == 0) {
      return null;
    }
    return MapBuilder.get("BLOB_URI_SCHEME", "content", "BLOB_URI_HOST", localResources.getString(i));
  }
  
  public String getName()
  {
    return "BlobModule";
  }
  
  public void initialize()
  {
    BlobCollector.install(getReactApplicationContext(), this);
  }
  
  public void release(String paramString)
  {
    remove(paramString);
  }
  
  public void remove(String paramString)
  {
    Map localMap = mBlobs;
    try
    {
      mBlobs.remove(paramString);
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void removeWebSocketHandler(int paramInt)
  {
    getWebSocketModule().setContentHandler(paramInt, null);
  }
  
  public byte[] resolve(Uri paramUri)
  {
    String str1 = paramUri.getLastPathSegment();
    String str2 = paramUri.getQueryParameter("offset");
    int i;
    if (str2 != null) {
      i = Integer.parseInt(str2, 10);
    } else {
      i = 0;
    }
    paramUri = paramUri.getQueryParameter("size");
    int j;
    if (paramUri != null) {
      j = Integer.parseInt(paramUri, 10);
    } else {
      j = -1;
    }
    return resolve(str1, i, j);
  }
  
  public byte[] resolve(ReadableMap paramReadableMap)
  {
    return resolve(paramReadableMap.getString("blobId"), paramReadableMap.getInt("offset"), paramReadableMap.getInt("size"));
  }
  
  public byte[] resolve(String paramString, int paramInt1, int paramInt2)
  {
    Map localMap = mBlobs;
    try
    {
      byte[] arrayOfByte = (byte[])mBlobs.get(paramString);
      if (arrayOfByte == null) {
        return null;
      }
      int i = paramInt2;
      if (paramInt2 == -1) {
        i = arrayOfByte.length - paramInt1;
      }
      if (paramInt1 <= 0)
      {
        paramString = arrayOfByte;
        if (i == arrayOfByte.length) {}
      }
      else
      {
        paramString = Arrays.copyOfRange(arrayOfByte, paramInt1, i + paramInt1);
      }
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void sendOverSocket(ReadableMap paramReadableMap, int paramInt)
  {
    paramReadableMap = resolve(paramReadableMap.getString("blobId"), paramReadableMap.getInt("offset"), paramReadableMap.getInt("size"));
    if (paramReadableMap != null)
    {
      getWebSocketModule().sendBinary(ByteString.of(paramReadableMap), paramInt);
      return;
    }
    getWebSocketModule().sendBinary(null, paramInt);
  }
  
  public String store(byte[] paramArrayOfByte)
  {
    String str = UUID.randomUUID().toString();
    store(paramArrayOfByte, str);
    return str;
  }
  
  public void store(byte[] paramArrayOfByte, String paramString)
  {
    Map localMap = mBlobs;
    try
    {
      mBlobs.put(paramString, paramArrayOfByte);
      return;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
}
