package com.facebook.imagepipeline.decoder;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import com.facebook.common.references.CloseableReference;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.platform.PlatformDecoder;
import com.facebook.imagepipeline.transformation.BitmapTransformation;
import java.util.Map;
import javax.annotation.Nullable;

public class DefaultImageDecoder
  implements ImageDecoder
{
  private final ImageDecoder mAnimatedGifDecoder;
  private final ImageDecoder mAnimatedWebPDecoder;
  @Nullable
  private final Map<ImageFormat, ImageDecoder> mCustomDecoders;
  private final ImageDecoder mDefaultDecoder = new ImageDecoder()
  {
    public CloseableImage decode(EncodedImage paramAnonymousEncodedImage, int paramAnonymousInt, QualityInfo paramAnonymousQualityInfo, ImageDecodeOptions paramAnonymousImageDecodeOptions)
    {
      ImageFormat localImageFormat = paramAnonymousEncodedImage.getImageFormat();
      if (localImageFormat == DefaultImageFormats.JPEG) {
        return decodeJpeg(paramAnonymousEncodedImage, paramAnonymousInt, paramAnonymousQualityInfo, paramAnonymousImageDecodeOptions);
      }
      if (localImageFormat == DefaultImageFormats.UNKNOWN) {
        return decodeGif(paramAnonymousEncodedImage, paramAnonymousInt, paramAnonymousQualityInfo, paramAnonymousImageDecodeOptions);
      }
      if (localImageFormat == DefaultImageFormats.WEBP_ANIMATED) {
        return decodeAnimatedWebp(paramAnonymousEncodedImage, paramAnonymousInt, paramAnonymousQualityInfo, paramAnonymousImageDecodeOptions);
      }
      if (localImageFormat != ImageFormat.UNKNOWN) {
        return decodeStaticImage(paramAnonymousEncodedImage, paramAnonymousImageDecodeOptions);
      }
      throw new DecodeException("unknown image format", paramAnonymousEncodedImage);
    }
  };
  private final PlatformDecoder mPlatformDecoder;
  
  public DefaultImageDecoder(ImageDecoder paramImageDecoder1, ImageDecoder paramImageDecoder2, PlatformDecoder paramPlatformDecoder)
  {
    this(paramImageDecoder1, paramImageDecoder2, paramPlatformDecoder, null);
  }
  
  public DefaultImageDecoder(ImageDecoder paramImageDecoder1, ImageDecoder paramImageDecoder2, PlatformDecoder paramPlatformDecoder, Map paramMap)
  {
    mAnimatedGifDecoder = paramImageDecoder1;
    mAnimatedWebPDecoder = paramImageDecoder2;
    mPlatformDecoder = paramPlatformDecoder;
    mCustomDecoders = paramMap;
  }
  
  private void maybeApplyTransformation(BitmapTransformation paramBitmapTransformation, CloseableReference paramCloseableReference)
  {
    if (paramBitmapTransformation == null) {
      return;
    }
    paramCloseableReference = (Bitmap)paramCloseableReference.get();
    if ((Build.VERSION.SDK_INT >= 12) && (paramBitmapTransformation.modifiesTransparency())) {
      paramCloseableReference.setHasAlpha(true);
    }
    paramBitmapTransformation.transform(paramCloseableReference);
  }
  
  public CloseableImage decode(EncodedImage paramEncodedImage, int paramInt, QualityInfo paramQualityInfo, ImageDecodeOptions paramImageDecodeOptions)
  {
    if (customImageDecoder != null) {
      return customImageDecoder.decode(paramEncodedImage, paramInt, paramQualityInfo, paramImageDecodeOptions);
    }
    ImageFormat localImageFormat = paramEncodedImage.getImageFormat();
    Object localObject = localImageFormat;
    if ((localImageFormat == null) || (localImageFormat == ImageFormat.UNKNOWN))
    {
      localImageFormat = ImageFormatChecker.getImageFormat_WrapIOException(paramEncodedImage.getInputStream());
      localObject = localImageFormat;
      paramEncodedImage.setImageFormat(localImageFormat);
    }
    if (mCustomDecoders != null)
    {
      localObject = (ImageDecoder)mCustomDecoders.get(localObject);
      if (localObject != null) {
        return ((ImageDecoder)localObject).decode(paramEncodedImage, paramInt, paramQualityInfo, paramImageDecodeOptions);
      }
    }
    return mDefaultDecoder.decode(paramEncodedImage, paramInt, paramQualityInfo, paramImageDecodeOptions);
  }
  
  public CloseableImage decodeAnimatedWebp(EncodedImage paramEncodedImage, int paramInt, QualityInfo paramQualityInfo, ImageDecodeOptions paramImageDecodeOptions)
  {
    return mAnimatedWebPDecoder.decode(paramEncodedImage, paramInt, paramQualityInfo, paramImageDecodeOptions);
  }
  
  public CloseableImage decodeGif(EncodedImage paramEncodedImage, int paramInt, QualityInfo paramQualityInfo, ImageDecodeOptions paramImageDecodeOptions)
  {
    if ((paramEncodedImage.getWidth() != -1) && (paramEncodedImage.getHeight() != -1))
    {
      if ((!forceStaticImage) && (mAnimatedGifDecoder != null)) {
        return mAnimatedGifDecoder.decode(paramEncodedImage, paramInt, paramQualityInfo, paramImageDecodeOptions);
      }
      return decodeStaticImage(paramEncodedImage, paramImageDecodeOptions);
    }
    throw new DecodeException("image width or height is incorrect", paramEncodedImage);
  }
  
  public CloseableStaticBitmap decodeJpeg(EncodedImage paramEncodedImage, int paramInt, QualityInfo paramQualityInfo, ImageDecodeOptions paramImageDecodeOptions)
  {
    CloseableReference localCloseableReference = mPlatformDecoder.decodeJPEGFromEncodedImageWithColorSpace(paramEncodedImage, bitmapConfig, null, paramInt, colorSpace);
    try
    {
      maybeApplyTransformation(bitmapTransformation, localCloseableReference);
      paramEncodedImage = new CloseableStaticBitmap(localCloseableReference, paramQualityInfo, paramEncodedImage.getRotationAngle(), paramEncodedImage.getExifOrientation());
      localCloseableReference.close();
      return paramEncodedImage;
    }
    catch (Throwable paramEncodedImage)
    {
      localCloseableReference.close();
      throw paramEncodedImage;
    }
  }
  
  public CloseableStaticBitmap decodeStaticImage(EncodedImage paramEncodedImage, ImageDecodeOptions paramImageDecodeOptions)
  {
    CloseableReference localCloseableReference = mPlatformDecoder.decodeFromEncodedImageWithColorSpace(paramEncodedImage, bitmapConfig, null, colorSpace);
    try
    {
      maybeApplyTransformation(bitmapTransformation, localCloseableReference);
      paramEncodedImage = new CloseableStaticBitmap(localCloseableReference, ImmutableQualityInfo.FULL_QUALITY, paramEncodedImage.getRotationAngle(), paramEncodedImage.getExifOrientation());
      localCloseableReference.close();
      return paramEncodedImage;
    }
    catch (Throwable paramEncodedImage)
    {
      localCloseableReference.close();
      throw paramEncodedImage;
    }
  }
}
