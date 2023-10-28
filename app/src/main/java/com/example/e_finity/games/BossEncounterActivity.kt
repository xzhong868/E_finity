package com.example.e_finity.games

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.e_finity.R
import com.example.e_finity.databinding.ActivityBossEncounterBinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.android.ext.android.bind

var mMediaPlayer: MediaPlayer? = null
class BossEncounterActivity: AppCompatActivity() {
    private lateinit var binding: ActivityBossEncounterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBossEncounterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide)
        val slideAnimationBoss = AnimationUtils.loadAnimation(this, R.anim.slideboss)

        val client = getclient()
        val bucket = client.storage["avatar"]
        val sharePreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        val url = bucket.publicUrl(sharePreference.getString("SESSION", "").toString() + ".png") + "?timestamp=" + (System.currentTimeMillis()/(1000*60*3))
        Glide.with(this).load(url).circleCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.avatar).into(binding.playerSprite)
        binding.playerSpriteCard.startAnimation(slideAnimation)
        binding.bossSprite.startAnimation(slideAnimationBoss)

        mMediaPlayer = MediaPlayer.create(this, R.raw.battlebgm)
        mMediaPlayer!!.isLooping = true
        mMediaPlayer!!.start()

        Handler().postDelayed({
            val intent = Intent(this, BossBattleActivity::class.java)
            startActivity(intent)
        }, 6500)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer!!.stop()
    }

    private fun getclient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://nabbsmcfsskdwjncycnk.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5hYmJzbWNmc3NrZHdqbmN5Y25rIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTM5MDM3ODksImV4cCI6MjAwOTQ3OTc4OX0.dRVk2u91mLhSMaA1s0FSyIFwnxe2Y3TPdZZ4Shc9mAY"
        ) {
            install(Postgrest)
            install(GoTrue)
            install(Storage)
        }
    }
}