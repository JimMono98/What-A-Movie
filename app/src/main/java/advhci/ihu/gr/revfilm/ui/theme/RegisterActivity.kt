package advhci.ihu.gr.revfilm.ui.theme

import advhci.ihu.gr.revfilm.R
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextMail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnreg1: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressbarreg: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initView()
    }

    private fun initView() {
        editTextMail = findViewById(R.id.reg_email)
        editTextPassword = findViewById(R.id.reg_pass)
        btnreg1 = findViewById(R.id.reg_btn)
        progressbarreg = findViewById(R.id.reg_progbar)
        auth = FirebaseAuth.getInstance()

        val linkTextView3i = findViewById<TextView>(R.id.linkTextView3)
        linkTextView3i.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }


        val currentUser = auth.currentUser
        if (currentUser != null) {
            Toast.makeText(
                this,
                "You Already Have An Account!",
                Toast.LENGTH_SHORT
            ).show()
        }


        btnreg1.setOnClickListener {
            val email = editTextMail.text.toString()
            val password = editTextPassword.text.toString()
            progressbarreg.visibility = View.VISIBLE

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please Fill In Your Username and Password", Toast.LENGTH_SHORT).show()
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressbarreg.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "Account Created!")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}