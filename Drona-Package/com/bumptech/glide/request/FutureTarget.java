package com.bumptech.glide.request;

import com.bumptech.glide.request.target.Target;
import java.util.concurrent.Future;

public abstract interface FutureTarget<R>
  extends Future<R>, Target<R>
{}
