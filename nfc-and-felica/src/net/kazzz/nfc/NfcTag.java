/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kazzz.nfc;

import net.kazzz.felica.lib.Util;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * NFC規格で使用するタグを抽象化したクラス提供します
 * 
 * @author Kazzz, k_morishita
 * @date 2011/02/19
 * @since Android API Level 9
 *
 */
public class NfcTag implements Parcelable {
    //private static String TAG = "NfcTag";
    public static final String ANDROID_NFC_EXTRA_TAG = "android.nfc.extra.TAG";
    
    public static final Parcelable.Creator<NfcTag> CREATOR = 
        new Parcelable.Creator<NfcTag>() {
            public NfcTag createFromParcel(Parcel in) {
                return new NfcTag(in);
            }
            
            public NfcTag[] newArray(int size) {
                return new NfcTag[size];
            }
        };
    
    protected byte[] idbytes;
    protected Parcelable nfcTag;

    /**
     * デフォルトコンストラクタ
     */
    public NfcTag() {
    }
    
    /**
     * コンストラクタ
     * @param in 
     */
    public NfcTag(Parcel in) {
        this();
        this.readFromParcel(in);
    }
    /**
     * コンストラクタ
     * 
     * @param nfcTag NfcTagをセット
     * @param id タグを識別するバイト列をセット
     */
    public NfcTag(Parcelable nfcTag, byte[] id) {
        this();
        this.nfcTag = nfcTag;
        this.idbytes = id;
    }
    /**
     * Nfcタグを取得します
     * @return Parcelable 内部に格納したNfcTagが戻ります
     */
    public Parcelable getNfcTag() {
        return nfcTag;
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idbytes.length);
        dest.writeByteArray(this.idbytes);
        dest.writeParcelable(this.nfcTag, 0);
    }
    /**
     * Parcel内からインスタンスを構成します 
     * @param source パーセルオブジェクトをセット
     */
    public void readFromParcel(Parcel source) {
        this.idbytes = new byte[source.readInt()];
        source.readByteArray(this.idbytes);
        this.nfcTag = source.readParcelable(this.getClass().getClassLoader());
    }
    /**
     * インテントをタグ情報をセットします
     * @param intent インテントをセット
     */
    public void putTagService(Intent intent) {
        intent.putExtra(ANDROID_NFC_EXTRA_TAG, this.nfcTag);
    }
    /**
     * IDを取得します
     * @return byte[] IDが戻ります
     */
    public byte[] getId() {
        return idbytes;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("NfcTag \n");
       sb.append(" idbytes:" + Util.getHexString(this.idbytes)  +  "\n");
       sb.append(" nfcTag: " + this.nfcTag.toString() + "\n");
       return sb.toString();
    }
    
}
