# Backend Repository Guide

## Scope and precedence

- These instructions apply to all work inside this repository.
- `AGENTS.md` is the canonical backend guidance source. Tool-specific files may import or route to it but must not duplicate it.
- A more specific `AGENTS.md` closer to a changed file overrides this file only within that subtree.
- Work from the workspace root when coordinating with the frontend, but run backend Git and Gradle commands against `backend` explicitly.
- Treat existing staged, unstaged, untracked, and ignored files as user-owned unless the current task explicitly includes them.
- Write repository documentation in English.

## Start every task

1. Confirm that the requested location is `backend` or `cross-repository`.
2. Run `git -C backend status --short --branch` from the workspace root.
3. Read only the routed documents relevant to the task.
4. Inspect nearby implementation and `build.gradle` before proposing structure or commands.
5. Identify the owning domain, application use case, inbound port, outbound capabilities, and adapters.
6. Keep the change bounded; do not mix unrelated cleanup or architectural migration into the task.

## Documentation routing

| Work type | Read before editing |
| --- | --- |
| Architecture, package boundaries, ports, adapters, reactive composition, or structural refactoring | `docs/ARCHITECTURE.md` |
| Secure user registration behavior or implementation | `docs/superpowers/plans/2026-08-03-secure-registration-backend.md` and `docs/superpowers/specs/2026-08-03-secure-registration-backend-bdd.md`; these describe an approved future design, not current behavior |
| Other feature behavior | The feature-local or task-specific specification named by the task |
| Planned guidance that does not exist yet | Read workspace `backlog/README.md`, then only the assigned `backlog/<state>/BL-NNN.md`; do not invent missing policy |

Update this table only after a new guide exists and has been reviewed.

## Supported commands

Run Gradle commands with `backend` as the working directory.

- Run locally: `./gradlew bootRun`
- Unit tests: `./gradlew test`
- Focused unit test: `./gradlew test --tests "org.efrenjm.investingtracker.application.service.authentication.AuthenticationServiceTest"`
- Integration tests: `./gradlew integrationTest`
- Focused integration test: `./gradlew integrationTest --tests "org.efrenjm.investingtracker.interfaces.rest.controller.authentication.AuthenticationControllerIT"`
- Build: `./gradlew build`

Integration tests require Docker/Testcontainers. There is currently no dedicated lint task in `build.gradle`; do not invent one or add tooling unless the task explicitly authorizes it.

## Architecture boundaries

- `domain` owns aggregates, value objects, deterministic business rules, domain services, and domain exceptions.
- `application` owns use cases, framework-neutral commands/results, inbound ports, and outbound capability ports.
- `interfaces` owns inbound adapters: REST controllers, HTTP DTOs, boundary validation, OpenAPI, filters, argument resolution, and exception-to-HTTP mapping.
- `infrastructure` owns outbound adapters and composition: MongoDB, Redis, JWT/crypto, mail, Twilio, framework configuration, and technical observability.
- Dependencies point inward: `interfaces -> application -> domain`, while `infrastructure` implements application/domain ports.
- Application and domain code must not depend on concrete interfaces or infrastructure adapters.
- Existing violations are migration debt; they do not authorize new coupling or a repository-wide refactor.

## Ports and adapters

- Name inbound ports after a specific use case rather than a generic service.
- Controllers invoke inbound ports and must not query repositories or select concrete adapters.
- Application use cases coordinate domain rules and outbound ports.
- Keep controller DTOs, `ServerWebExchange`, `ServerHttpResponse`, HTTP status types, Spring transaction types, Mongo entities, Redis clients, and provider SDK types out of port commands and results.
- Keep persistence, messaging, security, time, randomness, transactions, and external delivery behind explicit outbound ports when application policy depends on them.
- Map transport and persistence representations at adapter boundaries; do not serialize persistence entities or domain aggregates as accidental public contracts.

## Reactive behavior

- Preserve non-blocking behavior across WebFlux request paths.
- Compose side effects into the returned `Mono` or `Flux`.
- Do not call `block()` or initiate internal `subscribe()` calls in controllers, use cases, or domain logic.
- Keep pure domain calculations synchronous and deterministic.
- Isolate unavoidable blocking provider APIs behind an adapter and an explicit scheduling or asynchronous boundary.
- Do not discard reactive errors or detach work whose result affects correctness.

## HTTP, validation, and errors

- Validate syntax and transport shape at the REST boundary; enforce business invariants in application/domain code.
- Translate domain and application exceptions into public responses in interface advice.
- Keep HTTP status and response-schema decisions out of domain and application policy.
- Coordinate public API contract changes with the frontend and the applicable feature specification.
- Preserve indistinguishable responses where exposing account state would enable user enumeration.

## Persistence and external integrations

- Keep Mongo documents, Spring Data repositories, Redis operations, JWT implementation, email, and Twilio code in infrastructure adapters.
- Map persistence entities to and from core objects explicitly.
- Preserve reactive composition and concurrency guarantees across adapter calls.
- Enforce correctness through application checks and database constraints where both are required; do not rely on eventual cleanup as authorization.
- Tests and stories must never call real mail, SMS, authentication, or data services.

## Testing and verification

- Add or update tests for behavior changes and bug fixes. Reproduce a defect with a failing test first when practical.
- Run focused checks before the relevant full checks.
- Use unit tests for deterministic domain rules and application orchestration.
- Use controller tests for request/response mapping with mocked inbound ports.
- Use integration tests for real adapter wiring, persistence constraints, transactions, and Testcontainers-backed behavior.
- Match verification effort to risk; documentation-only work does not require Gradle execution unless source or build configuration also changes.
- Do not claim completion without fresh command output or direct file-level evidence.

## Security and privacy

- Never read, expose, copy, print, commit, or log private keys, certificates, API tokens, passwords, password hashes, OTP values, code digests, authentication headers, session cookies, environment secrets, or MongoDB, Redis, mail, and Twilio credentials.
- Inspect configuration names rather than values and redact sensitive values from output.
- Never log raw email addresses or other personal or financial data; use approved redaction or non-reversible correlation identifiers.
- Use synthetic data in documentation, tests, fixtures, screenshots, and examples.
- Keep secrets and provider-specific authentication inside infrastructure adapters.
- Keep authentication credentials in backend-managed `HttpOnly`, `Secure`, and appropriately configured `SameSite` cookies.
- Do not expose account existence through response status, body, schema, cooldown behavior, or provider-delivery details.
- Do not place secrets, tokens, passwords, OTPs, or sensitive personal data in URLs.
- If a secret is discovered, stop propagating it, report only its location and type, and recommend rotation without repeating its value.
- Justify dependency changes and review their security and maintenance implications before installation.

## Documentation and coordination

- Update `docs/ARCHITECTURE.md` when a task changes a documented boundary or target convention.
- Keep feature-specific behavior in feature or task specifications instead of expanding this file.
- Give a subagent a bounded objective, allowed files, acceptance criteria, and verification commands.
- Do not assign overlapping files to concurrent agents.
- Require every handoff to report changed files, checks run, assumptions, and remaining risks.
- Treat an `in-progress` backlog task as an ownership lock and follow workspace `backlog/README.md` for transitions.

## Completion and Git safety

- Review the final diff and preserve unrelated changes.
- Report files changed, verification evidence, assumptions, and remaining risks.
- Never stage, unstage, commit, amend, reset, restore, push, change branches, or modify remotes without explicit user authorization.
