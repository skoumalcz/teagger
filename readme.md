# Teagger

> Logging to file made easy

Teagger helps you to keep your logs forever or ship them back to you instantly.


## Usage ##

Create your instance with a file or stream:

```kotlin
class App: Application() {

    override fun onCreate() {
        Teagger.start(this) { withFile(getMyFile(context)) } 
    }

}
```

And use it:

```kotlin
fun logSomething() {
    Teagger.v("something") // gets scheduled to be written on disk
}
```

---

Use Timber integration:

```kotlin
Timber.plant(TeaggerTree())
```

---

Customize entries:

```kotlin
Teagger.instance.entryTransformer = object : LogEntryDelegate { /*...*/ }
```

---

Optionally display UI:

```xml
<application>
    <activity android:name="com.skoumal.teagger.ui.LoggerActivity" />
</application>
```

---

Don't forget to register a [FileProvider](app/src/main/res/xml/provider_paths.xml)!

---

## Download ##

`ext.teagger = `[![](https://jitpack.io/v/com.skoumal/teagger.svg)](https://jitpack.io/#com.skoumal/teagger)


```groovy
dependencies {
    implementation "com.skoumal:teagger:${ext.teagger}"
}
```