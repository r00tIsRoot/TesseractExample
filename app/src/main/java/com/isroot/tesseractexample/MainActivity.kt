package com.isroot.tesseractexample

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var tessApi : TessBaseAPI
    private var dataPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTesseract()
        Log.d("isroot", "OCR result is ${changeBitmapToString(BitmapFactory.decodeResource(resources, R.drawable.tesstest))}")
    }

    //assets에 있는 traineddata파일을 tesseract가 사용할 수 있는 폴더로 복사
    private fun copyTraineddataFile(lang: String){
        try {
            val filepath = "$dataPath/tessdata/$lang.traineddata"
            val outputFile = File(filepath)
            val assetManager: AssetManager = resources.assets
            val inputStream: InputStream = assetManager.open("$lang.traineddata")

            outputFile.outputStream().use {
                    fileOutput ->  inputStream.copyTo(fileOutput)
            }
            inputStream.close()
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkFile(dir: File, lang: String) {
        //디렉터리가 없지만, 해당경로에 디렉터리 생성을 성공할 경우
        if(!dir.exists() && dir.mkdirs()) {
            copyTraineddataFile(lang)
        }
        //디렉터리는 존지하지만 해당 디렉터리 내에 data파일이 없는 경우
        if(dir.exists()) {
            val dataFilePath: String = "$dataPath/tessdata$lang.traineddata"
            val dataFile = File(dataFilePath)
            if(!dataFile.exists()) {
                copyTraineddataFile(lang)
            }
        }
    }

    //Tesseract관련 초기화
    private fun initTesseract() {
        dataPath = "$filesDir/tesseract/"
        checkFile(File("${dataPath}/tessdata"), "kor")
        checkFile(File("${dataPath}/tessdata"), "eng")
        val lang = "kor+eng"
        tessApi = TessBaseAPI()
        tessApi.init(dataPath, lang)
    }

    private fun changeBitmapToString(bitmap: Bitmap): String {
        var ocrResult: String = ""
        tessApi.setImage(bitmap)
        ocrResult = tessApi.utF8Text

        return ocrResult
    }
}