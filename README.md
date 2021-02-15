# What is this Library?

`EventSteam` is event Pub/Sub library for androidx.ViewModel.

# Example

```kotlin
class ExampleViewModel : ViewModel() {
    val event = PendingEventStream { event: Event ->
        when(event) {
            EVENT_START_DOWNLOAD, EVENT_ABORT -> true    // EVENT_START_DOWNLOAD is supported.
            else -> false   // not supported event.
        }   
    }

    fun onClickStart() {
        event.next(EVENT_START_DOWNLOAD)
    }

    fun onClickAbortButton() {
        event.next(EVENT_ABORT)
    }
    
    companion object {
        val EVENT_START_DOWNLOAD = PendingEvent("EVENT_START_DOWNLOAD")
        val EVENT_ABORT = PendingEvent("EVENT_ABORT")
    }
}

class ExampleFragment : Fragment() {
    private val viewModel: ExampleViewModel

    fun onViewCreated() {
        viewModel.event.subscribe(viewLifecycleOwner) { event ->
            when(event) {
                EVENT_START_DOWNLOAD -> showDownlaodDialog()
            }
        }
    }
}

class ExampleActivity : AppCompatActivity() {
    private val viewModel: ExampleViewModel

    fun onCreate() {
        viewModel.event.subscribe(viewLifecycleOwner) { event ->
            when(event) {
                EVENT_ABORT -> finish()
            }
        }
    }
}
```

# How to Install

```groovy
// /app/build.gradle
dependencies {
    implementation 'io.github.eaglesakura.eventstream:eventstream:+'
}
```
