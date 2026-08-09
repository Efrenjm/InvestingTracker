# Backend Architecture

## Purpose and status

This document describes both the backend's observed structure and the target boundaries for new work and focused migrations. Sections labeled as current state report what exists in the repository at the time of writing. Target sections define the ports-and-adapters direction; they do not claim that existing packages already conform or authorize a repository-wide move.

## Technology baseline

- Java 21 and Spring Boot 3.3.5 are configured in `build.gradle`.
- HTTP uses Spring WebFlux and Project Reactor.
- Persistence uses reactive MongoDB; Redis infrastructure is reactive and supports session-related behavior.
- Security uses Spring Security with JWT cookie infrastructure.
- External delivery integrations include Spring Mail and Twilio.
- OpenAPI documentation uses springdoc; tests use JUnit 5, Reactor Test, Mockito, Spring Security Test, and Testcontainers.
- Unit tests and integration tests are separate Gradle tasks. `build.gradle` and the Gradle wrapper are authoritative for configured versions and tasks.

## Current structure

The production code is rooted at `org.efrenjm.investingtracker` and is currently divided into four broad package areas:

- `domain` contains models, DTO-like projections, business exceptions, domain services, and inbound/outbound port interfaces. Several ports and services currently depend on Reactor or Spring types.
- `application` contains orchestration services and application exceptions. Current services implement ports located under `domain.ports` and are registered as Spring services.
- `interfaces` contains REST controllers and DTOs, custom argument annotations, exception advice, and WebFlux filters.
- `infrastructure` contains Spring configuration, logging, JWT/security implementation, Mongo entities/repositories/adapters, Redis adapters, and provider-specific utility operations.

Unit tests live under `src/test`. Integration tests live under the separate `src/integrationTest` source set and use Docker/Testcontainers for real infrastructure wiring.

## Current request and dependency flow

REST controllers generally depend on interfaces under `domain.ports.inbound`, and application services implement those interfaces. Application services coordinate domain services and repository/security/messaging ports. Mongo adapters implement repository ports and map between persistence entities and domain models. Spring configuration composes controllers, services, security filters, repositories, and external integrations.

For example, `AuthenticationController` invokes `AuthPort`, `AuthenticationService` implements that port, and `UserRepositoryAdapter` implements `UserRepositoryPort`. This resembles a hexagonal flow, but transport types currently cross into ports and application orchestration imports concrete infrastructure and Spring transaction types. The target contract below governs new code and incremental corrections.

## Target structure

New capabilities and focused migrations should converge on this feature-oriented structure:

```text
org.efrenjm.investingtracker/
├── domain/<capability>/{model,service,exception}
├── application/<capability>/{port/in,port/out,command,result,service}
├── interfaces/rest/<capability>/{controller,dto,advice}
└── infrastructure/{config,persistence,security,notification,observability}
```

The capability placeholder is the owning business area, such as authentication, user, wallet, account, or transaction. This tree is a target for files touched by deliberate work; it is not an instruction to relocate all current packages in one change.

## Dependency direction

```text
REST/WebFlux adapter -> inbound port -> application use case -> domain
                                            |
                                            +-> outbound port <- infrastructure adapter
```

| Area | Allowed project dependencies | Forbidden project dependencies |
| --- | --- | --- |
| `domain` | Other pure domain types | `application`, `interfaces`, `infrastructure`, Spring, WebFlux, Reactor orchestration, persistence, and provider APIs |
| `application` | `domain` and deliberate Reactor composition | `interfaces`, concrete `infrastructure`, Spring Web/Security/transaction types, Mongo/Redis clients, and provider SDKs |
| `interfaces` | `application` plus deliberate boundary mapping | Concrete persistence/provider adapters and business-rule ownership |
| `infrastructure` | Application/domain contracts required to implement ports | Controllers and application/domain policy ownership |

Dependencies must remain acyclic. Dependency injection, callbacks, reactive publishers, reflection, or re-exports do not permit an outer-layer dependency to point into the core in reverse.

## Inbound ports and use cases

