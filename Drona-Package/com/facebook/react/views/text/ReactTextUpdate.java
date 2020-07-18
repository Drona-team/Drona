package com.facebook.react.views.text;

import android.text.Spannable;

public class ReactTextUpdate
{
  private final boolean mContainsImages;
  private final int mJsEventCounter;
  private final int mJustificationMode;
  private final float mPaddingBottom;
  private final float mPaddingLeft;
  private final float mPaddingRight;
  private final float mPaddingTop;
  private final int mSelectionEnd;
  private final int mSelectionStart;
  private final Spannable mText;
  private final int mTextAlign;
  private final int mTextBreakStrategy;
  
  public ReactTextUpdate(Spannable paramSpannable, int paramInt1, boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2)
  {
    this(paramSpannable, paramInt1, paramBoolean, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt2, 1, 0, -1, -1);
  }
  
  public ReactTextUpdate(Spannable paramSpannable, int paramInt1, boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramSpannable, paramInt1, paramBoolean, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt2, paramInt3, paramInt4, -1, -1);
  }
  
  public ReactTextUpdate(Spannable paramSpannable, int paramInt1, boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    mText = paramSpannable;
    mJsEventCounter = paramInt1;
    mContainsImages = paramBoolean;
    mPaddingLeft = paramFloat1;
    mPaddingTop = paramFloat2;
    mPaddingRight = paramFloat3;
    mPaddingBottom = paramFloat4;
    mTextAlign = paramInt2;
    mTextBreakStrategy = paramInt3;
    mSelectionStart = paramInt5;
    mSelectionEnd = paramInt6;
    mJustificationMode = paramInt4;
  }
  
  public boolean containsImages()
  {
    return mContainsImages;
  }
  
  public int getJsEventCounter()
  {
    return mJsEventCounter;
  }
  
  public int getJustificationMode()
  {
    return mJustificationMode;
  }
  
  public float getPaddingBottom()
  {
    return mPaddingBottom;
  }
  
  public float getPaddingLeft()
  {
    return mPaddingLeft;
  }
  
  public float getPaddingRight()
  {
    return mPaddingRight;
  }
  
  public float getPaddingTop()
  {
    return mPaddingTop;
  }
  
  public int getSelectionEnd()
  {
    return mSelectionEnd;
  }
  
  public int getSelectionStart()
  {
    return mSelectionStart;
  }
  
  public Spannable getText()
  {
    return mText;
  }
  
  public int getTextAlign()
  {
    return mTextAlign;
  }
  
  public int getTextBreakStrategy()
  {
    return mTextBreakStrategy;
  }
}