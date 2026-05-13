package dashika.speechedge.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class AudioRecorder actual constructor() {
    actual fun startRecording(): Flow<ByteArray> = flow {
        // iOS implementation would go here using AVAudioEngine
    }
}
