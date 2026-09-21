# Application logging

Authentication and email sending use `LocationLogger`, a predefined logger that
sets the source class and submodule once. Console output in the default/dev
profile follows this format:

```text
2026-09-20 20:32:58.405 | INFO | auth | RecoveryLogAdapter | a7k2m9x4p | Verification email requested
```

The columns are timestamp, level, submodule, simple class name, static location
code and message. The `auth` submodule covers authentication services, recovery
and authentication exception handling; `email` covers the SMTP adapter.
There are no repeated status/method labels, event labels, request UUIDs or elapsed
time fields. The production profile keeps structured JSON, including `module`,
`location`, the source logger name and the plain message.

## Generate a location code

From `backend`:

```bash
./scripts/generate-log-location.sh
```

The Bash script prints one nine-character code using `a-z` and `0-9`, followed
by a newline. It uses OS randomness and maintains no files, counters or registry.
It accepts no arguments. Generate a code when adding a log statement and paste
it into the source; do not invoke the generator at application runtime.

Before assigning a generated value, search `src/main/java` to check that it is
unused. Each logging location has a distinct code, including branches that
report different outcomes. Keep an existing code stable when editing the message
or moving the same logical log point. Do not copy a location code to another log.
Random generation does not replace this uniqueness check.

## Use the predefined logger

```java
private static final LocationLogger LOGGER =
        LocationLogger.forClass(AuthenticationService.class, "auth");

// Example only: generate an unused location for each new statement.
LOGGER.info("a7k2m9x4p", "Verification email requested");
```

Use `info`, `warn`, `error` or `debug` with the location and a concise message.
Successful outcomes use `info`. The logger validates the nine-character code and
supplies the module automatically. SLF4J records the actual source class, and
`LogOriginConverter` renders its simple name in console output. Metadata travels
with each log event; it does not use mutable thread-local context.

Never include addresses, phone numbers, credentials, codes, message bodies or
raw provider exception messages in the log. Use a fixed safe failure category
when the cause matters. Authentication call sites use fixed messages; the email
adapter reports categories such as `AUTHENTICATION`, `DNS` and `TIMEOUT`.

## Find the source and interpret delivery

Search a displayed location code in the repository, for example:

```bash
rg -n 'a7k2m9x4p' src/main/java
```

The code identifies a source location, not an individual request. Repeated lines
from the same location share the same code across requests and restarts.

An email queued for sending is not yet accepted by SMTP. SMTP acceptance means
the send call returned successfully; it does not confirm inbox delivery. A
timeout or cancellation means the caller stopped waiting and the transport may
still complete. The SMTP adapter logs its eventual result even after cancellation.
See [Password recovery](PASSWORD_RECOVERY.md#diagnosing-email-delivery).

## Migration scope

Only authentication and email logging have migrated. `AppLogger` remains for
unmigrated modules. Their console layout and framework logs remain unchanged;
`LogOriginConverter` falls back to the previous abbreviated logger column when
an event has no module/location metadata. Migrate other modules only in an
explicitly scoped task.
