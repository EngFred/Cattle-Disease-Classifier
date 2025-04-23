package com.engineerfred.nassa

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

data class Message(val text: String, val isUser: Boolean)

class QAViewModel : ViewModel()
{

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    private val _isTyping = mutableStateOf(false)
    val isTyping: State<Boolean> get() = _isTyping

    fun sendMessage(userInput: String) {
        _messages.add(Message(userInput, isUser = true))
        _isTyping.value = true

        viewModelScope.launch {
            val response = generateResponse(userInput)
            _isTyping.value = false
            _messages.add(
                if (response != null) Message(response, isUser = false)
                else Message("Oops! Something went wrong.", isUser = false)
            )
        }
    }

    private suspend fun generateResponse(prompt: String): String? {
        return try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-pro-002",
                apiKey = "AIzaSyAz6prWl8-o5_jPXVJhey3xlTz8zn4_yCE"
            )
            generativeModel.generateContent(prompt).text?.trim()
        } catch (e: Exception) {
            Log.e("QAScreen", "Error generating response: ${e.message}")
            null
        }
    }
}
