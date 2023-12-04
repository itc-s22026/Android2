package jp.ac.it_college.std.s22026.openweatherapi

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import jp.ac.it_college.std.s22026.openweatherapi.BuildConfig
import jp.ac.it_college.std.s22026.openweatherapi.R
import jp.ac.it_college.std.s22026.openweatherapi.cityList
import jp.ac.it_college.std.s22026.openweatherapi.databinding.ActivityNextMainBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NextMainActivity : AppCompatActivity() {
    private var currentIndex = 0
    private lateinit var binding: ActivityNextMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var listArray: JSONArray? = null

    companion object {
        private const val DEBUG_TAG = "OpenWeatherAPI"
        private const val WEATHER_INFO_URL =
            "https://api.openweathermap.org/data/2.5/forecast?lang=ja"
        private const val APP_ID = BuildConfig.APP_ID
    }

    private class WeatherInfoAsyncTask(activity: NextMainActivity) :
        AsyncTask<Int, Void, String>() {
        private val activityReference: WeakReference<NextMainActivity> = WeakReference(activity)

        override fun doInBackground(vararg params: Int?): String {
            val cityId = params.firstOrNull() ?: return ""
            val activity = activityReference.get() ?: return ""

            val url = "$WEATHER_INFO_URL&id=$cityId&appid=$APP_ID"

            try {
                val urlConnection = URL(url).openConnection() as? HttpURLConnection
                urlConnection?.run {
                    connectTimeout = 100000 // 10 seconds
                    readTimeout = 100000 // 10 seconds
                    requestMethod = "GET"

                    // エラーハンドリング
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val result = reader.readText()

                        // リソース解放
                        inputStream.close()
                        reader.close()
                        disconnect()

                        return result
                    } else {
                        // エラーレスポンスがある場合は処理を追加
                        Log.e(DEBUG_TAG, "HTTP レスポンスコードが異常です: $responseCode")

                        // エラーレスポンスの取得
                        val errorStream = errorStream
                        val reader = BufferedReader(InputStreamReader(errorStream))
                        val errorResult = reader.readText()

                        // リソース解放
                        errorStream.close()
                        reader.close()
                        disconnect()

                        return errorResult
                    }
                }
            } catch (ex: SocketTimeoutException) {
                ex.printStackTrace()
                Log.w(DEBUG_TAG, "通信タイムアウト", ex)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e(DEBUG_TAG, "エラー", ex)
            }
            return ""
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val activity = activityReference.get() ?: return

            // UIの更新はメインスレッドで行う
            activity.runOnUiThread {
                activity.showWeatherInfo(result)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNextMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intentから都市情報を取得
        val selectedCity = intent.getStringExtra("selectedCity")

        // TextViewに都市情報を表示
        val textSelectedCity = findViewById<TextView>(R.id.textSelectedCity)
        textSelectedCity.text = getString(R.string.tv_telop, selectedCity)

        // ここでCityIdを取得
        val cityId = getCityId(selectedCity)

        // 天気情報を非同期で取得
        WeatherInfoAsyncTask(this).execute(cityId)

        val nextButton = findViewById<Button>(R.id.nextButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val backButton = findViewById<Button>(R.id.backButton)

        if (nextButton != null) {
            nextButton.setOnClickListener {
                currentIndex += 1
                showCurrentWeatherInfo()
            }
        }

        resetButton.setOnClickListener {
//            Log.d("ResetButton", "Reset button clicked")
            val back = Intent(this, MainActivity::class.java)
            startActivity(back)
        }

        if (backButton != null) {
            backButton.setOnClickListener {
                currentIndex -= 1
                showCurrentWeatherInfo()
            }
        }
    }

    private fun getCityId(cityName: String?): Int {
        val city = cityList.find { it.CityName == cityName }
        Log.d(DEBUG_TAG, "City found: $cityName, CityId: ${city?.CityId}")
        return city?.CityId ?: -1
    }

    private fun updateUI(firstWeather: JSONObject) {
        // 修正：currentWeather の型を修正
        val mainInfo = firstWeather.getJSONObject("main")
        val feelsLike = mainInfo.getDouble("feels_like") - 273.15
        val temperature = mainInfo.getDouble("temp") - 273.15
        val pressure = mainInfo.getDouble("pressure")
        val humidity = mainInfo.getDouble("humidity")

        val weatherArray = firstWeather.getJSONArray("weather")
        val weatherDescription = weatherArray.getJSONObject(0).getString("description")
        val weatherIcon = weatherArray.getJSONObject(0).getString("icon")

        val windInfo = firstWeather.getJSONObject("wind")
        val windSpeed = windInfo.getDouble("speed")
        val windDirection = windInfo.getDouble("deg")

        val rainInfo = firstWeather.optJSONObject("rain")
        val precipitation = rainInfo?.optDouble("3h") ?: 0.0

        val snowInfo = firstWeather.optJSONObject("snow")
        val snowfall = snowInfo?.optDouble("3h") ?: 0.0

        // UIの更新はメインスレッドで行う
        binding.tvTempAPI.text = getString(R.string.temperature, temperature.toInt().toString())
        binding.tvFeelsLike.text = getString(R.string.feels_like, feelsLike.toInt().toString())
        binding.tvPressure.text = getString(R.string.pressure, pressure.toInt().toString())
        binding.tvHumidity.text = getString(R.string.humidity, humidity.toInt().toString())
        binding.tvWeatherDescription.text = getString(R.string.weather_description, weatherDescription)
        binding.tvWindSpeed.text = getString(R.string.wind_speed, windSpeed.toInt().toString())
        binding.tvWindDirection.text = getString(R.string.wind_direction, windDirection.toInt().toString())
        binding.tvPrecipitation.text = getString(R.string.precipitation, precipitation.toInt().toString())
        binding.tvSnowfall.text = getString(R.string.snowfall, snowfall.toInt().toString())
        binding.tvForecastTime.text = getString(R.string.forecast_time, firstWeather.getString("dt_txt"))
    }

    private fun showWeatherInfo(result: String) {
        try {
            val root = JSONObject(result)
            val city = root.getJSONObject("city")
            val cityName = city.getString("name")

            listArray = root.getJSONArray("list")
            // showCurrentWeatherInfo() の呼び出しをコメントアウト
            // showCurrentWeatherInfo()
        } catch (e: JSONException) {
            Log.e(DEBUG_TAG, "Error parsing JSON", e)
        }
    }


    private fun showCurrentWeatherInfo() {
        if (listArray != null && currentIndex >= 0 && currentIndex < listArray!!.length()) {
            val firstWeather = listArray!!.getJSONObject(currentIndex)
            val weatherTime = firstWeather.getString("dt_txt")

            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dtObjectDateTime = LocalDateTime.parse(weatherTime, formatter)

            if (dtObjectDateTime.isAfter(currentDateTime)) {
                runOnUiThread {
                    updateUI(firstWeather)
                }
            } else {
                // 日付が過去の場合、currentIndexを増やして再試行
                currentIndex += 1
                showCurrentWeatherInfo()
            }

            // 日付を表示
            Log.d(DEBUG_TAG, "Date: $weatherTime")
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish() // Activityを終了してMainActivityに戻る
    }
}
