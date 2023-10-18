package jp.ac.it_college.std.s22026.servicesample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import jp.ac.it_college.std.s22026.servicesample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i("SERVICE_SAMPLE", "通知の権限がもらえた")
                startSoundManagerService()
            } else {
                Log.i("SERVICE_SAMPLE", "通知の権限がもらえなかった")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btPlay.setOnClickListener { startSoundManagerService() }
        binding.btStop.setOnClickListener { stopSoundManagerService() }

        // 通知から起動されていたらやりたいこと書く
        val fromNotification = intent.getBooleanExtra("fromNotification", false)
        if (fromNotification) {
            binding.btPlay.isEnabled = false
            binding.btStop.isEnabled = true
        }
    }

    private fun startSoundManagerService() {
        // 適切に権限がなければ何もしない
        if (!notificationPermissionRequest()) return

        val intent = Intent(this, SoundManageService::class.java)
        startService(intent)
        binding.btPlay.isEnabled = false
        binding.btStop.isEnabled = true
    }

    private fun stopSoundManagerService() {
        val intent = Intent(this, SoundManageService::class.java)
        stopService(intent)
        binding.btPlay.isEnabled = true
        binding.btStop.isEnabled = false
    }

    private fun notificationPermissionRequest(): Boolean {
        // Android 13(API 33)未満なら無条件で true 返して何もしない
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        // 以下は権限の有無をチェック。
        // 権限があれば true 返して終わり。なければ権限のリクエストを投げつつ false を返す。
        return when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 権限を確認した結果、既にもらっていた場合はここ
                true
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                false
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
                false
            }
        }
    }
}