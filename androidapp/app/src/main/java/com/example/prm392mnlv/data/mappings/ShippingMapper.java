package com.example.prm392mnlv.data.mappings;

import com.example.prm392mnlv.data.models.ShippingMethod;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface ShippingMapper {
    ShippingMapper INSTANCE = Mappers.getMapper(ShippingMapper.class);

    Map<Integer, String> MAPPINGS = new HashMap<>() {{
        put(1, "InStore");
        put(2, "UserAddress");
    }};

    default String toDto(ShippingMethod model) {
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
