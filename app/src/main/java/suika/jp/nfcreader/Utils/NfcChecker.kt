package suika.jp.nfcreader.Utils

import android.content.Context
import android.nfc.NfcAdapter
import android.widget.Toast

class NfcChecker() {

    // NFCが利用可能かをチェックする
    fun checkEnable(NfcAdapter: NfcAdapter?, context: Context) {
        // NFCが搭載されているか
        if (NfcAdapter != null) {
            // NFCが有効になっているかをチェック
            if (NfcAdapter.isEnabled) {
                // 有効
                makeToast(context, "NFCの利用が可能です")
            } else {
                // 無効
                makeToast(context, "NFCが無効になっています")
            }
        } else {
            // NFCが搭載されていない
            makeToast(context, "NFCが搭載されていません")
        }
    }

    // ToastMessageの簡略化メソッド
    private fun makeToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}