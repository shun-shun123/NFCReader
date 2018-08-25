package suika.jp.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import suika.jp.nfcreader.Utils.NfcChecker
import suika.jp.nfcreader.Utils.Rireki
import java.io.ByteArrayOutputStream
import android.R.attr.tag
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
    private val NfcChecker: NfcChecker = NfcChecker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // NFCを扱うためのインスタンスを取得
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        this.NfcChecker.checkEnable(mNfcAdapter, this@MainActivity)
    }

    override fun onResume() {
        super.onResume()
        if (mNfcAdapter != null) {
            // 起動中のActivityが優先的にNFCを受け取れるよう設定
            val intent: Intent = Intent(this@MainActivity, this::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
            mNfcAdapter?.enableForegroundDispatch(this@MainActivity, pendingIntent, null, null)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            // Activityが非表示になる際に優先的にNFCを受け取る設定を解除
            mNfcAdapter?.disableForegroundDispatch(this@MainActivity)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val action: String? = intent?.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || 1==1) {

            val targetSystemCode = byteArrayOf(0x00.toByte(), 0x03.toByte())
            val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
            val nfc = NfcF.get(tag)
            nfc.connect()
            // polling コマンドを作成
            val polling = polling(targetSystemCode)
            // コマンドを送信して結果を取得
            val pollingRes = nfc.transceive(polling)
            // System 0 のIDｍを取得(1バイト目はデータサイズ、2バイト目はレスポンスコード、IDmのサイズは8バイト)
            val idm = Arrays.copyOfRange(pollingRes, 2, 10)
//            // 対象のサービスコード -> 0x1A8B
//            val targetServiceCode = byteArrayOf(0x1A.toByte(), 0x8B.toByte())
            // IDmを文字列に変換して表示
            read.text = idm?.toString()
            // 以下ブログ参照
            val req: ByteArray = readWithoutEncryption(idm, 4)
            Log.d("REQ", toHex(req))
            Log.d("REQ", parse(req))
        }
    }

    private fun polling(systemCode: ByteArray): ByteArray {
        val bout = ByteArrayOutputStream(100)

        bout.write(0x00)           // データ長バイトのダミー
        bout.write(0x00)           // コマンドコード
        bout.write(systemCode[0].toInt())  // systemCode
        bout.write(systemCode[1].toInt())  // systemCode
        bout.write(0x01)           // リクエストコード
        bout.write(0x0f)           // タイムスロット

        val msg = bout.toByteArray()
        msg[0] = msg.size.toByte() // 先頭１バイトはデータ長
        return msg
    }

    private fun readWithoutEncryption(idm: ByteArray?, size: Int): ByteArray {
        val bout: ByteArrayOutputStream = ByteArrayOutputStream(100)
        bout.write(0)
        bout.write(0x6) // Felicaコマンド, [Read Without Encryption] req[1]
        bout.write(idm)    // カードID 8byte req[2]～req[9]
        bout.write(1)   // サービスコードリストの長さ req[10]
        bout.write(0x0f) // 履歴のサービスコード下位バイト req[11]
        bout.write(0x09) // 履歴のサービスコード上位バイト req[12]
        bout.write(size)    //ブロック数 req[13]
        for (i in 0..size) {
            bout.write(0x80)
            bout.write(i)
        }
        var msg: ByteArray = bout.toByteArray()
        msg[0] = msg.size.toByte()
        return msg //req[0]
    }
    private fun parse(res: ByteArray): String {
        if (res[10] != 0x00.toByte()) {
            // Error処理
        }
        val size: Int = res[12].toInt()
        var str: String = ""
        for (i in 0..size) {
            val rireki: Rireki = Rireki.parse(res, 13 + i * 16)
            str += rireki.toString() + "\n"
        }
        return str
    }

    private fun toHex(id: ByteArray): String {
        val sbuf: StringBuilder = StringBuilder()
        for (i in 0..id.size - 1) {
            var hex: String = "0" + Integer.toString(id[i].toInt() + 0x0ff, 16);
            if (hex.length > 2)
                hex = hex.substring(1, 3);
            sbuf.append(" " + i + ":" + hex);
        }
        return sbuf.toString()
    }
}
