# Class structure

- Do not use nested or `inner` classes.
- Exception: members of a `sealed interface`/`sealed class` (for example MVI `Action`/`Event` subtypes) may stay nested inside the sealed type.
- Put each data class, helper or other supporting class in its own top-level file, not inside another class.
- This applies to tests too: fakes, stubs and test fixtures (for example `FakeRunRepository`) go in their own files, grouped in a separate package such as `.../fakes` or `.../testutil` in the test source set.
