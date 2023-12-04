package jp.ac.it_college.std.s22026.openweatherapi


import android.content.Intent
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
        private const val DEBUG_TAG = "OpenWeatherAPI"
        private const val WEATHER_INFO_URL = "https://api.openweathermap.org/data/2.5/weather"
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

//         Set up spinner adapter
//        val array = arrayOf(
//            "未選択", "北海道(札幌)", "青森", "岩手(盛岡)", "宮城(仙台)", "秋田", "山形", "福島",
//            "茨城(水戸)", "栃木(宇都宮)", "群馬(前橋)", "埼玉(さいたま)", "千葉", "東京", "神奈川(横浜)", "新潟",
//            "富山", "石川(金沢)", "福井", "山梨(甲府)", "長野", "岐阜", "静岡", "愛知(名古屋)",
//            "三重(津)", "滋賀(大津)", "京都", "大阪", "兵庫", "奈良", "和歌山", "鳥取",
//            "島根(松江)", "岡山", "広島", "山口", "徳島", "香川(高松)", "愛媛(松山)", "高知",
//            "福岡", "佐賀", "長崎", "熊本", "大分", "宮崎", "鹿児島", "沖縄(那覇)"
//        )
        fun getCityId(cityName: String?): Int {
            val city = cityList.find { it.CityName == cityName }
            Log.d(MainActivity.DEBUG_TAG, "City found: $cityName, CityId: ${city?.CityId}")
            return city?.CityId ?: -1
        }

        val selectedCity = intent.getStringExtra("selectedCity")
        val cityId = getCityId(selectedCity)

// Assuming cityList is a list of objects with CityId property
        val cityIdList = cityList.mapNotNull { it.CityId }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cityList)
        spinner.adapter = arrayAdapter


        // Set up button click listener
        buttonSetting.setOnClickListener {
            val selectedItem = spinner.selectedItem.toString()
            textSelected.text = "選択された目的地： $selectedItem"

            // 新しい画面に移動するためのIntentを作成
            val intent = Intent(this, NextMainActivity::class.java)
            // 選択された都市の情報を新しい画面に渡す
            intent.putExtra("selectedCity", selectedItem)
            // 新しい画面に移動
            startActivity(intent)
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
