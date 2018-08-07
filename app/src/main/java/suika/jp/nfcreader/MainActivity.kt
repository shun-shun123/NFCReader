package suika.jp.nfcreader

import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var mNfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // NFCを扱うためのインスタンスを取得
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        // NFCが搭載されているかチェック
        if (mNfcAdapter != null) {
            // NFC機能が有効になっているかチェック
            if (!mNfcAdapter.isEnabled) {
                // NFC機能が無効の場合はユーザへ通知
                Toast.makeText(this@MainActivity, "NFC機能が無効です", Toast.LENGTH_SHORT).show()
            }
        } else {
            // NFC非搭載の場合はユーザへ通知
            Toast.makeText(this@MainActivity, "NFCが非搭載です", Toast.LENGTH_SHORT).show()
        }
    }
}
