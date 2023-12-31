package com.example.e_finity.teams

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.e_finity.GroupRead
import com.example.e_finity.MainActivity
import com.example.e_finity.UserRead
import com.example.e_finity.adapter.MemberAdapter
import com.example.e_finity.databinding.ActivityJointeamBinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class JoinTeamActivity: AppCompatActivity() {
    private lateinit var binding: ActivityJointeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJointeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val client = getclient()
        val bucket = client.storage["avatar"]
        val sharePreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        val name = intent.getStringExtra("name")
        val color = intent.getStringExtra("color")
        val timemodi = intent.getStringExtra("timemodi")



        val url = bucket.publicUrl(name + ".png") + "?timestamp=" + timemodi
        Glide.with(this).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).error(
            com.example.e_finity.R.drawable.baseline_person).into(binding.jgroupAva)
        binding.jgroupName.text = name
        binding.jgroupAvaBorder.setStrokeColor(Color.parseColor("#"+color))
        binding.groupStroke.setStrokeColor(Color.parseColor("#"+color))
        if (sharePreference.getString("GROUP", "").toString() == name) {
            binding.teamjoinButton.text = "Leave"
        }
        else if (sharePreference.getString("GROUP", "").toString() != "None") {
            binding.teamjoinButton.visibility = View.GONE
        }

        lifecycleScope.launch {
            val memberDataResponse = client.postgrest["user"].select {
                if (name != null) {
                    eq("group", name)
                }
                order("id", Order.ASCENDING)
            }
            val memberData = memberDataResponse.decodeList<UserRead>()
            val adapter = MemberAdapter(memberData, name)
            binding.groupMemberRecycler.adapter = adapter
            binding.groupMemberRecycler.layoutManager = LinearLayoutManager(this@JoinTeamActivity)
        }

        binding.teamjoinButton.setOnClickListener {
            if (binding.teamjoinButton.text.toString() == "Leave") {
                runBlocking {
                    client.postgrest["user"].update ({
                        set("group", "None")
                    }) {
                        eq("uniqueID", sharePreference.getString("SESSION", "").toString())
                    }
                }
                val editor = sharePreference.edit()
                editor.putString("GROUP", "None")
                editor.apply()
                Toast.makeText(this, "You've left the group", Toast.LENGTH_LONG).show()
                binding.teamjoinButton.text = "Join"
            }
            else {
                runBlocking {
                    client.postgrest["user"].update ({
                        set("group", name)
                    }) {
                        eq("uniqueID", sharePreference.getString("SESSION", "").toString())
                    }
                }
                val editor = sharePreference.edit()
                editor.putString("GROUP", name)
                editor.apply()
                Toast.makeText(this, "You've joined the group", Toast.LENGTH_LONG).show()
                binding.teamjoinButton.text = "Leave"
            }
            lifecycleScope.launch {
                val memberDataResponse = client.postgrest["user"].select {
                    if (name != null) {
                        eq("group", name)
                    }
                    order("id", Order.ASCENDING)
                }
                val memberData = memberDataResponse.decodeList<UserRead>()
                val adapter = MemberAdapter(memberData, name)
                binding.groupMemberRecycler.adapter = adapter
                binding.groupMemberRecycler.layoutManager = LinearLayoutManager(this@JoinTeamActivity)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val client = getclient()
        val name = intent.getStringExtra("name")
        lifecycleScope.launch {
            val memberDataResponse = client.postgrest["user"].select {
                if (name != null) {
                    eq("group", name)
                }
                order("id", Order.ASCENDING)
            }
            val memberData = memberDataResponse.decodeList<UserRead>()
            val adapter = MemberAdapter(memberData, name)
            binding.groupMemberRecycler.adapter = adapter
            binding.groupMemberRecycler.layoutManager = LinearLayoutManager(this@JoinTeamActivity)
        }
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