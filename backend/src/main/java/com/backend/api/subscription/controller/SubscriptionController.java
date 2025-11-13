package com.backend.api.subscription.controller;


import com.backend.api.subscription.dto.response.SubscriptionResponse;
import com.backend.api.subscription.service.SubscriptionService;
import com.backend.global.Rq.Rq;
import com.backend.global.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription", description = "구독 관리 API(사용자)")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final Rq rq;


    @GetMapping("/me")
    @Operation(summary = "내 구독 조회", description = "로그인된 사용자의 구독 정보를 조회합니다.")
    public ApiResponse<SubscriptionResponse> getMySubscription() {
        Long userId = rq.getUser().getId();
        SubscriptionResponse response = subscriptionService.getSubscriptionByUserId(userId);
        return ApiResponse.ok("구독 정보를 불러왔습니다.", response);
    }


    @DeleteMapping("/cancel/{customerKey}")
    @Operation(summary = "구독 취소", description = "고객의 구독을 취소합니다. 다음 결제일에 자동결제가 실행되지 않습니다.")
    public ApiResponse<SubscriptionResponse> cancelSubscription(@PathVariable String customerKey) {
        SubscriptionResponse response = subscriptionService.cancelSubscription(customerKey);
        return ApiResponse.ok("구독이 성공적으로 취소되었습니다.", response);
    }

}
