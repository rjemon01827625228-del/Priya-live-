const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      username: "Alice",
      gender: "Female",
      country: "Bangladesh",
      countryFlag: "🇧🇩",
      bio: "Hello Priya Live!",
      coins: 100,
      diamonds: 50,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's profile", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      username: "Bob",
      gender: "Male",
      country: "Bangladesh",
      countryFlag: "🇧🇩",
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: rejects ghost fields on profile update (Shadow Update)", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      username: "Alice",
      gender: "Female",
      country: "Bangladesh",
      countryFlag: "🇧🇩",
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      username: "AliceUpdated",
      isVerified: true,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can create, query, and update own posts", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  const postRef = aliceDb.collection("users").doc(ALICE_UID).collection("posts").doc("post_1");

  await assertSucceeds(
    postRef.set({
      postId: "post_1",
      userId: ALICE_UID,
      authorName: "Alice",
      content: "Going live tonight!",
      likesCount: 0,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("posts")
      .where("userId", "==", ALICE_UID)
      .get()
  );

  await assertSucceeds(
    postRef.update({
      likesCount: 1,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: cannot read or write another user's posts", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("posts")
      .doc("bob_post")
      .set({
        postId: "bob_post",
        userId: BOB_UID,
        authorName: "Bob",
        content: "Bob's post",
        likesCount: 0,
        createdAt: now,
        updatedAt: now,
      });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("posts").doc("bob_post").get()
  );
});

test("PartyRoom: owner can create and update seatCount (8, 12, 16) and connected users can read", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date();
  const roomRef = aliceDb.collection("party_rooms").doc("party_room_alice_123");

  await assertSucceeds(
    roomRef.set({
      roomId: "party_room_alice_123",
      hostUid: ALICE_UID,
      roomTitle: "Alice's Party Board",
      seatCount: 8,
      isLocked: false,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    roomRef.update({
      seatCount: 16,
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(
    roomRef.update({
      seatCount: 12,
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(
    bobDb.collection("party_rooms").doc("party_room_alice_123").get()
  );

  await assertFails(
    bobDb.collection("party_rooms").doc("party_room_alice_123").update({
      seatCount: 8,
      updatedAt: new Date(),
    })
  );
});
