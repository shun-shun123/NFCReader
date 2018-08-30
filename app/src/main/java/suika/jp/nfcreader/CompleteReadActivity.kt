package suika.jp.nfcreader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.appcompat.R.styleable.Toolbar
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_completion.*
import android.support.v4.app.NavUtils
import android.view.MenuItem


open class CompleteReadActivity: AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_completion);
        //ユーザがカードをかざすとNow Loading ...に代わる
        val LodingCmplText: String = "Complete Loading !!"
        read.text =LodingCmplText
        val returnButton = findViewById<Button>(R.id.return_button)
        returnButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_completion)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}