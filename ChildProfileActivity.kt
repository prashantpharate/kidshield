package com.kidshield.childapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChildProfileActivity : AppCompatActivity() {

    private lateinit var etChildName: EditText
    private lateinit var etChildAge: EditText
    private lateinit var etScreenTimeLimit: EditText
    private lateinit var btnSaveProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_profile)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        etChildName = findViewById(R.id.etChildName)
        etChildAge = findViewById(R.id.etChildAge)
        etScreenTimeLimit = findViewById(R.id.etScreenTimeLimit)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        btnSaveProfile.setOnClickListener {
            val childName = etChildName.text.toString()
            val childAge = etChildAge.text.toString().toInt()
            val screenTimeLimit = etScreenTimeLimit.text.toString().toInt()

            val childProfile = ChildProfile(
                name = childName,
                age = childAge,
                screenTimeLimit = screenTimeLimit
            )

            try {
                val fileOutputStream = openFileOutput("child_profile.txt", MODE_PRIVATE)
                val childProfileString = "Name: ${childProfile.name}\nAge: ${childProfile.age}\nScreen Time Limit: ${childProfile.screenTimeLimit}"
                fileOutputStream.write(childProfileString.toByteArray())
                fileOutputStream.close()

                Toast.makeText(this, "Child Profile Saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error saving child profile", Toast.LENGTH_SHORT).show()
            }

            // For now, let's just print the child profile details
            println("Child Profile: $childProfile")
        }
    }
}