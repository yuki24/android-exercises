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
package net.kazzz.felica;

import static net.kazzz.felica.lib.FeliCaLib.COMMAND_POLLING;
import static net.kazzz.felica.lib.FeliCaLib.COMMAND_READ_WO_ENCRYPTION;
import static net.kazzz.felica.lib.FeliCaLib.COMMAND_REQUEST_SYSTEMCODE;
import static net.kazzz.felica.lib.FeliCaLib.COMMAND_SEARCH_SERVICECODE;
import static net.kazzz.felica.lib.FeliCaLib.COMMAND_WRITE_WO_ENCRYPTION;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kazzz.felica.command.PollingResponse;
import net.kazzz.felica.command.ReadResponse;
import net.kazzz.felica.command.WriteResponse;
import net.kazzz.felica.lib.FeliCaLib;
import net.kazzz.felica.lib.FeliCaLib.CommandPacket;
import net.kazzz.felica.lib.FeliCaLib.CommandResponse;
import net.kazzz.felica.lib.FeliCaLib.IDm;
import net.kazzz.felica.lib.FeliCaLib.PMm;
import net.kazzz.felica.lib.FeliCaLib.ServiceCode;
import net.kazzz.felica.lib.FeliCaLib.SystemCode;
import net.kazzz.nfc.NfcException;
import net.kazzz.nfc.NfcTag;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * FeliCa仕様に準拠した FeliCaタグクラスを提供します
 * 
 * @author Kazzz
 * @date 2011/01/23
 * @since Android API Level 9
 *
 */

public class FeliCaTag extends NfcTag {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<FeliCaTag> CREATOR = 
        new Parcelable.Creator<FeliCaTag>() {
            public FeliCaTag createFromParcel(Parcel in) {
                return new FeliCaTag(in);
            }
            
            public FeliCaTag[] newArray(int size) {
                return new FeliCaTag[size];
            }
        };

