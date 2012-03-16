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
package net.kazzz.felica.command;

import net.kazzz.felica.IFeliCaByteData;
import net.kazzz.felica.lib.FeliCaLib;


/**
 * FeliCaコマンドを抽象化したインタフェースを提供します
 * 
 * @author Kazzz
 * @date 2011/01/21
 * @since Android API Level 9
 *
 */

public interface IFeliCaCommand extends IFeliCaByteData {
    /**
     * PICC側を一意の識別するためのIDmを取得します
     * @return IDmが戻ります
     */
    FeliCaLib.IDm getIDm();
}
