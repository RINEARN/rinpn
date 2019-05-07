/*
 * This file is a simple example of a plug-in provides embedded function/variables to this software.
 * You can implement functions/variables for the calculator as methods/fields of this class.
 * If you modify this code, it requres the compilation as follows:
 * 
 * このファイルは、このソフトに組み込み関数/変数を追加するためのプラグインの、簡単な実装例です。
 * このクラスのメソッド/フィールドとして、電卓用の関数/変数を実装できます。
 * コードを編集した際は、以下のように再コンパイルする必要があります：
 * 
 *     cd <this_folder>
 *     javac -encoding UTF-8 @sourcelist.txt
 * 
 * If you want, you can create an new source code file and implement an new plug-in in there.
 * In such case, append a path of the new file in "sourcelist.txt" and take the recompilation, 
 * and then specify the class of the new plug-in in "Setting.vnano" to load it.
 * 
 * 必要に応じて、新しいソースコードファイルを作成し、そこに新しいプラグインを実装する事もできます。
 * その場合は、「 sourcelist.txt 」内にそのファイルのパスを追記した上で再コンパイルし、
 * さらに「 Setting.vnano 」内で、そのプラグインのクラスを読み込み対象リストに追加してください。
 * 
 * License: CC0
 */

public class ExamplePlugin {
    
    public double pivar = 1.0;

    public double pifun(double arg) {
        return arg * 2.0;
    }
}
