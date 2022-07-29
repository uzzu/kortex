[![Coding Style][ktlint-img]][ktlint] [![Released Version][maven-img]][maven]

# Kortex

Kotlin Coroutines helpers

## Requirements

Kotlin 1.6.+

## Setup

```kotlin
implementation("co.uzzu.kortex:kortex:0.11.0")
```

or resolve manually without using gradle metadata

```kotlin
// common project
implementation("co.uzzu.kortex:kortex-common:0.11.0")

// jvm project
implementation("co.uzzu.kortex:kortex-jvm:0.11.0")

// Android release project
implementation("co.uzzu.kortex:kortex-android:0.11.0")

// Android debug project
implementation("co.uzzu.kortex:kortex-android-debug:0.11.0")
```

## Features

### hot-launching

[See more examples](subprojects/core/src/jvmTest/kotlin/co/uzzu/kortex/HotLaunchJvmTest.kt)

### Pseudo hot-stream Flow with key cached

It works like `publish().refcount()` in RxJava, and caches the Flow instance by key string, so you can use a hot-stream considering the change of arguments.

[See more examples](https://github.com/uzzu/kortex/blob/main/subprojects/core/src/jvmTest/kotlin/co/uzzu/kortex/KeyedSingleSharedFlowJvmTest.kt)

### Deferred action after mutex unlocked

Deferred action after mutex unlocked, by using `MutexContext#defer` .
[See more examples](https://github.com/uzzu/kortex/blob/main/subprojects/core/src/commonTest/kotlin/co/uzzu/kortex/MutexContextDeferTest.kt)

### (Deprecated since 0.6.0, deleted since 0.9.0) hot-invocation for suspending function

Please migrate to Pseudo hot-stream Flow with key cached feature. It can be used to suspending function.

## License

[Apache 2.0 license](LICENSE.txt)

[ktlint-img]: https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg
[ktlint]: https://ktlint.github.io/
[maven-img]: https://img.shields.io/maven-central/v/co.uzzu.kortex/kortex.svg?maxAge=2000
[maven]: https://search.maven.org/search?q=g:co.uzzu.kortex
