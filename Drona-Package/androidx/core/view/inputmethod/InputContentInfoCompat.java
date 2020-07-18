package androidx.core.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build.VERSION;
import android.view.inputmethod.InputContentInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public final class InputContentInfoCompat
{
  private final InputContentInfoCompatImpl mImpl;
  
  public InputContentInfoCompat(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      mImpl = new InputContentInfoCompatApi25Impl(paramUri1, paramClipDescription, paramUri2);
      return;
    }
    mImpl = new InputContentInfoCompatBaseImpl(paramUri1, paramClipDescription, paramUri2);
  }
  
  private InputContentInfoCompat(InputContentInfoCompatImpl paramInputContentInfoCompatImpl)
  {
    mImpl = paramInputContentInfoCompatImpl;
  }
  
  public static InputContentInfoCompat wrap(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if (Build.VERSION.SDK_INT < 25) {
      return null;
    }
    return new InputContentInfoCompat(new InputContentInfoCompatApi25Impl(paramObject));
  }
  
  public Uri getContentUri()
  {
    return mImpl.getContentUri();
  }
  
  public ClipDescription getDescription()
  {
    return mImpl.getDescription();
  }
  
  public Uri getLinkUri()
  {
    return mImpl.getLinkUri();
  }
  
  public void releasePermission()
  {
    mImpl.releasePermission();
  }
  
  public void requestPermission()
  {
    mImpl.requestPermission();
  }
  
  public Object unwrap()
  {
    return mImpl.getInputContentInfo();
  }
  
  @RequiresApi(25)
  private static final class InputContentInfoCompatApi25Impl
    implements InputContentInfoCompat.InputContentInfoCompatImpl
  {
    @NonNull
    final InputContentInfo mObject;
    
    InputContentInfoCompatApi25Impl(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
    {
      mObject = new InputContentInfo(paramUri1, paramClipDescription, paramUri2);
    }
    
    InputContentInfoCompatApi25Impl(Object paramObject)
    {
      mObject = ((InputContentInfo)paramObject);
    }
    
    public Uri getContentUri()
    {
      return mObject.getContentUri();
    }
    
    public ClipDescription getDescription()
    {
      return mObject.getDescription();
    }
    
    public Object getInputContentInfo()
    {
      return mObject;
    }
    
    public Uri getLinkUri()
    {
      return mObject.getLinkUri();
    }
    
    public void releasePermission()
    {
      mObject.releasePermission();
    }
    
    public void requestPermission()
    {
      mObject.requestPermission();
    }
  }
  
  private static final class InputContentInfoCompatBaseImpl
    implements InputContentInfoCompat.InputContentInfoCompatImpl
  {
    @NonNull
    private final Uri mContentUri;
    @NonNull
    private final ClipDescription mDescription;
    @Nullable
    private final Uri mLinkUri;
    
    InputContentInfoCompatBaseImpl(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
    {
      mContentUri = paramUri1;
      mDescription = paramClipDescription;
      mLinkUri = paramUri2;
    }
    
    public Uri getContentUri()
    {
      return mContentUri;
    }
    
    public ClipDescription getDescription()
    {
      return mDescription;
    }
    
    public Object getInputContentInfo()
    {
      return null;
    }
    
    public Uri getLinkUri()
    {
      return mLinkUri;
    }
    
    public void releasePermission() {}
    
    public void requestPermission() {}
  }
  
  private static abstract interface InputContentInfoCompatImpl
  {
    public abstract Uri getContentUri();
    
    public abstract ClipDescription getDescription();
    
    public abstract Object getInputContentInfo();
    
    public abstract Uri getLinkUri();
    
    public abstract void releasePermission();
    
    public abstract void requestPermission();
  }
}
