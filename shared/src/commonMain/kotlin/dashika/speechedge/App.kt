package dashika.speechedge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dashika.speechedge.data.Transcription
import dashika.speechedge.ui.MainViewModel

@Composable
fun SpeechEdgeApp(viewModel: MainViewModel, hasPermission: Boolean) {
    val transcriptions by viewModel.transcriptions.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val partialText by viewModel.partialText.collectAsState()
    val isModelLoaded by viewModel.isModelLoaded.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = { viewModel.addSampleTranscription() },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Sample")
                }
                FloatingActionButton(
                    onClick = {
                        if (hasPermission && isModelLoaded) {
                            viewModel.toggleRecording()
                        }
                    },
                    containerColor = if (isRecording) MaterialTheme.colorScheme.errorContainer 
                                     else if (!isModelLoaded) MaterialTheme.colorScheme.surfaceVariant
                                     else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop Recording" else "Start Recording"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "SpeechEdge (KMP)",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            if (!isModelLoaded) {
                Text(
                    text = "Loading offline model...",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (!hasPermission) {
                Text(
                    text = "Microphone permission is required for transcription.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (partialText.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = partialText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(transcriptions) { transcription ->
                    TranscriptionItem(
                        transcription = transcription,
                        onDelete = { viewModel.deleteTranscription(transcription) }
                    )
                }
            }
        }
    }
}

@Composable
fun TranscriptionItem(transcription: Transcription, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transcription.text, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Timestamp: ${transcription.timestamp}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
