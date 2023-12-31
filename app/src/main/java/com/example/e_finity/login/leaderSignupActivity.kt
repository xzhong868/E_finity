package com.example.e_finity.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.e_finity.MainActivity
import com.example.e_finity.Stats
import com.example.e_finity.User
import com.example.e_finity.UserRead
import com.example.e_finity.databinding.ActivityLeadersignupBinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Returning
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.lang.Exception

class leaderSignupActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLeadersignupBinding

    val roles = arrayOf("Leader", "Senior")
    val client = getclient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeadersignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,roles)
        binding.roleSpinner.adapter = arrayAdapter

        binding.signupButton.setOnClickListener {
            if (binding.fullnameEditText.text.toString() == "" ||
                binding.phoneEditText.text.toString() == "" ||
                binding.emailEditText.text.toString() == "" ||
                binding.passwordEditText.text.toString() == "" ||
                binding.codeEditText.text.toString() == "") {
                Toast.makeText(this, "Incomplete fields", Toast.LENGTH_SHORT).show()
            }
            else {
                if (binding.roleSpinner.selectedItem.toString() == "Leader") {
                    if (binding.codeEditText.text.toString() == "bLds98") {
                        lifecycleScope.launch {
                            signUp()
                        }
                    }
                    else {
                        Toast.makeText(this, "Incorrect Code. Please check with your organiser for the correct code.", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    if (binding.codeEditText.text.toString() == "Slki20") {
                        lifecycleScope.launch {
                            signUp()
                        }
                    }
                    else {
                        Toast.makeText(this, "Incorrect Code. Please check with your organiser for the correct code.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    suspend fun signUp() {
        return try {
            client.gotrue.signUpWith(Email) {
                this.email = binding.emailEditText.text.toString()
                this.password = binding.passwordEditText.text.toString()
            }
            updateTable()
            Toast.makeText(this, "Successfully signed up", Toast.LENGTH_SHORT).show()
            login()
        } catch (e: Exception) {
            if ("confirmation_sent_at" in e.toString()) {
                updateTable()
                Toast.makeText(this, "Success signed up", Toast.LENGTH_SHORT).show()
                login()
            }
            else if ("User already registered" in e.toString()) {
                Toast.makeText(this, "There is an existing user with this email", Toast.LENGTH_LONG).show()
            }
            else if ("Unable to validate" in e.toString()) {
                Toast.makeText(this, "Incorrect email format", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateTable() {
        val user = User(uniqueID = binding.emailEditText.text.toString(),
            full_name = binding.fullnameEditText.text.toString(),
            phone_num = binding.phoneEditText.text.toString(),
            role = binding.roleSpinner.selectedItem.toString(),
            avatar = false,
            group = "None",
            score = 0)
        if (binding.roleSpinner.selectedItem.toString() == "Senior") {
            val userStats = Stats(uniqueID = binding.emailEditText.text.toString(),
                Attack = 999, HP = 999, Defence = 999, Accuracy = 999)
            lifecycleScope.launch {
                kotlin.runCatching {
                    client.postgrest["user"].insert(user, returning = Returning.MINIMAL)
                    client.postgrest["stats"].insert(userStats, returning = Returning.MINIMAL)
                }
            }
        }
        else {
            val userStats = Stats(uniqueID = binding.emailEditText.text.toString(),
                Attack = 50, HP = 50, Defence = 50, Accuracy = 50)
            lifecycleScope.launch {
                kotlin.runCatching {
                    client.postgrest["user"].insert(user, returning = Returning.MINIMAL)
                    client.postgrest["stats"].insert(userStats, returning = Returning.MINIMAL)
                }
            }
        }
    }

    private fun movePage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(org.koin.android.R.anim.abc_fade_in, androidx.transition.R.anim.abc_fade_out)
    }

    private fun login() {
        val sharePreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        lifecycleScope.launch {
            kotlin.runCatching {
                client.gotrue.loginWith(Email) {
                    email = binding.emailEditText.text.toString()
                    password = binding.passwordEditText.text.toString()
                }
            }.onSuccess {
                val editor = sharePreference.edit()
                editor.putString("SESSION", binding.emailEditText.text.toString())
                val userinforesponse = client.postgrest["user"].select{
                    eq("uniqueID", binding.emailEditText.text.toString())
                }
                val userinfo = userinforesponse.decodeList<UserRead>()
                editor.putBoolean("AVATAR", userinfo[0].avatar)
                editor.apply()
                movePage()
            }
        }
    }






    private fun getclient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://nabbsmcfsskdwjncycnk.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5hYmJzbWNmc3NrZHdqbmN5Y25rIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTM5MDM3ODksImV4cCI6MjAwOTQ3OTc4OX0.dRVk2u91mLhSMaA1s0FSyIFwnxe2Y3TPdZZ4Shc9mAY"
        ) {
            install(Postgrest)
            install(GoTrue)
        }
    }

}
