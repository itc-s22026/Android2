package jp.ac.it_college.std.s22026.implicitintentsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.ac.it_college.std.s22026.implicitintentsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
    }
}