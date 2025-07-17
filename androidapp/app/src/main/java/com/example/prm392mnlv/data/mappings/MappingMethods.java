package com.example.prm392mnlv.data.mappings;

import android.net.Uri;

import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Named("MappingMethods")
public class MappingMethods {

    @Named("DoubleToBigDecimal")
    BigDecimal toBigDecimal(double val) {
        return BigDecimal.valueOf(val).setScale(0, RoundingMode.HALF_EVEN);
    }

    @Named("StringToUri")
    Uri toUri(String val) {
        return Uri.parse(val);
    }
}
