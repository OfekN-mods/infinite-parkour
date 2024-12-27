package com.infinite_parkour.infinite_parkour.utils;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Nonnull
@TypeQualifierDefault(value={ElementType.METHOD, ElementType.PARAMETER})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface NonnullByDefault {}


