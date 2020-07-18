package com.facebook.react.views.text;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintContextWrapper;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactCompoundView;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewBackgroundManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ReactTextView
  extends AppCompatTextView
  implements ReactCompoundView
{
  private static final ViewGroup.LayoutParams EMPTY_LAYOUT_PARAMS = new ViewGroup.LayoutParams(0, 0);
  private boolean mContainsImages;
  private int mDefaultGravityHorizontal = getGravity() & 0x800007;
  private int mDefaultGravityVertical = getGravity() & 0x70;
  private TextUtils.TruncateAt mEllipsizeLocation = TextUtils.TruncateAt.END;
  private int mLinkifyMaskType = 0;
  private boolean mNotifyOnInlineViewLayout;
  private int mNumberOfLines = Integer.MAX_VALUE;
  private ReactViewBackgroundManager mReactBackgroundManager = new ReactViewBackgroundManager(this);
  private Spannable mSpanned;
  private int mTextAlign = 0;
  
  public ReactTextView(Context paramContext)
  {
    super(paramContext);
  }
  
  private ReactContext getReactContext()
  {
    Context localContext2 = getContext();
    Context localContext1 = localContext2;
    if ((localContext2 instanceof TintContextWrapper)) {
      localContext1 = ((TintContextWrapper)localContext2).getBaseContext();
    }
    return (ReactContext)localContext1;
  }
  
  private WritableMap inlineViewJson(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    WritableMap localWritableMap = Arguments.createMap();
    if (paramInt1 == 8)
    {
      localWritableMap.putString("visibility", "gone");
      localWritableMap.putInt("index", paramInt2);
      return localWritableMap;
    }
    if (paramInt1 == 0)
    {
      localWritableMap.putString("visibility", "visible");
      localWritableMap.putInt("index", paramInt2);
      localWritableMap.putDouble("left", PixelUtil.toDIPFromPixel(paramInt3));
      localWritableMap.putDouble("top", PixelUtil.toDIPFromPixel(paramInt4));
      localWritableMap.putDouble("right", PixelUtil.toDIPFromPixel(paramInt5));
      localWritableMap.putDouble("bottom", PixelUtil.toDIPFromPixel(paramInt6));
      return localWritableMap;
    }
    localWritableMap.putString("visibility", "unknown");
    localWritableMap.putInt("index", paramInt2);
    return localWritableMap;
  }
  
  public int getBreakStrategy()
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.text.ReactTextView: int getBreakStrategy()> does not exist!");
  }
  
  public int getJustificationMode()
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.text.ReactTextView: int getJustificationMode()> does not exist!");
  }
  
  public Spannable getSpanned()
  {
    return mSpanned;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if ((mContainsImages) && ((getText() instanceof Spanned)))
    {
      Object localObject = (Spanned)getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        if (localObject[i].getDrawable() == paramDrawable) {
          invalidate();
        }
        i += 1;
      }
    }
    super.invalidateDrawable(paramDrawable);
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((mContainsImages) && ((getText() instanceof Spanned)))
    {
      Object localObject = (Spanned)getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onAttachedToWindow();
        i += 1;
      }
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((mContainsImages) && ((getText() instanceof Spanned)))
    {
      Object localObject = (Spanned)getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onDetachedFromWindow();
        i += 1;
      }
    }
  }
  
  public void onFinishTemporaryDetach()
  {
    super.onFinishTemporaryDetach();
    if ((mContainsImages) && ((getText() instanceof Spanned)))
    {
      Object localObject = (Spanned)getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onFinishTemporaryDetach();
        i += 1;
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!(getText() instanceof Spanned)) {
      return;
    }
    if (!getReactContext().hasCatalystInstance()) {
      return;
    }
    Object localObject2 = (UIManagerModule)getReactContext().getNativeModule(UIManagerModule.class);
    Spanned localSpanned = (Spanned)getText();
    Layout localLayout = getLayout();
    TextInlineViewPlaceholderSpan[] arrayOfTextInlineViewPlaceholderSpan = (TextInlineViewPlaceholderSpan[])localSpanned.getSpans(0, localSpanned.length(), TextInlineViewPlaceholderSpan.class);
    if (mNotifyOnInlineViewLayout) {}
    for (Object localObject1 = new ArrayList(arrayOfTextInlineViewPlaceholderSpan.length);; localObject1 = null) {
      break;
    }
    int m = paramInt3 - paramInt1;
    int n = arrayOfTextInlineViewPlaceholderSpan.length;
    int j = 0;
    while (j < n)
    {
      TextInlineViewPlaceholderSpan localTextInlineViewPlaceholderSpan = arrayOfTextInlineViewPlaceholderSpan[j];
      View localView = ((UIManagerModule)localObject2).resolveView(localTextInlineViewPlaceholderSpan.getReactTag());
      int i1 = localSpanned.getSpanStart(localTextInlineViewPlaceholderSpan);
      int i4 = localLayout.getLineForOffset(i1);
      paramInt3 = localLayout.getEllipsisCount(i4);
      int k = 1;
      if (paramInt3 > 0) {
        paramInt3 = 1;
      } else {
        paramInt3 = 0;
      }
      if ((paramInt3 != 0) && (i1 >= localLayout.getLineStart(i4) + localLayout.getEllipsisStart(i4))) {}
      for (;;)
      {
        break;
        if (i4 < mNumberOfLines) {
          if (i1 < localLayout.getLineEnd(i4))
          {
            int i3 = localTextInlineViewPlaceholderSpan.getWidth();
            int i2 = localTextInlineViewPlaceholderSpan.getHeight();
            boolean bool = localLayout.isRtlCharAt(i1);
            if (localLayout.getParagraphDirection(i4) == -1) {
              paramBoolean = true;
            } else {
              paramBoolean = false;
            }
            if (i1 == localSpanned.length() - 1)
            {
              if (paramBoolean) {
                paramInt3 = m - (int)localLayout.getLineWidth(i4);
              } else {
                paramInt3 = (int)localLayout.getLineRight(i4) - i3;
              }
            }
            else
            {
              if (paramBoolean == bool) {
                paramInt3 = 1;
              } else {
                paramInt3 = 0;
              }
              if (paramInt3 != 0) {
                paramInt3 = (int)localLayout.getPrimaryHorizontal(i1);
              } else {
                paramInt3 = (int)localLayout.getSecondaryHorizontal(i1);
              }
              i = paramInt3;
              if (paramBoolean) {
                i = m - ((int)localLayout.getLineRight(i4) - paramInt3);
              }
              paramInt3 = i;
              if (bool) {
                paramInt3 = i - i3;
              }
            }
            if (bool) {
              paramInt3 += getTotalPaddingRight();
            } else {
              paramInt3 += getTotalPaddingLeft();
            }
            int i5 = paramInt1 + paramInt3;
            int i6 = getTotalPaddingTop() + localLayout.getLineBaseline(i4) - i2;
            i4 = paramInt2 + i6;
            int i = k;
            if (m > paramInt3) {
              if (paramInt4 - paramInt2 <= i6) {
                i = k;
              } else {
                i = 0;
              }
            }
            if (i != 0) {
              paramInt3 = 8;
            } else {
              paramInt3 = 0;
            }
            i = i5 + i3;
            k = i4 + i2;
            localView.setVisibility(paramInt3);
            localView.layout(i5, i4, i, k);
            if (mNotifyOnInlineViewLayout)
            {
              ((ArrayList)localObject1).add(inlineViewJson(paramInt3, i1, i5, i4, i, k));
              break label620;
            }
            break label620;
          }
        }
      }
      localView.setVisibility(8);
      if (mNotifyOnInlineViewLayout) {
        ((ArrayList)localObject1).add(inlineViewJson(8, i1, -1, -1, -1, -1));
      }
      label620:
      j += 1;
    }
    if (mNotifyOnInlineViewLayout)
    {
      Collections.sort((List)localObject1, new Comparator()
      {
        public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
        {
          paramAnonymousObject1 = (WritableMap)paramAnonymousObject1;
          paramAnonymousObject2 = (WritableMap)paramAnonymousObject2;
          return paramAnonymousObject1.getInt("index") - paramAnonymousObject2.getInt("index");
        }
      });
      localObject2 = Arguments.createArray();
      localObject1 = ((ArrayList)localObject1).iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((WritableArray)localObject2).pushMap((WritableMap)((Iterator)localObject1).next());
      }
      localObject1 = Arguments.createMap();
      ((WritableMap)localObject1).putArray("inlineViews", (ReadableArray)localObject2);
      ((RCTEventEmitter)getReactContext().getJSModule(RCTEventEmitter.class)).receiveEvent(getId(), "topInlineViewLayout", (WritableMap)localObject1);
    }
  }
  
  public void onStartTemporaryDetach()
  {
    super.onStartTemporaryDetach();
    if ((mContainsImages) && ((getText() instanceof Spanned)))
    {
      Object localObject = (Spanned)getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onStartTemporaryDetach();
        i += 1;
      }
    }
  }
  
  public int reactTagForTouch(float paramFloat1, float paramFloat2)
  {
    CharSequence localCharSequence = getText();
    int j = getId();
    int i = j;
    int k = (int)paramFloat1;
    int m = (int)paramFloat2;
    Object localObject2 = getLayout();
    if (localObject2 == null) {
      return j;
    }
    m = ((Layout)localObject2).getLineForVertical(m);
    int n = (int)((Layout)localObject2).getLineLeft(m);
    int i1 = (int)((Layout)localObject2).getLineRight(m);
    if (((localCharSequence instanceof Spanned)) && (k >= n) && (k <= i1))
    {
      Object localObject1 = (Spanned)localCharSequence;
      paramFloat1 = k;
      try
      {
        int i2 = ((Layout)localObject2).getOffsetForHorizontal(m, paramFloat1);
        localObject2 = (ReactTagSpan[])((Spanned)localObject1).getSpans(i2, i2, ReactTagSpan.class);
        if (localObject2 != null)
        {
          j = localCharSequence.length();
          k = 0;
          m = i;
          i = k;
          while (i < localObject2.length)
          {
            i1 = ((Spanned)localObject1).getSpanStart(localObject2[i]);
            int i3 = ((Spanned)localObject1).getSpanEnd(localObject2[i]);
            n = m;
            k = j;
            if (i3 > i2)
            {
              i1 = i3 - i1;
              n = m;
              k = j;
              if (i1 <= j)
              {
                n = localObject2[i].getReactTag();
                k = i1;
              }
            }
            i += 1;
            m = n;
            j = k;
          }
        }
        return j;
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Crash in HorizontalMeasurementProvider: ");
        ((StringBuilder)localObject1).append(localArrayIndexOutOfBoundsException.getMessage());
        FLog.e("ReactNative", ((StringBuilder)localObject1).toString());
      }
    }
    return m;
  }
  
  public void setBackgroundColor(int paramInt)
  {
    mReactBackgroundManager.setBackgroundColor(paramInt);
  }
  
  public void setBorderColor(int paramInt, float paramFloat1, float paramFloat2)
  {
    mReactBackgroundManager.setBorderColor(paramInt, paramFloat1, paramFloat2);
  }
  
  public void setBorderRadius(float paramFloat)
  {
    mReactBackgroundManager.setBorderRadius(paramFloat);
  }
  
  public void setBorderRadius(float paramFloat, int paramInt)
  {
    mReactBackgroundManager.setBorderRadius(paramFloat, paramInt);
  }
  
  public void setBorderStyle(String paramString)
  {
    mReactBackgroundManager.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(int paramInt, float paramFloat)
  {
    mReactBackgroundManager.setBorderWidth(paramInt, paramFloat);
  }
  
  public void setBreakStrategy(int paramInt)
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.text.ReactTextView: void setBreakStrategy(int)> does not exist!");
  }
  
  public void setEllipsizeLocation(TextUtils.TruncateAt paramTruncateAt)
  {
    mEllipsizeLocation = paramTruncateAt;
  }
  
  void setGravityHorizontal(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = mDefaultGravityHorizontal;
    }
    setGravity(i | getGravity() & 0xFFFFFFF8 & 0xFF7FFFF8);
  }
  
  void setGravityVertical(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = mDefaultGravityVertical;
    }
    setGravity(i | getGravity() & 0xFFFFFF8F);
  }
  
  public void setJustificationMode(int paramInt)
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.text.ReactTextView: void setJustificationMode(int)> does not exist!");
  }
  
  public void setLinkifyMask(int paramInt)
  {
    mLinkifyMaskType = paramInt;
  }
  
  public void setNotifyOnInlineViewLayout(boolean paramBoolean)
  {
    mNotifyOnInlineViewLayout = paramBoolean;
  }
  
  public void setNumberOfLines(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = Integer.MAX_VALUE;
    }
    mNumberOfLines = i;
    paramInt = mNumberOfLines;
    boolean bool = true;
    if (paramInt != 1) {
      bool = false;
    }
    setSingleLine(bool);
    setMaxLines(mNumberOfLines);
  }
  
  public void setSpanned(Spannable paramSpannable)
  {
    mSpanned = paramSpannable;
  }
  
  public void setText(ReactTextUpdate paramReactTextUpdate)
  {
    mContainsImages = paramReactTextUpdate.containsImages();
    if (getLayoutParams() == null) {
      setLayoutParams(EMPTY_LAYOUT_PARAMS);
    }
    Spannable localSpannable = paramReactTextUpdate.getText();
    if (mLinkifyMaskType > 0)
    {
      Linkify.addLinks(localSpannable, mLinkifyMaskType);
      setMovementMethod(LinkMovementMethod.getInstance());
    }
    setText(localSpannable);
    setPadding((int)Math.floor(paramReactTextUpdate.getPaddingLeft()), (int)Math.floor(paramReactTextUpdate.getPaddingTop()), (int)Math.floor(paramReactTextUpdate.getPaddingRight()), (int)Math.floor(paramReactTextUpdate.getPaddingBottom()));
    int i = paramReactTextUpdate.getTextAlign();
    if (mTextAlign != i) {
      mTextAlign = i;
    }
    setGravityHorizontal(mTextAlign);
    if ((Build.VERSION.SDK_INT >= 23) && (getBreakStrategy() != paramReactTextUpdate.getTextBreakStrategy())) {
      setBreakStrategy(paramReactTextUpdate.getTextBreakStrategy());
    }
    if ((Build.VERSION.SDK_INT >= 26) && (getJustificationMode() != paramReactTextUpdate.getJustificationMode())) {
      setJustificationMode(paramReactTextUpdate.getJustificationMode());
    }
    requestLayout();
  }
  
  public void updateView()
  {
    TextUtils.TruncateAt localTruncateAt;
    if (mNumberOfLines == Integer.MAX_VALUE) {
      localTruncateAt = null;
    } else {
      localTruncateAt = mEllipsizeLocation;
    }
    setEllipsize(localTruncateAt);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if ((mContainsImages) && ((getText() instanceof Spanned)))
    {
      Object localObject = (Spanned)getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        if (localObject[i].getDrawable() == paramDrawable) {
          return true;
        }
        i += 1;
      }
    }
    return super.verifyDrawable(paramDrawable);
  }
}
