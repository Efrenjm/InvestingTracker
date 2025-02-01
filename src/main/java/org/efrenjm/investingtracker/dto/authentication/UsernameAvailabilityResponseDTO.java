package org.efrenjm.investingtracker.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UsernameAvailabilityResponseDTO {
	private boolean available;
	private String message;
	private List<String> suggestions;
}
