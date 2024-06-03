package app.benaya.mystory.story

import app.benaya.mystory.settings.ViewModelAuthentication
import app.benaya.mystory.ActivityFragment
import app.benaya.mystory.R
import app.benaya.mystory.StoryUploadModelFactory
import app.benaya.mystory.settings.PreferencesForSetting
import app.benaya.mystory.databinding.ActivityStoryUploadBinding
import app.benaya.mystory.settings.lightStatusBar
import app.benaya.mystory.login.dataStore
import app.benaya.mystory.maps.ActivityPickMap
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.BuildConfig
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import id.zelory.compressor.Compressor
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@Suppress("NAME_SHADOWING")
class ActivityStoriesUpload : AppCompatActivity() {
    private lateinit var binding: ActivityStoryUploadBinding
    private lateinit var prefen: PreferencesForSetting
    private lateinit var token: String
    private var currentImageUri: Uri? = null
    private var getFileUriStory: File? = null
    private var latlng: LatLng? = null
    private lateinit var fileImage: File
    private val delayTime: Long = 1000
    private val timeInMillis: Long = System.currentTimeMillis()
    private val time: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date(timeInMillis))

    private val viewModel: ViewModelStoriesUpload by lazy {
        ViewModelProvider(this, StoryUploadModelFactory(this))[ViewModelStoriesUpload::class.java]
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            this.currentImageUri = uri
            binding.imagePreview.setImageURI(null)
            cropImage()
        } else {
            Snackbar.make(
                binding.root, getString(R.string.FAILED_IMAGE_SHOW), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.imagePreview.setImageURI(null)
            cropImageCamera()
        } else {
            Snackbar.make(
                binding.root, getString(R.string.FAILED_IMAGE_CAPTURE), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentMaps = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val address = data.getStringExtra("address")
                val lat = data.getDoubleExtra("lat", 0.0)
                val lng = data.getDoubleExtra("lng", 0.0)
                latlng = LatLng(lat, lng)
                binding.Locationpick.text = address
            }
        } else {
            latlng = null
            binding.Locationpick.text = getString(R.string.PICK_LOCATION)
            binding.CbLocation.isChecked = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryUploadBinding.inflate(layoutInflater)
        lightStatusBar(window, true)
        setContentView(binding.root)
        prefen = PreferencesForSetting.getInstance(dataStore)
        btnClick()

        val viewModelAuthentication =
            ViewModelProvider(
                this,
                ModelFactoryStories(prefen)
            )[ViewModelAuthentication::class.java]
        viewModelAuthentication.getUserLoginToken().observe(this) {
            token = it
        }

        viewModel.isLoadinguploadStory.observe(this) {
            showLoading(it)
        }

        viewModel.uploadstorystatus.observe(this) { storyUploadStatus ->
            if (!storyUploadStatus.isNullOrEmpty() && storyUploadStatus == "Story created successfully") {
                Snackbar.make(
                    binding.root, getString(R.string.SUCCESS_UPLOAD_IMAGES), Snackbar.LENGTH_SHORT
                ).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@ActivityStoriesUpload, ActivityFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }, delayTime)
            } else if (viewModel.isErrorstoryupload && !storyUploadStatus.isNullOrEmpty()) {
                Snackbar.make(binding.root, storyUploadStatus, Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun btnClick() {
        binding.apply {
            bckDetailStory.setOnClickListener {
                finish()
            }

            btnUploadImage.setOnClickListener {

                if (this@ActivityStoriesUpload.currentImageUri == null) {
                    Snackbar.make(
                        binding.root, getString(R.string.EMPTY_IMAGE_UPLOAD), Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val caption = binding.tvCaption.text.toString().trim()
                if (caption.isEmpty()) {
                    binding.tvCaption.error = resources.getString(R.string.EMPTY_CAPTION_UPLOAD)
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val file = getFileUriStory as File

                        var compressedFile: File? = null
                        var compressedFileSize = file.length()
                        while (compressedFileSize > 1 * 1024 * 1024) {
                            compressedFile = withContext(Dispatchers.Default) {
                                Compressor.compress(applicationContext, file)
                            }
                            compressedFileSize = compressedFile.length()
                        }

                        fileImage = compressedFile ?: file

                    }

                    val imageCompressFile =
                        fileImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val image: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo", fileImage.name, imageCompressFile
                    )
                    val caption = caption.toRequestBody("text/plain".toMediaType())
                    viewModel.uploadStory(
                        token, image, caption, latlng?.latitude, latlng?.longitude
                    )
                }
            }

            btnTakePhoto.setOnClickListener {
                startCamera()
            }
            btnChooseImage.setOnClickListener {
                startGallery()
            }
            CbLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val intent = Intent(this@ActivityStoriesUpload, ActivityPickMap::class.java)
                    launcherIntentMaps.launch(intent)
                } else {
                    latlng = null
                    binding.Locationpick.text = getString(R.string.PICK_LOCATION)
                }
            }
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = createImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun showImage() {
        this.currentImageUri?.let {
            binding.imagePreview.setImageURI(it)
        }
    }

    private fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(time, ".jpg", storageDir)
    }

    private fun convertFile(image: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val imageFile = createTempFile(context)
        val inputStream = contentResolver.openInputStream(image) as InputStream
        val outputStream: OutputStream = FileOutputStream(imageFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return imageFile
    }

    private fun cropImage() {
        val result = Uri.fromFile(File(cacheDir, "Gallery_$time.jpg"))
        this.currentImageUri?.let {
            UCrop.of(it, result).withOptions(UCrop.Options().apply {
                setCompressionFormat(Bitmap.CompressFormat.JPEG)
                setCompressionQuality(90)
            }).start(this)
        }
    }

    private fun cropImageCamera() {
        val result = Uri.fromFile(File(cacheDir, "Cam_$time.jpg"))
        currentImageUri?.let {
            UCrop.of(it, result).withOptions(UCrop.Options().apply {
                setCompressionFormat(Bitmap.CompressFormat.JPEG)
                setCompressionQuality(90)
            }).start(this)
        }
    }

    private fun createImageUri(context: Context): Uri {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "Cam_$time.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            )
        }
        return uri ?: getUri(this)
    }

    @Suppress("DEPRECATION")
    private fun getUri(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "/Camera/$time.jpg")
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
        return FileProvider.getUriForFile(
            context, "${BuildConfig.APPLICATION_ID}.fileprovider", imageFile
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val uri = data?.let { UCrop.getOutput(it) }
            currentImageUri = uri
            showImage()
            if (uri != null) {
                val myFile = convertFile(uri, this@ActivityStoriesUpload)
                getFileUriStory = myFile
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = data?.let { UCrop.getError(it) }
            Snackbar.make(binding.root, "$cropError", Snackbar.LENGTH_SHORT).show()
        } else if (resultCode == RESULT_CANCELED) {
            this.currentImageUri = null
            binding.imagePreview.setImageURI(null)
            binding.imagePreview.setImageResource(R.drawable.ic_holder_place)
        }
    }
}
