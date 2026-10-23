# Testing

- Tests are not mandatory. Write them when asked.
- Stack: JUnit4 (`org.junit.Test`, `org.junit.Assert.*`) + `kotlinx.coroutines.test.runTest`. Do not add mocking libraries; use hand-written fakes.
- Fakes:
  - Each fake goes in its own file in a `fakes` package of the test source set, named `Fake<Interface>` (`FakeUserDao`, `FakeSessionStorage`).
  - A fake implements the real interface and exposes its stored data for assertions.
- Name tests as backticked sentences describing behavior: `` `login with wrong password returns invalid credentials` ``.
- Test classes are named `<ClassUnderTest>Test` and live in the same package as the class under test.
- Leave the generated `ExampleUnitTest`/`ExampleInstrumentedTest` files alone.
