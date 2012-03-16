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

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 各種ユーティリティを提供します
 * 
 * @author Kazzz
 * @date 2011/01/24
 * @since Android API Level 4
 *
 */

public final class Util {
    private Util() {}
    /**
     * intをバイト配列にします。
     * 
     * @param a 整数をセット
     * @return byte[] byte配列が戻ります
     */
    public static byte[] toBytes(int a) {
        byte[] bs = new byte[4];
        bs[3] = (byte) (0x000000ff & (a));
        bs[2] = (byte) (0x000000ff & (a >>> 8));
        bs[1] = (byte) (0x000000ff & (a >>> 16));
        bs[0] = (byte) (0x000000ff & (a >>> 24));
        return bs;
    }

    /**
     * バイトの配列をintにします。
     * 
     * @param bytes バイト配列をセット
     * @return int 整数が戻ります
     */
    public static int toInt(byte... b) {
        if ( b == null || b.length == 0 )
            throw new IllegalArgumentException();
        
        if ( b.length == 1 ) 
            return b[0] & 0xFF;
        if ( b.length == 2 ) {
            int i = 0;
            i |= b[0] & 0xFF;
            i <<= 8;
            i |= b[1] & 0xFF;
            return i;
        }
        if ( b.length == 3 ) {
            int i = 0;
            i |= b[0] & 0xFF;
            i <<= 8;
            i |= b[1] & 0xFF;
            i <<= 8;
            i |= b[2] & 0xFF;
            return i;
        }
            
        return ByteBuffer.wrap(b).getInt();
    }
    /**
     * byte配列を16進数文字列で戻します
     * 
     * @param data データをセット 
     * @return 文字列が戻ります
     */
    public static String getHexString(byte data) {
        return getHexString(new byte[]{data});
    }
    /**
     * byte配列を16進数文字列で戻します
     * 
     * @param byteArray byte配列をセット 
     * @return 文字列が戻ります
     */
    public static String getHexString(byte[] byteArray, int... split) {
        StringBuilder builder = new StringBuilder();
        byte[] target = null;
        if ( split.length <= 1 ) {
            target = byteArray;
        } else  if ( split.length < 2 ) {
            target = Arrays.copyOfRange(byteArray, 0, 0 + split[0]);
        } else {
            target = Arrays.copyOfRange(byteArray, split[0], split[0] + split[1]);
        }
        for (byte b : target) {
            builder.append(String.format("%02x", b).toUpperCase());
        }
        return builder.toString();
    }
    /**
     * byte配列を2進数文字列で戻します
     * 
     * @param data byteデータをセット 
     * @return 文字列が戻ります
     */
    public static String getBinString(byte data) {
        return getBinString(new byte[]{data});
    }   
    /**
     * byte配列を2進数文字列で戻します
     * 
     * @param byteArray byte配列をセット 
     * @return 文字列が戻ります
     */
    public static String getBinString(byte[] byteArray, int... split) {
        StringBuilder builder = new StringBuilder();
        byte[] target = null;
        if ( split.length <= 1 ) {
            target = byteArray;
        } else  if ( split.length < 2 ) {
            target = Arrays.copyOfRange(byteArray, 0, 0 + split[0]);
        } else {
            target = Arrays.copyOfRange(byteArray, split[0], split[0] + split[1]);
        }
        
        for (byte b : target) {
            builder.append(String.format("%8s"
                    , Integer.toBinaryString(b & 0xFF)).replaceAll(" ", "0"));
        }
        return builder.toString();
    }
}
