# Kraken

![Java Version](https://img.shields.io/badge/Temurin-17-green?style=flat-square&logo=eclipse-adoptium)
![Kotlin Version](https://img.shields.io/badge/Kotlin-2.1.0-green?style=flat-square&logo=kotlin)
![Status](https://img.shields.io/badge/Status-Beta-yellowgreen?style=flat-square)

[![Gradle](https://img.shields.io/badge/Gradle-8.12.0-informational?style=flat-square&logo=gradle)](https://github.com/gradle/gradle)
[![Ktlint](https://img.shields.io/badge/Ktlint-1.5.0-informational?style=flat-square)](https://github.com/pinterest/ktlint)

[![Github - Version](https://img.shields.io/github/v/tag/Buried-In-Code/Kraken?logo=Github&label=Version&style=flat-square)](https://github.com/Buried-In-Code/Kraken/tags)
[![Github - License](https://img.shields.io/github/license/Buried-In-Code/Kraken?logo=Github&label=License&style=flat-square)](https://opensource.org/licenses/MIT)
[![Github - Contributors](https://img.shields.io/github/contributors/Buried-In-Code/Kraken?logo=Github&label=Contributors&style=flat-square)](https://github.com/Buried-In-Code/Kraken/graphs/contributors)

[![Github Action - Testing](https://img.shields.io/github/actions/workflow/status/Buried-In-Code/Kraken/testing.yaml?branch=main&logo=githubactions&label=Testing&style=flat-square)](https://github.com/Buried-In-Code/Kraken/actions/workflows/testing.yaml)
[![Github Action - Documentation](https://img.shields.io/github/actions/workflow/status/Buried-In-Code/Kraken/docs.yaml?branch=main&logo=githubactions&label=Documentation&style=flat-square)](https://github.com/Buried-In-Code/Kraken/actions/workflows/docs.yaml)

A Java/Kotlin wrapper for the [Metron](https://metron.cloud) API.

## Installation

To get started with Kraken, add the [JitPack](https://jitpack.io) repository to your `build.gradle.kts`.

```kts
repositories {
    maven("https://jitpack.io")
}
```

Then, add Kraken as a dependency.

```kts
dependencies {
    implementation("com.github.Buried-In-Code:Kraken:0.2.3")
}
```

### Example Usage

```kt
import github.buriedincode.kraken.Metron
import github.buriedincode.kraken.SQLiteCache
import github.buriedincode.kraken.AuthenticationException
import github.buriedincode.kraken.ServiceException

fun main() {
    try {
        val session = Metron(username="Username", password="Password", cache=SQLiteCache())

        // Get all Marvel comics for the week of 2021-06-07
        val thisWeek = session.listIssues(params = mapOf(
            "store_date_range_after" to "2021-06-07", 
            "store_date_range_before" to "2021-06-13", 
            "publisher_name" to "marvel"
        ))
        // Print the results
        thisWeek.forEach {
            println("${it.id} ${it.name}")
        }

        // Retrieve the detail for an individual issue
        val asm68 = session.getIssue(id = 31660)
        // Print the issue Description
        println(asm68.description)

  } catch (ae: AuthenticationException) {
      println("Invalid Metron Username/Password.")
  } catch (se: ServiceException) {
      println("Unsuccessful request: ${se.message}")
  }
}
```

## Documentation

- [Kraken](https://buried-in-code.github.io/Kraken)
- [Metron API](https://metron.cloud/docs/)

## Bugs/Requests

Please use the [GitHub issue tracker](https://github.com/Buried-In-Code/Kraken/issues) to submit bugs or request features.

## Socials

[![Social - Fosstodon](https://img.shields.io/badge/%40BuriedInCode-teal?label=Fosstodon&logo=mastodon&style=for-the-badge)](https://fosstodon.org/@BuriedInCode)\
[![Social - Matrix](https://img.shields.io/badge/%23The--Dev--Environment-teal?label=Matrix&logo=matrix&style=for-the-badge)](https://matrix.to/#/#The-Dev-Environment:matrix.org)
