package com.engineerfred.nassa

import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.engineerfred.nassa.ui.theme.TextPrimaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticScreen(
    onAskClicked: () -> Unit,
    viewModel: DiagnosticViewModel
) {
    val context = LocalContext.current

    val uiState = viewModel.uiState.collectAsState().value

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.refreshCacheFile()
            uiState.imageFileUri?.let { viewModel.setImageUri(it) } ?: Toast.makeText(context, "Image File Uri is null", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.setImageUri(it)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            uiState.imageFileUri?.let { cameraLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Cattle Disease Classifier", color = TextPrimaryDark) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(onClick = {
                        viewModel.updateDialogVisibility()
                    }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = TextPrimaryDark
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAskClicked,
                modifier = Modifier.padding(bottom = 25.dp, end = 30.dp ),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(painter = painterResource(R.drawable.ic_ask), contentDescription = null, tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Display
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                uiState.imageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_image),
                            contentDescription = "Placeholder",
                            tint = Color.Gray,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Camera Button
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            uiState.imageFileUri?.let { cameraLauncher.launch(it) } ?: Toast.makeText(context, "Failed to create image file", Toast.LENGTH_SHORT).show()
                        } else {
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_camera), contentDescription = "Camera")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Camera", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gallery Button
                Button(
                    onClick = {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            android.Manifest.permission.READ_MEDIA_IMAGES
                        } else {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            galleryLauncher.launch("image/*")
                        } else {
                            storagePermissionLauncher.launch(permission)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_gallery), contentDescription = "Gallery")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select from Gallery", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Analyze Button
                Button(
                    onClick = {
                        val imageBitmap = uiState.imageUri?.toBitmap(context)
                        if ( imageBitmap != null ) {
                            viewModel.classifyImage(imageBitmap)
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Analyze")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Image", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            val confidenceColor = uiState.classificationResult?.confidence?.let { confidence ->
                when {
                    confidence >= 0.8f -> Color(0xFF4CAF50) // Green (High Confidence)
                    confidence >= 0.5f -> Color(0xFFFF9800) // Orange (Moderate Confidence)
                    else -> Color(0xFFF44336) // Red (Low Confidence)
                }
            } ?: Color.Gray

            // Diagnostic Results
            Text(
                text = buildAnnotatedString {
                    with(uiState.classificationResult) {
                        if (this != null) {
                            append(label)
                            append(" : ")

                            withStyle(SpanStyle(color = confidenceColor, fontWeight = FontWeight.Bold)) {
                                append("%.4f".format(confidence))
                            }
                        }
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            )
            Spacer(Modifier.height(70.dp))
        }

        if ( uiState.showDialog ) {
            BasicAlertDialog(
                onDismissRequest = {viewModel.updateDialogVisibility()},
                modifier = Modifier.clip(
                    RoundedCornerShape(15.dp)
                ).background(MaterialTheme.colorScheme.background).padding(16.dp),
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Select Theme", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(10.dp))
                        ThemeOption("Use Device Theme", uiState.selectedTheme == ThemeOption.System) {
                            viewModel.updateDialogVisibility()
                            viewModel.updateTheme(ThemeOption.System)
                        }
                        ThemeOption("Light Theme", uiState.selectedTheme == ThemeOption.Light) {
                            viewModel.updateDialogVisibility()
                            viewModel.updateTheme(ThemeOption.Light)
                        }
                        ThemeOption("Dark Theme", uiState.selectedTheme == ThemeOption.Dark) {
                            viewModel.updateDialogVisibility()
                            viewModel.updateTheme(ThemeOption.Dark)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeOption(label: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        RadioButton(selected = isSelected, onClick = onSelect)
        Spacer(Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

