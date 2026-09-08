# TechCertHub testing

## Frontend smoke tests

```bash
node tests/admin-smoke.cjs
node tests/learn-smoke.cjs
node tests/quiz-smoke.cjs
node tests/community-smoke.cjs
```

## Backend tests

Run from the project root:

```bash
mvn clean test
```

The suite includes application-context, security, payment, PDF/download, and local profanity-filter tests.

## Community checks

The community feature is intentionally local/offline for moderation: no paid moderation API is used. The server normalizes common leetspeak and separators before checking the local blocklist, and authenticated post/answer creation is rate-limited.
