package androidx.core.text;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.LocaleList;
import android.text.Layout.Alignment;
import android.text.PrecomputedText;
import android.text.PrecomputedText.Params;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.opml.TraceCompat;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class PrecomputedTextCompat
  implements Spannable
{
  private static final char LINE_FEED = '\n';
  @GuardedBy("sLock")
  @NonNull
  private static Executor sExecutor = null;
  private static final Object sLock = new Object();
  @NonNull
  private final int[] mParagraphEnds;
  @NonNull
  private final Params mParams;
  @NonNull
  private final Spannable mText;
  @Nullable
  private final PrecomputedText mWrapped;
  
  private PrecomputedTextCompat(PrecomputedText paramPrecomputedText, Params paramParams)
  {
    mText = ((Spannable)paramPrecomputedText);
    mParams = paramParams;
    mParagraphEnds = null;
    mWrapped = null;
  }
  
  private PrecomputedTextCompat(CharSequence paramCharSequence, Params paramParams, int[] paramArrayOfInt)
  {
    mText = new SpannableString(paramCharSequence);
    mParams = paramParams;
    mParagraphEnds = paramArrayOfInt;
    mWrapped = null;
  }
  
  public static PrecomputedTextCompat create(CharSequence paramCharSequence, Params paramParams)
  {
    Preconditions.checkNotNull(paramCharSequence);
    Preconditions.checkNotNull(paramParams);
    try
    {
      TraceCompat.beginSection("PrecomputedText");
      ArrayList localArrayList = new ArrayList();
      int j = paramCharSequence.length();
      int i = 0;
      while (i < j)
      {
        i = TextUtils.indexOf(paramCharSequence, '\n', i, j);
        if (i < 0) {
          i = j;
        } else {
          i += 1;
        }
        localArrayList.add(Integer.valueOf(i));
      }
      int[] arrayOfInt = new int[localArrayList.size()];
      i = 0;
      for (;;)
      {
        j = localArrayList.size();
        if (i >= j) {
          break;
        }
        arrayOfInt[i] = ((Integer)localArrayList.get(i)).intValue();
        i += 1;
      }
      i = Build.VERSION.SDK_INT;
      if (i >= 23)
      {
        StaticLayout.Builder.obtain(paramCharSequence, 0, paramCharSequence.length(), paramParams.getTextPaint(), Integer.MAX_VALUE).setBreakStrategy(paramParams.getBreakStrategy()).setHyphenationFrequency(paramParams.getHyphenationFrequency()).setTextDirection(paramParams.getTextDirection()).build();
      }
      else
      {
        i = Build.VERSION.SDK_INT;
        if (i >= 21) {
          new StaticLayout(paramCharSequence, paramParams.getTextPaint(), Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        }
      }
      paramCharSequence = new PrecomputedTextCompat(paramCharSequence, paramParams, arrayOfInt);
      TraceCompat.endSection();
      return paramCharSequence;
    }
    catch (Throwable paramCharSequence)
    {
      TraceCompat.endSection();
      throw paramCharSequence;
    }
  }
  
  public static Future getTextFuture(CharSequence paramCharSequence, Params paramParams, Executor paramExecutor)
  {
    paramParams = new PrecomputedTextFutureTask(paramParams, paramCharSequence);
    paramCharSequence = paramExecutor;
    if (paramExecutor == null)
    {
      paramExecutor = sLock;
      try
      {
        if (sExecutor == null) {
          sExecutor = Executors.newFixedThreadPool(1);
        }
        paramCharSequence = sExecutor;
      }
      catch (Throwable paramCharSequence)
      {
        throw paramCharSequence;
      }
    }
    paramCharSequence.execute(paramParams);
    return paramParams;
  }
  
  public char charAt(int paramInt)
  {
    return mText.charAt(paramInt);
  }
  
  public int getParagraphCount()
  {
    return mParagraphEnds.length;
  }
  
  public int getParagraphEnd(int paramInt)
  {
    Preconditions.checkArgumentInRange(paramInt, 0, getParagraphCount(), "paraIndex");
    return mParagraphEnds[paramInt];
  }
  
  public int getParagraphStart(int paramInt)
  {
    Preconditions.checkArgumentInRange(paramInt, 0, getParagraphCount(), "paraIndex");
    if (paramInt == 0) {
      return 0;
    }
    return mParagraphEnds[(paramInt - 1)];
  }
  
  public Params getParams()
  {
    return mParams;
  }
  
  public PrecomputedText getPrecomputedText()
  {
    if ((mText instanceof PrecomputedText)) {
      return (PrecomputedText)mText;
    }
    return null;
  }
  
  public int getSpanEnd(Object paramObject)
  {
    return mText.getSpanEnd(paramObject);
  }
  
  public int getSpanFlags(Object paramObject)
  {
    return mText.getSpanFlags(paramObject);
  }
  
  public int getSpanStart(Object paramObject)
  {
    return mText.getSpanStart(paramObject);
  }
  
  public Object[] getSpans(int paramInt1, int paramInt2, Class paramClass)
  {
    return mText.getSpans(paramInt1, paramInt2, paramClass);
  }
  
  public int length()
  {
    return mText.length();
  }
  
  public int nextSpanTransition(int paramInt1, int paramInt2, Class paramClass)
  {
    return mText.nextSpanTransition(paramInt1, paramInt2, paramClass);
  }
  
  public void removeSpan(Object paramObject)
  {
    if (!(paramObject instanceof MetricAffectingSpan))
    {
      mText.removeSpan(paramObject);
      return;
    }
    throw new IllegalArgumentException("MetricAffectingSpan can not be removed from PrecomputedText.");
  }
  
  public void setSpan(Object paramObject, int paramInt1, int paramInt2, int paramInt3)
  {
    if (!(paramObject instanceof MetricAffectingSpan))
    {
      mText.setSpan(paramObject, paramInt1, paramInt2, paramInt3);
      return;
    }
    throw new IllegalArgumentException("MetricAffectingSpan can not be set to PrecomputedText.");
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return mText.subSequence(paramInt1, paramInt2);
  }
  
  public String toString()
  {
    return mText.toString();
  }
  
  public static final class Params
  {
    private final int mBreakStrategy;
    private final int mHyphenationFrequency;
    @NonNull
    private final TextPaint mPaint;
    @Nullable
    private final TextDirectionHeuristic mTextDir;
    final PrecomputedText.Params mWrapped;
    
    public Params(PrecomputedText.Params paramParams)
    {
      mPaint = paramParams.getTextPaint();
      mTextDir = paramParams.getTextDirection();
      mBreakStrategy = paramParams.getBreakStrategy();
      mHyphenationFrequency = paramParams.getHyphenationFrequency();
      mWrapped = null;
    }
    
    Params(TextPaint paramTextPaint, TextDirectionHeuristic paramTextDirectionHeuristic, int paramInt1, int paramInt2)
    {
      mWrapped = null;
      mPaint = paramTextPaint;
      mTextDir = paramTextDirectionHeuristic;
      mBreakStrategy = paramInt1;
      mHyphenationFrequency = paramInt2;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof Params)) {
        return false;
      }
      paramObject = (Params)paramObject;
      if (!equalsWithoutTextDirection(paramObject)) {
        return false;
      }
      return (Build.VERSION.SDK_INT < 18) || (mTextDir == paramObject.getTextDirection());
    }
    
    public boolean equalsWithoutTextDirection(Params paramParams)
    {
      if (mWrapped != null) {
        return mWrapped.equals(mWrapped);
      }
      if (Build.VERSION.SDK_INT >= 23)
      {
        if (mBreakStrategy != paramParams.getBreakStrategy()) {
          return false;
        }
        if (mHyphenationFrequency != paramParams.getHyphenationFrequency()) {
          return false;
        }
      }
      if (mPaint.getTextSize() != paramParams.getTextPaint().getTextSize()) {
        return false;
      }
      if (mPaint.getTextScaleX() != paramParams.getTextPaint().getTextScaleX()) {
        return false;
      }
      if (mPaint.getTextSkewX() != paramParams.getTextPaint().getTextSkewX()) {
        return false;
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        if (mPaint.getLetterSpacing() != paramParams.getTextPaint().getLetterSpacing()) {
          return false;
        }
        if (!TextUtils.equals(mPaint.getFontFeatureSettings(), paramParams.getTextPaint().getFontFeatureSettings())) {
          return false;
        }
      }
      if (mPaint.getFlags() != paramParams.getTextPaint().getFlags()) {
        return false;
      }
      if (Build.VERSION.SDK_INT >= 24)
      {
        if (!mPaint.getTextLocales().equals(paramParams.getTextPaint().getTextLocales())) {
          return false;
        }
      }
      else if ((Build.VERSION.SDK_INT >= 17) && (!mPaint.getTextLocale().equals(paramParams.getTextPaint().getTextLocale()))) {
        return false;
      }
      if (mPaint.getTypeface() == null)
      {
        if (paramParams.getTextPaint().getTypeface() != null) {
          return false;
        }
      }
      else if (!mPaint.getTypeface().equals(paramParams.getTextPaint().getTypeface())) {
        return false;
      }
      return true;
    }
    
    public int getBreakStrategy()
    {
      return mBreakStrategy;
    }
    
    public int getHyphenationFrequency()
    {
      return mHyphenationFrequency;
    }
    
    public TextDirectionHeuristic getTextDirection()
    {
      return mTextDir;
    }
    
    public TextPaint getTextPaint()
    {
      return mPaint;
    }
    
    public int hashCode()
    {
      if (Build.VERSION.SDK_INT >= 24) {
        return ObjectsCompat.hash(new Object[] { Float.valueOf(mPaint.getTextSize()), Float.valueOf(mPaint.getTextScaleX()), Float.valueOf(mPaint.getTextSkewX()), Float.valueOf(mPaint.getLetterSpacing()), Integer.valueOf(mPaint.getFlags()), mPaint.getTextLocales(), mPaint.getTypeface(), Boolean.valueOf(mPaint.isElegantTextHeight()), mTextDir, Integer.valueOf(mBreakStrategy), Integer.valueOf(mHyphenationFrequency) });
      }
      if (Build.VERSION.SDK_INT >= 21) {
        return ObjectsCompat.hash(new Object[] { Float.valueOf(mPaint.getTextSize()), Float.valueOf(mPaint.getTextScaleX()), Float.valueOf(mPaint.getTextSkewX()), Float.valueOf(mPaint.getLetterSpacing()), Integer.valueOf(mPaint.getFlags()), mPaint.getTextLocale(), mPaint.getTypeface(), Boolean.valueOf(mPaint.isElegantTextHeight()), mTextDir, Integer.valueOf(mBreakStrategy), Integer.valueOf(mHyphenationFrequency) });
      }
      if (Build.VERSION.SDK_INT >= 18) {
        return ObjectsCompat.hash(new Object[] { Float.valueOf(mPaint.getTextSize()), Float.valueOf(mPaint.getTextScaleX()), Float.valueOf(mPaint.getTextSkewX()), Integer.valueOf(mPaint.getFlags()), mPaint.getTextLocale(), mPaint.getTypeface(), mTextDir, Integer.valueOf(mBreakStrategy), Integer.valueOf(mHyphenationFrequency) });
      }
      if (Build.VERSION.SDK_INT >= 17) {
        return ObjectsCompat.hash(new Object[] { Float.valueOf(mPaint.getTextSize()), Float.valueOf(mPaint.getTextScaleX()), Float.valueOf(mPaint.getTextSkewX()), Integer.valueOf(mPaint.getFlags()), mPaint.getTextLocale(), mPaint.getTypeface(), mTextDir, Integer.valueOf(mBreakStrategy), Integer.valueOf(mHyphenationFrequency) });
      }
      return ObjectsCompat.hash(new Object[] { Float.valueOf(mPaint.getTextSize()), Float.valueOf(mPaint.getTextScaleX()), Float.valueOf(mPaint.getTextSkewX()), Integer.valueOf(mPaint.getFlags()), mPaint.getTypeface(), mTextDir, Integer.valueOf(mBreakStrategy), Integer.valueOf(mHyphenationFrequency) });
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder1 = new StringBuilder("{");
      StringBuilder localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append("textSize=");
      localStringBuilder2.append(mPaint.getTextSize());
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", textScaleX=");
      localStringBuilder2.append(mPaint.getTextScaleX());
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", textSkewX=");
      localStringBuilder2.append(mPaint.getTextSkewX());
      localStringBuilder1.append(localStringBuilder2.toString());
      if (Build.VERSION.SDK_INT >= 21)
      {
        localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append(", letterSpacing=");
        localStringBuilder2.append(mPaint.getLetterSpacing());
        localStringBuilder1.append(localStringBuilder2.toString());
        localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append(", elegantTextHeight=");
        localStringBuilder2.append(mPaint.isElegantTextHeight());
        localStringBuilder1.append(localStringBuilder2.toString());
      }
      if (Build.VERSION.SDK_INT >= 24)
      {
        localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append(", textLocale=");
        localStringBuilder2.append(mPaint.getTextLocales());
        localStringBuilder1.append(localStringBuilder2.toString());
      }
      else if (Build.VERSION.SDK_INT >= 17)
      {
        localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append(", textLocale=");
        localStringBuilder2.append(mPaint.getTextLocale());
        localStringBuilder1.append(localStringBuilder2.toString());
      }
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", typeface=");
      localStringBuilder2.append(mPaint.getTypeface());
      localStringBuilder1.append(localStringBuilder2.toString());
      if (Build.VERSION.SDK_INT >= 26)
      {
        localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append(", variationSettings=");
        localStringBuilder2.append(mPaint.getFontVariationSettings());
        localStringBuilder1.append(localStringBuilder2.toString());
      }
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", textDir=");
      localStringBuilder2.append(mTextDir);
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", breakStrategy=");
      localStringBuilder2.append(mBreakStrategy);
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", hyphenationFrequency=");
      localStringBuilder2.append(mHyphenationFrequency);
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder1.append("}");
      return localStringBuilder1.toString();
    }
    
    public static class Builder
    {
      private int mBreakStrategy;
      private int mHyphenationFrequency;
      @NonNull
      private final TextPaint mPaint;
      private TextDirectionHeuristic mTextDir;
      
      public Builder(TextPaint paramTextPaint)
      {
        mPaint = paramTextPaint;
        if (Build.VERSION.SDK_INT >= 23)
        {
          mBreakStrategy = 1;
          mHyphenationFrequency = 1;
        }
        else
        {
          mHyphenationFrequency = 0;
          mBreakStrategy = 0;
        }
        if (Build.VERSION.SDK_INT >= 18)
        {
          mTextDir = TextDirectionHeuristics.FIRSTSTRONG_LTR;
          return;
        }
        mTextDir = null;
      }
      
      public PrecomputedTextCompat.Params build()
      {
        return new PrecomputedTextCompat.Params(mPaint, mTextDir, mBreakStrategy, mHyphenationFrequency);
      }
      
      public Builder setBreakStrategy(int paramInt)
      {
        mBreakStrategy = paramInt;
        return this;
      }
      
      public Builder setHyphenationFrequency(int paramInt)
      {
        mHyphenationFrequency = paramInt;
        return this;
      }
      
      public Builder setTextDirection(TextDirectionHeuristic paramTextDirectionHeuristic)
      {
        mTextDir = paramTextDirectionHeuristic;
        return this;
      }
    }
  }
  
  private static class PrecomputedTextFutureTask
    extends FutureTask<PrecomputedTextCompat>
  {
    PrecomputedTextFutureTask(PrecomputedTextCompat.Params paramParams, CharSequence paramCharSequence)
    {
      super();
    }
    
    private static class PrecomputedTextCallback
      implements Callable<PrecomputedTextCompat>
    {
      private PrecomputedTextCompat.Params mParams;
      private CharSequence mText;
      
      PrecomputedTextCallback(PrecomputedTextCompat.Params paramParams, CharSequence paramCharSequence)
      {
        mParams = paramParams;
        mText = paramCharSequence;
      }
      
      public PrecomputedTextCompat call()
        throws Exception
      {
        return PrecomputedTextCompat.create(mText, mParams);
      }
    }
  }
}
