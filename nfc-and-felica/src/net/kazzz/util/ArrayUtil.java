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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 配列で使用するための各種ユーティリティ 
 *
 * @author Kazzz
 * @date 2011/02/20
 * @since Android API Level 4
 *
 */
public final class ArrayUtil {
    /**
     * 配列にオブジェクトを追加します。
     * @param <T>
     * 
     * @param array 対象の配列をセット
     * @param obj 対象のオブジェクトをセット
     * @return オブジェクトが追加された結果の配列
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[] add(T[] array, T obj) {
        if (array == null) {
            throw new IllegalArgumentException("array");
        }
        T[] newArray = (T[]) Array.newInstance(array.getClass()
                .getComponentType(), array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = obj;
        return newArray;
    }

    /**
     * 配列に配列を追加します。(合成)
     * @param <T>
     * 
     * @param a 配列Aをセット
     * @param b 配列Bをセット
     * @return 配列が追加された結果の配列
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[] add(final T[] a, final T[] b) {
        if (a != null && b != null) {
            if (a.length != 0 && b.length != 0) {
                T[] array = (T[]) Array.newInstance(a.getClass()
                        .getComponentType(), a.length + b.length);
                System.arraycopy(a, 0, array, 0, a.length);
                System.arraycopy(b, 0, array, a.length, b.length);
                return array;
            } else if (b.length == 0) {
                return a;
            } else {
                return b;
            }
        } else if (b == null) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * 配列中のオブジェクトのindexを返します。
     * 
     * @param array 配列をセット
     * @param obj 対象のオブジェクトをセット
     * @return 配列中のオブジェクトのindex
     */
    public static final <T> int indexOf(T[] array, T obj) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                Object o = array[i];
                if (o != null) {
                    if (o.equals(obj)) {
                        return i;
                    }
                } else if (obj == null) {
                    return i;

                }
            }
        }
        return -1;
    }
    /**
     * 文字列配列中のオブジェクトのindexを返します。
     * 
     * @param array 文字列配列をセット
     * @param str 対象の文字列オブジェクトをセット
     * @return 配列中のオブジェクトのindexが戻ります
     */
    public static final int indexOfIgnoreCase(String[] array, String str) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                String s = array[i];
                if (s != null) {
                    if (s.equalsIgnoreCase(str)) {
                        return i;
                    }
                } else if (str == null) {
                    return i;

                }
            }
        }
        return -1;
    }

    /**
     * 配列中のcharのindexを返します。
     * 
     * @param array 対象の文字配列をセット
     * @param ch 対象文字をセット
     * @return 配列中のcharのindex
     */
    public static final int indexOf(char[] array, char ch) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                char c = array[i];
                if (ch == c) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 配列中から対象のオブジェクトを削除します。
     * 
     * @param array 対象の配列をセット
     * @param obj 対象のオブジェクトをセット
     * @return 削除後の配列が戻ります
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[] remove(T[] array, T obj) {
        int index = indexOf(array, obj);
        if (index < 0) {
            return array;
        }
        T[] newArray = (T[]) Array.newInstance(
                array.getClass().getComponentType(), array.length - 1);
        if (index > 0) {
            System.arraycopy(array, 0, newArray, 0, index);
        }
        if (index < array.length - 1) {
            System.arraycopy(array, index + 1, newArray, index, newArray.length
                    - index);
        }
        return newArray;
    }

    /**
     * 配列が空かどうかを検査します
     *   
     * @param arrays 対象の配列をセット
     * @return 配列が空であれは゛trueが戻ります
     */
    public static final boolean isEmpty(Object[] arrays) {
        return (arrays == null || arrays.length == 0);
    }

    /**
     * 配列にオブジェクトが含まれているかどうかを返します。
     * 
     * @param array 対象の配列をセット
     * @param obj 対象のオブジェクトをセットします
     * @return 
     * @return 配列にオブジェクトが含まれている場合はtrueが戻ります
     */
    public static final <T> boolean contains(T[] array, T obj) {
        return -1 < indexOf(array, obj);
    }

    /**
     * 文字列配列に文字列オブジェクトが含まれているかどうかを返します。(文字列のケースを比較しません)
     * 
     * @param array 対象の配列をセット
     * @param obj 対象のオブジェクトをセットします
     * @return 配列にオブジェクトが含まれている場合はtrueが戻ります
     */
    public static final boolean containsIgnoreCase(String[] array, String str) {
        return -1 < indexOfIgnoreCase(array, str);
    }
    /**
     * 配列にcharが含まれているかどうかを返します。
     * 
     * @param array 対象の配列をセット
     * @param ch 対象の文字をセット
     * @return 配列にcharが含まれている場合trueが戻ります
     */
    public static final boolean contains(char[] array, char ch) {
        return -1 < indexOf(array, ch);
    }

    /**
     * 順番は無視して2つの配列が等しいかどうかを返します。
     * 
     * @param array1 一つ目の配列をセット
     * @param array2 二つ目の配列をセット
     * @return 順番は無視して2つの配列が等しい場合trueが戻ります
     */
    public static final <T> boolean equalsIgnoreSequence(T[] array1, T[] array2) {
        if (array1 == null && array2 == null) {
            return true;
        } else if (array1 == null || array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        List<T> list = Arrays.asList(array2);
        for (int i = 0; i < array1.length; i++) {
            T o1 = array1[i];
            if (!list.contains(o1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 配列を文字列に変換します。
     * 
     * @param array 配列をセット
     * @return 配列の文字列表現が戻ります
     */
    public static final String toString(Object[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i == 0) {
                sb.append('[');
            } else {
                sb.append(", ");
            }
            sb.append(String.valueOf(array[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * ある型の配列をオブジェクトの配列に変換します。
     * 
     * @param obj 対象のオブジェクトをセット
     * @return オブジェクトの配列が戻ります
     */
    public static final Object[] toObjectArray(Object obj) {
        int length = Array.getLength(obj);
        Object[] array = new Object[length];
        for (int i = 0; i < length; i++) {
            array[i] = Array.get(obj, i);
        }
        return array;
    }
    /**
     * 文字列配列の空の要素をトリミングします
     * @param array 配列をセット
     * @return String[] トリム後の配列が戻ります
     */
    public static final String[] trim(final String[] array) {
        String[] includes = FinderUtil.findAll(array, 
                new IPredicate<String>(){
                    @Override
                    public boolean evaluate(String input) {
                        return (input != null && input.length() > 0);
                    }});
        return includes;
    }
    /**
     * 文字列配列の要素から重複を削除します
     * @param array 配列をセット
     * @return T[] トリム後の配列が戻ります
     */
    public static final <T> T[] truncate(final T[] array) {
        final ArrayList<T> list = new ArrayList<T>(array.length);
        T[] includes = FinderUtil.findAll(array, 
                new IPredicate<T>(){
                    @Override
                    public boolean evaluate(T input) {
                        if ( list.contains(input) ) {
                            return false;
                        } else {
                            list.add(input);
                            return true;
                        }
                    }});
        return includes;
    }
    /**
     * 配列の要素数を変えてコピーします
     * @param <T> 配列要素の型パラメタ
     * @param original 元の配列
     * @param newLength 新しい配列のサイズ
     * @return T[] 新たな要素数の配列が戻ります
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }

    /**
     * 配列を別な要素型の配列にコピーします
     * @param <T> 先の要素型パラメタ
     * @param <U> 元の要素型パラメタ
     * @param original 元の配列
     * @param newLength 新たな配列の要素数
     * @param newType　新たな配列の型
     * @return T[] コピーされた新たな配列が戻ります
     */
    @SuppressWarnings("unchecked")
    public static final <T,U> T[] copyOf(U[] original, int newLength
            , Class<? extends T[]> newType) {
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

}
