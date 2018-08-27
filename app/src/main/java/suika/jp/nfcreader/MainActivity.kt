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


class MainActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
    private val NfcChecker: NfcChecker = NfcChecker()
    private val DATA: String = "FeliCaData"

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
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val nfc = NfcF.get(tag)
            try {
                nfc.connect()

                // polling処理
                // targetSystemCode: 0x0003は交通系ICカードのシステムコード
                val targetSystemCode = byteArrayOf(0x00, 0x03)
                val pollingCommand = polling(targetSystemCode)
                Log.d(DATA, "----------polling----------")
                val pollingRes = nfc.transceive(pollingCommand)
                // System 0 のIDｍを取得(1バイト目はデータサイズ(20)、2バイト目はレスポンスコード(0x01)、IDmのサイズは8バイト)
                val IDm = pollingRes.copyOfRange(2, 9)
                Log.d(DATA, "pollingRes: " + toHex(pollingRes))
                Log.d(DATA, "IDm: " + toHex(IDm))

                // RequestResponseでカードの状態を確認
                val requestResponseCommand = requestResopnse(IDm)
                Log.d(DATA, "----------RequestResponse----------")
                val requestResponse = nfc.transceive(requestResponseCommand)
                Log.d(DATA, "RequestResponse: " + toHex(requestResponse))

                // RequestServiceでエリアやサービスの存在を確認
                val requestServiceCommand = requestService(IDm)
                Log.d(DATA, "----------RequestService----------")
                val requestService = nfc.transceive(requestServiceCommand)
                Log.d(DATA, "RequestService: " + toHex(requestService))

                // Read Without Encryption
                val targetServiceCode = byteArrayOf(0x09, 0x0f)
                read.text = IDm.toString()
                val reqCommand: ByteArray = readWithoutEncryption(IDm, 1)
                Log.d(DATA, "----------readWithoutEncryption----------")
                val readRes = nfc.transceive(reqCommand)
                Log.d(DATA, "ReadWithoutEncryptionResponse: " + toHex(readRes))
//                Log.d(DATA, "parse(req): " + parse(reqCommand))
            } catch (e: Exception) {
                Log.d(DATA, "Exception: " + e.toString() + "  [cannnot read NFC]")
                if (nfc.isConnected) {
                    nfc.close()
                }
            }
        }
    }

    private fun polling(systemCode: ByteArray): ByteArray {
        /*
        size単位はbyte
        [コマンドパケットデータ]
        ・データ長バイト コマンドパケットのデータ長を入れてあげないと怒られる
        ・コマンドコード size: 1 データ: 0x00（固定）
        ・システムコード size: 2 データ: システムコードの指定（0xffffはワイルドカード）
        ・リクエストコード size: 1 データ: 0x00（要求なし） 0x01（システムコード要求） 0x02（通信性能要求）
        ・タイムスロット size: 1 応答可能な最大スロット数の指定（同時に何枚のカードと通信するか的な）
        [レスポンスパケットデータ]
        ・データサイズ、らしい。よくわからんが、、、気にしない
        ・レスポンスコード size: 1 データ: 0x01
        ・IDm size: 8 対象システムのIDm
        ・PMm size: 8
        ・リクエストデータ size: 2 コマンドパケットのリクエストコードが0x00以外で、かつ製品が対応するリクエストコードが指定された場合のみ返送される
         */
        val bout = ByteArrayOutputStream()
        bout.write(0x00)           // データ長バイトのダミー
        bout.write(0x00)           // コマンドコード
        bout.write(systemCode[0].toInt())  // systemCode
        bout.write(systemCode[1].toInt())  // systemCode
        bout.write(0x01)           // リクエストコード
        bout.write(0x0f)           // タイムスロット

        val commandPacket = bout.toByteArray()
        commandPacket[0] = commandPacket.size.toByte()
        Log.d(DATA, "pollingCommand: " + toHex(commandPacket))
        return commandPacket
    }

    private fun readWithoutEncryption(IDm: ByteArray?, size: Int): ByteArray {
        val bout: ByteArrayOutputStream = ByteArrayOutputStream()
        // サービスコード0x090fが交通系のサービスコード
        val serviceCode: ByteArray = byteArrayOf(0x00.toByte(), 0x8b.toByte())
        bout.write(0)
        bout.write(0x6) // Felicaコマンド, [Read Without Encryption] req[1]
        bout.write(IDm)    // カードID 8byte req[2]～req[9]
        bout.write(0x01)   // サービスコードリストの長さ req[10]
        bout.write(serviceCode[1].toInt()) // 履歴のサービスコード下位バイト req[11]（サービスコードはリトルエディアン）
        bout.write(serviceCode[0].toInt()) // 履歴のサービスコード上位バイト req[12]
        bout.write(size)    //ブロック数 req[13]

        for (i in 0..size) {
            bout.write(0x80)
            bout.write(i)
        }
        val commandPacket: ByteArray = bout.toByteArray()
        commandPacket[0] = commandPacket.size.toByte()
        Log.d(DATA, "readWithoutEncryptionCommand: " + toHex(commandPacket))
        return commandPacket //req[0]
    }

    private fun parse(res: ByteArray): String {
        if (res[10] != 0x00.toByte()) { // res[10] エラーコード. 0x00が正常
            return "ERROR"
        }
        val size: Int = res[12].toInt() - 1
        var str: String = ""
        Log.d("resNum", res.size.toString())
        Log.d("size", size.toString())
        for (i in 0..size - 2) {
            val rireki: Rireki = Rireki.parse(res, 13 + i * 16)
            str += rireki.toString() + "\n"
        }
        Log.d("c", str)
        return str
    }

    private fun toHex(id: ByteArray): String {
        val sbuf: StringBuilder = StringBuilder()
        for (i in 0.rangeTo(id.size - 1)) {
            var hex: String = "0" + Integer.toString(id[i].toInt() and 0xff, 16);
            if (hex.length > 2)
                hex = hex.substring(1, 3);
            sbuf.append(" " + i + ":" + hex);
        }
        return sbuf.toString()
    }

    /*
    カードの存在とモードを確認するためのコマンド
    カードの現在のモードを返す
     */
    private fun requestResopnse(IDm: ByteArray): ByteArray {
        val bout: ByteArrayOutputStream = ByteArrayOutputStream()
        bout.write(0x00)    // データ長
        bout.write(0x04)    // コマンドコード
        bout.write(IDm)        // IDm
        val commandPacket = bout.toByteArray()
        commandPacket[0] = commandPacket.size.toByte()
        Log.d(DATA, "requestResponseCommand: " + toHex(commandPacket))
        return commandPacket
    }

    /*
    エリアやサービスの存在確認と鍵バージョンを取得するためのコマンド
    指定したエリアやサービスが存在する場合には鍵バージョンを返送する
     */
    private fun requestService(IDm: ByteArray): ByteArray {
        // 0x008b カード情報の取得のサービスコード
        val serviceCode: ByteArray = byteArrayOf(0x00, 0x8b.toByte())
        val bout = ByteArrayOutputStream()
        bout.write(0)
        bout.write(0x02)    // コマンドコード
        bout.write(IDm)        // IDm
        bout.write(0x01)    // ノード数
        bout.write(serviceCode[1].toInt())
        bout.write(serviceCode[0].toInt())
        val commandPacket = bout.toByteArray()
        commandPacket[0] = commandPacket.size.toByte()
        Log.d(DATA, "requestServiceCommand: " + toHex(commandPacket))
        return commandPacket
    }
}
