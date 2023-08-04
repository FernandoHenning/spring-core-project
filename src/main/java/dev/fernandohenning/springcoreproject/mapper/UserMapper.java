package dev.fernandohenning.springcoreproject.mapper;

import dev.fernandohenning.springcoreproject.dto.user.request.CreateUserRequest;
import dev.fernandohenning.springcoreproject.dto.user.response.GetUserInformationResponse;
import dev.fernandohenning.springcoreproject.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    GetUserInformationResponse toGetUserInformation(User user);

    User toUser(CreateUserRequest request);
}
