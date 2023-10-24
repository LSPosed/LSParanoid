[![Build](https://github.com/LSPosed/LSParanoid/actions/workflows/build.yml/badge.svg)](https://github.com/LSPosed/LSParanoid/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.lsposed.lsparanoid/org.lsposed.lsparanoid.gradle.plugin)](https://central.sonatype.com/namespace/org.lsposed.lsparanoid)

LSParanoid
========

String obfuscator for Android applications. LSParanoid supports [configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html).

Usage
-----
In order to make LSParanoid work with your project you have to apply the LSParanoid Gradle plugin
to the project.

The following is an example `settings.gradle.kts` to apply LSParanoid.
```kotlin
pluginManagement {
  repositories {
    mavenCentral()
  }
  plugins {
    id("org.lsposed.lsparanoid") version "......"
  }
}
```

Now you can just annotate classes with strings that need to be obfuscated with `@Obfuscate`.
After you project compiles every string in annotated classes will be obfuscated.

**Note that you should use at least Java 17 to launch the gradle daemon for this plugin (this is also required by AGP 8+).**
The project that uses this plugin on the other hand does not necessarily to target Java 17.

Configuration
-------------
Paranoid plugin can be configured using `lsparanoid` extension object.

The following is an example `build.gradle.kts` that configures `lsparanoid` extension object with default values.
```kotlin
plugins {
    id("org.lsposed.lsparanoid")
    // other plugins...
}

lsparanoid {
  seed = null
  classFilter = null
  includeDependencies = false
  variantFilter = { true }
}

```

The extension object contains the following properties:
- `seed` - `Integer`. A seed that can be used to make obfuscation stable across builds. Default value is `null`. Set it to non-null can make the obfuscation task cacheable.
- `classFilter` - `(String) -> boolean`. If set, it allows to filter out classes that should be obfuscated. Use `classFilter = { true }` to turn on global obfuscation i.e. obfuscate all classes, not only annotated ones. Or apply a filter like `classFilter = { it.startsWith("com.example.") }` or `classFilter = { it != "module-info" }`. Default value is `null`.
- `includeDependencies` - `boolean`. If `true`, the obfuscation will be applied to all dependencies. Default value is `false`.
- `variantFilter` - `(Variant) -> boolean`. Allows to filter out variants that should be obfuscated. Default value always returns `true`. Note that you can set `seed`, `classFilter` and `includeDependencies` dynamically for each variant in `variantFilter`. For example
    ```kotlin
    variantFilter = { variant -> 
        // enable global obfuscate for globalObfuscate flavor release build
        if (variant.flavorName == "globalObfuscate" && variant.buildType == "release") {
            seed = 114514
            classFilter = { true }
            true
        } else if (variant.buildType == "release") {
            seed = 1919810
            classFilter = null
            true
        } else {
            false
        }
    }
    ```

How it works
------------
Let's say you have an `Activity` that contains some string you want to be obfuscated.

```java
@Obfuscate
public class MainActivity extends AppCompatActivity {
  private static final String QUESTION = "Q: %s";
  private static final String ANSWER = "A: %s";

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    final TextView questionTextView = (TextView) findViewById(R.id.questionTextView);
    questionTextView.setText(String.format(QUESTION, "Does it work?"));

    final TextView answerTextView = (TextView) findViewById(R.id.answerTextView);
    answerTextView.setText(String.format(ANSWER, "Sure it does!"));
  }
}
```

The class contains both string constants (`QUESTION` and `ANSWER`) and string literals.
After compilation this class will be transformed to something like this.

```java

@Obfuscate
public class MainActivity extends AppCompatActivity {
  private static final String QUESTION = Deobfuscator.getString(4);
  private static final String ANSWER = Deobfuscator.getString(5);

  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    final TextView questionTextView = (TextView) findViewById(R.id.questionTextView);
    questionTextView.setText(String.format(Deobfuscator.getString(0), Deobfuscator.getString(1)));

    final TextView answerTextView = (TextView) findViewById(R.id.answerTextView);
    answerTextView.setText(String.format(Deobfuscator.getString(2), Deobfuscator.getString(3)));
  }
}

```

Credit
------
LSParanoid was forked from https://github.com/MichaelRocks/paranoid. Credits to its original author Michael Rozumyanskiy.

License
=======
    Copyright 2021 Michael Rozumyanskiy
    Copyright 2023 LSPosed

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
