package jp.ac.it_college.std.s22026.media3servicesample

import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import jp.ac.it_college.std.s22026.media3servicesample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // MediaController のインスタンスを管理するオブジェクト
    private lateinit var controllerFuture: ListenableFuture<MediaController>

    // MediaController を使う処理を簡素化するための工夫
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.play.setOnClickListener { playSample() }
        binding.pause.setOnClickListener { pauseSample() }
    }

    override fun onStart() {
        super.onStart()
        // MediaController の準備
        controllerFuture = MediaController.Builder(
            this,
            // MediaSessionService との連携に必要なトークン
            SessionToken(this, ComponentName(this, MediaPlaybackService::class.java))
        ).buildAsync()
    }

    override fun onStop() {
        // MediaController のリソース解放
        MediaController.releaseFuture(controllerFuture)

        super.onStop()
    }

    private fun playSample() {
        val controller = this.controller ?: return

        // 再生したいデータを指定
        controller.setMediaItem(
            MediaItem.fromUri("android.resource://${packageName}/${R.raw.manuke}")
        )
        // 指定した番号のデータに切り替えつつ、デフォルト(多分先頭)に再生位置をセット
        controller.seekToDefaultPosition(0)
        // データの再生を指示
        controller.play()
    }

    private fun pauseSample() {
        val controller = this.controller ?: return
        // データの再生を停止
        controller.stop()
    }
}