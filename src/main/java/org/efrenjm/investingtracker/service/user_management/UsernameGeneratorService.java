package org.efrenjm.investingtracker.service.user_management;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.repository.AuthRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsernameGeneratorService {
//	private final Random random = new SecureRandom();
	private final AuthRepository authRepository;
//	private static final int MAX_ATTEMPS = 10;
//
//	private static final List<String> ADJECTIVES = Arrays.asList(
//			"swift", "brave", "bright", "clever", "mighty", "gentle", "noble",
//			"rapid", "silent", "cosmic", "mystic", "golden", "silver", "crystal",
//			"arctic", "lunar", "solar", "stellar", "fierce", "wise"
//	);
//
//	private static final List<String> NOUNS = Arrays.asList(
//			"eagle", "lion", "wolf", "falcon", "tiger", "phoenix", "dragon",
//			"panther", "jaguar", "hawk", "dolphin", "rider", "hunter", "spotter",
//			"ranger", "scout", "voyager", "pioneer", "sentinel", "guardian"
//	);


//	public String generateUsername() {
//		String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
//		String noun = NOUNS.get(random.nextInt(NOUNS.size()));
//		int number = random.nextInt(10000);
//
//		return String.format("%s.%s%03d", adjective, noun, number);
//	}

//	public Flux<String> suggestUniqueUsernames(int count) {
//		return Flux.range(0, count)
//				.flatMap(i -> generateUniqueUsername())
//				.distinct()
//				.collectList()
//				.flatMapMany(list -> {
//					if (list.size() < count) {
//						return suggestUniqueUsernames(count - list.size()).concatWith(Flux.fromIterable(list));
//					}
//					return Flux.fromIterable(list);
//				});
//	}

//	public Mono<Boolean> isUsernameAvailable(String username) {
//		return authRepository.existsByUsername(username)
//				.map(exists -> !exists);
//	}

//	private Mono<String> generateUniqueUsername() {
//		return Mono.defer(() -> {
//			String username = generateUsername();
//
//			return isUsernameAvailable(username)
//					.flatMap(available -> available ? Mono.just(username) : generateUniqueUsername())
//					.retry(MAX_ATTEMPS);
//		});
//	}
}
