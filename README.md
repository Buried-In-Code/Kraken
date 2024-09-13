# Kalibak

![Java Version](https://img.shields.io/badge/Temurin-17-green?style=flat-square&logo=eclipse-adoptium)
![Kotlin Version](https://img.shields.io/badge/Kotlin-2.0.20-green?style=flat-square&logo=kotlin)
![Status](https://img.shields.io/badge/Status-Beta-yellowgreen?style=flat-square)

[![Gradle](https://img.shields.io/badge/Gradle-8.10-informational?style=flat-square&logo=gradle)](https://github.com/gradle/gradle)
[![Ktlint](https://img.shields.io/badge/Ktlint-1.3.1-informational?style=flat-square)](https://github.com/pinterest/ktlint)

[![Github - Version](https://img.shields.io/github/v/tag/Buried-In-Code/Kalibak?logo=Github&label=Version&style=flat-square)](https://github.com/Buried-In-Code/Kalibak/tags)
[![Github - License](https://img.shields.io/github/license/Buried-In-Code/Kalibak?logo=Github&label=License&style=flat-square)](https://opensource.org/licenses/MIT)
[![Github - Contributors](https://img.shields.io/github/contributors/Buried-In-Code/Kalibak?logo=Github&label=Contributors&style=flat-square)](https://github.com/Buried-In-Code/Kalibak/graphs/contributors)

A Java/Kotlin wrapper for the [Metron](https://metron.cloud) API.

## Getting started

To get started with Kalibak, add the [JitPack](https://jitpack.io) repository to your `build.gradle.kts`.

```kts
repositories {
    maven("https://jitpack.io")
}
```

Then, add Kalibak as a dependency.

```kts
dependencies {
    implementation("com.github.Buried-In-Code:Kalibak:0.1.0")
}
```

### Usage

```kt
import github.buriedincode.kalibak.Metron
import github.buriedincode.kalibak.SQLiteCache
import github.buriedincode.kalibak.AuthenticationException
import github.buriedincode.kalibak.ServiceException

fun main() {
    try {
        val session = Metron("Username", "Password", cache=SQLiteCache())

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

For a complete list of available query parameters, refer to [Metron's API docs](https://metron.cloud/docs/).

## Socials

[![Social - Fosstodon](https://img.shields.io/badge/%40BuriedInCode-teal?label=Fosstodon&logo=mastodon&style=for-the-badge)](https://fosstodon.org/@BuriedInCode)\
[![Social - Matrix](https://img.shields.io/badge/%23The--Dev--Environment-teal?label=Matrix&logo=matrix&style=for-the-badge)](https://matrix.to/#/#The-Dev-Environment:matrix.org)
