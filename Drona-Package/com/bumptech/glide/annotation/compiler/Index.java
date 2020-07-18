package com.bumptech.glide.annotation.compiler;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({java.lang.annotation.ElementType.TYPE})
@interface Index
{
  String[] extensions();
  
  String[] modules();
}
