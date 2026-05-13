package dashika.speechedge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dashika.speechedge.audio.AudioRecorder
import dashika.speechedge.audio.SpeechRecognizer
import dashika.speechedge.data.Transcription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val speechRecognizer: SpeechRecognizer,
    private val audioRecorder: AudioRecorder
) : ViewModel() {

    private val _transcriptions = MutableStateFlow<List<Transcription>>(emptyList())
    val transcriptions: StateFlow<List<Transcription>> = _transcriptions.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText = _partialText.asStateFlow()

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded = _isModelLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            _isModelLoaded.value = speechRecognizer.initModel()
        }
    }

    fun toggleRecording() {
        if (!_isModelLoaded.value) return
        _isRecording.value = !_isRecording.value
        if (_isRecording.value) {
            startRecording()
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            speechRecognizer.recognize(audioRecorder.startRecording()).collect { resultJson ->
                if (resultJson.contains("\"partial\"")) {
                    val partial = resultJson.substringAfter("\"partial\" : \"").substringBefore("\"")
                    _partialText.value = partial
                } else if (resultJson.contains("\"text\"")) {
                    val text = resultJson.substringAfter("\"text\" : \"").substringBefore("\"")
                    if (text.isNotBlank()) {
                        val now = 0L // Placeholder
                        val newTranscription = Transcription(
                            id = now,
                            text = text,
                            timestamp = now
                        )
                        _transcriptions.value = _transcriptions.value + newTranscription
                        _partialText.value = ""
                    }
                }
            }
        }
    }

    fun addSampleTranscription() {
        val now = 0L // Placeholder
        val sample = Transcription(
            id = now,
            text = "Sample transcription",
            timestamp = now
        )
        _transcriptions.value = _transcriptions.value + sample
    }

    fun deleteTranscription(transcription: Transcription) {
        _transcriptions.value = _transcriptions.value.filter { it.id != transcription.id }
    }
}
