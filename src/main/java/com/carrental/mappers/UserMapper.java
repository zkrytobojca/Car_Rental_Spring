package com.carrental.mappers;

import com.carrental.models.User;
import com.carrental.models.mt.UserMT;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mapping(source = "password", target = "password", qualifiedByName = "encrypt")
    User convertUserMT(UserMT userMT);

    @Named("encrypt")
    static String encrypt(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

}