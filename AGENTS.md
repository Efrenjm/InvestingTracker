# Backend agent instructions

Applies throughout `backend`; a closer `AGENTS.md` overrides it within its subtree. Tool-specific files route here without duplicating policy.

## Workflow

- Start with `git -C backend status --short --branch` from the workspace root. Run backend Gradle commands inside `backend`; inspect `build.gradle` and nearby code before proposing changes.
- Read the applicable guides below. Preserve public behavior unless the task changes it; avoid unrelated cleanup or migration.
- Treat existing staged, unstaged, untracked and ignored files as user-owned. Never stage, unstage, commit, amend, reset, restore, push, change branches or modify remotes without explicit authorization.
- Write repository documentation in English.
- Before delivery, run the development guide's formatting and lint checks (`./gradlew formatCheck lint`). Use `./gradlew check` and explicit integration tests when behavior, build wiring or persistence changes warrant broader verification.

## Read by task

| Task | Required source |
|---|---|
| Code, configuration, dependencies, tests, security or delegation | [Development](docs/DEVELOPMENT.md) |
| Package ownership, dependency direction, ports, adapters or refactoring | [Architecture](docs/ARCHITECTURE.md) |
| Runtime flows, API behavior, authentication, storage, consistency or integrations | [System design](docs/SYSTEM_DESIGN.md) |
| Feature behavior | Existing assigned feature specification; clarify missing inputs rather than inventing policy |

## Always preserve

- Never read or expose secrets, credentials, private keys, certificates, session data or real personal/financial data. Inspect configuration names, not secret values; use synthetic examples and follow the development guide's handling rules.
- Preserve inward dependency direction and non-blocking request behavior. Existing violations are debt, not precedent.
- Add or update tests for behavior changes. Run focused checks and relevant broader checks; integration tests require Docker and an explicit Gradle task.
- Review the diff and report fresh verification evidence and limitations. Documentation-only work needs link/source checks, not application builds.

## Documentation placement

- Maintained guides belong in `docs/`; each guide owns one concern and links to the others.
- Plans, specs, draft reviews and Superpowers artifacts belong in `.docs/`. Historical artifacts do not override maintained policy.
