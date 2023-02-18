[![Build](https://github.com/LSPosed/LSParanoid/actions/workflows/build.yml/badge.svg)](https://github.com/LSPosed/LSParanoid/actions/workflows/build.yml)

LSParanoid
========

String obfuscator for Android applications.

Usage
-----
In order to make Paranoid work with your project you have to apply the Paranoid Gradle plugin
to the project.

```kotlin
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.lsposed.lsparanoid:gradle-plugin:0.4.2")
        // classpath("com.android.tools.build:gradle")
    }
}

plugins {
    id("org.lsposed.lsparanoid")
    id("com.android.application")
}
```

Now you can just annotate classes with strings that need to be obfuscated with `@Obfuscate`.
After you project compiles every string in annotated classes will be obfuscated.
Note that this plugin only applies with Android Gradle Plugin. You must either applies
`com.android.application` at the mean time.

Configuration
-------------
Paranoid plugin can be configured using `lsparanoid` extension object:
```kotlin
lsparanoid {
  // ...
}

```

The extension object contains the following properties:
- `enabled` — `boolean`. Allows to disable obfuscation for the project. Default value is `true`.
- `seed` - `Integer`. A seed that can be used to make obfuscation stable across builds. Default value is `null`, which means that the seed
  is computed from input files on each build.
- `global` - `boolean`. If `true`, the obfuscation will be applied to all classes, not only annotated ones. Default value is `false`.
- `variantFilter` - `(Variant) -> boolean`. Allows to filter out variants that should be obfuscated. Default value always returns `true`.

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
