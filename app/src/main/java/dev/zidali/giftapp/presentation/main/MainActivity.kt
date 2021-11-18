package dev.zidali.giftapp.presentation.main

import android.os.Bundle
import android.os.PersistableBundle
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.databinding.ActivityMainBinding
import dev.zidali.giftapp.presentation.BaseActivity

@AndroidEntryPoint
class MainActivity: BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun displayProgressBar(isLoading: Boolean) {
        TODO("Not yet implemented")
    }
}