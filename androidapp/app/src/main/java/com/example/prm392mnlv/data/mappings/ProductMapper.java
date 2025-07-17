package com.example.prm392mnlv.data.mappings;

import com.example.prm392mnlv.data.dto.response.ProductResponse;
import com.example.prm392mnlv.data.models.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = MappingMethods.class)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "price", qualifiedByName = {"MappingMethods", "DoubleToBigDecimal"})
    @Mapping(target = "imageUrl", qualifiedByName = {"MappingMethods", "StringToUri"})
    Product toModel(ProductResponse dto);
}
