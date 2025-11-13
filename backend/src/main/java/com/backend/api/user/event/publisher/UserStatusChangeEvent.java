package com.backend.api.user.event.publisher;

import com.backend.domain.user.entity.User;
import com.backend.domain.userPenalty.entity.UserPenalty;

public record UserStatusChangeEvent(User user, UserPenalty penalty) {

}
