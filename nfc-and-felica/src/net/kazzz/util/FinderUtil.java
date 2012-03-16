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

package net.kazzz.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * いろいろなオブジェクトを探すユーティリティクラスです
 * 
 * @author Kazzz
 * @date 2011/02/20
 * @since Android API Level 4
 *
 */

public final class FinderUtil {
    
    /**
     * 配列中から条件に合う要素を抽出します
     * @param <T> 任意の型を指定します
     * @param array <T>型の配列をセット
     * @param match 述語論理インタフェースをセットします
     * @return T 見つかった要素が戻ります
     */
    public static final <T> T find(T[] array, IPredicate<T> match) {
        T result = null;
        for (int i = 0; i < array.length; i++) {
            T t = array[i];
            if ( match.evaluate(t) ){
                result = t;
                break;
            }
        }
        return result;
    }
    /**
     * 配列中から条件に合う要素を抽出して配列を取得します
     * @param <T> 任意の型を指定します
     * @param array <T>型の配列をセット
     * @param match 述語論理インタフェースをセットします
     * @return T[] 型の配列が戻ります
     */
    public static final <T> T[] findAll(T[] array, IPredicate<T> match) {
        ArrayList<T> temp = new ArrayList<T>();
        int size = array.length;
        for (int i = 0; i < size; i++ ) {
            T t = array[i];
            if ( match.evaluate(t) ){
                temp.add(t);
            }
        }
        
        T[] result = ArrayUtil.copyOf(array, 0); 
        return temp.toArray(result);

        //T[] result = (T[])
        //    Array.newInstance(array.getClass().getComponentType(), temp.size());
        //return temp.toArray(result);
    }
    /**
     * コレクションから条件に合う要素だけを抽出します
     * @param <T> 任意の型を指定します
     * @param array <T>型の配列をセット
     * @param match 述語論理インタフェースをセットします
     * @return T 見つかった要素が戻ります
     */
    public static final <T> T find(Collection<T> list, IPredicate<T> match) {
        T result = null;
        Iterator<T> i = list.iterator();
        while( i.hasNext() ) {
            T t = i.next();
            if ( match.evaluate(t) ){
                result = t;
                break;
            }
        }
        return result;
    }
    /**
     * コレクションから条件に合う要素を抽出てコレクションを取得します
     * @param <T> 任意の型を指定します
     * @param array <T>型の配列をセット
     * @param match 述語論理インタフェースをセットします
     * @return Collection<T> 型のコレクションが戻ります
     */
    public static final <T> Collection<T> findAll(Collection<T> list
    		, IPredicate<T> match) {
        ArrayList<T> temp = new ArrayList<T>();
        Iterator<T> i = list.iterator();
        while( i.hasNext() ) {
            T t = i.next();
            if ( match.evaluate(t) ){
                temp.add(t);
                break;
            }
        }
        return temp;
    }
    /**
     * リストから条件に合う要素だけを抽出します
     * @param <T> 任意の型を指定します
     * @param array <T>型の配列をセット
     * @param match 述語論理インタフェースをセットします
     * @return T 見つかった要素が戻ります
     */
    public static final <T> T find(List<T> list, IPredicate<T> match) {
        T result = null;
        int size = list.size();
        for (int i = 0; i < size; i++ ) {
            T t = list.get(i);
            if ( match.evaluate(t) ){
                result = t;
                break;
            }
        }
        return result;
    }
    /**
     * リストから条件に合う要素を抽出してリストで取得します
     * @param <T> 任意の型を指定します
     * @param array <T>型の配列をセット
     * @param match 述語論理インタフェースをセットします
     * @return List<T> 型のリスト(ArrayListにキャストできます)が戻ります
     */
    public static final <T> List<T> findAll(List<T> list, IPredicate<T> match) {
        ArrayList<T> temp = new ArrayList<T>();
        int size = list.size();
        for (int i = 0; i < size; i++ ) {
            T t = list.get(i);
            if ( match.evaluate(t) ){
                temp.add(t);
                break;
            }
        }
        return temp;
    }
    
    /**
     * 辞書中からプレフィクスが含まれるエントリだけを列挙します
     * @param <TValue> マップ値の型パラメタ
     * @param dictionary 対象の辞書(マップ)
     * @param prefix プレフィクス文字列をセット
     * @return Iterator<Entry<String, TValue>> エントリのイテレータが戻ります
     */
    public static <TValue> Iterator<Entry<String, TValue>> findKeysWithPrefix(
            Map<String, TValue> dictionary, String prefix) {
    
        HashMap<String, TValue> map = new HashMap<String, TValue>();
        
        if ( dictionary.containsKey(prefix) ) {
            map.put(prefix, dictionary.get(prefix));   
        }
        
        Iterator<Entry<String, TValue>> i = dictionary.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, TValue> entry = i.next();
            if (entry.getKey().length() <= prefix.length()) {
                continue;
            }

            if (!entry.getKey().toLowerCase().startsWith(prefix)) {
                continue;
            }

            char charAfterPrefix = entry.getKey().charAt(prefix.length());
            switch (charAfterPrefix) {
                case '[':
                case '.':
                    map.put(entry.getKey(), entry.getValue());
                    break;
            }
        }
        return map.entrySet().iterator();
    }
    /**
     * プレフィクスに対して少なくとも一つのエントリが含まれているかどうかを検査します
     * @param <TValue> 値の型パラメタ
     * @param dictionary 対象の辞書
     * @param prefix プレフィクスをセット
     * @return boolean 含まれている場合はtrueが戻ります
     */
    public static <TValue> boolean doesAnyKeyHavePrefix(Map<String, TValue> dictionary
            , String prefix) {
        return findKeysWithPrefix(dictionary, prefix).hasNext();
    }

}
