package dashika.speechedge.audio

import kotlinx.coroutines.flow.Flow

expect class SpeechRecognizer {
    val isModelLoaded: Flow<Boolean>
    suspend fun initModel(): Boolean
    fun recognize(audioFlow: Flow<ByteArray>): Flow<String>
}
