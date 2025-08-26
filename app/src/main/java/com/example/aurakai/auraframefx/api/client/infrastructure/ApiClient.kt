package dev.aurakai.auraframefx.api.client.infrastructure

import okhttp3.Call
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.URLConnection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Locale
import java.util.regex.Pattern

val EMPTY_REQUEST: RequestBody = ByteArray(0).toRequestBody()

open class ApiClient(val baseUrl: String, val client: Call.Factory = defaultClient) {
    companion object {
        protected const val ContentType: String = "Content-Type"
        protected const val Accept: String = "Accept"
        protected const val Authorization: String = "Authorization"
        protected const val JsonMediaType: String = "application/json"
        protected const val FormDataMediaType: String = "multipart/form-data"
        protected const val FormUrlEncMediaType: String = "application/x-www-form-urlencoded"
        protected const val XmlMediaType: String = "application/xml"
        protected const val OctetMediaType: String = "application/octet-stream"
        protected const val TextMediaType: String = "text/plain"

        val apiKey: MutableMap<String, String> = mutableMapOf()
        val apiKeyPrefix: MutableMap<String, String> = mutableMapOf()
        var username: String? = null
        var password: String? = null
        var accessToken: String? = null
        const val baseUrlKey: String = "dev.aurakai.auraframefx.api.client.baseUrl"

        @JvmStatic
        val defaultClient: OkHttpClient by lazy {
            builder.build()
        }

        @JvmStatic
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
    }

    /**
     * Guess Content-Type header from the given byteArray (defaults to "application/octet-stream").
     *
     * @param byteArray The given file
     * @return The guessed Content-Type
     */
    protected fun guessContentTypeFromByteArray(byteArray: ByteArray): String {
        val contentType = try {
            URLConnection.guessContentTypeFromStream(byteArray.inputStream())
        } catch (io: IOException) {
            "application/octet-stream"
        }
        return contentType
    }

    /**
     * Guess Content-Type header from the given file (defaults to "application/octet-stream").
     *
     * @param file The given file
     * @return The guessed Content-Type
     */
    protected fun guessContentTypeFromFile(file: File): String {
        val contentType = URLConnection.guessContentTypeFromName(file.name)
        return contentType ?: "application/octet-stream"
    }

    /**
     * Adds a File to a MultipartBody.Builder
     * Defined a helper in the requestBody method to not duplicate code
     * It will be used when the content is a FormDataMediaType and the body of the PartConfig is a File
     *
     * @param name The field name to add in the request
     * @param headers The headers that are in the PartConfig
     * @param file The file that will be added as the field value
     * @return The method returns Unit but the new Part is added to the Builder that the extension function is applying on
     * @see requestBody
     */
    protected fun MultipartBody.Builder.addPartToMultiPart(
        name: String,
        headers: Map<String, String>,
        file: File,
    ) {
        val partHeaders = headers.toMutableMap() +
                ("Content-Disposition" to "form-data; name=\"$name\"; filename=\"${file.name}\"")
        val fileMediaType = guessContentTypeFromFile(file).toMediaTypeOrNull()
        addPart(
            partHeaders.toHeaders(),
            file.asRequestBody(fileMediaType)
        )
    }

    /**
     * Adds any type to a MultipartBody.Builder
     * Defined a helper in the requestBody method to not duplicate code
     * It will be used when the content is a FormDataMediaType and the body of the PartConfig is not a File.
     *
     * @param name The field name to add in the request
     * @param headers The headers that are in the PartConfig
     * @param obj The field name to add in the request
     * @return The method returns Unit but the new Part is added to the Builder that the extension function is applying on
     * @see requestBody
     */
    protected fun <T> MultipartBody.Builder.addPartToMultiPart(
        name: String,
        headers: Map<String, String>,
        obj: T?,
    ) {
        val partHeaders = headers.toMutableMap() +
                ("Content-Disposition" to "form-data; name=\"$name\"")
        addPart(
            partHeaders.toHeaders(),
            parameterToString(obj).toRequestBody(null)
        )
    }

