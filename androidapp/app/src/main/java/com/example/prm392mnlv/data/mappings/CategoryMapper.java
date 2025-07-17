package com.example.prm392mnlv.data.mappings;

import com.example.prm392mnlv.data.dto.response.CategoryResponse;
import com.example.prm392mnlv.data.models.Category;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toModel(CategoryResponse dto);
}