- Name an inbound port after one application capability, such as `StartRegistrationUseCase`, rather than a generic service collection.
- Place its framework-neutral command and result types with the owning application capability.
- A REST controller maps its DTO to a command, invokes the inbound port, and maps the result to the public response.
- Inbound ports must not accept controller DTOs, `ServerWebExchange`, `ServerHttpResponse`, HTTP status types, Mongo entities, Redis/provider objects, or Spring transaction types.
- Application use cases, scheduled jobs, and other inbound adapters may invoke an inbound port; concrete outbound adapters may not bypass it to drive business behavior.

Current inbound ports under `domain.ports.inbound` are retained until a focused migration moves each capability without changing behavior.

## Outbound ports and adapters

- Place an outbound capability port with the application policy that requires it, unless the interface expresses a truly domain-owned business abstraction.
- Use outbound ports for persistence, messaging, security primitives, time, randomness, transactions, external delivery, and other effects required by a use case.
- Keep port inputs and outputs independent of Mongo documents, Redis clients, SMTP/Twilio types, JWT libraries, HTTP response objects, and Spring transaction APIs.
- Infrastructure adapters implement these ports and perform representation mapping.
- A use case selects behavior through a port; it never constructs or selects a concrete adapter.

## Domain boundary

The target domain contains aggregates, value objects, deterministic business rules, domain services, and domain exceptions. Domain behavior is synchronous and does not perform I/O. Time, randomness, persistence, delivery, security mechanisms, and transactions enter through application-controlled capabilities.

Domain types must not require Spring annotations, WebFlux, Reactor orchestration, HTTP concepts, persistence annotations, provider SDKs, or Spring Security identities. Existing framework imports in `domain` are migration debt, not target precedent.

## Application boundary

Application code owns use-case orchestration, commands, results, inbound ports, and outbound capability ports. It may deliberately use `Mono` and `Flux` so adapter calls remain non-blocking, while domain calculations stay synchronous.

Application services depend only on domain behavior and port contracts. They do not import controllers, concrete infrastructure adapters, WebFlux exchange/response objects, concrete transaction operators, or provider SDKs. Spring composition should register application services from an outer configuration boundary rather than making framework annotations part of the use-case contract.

## Interface adapters

- REST controllers own routing, request/response DTOs, Jakarta validation, OpenAPI annotations, and HTTP metadata.
- Filters and argument resolvers translate authenticated transport context into transport-safe application input.
- Exception advice maps domain/application failures to stable public status codes and schemas.
- Controllers do not query repositories, choose notification behavior, open transactions, or decide persistence rules.
- Public contract changes require coordination with the frontend and the applicable feature specification.

## Infrastructure adapters

- MongoDB entities, Spring Data repositories, projections, and mapping adapters remain under infrastructure.
- Redis operations and session storage remain behind outbound ports.
- JWT libraries, password encoding, cryptographic operations, mail, and Twilio remain provider-specific adapters.
- Concrete logging/telemetry implementation and Spring runtime composition remain outside application and domain.
- Adapters translate technical failures without embedding application policy or exposing provider details in public responses.

## Reactive composition

- Preserve non-blocking execution across WebFlux request paths.
- Compose repository, security, and delivery effects into the returned `Mono` or `Flux` so completion and errors remain observable.
- Do not call `block()` or initiate internal `subscribe()` calls in controllers, use cases, or domain logic.
- Do not discard errors or detach correctness-critical work from the main pipeline.
- Isolate unavoidable blocking provider APIs behind an infrastructure adapter and an explicit scheduler or asynchronous delivery boundary.
- Keep domain rules deterministic and testable without Reactor when they do not perform I/O.

## HTTP validation and error mapping

- Interface DTO validation owns malformed transport input and syntactic constraints.
- Application/domain code owns business invariants and state-transition rules.
- Domain and application exceptions contain no HTTP status or response body decisions.
- Interface advice translates those failures into the documented public contract.
- Error schemas, cooldown responses, authentication responses, and other public behavior must stay consistent with frontend contracts and feature BDD specifications.

