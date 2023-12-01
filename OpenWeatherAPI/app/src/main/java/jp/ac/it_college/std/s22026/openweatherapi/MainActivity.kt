package jp.ac.it_college.std.s22026.openweatherapi

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import jp.ac.it_college.std.s22026.openweatherapi.BuildConfig.APP_ID
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    companion object {
        private const val DEBUG_TAG = "AsyncSample"
        private const val WEATHER_INFO_URL =
            "https://api.openweathermap.org/data/2.5/forecast?"
        // Replace with your actual OpenWeatherMap API key
    }

    private lateinit var spinner: Spinner
    private lateinit var buttonSetting: Button
    private lateinit var textSelected: TextView
    private lateinit var buttonReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        spinner = findViewById(R.id.spinner)
        buttonSetting = findViewById(R.id.buttonSetting)
        textSelected = findViewById(R.id.textSelected)
        buttonReset = findViewById(R.id.buttonReset)

        // Set up spinner adapter
        val array = arrayOf(
            "未選択", "北海道", "青森", "岩手", "宮城", "秋田", "山形", "福島",
            "茨城", "栃木", "群馬", "埼玉", "千葉", "東京", "神奈川", "新潟",
            "富山", "石川", "福井", "山梨", "長野", "岐阜", "静岡", "愛知",
            "三重", "滋賀", "京都", "大阪", "兵庫", "奈良", "和歌山", "鳥取",
            "島根", "岡山", "広島", "山口", "徳島", "香川", "愛媛", "高知",
            "福岡", "佐賀", "長崎", "熊本", "大分", "宮崎", "鹿児島", "沖縄"
        )
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, array)
        spinner.adapter = arrayAdapter

        // Set up button click listener
        buttonSetting.setOnClickListener {
            val selectedItem = spinner.selectedItem.toString()
            textSelected.text = "選択された目的地： $selectedItem"
        }

        buttonReset.setOnClickListener {
            spinner.setSelection(0)
        }

        // Set up spinner item selection listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == 0) {
                    buttonSetting.isEnabled = false
                    textSelected.text = "選択された目的地："
                } else {
                    buttonSetting.isEnabled = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected
            }
        }
    }

    private fun receiveWeatherInfo(cityid: Int) {
        val url = "$WEATHER_INFO_URL?id=$cityid&appid=$APP_ID"
        val executorService = Executors.newSingleThreadExecutor()
        val backgroundReceiver = WeatherInfoBackgroundReceiver(url)
        val future = executorService.submit(backgroundReceiver)
        val result = future.get()
        // Handle the result as needed
        Log.d(DEBUG_TAG, "Received weather info: $result")
    }

    private class WeatherInfoBackgroundReceiver(val urlString: String) : Callable<String> {
        override fun call(): String {
            val url = URL(urlString)
            val con = url.openConnection() as HttpURLConnection
            con.apply {
                connectTimeout = 1000
                readTimeout = 1000
                requestMethod = "GET"
            }
            return try {
                con.connect()
                val result = con.inputStream.reader().readText()
                con.disconnect()
                result
            } catch (ex: SocketTimeoutException) {
                Log.w(DEBUG_TAG, "通信タイムアウト", ex)
                ""
            }
        }
    }
}
