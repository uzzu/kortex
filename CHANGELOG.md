# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased] - xxxx-xx-xx

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [0.9.0] - 2022-02-22

### Changed
- Use Kotlin 1.6.10
- Use kotlinx.coroutines 1.6.0

### Removed
- HotInvocation feature was removed. Please migrate to KeyedSingleSharedFlowContext

## [0.8.1] - 2021-11-12

### Fixed
- Fix java compatibility declaration

### Deprecated
HotInvocation feature be removed in version 0.9.0. Please migrate to KeyedSingleSharedFlowContext

## [0.8.0] - 2021-11-06

### Changed
- Use Kotlin 1.5.30
- Use Android Gradle Plugin 7.0.3, and compileSdkVersion is moved to 31

### Fixed
- Fix build time warnings for using `@OptIn` annotation
- Resolve ktlint violation

### Deprecated
- HotInvocation feature be removed in version 0.9.0. Please migrate to KeyedSingleSharedFlowContext

## [0.7.0] - 2021-06-25

### Changed
- Use Kotlin 1.5.20

### Deprecated
- HotInvocation feature be removed in version 0.9.0. Please migrate to KeyedSingleSharedFlowContext

## [0.6.0] - 2021-06-21

### Changed
- Use Kotlin 1.5.10
- Add a new hot-invocation implementation `KeyedSingleSharedFlowContext` , by using kotlinx.coroutines.Flow

### Deprecated
- HotInvocation feature be removed in version 0.9.0. Please migrate to KeyedSingleSharedFlowContext

## [0.5.0] - 2021-05-24

### Changed
- Use Kotlin 1.5.0
  - BroadcastChannel is deprecated, so we have to replace with SharedFlow or any others.

## [0.4.1] - 2020-10-09

### Changed
- Use Kotlin 1.4.10

## [0.4.0] - 2020-09-06

### Changed
- The maven artifacts of this library are published to Maven Central Repository since this version.
  - Previously they were published to jcenter.
- Use Kotlin 1.4.0
- Use kotlinx-coroutines 1.3.9

## [0.3.1] - 2020-08-11

### Changed
- Add Android library target to support resolution of gradle module metadata.

## [0.3.0] - 2020-08-11

### Changed
- Use Kotlin 1.3.72
- Use kotlinx-coroutines 1.3.8

## [0.2.0] - 2020-02-01

### Changed
- Kotlin is updated to 1.3.60
- package is renamed from `com.github.uzzu.kortex` to `co.uzzu.kortex`
- [internal] Project structure is updated to after 1.3 MPP structure

## [0.1.2] - 2019-05-27

### Changed
- Kotlin is updated to 1.3.31
- [internal] Some test libraries and gradle plugins are updated to latest.

## [0.1.1] - 2019-01-26

### Changed
- Kotlin is updated to 1.3.20
- [internal] Some test libraries are updated to latest.

## [0.1.0] - 2019-01-04

### Added
- `CoroutineScope#launchHot`
- `CoroutineScope#withHot`
