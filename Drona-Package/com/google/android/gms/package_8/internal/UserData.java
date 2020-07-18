package com.google.android.gms.package_8.internal;

import android.os.Build.VERSION;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.DefaultClock;
import com.google.android.gms.internal.ads.zzada;
import com.google.android.gms.internal.ads.zzajh;
import com.google.android.gms.internal.ads.zzajs;
import com.google.android.gms.internal.ads.zzalk;
import com.google.android.gms.internal.ads.zzamn;
import com.google.android.gms.internal.ads.zzaqd;
import com.google.android.gms.internal.ads.zzaqw;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzare;
import com.google.android.gms.internal.ads.zzasg;
import com.google.android.gms.internal.ads.zzavg;
import com.google.android.gms.internal.ads.zzawm;
import com.google.android.gms.internal.ads.zzaxi;
import com.google.android.gms.internal.ads.zzaxo;
import com.google.android.gms.internal.ads.zzaya;
import com.google.android.gms.internal.ads.zzayi;
import com.google.android.gms.internal.ads.zzazg;
import com.google.android.gms.internal.ads.zzazh;
import com.google.android.gms.internal.ads.zzazp;
import com.google.android.gms.internal.ads.zzbbs;
import com.google.android.gms.internal.ads.zzbbz;
import com.google.android.gms.internal.ads.zzbfs;
import com.google.android.gms.internal.ads.zzbhf;
import com.google.android.gms.internal.ads.zzuq;
import com.google.android.gms.internal.ads.zzvm;
import com.google.android.gms.internal.ads.zzvn;
import com.google.android.gms.internal.ads.zzwi;
import com.google.android.gms.package_8.internal.overlay.Diff;
import com.google.android.gms.package_8.internal.overlay.Email;
import com.google.android.gms.package_8.internal.overlay.IpAddress;
import com.google.android.gms.package_8.internal.overlay.RequestQueue;

@zzard
public final class UserData
{
  private static UserData zzbrn = new UserData();
  private final RequestQueue zzbro;
  private final zzare zzbrp;
  private final Email zzbrq;
  private final zzaqw zzbrr;
  private final zzaxi zzbrs;
  private final zzbhf zzbrt;
  private final zzaxo zzbru;
  private final zzuq zzbrv;
  private final zzawm zzbrw;
  private final zzaya zzbrx;
  private final zzvm zzbry;
  private final zzvn zzbrz;
  private final Clock zzbsa;
  private final Notifier zzbsb;
  private final zzada zzbsc;
  private final zzayi zzbsd;
  private final zzasg zzbse;
  private final zzajs zzbsf;
  private final zzbbs zzbsg;
  private final zzajh zzbsh;
  private final zzalk zzbsi;
  private final zzazg zzbsj;
  private final Diff zzbsk;
  private final IpAddress zzbsl;
  private final zzamn zzbsm;
  private final zzazh zzbsn;
  private final zzaqd zzbso;
  private final zzwi zzbsp;
  private final zzavg zzbsq;
  private final zzazp zzbsr;
  private final zzbfs zzbss;
  private final zzbbz zzbst;
  
  protected UserData()
  {
    this(new RequestQueue(), new zzare(), new Email(), new zzaqw(), new zzaxi(), new zzbhf(), zzaxo.zzcv(Build.VERSION.SDK_INT), new zzuq(), new zzawm(), new zzaya(), new zzvm(), new zzvn(), DefaultClock.getInstance(), new Notifier(), new zzada(), new zzayi(), new zzasg(), new zzajs(), new zzbbs(), new zzalk(), new zzazg(), new Diff(), new IpAddress(), new zzamn(), new zzazh(), new zzaqd(), new zzwi(), new zzavg(), new zzazp(), new zzbfs(), new zzbbz());
  }
  
