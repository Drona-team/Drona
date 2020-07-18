package com.facebook.react.views.image;

import androidx.annotation.Nullable;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class ImageLoadEvent
  extends Event<ImageLoadEvent>
{
  public static final int ON_ERROR = 1;
  public static final int ON_LOAD = 2;
  public static final int ON_LOAD_END = 3;
  public static final int ON_LOAD_START = 4;
  public static final int ON_PROGRESS = 5;
  private final int mEventType;
  private final int mHeight;
  @Nullable
  private final String mImageError;
  @Nullable
  private final String mImageUri;
  private final int mWidth;
  
  public ImageLoadEvent(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, null);
  }
  
  public ImageLoadEvent(int paramInt1, int paramInt2, String paramString)
  {
    this(paramInt1, paramInt2, paramString, 0, 0, null);
  }
  
  public ImageLoadEvent(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramString, paramInt3, paramInt4, null);
  }
  
  public ImageLoadEvent(int paramInt1, int paramInt2, String paramString1, int paramInt3, int paramInt4, String paramString2)
  {
    super(paramInt1);
    mEventType = paramInt2;
    mImageUri = paramString1;
    mWidth = paramInt3;
    mHeight = paramInt4;
    mImageError = paramString2;
  }
  
  public ImageLoadEvent(int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
  {
    this(paramInt1, paramInt2, null, 0, 0, paramString);
  }
  
  public static String eventNameForType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid image event: ");
      localStringBuilder.append(Integer.toString(paramInt));
      throw new IllegalStateException(localStringBuilder.toString());
    case 5: 
      return "topProgress";
    case 4: 
      return "topLoadStart";
    case 3: 
      return "topLoadEnd";
    case 2: 
      return "topLoad";
    }
    return "topError";
  }
  
  public void dispatch(RCTEventEmitter paramRCTEventEmitter)
  {
    Object localObject;
    if ((mImageUri == null) && (mEventType != 2) && (mEventType != 1))
    {
      localObject = null;
    }
    else
    {
      WritableMap localWritableMap2 = Arguments.createMap();
      WritableMap localWritableMap1 = localWritableMap2;
      if (mImageUri != null) {
        localWritableMap2.putString("uri", mImageUri);
      }
      if (mEventType == 2)
      {
        localObject = Arguments.createMap();
        ((WritableMap)localObject).putDouble("width", mWidth);
        ((WritableMap)localObject).putDouble("height", mHeight);
        if (mImageUri != null) {
          ((WritableMap)localObject).putString("url", mImageUri);
        }
        localWritableMap2.putMap("source", (ReadableMap)localObject);
        localObject = localWritableMap1;
      }
      else
      {
        localObject = localWritableMap1;
        if (mEventType == 1)
        {
          localWritableMap2.putString("error", mImageError);
          localObject = localWritableMap1;
        }
      }
    }
    paramRCTEventEmitter.receiveEvent(getViewTag(), getEventName(), (WritableMap)localObject);
  }
  
  public short getCoalescingKey()
  {
    return (short)mEventType;
  }
  
  public String getEventName()
  {
    return eventNameForType(mEventType);
  }
}
