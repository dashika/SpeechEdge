package dashika.speechedge.audio

import kotlinx.coroutines.flow.Flow

expect class AudioRecorder() {
    fun startRecording(): Flow<ByteArray>
}
