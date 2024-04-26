package advhci.ihu.gr.revfilm.ui.theme

import advhci.ihu.gr.revfilm.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var userEdt: EditText
    private lateinit var passEdt: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
    }

    private fun initView() {
        userEdt = findViewById(R.id.editTextText)
        passEdt = findViewById(R.id.editTextPassword)
        loginBtn = findViewById(R.id.loginBtn)

        loginBtn.setOnClickListener {
            val username = userEdt.text.toString()
            val password = passEdt.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in your username and password", Toast.LENGTH_SHORT).show()
            } else if (username == "test" && password == "test") {
                startActivity(Intent(this, MainActivity::class.java))
                // Replace MainActivity::class.java with your desired destination activity
            } else {
                Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}