    protected Tag nfcTag;
    protected IDm idm;
    protected PMm pmm;
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public FeliCaTag(Parcel in) {
        this.readFromParcel(in);
    }
    /**
     * コンストラクタ
     * 
     * @param nfcTag NFCTagへの参照をセット
     */
    public FeliCaTag(Parcelable nfcTag) {
        this.nfcTag =  (Tag)nfcTag;
    }
    /**
     * コンストラクタ
     * 
     * @param nfcTag NFCTagへの参照をセット
     * @param idm FeliCa IDmをセット
     * @param pmm FeliCa PMmをセット
     */
    public FeliCaTag(Tag nfcTag, IDm idm, PMm pmm) {
        this.nfcTag =  nfcTag;
        this.idm = idm;
        this.pmm = pmm;
    }
    
    
    /* (non-Javadoc)
     * @see net.kazzz.nfc.NfcTag#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.nfcTag, 0);
        dest.writeParcelable(this.idm, 0);
        dest.writeParcelable(this.pmm, 0);
    }
    /* (non-Javadoc)
     * @see net.kazzz.nfc.NfcTag#readFromParcel(android.os.Parcel)
     */
    @Override
    public void readFromParcel(Parcel source) {
        ClassLoader cl = this.getClass().getClassLoader();
        this.nfcTag = source.readParcelable(cl);
        this.idm = source.readParcelable(cl);
        this.pmm = source.readParcelable(cl);
    }
    /**
     * カードデータをポーリングします
     * 
     * @param systemCode 対象のシステムコードをセットします 
     * @return　byte[] システムコードの配列が戻ります
     * @throws FeliCaException
     */
    public byte[] polling(int systemCode) throws FeliCaException {
        if ( this.nfcTag == null ) {
            throw new FeliCaException("tagService is null. no polling execution");
        }
        CommandPacket polling = 
            new CommandPacket(COMMAND_POLLING
                    , new byte[] {
                      (byte) (systemCode >> 8)  // システムコード
                    , (byte) (systemCode & 0xff)
                    , (byte) 0x01              //　システムコードリクエスト
                    , (byte) 0x00});           // タイムスロット}; 
        CommandResponse r = FeliCaLib.execute(this.nfcTag, polling);
        PollingResponse pr = new PollingResponse(r);
        this.idm = pr.getIDm();
        this.pmm = pr.getPMm();
        return pr.getBytes();
    }
    /**
     * カードデータをポーリングしてIDmを取得します
     * 
     * @param systemCode 対象のシステムコードをセットします 
     * @return　IDm IDmが戻ります
     * @throws FeliCaException
     */
    public IDm pollingAndGetIDm(int systemCode) throws FeliCaException {
        this.polling(systemCode);
        return this.idm;
    }
    /**
     * FeliCa IDmを取得します
     * @return IDm IDmが戻ります
     * @throws FeliCaException
     */
    public IDm getIDm() throws FeliCaException {
        return this.idm;
    }
    /**
     * FeliCa PMmを取得します
     * @return PMm PMmが戻ります
     * @throws FeliCaException
     */
    public PMm getPMm() throws FeliCaException {
        return this.pmm;
    }
    /**
     * SystemCodeの一覧を取得します。
     * 
     * @return SystemCode[] 検出された SystemCodeの一覧を返します。
     * @throws NfcException
     */
    public final SystemCode[] getSystemCodeList() throws FeliCaException {
        //request systemCode 
        CommandPacket reqSystemCode = new CommandPacket(COMMAND_REQUEST_SYSTEMCODE, idm);
        CommandResponse r = FeliCaLib.execute(this.nfcTag, reqSystemCode);
        byte[] retBytes = r.getBytes();
        int num = (int)retBytes[10];
        //Log.d(TAG, "Num SystemCode: " + num);
        SystemCode retCodeList[] = new SystemCode[num];
        for (int i=0; i < num; i++) {
            retCodeList[i] = new SystemCode(Arrays.copyOfRange(retBytes, 11+i*2, 13+i*2));
        }
        return retCodeList;
    }
    /**
     * Polling済みシステム領域のサービスの一覧を取得します。
     * 
     * @return ServiceCode[] 検出された ServiceCodeの配列
     * @throws NfcException
     */
    public ServiceCode[] getServiceCodeList() throws FeliCaException {
        int index = 1; // 0番目は root areaなので1オリジンで開始する
        List<ServiceCode> serviceCodeList = new ArrayList<ServiceCode>();
        while (true) {
            byte[] bytes = doSearchServiceCode(index); // 1件1件 通信して聞き出します。
            if (bytes.length != 2 && bytes.length != 4) break; // 2 or 4 バイトじゃない場合は、とりあえず終了しておきます。正しい判定ではないかもしれません。
            if (bytes.length == 2) { // 2バイトは ServiceCode として扱っています。
                if (bytes[0] == (byte)0xff && bytes[1] == (byte)0xff) break; // FFFF が終了コードのようです
                serviceCodeList.add(new ServiceCode(bytes));
            }
            index++;
        }
        return serviceCodeList.toArray(new ServiceCode[serviceCodeList.size()]);
    }
    /**
     * COMMAND_SEARCH_SERVICECODE を実行します。
     * 参考: http://wiki.osdev.info/index.php?PaSoRi%2FRC-S320#content_1_25
     * @param index ？番目か
     * @return Response部分
     * @throws FeliCaException
     */
    protected byte[] doSearchServiceCode(int index) throws FeliCaException {
        CommandPacket reqServiceCode =
            new CommandPacket(COMMAND_SEARCH_SERVICECODE, idm
                    , new byte[]{(byte)(index & 0xff), (byte)(index >> 8)});
        CommandResponse r = FeliCaLib.execute(this.nfcTag, reqServiceCode);
        byte[] bytes = r.getBytes();
        if (bytes == null || bytes.length <= 0 || bytes[1] != (byte)0x0b) { // 正常応答かどうか
            throw new FeliCaException("ResponseCode is not 0x0b");
        }
        return Arrays.copyOfRange(bytes, 10, bytes.length);
    }   
    /**
     * 認証不要領域のデータを読み込みます
     * 
     * @param serviceCode サービスコードをセット
     * @param addr 読み込むブロックのアドレス (0オリジン)をセット
     * @return ReadResponse 読み込んだ結果が戻ります
     * @throws FeliCaException
     */
    public ReadResponse readWithoutEncryption(ServiceCode serviceCode,
            byte addr) throws FeliCaException {
        if ( this.nfcTag == null ) {
            throw new FeliCaException("tagService is null. no read execution");
        }
        // read without encryption
        byte[] bytes = serviceCode.getBytes();
        CommandPacket readWoEncrypt = 
            new CommandPacket(COMMAND_READ_WO_ENCRYPTION, idm
                ,  new byte[]{(byte) 0x01         // サービス数
                    , (byte) bytes[0]             // サービスコード (little endian)
                    , (byte) bytes[1]
                    , (byte) 0x01                 // 同時読み込みブロック数
                    , (byte) 0x80, addr });       // ブロックリスト
        CommandResponse r = FeliCaLib.execute(this.nfcTag, readWoEncrypt);
        return new ReadResponse(r); 
    }
    /**
     * 認証不要領域のデータを書き込みます
     * 
     * @param serviceCode サービスコードをセット
     * @param addr データをセットするブロックのアドレス(0オリジン)をセット
     * @param buff 書きこむデータをセット (16バイト)
     * @return WriteResponse 書き込んだ結果レスポンスオブジェクトが戻ります
     * @throws FeliCaException
     */
    public WriteResponse writeWithoutEncryption(ServiceCode serviceCode,
            byte addr, byte[] buff) throws FeliCaException {
        if ( this.nfcTag == null ) {
            throw new FeliCaException("tagService is null. no write execution");
        }
        // write without encryption
        byte[] bytes = serviceCode.getBytes();
        ByteBuffer b =  ByteBuffer.allocate(22); // コマンド 6バイト + 書きだすデータ 16バイト
        b.put(new byte[]{(byte) 0x01             // Number of Service
                , (byte) bytes[0]                // サービスコード (little endian)
                , (byte) bytes[1]
                , (byte) 0x01                    // 同時書き込みブロック数
                , (byte) 0x80, (byte) addr       // ブロックリスト 0x80は (2バイトブロックエレメント+ランダムサービス)
                });
        b.put(buff, 0, buff.length > 16 ? 16 : buff.length); //書き出すデータ  (一度につき16バイト)
        CommandPacket writeWoEncrypt = 
            new CommandPacket(COMMAND_WRITE_WO_ENCRYPTION, idm, b.array());
        CommandResponse r = FeliCaLib.execute(this.nfcTag, writeWoEncrypt);
        return new WriteResponse(r);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("FeliCaTag \n");
       if ( this.idm != null ) 
           sb.append(this.idm.toString()).append("\n");
       if ( this.pmm != null ) 
           sb.append(this.pmm.toString()).append("\n");
       return sb.toString();
    }
    
}
