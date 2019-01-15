# Kortex

common | jvm
--- | --- 
[ ![Download](https://api.bintray.com/packages/uzzu/maven/kortex-common/images/download.svg) ](https://bintray.com/uzzu/maven/kortex-common/_latestVersion) | [ ![Download](https://api.bintray.com/packages/uzzu/maven/kortex-jvm/images/download.svg) ](https://bintray.com/uzzu/maven/kortex-jvm/_latestVersion)

Kotlin Coroutines helpers

## Requirements

Kotlin 1.3.+

## Setup

```kotlin
repositories {
    jcenter()
    // If you can't download package, please add.
    maven { url = "https://dl.bintray.com/uzzu/maven/" }
}
```

```kotlin
// common project
implementation("com.github.uzzu.kortex:kortex-common:0.1.0")

// jvm, and Android project
implementation("com.github.uzzu.kortex:kortex-jvm:0.1.0")
```

## Features

### hot-launching

[See more examples](subprojects/core-test/src/test/kotlin/com/github/uzzu/kortex/HotLaunchJvmTest.kt)


### hot-invocatoin of suspend function

[See more examples](subprojects/core-test/src/test/kotlin/com/github/uzzu/kortex/HotInvocationJvmTest.kt)

## License

[Apache 2.0 license](LICENSE.txt)


