package dashika.speechedge.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class SpeechRecognizer {
    private val _isModelLoaded = MutableStateFlow(false)
    actual val isModelLoaded: Flow<Boolean> = _isModelLoaded

    actual suspend fun initModel(): Boolean {
        // iOS implementation would go here using C-Interop with Vosk iOS library
        return false
    }

    actual fun recognize(audioFlow: Flow<ByteArray>): Flow<String> {
        // iOS implementation would go here
        return MutableStateFlow("")
    }
}