## Persistence, transactions, and external integrations

- Map explicitly between Mongo entities and core objects at the persistence adapter boundary.
- Combine application checks with database indexes, unique constraints, optimistic concurrency, or conditional updates when correctness requires both.
- Treat Mongo TTL cleanup as eventual retention behavior, not authorization or expiry enforcement.
- Expose atomic work to application code through a transaction capability port rather than `TransactionalOperator`.
- Keep Redis keys, SMTP messages, Twilio requests, JWT claims/cookies, and provider retry details inside their adapters.
- Make external delivery observable without changing a generic public security response based on delivery outcome.

## Security and privacy boundary

- Never read, expose, print, copy, commit, or log environment secrets, private keys, certificates, passwords, password hashes, OTP values, code digests, JWTs, cookies, or MongoDB, Redis, mail, and Twilio credentials.
- Inspect configuration names rather than values and redact sensitive output.
- Do not log raw email addresses or other personal or financial data; use approved redaction or non-reversible correlation identifiers.
- Use synthetic data in documentation, tests, fixtures, screenshots, and examples.
- Keep credentials and provider authentication inside infrastructure adapters.
- Keep browser authentication in backend-managed `HttpOnly`, `Secure`, and appropriately configured `SameSite` cookies.
- For account discovery, registration, and recovery, public status, body, schema, cooldown behavior, and provider-delivery details must not reveal whether an account exists.
- CORS with credentials requires explicit trusted origins; a wildcard origin is not valid for credentialed requests.
- If a secret is discovered, report only its location and type and recommend rotation without repeating the value.

## Testing placement

- Test deterministic domain rules with fast unit tests under `src/test`.
- Test application orchestration with mocked or fake outbound ports and Reactor Test where publishers are involved.
- Test REST mapping, validation, status, schema, and advice with controller-focused tests whose application port is mocked.
- Test adapter mapping, persistence constraints, concurrency, transactions, Redis behavior, and runtime wiring through adapter tests and `src/integrationTest`.
- Run unit tests with `./gradlew test`.
- Run Docker/Testcontainers integration tests separately with `./gradlew integrationTest`.
- Add security-focused equivalence and leakage tests when a public flow can reveal account or credential state.

## Incremental migration policy

- New code follows the target dependency contract.
- Existing code moves only in a focused, behavior-preserving task with explicit acceptance criteria.
- Characterize current behavior before changing a boundary.
- Do not combine a feature change with repository-wide package cleanup.
- Prefer a temporary mapper or adapter over allowing an HTTP, persistence, transaction, or provider type into the core.
- Remove a temporary bridge only after all callers use the replacement port and focused plus relevant full tests pass.
- Record valuable migration work in the workspace backlog; known gaps in this document are observations, not automatically assigned tasks.

## Known architectural gaps

These observations describe measured current-state gaps and do not expand target permissions:

- Ports under `domain.ports.inbound` and `domain.ports.outbound` expose Reactor types.
- `AuthPort` accepts `ServerWebExchange`.
- Security/JWT ports accept `ServerHttpResponse`.
- Domain services use Spring `@Service` annotations.
- `UserIdentity` exposes Spring Security `GrantedAuthority` types.
- `AuthenticationService` imports the infrastructure-specific `AppLogger`.
- `AuthenticationService` depends directly on Spring's `TransactionalOperator`.
- Authentication delivery effects include internal `subscribe()` calls.
- Application services are currently registered with Spring `@Service` and require incremental composition cleanup.
- Some authentication log messages include complete submitted credentials and require a focused security hardening task.

## Related documentation

- [Backend repository guide](../AGENTS.md)
- [Canonical backend guidance design](superpowers/specs/2026-08-05-backend-canonical-guidance-design.md)
- [Secure-registration backend plan](superpowers/plans/2026-08-03-secure-registration-backend.md) — approved future behavior, not current implementation
- [Secure-registration backend BDD](superpowers/specs/2026-08-03-secure-registration-backend-bdd.md) — approved future behavior, not current implementation
