package jp.ac.it_college.std.s22026.navigationsample.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.ac.it_college.std.s22026.navigationsample.R
import jp.ac.it_college.std.s22026.navigationsample.databinding.FragmentHomeBinding
import jp.ac.it_college.std.s22026.navigationsample.databinding.FragmentSecondBinding


/**
 * 2番目の画面用のフラグメント
 */
class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
