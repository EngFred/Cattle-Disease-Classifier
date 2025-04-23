package com.engineerfred.nassa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.nassa.ui.theme.TextPrimaryDark
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QAScreen(
    onBack: () -> Unit,
    darkTheme: Boolean,
    viewModel: QAViewModel = hiltViewModel()
) {

    val messages by remember { derivedStateOf { viewModel.messages } }
    val isTyping by viewModel.isTyping

    val listState = rememberLazyListState()

    val containerColor = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Question Answer", color = TextPrimaryDark) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextPrimaryDark
                        )
                    }
                }
            )
        },
        bottomBar = {
            MessageInputField(
                onSendMessage = { viewModel.sendMessage(it) },
                isTyping = isTyping
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (messages.isEmpty()) {
                EmptyChatUI()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(message)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (isTyping) {
                        item { TypingIndicator() }
                    }
                }

                LaunchedEffect(messages.size) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }
        }
    }

}

@Composable
fun ChatBubble(message: Message) {

    val isError = message.text.contains("Oops! Something went wrong.")
    val bubbleColor = when {
        isError -> Color(0xFFFFE0E0)
        message.isUser -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }
    val textColor = if (isError) Color.Red else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(8.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = parseMarkdownToAnnotatedString(message.text),
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}



@Composable
fun TypingIndicator() {
    var dotCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 4
        }
    }

    val dots = ".".repeat(dotCount)
    ChatBubble(Message("Replying$dots", isUser = false))
}


@Composable
fun MessageInputField(
    modifier: Modifier = Modifier,
    onSendMessage: (String) -> Unit,
    isTyping: Boolean
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(24.dp)).imePadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = {
                Text(
                    "Ask something...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        IconButton(
            onClick = {
                if (text.isNotBlank() && isTyping.not()) {
                    onSendMessage(text)
                    text = ""
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_send),
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}



@Composable
fun EmptyChatUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_message),
            contentDescription = "Empty Chat",
            modifier = Modifier
                .size(130.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Ask me anything about cattle diseases! 🐄",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "I can help answer your questions and give you advice on cattle health.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}




