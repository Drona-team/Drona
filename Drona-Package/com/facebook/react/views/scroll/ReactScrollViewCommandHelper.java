package com.facebook.react.views.scroll;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.PixelUtil;
import java.util.Map;

public class ReactScrollViewCommandHelper
{
  public static final int COMMAND_FLASH_SCROLL_INDICATORS = 3;
  public static final int COMMAND_SCROLL_TO = 1;
  public static final int COMMAND_SCROLL_TO_END = 2;
  
  public ReactScrollViewCommandHelper() {}
  
  public static Map getCommandsMap()
  {
    return MapBuilder.get("scrollTo", Integer.valueOf(1), "scrollToEnd", Integer.valueOf(2), "flashScrollIndicators", Integer.valueOf(3));
  }
  
  public static void receiveCommand(ScrollCommandHandler paramScrollCommandHandler, Object paramObject, int paramInt, ReadableArray paramReadableArray)
  {
    Assertions.assertNotNull(paramScrollCommandHandler);
    Assertions.assertNotNull(paramObject);
    Assertions.assertNotNull(paramReadableArray);
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException(String.format("Unsupported command %d received by %s.", new Object[] { Integer.valueOf(paramInt), paramScrollCommandHandler.getClass().getSimpleName() }));
    case 3: 
      paramScrollCommandHandler.flashScrollIndicators(paramObject);
      return;
    case 2: 
      scrollToEnd(paramScrollCommandHandler, paramObject, paramReadableArray);
      return;
    }
    scrollTo(paramScrollCommandHandler, paramObject, paramReadableArray);
  }
  
  public static void receiveCommand(ScrollCommandHandler paramScrollCommandHandler, Object paramObject, String paramString, ReadableArray paramReadableArray)
  {
    Assertions.assertNotNull(paramScrollCommandHandler);
    Assertions.assertNotNull(paramObject);
    Assertions.assertNotNull(paramReadableArray);
    int i = paramString.hashCode();
    if (i != -402165208)
    {
      if (i != 28425985)
      {
        if ((i == 2055114131) && (paramString.equals("scrollToEnd")))
        {
          i = 1;
          break label93;
        }
      }
      else if (paramString.equals("flashScrollIndicators"))
      {
        i = 2;
        break label93;
      }
    }
    else if (paramString.equals("scrollTo"))
    {
      i = 0;
      break label93;
    }
    i = -1;
    switch (i)
    {
    default: 
      throw new IllegalArgumentException(String.format("Unsupported command %s received by %s.", new Object[] { paramString, paramScrollCommandHandler.getClass().getSimpleName() }));
    case 2: 
      paramScrollCommandHandler.flashScrollIndicators(paramObject);
      return;
    case 1: 
      label93:
      scrollToEnd(paramScrollCommandHandler, paramObject, paramReadableArray);
      return;
    }
    scrollTo(paramScrollCommandHandler, paramObject, paramReadableArray);
  }
  
  private static void scrollTo(ScrollCommandHandler paramScrollCommandHandler, Object paramObject, ReadableArray paramReadableArray)
  {
    paramScrollCommandHandler.scrollTo(paramObject, new ScrollToCommandData(Math.round(PixelUtil.toPixelFromDIP(paramReadableArray.getDouble(0))), Math.round(PixelUtil.toPixelFromDIP(paramReadableArray.getDouble(1))), paramReadableArray.getBoolean(2)));
  }
  
  private static void scrollToEnd(ScrollCommandHandler paramScrollCommandHandler, Object paramObject, ReadableArray paramReadableArray)
  {
    paramScrollCommandHandler.scrollToEnd(paramObject, new ScrollToEndCommandData(paramReadableArray.getBoolean(0)));
  }
  
  public static abstract interface ScrollCommandHandler<T>
  {
    public abstract void flashScrollIndicators(Object paramObject);
    
    public abstract void scrollTo(Object paramObject, ReactScrollViewCommandHelper.ScrollToCommandData paramScrollToCommandData);
    
    public abstract void scrollToEnd(Object paramObject, ReactScrollViewCommandHelper.ScrollToEndCommandData paramScrollToEndCommandData);
  }
  
  public static class ScrollToCommandData
  {
    public final boolean mAnimated;
    public final int mDestX;
    public final int mDestY;
    
    ScrollToCommandData(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      mDestX = paramInt1;
      mDestY = paramInt2;
      mAnimated = paramBoolean;
    }
  }
  
  public static class ScrollToEndCommandData
  {
    public final boolean mAnimated;
    
    ScrollToEndCommandData(boolean paramBoolean)
    {
      mAnimated = paramBoolean;
    }
  }
}
