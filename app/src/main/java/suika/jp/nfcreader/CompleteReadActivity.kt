package suika.jp.nfcreader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_completion.*


open class CompleteReadActivity: AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_completion);
        //ユーザがカードをかざすとNow Loading ...に代わる
        val LodingCmplText: String = "Complete Loading !!"
        read.text =LodingCmplText

    }
}