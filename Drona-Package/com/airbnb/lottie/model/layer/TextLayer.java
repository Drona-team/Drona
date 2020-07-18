package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.collection.SparseArrayCompat;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.TextDelegate;
import com.airbnb.lottie.animation.content.ContentGroup;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TextKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.model.DocumentData;
import com.airbnb.lottie.model.DocumentData.Justification;
import com.airbnb.lottie.model.Font;
import com.airbnb.lottie.model.FontCharacter;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableTextFrame;
import com.airbnb.lottie.model.animatable.AnimatableTextProperties;
import com.airbnb.lottie.model.content.ShapeGroup;
import com.airbnb.lottie.utils.Utils;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextLayer
  extends BaseLayer
{
  private final LongSparseArray<String> codePointCache = new LongSparseArray();
  @Nullable
  private BaseKeyframeAnimation<Integer, Integer> colorAnimation;
  private final LottieComposition composition;
  private final Map<FontCharacter, List<ContentGroup>> contentsForCharacter = new HashMap();
  private final Paint fillPaint = new Paint(1) {};
  private final LottieDrawable lottieDrawable;
  private final Matrix matrix = new Matrix();
  private final RectF rectF = new RectF();
  private final StringBuilder stringBuilder = new StringBuilder(2);
  @Nullable
  private BaseKeyframeAnimation<Integer, Integer> strokeColorAnimation;
  private final Paint strokePaint = new Paint(1) {};
  @Nullable
  private BaseKeyframeAnimation<Float, Float> strokeWidthAnimation;
  private final TextKeyframeAnimation textAnimation;
  @Nullable
  private BaseKeyframeAnimation<Float, Float> trackingAnimation;
  
  TextLayer(LottieDrawable paramLottieDrawable, Layer paramLayer)
  {
    super(paramLottieDrawable, paramLayer);
    lottieDrawable = paramLottieDrawable;
    composition = paramLayer.getComposition();
    textAnimation = paramLayer.getText().createAnimation();
    textAnimation.addUpdateListener(this);
    addAnimation(textAnimation);
    paramLottieDrawable = paramLayer.getTextProperties();
    if ((paramLottieDrawable != null) && (color != null))
    {
      colorAnimation = color.createAnimation();
      colorAnimation.addUpdateListener(this);
      addAnimation(colorAnimation);
    }
    if ((paramLottieDrawable != null) && (stroke != null))
    {
      strokeColorAnimation = stroke.createAnimation();
      strokeColorAnimation.addUpdateListener(this);
      addAnimation(strokeColorAnimation);
    }
    if ((paramLottieDrawable != null) && (strokeWidth != null))
    {
      strokeWidthAnimation = strokeWidth.createAnimation();
      strokeWidthAnimation.addUpdateListener(this);
      addAnimation(strokeWidthAnimation);
    }
    if ((paramLottieDrawable != null) && (tracking != null))
    {
      trackingAnimation = tracking.createAnimation();
      trackingAnimation.addUpdateListener(this);
      addAnimation(trackingAnimation);
    }
  }
  
  private void applyJustification(DocumentData.Justification paramJustification, Canvas paramCanvas, float paramFloat)
  {
    switch (3.$SwitchMap$com$airbnb$lottie$model$DocumentData$Justification[paramJustification.ordinal()])
    {
    default: 
      
    case 3: 
      paramCanvas.translate(-paramFloat / 2.0F, 0.0F);
      return;
    case 2: 
      paramCanvas.translate(-paramFloat, 0.0F);
    }
  }
  
  private String codePointToString(String paramString, int paramInt)
  {
    int i = paramString.codePointAt(paramInt);
    int j = i;
    i = Character.charCount(i) + paramInt;
    while (i < paramString.length())
    {
      int k = paramString.codePointAt(i);
      if (!isModifier(k)) {
        break;
      }
      i += Character.charCount(k);
      j = j * 31 + k;
    }
    LongSparseArray localLongSparseArray = codePointCache;
    long l = j;
    if (localLongSparseArray.containsKey(l)) {
      return (String)codePointCache.get(l);
    }
    stringBuilder.setLength(0);
    while (paramInt < i)
    {
      j = paramString.codePointAt(paramInt);
      stringBuilder.appendCodePoint(j);
      paramInt += Character.charCount(j);
    }
    paramString = stringBuilder.toString();
    codePointCache.put(l, paramString);
    return paramString;
  }
  
  private void drawCharacter(String paramString, Paint paramPaint, Canvas paramCanvas)
  {
    if (paramPaint.getColor() == 0) {
      return;
    }
    if ((paramPaint.getStyle() == Paint.Style.STROKE) && (paramPaint.getStrokeWidth() == 0.0F)) {
      return;
    }
    paramCanvas.drawText(paramString, 0, paramString.length(), 0.0F, 0.0F, paramPaint);
  }
  
  private void drawCharacterAsGlyph(FontCharacter paramFontCharacter, Matrix paramMatrix, float paramFloat, DocumentData paramDocumentData, Canvas paramCanvas)
  {
    paramFontCharacter = getContentsForCharacter(paramFontCharacter);
    int i = 0;
    while (i < paramFontCharacter.size())
    {
      Path localPath = ((ContentGroup)paramFontCharacter.get(i)).getPath();
      localPath.computeBounds(rectF, false);
      matrix.set(paramMatrix);
      matrix.preTranslate(0.0F, (float)-baselineShift * Utils.dpScale());
      matrix.preScale(paramFloat, paramFloat);
      localPath.transform(matrix);
      if (strokeOverFill)
      {
        drawGlyph(localPath, fillPaint, paramCanvas);
        drawGlyph(localPath, strokePaint, paramCanvas);
      }
      else
      {
        drawGlyph(localPath, strokePaint, paramCanvas);
        drawGlyph(localPath, fillPaint, paramCanvas);
      }
      i += 1;
    }
  }
  
  private void drawCharacterFromFont(String paramString, DocumentData paramDocumentData, Canvas paramCanvas)
  {
    if (strokeOverFill)
    {
      drawCharacter(paramString, fillPaint, paramCanvas);
      drawCharacter(paramString, strokePaint, paramCanvas);
      return;
    }
    drawCharacter(paramString, strokePaint, paramCanvas);
    drawCharacter(paramString, fillPaint, paramCanvas);
  }
  
  private void drawFontTextLine(String paramString, DocumentData paramDocumentData, Canvas paramCanvas, float paramFloat)
  {
    int i = 0;
    while (i < paramString.length())
    {
      String str = codePointToString(paramString, i);
      i += str.length();
      drawCharacterFromFont(str, paramDocumentData, paramCanvas);
      float f3 = fillPaint.measureText(str, 0, 1);
      float f2 = tracking / 10.0F;
      float f1 = f2;
      if (trackingAnimation != null) {
        f1 = f2 + ((Float)trackingAnimation.getValue()).floatValue();
      }
      paramCanvas.translate(f3 + f1 * paramFloat, 0.0F);
    }
  }
  
  private void drawGlyph(Path paramPath, Paint paramPaint, Canvas paramCanvas)
  {
    if (paramPaint.getColor() == 0) {
      return;
    }
    if ((paramPaint.getStyle() == Paint.Style.STROKE) && (paramPaint.getStrokeWidth() == 0.0F)) {
      return;
    }
    paramCanvas.drawPath(paramPath, paramPaint);
  }
  
  private void drawGlyphTextLine(String paramString, DocumentData paramDocumentData, Matrix paramMatrix, Font paramFont, Canvas paramCanvas, float paramFloat1, float paramFloat2)
  {
    int i = 0;
    while (i < paramString.length())
    {
      int j = FontCharacter.hashFor(paramString.charAt(i), paramFont.getFamily(), paramFont.getStyle());
      FontCharacter localFontCharacter = (FontCharacter)composition.getCharacters().get(j);
      if (localFontCharacter != null)
      {
        drawCharacterAsGlyph(localFontCharacter, paramMatrix, paramFloat2, paramDocumentData, paramCanvas);
        float f3 = (float)localFontCharacter.getWidth();
        float f4 = Utils.dpScale();
        float f2 = tracking / 10.0F;
        float f1 = f2;
        if (trackingAnimation != null) {
          f1 = f2 + ((Float)trackingAnimation.getValue()).floatValue();
        }
        paramCanvas.translate(f3 * paramFloat2 * f4 * paramFloat1 + f1 * paramFloat1, 0.0F);
      }
      i += 1;
    }
  }
  
  private void drawTextGlyphs(DocumentData paramDocumentData, Matrix paramMatrix, Font paramFont, Canvas paramCanvas)
  {
    float f1 = (float)size / 100.0F;
    float f2 = Utils.getScale(paramMatrix);
    Object localObject = text;
    float f3 = (float)lineHeight * Utils.dpScale();
    localObject = getTextLines((String)localObject);
    int j = ((List)localObject).size();
    int i = 0;
    while (i < j)
    {
      String str = (String)((List)localObject).get(i);
      float f4 = getTextLineWidthForGlyphs(str, paramFont, f1, f2);
      paramCanvas.save();
      applyJustification(justification, paramCanvas, f4);
      f4 = (j - 1) * f3 / 2.0F;
      paramCanvas.translate(0.0F, i * f3 - f4);
      drawGlyphTextLine(str, paramDocumentData, paramMatrix, paramFont, paramCanvas, f2, f1);
      paramCanvas.restore();
      i += 1;
    }
  }
  
  private void drawTextWithFont(DocumentData paramDocumentData, Font paramFont, Matrix paramMatrix, Canvas paramCanvas)
  {
    float f1 = Utils.getScale(paramMatrix);
    Typeface localTypeface = lottieDrawable.getTypeface(paramFont.getFamily(), paramFont.getStyle());
    if (localTypeface == null) {
      return;
    }
    String str = text;
    TextDelegate localTextDelegate = lottieDrawable.getTextDelegate();
    paramFont = str;
    if (localTextDelegate != null) {
      paramFont = localTextDelegate.getTextInternal(str);
    }
    fillPaint.setTypeface(localTypeface);
    fillPaint.setTextSize((float)(size * Utils.dpScale()));
    strokePaint.setTypeface(fillPaint.getTypeface());
    strokePaint.setTextSize(fillPaint.getTextSize());
    float f2 = (float)lineHeight * Utils.dpScale();
    paramFont = getTextLines(paramFont);
    int j = paramFont.size();
    int i = 0;
    while (i < j)
    {
      str = (String)paramFont.get(i);
      float f3 = strokePaint.measureText(str);
      applyJustification(justification, paramCanvas, f3);
      f3 = (j - 1) * f2 / 2.0F;
      paramCanvas.translate(0.0F, i * f2 - f3);
      drawFontTextLine(str, paramDocumentData, paramCanvas, f1);
      paramCanvas.setMatrix(paramMatrix);
      i += 1;
    }
  }
  
  private List getContentsForCharacter(FontCharacter paramFontCharacter)
  {
    if (contentsForCharacter.containsKey(paramFontCharacter)) {
      return (List)contentsForCharacter.get(paramFontCharacter);
    }
    List localList = paramFontCharacter.getShapes();
    int j = localList.size();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      ShapeGroup localShapeGroup = (ShapeGroup)localList.get(i);
      localArrayList.add(new ContentGroup(lottieDrawable, this, localShapeGroup));
      i += 1;
    }
    contentsForCharacter.put(paramFontCharacter, localArrayList);
    return localArrayList;
  }
  
  private float getTextLineWidthForGlyphs(String paramString, Font paramFont, float paramFloat1, float paramFloat2)
  {
    float f = 0.0F;
    int i = 0;
    while (i < paramString.length())
    {
      int j = FontCharacter.hashFor(paramString.charAt(i), paramFont.getFamily(), paramFont.getStyle());
      FontCharacter localFontCharacter = (FontCharacter)composition.getCharacters().get(j);
      if (localFontCharacter != null) {
        f = (float)(f + localFontCharacter.getWidth() * paramFloat1 * Utils.dpScale() * paramFloat2);
      }
      i += 1;
    }
    return f;
  }
  
  private List getTextLines(String paramString)
  {
    return Arrays.asList(paramString.replaceAll("\r\n", "\r").replaceAll("\n", "\r").split("\r"));
  }
  
  private boolean isModifier(int paramInt)
  {
    return (Character.getType(paramInt) == 16) || (Character.getType(paramInt) == 27) || (Character.getType(paramInt) == 6) || (Character.getType(paramInt) == 28) || (Character.getType(paramInt) == 19);
  }
  
  public void addValueCallback(Object paramObject, LottieValueCallback paramLottieValueCallback)
  {
    super.addValueCallback(paramObject, paramLottieValueCallback);
    if ((paramObject == LottieProperty.COLOR) && (colorAnimation != null))
    {
      colorAnimation.setValueCallback(paramLottieValueCallback);
      return;
    }
    if ((paramObject == LottieProperty.STROKE_COLOR) && (strokeColorAnimation != null))
    {
      strokeColorAnimation.setValueCallback(paramLottieValueCallback);
      return;
    }
    if ((paramObject == LottieProperty.STROKE_WIDTH) && (strokeWidthAnimation != null))
    {
      strokeWidthAnimation.setValueCallback(paramLottieValueCallback);
      return;
    }
    if ((paramObject == LottieProperty.TEXT_TRACKING) && (trackingAnimation != null)) {
      trackingAnimation.setValueCallback(paramLottieValueCallback);
    }
  }
  
  void drawLayer(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    paramCanvas.save();
    if (!lottieDrawable.useTextGlyphs()) {
      paramCanvas.setMatrix(paramMatrix);
    }
    DocumentData localDocumentData = (DocumentData)textAnimation.getValue();
    Font localFont = (Font)composition.getFonts().get(fontName);
    if (localFont == null)
    {
      paramCanvas.restore();
      return;
    }
    if (colorAnimation != null) {
      fillPaint.setColor(((Integer)colorAnimation.getValue()).intValue());
    } else {
      fillPaint.setColor(color);
    }
    if (strokeColorAnimation != null) {
      strokePaint.setColor(((Integer)strokeColorAnimation.getValue()).intValue());
    } else {
      strokePaint.setColor(strokeColor);
    }
    if (transform.getOpacity() == null) {
      paramInt = 100;
    } else {
      paramInt = ((Integer)transform.getOpacity().getValue()).intValue();
    }
    paramInt = paramInt * 255 / 100;
    fillPaint.setAlpha(paramInt);
    strokePaint.setAlpha(paramInt);
    if (strokeWidthAnimation != null)
    {
      strokePaint.setStrokeWidth(((Float)strokeWidthAnimation.getValue()).floatValue());
    }
    else
    {
      float f = Utils.getScale(paramMatrix);
      strokePaint.setStrokeWidth((float)(strokeWidth * Utils.dpScale() * f));
    }
    if (lottieDrawable.useTextGlyphs()) {
      drawTextGlyphs(localDocumentData, paramMatrix, localFont, paramCanvas);
    } else {
      drawTextWithFont(localDocumentData, localFont, paramMatrix, paramCanvas);
    }
    paramCanvas.restore();
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix, boolean paramBoolean)
  {
    super.getBounds(paramRectF, paramMatrix, paramBoolean);
    paramRectF.set(0.0F, 0.0F, composition.getBounds().width(), composition.getBounds().height());
  }
}