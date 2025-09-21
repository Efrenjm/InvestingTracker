package org.efrenjm.investingtracker.domain.dto;

public record PublicProfile (
	String id,
	String username,
	String email,
	String phoneNumber,
	String firstName,
	String middleName,
	String lastName,
	String profilePicture
)
{}