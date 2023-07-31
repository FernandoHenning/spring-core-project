package dev.fernandohenning.springcoreproject.mappers.user;

import dev.fernandohenning.springcoreproject.dto.user.response.GetUserInformationResponse;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.mapper.UserMapper;

import dev.fernandohenning.springcoreproject.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserMapperTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    void validUserToGetUserInformationResponse(){
        LocalDateTime currentDateTime =  LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password1234")
                .passwordConfirmation("password1234")
                .createdAt(currentDateTime)
                .role(Role.MODERATOR)
                .isEnabled(false)
                .isLocked(false)
                .build();

        GetUserInformationResponse dto = userMapper.toGetUserInformation(user);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(1L, dto.id());
        Assertions.assertEquals("John", dto.firstName());
        Assertions.assertEquals("Doe", dto.lastName());
        Assertions.assertEquals(currentDateTime, dto.createdAt());
        Assertions.assertEquals(Role.MODERATOR, dto.role());
    }
    @Test
    void invalidUserToGetUserInformationResponse_InvalidUser(){

        GetUserInformationResponse dto = userMapper.toGetUserInformation(null);

        Assertions.assertNull(dto);
    }

}
