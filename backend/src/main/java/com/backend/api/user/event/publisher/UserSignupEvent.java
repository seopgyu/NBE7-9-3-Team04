package com.backend.api.user.event.publisher;

import com.backend.domain.user.entity.User;

public record UserSignupEvent(User user) {

}

