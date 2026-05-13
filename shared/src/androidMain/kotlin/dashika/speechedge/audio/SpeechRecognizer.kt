package dashika.speechedge.audio

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class SpeechRecognizer(private val context: Context) {
    private var model: Model? = null
    private val _isModelLoaded = MutableStateFlow(false)
    actual val isModelLoaded: Flow<Boolean> = _isModelLoaded

    actual suspend fun initModel(): Boolean = suspendCoroutine { continuation ->
        StorageService.unpack(context, "model/vosk-model-small-ru-0.22", "model",
            { model ->
                this.model = model
                _isModelLoaded.value = true
                continuation.resume(true)
            },
            { exception ->
                exception.printStackTrace()
                continuation.resume(false)
            }
        )
    }

    actual fun recognize(audioFlow: Flow<ByteArray>): Flow<String> = callbackFlow {
        val recognizer = model?.let { Recognizer(it, 16000.0f) }
        if (recognizer == null) {
            close(IllegalStateException("Model not initialized"))
            return@callbackFlow
        }

        audioFlow.collect { data ->
            // Vosk acceptWaveForm can take byte[] or short[]
            if (recognizer.acceptWaveForm(data, data.size)) {
                trySend(recognizer.result)
            } else {
                trySend(recognizer.partialResult)
            }
        }

        awaitClose {
            recognizer.close()
        }
    }.flowOn(Dispatchers.Default)
}
