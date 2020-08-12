# Kortex

[![Download](https://api.bintray.com/packages/uzzu/maven/kortex/images/download.svg)](https://bintray.com/uzzu/maven/kortex/_latestVersion)

Kotlin Coroutines helpers

## Requirements

Kotlin 1.3.+

## Setup

```kotlin
repositories {
    jcenter()
    // If you can't download package, please try to add.
    maven(url = "https://dl.bintray.com/uzzu/maven/")
}
```

```kotlin
implementation("co.uzzu.kortex:kortex:0.3.1")
```

or resolve manually without using gradle metadata

```kotlin
// common project
implementation("co.uzzu.kortex:kortex-common:0.3.1")

// jvm project
implementation("co.uzzu.kortex:kortex-jvm:0.3.1")

// Android release project
implementation("co.uzzu.kortex:kortex-android:0.3.1")

// Android debug project
implementation("co.uzzu.kortex:kortex-android-debug:0.3.1")
```

## Features

### hot-launching

[See more examples](subprojects/core/src/jvmTest/kotlin/co/uzzu/kortex/HotLaunchJvmTest.kt)

### hot-invocation for suspending function

[See more examples](subprojects/core/src/jvmTest/kotlin/co/uzzu/kortex/HotInvocationJvmTest.kt)

## License

[Apache 2.0 license](LICENSE.txt)