    private fun <T> buildMultipartRequestBody(content: T): RequestBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .apply {
                @Suppress("UNCHECKED_CAST")
                (content as Map<String, PartConfig<*>>).forEach { (name, part) ->
                    when (val body = part.body) {
                        is File -> addPartToMultiPart(name, part.headers, body)
                        is List<*> -> body.forEach { item ->
                            if (item is File) {
                                addPartToMultiPart(name, part.headers, item)
                            } else {
                                addPartToMultiPart(name, part.headers, item)
                            }
                        }
                        else -> addPartToMultiPart(name, part.headers, body)
                    }
                }
            }.build()
    }

    private fun <T> buildFormUrlEncodedRequestBody(content: T): RequestBody {
        return FormBody.Builder().apply {
            @Suppress("UNCHECKED_CAST")
            (content as Map<String, PartConfig<*>>).forEach { (name, part) ->
                add(name, parameterToString(part.body))
            }
        }.build()
    }

    private inline fun <reified T> buildJsonRequestBody(content: T, mediaType: String?): RequestBody {
        return if (content == null) {
            EMPTY_REQUEST
        } else {
            Serializer.kotlinxSerializationJson.encodeToString(content)
                .toRequestBody((mediaType ?: JsonMediaType).toMediaTypeOrNull())
        }
    }

    protected inline fun <reified T> requestBody(content: T, mediaType: String?): RequestBody =
        when {
            content is ByteArray -> content.toRequestBody(
                (mediaType ?: guessContentTypeFromByteArray(content)).toMediaTypeOrNull()
            )
            content is File -> content.asRequestBody(
                (mediaType ?: guessContentTypeFromFile(content)).toMediaTypeOrNull()
            )
            mediaType == FormDataMediaType -> buildMultipartRequestBody(content)
            mediaType == FormUrlEncMediaType -> buildFormUrlEncodedRequestBody(content)
            mediaType == null || mediaType.startsWith("application/") && mediaType.endsWith("json") ->
                buildJsonRequestBody(content, mediaType)
            mediaType == XmlMediaType -> throw UnsupportedOperationException("xml not currently supported.")
            mediaType == TextMediaType && content is String ->
                content.toRequestBody(TextMediaType.toMediaTypeOrNull())
            else -> throw UnsupportedOperationException("requestBody currently only supports JSON body, text body, byte body and File body.")
        }

    private fun handleFileResponse(response: Response): File {
        val body = response.body ?: throw IOException("Response body is null")
        val contentDisposition = response.header("Content-Disposition")
        val fileName = contentDisposition?.let {
            val pattern = Pattern.compile("filename=['\"]?([^'\"\\s]+)['\"]?")
            val matcher = pattern.matcher(it)
            if (matcher.find()) matcher.group(1)?.replace(".*[/\\\\]", "")?.replace(";", "") else null
        }

        val (prefix, suffix) = if (fileName == null) {
            "download" to ""
        } else {
            val pos = fileName.lastIndexOf(".")
            if (pos == -1) {
                fileName to null
            } else {
                fileName.substring(0, pos) to fileName.substring(pos)
            }
        }
        val finalPrefix = if (prefix.length < 3) "download" else prefix

        val tempFile = java.nio.file.Files.createTempFile(finalPrefix, suffix).toFile()
        tempFile.deleteOnExit()
        body.byteStream().use { inputStream ->
            tempFile.outputStream().use { tempFileOutputStream ->
                inputStream.copyTo(tempFileOutputStream)
            }
        }
        return tempFile
    }

    protected inline fun <reified T : Any?> responseBody(
        response: Response,
        mediaType: String? = JsonMediaType,
    ): T? {
        val body = response.body ?: return null

        if (T::class.java == Unit::class.java) {
            return null
        }
        if (T::class.java == File::class.java) {
            @Suppress("UNCHECKED_CAST")
            return handleFileResponse(response) as? T
        }

        return when {
            mediaType == null || (mediaType.startsWith("application/") && mediaType.endsWith("json")) -> {
                val bodyContent = body.string()
                if (bodyContent.isEmpty()) null else Serializer.kotlinxSerializationJson.decodeFromString<T>(bodyContent)
            }
            mediaType == OctetMediaType -> body.bytes() as? T
            mediaType == TextMediaType -> body.string() as? T
            else -> throw UnsupportedOperationException("responseBody currently only supports JSON body, text body and byte body.")
        }
    }

    private fun <I> buildRequest(requestConfig: RequestConfig<I>, httpUrl: okhttp3.HttpUrl): Request {
        val url = httpUrl.newBuilder()
            .addEncodedPathSegments(requestConfig.path.trimStart('/'))
            .apply {
                requestConfig.query.forEach { (key, value) ->
                    value.forEach { queryValue -> addQueryParameter(key, queryValue) }
                }
            }.build()

        val headers = requestConfig.headers.toMutableMap()
        if (requestConfig.body != null && headers[ContentType].isNullOrEmpty()) {
            headers[ContentType] = JsonMediaType
        }
        if (headers[Accept].isNullOrEmpty()) {
            headers[Accept] = JsonMediaType
        }
        if (headers[Accept].isNullOrEmpty()) {
            throw IllegalStateException("Missing Accept header. This is required.")
        }

        val contentType = headers[ContentType]?.substringBefore(";")?.lowercase(Locale.US)

        val requestBuilder = Request.Builder().url(url)
        val body = requestConfig.body?.let { requestBody(it, contentType) }

        when (requestConfig.method) {
            RequestMethod.DELETE -> requestBuilder.delete(body)
            RequestMethod.GET -> requestBuilder.get()
            RequestMethod.HEAD -> requestBuilder.head()
            RequestMethod.PATCH -> requestBuilder.patch(body ?: EMPTY_REQUEST)
            RequestMethod.PUT -> requestBuilder.put(body ?: EMPTY_REQUEST)
            RequestMethod.POST -> requestBuilder.post(body ?: EMPTY_REQUEST)
            RequestMethod.OPTIONS -> requestBuilder.method("OPTIONS", null)
        }

        val headersBuilder = Headers.Builder()
        headers.forEach { (key, value) -> headersBuilder.add(key, value) }
        requestBuilder.headers(headersBuilder.build())

        return requestBuilder.build()
    }

    private inline fun <reified T : Any?> handleResponse(response: Response): ApiResponse<T?> {
        val accept = response.header(ContentType)?.substringBefore(";")?.lowercase(Locale.US)
        return response.use {
            when {
                it.isRedirect -> Redirection(it.code, it.headers.toMultimap())
                it.isInformational -> Informational(it.message, it.code, it.headers.toMultimap())
                it.isSuccessful -> Success(responseBody<T>(it, accept), it.code, it.headers.toMultimap())
                it.isClientError -> ClientError(it.message, it.body?.string(), it.code, it.headers.toMultimap())
                else -> ServerError(it.message, it.body?.string(), it.code, it.headers.toMultimap())
            }
        }
    }

    protected inline fun <reified I, reified T : Any?> request(requestConfig: RequestConfig<I>): ApiResponse<T?> {
        val httpUrl = baseUrl.toHttpUrlOrNull() ?: throw IllegalStateException("baseUrl is invalid.")
        val request = buildRequest(requestConfig, httpUrl)
        val response = client.newCall(request).execute()
        return handleResponse(response)
    }

    protected fun parameterToString(value: Any?): String = when (value) {
        null -> ""
        is Array<*> -> toMultiValue(value, "csv").toString()
        is Iterable<*> -> toMultiValue(value, "csv").toString()
        is OffsetDateTime, is OffsetTime, is LocalDateTime, is LocalDate, is LocalTime ->
            parseDateToQueryString(value)

        else -> value.toString()
    }

    protected inline fun <reified T : Any> parseDateToQueryString(value: T): String {
        /*
        .replace("\"", "") converts the json object string to an actual string for the query parameter.
        The moshi or gson adapter allows a more generic solution instead of trying to use a native
        formatter. It also easily allows to provide a simple way to define a custom date format pattern
        inside a gson/moshi adapter.
        */
        return Serializer.kotlinxSerializationJson.encodeToString(value).replace("\"", "")
    }
}
