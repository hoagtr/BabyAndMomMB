package com.example.prm392mnlv.data.mappings;

import com.example.prm392mnlv.data.models.PaymentMethod;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    Map<Integer, String> MAPPINGS = new HashMap<>() {{
        put(1, "COD");
        put(2, "ZaloPay");
    }};

    default String toDto(PaymentMethod model) {
        if (model == null) {
            return null;
        }

        if (MAPPINGS.containsKey(model.getId())) {
            return MAPPINGS.get(model.getId());
        } else {
            return model.getName();
        }
    }
}
