package jp.ac.it_college.std.s22026.openweatherapi

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import jp.ac.it_college.std.s22026.openweatherapi.R
import jp.ac.it_college.std.s22026.openweatherapi.cityList
import jp.ac.it_college.std.s22026.openweatherapi.databinding.ActivityNextMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class NextMainActivity : AppCompatActivity() {

    companion object {
        private const val DEBUG_TAG = "OpenWeatherAPI"
        private const val WEATHER_INFO_URL =
            "https://api.openweathermap.org/data/2.5/forecast?lang=ja"
        private const val APP_ID = BuildConfig.APP_ID
    }

    private lateinit var binding: ActivityNextMainBinding

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
    }

    private fun getCityId(cityName: String?): Int {
        val city = cityList.find { it.CityName == cityName }
        Log.d(DEBUG_TAG, "City found: $cityName, CityId: ${city?.CityId}")
        return city?.CityId ?: -1
    }

    private fun showWeatherInfo(result: String) {
        val root = JSONObject(result)

        // "list" キーの存在を確認
        if (root.has("list")) {
            val weatherArray = root.getJSONArray("list")

            // 3時間ごとの天気情報を取得
            for (i in 0 until weatherArray.length()) {
                val currentWeather = weatherArray.getJSONObject(i)
                val weatherTime = currentWeather.getString("dt_txt")

                // 日付を表示
                Log.d(DEBUG_TAG, "Date: $weatherTime")

                // 必要な項目を取得
                val mainInfo = currentWeather.getJSONObject("main")
                val feelsLike = mainInfo.getDouble("feels_like") - 273.15
                val temperature = mainInfo.getDouble("temp") - 273.15
                val pressure = mainInfo.getDouble("pressure")
                val humidity = mainInfo.getDouble("humidity")

                val weatherArray = currentWeather.getJSONArray("weather")
                val weatherDescription = weatherArray.getJSONObject(0).getString("description")
                val weatherIcon = weatherArray.getJSONObject(0).getString("icon")

                val windInfo = currentWeather.getJSONObject("wind")
                val windSpeed = windInfo.getDouble("speed")
                val windDirection = windInfo.getDouble("deg")

                val rainInfo = currentWeather.optJSONObject("rain")
                val precipitation = rainInfo?.optDouble("3h") ?: 0.0

                val snowInfo = currentWeather.optJSONObject("snow")
                val snowfall = snowInfo?.optDouble("3h") ?: 0.0

                // UIの更新はメインスレッドで行う
                runOnUiThread {
                    binding.tvTempAPI.text = getString(R.string.temperature, temperature.toInt().toString())
                    binding.tvFeelsLike.text = getString(R.string.feels_like, feelsLike.toInt().toString())
                    binding.tvPressure.text = getString(R.string.pressure, pressure.toInt().toString())
                    binding.tvWeatherDescription.text = getString(R.string.weather_description, weatherDescription)
                    binding.tvWindSpeed.text = getString(R.string.wind_speed, windSpeed.toInt().toString())
                    binding.tvWindDirection.text = getString(R.string.wind_direction, windDirection.toInt().toString())
                    binding.tvPrecipitation.text = getString(R.string.precipitation, precipitation.toInt().toString())
                    binding.tvSnowfall.text = getString(R.string.snowfall, snowfall.toInt().toString())
                    binding.tvForecastTime.text = getString(R.string.forecast_time, weatherTime)
                }

            }
        } else {
            // "list" キーが存在しない場合のエラーログ
            Log.e(DEBUG_TAG, "No value for list")
        }
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
}










