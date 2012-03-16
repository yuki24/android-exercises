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
package net.kazzz.felica.lib;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.kazzz.felica.FeliCaException;
import net.kazzz.felica.IFeliCaByteData;
import net.kazzz.felica.command.IFeliCaCommand;
import net.kazzz.nfc.NfcException;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.NfcF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * FeliCa、FeliCa Liteデバイスにアクセスするためのコマンドとデータ操作をライブラリィとして提供します
 * 
 * <pre>
 * ※ 「FeliCa」は、ソニー株式会社が開発した非接触ICカードの技術方式です。
 * ※ 「FeliCa」、「FeliCa Lite」、「FeliCa Plug」、「FeliCaポケット」、「FeliCaランチャー」は、ソニー株式会社の登録商標です。
 * ※ 「Suica」は東日本旅客鉄道株式会社の登録商標です。
 * ※ 「PASMO」は、株式会社パスモの登録商標です。
 * 
 * 本ライブラリィはFeliCa、ソニー株式会社とはなんの関係もありません。
 * </pre>
 * 
 * @author Kazzz
 * @date 2011/03/04
 * @since Android API Level 10
 *
 */

public final class FeliCaLib {
    static final String TAG = "FeliCaLib";
    
    //polling
    public static final byte COMMAND_POLLING = 0x00;
    public static final byte RESPONSE_POLLING = 0x01;

    //request service
    public static final byte COMMAND_REQUEST_SERVICE = 0x02;
    public static final byte RESPONSE_REQUEST_SERVICE = 0x03;

    //request RESPONSE
    public static final byte COMMAND_REQUEST_RESPONSE = 0x04;
    public static final byte RESPONSE_REQUEST_RESPONSE = 0x05;

    //read without encryption
    public static final byte COMMAND_READ_WO_ENCRYPTION = 0x06;
    public static final byte RESPONSE_READ_WO_ENCRYPTION = 0x07;

    //write without encryption
    public static final byte COMMAND_WRITE_WO_ENCRYPTION = 0x08;
    public static final byte RESPONSE_WRITE_WO_ENCRYPTION = 0x09;

    //search service code
    public static final byte COMMAND_SEARCH_SERVICECODE = 0x0a;
    public static final byte RESPONSE_SEARCH_SERVICECODE = 0x0b;

    //request system code
    public static final byte COMMAND_REQUEST_SYSTEMCODE = 0x0c;
    public static final byte RESPONSE_REQUEST_SYSTEMCODE = 0x0d;

    //authentication 1
    public static final byte COMMAND_AUTHENTICATION1 = 0x10;
    public static final byte RESPONSE_AUTHENTICATION1 = 0x11;

    //authentication 2
    public static final byte COMMAND_AUTHENTICATION2 = 0x12;
    public static final byte RESPONSE_AUTHENTICATION2 = 0x13;

    //read
    public static final byte COMMAND_READ = 0x14;
    public static final byte RESPONSE_READ = 0x15;

    //write
    public static final byte COMMAND_WRITE = 0x16;
    public static final byte RESPONSE_WRITE = 0x17;

    // システムコード
    public static final int SYSTEMCODE_ANY = 0xffff;         // ANY
    public static final int SYSTEMCODE_FELICA_LITE = 0x88b4; // FeliCa Lite
    public static final int SYSTEMCODE_COMMON = 0xfe00;      // 共通領域
    public static final int SYSTEMCODE_CYBERNE = 0x0003;     // サイバネ領域
    public static final int SYSTEMCODE_EDY = 0xfe00;         // Edy (=共通領域)
    public static final int SYSTEMCODE_SUICA = 0x0003;       // Suica (=サイバネ領域)
    public static final int SYSTEMCODE_PASMO = 0x0003;       // Pasmo (=サイバネ領域)
    
