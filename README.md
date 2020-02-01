# Kortex

[![Download](https://api.bintray.com/packages/uzzu/maven/kortex/images/download.svg)](https://bintray.com/uzzu/maven/kortex/_latestVersion)

Kotlin Coroutines helpers

## Requirements

Kotlin 1.3.+

## Setup

```kotlin
repositories {
    jcenter()
    // If you can't download package, please add.
    maven(url = "https://dl.bintray.com/uzzu/maven/")
}
```

```kotlin
// common project
implementation("co.uzzu.kortex:kortex-common:0.2.0")

// jvm, and Android project
implementation("co.uzzu.kortex:kortex-jvm:0.2.0")
```

## Features

### hot-launching

[See more examples](subprojects/core/src/jvmTest/kotlin/com/github/uzzu/kortex/HotLaunchJvmTest.kt)

### hot-invocation for suspending function

[See more examples](subprojects/core/src/jvmTest/kotlin/com/github/uzzu/kortex/HotInvocationJvmTest.kt)

## License

[Apache 2.0 license](LICENSE.txt)