//
//    private fun getCityId(cityName: String?): String {
//        // 各都市のIDを返すロジックをここに追加
//        // 例: 東京の場合は "1850147"
//        return "CITY_ID_FOR_$cityName"
//    }
//
//    private fun displayWeatherDetails(cityId: Int) {}
//
//    @SuppressLint("StringFormatInvalid")
//    private fun displayWeatherDetails(weatherDetail: WeatherDetail?) {
//        if (weatherDetail != null) {
//            // 天気情報を表示するTextViewなどを取得し、データを表示
//            findViewById<TextView>(R.id.tvWeatherMain).text =
//                getString(R.string.tv_weather_main, weatherDetail.weatherDescription)
//
//            findViewById<TextView>(R.id.tvTemp).text =
//                getString(R.string.tv_temp, weatherDetail.temperature)
//
//            findViewById<TextView>(R.id.tvFeelsLike).text =
//                getString(R.string.tv_feels_like, weatherDetail.feelsLike)
//
//            findViewById<TextView>(R.id.tvGrndLevel).text =
//                getString(R.string.tv_grnd_level, weatherDetail.pressure)
//
//            findViewById<TextView>(R.id.tvHumidity).text =
//                getString(R.string.tv_humidity, weatherDetail.humidity)
//
//            findViewById<TextView>(R.id.tvWindSpeed).text =
//                getString(R.string.tv_wind_speed, weatherDetail.windSpeed)
//
//            findViewById<TextView>(R.id.tvWindDeg).text =
//                getString(R.string.tv_wind_deg, weatherDetail.windDirection)
//
//            findViewById<TextView>(R.id.tvWindGust).text =
//                getString(R.string.tv_wind_gust, weatherDetail.gustSpeed)
//
//            findViewById<TextView>(R.id.tvPop).text =
//                getString(R.string.tv_pop, weatherDetail.precipitationProbability.toString())
//
//            findViewById<TextView>(R.id.tvD).text =
//                getString(R.string.tv_d, weatherDetail.forecastTime)
//        }
//    }
//}
//
//class WeatherDetailFetcher(
//    private val APP_ID: String,
//    private val callback: (WeatherDetail?) -> Unit
//) : AsyncTask<String, Void, WeatherDetail?>() {
//
//    override fun doInBackground(vararg params: String?): WeatherDetail? {
//        val cityId = params[0]?.toIntOrNull() ?: return null
//        val weatherDetailUrl =
//            "https://api.openweathermap.org/data/2.5/forecast?id=$cityId&appid=$APP_ID"
//
//        try {
//            val url = URL(weatherDetailUrl)
//            val connection = url.openConnection() as HttpURLConnection
//            connection.requestMethod = "GET"
//            connection.connect()
//
//            val responseCode = connection.responseCode
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                val reader = BufferedReader(InputStreamReader(connection.inputStream))
//                val response = StringBuilder()
//                var line: String?
//                while (reader.readLine().also { line = it } != null) {
//                    response.append(line)
//                }
//                reader.close()
//
//                val jsonResponse = JSONObject(response.toString())
//
//                // 解析してWeatherDetailオブジェクトを作成
//                return parseWeatherDetail(jsonResponse)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return null
//    }
//
//    override fun onPostExecute(result: WeatherDetail?) {
//        super.onPostExecute(result)
//        // 非同期処理が完了した後にUIを更新する
//        callback(result)
//    }
//
//    private fun parseWeatherDetail(jsonResponse: JSONObject): WeatherDetail {
//        val main = jsonResponse.getJSONObject("main")
//        val weatherArray = jsonResponse.getJSONArray("weather")
//        val weatherObject = weatherArray.getJSONObject(0)
//        val wind = jsonResponse.getJSONObject("wind")
//
//        return WeatherDetail(
//            temperature = main.getDouble("temp"),
//            feelsLike = main.getDouble("feels_like"),
//            pressure = main.getInt("pressure"),
//            humidity = main.getInt("humidity"),
//            weatherDescription = weatherObject.getString("description"),
//            weatherIcon = weatherObject.getString("icon"),
//            windSpeed = wind.getDouble("speed"),
//            windDirection = wind.getInt("deg"),
//            gustSpeed = wind.getDouble("gust"),
//            precipitationProbability = jsonResponse.optInt("pop", 0),
//            snowfallLast3Hours = jsonResponse.optDouble("snow", 0.0),
//            forecastTime = "2023-12-01 12:00:00"
//        )
//    }
//}
//
//data class WeatherDetail(
//    val temperature: Double,
//    val feelsLike: Double,
//    val pressure: Int,
//    val humidity: Int,
//    val weatherDescription: String,
//    val weatherIcon: String,
//    val windSpeed: Double,
//    val windDirection: Int,
//    val gustSpeed: Double,
//    val precipitationProbability: Int,
//    val snowfallLast3Hours: Double?,
//    val forecastTime: String
//)