    // サービスコード suica/pasmo (little endian)
    public static final int SERVICE_SUICA_INOUT = 0x108f;           // SUICA/PASMO 入退場記録
    public static final int SERVICE_SUICA_HISTORY = 0x090f;         // SUICA/PASMO履歴
    public static final int SERVICE_FELICA_LITE_READONLY = 0x0b00;  // FeliCa Lite RO権限 
    public static final int SERVICE_FELICA_LITE_READWRITE = 0x0900; // FeliCa Lite RW権限

    
    //アクセス属性 (サービスコードの下6ビット
    public static final int RANDOM_RW_AUTH = 0x08;   // ランダムサービス(リード/ライト:認証必要) 001000b
    public static final int RANDOM_RW_WOAUTH = 0x09; // ランダムサービス(リード/ライト:認証不要) 001001b
    public static final int RANDOM_RO_AUTH = 0x0a;   // ランダムサービス(リードオンリー:認証必要) 001010b
    public static final int RANDOM_RO_WOAUTH = 0x0b; // ランダムサービス(リードオンリー:認証不要) 001011b

    public static final int CYCLIC_RW_AUTH = 0x0c;   // サイクリックサービス(リード/ライト:認証必要) 001100b
    public static final int CYCLIC_RW_WOAUTH = 0x0d; // サイクリックサービス(リード/ライト:認証不要) 001101b
    public static final int CYCLIC_RO_AUTH = 0x0e;   // サイクリックサービス(リードオンリー:認証必要) 000111b
    public static final int CYCLIC_RO_WOAUTH = 0x0f; // サイクリックサービス(リードオンリー:認証不要) 001111b

    public static final int PARSE_DR_AUTH = 0x10;      // パースサービス(ダイレクト:認証必要) 010000b
    public static final int PARSE_DR_WOAUTH = 0x11;    // パースサービス(ダイレクト:認証不要) 010001b
    public static final int PARSE_CB_DEC_AUTH = 0x12;  // パースサービス(キャッシュバック/デクリメント:認証必要) 010010b
    public static final int PARSE_CB_DEC_WOAUTH = 0x13;// パースサービス(キャッシュバック/デクリメント:認証不要) 010011b
    public static final int PARSE_DEC_AUTH = 0x14;     // パースサービス(デクリメント:認証必要) 010100b
    public static final int PARSE_DEC_WOAUTH = 0x15;   // パースサービス(デクリメント:認証不要) 010101b
    public static final int PARSE_RO_AUTH = 0x16;      // パースサービス(リードオンリー:認証必要) 010100b
    public static final int PARSE_RO_WOAUTH = 0x17;    // パースサービス(リードオンリー:認証不要) 010101b
    

    public static final int STATUSFLAG1_NORMAL = 0x00; //正常終了 
    public static final int STATUSFLAG1_ERROR = 0xff;  //エラー　(ブロック番号に依らない)

    public static final int STATUSFLAG2_NORMAL = 0x00;          //正常終了
    public static final int STATUSFLAG2_ERROR_LENGTH    = 0x01; 
    public static final int STATUSFLAG2_ERROR_FLOWN     = 0x02; 
    public static final int STATUSFLAG2_ERROR_MEMORY    = 0x70; 
    public static final int STATUSFLAG2_ERROR_WRITELIMIT= 0x71; 
   
    public static final Map<Byte, String> commandMap = new HashMap<Byte, String>();
    
