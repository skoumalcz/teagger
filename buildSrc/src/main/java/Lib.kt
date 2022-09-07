@Suppress("ClassName")
object Lib {

    private object V {
        const val kotlin = "1.3.72"
        const val kotlinCoroutines = "1.3.2"
        const val gradle = "4.0.0"

        object AndroidX {
            const val core = "1.2.0-rc01"
        }

        // this is kinda deprecated
        const val timber = "4.7.1"
        const val maven = "2.1"
    }

    //region Access definitions
    val kotlin = Kotlin
    val androidx = AndroidX
    val other = Other
    //endregion

    object Kotlin {
        val lib = kotlin("stdlib-jdk7", V.kotlin)
        val gradle = kotlin("gradle-plugin", V.kotlin)
        val coroutines = kotlinx("coroutines-android", V.kotlinCoroutines)
        val coroutinesTest = kotlinx("coroutines-test", V.kotlinCoroutines)
    }

    object AndroidX {
        val build = "com.android.tools.build:gradle:${V.gradle}"
        val core = androidx("core", "core-ktx", V.AndroidX.core)
    }

    object Other {
        const val timber = "com.jakewharton.timber:timber:${V.timber}"
    }

    const val maven = "com.github.dcendents:android-maven-gradle-plugin:${V.maven}"
    const val teanity = "com.skoumal:teanity-plugin:2.0.0-alpha01"

    // ---

    private fun kotlin(module: String, version: String? = null) =
        "org.jetbrains.kotlin:kotlin-$module${version?.let { ":$version" } ?: ""}"

    private fun kotlinx(module: String, version: String? = null) =
        "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

    private fun androidx(group: String, module: String, version: String) =
        "androidx.$group:$module:$version"

}

