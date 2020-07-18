package com.google.android.exoplayer2.scheduler;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.PersistableBundle;
import com.google.android.exoplayer2.util.Util;

@TargetApi(21)
public final class PlatformScheduler
  implements Scheduler
{
  private static final String KEY_REQUIREMENTS = "requirements";
  private static final String KEY_SERVICE_ACTION = "service_action";
  private static final String KEY_SERVICE_PACKAGE = "service_package";
  private static final String PAGE_KEY = "PlatformScheduler";
  private final int jobId;
  private final JobScheduler jobScheduler;
  private final ComponentName jobServiceComponentName;
  
  public PlatformScheduler(Context paramContext, int paramInt)
  {
    jobId = paramInt;
    jobServiceComponentName = new ComponentName(paramContext, PlatformSchedulerService.class);
    jobScheduler = ((JobScheduler)paramContext.getSystemService("jobscheduler"));
  }
  
  private static JobInfo buildJobInfo(int paramInt, ComponentName paramComponentName, Requirements paramRequirements, String paramString1, String paramString2)
  {
    paramComponentName = new JobInfo.Builder(paramInt, paramComponentName);
    switch (paramRequirements.getRequiredNetworkType())
    {
    default: 
      throw new UnsupportedOperationException();
    case 4: 
      if (Util.SDK_INT >= 26) {
        paramInt = 4;
      } else {
        throw new UnsupportedOperationException();
      }
      break;
    case 3: 
      if (Util.SDK_INT >= 24) {
        paramInt = 3;
      } else {
        throw new UnsupportedOperationException();
      }
      break;
    case 2: 
      paramInt = 2;
      break;
    case 1: 
      paramInt = 1;
      break;
    case 0: 
      paramInt = 0;
    }
    paramComponentName.setRequiredNetworkType(paramInt);
    paramComponentName.setRequiresDeviceIdle(paramRequirements.isIdleRequired());
    paramComponentName.setRequiresCharging(paramRequirements.isChargingRequired());
    paramComponentName.setPersisted(true);
    PersistableBundle localPersistableBundle = new PersistableBundle();
    localPersistableBundle.putString("service_action", paramString1);
    localPersistableBundle.putString("service_package", paramString2);
    localPersistableBundle.putInt("requirements", paramRequirements.getRequirementsData());
    paramComponentName.setExtras(localPersistableBundle);
    return paramComponentName.build();
  }
  
  private static void logd(String paramString) {}
  
  public boolean cancel()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Canceling job: ");
    localStringBuilder.append(jobId);
    logd(localStringBuilder.toString());
    jobScheduler.cancel(jobId);
    return true;
  }
  
  public boolean schedule(Requirements paramRequirements, String paramString1, String paramString2)
  {
    paramRequirements = buildJobInfo(jobId, jobServiceComponentName, paramRequirements, paramString2, paramString1);
    int i = jobScheduler.schedule(paramRequirements);
    paramRequirements = new StringBuilder();
    paramRequirements.append("Scheduling job: ");
    paramRequirements.append(jobId);
    paramRequirements.append(" result: ");
    paramRequirements.append(i);
    logd(paramRequirements.toString());
    return i == 1;
  }
  
  public static final class PlatformSchedulerService
    extends JobService
  {
    public PlatformSchedulerService() {}
    
    public boolean onStartJob(JobParameters paramJobParameters)
    {
      PlatformScheduler.logd("PlatformSchedulerService started");
      Object localObject = paramJobParameters.getExtras();
      if (new Requirements(((BaseBundle)localObject).getInt("requirements")).checkRequirements(this))
      {
        PlatformScheduler.logd("Requirements are met");
        paramJobParameters = ((BaseBundle)localObject).getString("service_action");
        localObject = ((BaseBundle)localObject).getString("service_package");
        Intent localIntent = new Intent(paramJobParameters).setPackage((String)localObject);
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Starting service action: ");
        localStringBuilder.append(paramJobParameters);
        localStringBuilder.append(" package: ");
        localStringBuilder.append((String)localObject);
        PlatformScheduler.logd(localStringBuilder.toString());
        Util.startForegroundService(this, localIntent);
      }
      else
      {
        PlatformScheduler.logd("Requirements are not met");
        jobFinished(paramJobParameters, true);
      }
      return false;
    }
    
    public boolean onStopJob(JobParameters paramJobParameters)
    {
      return false;
    }
  }
}
