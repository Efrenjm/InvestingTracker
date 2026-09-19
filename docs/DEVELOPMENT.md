# Backend Development Guide

## Authority

This guide owns implementation practices, verification commands, contributor safety and documentation workflow. [ARCHITECTURE.md](ARCHITECTURE.md) owns dependency boundaries; [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md) owns runtime behavior and system security contracts. Read the applicable feature specification for behavior changes.

## Before changing implementation

- Run `git -C backend status --short --branch` from the workspace root and inspect nearby code plus `build.gradle`.
- Identify the owning capability, use case, inbound port and outbound adapters using the architecture guide.
- Preserve public behavior unless the task explicitly changes it. Keep unrelated cleanup and migrations separate.
- Preserve existing staged, unstaged, untracked and ignored files. Git authorization rules are in `AGENTS.md`.
- Documentation is written in English. Use only synthetic examples.

## Supported commands

Run Gradle commands inside `backend`, using its wrapper and the Java toolchain declared in `build.gradle` (currently Java 21).

| Purpose | Command |
|---|---|
| Local application | `./gradlew bootRun` |
| Unit tests | `./gradlew test` |
| Focused unit test | `./gradlew test --tests "org.efrenjm.investingtracker.application.service.authentication.AuthenticationServiceTest"` |
| Integration tests | `./gradlew integrationTest` |
| Focused integration test | `./gradlew integrationTest --tests "org.efrenjm.investingtracker.interfaces.rest.controller.authentication.AuthenticationControllerIT"` |
| Build | `./gradlew build` |

Integration tests require Docker/Testcontainers. The custom `integrationTest` task has `shouldRunAfter test`, which orders tasks when both are selected; it does not make `build` or `check` execute integration tests. Run them explicitly when needed. The task currently disables Testcontainers Ryuk; do not assume automatic container cleanup from this configuration.

Authentication integration tests also require the Redis Testcontainer. Redis is used for revocable JWT sessions and the independent profile cache; tests should isolate both namespaces between cases and must use synthetic credentials and session data.

There is no dedicated lint task or architecture-enforcement task in `build.gradle`. Do not invent commands or install tooling unless the assigned task authorizes it. This documentation consolidation adds no commit hook or remote CI enforcement.

## Reactive implementation

- Preserve non-blocking behavior across WebFlux request paths.
- Compose side effects into the returned `Mono` or `Flux`.
- Do not call `block()` or initiate internal `subscribe()` calls in controllers, use cases, or domain logic.
- Keep pure domain calculations synchronous and deterministic.
- Isolate unavoidable blocking provider APIs behind an adapter and an explicit scheduling or asynchronous boundary.
- Do not discard reactive errors or detach work whose result affects correctness.

## Mapping and validation

- Follow the architecture guide for ownership of HTTP DTOs, commands, results and persistence entities.
- Validate syntax and transport shape at the REST boundary; enforce business invariants in application/domain behavior.
- Map failures at interface advice according to the system contract. Do not expose internal exceptions or provider details as an accidental API.
- Preserve concurrency guarantees across adapter calls and test partial failures; reactive composition alone does not establish atomicity.

## Testing and verification

- Add or update tests for behavior changes and bug fixes. Reproduce a defect with a failing test first when practical.
- Run focused checks before the relevant full checks.
- Use unit tests for deterministic domain rules and application orchestration.
- Use controller tests for request/response mapping with mocked inbound ports.
- Use integration tests for real adapter wiring, persistence constraints, transactions, and Testcontainers-backed behavior.
- Match verification effort to risk; documentation-only work does not require Gradle execution unless source or build configuration also changes.
- Do not claim completion without fresh command output or direct file-level evidence.

- `src/test` holds unit tests; `src/integrationTest` holds tests requiring real adapter/runtime wiring.
- Use Reactor Test to verify publisher completion, errors and effect ordering where relevant.
- Tests must never call real mail, SMS, authentication or external data services. Use fake ports or controlled infrastructure.
- Security-sensitive public flows need tests for response equivalence and leakage, as defined in the system guide.
- Documentation-only work requires link checks, source-reference checks and diff review, not Gradle execution.
- Existing violations remain explicit debt; this guide does not establish a suppression baseline or a staged-file enforcement mechanism. Any future automated rollout requires a scoped task.

## Handling sensitive information

- Never read, expose, copy, print, commit or log private keys, certificates, API tokens, passwords, password hashes, OTP values, code digests, authentication headers, session cookies, environment secrets or provider/database credentials.
- Inspect configuration names rather than values. Keep example data synthetic and redact sensitive values from outputs.
- Never log raw email addresses or other personal/financial data; use approved redaction or non-reversible correlation identifiers.
- If a secret is discovered, stop propagating it, identify only its location and type, and recommend rotation without repeating it.
- Review the security and maintenance implications of dependency changes before installation.

Runtime cookie, authorization, privacy and public-response requirements belong to [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md#system-contracts-for-new-work).

## Documentation workflow

| Location | Content and authority |
|---|---|
| `AGENTS.md` | Brief task routing and essential workspace safeguards |
| `docs/ARCHITECTURE.md` | Structural contract |
| `docs/SYSTEM_DESIGN.md` | Observed runtime flows and system contracts |
| `docs/DEVELOPMENT.md` | Implementation and verification conventions |
| `.docs/plans/`, `.docs/specs/`, `.docs/reviews/` | Plans, feature specs, review evidence and Superpowers artifacts |

Keep each rule in its owning guide and link to it elsewhere. An approved feature spec supplies behavior for its task, but does not silently redefine shared policy. Promote accepted durable decisions to the owning maintained guide. Label current implementation, target requirements and unresolved choices explicitly.

Do not link to unavailable plans or claim their contents are implemented. If a task needs a missing specification, identify the missing input and clarify it with the user. Do not manufacture historical approval. See the [reference audit](../.docs/reviews/2026-09-13-documentation-audit.md) for removed broken routes.

If an existing work tracker marks a task in progress, respect its ownership lock and documented transition rules. No workspace backlog is present in this checkout; do not invent its files or state transitions.

## Coordination and completion

- Give any authorized subagent a bounded objective, allowed files, acceptance criteria and verification commands; avoid overlapping ownership.
- Handoffs report changed files, actual checks, assumptions and remaining risks.
- Review the final diff and local links; preserve unrelated work and the Git index.
- Report fresh verification evidence and limitations. Never claim an unexecuted check passed.
