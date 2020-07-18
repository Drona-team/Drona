package androidx.exifinterface.media;

import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaDataSource;
import android.media.MediaMetadataRetriever;
import android.os.Build.VERSION;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.RestrictTo;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExifInterface
{
  public static final short ALTITUDE_ABOVE_SEA_LEVEL = 0;
  public static final short ALTITUDE_BELOW_SEA_LEVEL = 1;
  static final Charset ASCII;
  public static final int[] BITS_PER_SAMPLE_GREYSCALE_1;
  public static final int[] BITS_PER_SAMPLE_GREYSCALE_2;
  public static final int[] BITS_PER_SAMPLE_RGB;
  static final short BYTE_ALIGN_II = 18761;
  static final short BYTE_ALIGN_MM = 19789;
  public static final int COLOR_SPACE_S_RGB = 1;
  public static final int COLOR_SPACE_UNCALIBRATED = 65535;
  public static final short CONTRAST_HARD = 2;
  public static final short CONTRAST_NORMAL = 0;
  public static final short CONTRAST_SOFT = 1;
  public static final int DATA_DEFLATE_ZIP = 8;
  public static final int DATA_HUFFMAN_COMPRESSED = 2;
  public static final int DATA_JPEG = 6;
  public static final int DATA_JPEG_COMPRESSED = 7;
  public static final int DATA_LOSSY_JPEG = 34892;
  public static final int DATA_PACK_BITS_COMPRESSED = 32773;
  public static final int DATA_UNCOMPRESSED = 1;
  private static final boolean DEBUG = Log.isLoggable("ExifInterface", 3);
  static final byte[] EXIF_ASCII_PREFIX;
  private static final ExifTag[] EXIF_POINTER_TAGS;
  static final ExifTag[][] EXIF_TAGS;
  public static final short EXPOSURE_MODE_AUTO = 0;
  public static final short EXPOSURE_MODE_AUTO_BRACKET = 2;
  public static final short EXPOSURE_MODE_MANUAL = 1;
  public static final short EXPOSURE_PROGRAM_ACTION = 6;
  public static final short EXPOSURE_PROGRAM_APERTURE_PRIORITY = 3;
  public static final short EXPOSURE_PROGRAM_CREATIVE = 5;
  public static final short EXPOSURE_PROGRAM_LANDSCAPE_MODE = 8;
  public static final short EXPOSURE_PROGRAM_MANUAL = 1;
  public static final short EXPOSURE_PROGRAM_NORMAL = 2;
  public static final short EXPOSURE_PROGRAM_NOT_DEFINED = 0;
  public static final short EXPOSURE_PROGRAM_PORTRAIT_MODE = 7;
  public static final short EXPOSURE_PROGRAM_SHUTTER_PRIORITY = 4;
  public static final short FILE_SOURCE_DSC = 3;
  public static final short FILE_SOURCE_OTHER = 0;
  public static final short FILE_SOURCE_REFLEX_SCANNER = 2;
  public static final short FILE_SOURCE_TRANSPARENT_SCANNER = 1;
  public static final short FLAG_FLASH_FIRED = 1;
  public static final short FLAG_FLASH_MODE_AUTO = 24;
  public static final short FLAG_FLASH_MODE_COMPULSORY_FIRING = 8;
  public static final short FLAG_FLASH_MODE_COMPULSORY_SUPPRESSION = 16;
  public static final short FLAG_FLASH_NO_FLASH_FUNCTION = 32;
  public static final short FLAG_FLASH_RED_EYE_SUPPORTED = 64;
  public static final short FLAG_FLASH_RETURN_LIGHT_DETECTED = 6;
  public static final short FLAG_FLASH_RETURN_LIGHT_NOT_DETECTED = 4;
  private static final List<Integer> FLIPPED_ROTATION_ORDER;
  public static final short FORMAT_CHUNKY = 1;
  public static final short FORMAT_PLANAR = 2;
  public static final short GAIN_CONTROL_HIGH_GAIN_DOWN = 4;
  public static final short GAIN_CONTROL_HIGH_GAIN_UP = 2;
  public static final short GAIN_CONTROL_LOW_GAIN_DOWN = 3;
  public static final short GAIN_CONTROL_LOW_GAIN_UP = 1;
  public static final short GAIN_CONTROL_NONE = 0;
  public static final String GPS_DIRECTION_MAGNETIC = "M";
  public static final String GPS_DIRECTION_TRUE = "T";
  public static final String GPS_DISTANCE_KILOMETERS = "K";
  public static final String GPS_DISTANCE_MILES = "M";
  public static final String GPS_DISTANCE_NAUTICAL_MILES = "N";
  public static final String GPS_MEASUREMENT_2D = "2";
  public static final String GPS_MEASUREMENT_3D = "3";
  public static final short GPS_MEASUREMENT_DIFFERENTIAL_CORRECTED = 1;
  public static final String GPS_MEASUREMENT_INTERRUPTED = "V";
  public static final String GPS_MEASUREMENT_IN_PROGRESS = "A";
  public static final short GPS_MEASUREMENT_NO_DIFFERENTIAL = 0;
  public static final String GPS_SPEED_KILOMETERS_PER_HOUR = "K";
  public static final String GPS_SPEED_KNOTS = "N";
  public static final String GPS_SPEED_MILES_PER_HOUR = "M";
  private static final byte[] HEIF_BRAND_HEIC;
  private static final byte[] HEIF_BRAND_MIF1;
  private static final byte[] HEIF_TYPE_FTYP;
  static final byte[] IDENTIFIER_EXIF_APP1;
  private static final byte[] IDENTIFIER_XMP_APP1;
  private static final ExifTag[] IFD_EXIF_TAGS;
  private static final int IFD_FORMAT_BYTE = 1;
  static final int[] IFD_FORMAT_BYTES_PER_FORMAT;
  private static final int IFD_FORMAT_DOUBLE = 12;
  private static final int IFD_FORMAT_IFD = 13;
  static final String[] IFD_FORMAT_NAMES;
  private static final int IFD_FORMAT_SBYTE = 6;
  private static final int IFD_FORMAT_SINGLE = 11;
  private static final int IFD_FORMAT_SLONG = 9;
  private static final int IFD_FORMAT_SRATIONAL = 10;
  private static final int IFD_FORMAT_SSHORT = 8;
  private static final int IFD_FORMAT_STRING = 2;
  private static final int IFD_FORMAT_ULONG = 4;
  private static final int IFD_FORMAT_UNDEFINED = 7;
  private static final int IFD_FORMAT_URATIONAL = 5;
  private static final int IFD_FORMAT_USHORT = 3;
  private static final ExifTag[] IFD_GPS_TAGS;
  private static final ExifTag[] IFD_INTEROPERABILITY_TAGS;
  private static final int IFD_OFFSET = 8;
  private static final ExifTag[] IFD_THUMBNAIL_TAGS;
  private static final ExifTag[] IFD_TIFF_TAGS;
  private static final int IFD_TYPE_EXIF = 1;
  private static final int IFD_TYPE_GPS = 2;
  private static final int IFD_TYPE_INTEROPERABILITY = 3;
  private static final int IFD_TYPE_ORF_CAMERA_SETTINGS = 7;
  private static final int IFD_TYPE_ORF_IMAGE_PROCESSING = 8;
  private static final int IFD_TYPE_ORF_MAKER_NOTE = 6;
  private static final int IFD_TYPE_PEF = 9;
  static final int IFD_TYPE_PREVIEW = 5;
  static final int IFD_TYPE_PRIMARY = 0;
  static final int IFD_TYPE_THUMBNAIL = 4;
  private static final int IMAGE_TYPE_ARW = 1;
  private static final int IMAGE_TYPE_CR2 = 2;
  private static final int IMAGE_TYPE_DNG = 3;
  private static final int IMAGE_TYPE_HEIF = 12;
  private static final int IMAGE_TYPE_JPEG = 4;
  private static final int IMAGE_TYPE_NEF = 5;
  private static final int IMAGE_TYPE_NRW = 6;
  private static final int IMAGE_TYPE_ORF = 7;
  private static final int IMAGE_TYPE_PEF = 8;
  private static final int IMAGE_TYPE_RAF = 9;
  private static final int IMAGE_TYPE_RW2 = 10;
  private static final int IMAGE_TYPE_SRW = 11;
  private static final int IMAGE_TYPE_UNKNOWN = 0;
  private static final ExifTag JPEG_INTERCHANGE_FORMAT_LENGTH_TAG;
  private static final ExifTag JPEG_INTERCHANGE_FORMAT_TAG;
  static final byte[] JPEG_SIGNATURE;
  public static final String LATITUDE_NORTH = "N";
  public static final String LATITUDE_SOUTH = "S";
  public static final short LIGHT_SOURCE_CLOUDY_WEATHER = 10;
  public static final short LIGHT_SOURCE_COOL_WHITE_FLUORESCENT = 14;
  public static final short LIGHT_SOURCE_D50 = 23;
  public static final short LIGHT_SOURCE_D55 = 20;
  public static final short LIGHT_SOURCE_D65 = 21;
  public static final short LIGHT_SOURCE_D75 = 22;
  public static final short LIGHT_SOURCE_DAYLIGHT = 1;
  public static final short LIGHT_SOURCE_DAYLIGHT_FLUORESCENT = 12;
  public static final short LIGHT_SOURCE_DAY_WHITE_FLUORESCENT = 13;
  public static final short LIGHT_SOURCE_FINE_WEATHER = 9;
  public static final short LIGHT_SOURCE_FLASH = 4;
  public static final short LIGHT_SOURCE_FLUORESCENT = 2;
  public static final short LIGHT_SOURCE_ISO_STUDIO_TUNGSTEN = 24;
  public static final short LIGHT_SOURCE_OTHER = 255;
  public static final short LIGHT_SOURCE_SHADE = 11;
  public static final short LIGHT_SOURCE_STANDARD_LIGHT_A = 17;
  public static final short LIGHT_SOURCE_STANDARD_LIGHT_B = 18;
  public static final short LIGHT_SOURCE_STANDARD_LIGHT_C = 19;
  public static final short LIGHT_SOURCE_TUNGSTEN = 3;
  public static final short LIGHT_SOURCE_UNKNOWN = 0;
  public static final short LIGHT_SOURCE_WARM_WHITE_FLUORESCENT = 16;
  public static final short LIGHT_SOURCE_WHITE_FLUORESCENT = 15;
  public static final String LONGITUDE_EAST = "E";
  public static final String LONGITUDE_WEST = "W";
  static final byte MARKER = -1;
  static final byte MARKER_APP1 = -31;
  private static final byte MARKER_COM = -2;
  static final byte MARKER_EOI = -39;
  private static final byte MARKER_SOF0 = -64;
  private static final byte MARKER_SOF1 = -63;
  private static final byte MARKER_SOF10 = -54;
  private static final byte MARKER_SOF11 = -53;
  private static final byte MARKER_SOF13 = -51;
  private static final byte MARKER_SOF14 = -50;
  private static final byte MARKER_SOF15 = -49;
  private static final byte MARKER_SOF2 = -62;
  private static final byte MARKER_SOF3 = -61;
  private static final byte MARKER_SOF5 = -59;
  private static final byte MARKER_SOF6 = -58;
  private static final byte MARKER_SOF7 = -57;
  private static final byte MARKER_SOF9 = -55;
  private static final byte MARKER_SOI = -40;
  private static final byte MARKER_SOS = -38;
  private static final int MAX_THUMBNAIL_SIZE = 512;
  public static final short METERING_MODE_AVERAGE = 1;
  public static final short METERING_MODE_CENTER_WEIGHT_AVERAGE = 2;
  public static final short METERING_MODE_MULTI_SPOT = 4;
  public static final short METERING_MODE_OTHER = 255;
  public static final short METERING_MODE_PARTIAL = 6;
  public static final short METERING_MODE_PATTERN = 5;
  public static final short METERING_MODE_SPOT = 3;
  public static final short METERING_MODE_UNKNOWN = 0;
  private static final ExifTag[] ORF_CAMERA_SETTINGS_TAGS;
  private static final ExifTag[] ORF_IMAGE_PROCESSING_TAGS;
  private static final byte[] ORF_MAKER_NOTE_HEADER_1;
  private static final int ORF_MAKER_NOTE_HEADER_1_SIZE = 8;
  private static final byte[] ORF_MAKER_NOTE_HEADER_2;
  private static final int ORF_MAKER_NOTE_HEADER_2_SIZE = 12;
  private static final ExifTag[] ORF_MAKER_NOTE_TAGS;
  private static final short ORF_SIGNATURE_1 = 20306;
  private static final short ORF_SIGNATURE_2 = 21330;
  public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
  public static final int ORIENTATION_FLIP_VERTICAL = 4;
  public static final int ORIENTATION_NORMAL = 1;
  public static final int ORIENTATION_ROTATE_180 = 3;
  public static final int ORIENTATION_ROTATE_270 = 8;
  public static final int ORIENTATION_ROTATE_90 = 6;
  public static final int ORIENTATION_TRANSPOSE = 5;
  public static final int ORIENTATION_TRANSVERSE = 7;
  public static final int ORIENTATION_UNDEFINED = 0;
  public static final int ORIGINAL_RESOLUTION_IMAGE = 0;
  private static final int PEF_MAKER_NOTE_SKIP_SIZE = 6;
  private static final String PEF_SIGNATURE = "PENTAX";
  private static final ExifTag[] PEF_TAGS;
  public static final int PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO = 1;
  public static final int PHOTOMETRIC_INTERPRETATION_RGB = 2;
  public static final int PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO = 0;
  public static final int PHOTOMETRIC_INTERPRETATION_YCBCR = 6;
  private static final int RAF_INFO_SIZE = 160;
  private static final int RAF_JPEG_LENGTH_VALUE_SIZE = 4;
  private static final int RAF_OFFSET_TO_JPEG_IMAGE_OFFSET = 84;
  private static final String RAF_SIGNATURE = "FUJIFILMCCD-RAW";
  public static final int REDUCED_RESOLUTION_IMAGE = 1;
  public static final short RENDERED_PROCESS_CUSTOM = 1;
  public static final short RENDERED_PROCESS_NORMAL = 0;
  public static final short RESOLUTION_UNIT_CENTIMETERS = 3;
  public static final short RESOLUTION_UNIT_INCHES = 2;
  private static final List<Integer> ROTATION_ORDER = Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(6), Integer.valueOf(3), Integer.valueOf(8) });
  private static final short RW2_SIGNATURE = 85;
  public static final short SATURATION_HIGH = 0;
  public static final short SATURATION_LOW = 0;
  public static final short SATURATION_NORMAL = 0;
  public static final short SCENE_CAPTURE_TYPE_LANDSCAPE = 1;
  public static final short SCENE_CAPTURE_TYPE_NIGHT = 3;
  public static final short SCENE_CAPTURE_TYPE_PORTRAIT = 2;
  public static final short SCENE_CAPTURE_TYPE_STANDARD = 0;
  public static final short SCENE_TYPE_DIRECTLY_PHOTOGRAPHED = 1;
  public static final short SENSITIVITY_TYPE_ISO_SPEED = 3;
  public static final short SENSITIVITY_TYPE_REI = 2;
  public static final short SENSITIVITY_TYPE_REI_AND_ISO = 6;
  public static final short SENSITIVITY_TYPE_SOS = 1;
  public static final short SENSITIVITY_TYPE_SOS_AND_ISO = 5;
  public static final short SENSITIVITY_TYPE_SOS_AND_REI = 4;
  public static final short SENSITIVITY_TYPE_SOS_AND_REI_AND_ISO = 7;
  public static final short SENSITIVITY_TYPE_UNKNOWN = 0;
  public static final short SENSOR_TYPE_COLOR_SEQUENTIAL = 5;
  public static final short SENSOR_TYPE_COLOR_SEQUENTIAL_LINEAR = 8;
  public static final short SENSOR_TYPE_NOT_DEFINED = 1;
  public static final short SENSOR_TYPE_ONE_CHIP = 2;
  public static final short SENSOR_TYPE_THREE_CHIP = 4;
  public static final short SENSOR_TYPE_TRILINEAR = 7;
  public static final short SENSOR_TYPE_TWO_CHIP = 3;
  public static final short SHARPNESS_HARD = 2;
  public static final short SHARPNESS_NORMAL = 0;
  public static final short SHARPNESS_SOFT = 1;
  private static final int SIGNATURE_CHECK_SIZE = 5000;
  static final byte START_CODE = 42;
  public static final short SUBJECT_DISTANCE_RANGE_CLOSE_VIEW = 2;
  public static final short SUBJECT_DISTANCE_RANGE_DISTANT_VIEW = 3;
  public static final short SUBJECT_DISTANCE_RANGE_MACRO = 1;
  public static final short SUBJECT_DISTANCE_RANGE_UNKNOWN = 0;
  private static final String TAG = "ExifInterface";
  public static final String TAG_APERTURE_VALUE = "ApertureValue";
  public static final String TAG_ARTIST = "Artist";
  public static final String TAG_BITS_PER_SAMPLE = "BitsPerSample";
  public static final String TAG_BODY_SERIAL_NUMBER = "BodySerialNumber";
  public static final String TAG_BRIGHTNESS_VALUE = "BrightnessValue";
  @Deprecated
  public static final String TAG_CAMARA_OWNER_NAME = "CameraOwnerName";
  public static final String TAG_CAMERA_OWNER_NAME = "CameraOwnerName";
  public static final String TAG_CFA_PATTERN = "CFAPattern";
  public static final String TAG_COLOR_SPACE = "ColorSpace";
  public static final String TAG_COMPONENTS_CONFIGURATION = "ComponentsConfiguration";
  public static final String TAG_COMPRESSED_BITS_PER_PIXEL = "CompressedBitsPerPixel";
  public static final String TAG_COMPRESSION = "Compression";
  public static final String TAG_CONTRAST = "Contrast";
  public static final String TAG_COPYRIGHT = "Copyright";
  public static final String TAG_CUSTOM_RENDERED = "CustomRendered";
  public static final String TAG_DATETIME = "DateTime";
  public static final String TAG_DATETIME_DIGITIZED = "DateTimeDigitized";
  public static final String TAG_DATETIME_ORIGINAL = "DateTimeOriginal";
  public static final String TAG_DEFAULT_CROP_SIZE = "DefaultCropSize";
  public static final String TAG_DEVICE_SETTING_DESCRIPTION = "DeviceSettingDescription";
  public static final String TAG_DIGITAL_ZOOM_RATIO = "DigitalZoomRatio";
  public static final String TAG_DNG_VERSION = "DNGVersion";
  private static final String TAG_EXIF_IFD_POINTER = "ExifIFDPointer";
  public static final String TAG_EXIF_VERSION = "ExifVersion";
  public static final String TAG_EXPOSURE_BIAS_VALUE = "ExposureBiasValue";
  public static final String TAG_EXPOSURE_INDEX = "ExposureIndex";
  public static final String TAG_EXPOSURE_MODE = "ExposureMode";
  public static final String TAG_EXPOSURE_PROGRAM = "ExposureProgram";
  public static final String TAG_EXPOSURE_TIME = "ExposureTime";
  public static final String TAG_FILE_SOURCE = "FileSource";
  public static final String TAG_FLASH = "Flash";
  public static final String TAG_FLASHPIX_VERSION = "FlashpixVersion";
  public static final String TAG_FLASH_ENERGY = "FlashEnergy";
  public static final String TAG_FOCAL_LENGTH = "FocalLength";
  public static final String TAG_FOCAL_LENGTH_IN_35MM_FILM = "FocalLengthIn35mmFilm";
  public static final String TAG_FOCAL_PLANE_RESOLUTION_UNIT = "FocalPlaneResolutionUnit";
  public static final String TAG_FOCAL_PLANE_X_RESOLUTION = "FocalPlaneXResolution";
  public static final String TAG_FOCAL_PLANE_Y_RESOLUTION = "FocalPlaneYResolution";
  public static final String TAG_F_NUMBER = "FNumber";
  public static final String TAG_GAIN_CONTROL = "GainControl";
  public static final String TAG_GAMMA = "Gamma";
  public static final String TAG_GPS_ALTITUDE = "GPSAltitude";
  public static final String TAG_GPS_ALTITUDE_REF = "GPSAltitudeRef";
  public static final String TAG_GPS_AREA_INFORMATION = "GPSAreaInformation";
  public static final String TAG_GPS_DATESTAMP = "GPSDateStamp";
  public static final String TAG_GPS_DEST_BEARING = "GPSDestBearing";
  public static final String TAG_GPS_DEST_BEARING_REF = "GPSDestBearingRef";
  public static final String TAG_GPS_DEST_DISTANCE = "GPSDestDistance";
  public static final String TAG_GPS_DEST_DISTANCE_REF = "GPSDestDistanceRef";
  public static final String TAG_GPS_DEST_LATITUDE = "GPSDestLatitude";
  public static final String TAG_GPS_DEST_LATITUDE_REF = "GPSDestLatitudeRef";
  public static final String TAG_GPS_DEST_LONGITUDE = "GPSDestLongitude";
  public static final String TAG_GPS_DEST_LONGITUDE_REF = "GPSDestLongitudeRef";
  public static final String TAG_GPS_DIFFERENTIAL = "GPSDifferential";
  public static final String TAG_GPS_DOP = "GPSDOP";
  public static final String TAG_GPS_H_POSITIONING_ERROR = "GPSHPositioningError";
  public static final String TAG_GPS_IMG_DIRECTION = "GPSImgDirection";
  public static final String TAG_GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef";
  private static final String TAG_GPS_INFO_IFD_POINTER = "GPSInfoIFDPointer";
  public static final String TAG_GPS_LATITUDE = "GPSLatitude";
  public static final String TAG_GPS_LATITUDE_REF = "GPSLatitudeRef";
  public static final String TAG_GPS_LONGITUDE = "GPSLongitude";
  public static final String TAG_GPS_LONGITUDE_REF = "GPSLongitudeRef";
  public static final String TAG_GPS_MAP_DATUM = "GPSMapDatum";
  public static final String TAG_GPS_MEASURE_MODE = "GPSMeasureMode";
  public static final String TAG_GPS_PROCESSING_METHOD = "GPSProcessingMethod";
  public static final String TAG_GPS_SATELLITES = "GPSSatellites";
  public static final String TAG_GPS_SPEED = "GPSSpeed";
  public static final String TAG_GPS_SPEED_REF = "GPSSpeedRef";
  public static final String TAG_GPS_STATUS = "GPSStatus";
  public static final String TAG_GPS_TIMESTAMP = "GPSTimeStamp";
  public static final String TAG_GPS_TRACK = "GPSTrack";
  public static final String TAG_GPS_TRACK_REF = "GPSTrackRef";
  public static final String TAG_GPS_VERSION_ID = "GPSVersionID";
  private static final String TAG_HAS_THUMBNAIL = "HasThumbnail";
  public static final String TAG_IMAGE_DESCRIPTION = "ImageDescription";
  public static final String TAG_IMAGE_LENGTH = "ImageLength";
  public static final String TAG_IMAGE_UNIQUE_ID = "ImageUniqueID";
  public static final String TAG_IMAGE_WIDTH = "ImageWidth";
  private static final String TAG_INTEROPERABILITY_IFD_POINTER = "InteroperabilityIFDPointer";
  public static final String TAG_INTEROPERABILITY_INDEX = "InteroperabilityIndex";
  public static final String TAG_ISO_SPEED = "ISOSpeed";
  public static final String TAG_ISO_SPEED_LATITUDE_YYY = "ISOSpeedLatitudeyyy";
  public static final String TAG_ISO_SPEED_LATITUDE_ZZZ = "ISOSpeedLatitudezzz";
  @Deprecated
  public static final String TAG_ISO_SPEED_RATINGS = "ISOSpeedRatings";
  public static final String TAG_JPEG_INTERCHANGE_FORMAT = "JPEGInterchangeFormat";
  public static final String TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = "JPEGInterchangeFormatLength";
  public static final String TAG_LENS_MAKE = "LensMake";
  public static final String TAG_LENS_MODEL = "LensModel";
  public static final String TAG_LENS_SERIAL_NUMBER = "LensSerialNumber";
  public static final String TAG_LENS_SPECIFICATION = "LensSpecification";
  public static final String TAG_LIGHT_SOURCE = "LightSource";
  public static final String TAG_MAKE = "Make";
  public static final String TAG_MAKER_NOTE = "MakerNote";
  public static final String TAG_MAX_APERTURE_VALUE = "MaxApertureValue";
  public static final String TAG_METERING_MODE = "MeteringMode";
  public static final String TAG_MODEL = "Model";
  public static final String TAG_NEW_SUBFILE_TYPE = "NewSubfileType";
  public static final String TAG_OECF = "OECF";
  public static final String TAG_ORF_ASPECT_FRAME = "AspectFrame";
  private static final String TAG_ORF_CAMERA_SETTINGS_IFD_POINTER = "CameraSettingsIFDPointer";
  private static final String TAG_ORF_IMAGE_PROCESSING_IFD_POINTER = "ImageProcessingIFDPointer";
  public static final String TAG_ORF_PREVIEW_IMAGE_LENGTH = "PreviewImageLength";
  public static final String TAG_ORF_PREVIEW_IMAGE_START = "PreviewImageStart";
  public static final String TAG_ORF_THUMBNAIL_IMAGE = "ThumbnailImage";
  public static final String TAG_ORIENTATION = "Orientation";
  public static final String TAG_PHOTOGRAPHIC_SENSITIVITY = "PhotographicSensitivity";
  public static final String TAG_PHOTOMETRIC_INTERPRETATION = "PhotometricInterpretation";
  public static final String TAG_PIXEL_X_DIMENSION = "PixelXDimension";
  public static final String TAG_PIXEL_Y_DIMENSION = "PixelYDimension";
  public static final String TAG_PLANAR_CONFIGURATION = "PlanarConfiguration";
  public static final String TAG_PRIMARY_CHROMATICITIES = "PrimaryChromaticities";
  private static final ExifTag TAG_RAF_IMAGE_SIZE;
  public static final String TAG_RECOMMENDED_EXPOSURE_INDEX = "RecommendedExposureIndex";
  public static final String TAG_REFERENCE_BLACK_WHITE = "ReferenceBlackWhite";
  public static final String TAG_RELATED_SOUND_FILE = "RelatedSoundFile";
  public static final String TAG_RESOLUTION_UNIT = "ResolutionUnit";
  public static final String TAG_ROWS_PER_STRIP = "RowsPerStrip";
  public static final String TAG_RW2_ISO = "ISO";
  public static final String TAG_RW2_JPG_FROM_RAW = "JpgFromRaw";
  public static final String TAG_RW2_SENSOR_BOTTOM_BORDER = "SensorBottomBorder";
  public static final String TAG_RW2_SENSOR_LEFT_BORDER = "SensorLeftBorder";
  public static final String TAG_RW2_SENSOR_RIGHT_BORDER = "SensorRightBorder";
  public static final String TAG_RW2_SENSOR_TOP_BORDER = "SensorTopBorder";
  public static final String TAG_SAMPLES_PER_PIXEL = "SamplesPerPixel";
  public static final String TAG_SATURATION = "Saturation";
  public static final String TAG_SCENE_CAPTURE_TYPE = "SceneCaptureType";
  public static final String TAG_SCENE_TYPE = "SceneType";
  public static final String TAG_SENSING_METHOD = "SensingMethod";
  public static final String TAG_SENSITIVITY_TYPE = "SensitivityType";
  public static final String TAG_SHARPNESS = "Sharpness";
  public static final String TAG_SHUTTER_SPEED_VALUE = "ShutterSpeedValue";
  public static final String TAG_SOFTWARE = "Software";
  public static final String TAG_SPATIAL_FREQUENCY_RESPONSE = "SpatialFrequencyResponse";
  public static final String TAG_SPECTRAL_SENSITIVITY = "SpectralSensitivity";
  public static final String TAG_STANDARD_OUTPUT_SENSITIVITY = "StandardOutputSensitivity";
  public static final String TAG_STRIP_BYTE_COUNTS = "StripByteCounts";
  public static final String TAG_STRIP_OFFSETS = "StripOffsets";
  public static final String TAG_SUBFILE_TYPE = "SubfileType";
  public static final String TAG_SUBJECT_AREA = "SubjectArea";
  public static final String TAG_SUBJECT_DISTANCE = "SubjectDistance";
  public static final String TAG_SUBJECT_DISTANCE_RANGE = "SubjectDistanceRange";
  public static final String TAG_SUBJECT_LOCATION = "SubjectLocation";
  public static final String TAG_SUBSEC_TIME = "SubSecTime";
  public static final String TAG_SUBSEC_TIME_DIGITIZED = "SubSecTimeDigitized";
  public static final String TAG_SUBSEC_TIME_ORIGINAL = "SubSecTimeOriginal";
  private static final String TAG_SUB_IFD_POINTER = "SubIFDPointer";
  private static final String TAG_THUMBNAIL_DATA = "ThumbnailData";
  public static final String TAG_THUMBNAIL_IMAGE_LENGTH = "ThumbnailImageLength";
  public static final String TAG_THUMBNAIL_IMAGE_WIDTH = "ThumbnailImageWidth";
  private static final String TAG_THUMBNAIL_LENGTH = "ThumbnailLength";
  private static final String TAG_THUMBNAIL_OFFSET = "ThumbnailOffset";
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public static final String TAG_THUMBNAIL_ORIENTATION = "ThumbnailOrientation";
  public static final String TAG_TRANSFER_FUNCTION = "TransferFunction";
  public static final String TAG_USER_COMMENT = "UserComment";
  public static final String TAG_WHITE_BALANCE = "WhiteBalance";
  public static final String TAG_WHITE_POINT = "WhitePoint";
  public static final String TAG_XMP = "Xmp";
  public static final String TAG_X_RESOLUTION = "XResolution";
  public static final String TAG_Y_CB_CR_COEFFICIENTS = "YCbCrCoefficients";
  public static final String TAG_Y_CB_CR_POSITIONING = "YCbCrPositioning";
  public static final String TAG_Y_CB_CR_SUB_SAMPLING = "YCbCrSubSampling";
  public static final String TAG_Y_RESOLUTION = "YResolution";
  @Deprecated
  public static final int WHITEBALANCE_AUTO = 0;
  @Deprecated
  public static final int WHITEBALANCE_MANUAL = 1;
  public static final short WHITE_BALANCE_AUTO = 0;
  public static final short WHITE_BALANCE_MANUAL = 1;
  public static final short Y_CB_CR_POSITIONING_CENTERED = 1;
  public static final short Y_CB_CR_POSITIONING_CO_SITED = 2;
  private static final HashMap<Integer, Integer> sExifPointerTagMap;
  private static final HashMap<Integer, ExifTag>[] sExifTagMapsForReading;
  private static final HashMap<String, ExifTag>[] sExifTagMapsForWriting;
  private static SimpleDateFormat sFormatter;
  private static final Pattern sGpsTimestampPattern = Pattern.compile("^([0-9][0-9]):([0-9][0-9]):([0-9][0-9])$");
  private static final Pattern sNonZeroTimePattern;
  private static final HashSet<String> sTagSetForCompatibility;
  private AssetManager.AssetInputStream mAssetInputStream;
  private final HashMap<String, ExifAttribute>[] mAttributes = new HashMap[EXIF_TAGS.length];
  private Set<Integer> mAttributesOffsets = new HashSet(EXIF_TAGS.length);
  private ByteOrder mExifByteOrder = ByteOrder.BIG_ENDIAN;
  private int mExifOffset;
  private String mFilename;
  private boolean mHasThumbnail;
  private boolean mIsSupportedFile;
  private int mMimeType;
  private boolean mModified;
  private int mOrfMakerNoteOffset;
  private int mOrfThumbnailLength;
  private int mOrfThumbnailOffset;
  private int mRw2JpgFromRawOffset;
  private FileDescriptor mSeekableFileDescriptor;
  private byte[] mThumbnailBytes;
  private int mThumbnailCompression;
  private int mThumbnailLength;
  private int mThumbnailOffset;
  
  static
  {
    FLIPPED_ROTATION_ORDER = Arrays.asList(new Integer[] { Integer.valueOf(2), Integer.valueOf(7), Integer.valueOf(4), Integer.valueOf(5) });
    BITS_PER_SAMPLE_RGB = new int[] { 8, 8, 8 };
    BITS_PER_SAMPLE_GREYSCALE_1 = new int[] { 4 };
    BITS_PER_SAMPLE_GREYSCALE_2 = new int[] { 8 };
    JPEG_SIGNATURE = new byte[] { -1, -40, -1 };
    HEIF_TYPE_FTYP = new byte[] { 102, 116, 121, 112 };
    HEIF_BRAND_MIF1 = new byte[] { 109, 105, 102, 49 };
    HEIF_BRAND_HEIC = new byte[] { 104, 101, 105, 99 };
    ORF_MAKER_NOTE_HEADER_1 = new byte[] { 79, 76, 89, 77, 80, 0 };
    ORF_MAKER_NOTE_HEADER_2 = new byte[] { 79, 76, 89, 77, 80, 85, 83, 0, 73, 73 };
    IFD_FORMAT_NAMES = new String[] { "", "BYTE", "STRING", "USHORT", "ULONG", "URATIONAL", "SBYTE", "UNDEFINED", "SSHORT", "SLONG", "SRATIONAL", "SINGLE", "DOUBLE", "IFD" };
    IFD_FORMAT_BYTES_PER_FORMAT = new int[] { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8, 1 };
    EXIF_ASCII_PREFIX = new byte[] { 65, 83, 67, 73, 73, 0, 0, 0 };
    IFD_TIFF_TAGS = new ExifTag[] { new ExifTag("NewSubfileType", 254, 4), new ExifTag("SubfileType", 255, 4), new ExifTag("ImageWidth", 256, 3, 4), new ExifTag("ImageLength", 257, 3, 4), new ExifTag("BitsPerSample", 258, 3), new ExifTag("Compression", 259, 3), new ExifTag("PhotometricInterpretation", 262, 3), new ExifTag("ImageDescription", 270, 2), new ExifTag("Make", 271, 2), new ExifTag("Model", 272, 2), new ExifTag("StripOffsets", 273, 3, 4), new ExifTag("Orientation", 274, 3), new ExifTag("SamplesPerPixel", 277, 3), new ExifTag("RowsPerStrip", 278, 3, 4), new ExifTag("StripByteCounts", 279, 3, 4), new ExifTag("XResolution", 282, 5), new ExifTag("YResolution", 283, 5), new ExifTag("PlanarConfiguration", 284, 3), new ExifTag("ResolutionUnit", 296, 3), new ExifTag("TransferFunction", 301, 3), new ExifTag("Software", 305, 2), new ExifTag("DateTime", 306, 2), new ExifTag("Artist", 315, 2), new ExifTag("WhitePoint", 318, 5), new ExifTag("PrimaryChromaticities", 319, 5), new ExifTag("SubIFDPointer", 330, 4), new ExifTag("JPEGInterchangeFormat", 513, 4), new ExifTag("JPEGInterchangeFormatLength", 514, 4), new ExifTag("YCbCrCoefficients", 529, 5), new ExifTag("YCbCrSubSampling", 530, 3), new ExifTag("YCbCrPositioning", 531, 3), new ExifTag("ReferenceBlackWhite", 532, 5), new ExifTag("Copyright", 33432, 2), new ExifTag("ExifIFDPointer", 34665, 4), new ExifTag("GPSInfoIFDPointer", 34853, 4), new ExifTag("SensorTopBorder", 4, 4), new ExifTag("SensorLeftBorder", 5, 4), new ExifTag("SensorBottomBorder", 6, 4), new ExifTag("SensorRightBorder", 7, 4), new ExifTag("ISO", 23, 3), new ExifTag("JpgFromRaw", 46, 7), new ExifTag("Xmp", 700, 1) };
    IFD_EXIF_TAGS = new ExifTag[] { new ExifTag("ExposureTime", 33434, 5), new ExifTag("FNumber", 33437, 5), new ExifTag("ExposureProgram", 34850, 3), new ExifTag("SpectralSensitivity", 34852, 2), new ExifTag("PhotographicSensitivity", 34855, 3), new ExifTag("OECF", 34856, 7), new ExifTag("ExifVersion", 36864, 2), new ExifTag("DateTimeOriginal", 36867, 2), new ExifTag("DateTimeDigitized", 36868, 2), new ExifTag("ComponentsConfiguration", 37121, 7), new ExifTag("CompressedBitsPerPixel", 37122, 5), new ExifTag("ShutterSpeedValue", 37377, 10), new ExifTag("ApertureValue", 37378, 5), new ExifTag("BrightnessValue", 37379, 10), new ExifTag("ExposureBiasValue", 37380, 10), new ExifTag("MaxApertureValue", 37381, 5), new ExifTag("SubjectDistance", 37382, 5), new ExifTag("MeteringMode", 37383, 3), new ExifTag("LightSource", 37384, 3), new ExifTag("Flash", 37385, 3), new ExifTag("FocalLength", 37386, 5), new ExifTag("SubjectArea", 37396, 3), new ExifTag("MakerNote", 37500, 7), new ExifTag("UserComment", 37510, 7), new ExifTag("SubSecTime", 37520, 2), new ExifTag("SubSecTimeOriginal", 37521, 2), new ExifTag("SubSecTimeDigitized", 37522, 2), new ExifTag("FlashpixVersion", 40960, 7), new ExifTag("ColorSpace", 40961, 3), new ExifTag("PixelXDimension", 40962, 3, 4), new ExifTag("PixelYDimension", 40963, 3, 4), new ExifTag("RelatedSoundFile", 40964, 2), new ExifTag("InteroperabilityIFDPointer", 40965, 4), new ExifTag("FlashEnergy", 41483, 5), new ExifTag("SpatialFrequencyResponse", 41484, 7), new ExifTag("FocalPlaneXResolution", 41486, 5), new ExifTag("FocalPlaneYResolution", 41487, 5), new ExifTag("FocalPlaneResolutionUnit", 41488, 3), new ExifTag("SubjectLocation", 41492, 3), new ExifTag("ExposureIndex", 41493, 5), new ExifTag("SensingMethod", 41495, 3), new ExifTag("FileSource", 41728, 7), new ExifTag("SceneType", 41729, 7), new ExifTag("CFAPattern", 41730, 7), new ExifTag("CustomRendered", 41985, 3), new ExifTag("ExposureMode", 41986, 3), new ExifTag("WhiteBalance", 41987, 3), new ExifTag("DigitalZoomRatio", 41988, 5), new ExifTag("FocalLengthIn35mmFilm", 41989, 3), new ExifTag("SceneCaptureType", 41990, 3), new ExifTag("GainControl", 41991, 3), new ExifTag("Contrast", 41992, 3), new ExifTag("Saturation", 41993, 3), new ExifTag("Sharpness", 41994, 3), new ExifTag("DeviceSettingDescription", 41995, 7), new ExifTag("SubjectDistanceRange", 41996, 3), new ExifTag("ImageUniqueID", 42016, 2), new ExifTag("DNGVersion", 50706, 1), new ExifTag("DefaultCropSize", 50720, 3, 4) };
    IFD_GPS_TAGS = new ExifTag[] { new ExifTag("GPSVersionID", 0, 1), new ExifTag("GPSLatitudeRef", 1, 2), new ExifTag("GPSLatitude", 2, 5), new ExifTag("GPSLongitudeRef", 3, 2), new ExifTag("GPSLongitude", 4, 5), new ExifTag("GPSAltitudeRef", 5, 1), new ExifTag("GPSAltitude", 6, 5), new ExifTag("GPSTimeStamp", 7, 5), new ExifTag("GPSSatellites", 8, 2), new ExifTag("GPSStatus", 9, 2), new ExifTag("GPSMeasureMode", 10, 2), new ExifTag("GPSDOP", 11, 5), new ExifTag("GPSSpeedRef", 12, 2), new ExifTag("GPSSpeed", 13, 5), new ExifTag("GPSTrackRef", 14, 2), new ExifTag("GPSTrack", 15, 5), new ExifTag("GPSImgDirectionRef", 16, 2), new ExifTag("GPSImgDirection", 17, 5), new ExifTag("GPSMapDatum", 18, 2), new ExifTag("GPSDestLatitudeRef", 19, 2), new ExifTag("GPSDestLatitude", 20, 5), new ExifTag("GPSDestLongitudeRef", 21, 2), new ExifTag("GPSDestLongitude", 22, 5), new ExifTag("GPSDestBearingRef", 23, 2), new ExifTag("GPSDestBearing", 24, 5), new ExifTag("GPSDestDistanceRef", 25, 2), new ExifTag("GPSDestDistance", 26, 5), new ExifTag("GPSProcessingMethod", 27, 7), new ExifTag("GPSAreaInformation", 28, 7), new ExifTag("GPSDateStamp", 29, 2), new ExifTag("GPSDifferential", 30, 3) };
    IFD_INTEROPERABILITY_TAGS = new ExifTag[] { new ExifTag("InteroperabilityIndex", 1, 2) };
    IFD_THUMBNAIL_TAGS = new ExifTag[] { new ExifTag("NewSubfileType", 254, 4), new ExifTag("SubfileType", 255, 4), new ExifTag("ThumbnailImageWidth", 256, 3, 4), new ExifTag("ThumbnailImageLength", 257, 3, 4), new ExifTag("BitsPerSample", 258, 3), new ExifTag("Compression", 259, 3), new ExifTag("PhotometricInterpretation", 262, 3), new ExifTag("ImageDescription", 270, 2), new ExifTag("Make", 271, 2), new ExifTag("Model", 272, 2), new ExifTag("StripOffsets", 273, 3, 4), new ExifTag("ThumbnailOrientation", 274, 3), new ExifTag("SamplesPerPixel", 277, 3), new ExifTag("RowsPerStrip", 278, 3, 4), new ExifTag("StripByteCounts", 279, 3, 4), new ExifTag("XResolution", 282, 5), new ExifTag("YResolution", 283, 5), new ExifTag("PlanarConfiguration", 284, 3), new ExifTag("ResolutionUnit", 296, 3), new ExifTag("TransferFunction", 301, 3), new ExifTag("Software", 305, 2), new ExifTag("DateTime", 306, 2), new ExifTag("Artist", 315, 2), new ExifTag("WhitePoint", 318, 5), new ExifTag("PrimaryChromaticities", 319, 5), new ExifTag("SubIFDPointer", 330, 4), new ExifTag("JPEGInterchangeFormat", 513, 4), new ExifTag("JPEGInterchangeFormatLength", 514, 4), new ExifTag("YCbCrCoefficients", 529, 5), new ExifTag("YCbCrSubSampling", 530, 3), new ExifTag("YCbCrPositioning", 531, 3), new ExifTag("ReferenceBlackWhite", 532, 5), new ExifTag("Copyright", 33432, 2), new ExifTag("ExifIFDPointer", 34665, 4), new ExifTag("GPSInfoIFDPointer", 34853, 4), new ExifTag("DNGVersion", 50706, 1), new ExifTag("DefaultCropSize", 50720, 3, 4) };
    TAG_RAF_IMAGE_SIZE = new ExifTag("StripOffsets", 273, 3);
    ORF_MAKER_NOTE_TAGS = new ExifTag[] { new ExifTag("ThumbnailImage", 256, 7), new ExifTag("CameraSettingsIFDPointer", 8224, 4), new ExifTag("ImageProcessingIFDPointer", 8256, 4) };
    ORF_CAMERA_SETTINGS_TAGS = new ExifTag[] { new ExifTag("PreviewImageStart", 257, 4), new ExifTag("PreviewImageLength", 258, 4) };
    ORF_IMAGE_PROCESSING_TAGS = new ExifTag[] { new ExifTag("AspectFrame", 4371, 3) };
    PEF_TAGS = new ExifTag[] { new ExifTag("ColorSpace", 55, 3) };
    EXIF_TAGS = new ExifTag[][] { IFD_TIFF_TAGS, IFD_EXIF_TAGS, IFD_GPS_TAGS, IFD_INTEROPERABILITY_TAGS, IFD_THUMBNAIL_TAGS, IFD_TIFF_TAGS, ORF_MAKER_NOTE_TAGS, ORF_CAMERA_SETTINGS_TAGS, ORF_IMAGE_PROCESSING_TAGS, PEF_TAGS };
    EXIF_POINTER_TAGS = new ExifTag[] { new ExifTag("SubIFDPointer", 330, 4), new ExifTag("ExifIFDPointer", 34665, 4), new ExifTag("GPSInfoIFDPointer", 34853, 4), new ExifTag("InteroperabilityIFDPointer", 40965, 4), new ExifTag("CameraSettingsIFDPointer", 8224, 1), new ExifTag("ImageProcessingIFDPointer", 8256, 1) };
    JPEG_INTERCHANGE_FORMAT_TAG = new ExifTag("JPEGInterchangeFormat", 513, 4);
    JPEG_INTERCHANGE_FORMAT_LENGTH_TAG = new ExifTag("JPEGInterchangeFormatLength", 514, 4);
    sExifTagMapsForReading = new HashMap[EXIF_TAGS.length];
    sExifTagMapsForWriting = new HashMap[EXIF_TAGS.length];
    sTagSetForCompatibility = new HashSet(Arrays.asList(new String[] { "FNumber", "DigitalZoomRatio", "ExposureTime", "SubjectDistance", "GPSTimeStamp" }));
    sExifPointerTagMap = new HashMap();
    ASCII = Charset.forName("US-ASCII");
    IDENTIFIER_EXIF_APP1 = "Exif\000\000".getBytes(ASCII);
    IDENTIFIER_XMP_APP1 = "http://ns.adobe.com/xap/1.0/\000".getBytes(ASCII);
    sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    int i = 0;
    while (i < EXIF_TAGS.length)
    {
      sExifTagMapsForReading[i] = new HashMap();
      sExifTagMapsForWriting[i] = new HashMap();
      ExifTag[] arrayOfExifTag = EXIF_TAGS[i];
      int k = arrayOfExifTag.length;
      int j = 0;
      while (j < k)
      {
        ExifTag localExifTag = arrayOfExifTag[j];
        sExifTagMapsForReading[i].put(Integer.valueOf(number), localExifTag);
        sExifTagMapsForWriting[i].put(name, localExifTag);
        j += 1;
      }
      i += 1;
    }
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS0number), Integer.valueOf(5));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS1number), Integer.valueOf(1));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS2number), Integer.valueOf(2));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS3number), Integer.valueOf(3));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS4number), Integer.valueOf(7));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS5number), Integer.valueOf(8));
    sNonZeroTimePattern = Pattern.compile(".*[1-9].*");
  }
  
  public ExifInterface(File paramFile)
    throws IOException
  {
    if (paramFile != null)
    {
      initForFilename(paramFile.getAbsolutePath());
      return;
    }
    throw new NullPointerException("file cannot be null");
  }
  
  public ExifInterface(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    if (paramFileDescriptor != null)
    {
      Object localObject = null;
      mAssetInputStream = null;
      mFilename = null;
      int i = 0;
      if ((Build.VERSION.SDK_INT >= 21) && (isSeekableFD(paramFileDescriptor)))
      {
        mSeekableFileDescriptor = paramFileDescriptor;
        try
        {
          paramFileDescriptor = Os.dup(paramFileDescriptor);
          i = 1;
        }
        catch (Exception paramFileDescriptor)
        {
          throw new IOException("Failed to duplicate file descriptor", paramFileDescriptor);
        }
      }
      else
      {
        mSeekableFileDescriptor = null;
      }
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(paramFileDescriptor);
        try
        {
          loadAttributes(localFileInputStream);
          closeQuietly(localFileInputStream);
          if (i == 0) {
            return;
          }
          closeFileDescriptor(paramFileDescriptor);
          return;
        }
        catch (Throwable localThrowable1)
        {
          localObject = localFileInputStream;
        }
        closeQuietly(localObject);
      }
      catch (Throwable localThrowable2) {}
      if (i != 0) {
        closeFileDescriptor(paramFileDescriptor);
      }
      throw localThrowable2;
    }
    throw new NullPointerException("fileDescriptor cannot be null");
  }
  
  public ExifInterface(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream != null)
    {
      mFilename = null;
      if ((paramInputStream instanceof AssetManager.AssetInputStream))
      {
        mAssetInputStream = ((AssetManager.AssetInputStream)paramInputStream);
        mSeekableFileDescriptor = null;
      }
      else
      {
        if ((paramInputStream instanceof FileInputStream))
        {
          FileInputStream localFileInputStream = (FileInputStream)paramInputStream;
          if (isSeekableFD(localFileInputStream.getFD()))
          {
            mAssetInputStream = null;
            mSeekableFileDescriptor = localFileInputStream.getFD();
            break label117;
          }
        }
        mAssetInputStream = null;
        mSeekableFileDescriptor = null;
      }
      label117:
      loadAttributes(paramInputStream);
      return;
    }
    throw new NullPointerException("inputStream cannot be null");
  }
  
  public ExifInterface(String paramString)
    throws IOException
  {
    if (paramString != null)
    {
      initForFilename(paramString);
      return;
    }
    throw new NullPointerException("filename cannot be null");
  }
  
  private void addDefaultValuesForCompatibility()
  {
    String str = getAttribute("DateTimeOriginal");
    if ((str != null) && (getAttribute("DateTime") == null)) {
      mAttributes[0].put("DateTime", ExifAttribute.createString(str));
    }
    if (getAttribute("ImageWidth") == null) {
      mAttributes[0].put("ImageWidth", ExifAttribute.createULong(0L, mExifByteOrder));
    }
    if (getAttribute("ImageLength") == null) {
      mAttributes[0].put("ImageLength", ExifAttribute.createULong(0L, mExifByteOrder));
    }
    if (getAttribute("Orientation") == null) {
      mAttributes[0].put("Orientation", ExifAttribute.createULong(0L, mExifByteOrder));
    }
    if (getAttribute("LightSource") == null) {
      mAttributes[1].put("LightSource", ExifAttribute.createULong(0L, mExifByteOrder));
    }
  }
  
  private static void closeFileDescriptor(FileDescriptor paramFileDescriptor)
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    try
    {
      Os.close(paramFileDescriptor);
      return;
    }
    catch (Exception paramFileDescriptor)
    {
      for (;;) {}
    }
    Log.e("ExifInterface", "Error closing fd.");
    return;
    Log.e("ExifInterface", "closeFileDescriptor is called in API < 21, which must be wrong.");
  }
  
  private static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
        return;
      }
      catch (RuntimeException paramCloseable)
      {
        throw paramCloseable;
      }
      catch (Exception paramCloseable) {}
    }
  }
  
  private String convertDecimalDegree(double paramDouble)
  {
    long l1 = paramDouble;
    paramDouble -= l1;
    long l2 = (paramDouble * 60.0D);
    long l3 = Math.round((paramDouble - l2 / 60.0D) * 3600.0D * 1.0E7D);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(l1);
    localStringBuilder.append("/1,");
    localStringBuilder.append(l2);
    localStringBuilder.append("/1,");
    localStringBuilder.append(l3);
    localStringBuilder.append("/10000000");
    return localStringBuilder.toString();
  }
  
  private static double convertRationalLatLonToDouble(String paramString1, String paramString2)
  {
    double d1;
    try
    {
      paramString1 = paramString1.split(",", -1);
      Object localObject1 = paramString1[0];
      localObject1 = ((String)localObject1).split("/", -1);
      Object localObject2 = localObject1[0];
      d1 = Double.parseDouble(localObject2.trim());
      localObject1 = localObject1[1];
      double d2 = Double.parseDouble(((String)localObject1).trim());
      d1 /= d2;
      localObject1 = paramString1[1];
      localObject1 = ((String)localObject1).split("/", -1);
      localObject2 = localObject1[0];
      d2 = Double.parseDouble(localObject2.trim());
      localObject1 = localObject1[1];
      double d3 = Double.parseDouble(((String)localObject1).trim());
      d2 /= d3;
      paramString1 = paramString1[2];
      paramString1 = paramString1.split("/", -1);
      localObject1 = paramString1[0];
      d3 = Double.parseDouble(((String)localObject1).trim());
      paramString1 = paramString1[1];
      double d4 = Double.parseDouble(paramString1.trim());
      d3 /= d4;
      d1 = d1 + d2 / 60.0D + d3 / 3600.0D;
      boolean bool = paramString2.equals("S");
      if (!bool)
      {
        bool = paramString2.equals("W");
        if (!bool)
        {
          bool = paramString2.equals("N");
          if (bool) {
            return d1;
          }
          bool = paramString2.equals("E");
          if (bool) {
            return d1;
          }
          paramString1 = new IllegalArgumentException();
          throw paramString1;
        }
      }
      return -d1;
    }
    catch (NumberFormatException paramString1)
    {
      for (;;) {}
    }
    catch (ArrayIndexOutOfBoundsException paramString1)
    {
      for (;;) {}
    }
    throw new IllegalArgumentException();
    return d1;
  }
  
  private static long[] convertToLongArray(Object paramObject)
  {
    if ((paramObject instanceof int[]))
    {
      paramObject = (int[])paramObject;
      long[] arrayOfLong = new long[paramObject.length];
      int i = 0;
      while (i < paramObject.length)
      {
        arrayOfLong[i] = paramObject[i];
        i += 1;
      }
      return arrayOfLong;
    }
    if ((paramObject instanceof long[])) {
      return (long[])paramObject;
    }
    return null;
  }
  
  private static int copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['?'];
    int i = 0;
    for (;;)
    {
      int j = paramInputStream.read(arrayOfByte);
      if (j == -1) {
        break;
      }
      i += j;
      paramOutputStream.write(arrayOfByte, 0, j);
    }
    return i;
  }
  
  private ExifAttribute getExifAttribute(String paramString)
  {
    if (paramString != null)
    {
      String str = paramString;
      if ("ISOSpeedRatings".equals(paramString))
      {
        if (DEBUG) {
          Log.d("ExifInterface", "getExifAttribute: Replacing TAG_ISO_SPEED_RATINGS with TAG_PHOTOGRAPHIC_SENSITIVITY.");
        }
        str = "PhotographicSensitivity";
      }
      int i = 0;
      while (i < EXIF_TAGS.length)
      {
        paramString = (ExifAttribute)mAttributes[i].get(str);
        if (paramString != null) {
          return paramString;
        }
        i += 1;
      }
      return null;
    }
    throw new NullPointerException("tag shouldn't be null");
  }
  
  private void getHeifAttributes(final ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    MediaMetadataRetriever localMediaMetadataRetriever = new MediaMetadataRetriever();
    try
    {
      int i = Build.VERSION.SDK_INT;
      if (i >= 23)
      {
        localMediaMetadataRetriever.setDataSource(new MediaDataSource()
        {
          long mPosition;
          
          public void close()
            throws IOException
          {}
          
          public long getSize()
            throws IOException
          {
            return -1L;
          }
          
          public int readAt(long paramAnonymousLong, byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
            throws IOException
          {
            if (paramAnonymousInt2 == 0) {
              return 0;
            }
            if (paramAnonymousLong < 0L) {
              return -1;
            }
            long l;
            ExifInterface.ByteOrderedDataInputStream localByteOrderedDataInputStream;
            if (mPosition != paramAnonymousLong) {
              if (mPosition >= 0L)
              {
                l = mPosition;
                localByteOrderedDataInputStream = paramByteOrderedDataInputStream;
              }
            }
            try
            {
              int i = localByteOrderedDataInputStream.available();
              if (paramAnonymousLong >= l + i) {
                return -1;
              }
              localByteOrderedDataInputStream = paramByteOrderedDataInputStream;
              localByteOrderedDataInputStream.seek(paramAnonymousLong);
              mPosition = paramAnonymousLong;
              localByteOrderedDataInputStream = paramByteOrderedDataInputStream;
              int j = localByteOrderedDataInputStream.available();
              i = paramAnonymousInt2;
              if (paramAnonymousInt2 > j)
              {
                localByteOrderedDataInputStream = paramByteOrderedDataInputStream;
                i = localByteOrderedDataInputStream.available();
              }
              localByteOrderedDataInputStream = paramByteOrderedDataInputStream;
              paramAnonymousInt1 = localByteOrderedDataInputStream.read(paramAnonymousArrayOfByte, paramAnonymousInt1, i);
              if (paramAnonymousInt1 >= 0)
              {
                mPosition += paramAnonymousInt1;
                return paramAnonymousInt1;
              }
            }
            catch (IOException paramAnonymousArrayOfByte)
            {
              for (;;) {}
            }
            mPosition = -1L;
            return -1;
          }
        });
      }
      else
      {
        localObject1 = mSeekableFileDescriptor;
        if (localObject1 != null)
        {
          localMediaMetadataRetriever.setDataSource(mSeekableFileDescriptor);
        }
        else
        {
          localObject1 = mFilename;
          if (localObject1 == null) {
            break label569;
          }
          localMediaMetadataRetriever.setDataSource(mFilename);
        }
      }
      Object localObject2 = localMediaMetadataRetriever.extractMetadata(33);
      String str3 = localMediaMetadataRetriever.extractMetadata(34);
      Object localObject1 = localMediaMetadataRetriever.extractMetadata(26);
      String str1 = localMediaMetadataRetriever.extractMetadata(17);
      boolean bool = "yes".equals(localObject1);
      localObject1 = null;
      String str2;
      if (bool)
      {
        localObject1 = localMediaMetadataRetriever.extractMetadata(29);
        str1 = localMediaMetadataRetriever.extractMetadata(30);
        str2 = localMediaMetadataRetriever.extractMetadata(31);
      }
      else
      {
        bool = "yes".equals(str1);
        if (bool)
        {
          localObject1 = localMediaMetadataRetriever.extractMetadata(18);
          str1 = localMediaMetadataRetriever.extractMetadata(19);
          str2 = localMediaMetadataRetriever.extractMetadata(24);
        }
        else
        {
          str1 = null;
          str2 = null;
        }
      }
      if (localObject1 != null) {
        mAttributes[0].put("ImageWidth", ExifAttribute.createUShort(Integer.parseInt((String)localObject1), mExifByteOrder));
      }
      if (str1 != null) {
        mAttributes[0].put("ImageLength", ExifAttribute.createUShort(Integer.parseInt(str1), mExifByteOrder));
      }
      int j;
      if (str2 != null)
      {
        i = 1;
        j = Integer.parseInt(str2);
        if (j != 90)
        {
          if (j != 180)
          {
            if (j == 270) {
              i = 8;
            }
          }
          else {
            i = 3;
          }
        }
        else {
          i = 6;
        }
        mAttributes[0].put("Orientation", ExifAttribute.createUShort(i, mExifByteOrder));
      }
      if ((localObject2 != null) && (str3 != null))
      {
        j = Integer.parseInt((String)localObject2);
        i = Integer.parseInt(str3);
        if (i > 6)
        {
          paramByteOrderedDataInputStream.seek(j);
          localObject2 = new byte[6];
          j = paramByteOrderedDataInputStream.read((byte[])localObject2);
          if (j == 6)
          {
            i -= 6;
            bool = Arrays.equals((byte[])localObject2, IDENTIFIER_EXIF_APP1);
            if (bool)
            {
              localObject2 = new byte[i];
              j = paramByteOrderedDataInputStream.read((byte[])localObject2);
              if (j == i) {
                readExifSegment((byte[])localObject2, 0);
              } else {
                throw new IOException("Can't read exif");
              }
            }
            else
            {
              throw new IOException("Invalid identifier");
            }
          }
          else
          {
            throw new IOException("Can't read identifier");
          }
        }
        else
        {
          throw new IOException("Invalid exif length");
        }
      }
      bool = DEBUG;
      if (bool)
      {
        paramByteOrderedDataInputStream = new StringBuilder();
        paramByteOrderedDataInputStream.append("Heif meta: ");
        paramByteOrderedDataInputStream.append((String)localObject1);
        paramByteOrderedDataInputStream.append("x");
        paramByteOrderedDataInputStream.append(str1);
        paramByteOrderedDataInputStream.append(", rotation ");
        paramByteOrderedDataInputStream.append(str2);
        Log.d("ExifInterface", paramByteOrderedDataInputStream.toString());
      }
      localMediaMetadataRetriever.release();
      return;
      label569:
      localMediaMetadataRetriever.release();
      return;
    }
    catch (Throwable paramByteOrderedDataInputStream)
    {
      localMediaMetadataRetriever.release();
      throw paramByteOrderedDataInputStream;
    }
  }
  
  private void getJpegAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt1, int paramInt2)
    throws IOException
  {
    Object localObject;
    if (DEBUG)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getJpegAttributes starting with: ");
      ((StringBuilder)localObject).append(paramByteOrderedDataInputStream);
      Log.d("ExifInterface", ((StringBuilder)localObject).toString());
    }
    paramByteOrderedDataInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    paramByteOrderedDataInputStream.seek(paramInt1);
    int i = paramByteOrderedDataInputStream.readByte();
    if (i == -1)
    {
      if (paramByteOrderedDataInputStream.readByte() == -40)
      {
        paramInt1 = paramInt1 + 1 + 1;
        for (;;)
        {
          i = paramByteOrderedDataInputStream.readByte();
          if (i != -1) {
            break label824;
          }
          int m = paramByteOrderedDataInputStream.readByte();
          if (DEBUG)
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("Found JPEG segment indicator: ");
            ((StringBuilder)localObject).append(Integer.toHexString(m & 0xFF));
            Log.d("ExifInterface", ((StringBuilder)localObject).toString());
          }
          if ((m == -39) || (m == -38)) {
            break label815;
          }
          i = paramByteOrderedDataInputStream.readUnsignedShort() - 2;
          int j = paramInt1 + 1 + 1 + 2;
          if (DEBUG)
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("JPEG segment: ");
            ((StringBuilder)localObject).append(Integer.toHexString(m & 0xFF));
            ((StringBuilder)localObject).append(" (length: ");
            ((StringBuilder)localObject).append(i + 2);
            ((StringBuilder)localObject).append(")");
            Log.d("ExifInterface", ((StringBuilder)localObject).toString());
          }
          if (i < 0) {
            break label804;
          }
          int k;
          if (m != -31)
          {
            paramInt1 = j;
            k = i;
            if (m != -2) {
              switch (m)
              {
              default: 
                switch (m)
                {
                default: 
                  switch (m)
                  {
                  default: 
                    switch (m)
                    {
                    default: 
                      paramInt1 = j;
                    }
                    break;
                  }
                  break;
                }
                break;
              case -64: 
              case -63: 
              case -62: 
              case -61: 
                if (paramByteOrderedDataInputStream.skipBytes(1) == 1)
                {
                  mAttributes[paramInt2].put("ImageLength", ExifAttribute.createULong(paramByteOrderedDataInputStream.readUnsignedShort(), mExifByteOrder));
                  mAttributes[paramInt2].put("ImageWidth", ExifAttribute.createULong(paramByteOrderedDataInputStream.readUnsignedShort(), mExifByteOrder));
                  i -= 5;
                  paramInt1 = j;
                  break;
                }
                throw new IOException("Invalid SOFx");
              }
            }
          }
          else
          {
            localObject = new byte[i];
            paramByteOrderedDataInputStream.readFully((byte[])localObject);
            long l;
            if (startsWith((byte[])localObject, IDENTIFIER_EXIF_APP1))
            {
              l = j + IDENTIFIER_EXIF_APP1.length;
              readExifSegment(Arrays.copyOfRange((byte[])localObject, IDENTIFIER_EXIF_APP1.length, localObject.length), paramInt2);
              mExifOffset = ((int)l);
            }
            else if (startsWith((byte[])localObject, IDENTIFIER_XMP_APP1))
            {
              l = j + IDENTIFIER_XMP_APP1.length;
              localObject = Arrays.copyOfRange((byte[])localObject, IDENTIFIER_XMP_APP1.length, localObject.length);
              if (getAttribute("Xmp") == null) {
                mAttributes[0].put("Xmp", new ExifAttribute(1, localObject.length, l, (byte[])localObject));
              }
            }
            paramInt1 = i + j;
            k = 0;
          }
          localObject = new byte[k];
          if (paramByteOrderedDataInputStream.read((byte[])localObject) != k) {
            break label793;
          }
          if (getAttribute("UserComment") == null) {
            mAttributes[1].put("UserComment", ExifAttribute.createString(new String((byte[])localObject, ASCII)));
          }
          i = 0;
          if (i < 0) {
            break label782;
          }
          if (paramByteOrderedDataInputStream.skipBytes(i) != i) {
            break;
          }
          paramInt1 += i;
        }
        throw new IOException("Invalid JPEG segment");
        label782:
        throw new IOException("Invalid length");
        label793:
        throw new IOException("Invalid exif");
        label804:
        throw new IOException("Invalid length");
        label815:
        paramByteOrderedDataInputStream.setByteOrder(mExifByteOrder);
        return;
        label824:
        paramByteOrderedDataInputStream = new StringBuilder();
        paramByteOrderedDataInputStream.append("Invalid marker:");
        paramByteOrderedDataInputStream.append(Integer.toHexString(i & 0xFF));
        throw new IOException(paramByteOrderedDataInputStream.toString());
      }
      paramByteOrderedDataInputStream = new StringBuilder();
      paramByteOrderedDataInputStream.append("Invalid marker: ");
      paramByteOrderedDataInputStream.append(Integer.toHexString(i & 0xFF));
      throw new IOException(paramByteOrderedDataInputStream.toString());
    }
    paramByteOrderedDataInputStream = new StringBuilder();
    paramByteOrderedDataInputStream.append("Invalid marker: ");
    paramByteOrderedDataInputStream.append(Integer.toHexString(i & 0xFF));
    throw new IOException(paramByteOrderedDataInputStream.toString());
  }
  
  private int getMimeType(BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    paramBufferedInputStream.mark(5000);
    byte[] arrayOfByte = new byte['?'];
    paramBufferedInputStream.read(arrayOfByte);
    paramBufferedInputStream.reset();
    if (isJpegFormat(arrayOfByte)) {
      return 4;
    }
    if (isRafFormat(arrayOfByte)) {
      return 9;
    }
    if (isHeifFormat(arrayOfByte)) {
      return 12;
    }
    if (isOrfFormat(arrayOfByte)) {
      return 7;
    }
    if (isRw2Format(arrayOfByte)) {
      return 10;
    }
    return 0;
  }
  
  private void getOrfAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    getRawAttributes(paramByteOrderedDataInputStream);
    paramByteOrderedDataInputStream = (ExifAttribute)mAttributes[1].get("MakerNote");
    if (paramByteOrderedDataInputStream != null)
    {
      paramByteOrderedDataInputStream = new ByteOrderedDataInputStream(bytes);
      paramByteOrderedDataInputStream.setByteOrder(mExifByteOrder);
      Object localObject = new byte[ORF_MAKER_NOTE_HEADER_1.length];
      paramByteOrderedDataInputStream.readFully((byte[])localObject);
      paramByteOrderedDataInputStream.seek(0L);
      byte[] arrayOfByte = new byte[ORF_MAKER_NOTE_HEADER_2.length];
      paramByteOrderedDataInputStream.readFully(arrayOfByte);
      if (Arrays.equals((byte[])localObject, ORF_MAKER_NOTE_HEADER_1)) {
        paramByteOrderedDataInputStream.seek(8L);
      } else if (Arrays.equals(arrayOfByte, ORF_MAKER_NOTE_HEADER_2)) {
        paramByteOrderedDataInputStream.seek(12L);
      }
      readImageFileDirectory(paramByteOrderedDataInputStream, 6);
      paramByteOrderedDataInputStream = (ExifAttribute)mAttributes[7].get("PreviewImageStart");
      localObject = (ExifAttribute)mAttributes[7].get("PreviewImageLength");
      if ((paramByteOrderedDataInputStream != null) && (localObject != null))
      {
        mAttributes[5].put("JPEGInterchangeFormat", paramByteOrderedDataInputStream);
        mAttributes[5].put("JPEGInterchangeFormatLength", localObject);
      }
      paramByteOrderedDataInputStream = (ExifAttribute)mAttributes[8].get("AspectFrame");
      if (paramByteOrderedDataInputStream != null)
      {
        paramByteOrderedDataInputStream = (int[])paramByteOrderedDataInputStream.getValue(mExifByteOrder);
        if ((paramByteOrderedDataInputStream != null) && (paramByteOrderedDataInputStream.length == 4))
        {
          if ((paramByteOrderedDataInputStream[2] > paramByteOrderedDataInputStream[0]) && (paramByteOrderedDataInputStream[3] > paramByteOrderedDataInputStream[1]))
          {
            int m = paramByteOrderedDataInputStream[2] - paramByteOrderedDataInputStream[0] + 1;
            int k = paramByteOrderedDataInputStream[3] - paramByteOrderedDataInputStream[1] + 1;
            int j = m;
            int i = k;
            if (m < k)
            {
              j = m + k;
              i = j - k;
              j -= i;
            }
            paramByteOrderedDataInputStream = ExifAttribute.createUShort(j, mExifByteOrder);
            localObject = ExifAttribute.createUShort(i, mExifByteOrder);
            mAttributes[0].put("ImageWidth", paramByteOrderedDataInputStream);
            mAttributes[0].put("ImageLength", localObject);
          }
        }
        else
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Invalid aspect frame values. frame=");
          ((StringBuilder)localObject).append(Arrays.toString(paramByteOrderedDataInputStream));
          Log.w("ExifInterface", ((StringBuilder)localObject).toString());
        }
      }
    }
  }
  
  private void getRafAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    paramByteOrderedDataInputStream.skipBytes(84);
    Object localObject = new byte[4];
    byte[] arrayOfByte = new byte[4];
    paramByteOrderedDataInputStream.read((byte[])localObject);
    paramByteOrderedDataInputStream.skipBytes(4);
    paramByteOrderedDataInputStream.read(arrayOfByte);
    int i = ByteBuffer.wrap((byte[])localObject).getInt();
    int j = ByteBuffer.wrap(arrayOfByte).getInt();
    getJpegAttributes(paramByteOrderedDataInputStream, i, 5);
    paramByteOrderedDataInputStream.seek(j);
    paramByteOrderedDataInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    j = paramByteOrderedDataInputStream.readInt();
    if (DEBUG)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("numberOfDirectoryEntry: ");
      ((StringBuilder)localObject).append(j);
      Log.d("ExifInterface", ((StringBuilder)localObject).toString());
    }
    i = 0;
    while (i < j)
    {
      int k = paramByteOrderedDataInputStream.readUnsignedShort();
      int m = paramByteOrderedDataInputStream.readUnsignedShort();
      if (k == TAG_RAF_IMAGE_SIZEnumber)
      {
        i = paramByteOrderedDataInputStream.readShort();
        j = paramByteOrderedDataInputStream.readShort();
        paramByteOrderedDataInputStream = ExifAttribute.createUShort(i, mExifByteOrder);
        localObject = ExifAttribute.createUShort(j, mExifByteOrder);
        mAttributes[0].put("ImageLength", paramByteOrderedDataInputStream);
        mAttributes[0].put("ImageWidth", localObject);
        if (!DEBUG) {
          break;
        }
        paramByteOrderedDataInputStream = new StringBuilder();
        paramByteOrderedDataInputStream.append("Updated to length: ");
        paramByteOrderedDataInputStream.append(i);
        paramByteOrderedDataInputStream.append(", width: ");
        paramByteOrderedDataInputStream.append(j);
        Log.d("ExifInterface", paramByteOrderedDataInputStream.toString());
        return;
      }
      paramByteOrderedDataInputStream.skipBytes(m);
      i += 1;
    }
  }
  
  private void getRawAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    parseTiffHeaders(paramByteOrderedDataInputStream, paramByteOrderedDataInputStream.available());
    readImageFileDirectory(paramByteOrderedDataInputStream, 0);
    updateImageSizeValues(paramByteOrderedDataInputStream, 0);
    updateImageSizeValues(paramByteOrderedDataInputStream, 5);
    updateImageSizeValues(paramByteOrderedDataInputStream, 4);
    validateImages(paramByteOrderedDataInputStream);
    if (mMimeType == 8)
    {
      paramByteOrderedDataInputStream = (ExifAttribute)mAttributes[1].get("MakerNote");
      if (paramByteOrderedDataInputStream != null)
      {
        paramByteOrderedDataInputStream = new ByteOrderedDataInputStream(bytes);
        paramByteOrderedDataInputStream.setByteOrder(mExifByteOrder);
        paramByteOrderedDataInputStream.seek(6L);
        readImageFileDirectory(paramByteOrderedDataInputStream, 9);
        paramByteOrderedDataInputStream = (ExifAttribute)mAttributes[9].get("ColorSpace");
        if (paramByteOrderedDataInputStream != null) {
          mAttributes[1].put("ColorSpace", paramByteOrderedDataInputStream);
        }
      }
    }
  }
  
  private void getRw2Attributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    getRawAttributes(paramByteOrderedDataInputStream);
    if ((ExifAttribute)mAttributes[0].get("JpgFromRaw") != null) {
      getJpegAttributes(paramByteOrderedDataInputStream, mRw2JpgFromRawOffset, 5);
    }
    paramByteOrderedDataInputStream = (ExifAttribute)mAttributes[0].get("ISO");
    ExifAttribute localExifAttribute = (ExifAttribute)mAttributes[1].get("PhotographicSensitivity");
    if ((paramByteOrderedDataInputStream != null) && (localExifAttribute == null)) {
      mAttributes[1].put("PhotographicSensitivity", paramByteOrderedDataInputStream);
    }
  }
  
  private static Pair guessDataFormat(String paramString)
  {
    boolean bool = paramString.contains(",");
    int i = 1;
    Object localObject;
    if (bool)
    {
      String[] arrayOfString = paramString.split(",", -1);
      localObject = guessDataFormat(arrayOfString[0]);
      paramString = (String)localObject;
      if (((Integer)first).intValue() == 2) {
        return localObject;
      }
      while (i < arrayOfString.length)
      {
        localObject = guessDataFormat(arrayOfString[i]);
        int j;
        if ((!((Integer)first).equals(first)) && (!((Integer)second).equals(first))) {
          j = -1;
        } else {
          j = ((Integer)first).intValue();
        }
        int k;
        if ((((Integer)second).intValue() != -1) && ((((Integer)first).equals(second)) || (((Integer)second).equals(second)))) {
          k = ((Integer)second).intValue();
        } else {
          k = -1;
        }
        if ((j == -1) && (k == -1)) {
          return new Pair(Integer.valueOf(2), Integer.valueOf(-1));
        }
        if (j == -1) {
          paramString = new Pair(Integer.valueOf(k), Integer.valueOf(-1));
        } else if (k == -1) {
          paramString = new Pair(Integer.valueOf(j), Integer.valueOf(-1));
        }
        i += 1;
      }
      return paramString;
    }
    if (paramString.contains("/"))
    {
      paramString = paramString.split("/", -1);
      if (paramString.length == 2) {
        localObject = paramString[0];
      }
    }
    try
    {
      double d = Double.parseDouble((String)localObject);
      l1 = d;
      paramString = paramString[1];
      d = Double.parseDouble(paramString);
      long l2 = d;
      if ((l1 >= 0L) && (l2 >= 0L))
      {
        if ((l1 <= 2147483647L) && (l2 <= 2147483647L))
        {
          paramString = new Pair(Integer.valueOf(10), Integer.valueOf(5));
          return paramString;
        }
        paramString = new Pair(Integer.valueOf(5), Integer.valueOf(-1));
        return paramString;
      }
      paramString = new Pair(Integer.valueOf(10), Integer.valueOf(-1));
      return paramString;
    }
    catch (NumberFormatException paramString)
    {
      long l1;
      for (;;) {}
    }
    return new Pair(Integer.valueOf(2), Integer.valueOf(-1));
    try
    {
      localObject = Long.valueOf(Long.parseLong(paramString));
      l1 = ((Long)localObject).longValue();
      if (l1 >= 0L)
      {
        l1 = ((Long)localObject).longValue();
        if (l1 <= 65535L)
        {
          localObject = new Pair(Integer.valueOf(3), Integer.valueOf(4));
          return localObject;
        }
      }
      l1 = ((Long)localObject).longValue();
      if (l1 < 0L)
      {
        localObject = new Pair(Integer.valueOf(9), Integer.valueOf(-1));
        return localObject;
      }
      localObject = new Pair(Integer.valueOf(4), Integer.valueOf(-1));
      return localObject;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      label584:
      for (;;) {}
    }
    try
    {
      Double.parseDouble(paramString);
      paramString = new Pair(Integer.valueOf(12), Integer.valueOf(-1));
      return paramString;
    }
    catch (NumberFormatException paramString)
    {
      break label584;
    }
    return new Pair(Integer.valueOf(2), Integer.valueOf(-1));
  }
  
  private void handleThumbnailFromJfif(ByteOrderedDataInputStream paramByteOrderedDataInputStream, HashMap paramHashMap)
    throws IOException
  {
    ExifAttribute localExifAttribute = (ExifAttribute)paramHashMap.get("JPEGInterchangeFormat");
    paramHashMap = (ExifAttribute)paramHashMap.get("JPEGInterchangeFormatLength");
    if ((localExifAttribute != null) && (paramHashMap != null))
    {
      int j = localExifAttribute.getIntValue(mExifByteOrder);
      int i = j;
      int k = Math.min(paramHashMap.getIntValue(mExifByteOrder), paramByteOrderedDataInputStream.getLength() - j);
      if ((mMimeType != 4) && (mMimeType != 9) && (mMimeType != 10))
      {
        if (mMimeType == 7) {
          i = j + mOrfMakerNoteOffset;
        }
      }
      else {
        i = j + mExifOffset;
      }
      if (DEBUG)
      {
        paramHashMap = new StringBuilder();
        paramHashMap.append("Setting thumbnail attributes with offset: ");
        paramHashMap.append(i);
        paramHashMap.append(", length: ");
        paramHashMap.append(k);
        Log.d("ExifInterface", paramHashMap.toString());
      }
      if ((i > 0) && (k > 0))
      {
        mHasThumbnail = true;
        mThumbnailOffset = i;
        mThumbnailLength = k;
        if ((mFilename == null) && (mAssetInputStream == null) && (mSeekableFileDescriptor == null))
        {
          paramHashMap = new byte[k];
          paramByteOrderedDataInputStream.seek(i);
          paramByteOrderedDataInputStream.readFully(paramHashMap);
          mThumbnailBytes = paramHashMap;
        }
      }
    }
  }
  
  private void handleThumbnailFromStrips(ByteOrderedDataInputStream paramByteOrderedDataInputStream, HashMap paramHashMap)
    throws IOException
  {
    Object localObject1 = (ExifAttribute)paramHashMap.get("StripOffsets");
    Object localObject2 = (ExifAttribute)paramHashMap.get("StripByteCounts");
    if ((localObject1 != null) && (localObject2 != null))
    {
      paramHashMap = convertToLongArray(((ExifAttribute)localObject1).getValue(mExifByteOrder));
      localObject1 = convertToLongArray(((ExifAttribute)localObject2).getValue(mExifByteOrder));
      if (paramHashMap == null)
      {
        Log.w("ExifInterface", "stripOffsets should not be null.");
        return;
      }
      if (localObject1 == null)
      {
        Log.w("ExifInterface", "stripByteCounts should not be null.");
        return;
      }
      int j = localObject1.length;
      long l = 0L;
      int i = 0;
      while (i < j)
      {
        l += localObject1[i];
        i += 1;
      }
      localObject2 = new byte[(int)l];
      i = 0;
      int k = 0;
      j = 0;
      while (i < paramHashMap.length)
      {
        int n = (int)paramHashMap[i];
        int m = (int)localObject1[i];
        n -= k;
        if (n < 0) {
          Log.d("ExifInterface", "Invalid strip offset value");
        }
        paramByteOrderedDataInputStream.seek(n);
        byte[] arrayOfByte = new byte[m];
        paramByteOrderedDataInputStream.read(arrayOfByte);
        k = k + n + m;
        System.arraycopy(arrayOfByte, 0, localObject2, j, arrayOfByte.length);
        j += arrayOfByte.length;
        i += 1;
      }
      mHasThumbnail = true;
      mThumbnailBytes = ((byte[])localObject2);
      mThumbnailLength = localObject2.length;
    }
  }
  
  private void initForFilename(String paramString)
    throws IOException
  {
    if (paramString != null)
    {
      mAssetInputStream = null;
      mFilename = paramString;
      try
      {
        paramString = new FileInputStream(paramString);
        try
        {
          boolean bool = isSeekableFD(paramString.getFD());
          if (bool) {
            mSeekableFileDescriptor = paramString.getFD();
          } else {
            mSeekableFileDescriptor = null;
          }
          loadAttributes(paramString);
          closeQuietly(paramString);
          return;
        }
        catch (Throwable localThrowable1) {}
        closeQuietly(paramString);
      }
      catch (Throwable localThrowable2)
      {
        paramString = null;
      }
      throw localThrowable2;
    }
    throw new NullPointerException("filename cannot be null");
  }
  
  /* Error */
  private boolean isHeifFormat(byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 17
    //   3: aconst_null
    //   4: astore 15
    //   6: new 8	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream
    //   9: dup
    //   10: aload_1
    //   11: invokespecial 1566	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:<init>	([B)V
    //   14: astore 16
    //   16: getstatic 1206	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   19: astore 15
    //   21: aload 16
    //   23: aload 15
    //   25: invokevirtual 1473	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:setByteOrder	(Ljava/nio/ByteOrder;)V
    //   28: aload 16
    //   30: invokevirtual 1601	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readInt	()I
    //   33: istore_2
    //   34: iload_2
    //   35: i2l
    //   36: lstore 6
    //   38: iconst_4
    //   39: newarray byte
    //   41: astore 15
    //   43: aload 16
    //   45: aload 15
    //   47: invokevirtual 1385	java/io/InputStream:read	([B)I
    //   50: pop
    //   51: getstatic 948	androidx/exifinterface/media/ExifInterface:HEIF_TYPE_FTYP	[B
    //   54: astore 17
    //   56: aload 15
    //   58: aload 17
    //   60: invokestatic 1440	java/util/Arrays:equals	([B[B)Z
    //   63: istore 14
    //   65: iload 14
    //   67: ifne +10 -> 77
    //   70: aload 16
    //   72: invokevirtual 1715	java/io/InputStream:close	()V
    //   75: iconst_0
    //   76: ireturn
    //   77: ldc2_w 1716
    //   80: lstore 8
    //   82: lload 6
    //   84: lconst_1
    //   85: lcmp
    //   86: ifne +30 -> 116
    //   89: aload 16
    //   91: invokevirtual 1720	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readLong	()J
    //   94: lstore 10
    //   96: lload 10
    //   98: lstore 6
    //   100: lload 10
    //   102: ldc2_w 1716
    //   105: lcmp
    //   106: ifge +15 -> 121
    //   109: aload 16
    //   111: invokevirtual 1715	java/io/InputStream:close	()V
    //   114: iconst_0
    //   115: ireturn
    //   116: ldc2_w 1567
    //   119: lstore 8
    //   121: aload_1
    //   122: arraylength
    //   123: i2l
    //   124: lstore 12
    //   126: lload 6
    //   128: lstore 10
    //   130: lload 6
    //   132: lload 12
    //   134: lcmp
    //   135: ifle +10 -> 145
    //   138: aload_1
    //   139: arraylength
    //   140: istore_2
    //   141: iload_2
    //   142: i2l
    //   143: lstore 10
    //   145: lload 10
    //   147: lload 8
    //   149: lsub
    //   150: lstore 8
    //   152: lload 8
    //   154: ldc2_w 1567
    //   157: lcmp
    //   158: ifge +10 -> 168
    //   161: aload 16
    //   163: invokevirtual 1715	java/io/InputStream:close	()V
    //   166: iconst_0
    //   167: ireturn
    //   168: iconst_4
    //   169: newarray byte
    //   171: astore_1
    //   172: lconst_0
    //   173: lstore 6
    //   175: iconst_0
    //   176: istore_2
    //   177: iconst_0
    //   178: istore_3
    //   179: lload 6
    //   181: lload 8
    //   183: ldc2_w 1721
    //   186: ldiv
    //   187: lcmp
    //   188: ifge +132 -> 320
    //   191: aload 16
    //   193: aload_1
    //   194: invokevirtual 1385	java/io/InputStream:read	([B)I
    //   197: istore 4
    //   199: aload_1
    //   200: arraylength
    //   201: istore 5
    //   203: iload 4
    //   205: iload 5
    //   207: if_icmpeq +10 -> 217
    //   210: aload 16
    //   212: invokevirtual 1715	java/io/InputStream:close	()V
    //   215: iconst_0
    //   216: ireturn
    //   217: lload 6
    //   219: lconst_1
    //   220: lcmp
    //   221: ifne +9 -> 230
    //   224: iload_3
    //   225: istore 5
    //   227: goto +81 -> 308
    //   230: getstatic 953	androidx/exifinterface/media/ExifInterface:HEIF_BRAND_MIF1	[B
    //   233: astore 15
    //   235: aload_1
    //   236: aload 15
    //   238: invokestatic 1440	java/util/Arrays:equals	([B[B)Z
    //   241: istore 14
    //   243: iload 14
    //   245: ifeq +9 -> 254
    //   248: iconst_1
    //   249: istore 4
    //   251: goto +29 -> 280
    //   254: getstatic 958	androidx/exifinterface/media/ExifInterface:HEIF_BRAND_HEIC	[B
    //   257: astore 15
    //   259: aload_1
    //   260: aload 15
    //   262: invokestatic 1440	java/util/Arrays:equals	([B[B)Z
    //   265: istore 14
    //   267: iload_2
    //   268: istore 4
    //   270: iload 14
    //   272: ifeq +8 -> 280
    //   275: iconst_1
    //   276: istore_3
    //   277: iload_2
    //   278: istore 4
    //   280: iload 4
    //   282: istore_2
    //   283: iload_3
    //   284: istore 5
    //   286: iload 4
    //   288: ifeq +20 -> 308
    //   291: iload 4
    //   293: istore_2
    //   294: iload_3
    //   295: istore 5
    //   297: iload_3
    //   298: ifeq +10 -> 308
    //   301: aload 16
    //   303: invokevirtual 1715	java/io/InputStream:close	()V
    //   306: iconst_1
    //   307: ireturn
    //   308: lload 6
    //   310: lconst_1
    //   311: ladd
    //   312: lstore 6
    //   314: iload 5
    //   316: istore_3
    //   317: goto -138 -> 179
    //   320: aload 16
    //   322: invokevirtual 1715	java/io/InputStream:close	()V
    //   325: iconst_0
    //   326: ireturn
    //   327: astore_1
    //   328: goto +66 -> 394
    //   331: astore 15
    //   333: aload 16
    //   335: astore_1
    //   336: aload 15
    //   338: astore 16
    //   340: goto +16 -> 356
    //   343: astore_1
    //   344: aload 15
    //   346: astore 16
    //   348: goto +46 -> 394
    //   351: astore 16
    //   353: aload 17
    //   355: astore_1
    //   356: aload_1
    //   357: astore 15
    //   359: getstatic 918	androidx/exifinterface/media/ExifInterface:DEBUG	Z
    //   362: istore 14
    //   364: iload 14
    //   366: ifeq +18 -> 384
    //   369: aload_1
    //   370: astore 15
    //   372: ldc_w 371
    //   375: ldc_w 1724
    //   378: aload 16
    //   380: invokestatic 1727	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   383: pop
    //   384: aload_1
    //   385: ifnull +21 -> 406
    //   388: aload_1
    //   389: invokevirtual 1715	java/io/InputStream:close	()V
    //   392: iconst_0
    //   393: ireturn
    //   394: aload 16
    //   396: ifnull +8 -> 404
    //   399: aload 16
    //   401: invokevirtual 1715	java/io/InputStream:close	()V
    //   404: aload_1
    //   405: athrow
    //   406: iconst_0
    //   407: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	408	0	this	ExifInterface
    //   0	408	1	paramArrayOfByte	byte[]
    //   33	261	2	i	int
    //   178	139	3	j	int
    //   197	95	4	k	int
    //   201	114	5	m	int
    //   36	277	6	l1	long
    //   80	102	8	l2	long
    //   94	52	10	l3	long
    //   124	9	12	l4	long
    //   63	302	14	bool	boolean
    //   4	257	15	localObject1	Object
    //   331	14	15	localException1	Exception
    //   357	14	15	arrayOfByte1	byte[]
    //   14	333	16	localObject2	Object
    //   351	49	16	localException2	Exception
    //   1	353	17	arrayOfByte2	byte[]
    // Exception table:
    //   from	to	target	type
    //   16	21	327	java/lang/Throwable
    //   21	34	327	java/lang/Throwable
    //   38	43	327	java/lang/Throwable
    //   43	51	327	java/lang/Throwable
    //   56	65	327	java/lang/Throwable
    //   89	96	327	java/lang/Throwable
    //   121	126	327	java/lang/Throwable
    //   138	141	327	java/lang/Throwable
    //   168	172	327	java/lang/Throwable
    //   191	199	327	java/lang/Throwable
    //   199	203	327	java/lang/Throwable
    //   235	243	327	java/lang/Throwable
    //   259	267	327	java/lang/Throwable
    //   21	34	331	java/lang/Exception
    //   43	51	331	java/lang/Exception
    //   56	65	331	java/lang/Exception
    //   89	96	331	java/lang/Exception
    //   191	199	331	java/lang/Exception
    //   235	243	331	java/lang/Exception
    //   259	267	331	java/lang/Exception
    //   6	16	343	java/lang/Throwable
    //   359	364	343	java/lang/Throwable
    //   372	384	343	java/lang/Throwable
    //   6	16	351	java/lang/Exception
  }
  
  private static boolean isJpegFormat(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = 0;
    while (i < JPEG_SIGNATURE.length)
    {
      if (paramArrayOfByte[i] != JPEG_SIGNATURE[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean isOrfFormat(byte[] paramArrayOfByte)
    throws IOException
  {
    paramArrayOfByte = new ByteOrderedDataInputStream(paramArrayOfByte);
    mExifByteOrder = readByteOrder(paramArrayOfByte);
    paramArrayOfByte.setByteOrder(mExifByteOrder);
    int i = paramArrayOfByte.readShort();
    paramArrayOfByte.close();
    return (i == 20306) || (i == 21330);
  }
  
  private boolean isRafFormat(byte[] paramArrayOfByte)
    throws IOException
  {
    byte[] arrayOfByte = "FUJIFILMCCD-RAW".getBytes(Charset.defaultCharset());
    int i = 0;
    while (i < arrayOfByte.length)
    {
      if (paramArrayOfByte[i] != arrayOfByte[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean isRw2Format(byte[] paramArrayOfByte)
    throws IOException
  {
    paramArrayOfByte = new ByteOrderedDataInputStream(paramArrayOfByte);
    mExifByteOrder = readByteOrder(paramArrayOfByte);
    paramArrayOfByte.setByteOrder(mExifByteOrder);
    int i = paramArrayOfByte.readShort();
    paramArrayOfByte.close();
    return i == 85;
  }
  
  private static boolean isSeekableFD(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    int i;
    if (Build.VERSION.SDK_INT >= 21) {
      i = OsConstants.SEEK_CUR;
    }
    try
    {
      Os.lseek(paramFileDescriptor, 0L, i);
      return true;
    }
    catch (Exception paramFileDescriptor) {}
    return false;
    return false;
  }
  
  private boolean isSupportedDataType(HashMap paramHashMap)
    throws IOException
  {
    Object localObject = (ExifAttribute)paramHashMap.get("BitsPerSample");
    if (localObject != null)
    {
      localObject = (int[])((ExifAttribute)localObject).getValue(mExifByteOrder);
      if (Arrays.equals(BITS_PER_SAMPLE_RGB, (int[])localObject)) {
        return true;
      }
      if (mMimeType == 3)
      {
        paramHashMap = (ExifAttribute)paramHashMap.get("PhotometricInterpretation");
        if (paramHashMap != null)
        {
          int i = paramHashMap.getIntValue(mExifByteOrder);
          if ((i == 1) && (Arrays.equals((int[])localObject, BITS_PER_SAMPLE_GREYSCALE_2))) {
            break label122;
          }
          if ((i == 6) && (Arrays.equals((int[])localObject, BITS_PER_SAMPLE_RGB))) {
            return true;
          }
        }
      }
    }
    if (DEBUG) {
      Log.d("ExifInterface", "Unsupported data type value");
    }
    return false;
    label122:
    return true;
  }
  
  private boolean isThumbnail(HashMap paramHashMap)
    throws IOException
  {
    ExifAttribute localExifAttribute = (ExifAttribute)paramHashMap.get("ImageLength");
    paramHashMap = (ExifAttribute)paramHashMap.get("ImageWidth");
    if ((localExifAttribute != null) && (paramHashMap != null))
    {
      int i = localExifAttribute.getIntValue(mExifByteOrder);
      int j = paramHashMap.getIntValue(mExifByteOrder);
      if ((i <= 512) && (j <= 512)) {
        return true;
      }
    }
    return false;
  }
  
  private void loadAttributes(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream != null)
    {
      int i = 0;
      for (;;)
      {
        localObject2 = this;
        Object localObject3;
        HashMap localHashMap;
        try
        {
          int j = EXIF_TAGS.length;
          if (i < j)
          {
            localObject2 = this;
            localObject3 = mAttributes;
            localObject2 = this;
            localObject1 = this;
          }
        }
        catch (Throwable paramInputStream) {}
        try
        {
          localHashMap = new HashMap();
          localObject3[i] = localHashMap;
          i += 1;
        }
        catch (IOException paramInputStream)
        {
          localObject2 = localObject1;
          mIsSupportedFile = false;
          localObject2 = localObject1;
          boolean bool = DEBUG;
          if (!bool) {
            break label416;
          }
          localObject2 = localObject1;
          Log.w("ExifInterface", "Invalid image: ExifInterface got an unsupported image format file(ExifInterface supports JPEG and some RAW image formats only) or a corrupted JPEG file to ExifInterface.", paramInputStream);
          ((ExifInterface)localObject1).addDefaultValuesForCompatibility();
          if (!DEBUG) {
            return;
          }
        }
      }
      Object localObject2 = this;
      Object localObject1 = this;
      paramInputStream = new BufferedInputStream(paramInputStream, 5000);
      localObject2 = this;
      localObject3 = (BufferedInputStream)paramInputStream;
      localObject2 = this;
      localObject1 = this;
      i = getMimeType((BufferedInputStream)localObject3);
      localObject2 = this;
      mMimeType = i;
      localObject2 = this;
      localObject1 = this;
      localObject3 = new ByteOrderedDataInputStream(paramInputStream);
      localObject2 = this;
      i = mMimeType;
      paramInputStream = this;
      switch (i)
      {
      default: 
        break;
      case 12: 
        localObject2 = paramInputStream;
        localObject1 = paramInputStream;
        paramInputStream.getHeifAttributes((ByteOrderedDataInputStream)localObject3);
        break;
      case 10: 
        localObject2 = paramInputStream;
        localObject1 = paramInputStream;
        paramInputStream.getRw2Attributes((ByteOrderedDataInputStream)localObject3);
        break;
      case 9: 
        localObject2 = paramInputStream;
        localObject1 = paramInputStream;
        paramInputStream.getRafAttributes((ByteOrderedDataInputStream)localObject3);
        break;
      case 7: 
        localObject2 = paramInputStream;
        localObject1 = paramInputStream;
        paramInputStream.getOrfAttributes((ByteOrderedDataInputStream)localObject3);
        break;
      case 4: 
        localObject2 = paramInputStream;
        localObject1 = paramInputStream;
        paramInputStream.getJpegAttributes((ByteOrderedDataInputStream)localObject3, 0, 0);
        break;
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 5: 
      case 6: 
      case 8: 
      case 11: 
        localObject2 = paramInputStream;
        localObject1 = paramInputStream;
        paramInputStream.getRawAttributes((ByteOrderedDataInputStream)localObject3);
      }
      localObject2 = paramInputStream;
      localObject1 = paramInputStream;
      paramInputStream.setThumbnailData((ByteOrderedDataInputStream)localObject3);
      localObject2 = paramInputStream;
      mIsSupportedFile = true;
      paramInputStream.addDefaultValuesForCompatibility();
      if (DEBUG)
      {
        break label430;
        label416:
        paramInputStream = (InputStream)localObject1;
        label430:
        paramInputStream.printAttributes();
        return;
        ((ExifInterface)localObject2).addDefaultValuesForCompatibility();
        if (DEBUG) {
          ((ExifInterface)localObject2).printAttributes();
        }
        throw paramInputStream;
      }
    }
    else
    {
      throw new NullPointerException("inputstream shouldn't be null");
    }
  }
  
  private static long parseDateTime(String paramString1, String paramString2)
  {
    ParsePosition localParsePosition;
    SimpleDateFormat localSimpleDateFormat;
    if (paramString1 != null)
    {
      if (!sNonZeroTimePattern.matcher(paramString1).matches()) {
        return -1L;
      }
      localParsePosition = new ParsePosition(0);
      localSimpleDateFormat = sFormatter;
    }
    long l2;
    for (;;)
    {
      try
      {
        paramString1 = localSimpleDateFormat.parse(paramString1, localParsePosition);
        if (paramString1 == null) {
          return -1L;
        }
        l2 = paramString1.getTime();
        if (paramString2 == null) {
          break;
        }
      }
      catch (IllegalArgumentException paramString1)
      {
        long l1;
        return -1L;
      }
      try
      {
        l1 = Long.parseLong(paramString2);
        if (l1 > 1000L) {
          l1 /= 10L;
        } else {
          return l2 + l1;
        }
      }
      catch (NumberFormatException paramString1)
      {
        return l2;
      }
      catch (IllegalArgumentException paramString1)
      {
        return -1L;
      }
    }
    return -1L;
    return l2;
  }
  
  private void parseTiffHeaders(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    mExifByteOrder = readByteOrder(paramByteOrderedDataInputStream);
    paramByteOrderedDataInputStream.setByteOrder(mExifByteOrder);
    int i = paramByteOrderedDataInputStream.readUnsignedShort();
    if ((mMimeType != 7) && (mMimeType != 10) && (i != 42))
    {
      paramByteOrderedDataInputStream = new StringBuilder();
      paramByteOrderedDataInputStream.append("Invalid start code: ");
      paramByteOrderedDataInputStream.append(Integer.toHexString(i));
      throw new IOException(paramByteOrderedDataInputStream.toString());
    }
    i = paramByteOrderedDataInputStream.readInt();
    if ((i >= 8) && (i < paramInt))
    {
      paramInt = i - 8;
      if (paramInt > 0)
      {
        if (paramByteOrderedDataInputStream.skipBytes(paramInt) == paramInt) {
          return;
        }
        paramByteOrderedDataInputStream = new StringBuilder();
        paramByteOrderedDataInputStream.append("Couldn't jump to first Ifd: ");
        paramByteOrderedDataInputStream.append(paramInt);
        throw new IOException(paramByteOrderedDataInputStream.toString());
      }
    }
    else
    {
      paramByteOrderedDataInputStream = new StringBuilder();
      paramByteOrderedDataInputStream.append("Invalid first Ifd offset: ");
      paramByteOrderedDataInputStream.append(i);
      throw new IOException(paramByteOrderedDataInputStream.toString());
    }
  }
  
  private void printAttributes()
  {
    int i = 0;
    while (i < mAttributes.length)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("The size of tag group[");
      ((StringBuilder)localObject).append(i);
      ((StringBuilder)localObject).append("]: ");
      ((StringBuilder)localObject).append(mAttributes[i].size());
      Log.d("ExifInterface", ((StringBuilder)localObject).toString());
      localObject = mAttributes[i].entrySet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
        ExifAttribute localExifAttribute = (ExifAttribute)localEntry.getValue();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("tagName: ");
        localStringBuilder.append((String)localEntry.getKey());
        localStringBuilder.append(", tagType: ");
        localStringBuilder.append(localExifAttribute.toString());
        localStringBuilder.append(", tagValue: '");
        localStringBuilder.append(localExifAttribute.getStringValue(mExifByteOrder));
        localStringBuilder.append("'");
        Log.d("ExifInterface", localStringBuilder.toString());
      }
      i += 1;
    }
  }
  
  private ByteOrder readByteOrder(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    int i = paramByteOrderedDataInputStream.readShort();
    if (i != 18761)
    {
      if (i == 19789)
      {
        if (DEBUG) {
          Log.d("ExifInterface", "readExifSegment: Byte Align MM");
        }
        return ByteOrder.BIG_ENDIAN;
      }
      paramByteOrderedDataInputStream = new StringBuilder();
      paramByteOrderedDataInputStream.append("Invalid byte order: ");
      paramByteOrderedDataInputStream.append(Integer.toHexString(i));
      throw new IOException(paramByteOrderedDataInputStream.toString());
    }
    if (DEBUG) {
      Log.d("ExifInterface", "readExifSegment: Byte Align II");
    }
    return ByteOrder.LITTLE_ENDIAN;
  }
  
  private void readExifSegment(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    ByteOrderedDataInputStream localByteOrderedDataInputStream = new ByteOrderedDataInputStream(paramArrayOfByte);
    parseTiffHeaders(localByteOrderedDataInputStream, paramArrayOfByte.length);
    readImageFileDirectory(localByteOrderedDataInputStream, paramInt);
  }
  
  private void readImageFileDirectory(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    mAttributesOffsets.add(Integer.valueOf(mPosition));
    if (mPosition + 2 > mLength) {
      return;
    }
    int m = paramByteOrderedDataInputStream.readShort();
    Object localObject1;
    if (DEBUG)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("numberOfDirectoryEntry: ");
      ((StringBuilder)localObject1).append(m);
      Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
    }
    if (mPosition + m * 12 <= mLength)
    {
      if (m <= 0) {
        return;
      }
      long l1;
      for (int k = 0; k < m; k = (short)(k + 1))
      {
        int i1 = paramByteOrderedDataInputStream.readUnsignedShort();
        int j = paramByteOrderedDataInputStream.readUnsignedShort();
        int i = j;
        int n = paramByteOrderedDataInputStream.readInt();
        long l2 = paramByteOrderedDataInputStream.peek() + 4L;
        Object localObject2 = (ExifTag)sExifTagMapsForReading[paramInt].get(Integer.valueOf(i1));
        if (DEBUG)
        {
          if (localObject2 != null) {}
          for (localObject1 = name;; localObject1 = null) {
            break;
          }
          Log.d("ExifInterface", String.format("ifdType: %d, tagNumber: %d, tagName: %s, dataFormat: %d, numberOfComponents: %d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i1), localObject1, Integer.valueOf(j), Integer.valueOf(n) }));
        }
        if (localObject2 == null) {
          if (DEBUG)
          {
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("Skip the tag entry since tag number is not defined: ");
            ((StringBuilder)localObject1).append(i1);
            Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
          }
        }
        for (;;)
        {
          break;
          if ((j > 0) && (j < IFD_FORMAT_BYTES_PER_FORMAT.length))
          {
            if (!((ExifTag)localObject2).isFormatCompatible(j))
            {
              if (DEBUG)
              {
                localObject1 = new StringBuilder();
                ((StringBuilder)localObject1).append("Skip the tag entry since data format (");
                ((StringBuilder)localObject1).append(IFD_FORMAT_NAMES[j]);
                ((StringBuilder)localObject1).append(") is unexpected for tag: ");
                ((StringBuilder)localObject1).append(name);
                Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
              }
            }
            else
            {
              if (j == 7) {
                i = primaryFormat;
              }
              l1 = n * IFD_FORMAT_BYTES_PER_FORMAT[i];
              if ((l1 >= 0L) && (l1 <= 2147483647L))
              {
                j = 1;
                break label547;
              }
              if (DEBUG)
              {
                localObject1 = new StringBuilder();
                ((StringBuilder)localObject1).append("Skip the tag entry since the number of components is invalid: ");
                ((StringBuilder)localObject1).append(n);
                Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
              }
              j = 0;
              break label547;
            }
          }
          else if (DEBUG)
          {
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("Skip the tag entry since data format is invalid: ");
            ((StringBuilder)localObject1).append(j);
            Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
          }
        }
        i = j;
        j = 0;
        l1 = 0L;
        label547:
        if (j == 0) {
          paramByteOrderedDataInputStream.seek(l2);
        }
        for (;;)
        {
          break;
          Object localObject3;
          if (l1 > 4L)
          {
            j = paramByteOrderedDataInputStream.readInt();
            if (DEBUG)
            {
              localObject1 = new StringBuilder();
              ((StringBuilder)localObject1).append("seek to data offset: ");
              ((StringBuilder)localObject1).append(j);
              Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
            }
            if (mMimeType == 7)
            {
              if ("MakerNote".equals(name)) {
                mOrfMakerNoteOffset = j;
              }
              while ((paramInt != 6) || (!"ThumbnailImage".equals(name))) {
                break;
              }
              mOrfThumbnailOffset = j;
              mOrfThumbnailLength = n;
              localObject1 = ExifAttribute.createUShort(6, mExifByteOrder);
              localObject3 = ExifAttribute.createULong(mOrfThumbnailOffset, mExifByteOrder);
              ExifAttribute localExifAttribute = ExifAttribute.createULong(mOrfThumbnailLength, mExifByteOrder);
              mAttributes[4].put("Compression", localObject1);
              mAttributes[4].put("JPEGInterchangeFormat", localObject3);
              mAttributes[4].put("JPEGInterchangeFormatLength", localExifAttribute);
            }
            else if ((mMimeType == 10) && ("JpgFromRaw".equals(name)))
            {
              mRw2JpgFromRawOffset = j;
            }
            long l3 = j;
            if (l3 + l1 <= mLength)
            {
              paramByteOrderedDataInputStream.seek(l3);
            }
            else
            {
              if (DEBUG)
              {
                localObject1 = new StringBuilder();
                ((StringBuilder)localObject1).append("Skip the tag entry since data offset is invalid: ");
                ((StringBuilder)localObject1).append(j);
                Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
              }
              paramByteOrderedDataInputStream.seek(l2);
              continue;
            }
          }
          localObject1 = (Integer)sExifPointerTagMap.get(Integer.valueOf(i1));
          if (DEBUG)
          {
            localObject3 = new StringBuilder();
            ((StringBuilder)localObject3).append("nextIfdType: ");
            ((StringBuilder)localObject3).append(localObject1);
            ((StringBuilder)localObject3).append(" byteCount: ");
            ((StringBuilder)localObject3).append(l1);
            Log.d("ExifInterface", ((StringBuilder)localObject3).toString());
          }
          if (localObject1 != null)
          {
            l1 = -1L;
            switch (i)
            {
            default: 
              break;
            case 9: 
            case 13: 
              l1 = paramByteOrderedDataInputStream.readInt();
              break;
            case 8: 
              l1 = paramByteOrderedDataInputStream.readShort();
              break;
            case 4: 
              l1 = paramByteOrderedDataInputStream.readUnsignedInt();
              break;
            case 3: 
              l1 = paramByteOrderedDataInputStream.readUnsignedShort();
            }
            if (DEBUG) {
              Log.d("ExifInterface", String.format("Offset: %d, tagName: %s", new Object[] { Long.valueOf(l1), name }));
            }
            if ((l1 > 0L) && (l1 < mLength))
            {
              if (!mAttributesOffsets.contains(Integer.valueOf((int)l1)))
              {
                paramByteOrderedDataInputStream.seek(l1);
                readImageFileDirectory(paramByteOrderedDataInputStream, ((Integer)localObject1).intValue());
              }
              else if (DEBUG)
              {
                localObject2 = new StringBuilder();
                ((StringBuilder)localObject2).append("Skip jump into the IFD since it has already been read: IfdType ");
                ((StringBuilder)localObject2).append(localObject1);
                ((StringBuilder)localObject2).append(" (at ");
                ((StringBuilder)localObject2).append(l1);
                ((StringBuilder)localObject2).append(")");
                Log.d("ExifInterface", ((StringBuilder)localObject2).toString());
              }
            }
            else if (DEBUG)
            {
              localObject1 = new StringBuilder();
              ((StringBuilder)localObject1).append("Skip jump into the IFD since its offset is invalid: ");
              ((StringBuilder)localObject1).append(l1);
              Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
            }
            paramByteOrderedDataInputStream.seek(l2);
          }
          else
          {
            j = paramByteOrderedDataInputStream.peek();
            localObject1 = new byte[(int)l1];
            paramByteOrderedDataInputStream.readFully((byte[])localObject1);
            localObject1 = new ExifAttribute(i, n, j, (byte[])localObject1);
            mAttributes[paramInt].put(name, localObject1);
            if ("DNGVersion".equals(name)) {
              mMimeType = 3;
            }
            if (((!"Make".equals(name)) && (!"Model".equals(name))) || ((((ExifAttribute)localObject1).getStringValue(mExifByteOrder).contains("PENTAX")) || (("Compression".equals(name)) && (((ExifAttribute)localObject1).getIntValue(mExifByteOrder) == 65535)))) {
              mMimeType = 8;
            }
            if (paramByteOrderedDataInputStream.peek() != l2) {
              paramByteOrderedDataInputStream.seek(l2);
            }
          }
        }
      }
      if (paramByteOrderedDataInputStream.peek() + 4 <= mLength)
      {
        paramInt = paramByteOrderedDataInputStream.readInt();
        if (DEBUG) {
          Log.d("ExifInterface", String.format("nextIfdOffset: %d", new Object[] { Integer.valueOf(paramInt) }));
        }
        l1 = paramInt;
        if ((l1 > 0L) && (paramInt < mLength))
        {
          if (!mAttributesOffsets.contains(Integer.valueOf(paramInt)))
          {
            paramByteOrderedDataInputStream.seek(l1);
            if (mAttributes[4].isEmpty())
            {
              readImageFileDirectory(paramByteOrderedDataInputStream, 4);
              return;
            }
            if (mAttributes[5].isEmpty()) {
              readImageFileDirectory(paramByteOrderedDataInputStream, 5);
            }
          }
          else if (DEBUG)
          {
            paramByteOrderedDataInputStream = new StringBuilder();
            paramByteOrderedDataInputStream.append("Stop reading file since re-reading an IFD may cause an infinite loop: ");
            paramByteOrderedDataInputStream.append(paramInt);
            Log.d("ExifInterface", paramByteOrderedDataInputStream.toString());
          }
        }
        else if (DEBUG)
        {
          paramByteOrderedDataInputStream = new StringBuilder();
          paramByteOrderedDataInputStream.append("Stop reading file since a wrong offset may cause an infinite loop: ");
          paramByteOrderedDataInputStream.append(paramInt);
          Log.d("ExifInterface", paramByteOrderedDataInputStream.toString());
        }
      }
    }
  }
  
  private void removeAttribute(String paramString)
  {
    int i = 0;
    while (i < EXIF_TAGS.length)
    {
      mAttributes[i].remove(paramString);
      i += 1;
    }
  }
  
  private void retrieveJpegImageSize(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    ExifAttribute localExifAttribute1 = (ExifAttribute)mAttributes[paramInt].get("ImageLength");
    ExifAttribute localExifAttribute2 = (ExifAttribute)mAttributes[paramInt].get("ImageWidth");
    if ((localExifAttribute1 == null) || (localExifAttribute2 == null))
    {
      localExifAttribute1 = (ExifAttribute)mAttributes[paramInt].get("JPEGInterchangeFormat");
      if (localExifAttribute1 != null) {
        getJpegAttributes(paramByteOrderedDataInputStream, localExifAttribute1.getIntValue(mExifByteOrder), paramInt);
      }
    }
  }
  
  private void saveJpegAttributes(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    Object localObject;
    if (DEBUG)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("saveJpegAttributes starting with (inputStream: ");
      ((StringBuilder)localObject).append(paramInputStream);
      ((StringBuilder)localObject).append(", outputStream: ");
      ((StringBuilder)localObject).append(paramOutputStream);
      ((StringBuilder)localObject).append(")");
      Log.d("ExifInterface", ((StringBuilder)localObject).toString());
    }
    paramInputStream = new DataInputStream(paramInputStream);
    paramOutputStream = new ByteOrderedDataOutputStream(paramOutputStream, ByteOrder.BIG_ENDIAN);
    if (paramInputStream.readByte() == -1)
    {
      paramOutputStream.writeByte(-1);
      if (paramInputStream.readByte() == -40)
      {
        paramOutputStream.writeByte(-40);
        paramOutputStream.writeByte(-1);
        paramOutputStream.writeByte(-31);
        writeExifSegment(paramOutputStream, 6);
        localObject = new byte['?'];
        while (paramInputStream.readByte() == -1)
        {
          int i = paramInputStream.readByte();
          if (i != -31)
          {
            switch (i)
            {
            default: 
              paramOutputStream.writeByte(-1);
              paramOutputStream.writeByte(i);
              i = paramInputStream.readUnsignedShort();
              paramOutputStream.writeUnsignedShort(i);
              i -= 2;
              if (i >= 0) {
                while (i > 0)
                {
                  j = paramInputStream.read((byte[])localObject, 0, Math.min(i, localObject.length));
                  if (j < 0) {
                    break;
                  }
                  paramOutputStream.write((byte[])localObject, 0, j);
                  i -= j;
                }
              }
              throw new IOException("Invalid length");
            }
            paramOutputStream.writeByte(-1);
            paramOutputStream.writeByte(i);
            copy(paramInputStream, paramOutputStream);
            return;
          }
          int j = paramInputStream.readUnsignedShort() - 2;
          if (j >= 0)
          {
            byte[] arrayOfByte = new byte[6];
            if (j >= 6) {
              if (paramInputStream.read(arrayOfByte) == 6)
              {
                if (Arrays.equals(arrayOfByte, IDENTIFIER_EXIF_APP1))
                {
                  i = j - 6;
                  if (paramInputStream.skipBytes(i) == i) {
                    continue;
                  }
                  throw new IOException("Invalid length");
                }
              }
              else {
                throw new IOException("Invalid exif");
              }
            }
            paramOutputStream.writeByte(-1);
            paramOutputStream.writeByte(i);
            paramOutputStream.writeUnsignedShort(j + 2);
            i = j;
            if (j >= 6)
            {
              i = j - 6;
              paramOutputStream.write(arrayOfByte);
            }
            for (;;)
            {
              if (i <= 0) {
                break label457;
              }
              j = paramInputStream.read((byte[])localObject, 0, Math.min(i, localObject.length));
              if (j < 0) {
                break;
              }
              paramOutputStream.write((byte[])localObject, 0, j);
              i -= j;
            }
          }
          else
          {
            label457:
            throw new IOException("Invalid length");
          }
        }
        throw new IOException("Invalid marker");
      }
      throw new IOException("Invalid marker");
    }
    throw new IOException("Invalid marker");
  }
  
  private void setThumbnailData(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    HashMap localHashMap = mAttributes[4];
    ExifAttribute localExifAttribute = (ExifAttribute)localHashMap.get("Compression");
    if (localExifAttribute != null)
    {
      mThumbnailCompression = localExifAttribute.getIntValue(mExifByteOrder);
      int i = mThumbnailCompression;
      if (i != 1) {
        switch (i)
        {
        default: 
          return;
        case 6: 
          handleThumbnailFromJfif(paramByteOrderedDataInputStream, localHashMap);
          return;
        }
      }
      if (isSupportedDataType(localHashMap)) {
        handleThumbnailFromStrips(paramByteOrderedDataInputStream, localHashMap);
      }
    }
    else
    {
      mThumbnailCompression = 6;
      handleThumbnailFromJfif(paramByteOrderedDataInputStream, localHashMap);
    }
  }
  
  private static boolean startsWith(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1 != null)
    {
      if (paramArrayOfByte2 == null) {
        return false;
      }
      if (paramArrayOfByte1.length < paramArrayOfByte2.length) {
        return false;
      }
      int i = 0;
      while (i < paramArrayOfByte2.length)
      {
        if (paramArrayOfByte1[i] != paramArrayOfByte2[i]) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    return false;
  }
  
  private void swapBasedOnImageSize(int paramInt1, int paramInt2)
    throws IOException
  {
    if ((!mAttributes[paramInt1].isEmpty()) && (!mAttributes[paramInt2].isEmpty()))
    {
      Object localObject = (ExifAttribute)mAttributes[paramInt1].get("ImageLength");
      ExifAttribute localExifAttribute1 = (ExifAttribute)mAttributes[paramInt1].get("ImageWidth");
      ExifAttribute localExifAttribute2 = (ExifAttribute)mAttributes[paramInt2].get("ImageLength");
      ExifAttribute localExifAttribute3 = (ExifAttribute)mAttributes[paramInt2].get("ImageWidth");
      if ((localObject != null) && (localExifAttribute1 != null))
      {
        if ((localExifAttribute2 != null) && (localExifAttribute3 != null))
        {
          int i = ((ExifAttribute)localObject).getIntValue(mExifByteOrder);
          int j = localExifAttribute1.getIntValue(mExifByteOrder);
          int k = localExifAttribute2.getIntValue(mExifByteOrder);
          int m = localExifAttribute3.getIntValue(mExifByteOrder);
          if ((i < k) && (j < m))
          {
            localObject = mAttributes[paramInt1];
            mAttributes[paramInt1] = mAttributes[paramInt2];
            mAttributes[paramInt2] = localObject;
          }
        }
        else if (DEBUG)
        {
          Log.d("ExifInterface", "Second image does not contain valid size information");
        }
      }
      else if (DEBUG) {
        Log.d("ExifInterface", "First image does not contain valid size information");
      }
    }
    else if (DEBUG)
    {
      Log.d("ExifInterface", "Cannot perform swap since only one image data exists");
    }
  }
  
  private boolean updateAttribute(String paramString, ExifAttribute paramExifAttribute)
  {
    int i = 0;
    boolean bool = false;
    while (i < EXIF_TAGS.length)
    {
      if (mAttributes[i].containsKey(paramString))
      {
        mAttributes[i].put(paramString, paramExifAttribute);
        bool = true;
      }
      i += 1;
    }
    return bool;
  }
  
  private void updateImageSizeValues(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    Object localObject = (ExifAttribute)mAttributes[paramInt].get("DefaultCropSize");
    ExifAttribute localExifAttribute1 = (ExifAttribute)mAttributes[paramInt].get("SensorTopBorder");
    ExifAttribute localExifAttribute2 = (ExifAttribute)mAttributes[paramInt].get("SensorLeftBorder");
    ExifAttribute localExifAttribute3 = (ExifAttribute)mAttributes[paramInt].get("SensorBottomBorder");
    ExifAttribute localExifAttribute4 = (ExifAttribute)mAttributes[paramInt].get("SensorRightBorder");
    if (localObject != null)
    {
      if (format == 5)
      {
        localObject = (Rational[])((ExifAttribute)localObject).getValue(mExifByteOrder);
        if ((localObject != null) && (localObject.length == 2))
        {
          paramByteOrderedDataInputStream = ExifAttribute.createURational(localObject[0], mExifByteOrder);
          localObject = ExifAttribute.createURational(localObject[1], mExifByteOrder);
        }
        else
        {
          paramByteOrderedDataInputStream = new StringBuilder();
          paramByteOrderedDataInputStream.append("Invalid crop size values. cropSize=");
          paramByteOrderedDataInputStream.append(Arrays.toString((Object[])localObject));
          Log.w("ExifInterface", paramByteOrderedDataInputStream.toString());
        }
      }
      else
      {
        localObject = (int[])((ExifAttribute)localObject).getValue(mExifByteOrder);
        if ((localObject == null) || (localObject.length != 2)) {
          break label278;
        }
        paramByteOrderedDataInputStream = ExifAttribute.createUShort(localObject[0], mExifByteOrder);
        localObject = ExifAttribute.createUShort(localObject[1], mExifByteOrder);
      }
      mAttributes[paramInt].put("ImageWidth", paramByteOrderedDataInputStream);
      mAttributes[paramInt].put("ImageLength", localObject);
      return;
      label278:
      paramByteOrderedDataInputStream = new StringBuilder();
      paramByteOrderedDataInputStream.append("Invalid crop size values. cropSize=");
      paramByteOrderedDataInputStream.append(Arrays.toString((int[])localObject));
      Log.w("ExifInterface", paramByteOrderedDataInputStream.toString());
      return;
    }
    if ((localExifAttribute1 != null) && (localExifAttribute2 != null) && (localExifAttribute3 != null) && (localExifAttribute4 != null))
    {
      int i = localExifAttribute1.getIntValue(mExifByteOrder);
      int j = localExifAttribute3.getIntValue(mExifByteOrder);
      int k = localExifAttribute4.getIntValue(mExifByteOrder);
      int m = localExifAttribute2.getIntValue(mExifByteOrder);
      if ((j > i) && (k > m))
      {
        paramByteOrderedDataInputStream = ExifAttribute.createUShort(j - i, mExifByteOrder);
        localObject = ExifAttribute.createUShort(k - m, mExifByteOrder);
        mAttributes[paramInt].put("ImageLength", paramByteOrderedDataInputStream);
        mAttributes[paramInt].put("ImageWidth", localObject);
      }
    }
    else
    {
      retrieveJpegImageSize(paramByteOrderedDataInputStream, paramInt);
    }
  }
  
  private void validateImages(InputStream paramInputStream)
    throws IOException
  {
    swapBasedOnImageSize(0, 5);
    swapBasedOnImageSize(0, 4);
    swapBasedOnImageSize(5, 4);
    paramInputStream = (ExifAttribute)mAttributes[1].get("PixelXDimension");
    ExifAttribute localExifAttribute = (ExifAttribute)mAttributes[1].get("PixelYDimension");
    if ((paramInputStream != null) && (localExifAttribute != null))
    {
      mAttributes[0].put("ImageWidth", paramInputStream);
      mAttributes[0].put("ImageLength", localExifAttribute);
    }
    if ((mAttributes[4].isEmpty()) && (isThumbnail(mAttributes[5])))
    {
      mAttributes[4] = mAttributes[5];
      mAttributes[5] = new HashMap();
    }
    if (!isThumbnail(mAttributes[4])) {
      Log.d("ExifInterface", "No image meets the size requirements of a thumbnail image.");
    }
  }
  
  private int writeExifSegment(ByteOrderedDataOutputStream paramByteOrderedDataOutputStream, int paramInt)
    throws IOException
  {
    int[] arrayOfInt = new int[EXIF_TAGS.length];
    Object localObject1 = new int[EXIF_TAGS.length];
    Object localObject2 = EXIF_POINTER_TAGS;
    int j = localObject2.length;
    int i = 0;
    while (i < j)
    {
      removeAttribute(name);
      i += 1;
    }
    removeAttribute(JPEG_INTERCHANGE_FORMAT_TAGname);
    removeAttribute(JPEG_INTERCHANGE_FORMAT_LENGTH_TAGname);
    i = 0;
    int k;
    while (i < EXIF_TAGS.length)
    {
      localObject2 = mAttributes[i].entrySet().toArray();
      k = localObject2.length;
      j = 0;
      while (j < k)
      {
        Map.Entry localEntry = (Map.Entry)localObject2[j];
        if (localEntry.getValue() == null) {
          mAttributes[i].remove(localEntry.getKey());
        }
        j += 1;
      }
      i += 1;
    }
    if (!mAttributes[1].isEmpty()) {
      mAttributes[0].put(EXIF_POINTER_TAGS1name, ExifAttribute.createULong(0L, mExifByteOrder));
    }
    if (!mAttributes[2].isEmpty()) {
      mAttributes[0].put(EXIF_POINTER_TAGS2name, ExifAttribute.createULong(0L, mExifByteOrder));
    }
    if (!mAttributes[3].isEmpty()) {
      mAttributes[1].put(EXIF_POINTER_TAGS3name, ExifAttribute.createULong(0L, mExifByteOrder));
    }
    if (mHasThumbnail)
    {
      mAttributes[4].put(JPEG_INTERCHANGE_FORMAT_TAGname, ExifAttribute.createULong(0L, mExifByteOrder));
      mAttributes[4].put(JPEG_INTERCHANGE_FORMAT_LENGTH_TAGname, ExifAttribute.createULong(mThumbnailLength, mExifByteOrder));
    }
    i = 0;
    while (i < EXIF_TAGS.length)
    {
      localObject2 = mAttributes[i].entrySet().iterator();
      j = 0;
      while (((Iterator)localObject2).hasNext())
      {
        k = ((ExifAttribute)((Map.Entry)((Iterator)localObject2).next()).getValue()).size();
        if (k > 4) {
          j += k;
        }
      }
      localObject1[i] += j;
      i += 1;
    }
    j = 0;
    for (i = 8; j < EXIF_TAGS.length; i = k)
    {
      k = i;
      if (!mAttributes[j].isEmpty())
      {
        arrayOfInt[j] = i;
        k = i + (mAttributes[j].size() * 12 + 2 + 4 + localObject1[j]);
      }
      j += 1;
    }
    j = i;
    if (mHasThumbnail)
    {
      mAttributes[4].put(JPEG_INTERCHANGE_FORMAT_TAGname, ExifAttribute.createULong(i, mExifByteOrder));
      mThumbnailOffset = (paramInt + i);
      j = i + mThumbnailLength;
    }
    int m = j + 8;
    if (DEBUG)
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("totalSize length: ");
      ((StringBuilder)localObject2).append(m);
      Log.d("ExifInterface", ((StringBuilder)localObject2).toString());
      paramInt = 0;
      while (paramInt < EXIF_TAGS.length)
      {
        Log.d("ExifInterface", String.format("index: %d, offsets: %d, tag count: %d, data sizes: %d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(arrayOfInt[paramInt]), Integer.valueOf(mAttributes[paramInt].size()), Integer.valueOf(localObject1[paramInt]) }));
        paramInt += 1;
      }
    }
    if (!mAttributes[1].isEmpty()) {
      mAttributes[0].put(EXIF_POINTER_TAGS1name, ExifAttribute.createULong(arrayOfInt[1], mExifByteOrder));
    }
    if (!mAttributes[2].isEmpty()) {
      mAttributes[0].put(EXIF_POINTER_TAGS2name, ExifAttribute.createULong(arrayOfInt[2], mExifByteOrder));
    }
    if (!mAttributes[3].isEmpty()) {
      mAttributes[1].put(EXIF_POINTER_TAGS3name, ExifAttribute.createULong(arrayOfInt[3], mExifByteOrder));
    }
    paramByteOrderedDataOutputStream.writeUnsignedShort(m);
    paramByteOrderedDataOutputStream.write(IDENTIFIER_EXIF_APP1);
    short s;
    if (mExifByteOrder == ByteOrder.BIG_ENDIAN) {
      s = 19789;
    } else {
      s = 18761;
    }
    paramByteOrderedDataOutputStream.writeShort(s);
    paramByteOrderedDataOutputStream.setByteOrder(mExifByteOrder);
    paramByteOrderedDataOutputStream.writeUnsignedShort(42);
    paramByteOrderedDataOutputStream.writeUnsignedInt(8L);
    paramInt = 0;
    while (paramInt < EXIF_TAGS.length)
    {
      if (!mAttributes[paramInt].isEmpty())
      {
        paramByteOrderedDataOutputStream.writeUnsignedShort(mAttributes[paramInt].size());
        i = arrayOfInt[paramInt] + 2 + mAttributes[paramInt].size() * 12 + 4;
        localObject1 = mAttributes[paramInt].entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          int n = sExifTagMapsForWritinggetgetKeynumber;
          localObject2 = (ExifAttribute)((Map.Entry)localObject2).getValue();
          k = ((ExifAttribute)localObject2).size();
          j = k;
          paramByteOrderedDataOutputStream.writeUnsignedShort(n);
          paramByteOrderedDataOutputStream.writeUnsignedShort(format);
          paramByteOrderedDataOutputStream.writeInt(numberOfComponents);
          if (k > 4)
          {
            paramByteOrderedDataOutputStream.writeUnsignedInt(i);
            i += k;
          }
          else
          {
            paramByteOrderedDataOutputStream.write(bytes);
            if (k < 4) {
              while (j < 4)
              {
                paramByteOrderedDataOutputStream.writeByte(0);
                j += 1;
              }
            }
          }
        }
        if ((paramInt == 0) && (!mAttributes[4].isEmpty())) {
          paramByteOrderedDataOutputStream.writeUnsignedInt(arrayOfInt[4]);
        } else {
          paramByteOrderedDataOutputStream.writeUnsignedInt(0L);
        }
        localObject1 = mAttributes[paramInt].entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (ExifAttribute)((Map.Entry)((Iterator)localObject1).next()).getValue();
          if (bytes.length > 4) {
            paramByteOrderedDataOutputStream.write(bytes, 0, bytes.length);
          }
        }
      }
      paramInt += 1;
    }
    if (mHasThumbnail) {
      paramByteOrderedDataOutputStream.write(getThumbnailBytes());
    }
    paramByteOrderedDataOutputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    return m;
  }
  
  public void flipHorizontally()
  {
    int i = 1;
    switch (getAttributeInt("Orientation", 1))
    {
    default: 
      i = 0;
      break;
    case 8: 
      i = 7;
      break;
    case 7: 
      i = 8;
      break;
    case 6: 
      i = 5;
      break;
    case 5: 
      i = 6;
      break;
    case 4: 
      i = 3;
      break;
    case 3: 
      i = 4;
      break;
    case 1: 
      i = 2;
    }
    setAttribute("Orientation", Integer.toString(i));
  }
  
  public void flipVertically()
  {
    int i = 1;
    switch (getAttributeInt("Orientation", 1))
    {
    default: 
      i = 0;
      break;
    case 8: 
      i = 5;
      break;
    case 7: 
      i = 6;
      break;
    case 6: 
      i = 7;
      break;
    case 5: 
      i = 8;
      break;
    case 3: 
      i = 2;
      break;
    case 2: 
      i = 3;
      break;
    case 1: 
      i = 4;
    }
    setAttribute("Orientation", Integer.toString(i));
  }
  
  public double getAltitude(double paramDouble)
  {
    double d2 = getAttributeDouble("GPSAltitude", -1.0D);
    int j = getAttributeInt("GPSAltitudeRef", -1);
    double d1 = paramDouble;
    if (d2 >= 0.0D)
    {
      d1 = paramDouble;
      if (j >= 0)
      {
        int i = 1;
        if (j == 1) {
          i = -1;
        }
        d1 = d2 * i;
      }
    }
    return d1;
  }
  
  public String getAttribute(String paramString)
  {
    Object localObject;
    if (paramString != null)
    {
      localObject = getExifAttribute(paramString);
      if (localObject != null)
      {
        if (!sTagSetForCompatibility.contains(paramString)) {
          return ((ExifAttribute)localObject).getStringValue(mExifByteOrder);
        }
        if (paramString.equals("GPSTimeStamp"))
        {
          if ((format != 5) && (format != 10))
          {
            paramString = new StringBuilder();
            paramString.append("GPS Timestamp format is not rational. format=");
            paramString.append(format);
            Log.w("ExifInterface", paramString.toString());
            return null;
          }
          paramString = (Rational[])((ExifAttribute)localObject).getValue(mExifByteOrder);
          if ((paramString != null) && (paramString.length == 3)) {
            return String.format("%02d:%02d:%02d", new Object[] { Integer.valueOf((int)((float)0numerator / (float)0denominator)), Integer.valueOf((int)((float)1numerator / (float)1denominator)), Integer.valueOf((int)((float)2numerator / (float)2denominator)) });
          }
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Invalid GPS Timestamp array. array=");
          ((StringBuilder)localObject).append(Arrays.toString(paramString));
          Log.w("ExifInterface", ((StringBuilder)localObject).toString());
          return null;
        }
        paramString = mExifByteOrder;
      }
    }
    try
    {
      paramString = Double.toString(((ExifAttribute)localObject).getDoubleValue(paramString));
      return paramString;
    }
    catch (NumberFormatException paramString) {}
    return null;
    throw new NullPointerException("tag shouldn't be null");
    return null;
  }
  
  public byte[] getAttributeBytes(String paramString)
  {
    if (paramString != null)
    {
      paramString = getExifAttribute(paramString);
      if (paramString != null) {
        return bytes;
      }
      return null;
    }
    throw new NullPointerException("tag shouldn't be null");
  }
  
  public double getAttributeDouble(String paramString, double paramDouble)
  {
    ByteOrder localByteOrder;
    if (paramString != null)
    {
      paramString = getExifAttribute(paramString);
      if (paramString == null) {
        return paramDouble;
      }
      localByteOrder = mExifByteOrder;
    }
    try
    {
      double d = paramString.getDoubleValue(localByteOrder);
      return d;
    }
    catch (NumberFormatException paramString) {}
    throw new NullPointerException("tag shouldn't be null");
    return paramDouble;
  }
  
  public int getAttributeInt(String paramString, int paramInt)
  {
    ByteOrder localByteOrder;
    if (paramString != null)
    {
      paramString = getExifAttribute(paramString);
      if (paramString == null) {
        return paramInt;
      }
      localByteOrder = mExifByteOrder;
    }
    try
    {
      int i = paramString.getIntValue(localByteOrder);
      return i;
    }
    catch (NumberFormatException paramString) {}
    throw new NullPointerException("tag shouldn't be null");
    return paramInt;
  }
  
  public long[] getAttributeRange(String paramString)
  {
    if (paramString != null)
    {
      if (!mModified)
      {
        paramString = getExifAttribute(paramString);
        if (paramString != null) {
          return new long[] { bytesOffset, bytes.length };
        }
        return null;
      }
      throw new IllegalStateException("The underlying file has been modified since being parsed");
    }
    throw new NullPointerException("tag shouldn't be null");
  }
  
  public long getDateTime()
  {
    return parseDateTime(getAttribute("DateTime"), getAttribute("SubSecTime"));
  }
  
  public long getDateTimeDigitized()
  {
    return parseDateTime(getAttribute("DateTimeDigitized"), getAttribute("SubSecTimeDigitized"));
  }
  
  public long getDateTimeOriginal()
  {
    return parseDateTime(getAttribute("DateTimeOriginal"), getAttribute("SubSecTimeOriginal"));
  }
  
  public long getGpsDateTime()
  {
    Object localObject1 = getAttribute("GPSDateStamp");
    Object localObject2 = getAttribute("GPSTimeStamp");
    Object localObject3;
    if (localObject1 != null)
    {
      if (localObject2 != null)
      {
        if ((!sNonZeroTimePattern.matcher((CharSequence)localObject1).matches()) && (!sNonZeroTimePattern.matcher((CharSequence)localObject2).matches())) {
          return -1L;
        }
        localObject3 = new StringBuilder();
        ((StringBuilder)localObject3).append((String)localObject1);
        ((StringBuilder)localObject3).append(' ');
        ((StringBuilder)localObject3).append((String)localObject2);
        localObject1 = ((StringBuilder)localObject3).toString();
        localObject2 = new ParsePosition(0);
        localObject3 = sFormatter;
      }
    }
    else
    {
      try
      {
        localObject1 = ((SimpleDateFormat)localObject3).parse((String)localObject1, (ParsePosition)localObject2);
        if (localObject1 == null) {
          return -1L;
        }
        long l = ((Date)localObject1).getTime();
        return l;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return -1L;
    }
    return -1L;
  }
  
  public boolean getLatLong(float[] paramArrayOfFloat)
  {
    double[] arrayOfDouble = getLatLong();
    if (arrayOfDouble == null) {
      return false;
    }
    paramArrayOfFloat[0] = ((float)arrayOfDouble[0]);
    paramArrayOfFloat[1] = ((float)arrayOfDouble[1]);
    return true;
  }
  
  public double[] getLatLong()
  {
    String str1 = getAttribute("GPSLatitude");
    String str2 = getAttribute("GPSLatitudeRef");
    String str3 = getAttribute("GPSLongitude");
    String str4 = getAttribute("GPSLongitudeRef");
    if ((str1 != null) && (str2 != null) && (str3 != null) && (str4 != null)) {}
    try
    {
      double d1 = convertRationalLatLonToDouble(str1, str2);
      double d2 = convertRationalLatLonToDouble(str3, str4);
      return new double[] { d1, d2 };
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      StringBuilder localStringBuilder;
      for (;;) {}
    }
    localStringBuilder = new StringBuilder();
    localStringBuilder.append("Latitude/longitude values are not parsable. ");
    localStringBuilder.append(String.format("latValue=%s, latRef=%s, lngValue=%s, lngRef=%s", new Object[] { str1, str2, str3, str4 }));
    Log.w("ExifInterface", localStringBuilder.toString());
    return null;
  }
  
  public int getRotationDegrees()
  {
    switch (getAttributeInt("Orientation", 1))
    {
    default: 
      return 0;
    case 6: 
    case 7: 
      return 90;
    case 5: 
    case 8: 
      return 270;
    }
    return 180;
  }
  
  public byte[] getThumbnail()
  {
    if ((mThumbnailCompression != 6) && (mThumbnailCompression != 7)) {
      return null;
    }
    return getThumbnailBytes();
  }
  
  public Bitmap getThumbnailBitmap()
  {
    if (!mHasThumbnail) {
      return null;
    }
    if (mThumbnailBytes == null) {
      mThumbnailBytes = getThumbnailBytes();
    }
    if ((mThumbnailCompression != 6) && (mThumbnailCompression != 7))
    {
      if (mThumbnailCompression == 1)
      {
        int[] arrayOfInt = new int[mThumbnailBytes.length / 3];
        int i = 0;
        while (i < arrayOfInt.length)
        {
          localObject = mThumbnailBytes;
          int j = i * 3;
          arrayOfInt[i] = ((localObject[j] << 16) + 0 + (mThumbnailBytes[(j + 1)] << 8) + mThumbnailBytes[(j + 2)]);
          i += 1;
        }
        Object localObject = (ExifAttribute)mAttributes[4].get("ImageLength");
        ExifAttribute localExifAttribute = (ExifAttribute)mAttributes[4].get("ImageWidth");
        if ((localObject != null) && (localExifAttribute != null))
        {
          i = ((ExifAttribute)localObject).getIntValue(mExifByteOrder);
          return Bitmap.createBitmap(arrayOfInt, localExifAttribute.getIntValue(mExifByteOrder), i, Bitmap.Config.ARGB_8888);
        }
      }
      else
      {
        return null;
      }
    }
    else {
      return BitmapFactory.decodeByteArray(mThumbnailBytes, 0, mThumbnailLength);
    }
    return null;
  }
  
  public byte[] getThumbnailBytes()
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a15 = a14\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  public long[] getThumbnailRange()
  {
    if (!mModified)
    {
      if (mHasThumbnail) {
        return new long[] { mThumbnailOffset, mThumbnailLength };
      }
      return null;
    }
    throw new IllegalStateException("The underlying file has been modified since being parsed");
  }
  
  public boolean hasAttribute(String paramString)
  {
    return getExifAttribute(paramString) != null;
  }
  
  public boolean hasThumbnail()
  {
    return mHasThumbnail;
  }
  
  public boolean isFlipped()
  {
    int i = getAttributeInt("Orientation", 1);
    if ((i != 2) && (i != 7)) {
      switch (i)
      {
      default: 
        return false;
      }
    }
    return true;
  }
  
  public boolean isThumbnailCompressed()
  {
    if (!mHasThumbnail) {
      return false;
    }
    return (mThumbnailCompression == 6) || (mThumbnailCompression == 7);
  }
  
  public void resetOrientation()
  {
    setAttribute("Orientation", Integer.toString(1));
  }
  
  public void rotate(int paramInt)
  {
    if (paramInt % 90 == 0)
    {
      int m = getAttributeInt("Orientation", 1);
      boolean bool = ROTATION_ORDER.contains(Integer.valueOf(m));
      int j = 0;
      int k = 0;
      int i = 0;
      if (bool)
      {
        j = (ROTATION_ORDER.indexOf(Integer.valueOf(m)) + paramInt / 90) % 4;
        paramInt = i;
        if (j < 0) {
          paramInt = 4;
        }
        i = ((Integer)ROTATION_ORDER.get(j + paramInt)).intValue();
      }
      else
      {
        i = k;
        if (FLIPPED_ROTATION_ORDER.contains(Integer.valueOf(m)))
        {
          i = (FLIPPED_ROTATION_ORDER.indexOf(Integer.valueOf(m)) + paramInt / 90) % 4;
          paramInt = j;
          if (i < 0) {
            paramInt = 4;
          }
          i = ((Integer)FLIPPED_ROTATION_ORDER.get(i + paramInt)).intValue();
        }
      }
      setAttribute("Orientation", Integer.toString(i));
      return;
    }
    throw new IllegalArgumentException("degree should be a multiple of 90");
  }
  
  public void saveAttributes()
    throws IOException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  public void setAltitude(double paramDouble)
  {
    String str;
    if (paramDouble >= 0.0D) {
      str = "0";
    } else {
      str = "1";
    }
    setAttribute("GPSAltitude", new Rational(Math.abs(paramDouble)).toString());
    setAttribute("GPSAltitudeRef", str);
  }
  
  public void setAttribute(String paramString1, String paramString2)
  {
    String str = paramString1;
    Object localObject1 = paramString2;
    if (paramString1 != null)
    {
      if ("ISOSpeedRatings".equals(paramString1))
      {
        if (DEBUG) {
          Log.d("ExifInterface", "setAttribute: Replacing TAG_ISO_SPEED_RATINGS with TAG_PHOTOGRAPHIC_SENSITIVITY.");
        }
        str = "PhotographicSensitivity";
      }
      paramString1 = (String)localObject1;
      if (paramString2 != null)
      {
        paramString1 = (String)localObject1;
        if (sTagSetForCompatibility.contains(str)) {
          if (str.equals("GPSTimeStamp"))
          {
            paramString1 = sGpsTimestampPattern.matcher(paramString2);
            if (!paramString1.find())
            {
              paramString1 = new StringBuilder();
              paramString1.append("Invalid value for ");
              paramString1.append(str);
              paramString1.append(" : ");
              paramString1.append(paramString2);
              Log.w("ExifInterface", paramString1.toString());
              return;
            }
            paramString2 = new StringBuilder();
            paramString2.append(Integer.parseInt(paramString1.group(1)));
            paramString2.append("/1,");
            paramString2.append(Integer.parseInt(paramString1.group(2)));
            paramString2.append("/1,");
            paramString2.append(Integer.parseInt(paramString1.group(3)));
            paramString2.append("/1");
            paramString1 = paramString2.toString();
          }
        }
      }
    }
    try
    {
      double d = Double.parseDouble(paramString2);
      paramString1 = new Rational(d).toString();
    }
    catch (NumberFormatException paramString1)
    {
      int j;
      for (;;) {}
    }
    paramString1 = new StringBuilder();
    paramString1.append("Invalid value for ");
    paramString1.append(str);
    paramString1.append(" : ");
    paramString1.append(paramString2);
    Log.w("ExifInterface", paramString1.toString());
    return;
    j = 0;
    while (j < EXIF_TAGS.length)
    {
      if ((j != 4) || (mHasThumbnail))
      {
        paramString2 = (ExifTag)sExifTagMapsForWriting[j].get(str);
        if (paramString2 != null) {
          if (paramString1 == null)
          {
            mAttributes[j].remove(str);
          }
          else
          {
            Object localObject2 = guessDataFormat(paramString1);
            int i;
            if ((primaryFormat != ((Integer)first).intValue()) && (primaryFormat != ((Integer)second).intValue()))
            {
              if ((secondaryFormat != -1) && ((secondaryFormat == ((Integer)first).intValue()) || (secondaryFormat == ((Integer)second).intValue())))
              {
                i = secondaryFormat;
              }
              else
              {
                if ((primaryFormat != 1) && (primaryFormat != 7) && (primaryFormat != 2))
                {
                  if (!DEBUG) {
                    break label1409;
                  }
                  localObject1 = new StringBuilder();
                  ((StringBuilder)localObject1).append("Given tag (");
                  ((StringBuilder)localObject1).append(str);
                  ((StringBuilder)localObject1).append(") value didn't match with one of expected formats: ");
                  ((StringBuilder)localObject1).append(IFD_FORMAT_NAMES[primaryFormat]);
                  if (secondaryFormat == -1)
                  {
                    paramString2 = "";
                  }
                  else
                  {
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append(", ");
                    localStringBuilder.append(IFD_FORMAT_NAMES[secondaryFormat]);
                    paramString2 = localStringBuilder.toString();
                  }
                  ((StringBuilder)localObject1).append(paramString2);
                  ((StringBuilder)localObject1).append(" (guess: ");
                  ((StringBuilder)localObject1).append(IFD_FORMAT_NAMES[((Integer)first).intValue()]);
                  if (((Integer)second).intValue() == -1)
                  {
                    paramString2 = "";
                  }
                  else
                  {
                    paramString2 = new StringBuilder();
                    paramString2.append(", ");
                    paramString2.append(IFD_FORMAT_NAMES[((Integer)second).intValue()]);
                    paramString2 = paramString2.toString();
                  }
                  ((StringBuilder)localObject1).append(paramString2);
                  ((StringBuilder)localObject1).append(")");
                  Log.d("ExifInterface", ((StringBuilder)localObject1).toString());
                  break label1409;
                }
                i = primaryFormat;
              }
            }
            else {
              i = primaryFormat;
            }
            switch (i)
            {
            default: 
              break;
            case 6: 
            case 8: 
            case 11: 
              if (!DEBUG) {
                break label1409;
              }
              paramString2 = new StringBuilder();
              paramString2.append("Data format isn't one of expected formats: ");
              paramString2.append(i);
              Log.d("ExifInterface", paramString2.toString());
              break;
            case 12: 
              paramString2 = paramString1.split(",", -1);
              localObject1 = new double[paramString2.length];
              i = 0;
              while (i < paramString2.length)
              {
                localObject1[i] = Double.parseDouble(paramString2[i]);
                i += 1;
              }
              mAttributes[j].put(str, ExifAttribute.createDouble((double[])localObject1, mExifByteOrder));
              break;
            case 10: 
              paramString2 = paramString1.split(",", -1);
              localObject1 = new Rational[paramString2.length];
              i = 0;
              while (i < paramString2.length)
              {
                localObject2 = paramString2[i].split("/", -1);
                localObject1[i] = new Rational(Double.parseDouble(localObject2[0]), Double.parseDouble(localObject2[1]));
                i += 1;
              }
              mAttributes[j].put(str, ExifAttribute.createSRational((Rational[])localObject1, mExifByteOrder));
              break;
            case 9: 
              paramString2 = paramString1.split(",", -1);
              localObject1 = new int[paramString2.length];
              i = 0;
              while (i < paramString2.length)
              {
                localObject1[i] = Integer.parseInt(paramString2[i]);
                i += 1;
              }
              mAttributes[j].put(str, ExifAttribute.createSLong((int[])localObject1, mExifByteOrder));
              break;
            case 5: 
              paramString2 = paramString1.split(",", -1);
              localObject1 = new Rational[paramString2.length];
              i = 0;
              while (i < paramString2.length)
              {
                localObject2 = paramString2[i].split("/", -1);
                localObject1[i] = new Rational(Double.parseDouble(localObject2[0]), Double.parseDouble(localObject2[1]));
                i += 1;
              }
              mAttributes[j].put(str, ExifAttribute.createURational((Rational[])localObject1, mExifByteOrder));
              break;
            case 4: 
              paramString2 = paramString1.split(",", -1);
              localObject1 = new long[paramString2.length];
              i = 0;
              while (i < paramString2.length)
              {
                localObject1[i] = Long.parseLong(paramString2[i]);
                i += 1;
              }
              mAttributes[j].put(str, ExifAttribute.createULong((long[])localObject1, mExifByteOrder));
              break;
            case 3: 
              paramString2 = paramString1.split(",", -1);
              localObject1 = new int[paramString2.length];
              i = 0;
              while (i < paramString2.length)
              {
                localObject1[i] = Integer.parseInt(paramString2[i]);
                i += 1;
              }
              mAttributes[j].put(str, ExifAttribute.createUShort((int[])localObject1, mExifByteOrder));
              break;
            case 2: 
            case 7: 
              mAttributes[j].put(str, ExifAttribute.createString(paramString1));
              break;
            }
            mAttributes[j].put(str, ExifAttribute.createByte(paramString1));
          }
        }
      }
      label1409:
      j += 1;
    }
    return;
    throw new NullPointerException("tag shouldn't be null");
  }
  
  public void setDateTime(long paramLong)
  {
    setAttribute("DateTime", sFormatter.format(new Date(paramLong)));
    setAttribute("SubSecTime", Long.toString(paramLong % 1000L));
  }
  
  public void setGpsInfo(Location paramLocation)
  {
    if (paramLocation == null) {
      return;
    }
    setAttribute("GPSProcessingMethod", paramLocation.getProvider());
    setLatLong(paramLocation.getLatitude(), paramLocation.getLongitude());
    setAltitude(paramLocation.getAltitude());
    setAttribute("GPSSpeedRef", "K");
    setAttribute("GPSSpeed", new Rational(paramLocation.getSpeed() * (float)TimeUnit.HOURS.toSeconds(1L) / 1000.0F).toString());
    paramLocation = sFormatter.format(new Date(paramLocation.getTime())).split("\\s+", -1);
    setAttribute("GPSDateStamp", paramLocation[0]);
    setAttribute("GPSTimeStamp", paramLocation[1]);
  }
  
  public void setLatLong(double paramDouble1, double paramDouble2)
  {
    if ((paramDouble1 >= -90.0D) && (paramDouble1 <= 90.0D) && (!Double.isNaN(paramDouble1)))
    {
      if ((paramDouble2 >= -180.0D) && (paramDouble2 <= 180.0D) && (!Double.isNaN(paramDouble2)))
      {
        if (paramDouble1 >= 0.0D) {
          localObject = "N";
        } else {
          localObject = "S";
        }
        setAttribute("GPSLatitudeRef", (String)localObject);
        setAttribute("GPSLatitude", convertDecimalDegree(Math.abs(paramDouble1)));
        if (paramDouble2 >= 0.0D) {
          localObject = "E";
        } else {
          localObject = "W";
        }
        setAttribute("GPSLongitudeRef", (String)localObject);
        setAttribute("GPSLongitude", convertDecimalDegree(Math.abs(paramDouble2)));
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Longitude value ");
      ((StringBuilder)localObject).append(paramDouble2);
      ((StringBuilder)localObject).append(" is not valid.");
      throw new IllegalArgumentException(((StringBuilder)localObject).toString());
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Latitude value ");
    ((StringBuilder)localObject).append(paramDouble1);
    ((StringBuilder)localObject).append(" is not valid.");
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  private static class ByteOrderedDataInputStream
    extends InputStream
    implements DataInput
  {
    private static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
    private static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    private ByteOrder mByteOrder = ByteOrder.BIG_ENDIAN;
    private DataInputStream mDataInputStream;
    final int mLength;
    int mPosition;
    
    public ByteOrderedDataInputStream(InputStream paramInputStream)
      throws IOException
    {
      mDataInputStream = new DataInputStream(paramInputStream);
      mLength = mDataInputStream.available();
      mPosition = 0;
      mDataInputStream.mark(mLength);
    }
    
    public ByteOrderedDataInputStream(byte[] paramArrayOfByte)
      throws IOException
    {
      this(new ByteArrayInputStream(paramArrayOfByte));
    }
    
    public int available()
      throws IOException
    {
      return mDataInputStream.available();
    }
    
    public int getLength()
    {
      return mLength;
    }
    
    public int peek()
    {
      return mPosition;
    }
    
    public int read()
      throws IOException
    {
      mPosition += 1;
      return mDataInputStream.read();
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      paramInt1 = mDataInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      mPosition += paramInt1;
      return paramInt1;
    }
    
    public boolean readBoolean()
      throws IOException
    {
      mPosition += 1;
      return mDataInputStream.readBoolean();
    }
    
    public byte readByte()
      throws IOException
    {
      mPosition += 1;
      if (mPosition <= mLength)
      {
        int i = mDataInputStream.read();
        if (i >= 0) {
          return (byte)i;
        }
        throw new EOFException();
      }
      throw new EOFException();
    }
    
    public char readChar()
      throws IOException
    {
      mPosition += 2;
      return mDataInputStream.readChar();
    }
    
    public double readDouble()
      throws IOException
    {
      return Double.longBitsToDouble(readLong());
    }
    
    public float readFloat()
      throws IOException
    {
      return Float.intBitsToFloat(readInt());
    }
    
    public void readFully(byte[] paramArrayOfByte)
      throws IOException
    {
      mPosition += paramArrayOfByte.length;
      if (mPosition <= mLength)
      {
        if (mDataInputStream.read(paramArrayOfByte, 0, paramArrayOfByte.length) == paramArrayOfByte.length) {
          return;
        }
        throw new IOException("Couldn't read up to the length of buffer");
      }
      throw new EOFException();
    }
    
    public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      mPosition += paramInt2;
      if (mPosition <= mLength)
      {
        if (mDataInputStream.read(paramArrayOfByte, paramInt1, paramInt2) == paramInt2) {
          return;
        }
        throw new IOException("Couldn't read up to the length of buffer");
      }
      throw new EOFException();
    }
    
    public int readInt()
      throws IOException
    {
      mPosition += 4;
      if (mPosition <= mLength)
      {
        int i = mDataInputStream.read();
        int j = mDataInputStream.read();
        int k = mDataInputStream.read();
        int m = mDataInputStream.read();
        if ((i | j | k | m) >= 0)
        {
          if (mByteOrder == LITTLE_ENDIAN) {
            return (m << 24) + (k << 16) + (j << 8) + i;
          }
          if (mByteOrder == BIG_ENDIAN) {
            return (i << 24) + (j << 16) + (k << 8) + m;
          }
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Invalid byte order: ");
          localStringBuilder.append(mByteOrder);
          throw new IOException(localStringBuilder.toString());
        }
        throw new EOFException();
      }
      throw new EOFException();
    }
    
    public String readLine()
      throws IOException
    {
      Log.d("ExifInterface", "Currently unsupported");
      return null;
    }
    
    public long readLong()
      throws IOException
    {
      mPosition += 8;
      if (mPosition <= mLength)
      {
        int i = mDataInputStream.read();
        int j = mDataInputStream.read();
        int k = mDataInputStream.read();
        int m = mDataInputStream.read();
        int n = mDataInputStream.read();
        int i1 = mDataInputStream.read();
        int i2 = mDataInputStream.read();
        int i3 = mDataInputStream.read();
        if ((i | j | k | m | n | i1 | i2 | i3) >= 0)
        {
          if (mByteOrder == LITTLE_ENDIAN) {
            return (i3 << 56) + (i2 << 48) + (i1 << 40) + (n << 32) + (m << 24) + (k << 16) + (j << 8) + i;
          }
          if (mByteOrder == BIG_ENDIAN) {
            return (i << 56) + (j << 48) + (k << 40) + (m << 32) + (n << 24) + (i1 << 16) + (i2 << 8) + i3;
          }
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Invalid byte order: ");
          localStringBuilder.append(mByteOrder);
          throw new IOException(localStringBuilder.toString());
        }
        throw new EOFException();
      }
      throw new EOFException();
    }
    
    public short readShort()
      throws IOException
    {
      mPosition += 2;
      if (mPosition <= mLength)
      {
        int i = mDataInputStream.read();
        int j = mDataInputStream.read();
        if ((i | j) >= 0)
        {
          if (mByteOrder == LITTLE_ENDIAN) {
            return (short)((j << 8) + i);
          }
          if (mByteOrder == BIG_ENDIAN) {
            return (short)((i << 8) + j);
          }
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Invalid byte order: ");
          localStringBuilder.append(mByteOrder);
          throw new IOException(localStringBuilder.toString());
        }
        throw new EOFException();
      }
      throw new EOFException();
    }
    
    public String readUTF()
      throws IOException
    {
      mPosition += 2;
      return mDataInputStream.readUTF();
    }
    
    public int readUnsignedByte()
      throws IOException
    {
      mPosition += 1;
      return mDataInputStream.readUnsignedByte();
    }
    
    public long readUnsignedInt()
      throws IOException
    {
      return readInt() & 0xFFFFFFFF;
    }
    
    public int readUnsignedShort()
      throws IOException
    {
      mPosition += 2;
      if (mPosition <= mLength)
      {
        int i = mDataInputStream.read();
        int j = mDataInputStream.read();
        if ((i | j) >= 0)
        {
          if (mByteOrder == LITTLE_ENDIAN) {
            return (j << 8) + i;
          }
          if (mByteOrder == BIG_ENDIAN) {
            return (i << 8) + j;
          }
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Invalid byte order: ");
          localStringBuilder.append(mByteOrder);
          throw new IOException(localStringBuilder.toString());
        }
        throw new EOFException();
      }
      throw new EOFException();
    }
    
    public void seek(long paramLong)
      throws IOException
    {
      if (mPosition > paramLong)
      {
        mPosition = 0;
        mDataInputStream.reset();
        mDataInputStream.mark(mLength);
      }
      else
      {
        paramLong -= mPosition;
      }
      int i = (int)paramLong;
      if (skipBytes(i) == i) {
        return;
      }
      throw new IOException("Couldn't seek up to the byteCount");
    }
    
    public void setByteOrder(ByteOrder paramByteOrder)
    {
      mByteOrder = paramByteOrder;
    }
    
    public int skipBytes(int paramInt)
      throws IOException
    {
      int i = Math.min(paramInt, mLength - mPosition);
      paramInt = 0;
      while (paramInt < i) {
        paramInt += mDataInputStream.skipBytes(i - paramInt);
      }
      mPosition += paramInt;
      return paramInt;
    }
  }
  
  private static class ByteOrderedDataOutputStream
    extends FilterOutputStream
  {
    private ByteOrder mByteOrder;
    private final OutputStream mOutputStream;
    
    public ByteOrderedDataOutputStream(OutputStream paramOutputStream, ByteOrder paramByteOrder)
    {
      super();
      mOutputStream = paramOutputStream;
      mByteOrder = paramByteOrder;
    }
    
    public void setByteOrder(ByteOrder paramByteOrder)
    {
      mByteOrder = paramByteOrder;
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      mOutputStream.write(paramArrayOfByte);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      mOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public void writeByte(int paramInt)
      throws IOException
    {
      mOutputStream.write(paramInt);
    }
    
    public void writeInt(int paramInt)
      throws IOException
    {
      if (mByteOrder == ByteOrder.LITTLE_ENDIAN)
      {
        mOutputStream.write(paramInt >>> 0 & 0xFF);
        mOutputStream.write(paramInt >>> 8 & 0xFF);
        mOutputStream.write(paramInt >>> 16 & 0xFF);
        mOutputStream.write(paramInt >>> 24 & 0xFF);
        return;
      }
      if (mByteOrder == ByteOrder.BIG_ENDIAN)
      {
        mOutputStream.write(paramInt >>> 24 & 0xFF);
        mOutputStream.write(paramInt >>> 16 & 0xFF);
        mOutputStream.write(paramInt >>> 8 & 0xFF);
        mOutputStream.write(paramInt >>> 0 & 0xFF);
      }
    }
    
    public void writeShort(short paramShort)
      throws IOException
    {
      if (mByteOrder == ByteOrder.LITTLE_ENDIAN)
      {
        mOutputStream.write(paramShort >>> 0 & 0xFF);
        mOutputStream.write(paramShort >>> 8 & 0xFF);
        return;
      }
      if (mByteOrder == ByteOrder.BIG_ENDIAN)
      {
        mOutputStream.write(paramShort >>> 8 & 0xFF);
        mOutputStream.write(paramShort >>> 0 & 0xFF);
      }
    }
    
    public void writeUnsignedInt(long paramLong)
      throws IOException
    {
      writeInt((int)paramLong);
    }
    
    public void writeUnsignedShort(int paramInt)
      throws IOException
    {
      writeShort((short)paramInt);
    }
  }
  
  private static class ExifAttribute
  {
    public static final long BYTES_OFFSET_UNKNOWN = -1L;
    public final byte[] bytes;
    public final long bytesOffset;
    public final int format;
    public final int numberOfComponents;
    
    ExifAttribute(int paramInt1, int paramInt2, long paramLong, byte[] paramArrayOfByte)
    {
      format = paramInt1;
      numberOfComponents = paramInt2;
      bytesOffset = paramLong;
      bytes = paramArrayOfByte;
    }
    
    ExifAttribute(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      this(paramInt1, paramInt2, -1L, paramArrayOfByte);
    }
    
    public static ExifAttribute createByte(String paramString)
    {
      if ((paramString.length() == 1) && (paramString.charAt(0) >= '0') && (paramString.charAt(0) <= '1'))
      {
        byte[] arrayOfByte = new byte[1];
        arrayOfByte[0] = ((byte)(paramString.charAt(0) - '0'));
        return new ExifAttribute(1, arrayOfByte.length, arrayOfByte);
      }
      paramString = paramString.getBytes(ExifInterface.ASCII);
      return new ExifAttribute(1, paramString.length, paramString);
    }
    
    public static ExifAttribute createDouble(double paramDouble, ByteOrder paramByteOrder)
    {
      return createDouble(new double[] { paramDouble }, paramByteOrder);
    }
    
    public static ExifAttribute createDouble(double[] paramArrayOfDouble, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[12] * paramArrayOfDouble.length]);
      localByteBuffer.order(paramByteOrder);
      int j = paramArrayOfDouble.length;
      int i = 0;
      while (i < j)
      {
        localByteBuffer.putDouble(paramArrayOfDouble[i]);
        i += 1;
      }
      return new ExifAttribute(12, paramArrayOfDouble.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createSLong(int paramInt, ByteOrder paramByteOrder)
    {
      return createSLong(new int[] { paramInt }, paramByteOrder);
    }
    
    public static ExifAttribute createSLong(int[] paramArrayOfInt, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[9] * paramArrayOfInt.length]);
      localByteBuffer.order(paramByteOrder);
      int j = paramArrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        localByteBuffer.putInt(paramArrayOfInt[i]);
        i += 1;
      }
      return new ExifAttribute(9, paramArrayOfInt.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createSRational(ExifInterface.Rational paramRational, ByteOrder paramByteOrder)
    {
      return createSRational(new ExifInterface.Rational[] { paramRational }, paramByteOrder);
    }
    
    public static ExifAttribute createSRational(ExifInterface.Rational[] paramArrayOfRational, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[10] * paramArrayOfRational.length]);
      localByteBuffer.order(paramByteOrder);
      int j = paramArrayOfRational.length;
      int i = 0;
      while (i < j)
      {
        paramByteOrder = paramArrayOfRational[i];
        localByteBuffer.putInt((int)numerator);
        localByteBuffer.putInt((int)denominator);
        i += 1;
      }
      return new ExifAttribute(10, paramArrayOfRational.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createString(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append('\000');
      paramString = localStringBuilder.toString().getBytes(ExifInterface.ASCII);
      return new ExifAttribute(2, paramString.length, paramString);
    }
    
    public static ExifAttribute createULong(long paramLong, ByteOrder paramByteOrder)
    {
      return createULong(new long[] { paramLong }, paramByteOrder);
    }
    
    public static ExifAttribute createULong(long[] paramArrayOfLong, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[4] * paramArrayOfLong.length]);
      localByteBuffer.order(paramByteOrder);
      int j = paramArrayOfLong.length;
      int i = 0;
      while (i < j)
      {
        localByteBuffer.putInt((int)paramArrayOfLong[i]);
        i += 1;
      }
      return new ExifAttribute(4, paramArrayOfLong.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createURational(ExifInterface.Rational paramRational, ByteOrder paramByteOrder)
    {
      return createURational(new ExifInterface.Rational[] { paramRational }, paramByteOrder);
    }
    
    public static ExifAttribute createURational(ExifInterface.Rational[] paramArrayOfRational, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[5] * paramArrayOfRational.length]);
      localByteBuffer.order(paramByteOrder);
      int j = paramArrayOfRational.length;
      int i = 0;
      while (i < j)
      {
        paramByteOrder = paramArrayOfRational[i];
        localByteBuffer.putInt((int)numerator);
        localByteBuffer.putInt((int)denominator);
        i += 1;
      }
      return new ExifAttribute(5, paramArrayOfRational.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createUShort(int paramInt, ByteOrder paramByteOrder)
    {
      return createUShort(new int[] { paramInt }, paramByteOrder);
    }
    
    public static ExifAttribute createUShort(int[] paramArrayOfInt, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[3] * paramArrayOfInt.length]);
      localByteBuffer.order(paramByteOrder);
      int j = paramArrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        localByteBuffer.putShort((short)paramArrayOfInt[i]);
        i += 1;
      }
      return new ExifAttribute(3, paramArrayOfInt.length, localByteBuffer.array());
    }
    
    public double getDoubleValue(ByteOrder paramByteOrder)
    {
      paramByteOrder = getValue(paramByteOrder);
      if (paramByteOrder != null)
      {
        if ((paramByteOrder instanceof String)) {
          return Double.parseDouble((String)paramByteOrder);
        }
        if ((paramByteOrder instanceof long[]))
        {
          paramByteOrder = (long[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            return paramByteOrder[0];
          }
          throw new NumberFormatException("There are more than one component");
        }
        if ((paramByteOrder instanceof int[]))
        {
          paramByteOrder = (int[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            return paramByteOrder[0];
          }
          throw new NumberFormatException("There are more than one component");
        }
        if ((paramByteOrder instanceof double[]))
        {
          paramByteOrder = (double[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            return paramByteOrder[0];
          }
          throw new NumberFormatException("There are more than one component");
        }
        if ((paramByteOrder instanceof ExifInterface.Rational[]))
        {
          paramByteOrder = (ExifInterface.Rational[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            return paramByteOrder[0].calculate();
          }
          throw new NumberFormatException("There are more than one component");
        }
        throw new NumberFormatException("Couldn't find a double value");
      }
      throw new NumberFormatException("NULL can't be converted to a double value");
    }
    
    public int getIntValue(ByteOrder paramByteOrder)
    {
      paramByteOrder = getValue(paramByteOrder);
      if (paramByteOrder != null)
      {
        if ((paramByteOrder instanceof String)) {
          return Integer.parseInt((String)paramByteOrder);
        }
        if ((paramByteOrder instanceof long[]))
        {
          paramByteOrder = (long[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            return (int)paramByteOrder[0];
          }
          throw new NumberFormatException("There are more than one component");
        }
        if ((paramByteOrder instanceof int[]))
        {
          paramByteOrder = (int[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            return paramByteOrder[0];
          }
          throw new NumberFormatException("There are more than one component");
        }
        throw new NumberFormatException("Couldn't find a integer value");
      }
      throw new NumberFormatException("NULL can't be converted to a integer value");
    }
    
    public String getStringValue(ByteOrder paramByteOrder)
    {
      Object localObject = getValue(paramByteOrder);
      if (localObject == null) {
        return null;
      }
      if ((localObject instanceof String)) {
        return (String)localObject;
      }
      paramByteOrder = new StringBuilder();
      boolean bool = localObject instanceof long[];
      int j = 0;
      int k = 0;
      int m = 0;
      int i = 0;
      if (bool)
      {
        localObject = (long[])localObject;
        while (i < localObject.length)
        {
          paramByteOrder.append(localObject[i]);
          j = i + 1;
          i = j;
          if (j != localObject.length)
          {
            paramByteOrder.append(",");
            i = j;
          }
        }
        return paramByteOrder.toString();
      }
      if ((localObject instanceof int[]))
      {
        localObject = (int[])localObject;
        i = j;
        while (i < localObject.length)
        {
          paramByteOrder.append(localObject[i]);
          j = i + 1;
          i = j;
          if (j != localObject.length)
          {
            paramByteOrder.append(",");
            i = j;
          }
        }
        return paramByteOrder.toString();
      }
      if ((localObject instanceof double[]))
      {
        localObject = (double[])localObject;
        i = k;
        while (i < localObject.length)
        {
          paramByteOrder.append(localObject[i]);
          j = i + 1;
          i = j;
          if (j != localObject.length)
          {
            paramByteOrder.append(",");
            i = j;
          }
        }
        return paramByteOrder.toString();
      }
      if ((localObject instanceof ExifInterface.Rational[]))
      {
        localObject = (ExifInterface.Rational[])localObject;
        i = m;
        while (i < localObject.length)
        {
          paramByteOrder.append(numerator);
          paramByteOrder.append('/');
          paramByteOrder.append(denominator);
          j = i + 1;
          i = j;
          if (j != localObject.length)
          {
            paramByteOrder.append(",");
            i = j;
          }
        }
        return paramByteOrder.toString();
      }
      return null;
    }
    
    /* Error */
    Object getValue(ByteOrder paramByteOrder)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   4: astore 21
      //   6: new 200	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream
      //   9: dup
      //   10: aload 21
      //   12: invokespecial 203	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:<init>	([B)V
      //   15: astore 22
      //   17: aload 22
      //   19: astore 21
      //   21: aload 22
      //   23: aload_1
      //   24: invokevirtual 207	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:setByteOrder	(Ljava/nio/ByteOrder;)V
      //   27: aload 22
      //   29: astore 21
      //   31: aload_0
      //   32: getfield 24	androidx/exifinterface/media/ExifInterface$ExifAttribute:format	I
      //   35: istore 16
      //   37: iconst_1
      //   38: istore 9
      //   40: iconst_0
      //   41: istore 7
      //   43: iconst_0
      //   44: istore 10
      //   46: iconst_0
      //   47: istore 11
      //   49: iconst_0
      //   50: istore 12
      //   52: iconst_0
      //   53: istore 13
      //   55: iconst_0
      //   56: istore 14
      //   58: iconst_0
      //   59: istore 15
      //   61: iconst_0
      //   62: istore 8
      //   64: iconst_0
      //   65: istore 6
      //   67: iload 16
      //   69: lookupswitch	default:+107->176, 1:+1053->1122, 2:+808->877, 3:+729->798, 4:+650->719, 5:+542->611, 6:+1053->1122, 7:+808->877, 8:+463->532, 9:+384->453, 10:+266->335, 11:+186->255, 12:+113->182
      //   176: goto +3 -> 179
      //   179: goto +1100 -> 1279
      //   182: aload 22
      //   184: astore 21
      //   186: aload_0
      //   187: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   190: newarray double
      //   192: astore_1
      //   193: aload 22
      //   195: astore 21
      //   197: aload_0
      //   198: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   201: istore 7
      //   203: iload 6
      //   205: iload 7
      //   207: if_icmpge +27 -> 234
      //   210: aload 22
      //   212: astore 21
      //   214: aload 22
      //   216: invokevirtual 210	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readDouble	()D
      //   219: dstore_3
      //   220: aload_1
      //   221: iload 6
      //   223: dload_3
      //   224: dastore
      //   225: iload 6
      //   227: iconst_1
      //   228: iadd
      //   229: istore 6
      //   231: goto -38 -> 193
      //   234: aload 22
      //   236: invokevirtual 215	java/io/InputStream:close	()V
      //   239: aload_1
      //   240: areturn
      //   241: astore 21
      //   243: ldc -39
      //   245: ldc -37
      //   247: aload 21
      //   249: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   252: pop
      //   253: aload_1
      //   254: areturn
      //   255: aload 22
      //   257: astore 21
      //   259: aload_0
      //   260: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   263: newarray double
      //   265: astore_1
      //   266: iload 7
      //   268: istore 6
      //   270: aload 22
      //   272: astore 21
      //   274: aload_0
      //   275: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   278: istore 7
      //   280: iload 6
      //   282: iload 7
      //   284: if_icmpge +30 -> 314
      //   287: aload 22
      //   289: astore 21
      //   291: aload 22
      //   293: invokevirtual 229	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readFloat	()F
      //   296: fstore 5
      //   298: aload_1
      //   299: iload 6
      //   301: fload 5
      //   303: f2d
      //   304: dastore
      //   305: iload 6
      //   307: iconst_1
      //   308: iadd
      //   309: istore 6
      //   311: goto -41 -> 270
      //   314: aload 22
      //   316: invokevirtual 215	java/io/InputStream:close	()V
      //   319: aload_1
      //   320: areturn
      //   321: astore 21
      //   323: ldc -39
      //   325: ldc -37
      //   327: aload 21
      //   329: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   332: pop
      //   333: aload_1
      //   334: areturn
      //   335: aload 22
      //   337: astore 21
      //   339: aload_0
      //   340: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   343: anewarray 96	androidx/exifinterface/media/ExifInterface$Rational
      //   346: astore_1
      //   347: iload 10
      //   349: istore 6
      //   351: aload 22
      //   353: astore 21
      //   355: aload_0
      //   356: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   359: istore 7
      //   361: iload 6
      //   363: iload 7
      //   365: if_icmpge +67 -> 432
      //   368: aload 22
      //   370: astore 21
      //   372: aload 22
      //   374: invokevirtual 232	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readInt	()I
      //   377: istore 7
      //   379: iload 7
      //   381: i2l
      //   382: lstore 17
      //   384: aload 22
      //   386: astore 21
      //   388: aload 22
      //   390: invokevirtual 232	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readInt	()I
      //   393: istore 7
      //   395: iload 7
      //   397: i2l
      //   398: lstore 19
      //   400: aload 22
      //   402: astore 21
      //   404: new 96	androidx/exifinterface/media/ExifInterface$Rational
      //   407: dup
      //   408: lload 17
      //   410: lload 19
      //   412: invokespecial 235	androidx/exifinterface/media/ExifInterface$Rational:<init>	(JJ)V
      //   415: astore 23
      //   417: aload_1
      //   418: iload 6
      //   420: aload 23
      //   422: aastore
      //   423: iload 6
      //   425: iconst_1
      //   426: iadd
      //   427: istore 6
      //   429: goto -78 -> 351
      //   432: aload 22
      //   434: invokevirtual 215	java/io/InputStream:close	()V
      //   437: aload_1
      //   438: areturn
      //   439: astore 21
      //   441: ldc -39
      //   443: ldc -37
      //   445: aload 21
      //   447: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   450: pop
      //   451: aload_1
      //   452: areturn
      //   453: aload 22
      //   455: astore 21
      //   457: aload_0
      //   458: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   461: newarray int
      //   463: astore_1
      //   464: iload 11
      //   466: istore 6
      //   468: aload 22
      //   470: astore 21
      //   472: aload_0
      //   473: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   476: istore 7
      //   478: iload 6
      //   480: iload 7
      //   482: if_icmpge +29 -> 511
      //   485: aload 22
      //   487: astore 21
      //   489: aload 22
      //   491: invokevirtual 232	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readInt	()I
      //   494: istore 7
      //   496: aload_1
      //   497: iload 6
      //   499: iload 7
      //   501: iastore
      //   502: iload 6
      //   504: iconst_1
      //   505: iadd
      //   506: istore 6
      //   508: goto -40 -> 468
      //   511: aload 22
      //   513: invokevirtual 215	java/io/InputStream:close	()V
      //   516: aload_1
      //   517: areturn
      //   518: astore 21
      //   520: ldc -39
      //   522: ldc -37
      //   524: aload 21
      //   526: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   529: pop
      //   530: aload_1
      //   531: areturn
      //   532: aload 22
      //   534: astore 21
      //   536: aload_0
      //   537: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   540: newarray int
      //   542: astore_1
      //   543: iload 12
      //   545: istore 6
      //   547: aload 22
      //   549: astore 21
      //   551: aload_0
      //   552: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   555: istore 7
      //   557: iload 6
      //   559: iload 7
      //   561: if_icmpge +29 -> 590
      //   564: aload 22
      //   566: astore 21
      //   568: aload 22
      //   570: invokevirtual 239	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readShort	()S
      //   573: istore 7
      //   575: aload_1
      //   576: iload 6
      //   578: iload 7
      //   580: iastore
      //   581: iload 6
      //   583: iconst_1
      //   584: iadd
      //   585: istore 6
      //   587: goto -40 -> 547
      //   590: aload 22
      //   592: invokevirtual 215	java/io/InputStream:close	()V
      //   595: aload_1
      //   596: areturn
      //   597: astore 21
      //   599: ldc -39
      //   601: ldc -37
      //   603: aload 21
      //   605: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   608: pop
      //   609: aload_1
      //   610: areturn
      //   611: aload 22
      //   613: astore 21
      //   615: aload_0
      //   616: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   619: anewarray 96	androidx/exifinterface/media/ExifInterface$Rational
      //   622: astore_1
      //   623: iload 13
      //   625: istore 6
      //   627: aload 22
      //   629: astore 21
      //   631: aload_0
      //   632: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   635: istore 7
      //   637: iload 6
      //   639: iload 7
      //   641: if_icmpge +57 -> 698
      //   644: aload 22
      //   646: astore 21
      //   648: aload 22
      //   650: invokevirtual 243	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedInt	()J
      //   653: lstore 17
      //   655: aload 22
      //   657: astore 21
      //   659: aload 22
      //   661: invokevirtual 243	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedInt	()J
      //   664: lstore 19
      //   666: aload 22
      //   668: astore 21
      //   670: new 96	androidx/exifinterface/media/ExifInterface$Rational
      //   673: dup
      //   674: lload 17
      //   676: lload 19
      //   678: invokespecial 235	androidx/exifinterface/media/ExifInterface$Rational:<init>	(JJ)V
      //   681: astore 23
      //   683: aload_1
      //   684: iload 6
      //   686: aload 23
      //   688: aastore
      //   689: iload 6
      //   691: iconst_1
      //   692: iadd
      //   693: istore 6
      //   695: goto -68 -> 627
      //   698: aload 22
      //   700: invokevirtual 215	java/io/InputStream:close	()V
      //   703: aload_1
      //   704: areturn
      //   705: astore 21
      //   707: ldc -39
      //   709: ldc -37
      //   711: aload 21
      //   713: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   716: pop
      //   717: aload_1
      //   718: areturn
      //   719: aload 22
      //   721: astore 21
      //   723: aload_0
      //   724: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   727: newarray long
      //   729: astore_1
      //   730: iload 14
      //   732: istore 6
      //   734: aload 22
      //   736: astore 21
      //   738: aload_0
      //   739: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   742: istore 7
      //   744: iload 6
      //   746: iload 7
      //   748: if_icmpge +29 -> 777
      //   751: aload 22
      //   753: astore 21
      //   755: aload 22
      //   757: invokevirtual 243	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedInt	()J
      //   760: lstore 17
      //   762: aload_1
      //   763: iload 6
      //   765: lload 17
      //   767: lastore
      //   768: iload 6
      //   770: iconst_1
      //   771: iadd
      //   772: istore 6
      //   774: goto -40 -> 734
      //   777: aload 22
      //   779: invokevirtual 215	java/io/InputStream:close	()V
      //   782: aload_1
      //   783: areturn
      //   784: astore 21
      //   786: ldc -39
      //   788: ldc -37
      //   790: aload 21
      //   792: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   795: pop
      //   796: aload_1
      //   797: areturn
      //   798: aload 22
      //   800: astore 21
      //   802: aload_0
      //   803: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   806: newarray int
      //   808: astore_1
      //   809: iload 15
      //   811: istore 6
      //   813: aload 22
      //   815: astore 21
      //   817: aload_0
      //   818: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   821: istore 7
      //   823: iload 6
      //   825: iload 7
      //   827: if_icmpge +29 -> 856
      //   830: aload 22
      //   832: astore 21
      //   834: aload 22
      //   836: invokevirtual 246	androidx/exifinterface/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedShort	()I
      //   839: istore 7
      //   841: aload_1
      //   842: iload 6
      //   844: iload 7
      //   846: iastore
      //   847: iload 6
      //   849: iconst_1
      //   850: iadd
      //   851: istore 6
      //   853: goto -40 -> 813
      //   856: aload 22
      //   858: invokevirtual 215	java/io/InputStream:close	()V
      //   861: aload_1
      //   862: areturn
      //   863: astore 21
      //   865: ldc -39
      //   867: ldc -37
      //   869: aload 21
      //   871: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   874: pop
      //   875: aload_1
      //   876: areturn
      //   877: aload 22
      //   879: astore 21
      //   881: aload_0
      //   882: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   885: istore 7
      //   887: aload 22
      //   889: astore 21
      //   891: getstatic 249	androidx/exifinterface/media/ExifInterface:EXIF_ASCII_PREFIX	[B
      //   894: arraylength
      //   895: istore 10
      //   897: iload 8
      //   899: istore 6
      //   901: iload 7
      //   903: iload 10
      //   905: if_icmplt +93 -> 998
      //   908: iconst_0
      //   909: istore 6
      //   911: aload 22
      //   913: astore 21
      //   915: getstatic 249	androidx/exifinterface/media/ExifInterface:EXIF_ASCII_PREFIX	[B
      //   918: arraylength
      //   919: istore 10
      //   921: iload 9
      //   923: istore 7
      //   925: iload 6
      //   927: iload 10
      //   929: if_icmpge +50 -> 979
      //   932: aload 22
      //   934: astore 21
      //   936: aload_0
      //   937: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   940: iload 6
      //   942: baload
      //   943: istore 7
      //   945: aload 22
      //   947: astore 21
      //   949: getstatic 249	androidx/exifinterface/media/ExifInterface:EXIF_ASCII_PREFIX	[B
      //   952: iload 6
      //   954: baload
      //   955: istore 10
      //   957: iload 7
      //   959: iload 10
      //   961: if_icmpeq +9 -> 970
      //   964: iconst_0
      //   965: istore 7
      //   967: goto +12 -> 979
      //   970: iload 6
      //   972: iconst_1
      //   973: iadd
      //   974: istore 6
      //   976: goto -65 -> 911
      //   979: iload 8
      //   981: istore 6
      //   983: iload 7
      //   985: ifeq +13 -> 998
      //   988: aload 22
      //   990: astore 21
      //   992: getstatic 249	androidx/exifinterface/media/ExifInterface:EXIF_ASCII_PREFIX	[B
      //   995: arraylength
      //   996: istore 6
      //   998: aload 22
      //   1000: astore 21
      //   1002: new 108	java/lang/StringBuilder
      //   1005: dup
      //   1006: invokespecial 109	java/lang/StringBuilder:<init>	()V
      //   1009: astore_1
      //   1010: aload 22
      //   1012: astore 21
      //   1014: aload_0
      //   1015: getfield 26	androidx/exifinterface/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   1018: istore 7
      //   1020: iload 6
      //   1022: iload 7
      //   1024: if_icmpge +68 -> 1092
      //   1027: aload 22
      //   1029: astore 21
      //   1031: aload_0
      //   1032: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   1035: iload 6
      //   1037: baload
      //   1038: istore 7
      //   1040: iload 7
      //   1042: ifne +6 -> 1048
      //   1045: goto +47 -> 1092
      //   1048: iload 7
      //   1050: bipush 32
      //   1052: if_icmplt +20 -> 1072
      //   1055: iload 7
      //   1057: i2c
      //   1058: istore_2
      //   1059: aload 22
      //   1061: astore 21
      //   1063: aload_1
      //   1064: iload_2
      //   1065: invokevirtual 116	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
      //   1068: pop
      //   1069: goto +14 -> 1083
      //   1072: aload 22
      //   1074: astore 21
      //   1076: aload_1
      //   1077: bipush 63
      //   1079: invokevirtual 116	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
      //   1082: pop
      //   1083: iload 6
      //   1085: iconst_1
      //   1086: iadd
      //   1087: istore 6
      //   1089: goto -79 -> 1010
      //   1092: aload 22
      //   1094: astore 21
      //   1096: aload_1
      //   1097: invokevirtual 120	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1100: astore_1
      //   1101: aload 22
      //   1103: invokevirtual 215	java/io/InputStream:close	()V
      //   1106: aload_1
      //   1107: areturn
      //   1108: astore 21
      //   1110: ldc -39
      //   1112: ldc -37
      //   1114: aload 21
      //   1116: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1119: pop
      //   1120: aload_1
      //   1121: areturn
      //   1122: aload 22
      //   1124: astore 21
      //   1126: aload_0
      //   1127: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   1130: arraylength
      //   1131: istore 6
      //   1133: iload 6
      //   1135: iconst_1
      //   1136: if_icmpne +97 -> 1233
      //   1139: aload 22
      //   1141: astore 21
      //   1143: aload_0
      //   1144: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   1147: iconst_0
      //   1148: baload
      //   1149: istore 6
      //   1151: iload 6
      //   1153: iflt +80 -> 1233
      //   1156: aload 22
      //   1158: astore 21
      //   1160: aload_0
      //   1161: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   1164: iconst_0
      //   1165: baload
      //   1166: istore 6
      //   1168: iload 6
      //   1170: iconst_1
      //   1171: if_icmpgt +62 -> 1233
      //   1174: aload 22
      //   1176: astore 21
      //   1178: aload_0
      //   1179: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   1182: iconst_0
      //   1183: baload
      //   1184: istore 6
      //   1186: iload 6
      //   1188: bipush 48
      //   1190: iadd
      //   1191: i2c
      //   1192: istore_2
      //   1193: aload 22
      //   1195: astore 21
      //   1197: new 38	java/lang/String
      //   1200: dup
      //   1201: iconst_1
      //   1202: newarray char
      //   1204: dup
      //   1205: iconst_0
      //   1206: iload_2
      //   1207: castore
      //   1208: invokespecial 252	java/lang/String:<init>	([C)V
      //   1211: astore_1
      //   1212: aload 22
      //   1214: invokevirtual 215	java/io/InputStream:close	()V
      //   1217: aload_1
      //   1218: areturn
      //   1219: astore 21
      //   1221: ldc -39
      //   1223: ldc -37
      //   1225: aload 21
      //   1227: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1230: pop
      //   1231: aload_1
      //   1232: areturn
      //   1233: aload_0
      //   1234: getfield 30	androidx/exifinterface/media/ExifInterface$ExifAttribute:bytes	[B
      //   1237: astore_1
      //   1238: getstatic 52	androidx/exifinterface/media/ExifInterface:ASCII	Ljava/nio/charset/Charset;
      //   1241: astore 23
      //   1243: aload 22
      //   1245: astore 21
      //   1247: new 38	java/lang/String
      //   1250: dup
      //   1251: aload_1
      //   1252: aload 23
      //   1254: invokespecial 255	java/lang/String:<init>	([BLjava/nio/charset/Charset;)V
      //   1257: astore_1
      //   1258: aload 22
      //   1260: invokevirtual 215	java/io/InputStream:close	()V
      //   1263: aload_1
      //   1264: areturn
      //   1265: astore 21
      //   1267: ldc -39
      //   1269: ldc -37
      //   1271: aload 21
      //   1273: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1276: pop
      //   1277: aload_1
      //   1278: areturn
      //   1279: aload 22
      //   1281: invokevirtual 215	java/io/InputStream:close	()V
      //   1284: aconst_null
      //   1285: areturn
      //   1286: astore_1
      //   1287: ldc -39
      //   1289: ldc -37
      //   1291: aload_1
      //   1292: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1295: pop
      //   1296: aconst_null
      //   1297: areturn
      //   1298: astore 21
      //   1300: aload 22
      //   1302: astore_1
      //   1303: aload 21
      //   1305: astore 22
      //   1307: goto +14 -> 1321
      //   1310: astore_1
      //   1311: aconst_null
      //   1312: astore 21
      //   1314: goto +44 -> 1358
      //   1317: astore 22
      //   1319: aconst_null
      //   1320: astore_1
      //   1321: aload_1
      //   1322: astore 21
      //   1324: ldc -39
      //   1326: ldc_w 257
      //   1329: aload 22
      //   1331: invokestatic 260	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1334: pop
      //   1335: aload_1
      //   1336: ifnull +49 -> 1385
      //   1339: aload_1
      //   1340: invokevirtual 215	java/io/InputStream:close	()V
      //   1343: aconst_null
      //   1344: areturn
      //   1345: astore_1
      //   1346: ldc -39
      //   1348: ldc -37
      //   1350: aload_1
      //   1351: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1354: pop
      //   1355: aconst_null
      //   1356: areturn
      //   1357: astore_1
      //   1358: aload 21
      //   1360: ifnull +23 -> 1383
      //   1363: aload 21
      //   1365: invokevirtual 215	java/io/InputStream:close	()V
      //   1368: goto +15 -> 1383
      //   1371: astore 21
      //   1373: ldc -39
      //   1375: ldc -37
      //   1377: aload 21
      //   1379: invokestatic 225	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1382: pop
      //   1383: aload_1
      //   1384: athrow
      //   1385: aconst_null
      //   1386: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	1387	0	this	ExifAttribute
      //   0	1387	1	paramByteOrder	ByteOrder
      //   1058	149	2	c	char
      //   219	5	3	d	double
      //   296	6	5	f	float
      //   65	1126	6	i	int
      //   41	1015	7	j	int
      //   62	918	8	k	int
      //   38	884	9	m	int
      //   44	918	10	n	int
      //   47	418	11	i1	int
      //   50	494	12	i2	int
      //   53	571	13	i3	int
      //   56	675	14	i4	int
      //   59	751	15	i5	int
      //   35	33	16	i6	int
      //   382	384	17	l1	long
      //   398	279	19	l2	long
      //   4	209	21	localObject1	Object
      //   241	7	21	localIOException1	IOException
      //   257	33	21	localObject2	Object
      //   321	7	21	localIOException2	IOException
      //   337	66	21	localObject3	Object
      //   439	7	21	localIOException3	IOException
      //   455	33	21	localObject4	Object
      //   518	7	21	localIOException4	IOException
      //   534	33	21	localObject5	Object
      //   597	7	21	localIOException5	IOException
      //   613	56	21	localObject6	Object
      //   705	7	21	localIOException6	IOException
      //   721	33	21	localObject7	Object
      //   784	7	21	localIOException7	IOException
      //   800	33	21	localObject8	Object
      //   863	7	21	localIOException8	IOException
      //   879	216	21	localObject9	Object
      //   1108	7	21	localIOException9	IOException
      //   1124	72	21	localObject10	Object
      //   1219	7	21	localIOException10	IOException
      //   1245	1	21	localObject11	Object
      //   1265	7	21	localIOException11	IOException
      //   1298	6	21	localIOException12	IOException
      //   1312	52	21	localByteOrder	ByteOrder
      //   1371	7	21	localIOException13	IOException
      //   15	1291	22	localObject12	Object
      //   1317	13	22	localIOException14	IOException
      //   415	838	23	localObject13	Object
      // Exception table:
      //   from	to	target	type
      //   234	239	241	java/io/IOException
      //   314	319	321	java/io/IOException
      //   432	437	439	java/io/IOException
      //   511	516	518	java/io/IOException
      //   590	595	597	java/io/IOException
      //   698	703	705	java/io/IOException
      //   777	782	784	java/io/IOException
      //   856	861	863	java/io/IOException
      //   1101	1106	1108	java/io/IOException
      //   1212	1217	1219	java/io/IOException
      //   1258	1263	1265	java/io/IOException
      //   1279	1284	1286	java/io/IOException
      //   21	27	1298	java/io/IOException
      //   214	220	1298	java/io/IOException
      //   291	298	1298	java/io/IOException
      //   372	379	1298	java/io/IOException
      //   388	395	1298	java/io/IOException
      //   404	417	1298	java/io/IOException
      //   489	496	1298	java/io/IOException
      //   568	575	1298	java/io/IOException
      //   648	655	1298	java/io/IOException
      //   659	666	1298	java/io/IOException
      //   670	683	1298	java/io/IOException
      //   755	762	1298	java/io/IOException
      //   834	841	1298	java/io/IOException
      //   1002	1010	1298	java/io/IOException
      //   1063	1069	1298	java/io/IOException
      //   1076	1083	1298	java/io/IOException
      //   1096	1101	1298	java/io/IOException
      //   1197	1212	1298	java/io/IOException
      //   1247	1258	1298	java/io/IOException
      //   6	17	1310	java/lang/Throwable
      //   6	17	1317	java/io/IOException
      //   1339	1343	1345	java/io/IOException
      //   21	27	1357	java/lang/Throwable
      //   31	37	1357	java/lang/Throwable
      //   186	193	1357	java/lang/Throwable
      //   197	203	1357	java/lang/Throwable
      //   214	220	1357	java/lang/Throwable
      //   259	266	1357	java/lang/Throwable
      //   274	280	1357	java/lang/Throwable
      //   291	298	1357	java/lang/Throwable
      //   339	347	1357	java/lang/Throwable
      //   355	361	1357	java/lang/Throwable
      //   372	379	1357	java/lang/Throwable
      //   388	395	1357	java/lang/Throwable
      //   404	417	1357	java/lang/Throwable
      //   457	464	1357	java/lang/Throwable
      //   472	478	1357	java/lang/Throwable
      //   489	496	1357	java/lang/Throwable
      //   536	543	1357	java/lang/Throwable
      //   551	557	1357	java/lang/Throwable
      //   568	575	1357	java/lang/Throwable
      //   615	623	1357	java/lang/Throwable
      //   631	637	1357	java/lang/Throwable
      //   648	655	1357	java/lang/Throwable
      //   659	666	1357	java/lang/Throwable
      //   670	683	1357	java/lang/Throwable
      //   723	730	1357	java/lang/Throwable
      //   738	744	1357	java/lang/Throwable
      //   755	762	1357	java/lang/Throwable
      //   802	809	1357	java/lang/Throwable
      //   817	823	1357	java/lang/Throwable
      //   834	841	1357	java/lang/Throwable
      //   881	887	1357	java/lang/Throwable
      //   891	897	1357	java/lang/Throwable
      //   915	921	1357	java/lang/Throwable
      //   936	945	1357	java/lang/Throwable
      //   949	957	1357	java/lang/Throwable
      //   992	998	1357	java/lang/Throwable
      //   1002	1010	1357	java/lang/Throwable
      //   1014	1020	1357	java/lang/Throwable
      //   1031	1040	1357	java/lang/Throwable
      //   1063	1069	1357	java/lang/Throwable
      //   1076	1083	1357	java/lang/Throwable
      //   1096	1101	1357	java/lang/Throwable
      //   1126	1133	1357	java/lang/Throwable
      //   1143	1151	1357	java/lang/Throwable
      //   1160	1168	1357	java/lang/Throwable
      //   1178	1186	1357	java/lang/Throwable
      //   1197	1212	1357	java/lang/Throwable
      //   1247	1258	1357	java/lang/Throwable
      //   1324	1335	1357	java/lang/Throwable
      //   1363	1368	1371	java/io/IOException
    }
    
    public int size()
    {
      return ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[format] * numberOfComponents;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("(");
      localStringBuilder.append(ExifInterface.IFD_FORMAT_NAMES[format]);
      localStringBuilder.append(", data length:");
      localStringBuilder.append(bytes.length);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
  }
  
  static class ExifTag
  {
    public final String name;
    public final int number;
    public final int primaryFormat;
    public final int secondaryFormat;
    
    ExifTag(String paramString, int paramInt1, int paramInt2)
    {
      name = paramString;
      number = paramInt1;
      primaryFormat = paramInt2;
      secondaryFormat = -1;
    }
    
    ExifTag(String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      name = paramString;
      number = paramInt1;
      primaryFormat = paramInt2;
      secondaryFormat = paramInt3;
    }
    
    boolean isFormatCompatible(int paramInt)
    {
      if (primaryFormat != 7)
      {
        if (paramInt == 7) {
          return true;
        }
        if (primaryFormat != paramInt)
        {
          if (secondaryFormat == paramInt) {
            return true;
          }
          if (((primaryFormat == 4) || (secondaryFormat == 4)) && (paramInt == 3)) {
            return true;
          }
          if (((primaryFormat == 9) || (secondaryFormat == 9)) && (paramInt == 8)) {
            return true;
          }
          return ((primaryFormat == 12) || (secondaryFormat == 12)) && (paramInt == 11);
        }
      }
      return true;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public static @interface IfdType {}
  
  private static class Rational
  {
    public final long denominator;
    public final long numerator;
    
    Rational(double paramDouble)
    {
      this((paramDouble * 10000.0D), 10000L);
    }
    
    Rational(long paramLong1, long paramLong2)
    {
      if (paramLong2 == 0L)
      {
        numerator = 0L;
        denominator = 1L;
        return;
      }
      numerator = paramLong1;
      denominator = paramLong2;
    }
    
    public double calculate()
    {
      return numerator / denominator;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(numerator);
      localStringBuilder.append("/");
      localStringBuilder.append(denominator);
      return localStringBuilder.toString();
    }
  }
}