  private UserData(RequestQueue paramRequestQueue, zzare paramZzare, Email paramEmail, zzaqw paramZzaqw, zzaxi paramZzaxi, zzbhf paramZzbhf, zzaxo paramZzaxo, zzuq paramZzuq, zzawm paramZzawm, zzaya paramZzaya, zzvm paramZzvm, zzvn paramZzvn, Clock paramClock, Notifier paramNotifier, zzada paramZzada, zzayi paramZzayi, zzasg paramZzasg, zzajs paramZzajs, zzbbs paramZzbbs, zzalk paramZzalk, zzazg paramZzazg, Diff paramDiff, IpAddress paramIpAddress, zzamn paramZzamn, zzazh paramZzazh, zzaqd paramZzaqd, zzwi paramZzwi, zzavg paramZzavg, zzazp paramZzazp, zzbfs paramZzbfs, zzbbz paramZzbbz)
  {
    zzbro = paramRequestQueue;
    zzbrp = paramZzare;
    zzbrq = paramEmail;
    zzbrr = paramZzaqw;
    zzbrs = paramZzaxi;
    zzbrt = paramZzbhf;
    zzbru = paramZzaxo;
    zzbrv = paramZzuq;
    zzbrw = paramZzawm;
    zzbrx = paramZzaya;
    zzbry = paramZzvm;
    zzbrz = paramZzvn;
    zzbsa = paramClock;
    zzbsb = paramNotifier;
    zzbsc = paramZzada;
    zzbsd = paramZzayi;
    zzbse = paramZzasg;
    zzbsf = paramZzajs;
    zzbsg = paramZzbbs;
    zzbsh = new zzajh();
    zzbsi = paramZzalk;
    zzbsj = paramZzazg;
    zzbsk = paramDiff;
    zzbsl = paramIpAddress;
    zzbsm = paramZzamn;
    zzbsn = paramZzazh;
    zzbso = paramZzaqd;
    zzbsp = paramZzwi;
    zzbsq = paramZzavg;
    zzbsr = paramZzazp;
    zzbss = paramZzbfs;
    zzbst = paramZzbbz;
  }
  
  public static RequestQueue zzle()
  {
    return zzbrnzzbro;
  }
  
  public static Email zzlf()
  {
    return zzbrnzzbrq;
  }
  
  public static zzaxi zzlg()
  {
    return zzbrnzzbrs;
  }
  
  public static zzbhf zzlh()
  {
    return zzbrnzzbrt;
  }
  
  public static zzaxo zzli()
  {
    return zzbrnzzbru;
  }
  
  public static zzuq zzlj()
  {
    return zzbrnzzbrv;
  }
  
  public static zzawm zzlk()
  {
    return zzbrnzzbrw;
  }
  
  public static zzaya zzll()
  {
    return zzbrnzzbrx;
  }
  
  public static zzvn zzlm()
  {
    return zzbrnzzbrz;
  }
  
  public static Clock zzln()
  {
    return zzbrnzzbsa;
  }
  
  public static Notifier zzlo()
  {
    return zzbrnzzbsb;
  }
  
  public static zzada zzlp()
  {
    return zzbrnzzbsc;
  }
  
  public static zzayi zzlq()
  {
    return zzbrnzzbsd;
  }
  
  public static zzasg zzlr()
  {
    return zzbrnzzbse;
  }
  
  public static zzbbs zzls()
  {
    return zzbrnzzbsg;
  }
  
  public static zzalk zzlt()
  {
    return zzbrnzzbsi;
  }
  
  public static zzazg zzlu()
  {
    return zzbrnzzbsj;
  }
  
  public static zzaqd zzlv()
  {
    return zzbrnzzbso;
  }
  
  public static Diff zzlw()
  {
    return zzbrnzzbsk;
  }
  
  public static IpAddress zzlx()
  {
    return zzbrnzzbsl;
  }
  
  public static zzamn zzly()
  {
    return zzbrnzzbsm;
  }
  
  public static zzazh zzlz()
  {
    return zzbrnzzbsn;
  }
  
  public static zzwi zzma()
  {
    return zzbrnzzbsp;
  }
  
  public static zzazp zzmb()
  {
    return zzbrnzzbsr;
  }
  
  public static zzbfs zzmc()
  {
    return zzbrnzzbss;
  }
  
  public static zzbbz zzmd()
  {
    return zzbrnzzbst;
  }
  
  public static zzavg zzme()
  {
    return zzbrnzzbsq;
  }
}
