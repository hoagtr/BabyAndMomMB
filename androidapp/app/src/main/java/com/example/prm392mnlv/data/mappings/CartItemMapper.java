package com.example.prm392mnlv.data.mappings;

import com.example.prm392mnlv.data.dto.request.CartItemCreateRequest;
import com.example.prm392mnlv.data.dto.request.CartItemUpdateRequest;
import com.example.prm392mnlv.data.dto.response.CartItemResponse;
import com.example.prm392mnlv.data.models.CartItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = MappingMethods.class)
public interface CartItemMapper {
    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(target = "unitPrice", qualifiedByName = {"MappingMethods", "DoubleToBigDecimal"})
    CartItem toModel(CartItemResponse dto);

    CartItemCreateRequest toCreateRequest(CartItem model);

    CartItemUpdateRequest toUpdateRequest(CartItem model);
}