    //command code and name dictionary
    static {
        commandMap.put(COMMAND_POLLING, "Polling");
        commandMap.put(RESPONSE_POLLING, "Polling(responce)");
        commandMap.put(COMMAND_REQUEST_SERVICE, "Request Service");
        commandMap.put(RESPONSE_REQUEST_SERVICE, "Request Service(response)");
        commandMap.put(COMMAND_REQUEST_RESPONSE, "Request Response");
        commandMap.put(RESPONSE_REQUEST_RESPONSE, "Request Response(response)");
        commandMap.put(COMMAND_READ_WO_ENCRYPTION, "Read Without Encryption");
        commandMap.put(RESPONSE_READ_WO_ENCRYPTION, "Read Without Encryption(response)");
        commandMap.put(COMMAND_WRITE_WO_ENCRYPTION, "Write Without Encryption");
        commandMap.put(RESPONSE_WRITE_WO_ENCRYPTION, "Write Without Encryption(response)");
        commandMap.put(COMMAND_SEARCH_SERVICECODE, "Search Service");
        commandMap.put(RESPONSE_SEARCH_SERVICECODE, "Search Service(response)");
        commandMap.put(COMMAND_REQUEST_SYSTEMCODE, "Request System Code");
        commandMap.put(RESPONSE_REQUEST_SYSTEMCODE, "Request System Code(response)");
        commandMap.put(COMMAND_AUTHENTICATION1, "Authentication1");
        commandMap.put(RESPONSE_AUTHENTICATION1, "Authentication1(response)");
        commandMap.put(COMMAND_AUTHENTICATION2, "Authentication2");
        commandMap.put(RESPONSE_AUTHENTICATION2, "Authentication2(response)");
        commandMap.put(COMMAND_READ, "Read");
        commandMap.put(RESPONSE_READ, "Read(response)");
        commandMap.put(COMMAND_WRITE, "Write");
        commandMap.put(RESPONSE_WRITE, "Write(response)");
    }
    /**
     * 
     * FeliCa コマンドパケットクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class CommandPacket implements IFeliCaCommand {
        protected final int length;     //コマンド全体のデータ長 
        protected final byte commandCode;//コマンドコード
        protected final IDm  idm;        //FeliCa IDm
        protected final byte[] data;     //コマンドデータ
        /**
         * コンストラクタ
         * @param response 他のレスポンスをセット
         */
        public CommandPacket(CommandPacket command) throws FeliCaException {
            this(command.getBytes());
        }
        /**
         * コンストラクタ
         * 
         * @param data コマンドパケット全体を含むバイト列をセット
         * @throws FeliCaException 
         */
        public CommandPacket(final byte[] data) throws FeliCaException {
            this(data[0], Arrays.copyOfRange(data, 1, data.length));
        }
        /**
         * コンストラクタ
         * 
         * @param commandCode コマンドコードをセット
         * @param data コマンドデータをセット (IDmを含みます)
         * @throws FeliCaException 
         */
        public CommandPacket(byte commandCode, final byte... data) throws FeliCaException {
            if ( !commandMap.containsKey(commandCode))
                throw new FeliCaException("commandCode : " + commandCode + " not supported.");
            this.commandCode = commandCode;
            if ( data.length >= 8 ) {
                this.idm = new IDm(Arrays.copyOfRange(data, 0, 8));
                this.data = Arrays.copyOfRange(data, 8, data.length);
            } else {
                this.idm = null;
                this.data = Arrays.copyOfRange(data, 0, data.length);
            }
            this.length = data.length + 2;
            
            if ( this.length > 255 )
                throw new FeliCaException("command data too long (less than 255Byte)");        
        }
        /**
         * コンストラクタ
         * 
         * @param commandCode コマンドコードをセット
         * @param idm システム製造ID(IDm)をセット
         * @param data コマンドデータをセット
         * @throws FeliCaException 
         */
        public CommandPacket(byte commandCode, IDm idm, final byte... data) throws FeliCaException {
            if ( !commandMap.containsKey(commandCode))
                throw new FeliCaException("commandCode : " + commandCode + " not supported.");
            this.commandCode = commandCode;
            this.idm = idm;
            this.data = data;
            this.length = idm.getBytes().length + data.length + 2;
            if ( this.length > 255 )
                throw new FeliCaException("command data too long (less than 255byte)");        
        }
        /**
         * コンストラクタ
         * 
         * @param commandCode コマンドコードをセット
         * @param idm システム製造ID(IDm)がセットされたバイト配列をセット
         * @param data コマンドデータをセット
         * @throws FeliCaException 
         */
        public CommandPacket(byte commandCode, byte[] idm, final byte... data) throws FeliCaException {
            if ( !commandMap.containsKey(commandCode))
                throw new FeliCaException("commandCode : " + commandCode + " not supported.");
            this.commandCode = commandCode;
            this.idm = new IDm(idm);
            this.data = data;
            this.length = idm.length + data.length + 2;
            if ( this.length > 255 )
                throw new FeliCaException("command data too long (less than 255byte)");        
        }
        
