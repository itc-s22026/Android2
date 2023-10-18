package jp.ac.it_college.std.s22026.media3servicesample

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

// 専用クラス MediaSessionService を継承する必要がある。
class MediaPlaybackService : MediaSessionService() {
    // クライアント(MediaController) と連携するためのコンポーネント
    private var mediaSession: MediaSession? = null
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // ExoPlayer に設定するイベントリスナ
    // inner class で定義せずに object 式を使って無名クラスとして作る。
    private val playerListener = object : Player.Listener {
        // プレイヤーの再生状態が変化したときに呼ばれるイベントリスナ
        override fun onPlaybackStateChanged(playbackState: Int) {
            // 今回は再生終了(STATE_ENDED)だけ実装。
            when (playbackState) {
                // 再生完了
                Player.STATE_ENDED -> this@MediaPlaybackService.stopSelf()

                Player.STATE_BUFFERING -> {}

                Player.STATE_IDLE -> {}

                Player.STATE_READY -> {}

            }

        }

    }

    override fun onCreate() {
        super.onCreate()
        // プレイヤー本体となる　ExoPlayer を作れる
        val player = ExoPlayer.Builder(this).build()
        // さっき↑作ったイベントリスナをセット
        player.addListener(playerListener)

        // 作った ExoPlayer を基に MediaSession を作る。
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            // ExoPlayer のリソース開放
            player.release()

            // MediaSession そのもののリソース関数
            release()
        }
        // MediaSession を破棄
        mediaSession = null
        super.onDestroy()
    }
}