package com.berna8.tfg.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class StorageRepository(private val context: Context) {

    private val cloudName = "dyjqkrshk"
    private val uploadPreset = "autocita_preset"
    private val client = OkHttpClient()

    suspend fun subirImagenTaller(uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("No se pudo abrir la imagen"))
                val bytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "image.jpg",
                        bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", uploadPreset)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                    ?: return@withContext Result.failure(Exception("Respuesta vacía"))

                val json = JSONObject(responseBody)
                val url = json.getString("secure_url")
                Result.success(url)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun subirFotoPerfil(uid: String, uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("No se pudo abrir la imagen"))
                val bytes = inputStream.readBytes()
                inputStream.close()

                val timestamp = System.currentTimeMillis()
                val publicId = "perfiles/${uid}_$timestamp"

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "perfil.jpg",
                        bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", uploadPreset)
                    .addFormDataPart("public_id", publicId)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                    ?: return@withContext Result.failure(Exception("Respuesta vacía"))

                val json = JSONObject(responseBody)
                val url = json.getString("secure_url")
                Result.success(url)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}