        /* (non-Javadoc)
         * @see net.felica.IFeliCaCommand#getIDm()
         */
        @Override
        public IDm getIDm() {
            return this.idm;
        }
        /**
         * バイト列表現を戻します
         * @return byte[] このデータのバイト列表現を戻します
         */
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(this.length);
            byte length = (byte)this.length;
            if ( this.idm != null ) {
                buff.put(length).put(this.commandCode).put(this.idm.getBytes()).put(this.data);
            } else {
                buff.put(length).put(this.commandCode).put(this.data);
            }
            return buff.array();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
           StringBuilder sb = new StringBuilder();
           sb.append("FeliCa コマンドパケット \n");
           sb.append(" コマンド名:" + commandMap.get(this.commandCode)  +  "\n");
           sb.append(" データ長: " + Util.getHexString((byte)(this.length & 0xff)) + "\n");
           sb.append(" コマンドコード : " + Util.getHexString(this.commandCode) +  "\n");
           if ( this.idm != null )
               sb.append(" " + this.idm.toString() + "\n");
           sb.append(" データ: " + Util.getHexString(this.data) + "\n");
           return sb.toString();
        }

    }
    /**
     * FeliCa コマンドレスポンスクラスを提供します
     * 
     * @author Kazz
     * @since Android API Level 9
     */
    public static class CommandResponse implements IFeliCaCommand {
        protected final byte[] rawData;
        protected final int length;      //全体のデータ長 (FeliCaには無い)
        protected final byte responseCode;//コマンドレスポンスコード)
        protected final IDm idm;          //FeliCa IDm
        protected final byte[] data;      //コマンドデータ
        
        /**
         * コンストラクタ
         * @param response 他のレスポンスをセット
         */
        public CommandResponse(CommandResponse response) {
            this(response != null ? response.getBytes() : null);
        }
        /**
         * コンストラクタ
         * 
         * @param data コマンド実行結果で戻ったバイト列をセット
         */
        public CommandResponse(byte[] data) {
            if ( data != null ) {
                this.rawData = data;
                this.length = data[0] & 0xff; 
                this.responseCode = data[1];
                this.idm = new IDm(Arrays.copyOfRange(data, 2, 10));
                this.data = Arrays.copyOfRange(data, 10, data.length);
            } else {
                this.rawData = null;
                this.length = 0; 
                this.responseCode = 0;
                this.idm = null;
                this.data = null;
            }
        }
        /* (non-Javadoc)
         * @see net.felica.IFeliCaCommand#getIDm()
         */
        @Override
        public IDm getIDm() {
            return this.idm;
        }
        /**
         * バイト列表現を戻します
         * @return byte[] このデータのバイト列表現を戻します
         */
        public byte[] getBytes() {
            return this.rawData;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
           StringBuilder sb = new StringBuilder();
           sb.append(" \n\n");
           sb.append("FeliCa レスポンスパケット \n");
           sb.append(" コマンド名:" + commandMap.get(this.responseCode)  +  "\n");
           sb.append(" データ長: " + Util.getHexString((byte)(this.length & 0xff)) + "\n");
           sb.append(" レスポンスコード: " + Util.getHexString(this.responseCode) + "\n");
           sb.append(" "+ this.idm.toString() + "\n");
           sb.append(" データ: " + Util.getHexString(this.data) + "\n");
           return sb.toString();
        }      
    }
    /**
     * 
     * FeliCa IDmクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class IDm implements Parcelable, IFeliCaByteData {
        /** Parcelable need CREATOR field **/ 
        public static final Parcelable.Creator<IDm> CREATOR = 
            new Parcelable.Creator<IDm>() {
                public IDm createFromParcel(Parcel in) {
                    return new IDm(in);
                }
                
                public IDm[] newArray(int size) {
                    return new IDm[size];
                }
            };
        final byte[] manufactureCode;
        final byte[] cardIdentification;
        /**
         * コンストラクタ
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public IDm(Parcel in) {
            this.manufactureCode = new byte[in.readInt()];
            in.readByteArray(this.manufactureCode);
            this.cardIdentification = new byte[in.readInt()];
            in.readByteArray(this.cardIdentification);
        }
        /**
         * コンストラクタ 
         * @param bytes IDmの格納されているバイト列をセットします
         */
        public IDm(byte[] bytes) {
            this.manufactureCode = new byte[]{bytes[0], bytes[1]};
            this.cardIdentification = 
                new byte[]{bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]};
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
            //配列長を先に書きだしておく
            dest.writeInt(this.manufactureCode.length);
            dest.writeByteArray(this.manufactureCode);
            
            //配列長を先に書きだしておく
            dest.writeInt(this.cardIdentification.length);
            dest.writeByteArray(this.cardIdentification);
        }
        /* (non-Javadoc)
         * @see net.felica.IFeliCaByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(
                    this.manufactureCode.length + this.cardIdentification.length);
            buff.put(this.manufactureCode).put(this.cardIdentification);
            return buff.array();
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("IDm (8byte) : " + Util.getHexString(this.getBytes()) + "\n");
            sb.append(" 製造者コード: " + Util.getHexString(this.manufactureCode) + "\n");
            sb.append(" カード識別番号:\n");
            sb.append("   製造器:" + Util.getHexString(this.cardIdentification, 0, 2) + "\n");
            sb.append("   日付:" + Util.getHexString(this.cardIdentification, 2, 2) + "\n");
            sb.append("   シリアル:" + Util.getHexString(this.cardIdentification, 4, 2) + "\n");
            return sb.toString();
        }

    }
    /**
     * 
     * FeliCa PMmクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class PMm implements Parcelable, IFeliCaByteData {
        /** Parcelable need CREATOR field **/ 
        public static final Parcelable.Creator<PMm> CREATOR = 
            new Parcelable.Creator<PMm>() {
                public PMm createFromParcel(Parcel in) {
                    return new PMm(in);
                }
                
                public PMm[] newArray(int size) {
                    return new PMm[size];
                }
            };
        final byte[] icCode;              // ROM種別, IC種別
        final byte[] maximumResponseTime; // 最大応答時間
        /**
         * コンストラクタ
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public PMm(Parcel in) {
            this.icCode = new byte[in.readInt()];
            in.readByteArray(this.icCode);
            
            this.maximumResponseTime = new byte[in.readInt()];
            in.readByteArray(this.maximumResponseTime);
        }
       /**
         * コンストラクタ
         * @param bytes バイト列をセット
         */
        public PMm(byte[] bytes) {
            this.icCode = new byte[]{bytes[0], bytes[1]};
            this.maximumResponseTime = 
                new byte[]{bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]};
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
            //配列長を先に書きだす
            dest.writeInt(this.icCode.length);
            dest.writeByteArray(this.icCode);

            //配列長を先に書きだす
            dest.writeInt(this.maximumResponseTime.length);
            dest.writeByteArray(this.maximumResponseTime);
        }
        /* (non-Javadoc)
         * @see net.felica.IFeliCaByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(
                    this.icCode.length + this.maximumResponseTime.length);
            buff.put(this.icCode).put(this.maximumResponseTime);
            return buff.array();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PMm(製造パラメータ)\n");
            sb.append(" ICコード(2byte): " + Util.getHexString(this.icCode) + "\n");
            sb.append("   ROM種別: " + Util.getHexString(this.icCode, 0, 1) + "\n");
            sb.append("   IC 種別: " + Util.getHexString(this.icCode, 1, 1) + "\n");
            sb.append("\n");
            sb.append(" 最大応答時間パラメタ(6byte)\n");
            sb.append("  B3(request service):" + Util.getBinString(this.maximumResponseTime, 0, 1) + "\n");
            sb.append("  B4(request response):" + Util.getBinString(this.maximumResponseTime, 1, 1) + "\n");
            sb.append("  B5(authenticate):" + Util.getBinString(this.maximumResponseTime, 2, 1) + "\n");
            sb.append("  B6(read):" + Util.getBinString(this.maximumResponseTime, 3, 1) + "\n");
            sb.append("  B7(write):" + Util.getBinString(this.maximumResponseTime, 4, 1) + "\n");
            sb.append("  B8():" + Util.getBinString(this.maximumResponseTime, 5, 1) + "\n");
            return sb.toString();
        }
    }
    
   /**
    * FeliCa SystemCodeクラスを提供します
    * 
    * @author Kazzz
    * @date 2011/01/20
    * @since Android API Level 9
    */
    public static class SystemCode implements IFeliCaByteData {
        final byte[] systemCode;
        /**
         * コンストラクタ
         * @param bytes バイト列をセット
         */
        public SystemCode(byte[] bytes) {
            this.systemCode = bytes;
        }
        /* (non-Javadoc)
         * @see net.felica.IFeliCaByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            return this.systemCode;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("システムコード : " + Util.getHexString(this.systemCode) + "\n");
            return sb.toString();
        }
    }
    /**
     * FeliCa ServiceCodeクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
     public static class ServiceCode {
         final byte[] serviceCode;
         final byte[] serviceCodeLE; // little endian
         /**
          * コンストラクタ
          * @param bytes バイト列をセット
          */
         public ServiceCode(byte[] bytes) {
             this.serviceCode = bytes;
             if (bytes.length == 2) {
                 this.serviceCodeLE = new byte[] {bytes[1], bytes[0]};
             } else {
                 this.serviceCodeLE = null;
             }
         }
         public ServiceCode(int serviceCode) {
             this(new byte[]{(byte) (serviceCode & 0xff), (byte) (serviceCode >> 8)});
         }
         
         /* 
          * サービスコードをバイト列として返します。
          * @return サービスコードのバイト列表現
          */
         public byte[] getBytes() {
             return this.serviceCode;
         }
         /**
          * このサービスコードは、認証が必要か否かを検査します
          * @return boolean 認証が必要ならTrueが戻ります
          */
         public boolean encryptNeeded() {
             boolean ret = false;
             if (serviceCodeLE != null) {
                 ret = (serviceCodeLE[1] & 0x1) == 0;
             }
             return ret;
         }
         
         /**
          * このサービスコードは書込み可能か否かを検査します
          * @return boolean 書込み可能ならTrueが戻ります
          */
         public boolean isWritable() {
             boolean ret = false;
             if (serviceCodeLE != null) {
                 int accessInfo = serviceCodeLE[1] & 0x3F; // 下位6bitがアクセス情報
                 ret = (accessInfo & 0x2) == 0 || accessInfo == 0x13 || accessInfo==0x12; 
             }
             return ret;
         }
         
         /** 
          * サービスコードのアクセス権の意味は、JIS_X_6319_4 を参照しました。
          * @author morishita_2
          */
         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(Util.getHexString(serviceCodeLE));
             if (serviceCodeLE != null) {
                 int accessInfo = serviceCodeLE[1] & 0x3F; // 下位6bitがアクセス情報
                 switch (accessInfo) {
                 case 0x09: sb.append(" 固定長RW"); break; // RW: ReadWrite
                 case 0x0b: sb.append(" 固定長RO"); break; // RO: ReadOnly
                 case 0x0d: sb.append(" 循環RW"); break;
                 case 0x0f: sb.append(" 循環RO"); break;
                 case 0x11: sb.append(" 加減算直接"); break;
                 case 0x13: sb.append(" 加減算戻入"); break;
                 case 0x15: sb.append(" 加減算減算"); break;
                 case 0x17: sb.append(" 加減算RO"); break;
                 //
                 case 0x08: sb.append(" 固定長RW(Locked)"); break; // RW: ReadWrite
                 case 0x0a: sb.append(" 固定長RO(Locked)"); break; // RO: ReadOnly
                 case 0x0c: sb.append(" 循環RW(Locked)"); break;
                 case 0x0e: sb.append(" 循環RO(Locked)"); break;
                 case 0x10: sb.append(" 加減算直接(Locked)"); break;
                 case 0x12: sb.append(" 加減算戻入(Locked)"); break;
                 case 0x14: sb.append(" 加減算減算(Locked)"); break;
                 case 0x16: sb.append(" 加減算RO(Locked)"); break;
                 }
                 
             }
             //sb.append("\n");
             return sb.toString();
         }
     }
    
    /**
     * 
     * Felica FileSystemにおけるService(サービス)クラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class Service implements IFeliCaByteData {
        final ServiceCode[] serviceCodes;
        final BlockListElement[] blockListElements;
        /**
         * コンストラクタ
         * 
         * @param serviceCode サービスコードの配列をセット
         * @param blockListElements ブロックリストエレメントの配列をセット
         */
        public Service(ServiceCode[] serviceCodes, BlockListElement ... blockListElements ) {
            this.serviceCodes = serviceCodes;
            this.blockListElements = blockListElements;
        }
        /* (non-Javadoc)
         * @see net.felica.IFeliCaByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {

            int length = 0;
            for (ServiceCode s : this.serviceCodes ) {
                length += s.getBytes().length;
            }
            
            for (BlockListElement b : blockListElements) {
                length += b.getBytes().length;
            }
            
            ByteBuffer buff = ByteBuffer.allocate(length);
            for (ServiceCode s : this.serviceCodes ) {
                buff.put(s.getBytes());
            }
            
            for (BlockListElement b : blockListElements) {
                buff.put(b.getBytes());
            }
            
            return buff.array();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (ServiceCode s : this.serviceCodes ) {
                sb.append(s.toString());
            }
            
            for (BlockListElement b : blockListElements) {
                sb.append(b.toString());
            }
            return sb.toString();
        }
    }
    
   
    /**
     * FeliCa FileSystemにおけるBlock(ブロック)を抽象化したクラス提供します
     * @author Kazzz
     * @date 2011/2/20
     * @since Android API Level 9
     */
    public static class Block implements IFeliCaByteData {
        final byte[] data;
        /**
         * デフォルトコンストラクタ
         */
        public Block() {
            this.data = new byte[16];
        }
        /**
         * コンストラクタ
         * @param data ブロックを構成する
         */
        public Block(byte[] data) {
            this.data = data;
        }
        /* (non-Javadoc)
         * @see net.felica.IFeliCaByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            return this.data;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ブロック : " + Util.getHexString(this.data) + "\n");
            return sb.toString();
        }  
    }
    
    /**
     * Felica FileSystemにおけるBlockListElement(2byte又は3byte)クラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class BlockListElement implements IFeliCaByteData {
        public static final byte LENGTH_2_BYTE = (byte) 0x80;
        public static final byte LENGTH_3_BYTE = (byte) 0x00; 
        public static final byte ACCESSMODE_DECREMENT = 0x00; 
        public static final byte ACCESSMODE_CACHEBACK = 0x01; 
        final byte lengthAndaccessMode; // 
        final byte serviceCodeListOrder; // 
        final byte[] blockNumber;
        
        /**
         * コンストラクタ
         * @param accessMode アクセスモードを0又は1でセット
         * @param serviceCodeListOrder サービスコードリスト順をセット
         * @param blockNumber 対象のブロック番号を1バイト又は2バイトでセット
         */
        public BlockListElement (byte accessMode, byte serviceCodeListOrder, byte... blockNumber ) {
            if ( blockNumber.length > 1 ) {
                this.lengthAndaccessMode =  (byte)(accessMode | LENGTH_2_BYTE & 0xFF);
            } else {
                this.lengthAndaccessMode =  (byte)(accessMode | LENGTH_3_BYTE & 0xFF);
            }
            this.serviceCodeListOrder = (byte) (serviceCodeListOrder & 0x0F);
            this.blockNumber = blockNumber;
        }
        /* (non-Javadoc)
         * @see net.felica.IFeliCaByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            if ( (this.lengthAndaccessMode & LENGTH_2_BYTE) == 1 ) {
                ByteBuffer buff = ByteBuffer.allocate(2);
                buff.put( (byte)
                        ((this.lengthAndaccessMode | this.serviceCodeListOrder) & 0xFF))
                    .put(this.blockNumber[0]);
                return buff.array();
            } else {
                ByteBuffer buff = ByteBuffer.allocate(3);
                buff.put( (byte)
                        ((this.lengthAndaccessMode | this.serviceCodeListOrder) & 0xFF))
                    .put(this.blockNumber[1])
                    .put(this.blockNumber[0]); //little endian
                return buff.array();
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ブロックリストエレメント\n");
            sb.append("  データ長 : " + this.getBytes().length + " byte\n");
            sb.append("  アクセスモード        : " + Util.getBinString((byte)(this.lengthAndaccessMode & 0x8F)) + "\n");
            sb.append("  サービスコードリスト順: " + Util.getHexString(this.serviceCodeListOrder) + "\n");
            sb.append("  ブロックナンバー      : " + Util.getHexString(this.blockNumber) + "\n");
            return sb.toString();
        }   
    }
    /**
     * FeliCa Liteで使用されるメモリコンフィグレーションブロック(16byte)を抽象化したクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/02/21
     * @since Android API Level 9
     *
     */
    public static class MemoryConfigurationBlock extends Block implements IFeliCaByteData {
        /**
         * コンストラクタ
         * @param mcData MC領域のデータブロック(16バイト)をセット
         */
        public MemoryConfigurationBlock (byte[] mcData) {
            super(mcData);
        }
        /**
         * NDEFをサポートするか否かを検査します
         * @return boolean NDEFをサポートしている場合trueが戻ります
         */
        public boolean isNdefSupport() {
            if ( this.data == null ) return false;
            return ( this.data[3] & (byte)0xff ) == 1; 
        }
        /**
         * Ndefをサポートするか否かを設定します
         * @param ndefSupport Ndefをサポートする場合はtrueをセットします
         */
        public void setNdefSupport(boolean ndefSupport) {
            this.data[3] = (byte) (ndefSupport ? 1 : 0);
        }
        /**
         * ブロック中の領域 (0x00h～0x0fh)が書きこみ可能な否かを検査します
         * 
         * @param addr 調べたいブロック番号へのアドレスをセット (複数セットした場合はand演算されます)
         * @return　書き込み可能な場合にはtrueが戻ります
         */
        public boolean isWritable(int... addrs) {
            if ( this.data == null ) return false;
            
            boolean result = true;
            for ( int a : addrs ) {
                byte b = (byte) ((a & 0xff) + 1);
                if ( a < 8 ) {
                    result &= (this.data[0] & b ) == b;
                    continue;
                } else 
                if ( a < 16 ) {
                    result &= (this.data[1] & b ) == b;
                    continue;
                } else 
                result &= (this.data[2] & b ) == b;
            }
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("メモリコンフィグレーションブロック(MC)\n");
            sb.append("  NdefSupport  : " + this.isNdefSupport() + "\n");
            sb.append("  MemoryConfig : \n");
            for ( int i = 0; i < this.data.length; i++ ) {
                sb.append("    ブロック  " + i + " = "  
                        + (this.isWritable(i) ? "1:RW" : "0:RO") + "\n");
            }
            return sb.toString();
        }   
        
    }
    /**
     * コマンドを実行します
     *
     * @param Tag Tagクラスの参照をセットします
     * @param commandPacket 実行するコマンドパケットをセットします
     * @return CommandResponse コマンドの実行結果が戻ります 
     * @throws FeliCaException コマンドの発行に失敗した場合にスローされます
     */
    public static final CommandResponse execute(Tag tag, CommandPacket commandPacket) throws FeliCaException {
        byte[] result = executeRaw(tag, commandPacket.getBytes());
        return new CommandResponse(result);
    }
    /**
     * Rawデータを使ってコマンドを実行します
     * 
     * @param Tag Tagクラスの参照をセットします
     * @param data コマンドにセットするデータをセットします
     * @return byte[] コマンドの実行結果バイト列で戻ります 
     * @throws FeliCaException コマンドの発行に失敗した場合にスローされます
     */
    public static final byte[] executeRaw(Tag tag, byte[] data) throws FeliCaException {
        try {
            return transceive(tag, data);
        } catch (NfcException e) {
            throw new FeliCaException(e);
        }
    }
    /**
     * INfcTag#transceiveを実行します
     * 
     * @param Tag Tagクラスの参照をセットします
     * @param commandPacket 実行するコマンドパケットをセットします
     * @return byte[] コマンドの実行結果バイト列で戻ります 
     * @throws FeliCaException コマンドの発行に失敗した場合にスローされます
     */
    public static final byte[] transceive(Tag tag, byte[] data) throws NfcException {
        //NfcFはFeliCa
        NfcF nfcF = NfcF.get(tag);
        if ( nfcF == null ) throw new NfcException("tag is not FeliCa(NFC-F) ");
        try {
            nfcF.connect();
            try {
                return nfcF.transceive(data);
            } finally {
                nfcF.close();
            }
        } catch (TagLostException e) {
            return null; //Tag Lost
        } catch (IOException e) {
            throw new NfcException(e);
        }
    }
    
}
