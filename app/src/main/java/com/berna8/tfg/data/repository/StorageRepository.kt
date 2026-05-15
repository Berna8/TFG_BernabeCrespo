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

/**
 * Repositorio encargado de subir imágenes a Cloudinary.
 * Gestiona tanto las imágenes del taller como las fotos de perfil de los usuarios.
 */
class StorageRepository(private val context: Context) {

    private val cloudName = "dyjqkrshk"
    private val uploadPreset = "autocita_preset"
    private val client = OkHttpClient()

    /**
     * Sube una imagen del taller a Cloudinary y devuelve la URL segura.
     */
    suspend fun subirImagenTaller(uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("No se pudo abrir la imagen"))
                val bytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image.jpg", bytes.toRequestBody("image/jpeg".toMediaTypeOrNull()))
                    .addFormDataPart("upload_preset", uploadPreset)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                    ?: return@withContext Result.failure(Exception("Respuesta vacía"))

                val url = JSONObject(responseBody).getString("secure_url")
                Result.success(url)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Sube la foto de perfil de un usuario a Cloudinary.
     * Usa un timestamp en el ID para forzar la actualización de la caché.
     */
    suspend fun subirFotoPerfil(uid: String, uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("No se pudo abrir la imagen"))
                val bytes = inputStream.readBytes()
                inputStream.close()

                val publicId = "perfiles/${uid}_${System.currentTimeMillis()}"

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "perfil.jpg", bytes.toRequestBody("image/jpeg".toMediaTypeOrNull()))
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

                val url = JSONObject(responseBody).getString("secure_url")
                Result.success(url)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}