package suika.jp.nfcreader.Utils;

import android.util.SparseArray;

import suika.jp.nfcreader.Json.Suika;

public class Rireki {
    private int termId;
    private int procId;
    private int year;
    private int month;
    private int day;
    private String kind;
    private int remain;
    private int seqNo;
    private String line;
    private String inRegion;
    private String inLine;
    private String inStation;
    private String outRegion;
    private String outLine;
    private String outStation;


    public Rireki(){
    }


    public static Rireki parse(byte[] res, int off) {
        Rireki self = new Rireki();
        self.init(res, off);
        return self;
    }

    public Suika getSuika() {
        Suika data = new Suika(TERM_MAP.get(this.termId),
                                PROC_MAP.get(this.procId),
                                this.inRegion,
                                this.inLine,
                                this.inStation,
                                this.outRegion,
                                this.outLine,
                                this.outStation,
                                this.year + "/" + this.month + "/" + this.day,
                                this.remain);
        return data;
    }

    private void init(byte[] res, int off) {
        this.termId = res[off+0]; //0: 端末種
        this.procId = res[off+1]; //1: 処理
        //2-3: ??
        int mixInt = toInt(res, off, 4,5);
        this.year  = (mixInt >> 9) & 0x07f;
        this.month = (mixInt >> 5) & 0x00f;
        this.day   = mixInt & 0x01f;
        this.line = "JR";

        if (isBuppan(this.procId)) {
            this.kind = "物販";
        } else if (isBus(this.procId)) {
            this.kind = "バス";
        } else {
//            this.kind = res[off+6] < 0x80 ? "JR" : "公営/私鉄" ;
            this.kind = PROC_MAP.get(res[off + 1]);
        }
        this.line = res[off + 6] < 0x80 ? "JR" : "公営/私鉄";
        this.remain  = toInt(res, off, 11,10); //10-11: 残高 (little endian)
        this.seqNo   = toInt(res, off, 12,13,14); //12-14: 連番
        this.inRegion = String.valueOf((res[off + 15] >> 6) & 0x03);
        this.inLine = String.format("%02X", res[off + 6]);
        this.inStation = String.format("%02X",res[off + 7]);
        this.outRegion = String.valueOf((res[off + 15] >> 4) & 0x03);
        this.outLine = String.format("%02X", res[off + 8]);
        this.outStation = String.format("%02X", res[off + 9]);
    }

    private int toInt(byte[] res, int off, int... idx) {
        int num = 0;
        for (int i=0; i<idx.length; i++) {
            num = num << 8;
            num += ((int)res[off+idx[i]]) & 0x0ff;
        }
        return num;
    }
    private boolean isBuppan(int procId) {
        return procId == 70 || procId == 73 || procId == 74
                || procId == 75 || procId == 198 || procId == 203;
    }
    private boolean isBus(int procId) {
        return procId == 13|| procId == 15|| procId ==  31|| procId == 35;
    }

    public String toString() {
        String str = seqNo
                +" , "+TERM_MAP.get(termId)
                +" , "+ PROC_MAP.get(procId)
                +" , "+kind
                +" , "+line
                +" , "+year+"/"+month+"/"+day
                +" ,残："+remain+"円";
        return str;
    }

    public static final SparseArray<String> TERM_MAP = new SparseArray<String>();
    public static final SparseArray<String> PROC_MAP = new SparseArray<String>();
    static {
        TERM_MAP.put(3 , "精算機");
        TERM_MAP.put(4 , "携帯型端末");
        TERM_MAP.put(5 , "車載端末");
        TERM_MAP.put(7 , "券売機");
        TERM_MAP.put(8 , "券売機");
        TERM_MAP.put(9 , "入金機");
        TERM_MAP.put(18 , "券売機");
        TERM_MAP.put(20 , "券売機等");
        TERM_MAP.put(21 , "券売機等");
        TERM_MAP.put(22 , "改札機");
        TERM_MAP.put(23 , "簡易改札機");
        TERM_MAP.put(24 , "窓口端末");
        TERM_MAP.put(25 , "窓口端末");
        TERM_MAP.put(26 , "改札端末");
        TERM_MAP.put(27 , "携帯電話");
        TERM_MAP.put(28 , "乗継精算機");
        TERM_MAP.put(29 , "連絡改札機");
        TERM_MAP.put(31 , "簡易入金機");
        TERM_MAP.put(70 , "VIEW ALTTE");
        TERM_MAP.put(72 , "VIEW ALTTE");
        TERM_MAP.put(199 , "物販端末");
        TERM_MAP.put(200 , "自販機");

        PROC_MAP.put(1 , "運賃支払(改札出場)");
        PROC_MAP.put(2 , "チャージ");
        PROC_MAP.put(3 , "券購(磁気券購入)");
        PROC_MAP.put(4 , "精算");
        PROC_MAP.put(5 , "精算 (入場精算)");
        PROC_MAP.put(6 , "窓出 (改札窓口処理)");
        PROC_MAP.put(7 , "新規 (新規発行)");
        PROC_MAP.put(8 , "控除 (窓口控除)");
        PROC_MAP.put(13 , "バス (PiTaPa系)");
        PROC_MAP.put(15 , "バス (IruCa系)");
        PROC_MAP.put(17 , "再発 (再発行処理)");
        PROC_MAP.put(19 , "支払 (新幹線利用)");
        PROC_MAP.put(20 , "入A (入場時オートチャージ)");
        PROC_MAP.put(21 , "出A (出場時オートチャージ)");
        PROC_MAP.put(31 , "入金 (バスチャージ)");
        PROC_MAP.put(35 , "券購 (バス路面電車企画券購入)");
        PROC_MAP.put(70 , "物販");
        PROC_MAP.put(72 , "特典 (特典チャージ)");
        PROC_MAP.put(73 , "入金 (レジ入金)");
        PROC_MAP.put(74 , "物販取消");
        PROC_MAP.put(75 , "入物 (入場物販)");
        PROC_MAP.put(198 , "物現 (現金併用物販)");
        PROC_MAP.put(203 , "入物 (入場現金併用物販)");
        PROC_MAP.put(132 , "精算 (他社精算)");
        PROC_MAP.put(133 , "精算 (他社入場精算)");
    }
}
