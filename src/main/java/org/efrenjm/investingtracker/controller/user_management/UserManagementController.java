package org.efrenjm.investingtracker.controller.user_management;

import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.service.user_management.UserManagementService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/user-management")
public class UserManagementController {
	private final UserManagementService userManagementService;

	@GetMapping("/profile/{userId}")
	public Mono<Profile> findUser(@PathVariable String userId) {
		return userManagementService.findUser(userId);
	}
}
