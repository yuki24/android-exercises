package net.kazzz.util;

/**
 * 述語論理ジェネリックインタフェースを定義します
 * 
 * @author Kazzz
 * @date 2011/02/20
 * @since Android API Level 4
 *
 */

public interface IPredicate<T> {
    /**
     * 条件に合致するか否かを判定するメソッドです
     * @param input 入力するオブジェクト
     * @return　boolean 条件に合致した場合にtrueを戻します
     */
    boolean evaluate(T input);
}
