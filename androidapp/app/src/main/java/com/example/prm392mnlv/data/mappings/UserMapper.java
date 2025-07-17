package com.example.prm392mnlv.data.mappings;

import com.example.prm392mnlv.data.dto.request.UserProfileUpdateRequest;
import com.example.prm392mnlv.data.dto.response.UserProfileResponse;
import com.example.prm392mnlv.data.models.User;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toModel(UserProfileResponse dto);

    UserProfileUpdateRequest toUpdateDto(User user);
}
