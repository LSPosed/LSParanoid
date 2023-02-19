This directory lists the samples for LSParanoid.

- library: This is a normal library project. It contains a single class `Library` with some strings.
- library-may-obfuscate: This is a library project that adds dependency `org.lsposed.lspanoid:core`. It contains two classes i) `LibraryMayObfuscate` which is annotated with `Obfuscate`, and ii) `LibraryMayNotObfuscate` which is not annotated with `Obfuscate`. The class `LibraryMayObfuscate` will be obfuscated.
- library-obfuscate: This is a library project that uses LSParanoid. It contains a single class `LibraryObfuscate` without annotation. It turns on `global` obfuscation, and its aar should be obfuscated.
- application: This is an application project that uses LSParanoid. It contains a single class `Application` annotated with `Obfuscate`. It also depends on the aforementioned three libraries. It only obfuscates the release build. It turns on `includeDependencies`. The resulting release apk should have `Application`, `LibraryObfuscate`, `LibraryMayObfuscate` obfuscated, and `Library`, `LibraryMayNotObfuscate` not obfuscated.
- application-global-obfuscate: This is an application that uses the same configuration as `application`, but turns on `global` obfuscation. The resulting release apk should have all classes obfuscated.
