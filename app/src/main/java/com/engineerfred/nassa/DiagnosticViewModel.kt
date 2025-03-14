package com.engineerfred.nassa

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject

enum class ThemeOption { System, Light, Dark }

data class DiagnosticUiState(
    val imageUri: Uri? = null,
    val classificationResult: ClassificationResult? = null,
    val cacheImageFile: File? = null,
    val imageFileUri: Uri? = null,
    val showDialog: Boolean = false,
    val selectedTheme: ThemeOption = ThemeOption.Light
)

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    private val classifier: CattleDiseaseClassifier,
    private val prefsManager: PreferencesManager,
    private val context: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getLastTheme()
        refreshCacheFile()
    }

    fun refreshCacheFile() {
        val newFile = File(context.cacheDir, "diagnostic_IMG_${System.currentTimeMillis()}.jpg").apply {
            createNewFile()
        }

        val newImageFileUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", newFile)
        _uiState.update {
            it.copy(
                cacheImageFile = newFile,
                imageFileUri = newImageFileUri
            )
        }
    }

    fun setImageUri(uri: Uri?) {
        _uiState.update {
            it.copy(
                imageUri = uri
            )
        }
    }

    fun classifyImage(bitmap: Bitmap) {
        val result = classifier.classifyImage(bitmap)
        _uiState.update {
            it.copy(classificationResult = result)
        }
    }

    fun updateDialogVisibility() {
        _uiState.update {
            it.copy(showDialog = !it.showDialog)
        }
    }

    fun updateTheme(theme: ThemeOption) {
        _uiState.update {
            it.copy(selectedTheme = theme)
        }
        prefsManager.saveTheme(theme)
    }

    private fun getLastTheme() {
        val savedTheme = prefsManager.getTheme()
        _uiState.update {
            it.copy(selectedTheme = savedTheme)
        }
    }
}