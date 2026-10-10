# Security Specification (Phase 0 TDD & Red Team Audit)

## 1. Data Invariants
1. **Strict Multi-User Ownership Isolation**: Every document in `/users/{userId}` and `/users/{userId}/posts/{postId}` belongs exclusively to `userId` and may only be read, queried, created, updated, or deleted when `request.auth != null && request.auth.uid == userId`.
2. **Path & Identity Binding**: Document fields `userId` and `postId` must match the path variables `{userId}` and `{postId}` as well as `request.auth.uid`, and all path IDs must match `^[a-zA-Z0-9_\-]+$` with length `1..128`.
3. **Strict Key Allowlisting & Anti-Update-Gap**: Every `create` and `update` operation validates the complete post-write state via `isValidUserProfile` or `isValidUserPost` and restricts keys via `hasAll`/`hasOnly` (on create) and `affectedKeys().hasOnly(...)` (on update).
4. **Temporal & Identity Immutability**: `userId`, `postId`, and `createdAt` are immutable across updates (`incoming().field == existing().field`), and timestamps must be server timestamps (`<= request.time`).

## 2. The "Dirty Dozen" Adversarial Payloads
1. **Unauthenticated Profile Read**: `unauthDb.collection("users").doc("alice_123").get()` -> `PERMISSION_DENIED`
2. **Cross-User Profile Read (PII Isolation)**: `bobDb.collection("users").doc("alice_123").get()` -> `PERMISSION_DENIED`
3. **Identity Spoofing on Profile Create**: `aliceDb.collection("users").doc("alice_123").set({ userId: "bob_456", ... })` -> `PERMISSION_DENIED`
4. **Shadow/Ghost Field Injection on Profile Create**: `aliceDb.collection("users").doc("alice_123").set({ ..., isAdmin: true })` -> `PERMISSION_DENIED`
5. **Shadow/Ghost Field Injection on Profile Update**: `aliceDb.collection("users").doc("alice_123").update({ isVerified: true })` -> `PERMISSION_DENIED`
6. **Value Poisoning on Profile Update (Oversized Username)**: `aliceDb.collection("users").doc("alice_123").update({ username: "x".repeat(200) })` -> `PERMISSION_DENIED`
7. **Invalid Enum Value on Gender**: `aliceDb.collection("users").doc("alice_123").update({ gender: "InvalidGender" })` -> `PERMISSION_DENIED`
8. **Immutable Field Mutation (`createdAt` or `userId`)**: `aliceDb.collection("users").doc("alice_123").update({ createdAt: newTimestamp })` -> `PERMISSION_DENIED`
9. **Future Timestamp Spoofing**: `aliceDb.collection("users").doc("alice_123").set({ ..., createdAt: futureTimestamp })` -> `PERMISSION_DENIED`
10. **Unauthenticated Post Read/List**: `unauthDb.collection("users").doc("alice_123").collection("posts").get()` -> `PERMISSION_DENIED`
11. **Cross-User Post Write**: `bobDb.collection("users").doc("alice_123").collection("posts").doc("post_1").set(...)` -> `PERMISSION_DENIED`
12. **Negative Likes Count Poisoning on Post**: `aliceDb.collection("users").doc("alice_123").collection("posts").doc("post_1").update({ likesCount: -5 })` -> `PERMISSION_DENIED`

## 3. Red Team Audit & Conflict Report
- **Shadow Update Test**: PASS (`hasOnly` and `affectedKeys().hasOnly` reject ghost fields).
- **PII Blanket Test**: PASS (`allow get, list` on `/users/{userId}` strictly requires `isOwner(userId)`).
- **Query Trust Test**: PASS (`allow list` requires `isOwner(userId) && existing().userId == request.auth.uid`).
- **Value Poisoning Test**: PASS (`isValidUserProfile` and `isValidUserPost` wrap `allow update`).
