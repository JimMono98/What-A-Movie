package advhci.ihu.gr.revfilm.ui.theme

import advhci.ihu.gr.revfilm.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.intro_pic)

        val imageView2: ImageView = findViewById(R.id.imageView2)
        imageView2.setImageResource(R.drawable.gradient_background)

        val getInBtn: Button = findViewById(R.id.getInBtn)
        getInBtn.setOnClickListener {
            startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
        }
    }
}