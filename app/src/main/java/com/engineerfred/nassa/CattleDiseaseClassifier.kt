package com.engineerfred.nassa

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import androidx.core.graphics.scale

class CattleDiseaseClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null

    companion object {
        private const val  MODEL_PATH = "model.tflite"
        const val TAG = "Nasser"
    }

    init {
        setup()
    }

    private fun setup() {
        try {
            val model = FileUtil.loadMappedFile(context, MODEL_PATH)
            val options = Interpreter.Options().apply {
                numThreads = 4
            }
            interpreter = Interpreter(model, options)

            val inputShape = interpreter?.getInputTensor(0)?.shape()
            val outputShape = interpreter?.getOutputTensor(0)?.shape()

            Log.i(TAG, "Model loaded successfully!\n_________________________\nInput shape: ${inputShape?.joinToString()}\nOutput shape: ${outputShape?.joinToString()}")

        } catch (ex: Exception) {
            Log.e(TAG, "Error loading model: ${ex.message}")
        }
    }

    fun classifyImage(bitmap: Bitmap): ClassificationResult? {

        try {
            val convertedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val resizedBitmap = convertedBitmap.scale(256, 256)

            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedBitmap)

            val imageProcessor = ImageProcessor.Builder()
                .add(NormalizeOp(0f, 255f)) // Normalize pixel values to [0, 1]
                .build()
            val processedImage = imageProcessor.process(tensorImage)

            val inputBuffer = processedImage.buffer

            Log.d(TAG, "Input Buffer Size: ${inputBuffer.remaining()} bytes")

            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 3), DataType.FLOAT32)
            interpreter?.run(inputBuffer, outputBuffer.buffer)

            val results = outputBuffer.floatArray
            Log.d(TAG, results.joinToString(","))

            val labels = arrayOf("Lumpy Skin Disease", "Foot and Mouth Disease", "Healthy")
            // Find the index of the highest confidence score
            val maxIndex = results.indices.maxByOrNull { results[it] } ?: 0
            val confidence = results[maxIndex]

            //return "${labels[maxIndex]} : ${"%.4f".format(confidence)}"
            return ClassificationResult(label = labels[maxIndex], confidence = confidence)
        } catch (e: Exception){
            Log.e(TAG, "Something went wrong! ${e.message}")
            return null
        }
    }

}
