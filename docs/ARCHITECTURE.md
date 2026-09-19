# Backend Architecture

## Authority and status

This is the sole authority for backend package ownership, dependency direction, ports, adapters and structural migration. [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md) owns runtime flows and system contracts; [DEVELOPMENT.md](DEVELOPMENT.md) owns coding, verification and contributor practices. [AGENTS.md](../AGENTS.md) routes tasks to these guides.

Current-state descriptions are observations of the `development` checkout reviewed on 2026-09-13. Target rules govern new work and focused migrations; they do not assert that existing code already conforms or authorize a repository-wide refactor.

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

Authentication session state follows the same boundary: application security ports own
`SessionRecord`, session creation, activity checks and invalidation, while Redis adapters
own key namespaces and serialization. Profile caching is a separate application port and
must not be used as evidence that an authentication session is active.

## Incremental migration policy

- New code follows the target dependency contract.
- Existing code moves only in a focused, behavior-preserving task with explicit acceptance criteria.
- Characterize current behavior before changing a boundary.
- Do not combine a feature change with repository-wide package cleanup.
- Prefer a temporary mapper or adapter over allowing an HTTP, persistence, transaction, or provider type into the core.
- Remove a temporary bridge only after all callers use the replacement port and focused plus relevant full tests pass.
- Record proposed migration work in `.docs/` or an existing assigned work tracker; observed gaps are not automatically assigned tasks. Do not rely on a backlog route unless it exists in the checkout.

## Boundary debt

The following observed violations remain migration debt:

- Existing `domain.ports` expose Reactor and transport types; `AuthPort` accepts `ServerWebExchange`, and JWT ports expose `ServerHttpResponse`.
- Domain services use Spring `@Service`; `UserIdentity` exposes Spring Security authority types.
- Application services use Spring registration annotations; `AuthenticationService` imports infrastructure logging and Spring's `TransactionalOperator`.
- Several interface/composition helpers are located differently from the target tree; location alone does not establish conformance.

Runtime and security limitations are recorded in [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md#current-limitations-and-open-decisions). Coding corrections follow [DEVELOPMENT.md](DEVELOPMENT.md); domain and application behavior must be characterized before moving boundaries.

## Changing this contract

Update this guide when a deliberate task changes a boundary. Record rationale, affected consumers and migration evidence under `.docs/`, then reflect the accepted rule here. Feature-specific behavior belongs to its assigned specification and must not redefine dependency permissions. Missing historical plans are not a substitute for an explicit decision.
