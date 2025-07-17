package com.example.prm392mnlv.retrofit.json;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.squareup.moshi.JsonQualifier;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
@JsonQualifier
public @interface JsonPath {
    String value();
}
