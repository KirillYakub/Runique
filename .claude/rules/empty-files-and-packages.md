# Empty files, packages and modules

- If a change leaves a class or file empty (no declarations left, or only `package`/import lines), do not delete it yourself. Ask the user whether it can be deleted.
- If deleting a file leaves its package (directory) or Gradle module with no source files, do not delete it yourself. Ask the user whether the package or module can be deleted.
- When the user approves deleting a module, also remove its `include(...)` entry in `settings.gradle.kts` and every `projects.<module>` dependency that points to it.
