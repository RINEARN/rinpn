## ステップ 6 - 別のソフトウェア内に組み込んで使う

&raquo; [English](Step6.md)

&raquo; [AIに使い方を聞く（ChatGPTのアカウントが必要）](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

RINPn が使用している計算＆スクリプト処理エンジンである「 Vnanoエンジン 」は、Java&trade; 言語で記述した別のソフトウェア内に組み込んで使う事ができます。 開発元は RINPn と同じで、組み込み方や使い方も意外と簡単なので、ここで実際に試してみましょう！

## サンプルコード

以下は、Vnanoエンジンを用いて、 RINPn と同じようなエンジン設定で式「 1.2 + 3.4 」の値を計算する単純なサンプルコードです：

    import org.vcssl.nano.VnanoEngine;
    import org.vcssl.nano.VnanoException;
    import org.vcssl.nano.interconnect.ScriptLoader;
    import org.vcssl.nano.interconnect.PluginLoader;
    import java.util.Map;
    import java.util.HashMap;

    public class EmbedUseExample {
        public static void main(String[] args) throws VnanoException {

            ////////// Vnano エンジンの準備 ここから //////////

            // RINPn で計算に用いているスクリプトエンジン（Vnanoエンジン）を生成
            VnanoEngine engine = new VnanoEngine();

            // Vnano エンジンのオプション設定（詳細は Settings.txt 内の説明参照）
            Map<String, Object> optionMap = new HashMap<String, Object>();
            optionMap.put("EVAL_INT_LITERAL_AS_FLOAT", true);
            optionMap.put("EVAL_ONLY_FLOAT", true);
            optionMap.put("EVAL_ONLY_EXPRESSION", true);
            optionMap.put("UI_MODE", "CUI");      // CUIベースソフト用
            // optionMap.put("UI_MODE", "GUI"); // GUIベースのソフト用
            optionMap.put("ACCELERATOR_ENABLED", true);
            optionMap.put("ACCELERATOR_OPTIMIZATION_LEVEL", 0);
            engine.setOptionMap(optionMap); // エンジンに登録

            // パーミッション機能に対応したプラグインに通知するパーミッション設定
            Map<String, String> permissionMap = new HashMap<String, String>();
            permissionMap.put("DEFAULT", "ASK");   // ユーザーに尋ねる挙動をデフォルト化
            // permissionMap.put("DEFAULT", "DENY"); // デフォルトを拒否挙動にしたい場合
            // (ここで個別に設定したい許可項目を追加設定 / 例は以下)
            // permissionMap.put("FILE_READ", "ALLOW");
            engine.setPermissionMap(permissionMap); // エンジンに登録

            // プラグインの読み込み
            PluginLoader pluginLoader = new PluginLoader("UTF-8");
            pluginLoader.setPluginListPath("./plugin/VnanoPluginList.txt");
            pluginLoader.load();
            for (Object plugin: pluginLoader.getPluginInstances()) {
                engine.connectPlugin("___VNANO_AUTO_KEY", plugin);
            }

            // ライブラリの読み込み
            ScriptLoader scriptLoader = new ScriptLoader("UTF-8");
            scriptLoader.setLibraryScriptListPath("./lib/VnanoLibraryList.txt");
            scriptLoader.load();
            String[] libPaths = scriptLoader.getLibraryScriptPaths(true);
            String[] libScripts = scriptLoader.getLibraryScriptContents();
            int libCount = libScripts.length;
            for (int ilib=0; ilib<libCount; ilib++) {
                engine.registerLibraryScript(libPaths[ilib], libScripts[ilib]);
            }

            ////////// Vnano エンジンの準備 ここまで //////////

            // Vnano エンジンで「 1.2 + 3.4 」の値を計算して表示
            String input = "1.2 + 3.4";
            double x = (double) engine.executeScript(input + ";");
            System.out.println("Vnano エンジンの出力値: " + x);

            // ライブラリ/プラグインの解放/接続解除（ソフトウェアの終了前などに行います）
            engine.unregisterAllLibraryScripts();
            engine.disconnectAllPlugins();
        }
    }


## コンパイル・実行方法

上記のコードは、Vnano エンジンのJarファイル「 **Vnano.jar (RINPnのフォルダ内に既に存在)** 」をクラスパスに指定しつつ、下記のようにコンパイルします：

    # Microsoft Windows をご使用の場合
    javac -cp ".;Vnano.jar" -encoding UTF-8 EmbedUseExample.java

    # Linux やその他の環境をご使用の場合
    javac -cp ".:Vnano.jar" -encoding UTF-8 EmbedUseExample.java

※ 最初の2行は、-cp 直後の "～" 内の区切り文字が、「 ; 」か「 : 」かという点が異なります。
コンパイルに成功すると、「 EmbedUseExample.class 」が生成されます。 同様に、Vnano.jar をクラスパスに指定しつつ実行します：

    # Microsoft Windows をご使用の場合
    java -cp ".;Vnano.jar" EmbedUseExample

    # Linux やその他の環境をご使用の場合
    java -cp ".:Vnano.jar" EmbedUseExample

実行結果は：

    スクリプトエンジンの出力値: 4.6

この通り、計算式「 1.2 + 3.4 」の値「 4.6 」を Vnano エンジンで求める事ができました。 式の中でライブラリ/プラグインの関数を呼んだり、式の代わりに Vnano で記述したスクリプトのコードを渡す事もできます。

## Vnanoエンジンのライセンス、その他詳しい情報など

RINPn 同様に、VnanoエンジンもMITライセンスのオープンソースソフトウェアで、商用・非商用問わず無償で利用できます。

その他、より詳しい情報や、ガイド・仕様書などのドキュメント類などについては、 下記 Vnano エンジンの公式サイトで整備されていますので、ご参照ください。

* [Vnano 公式サイト（日本語版）](https://www.vcssl.org/ja-jp/vnano/)
* [Vnano 公式サイト（英語版）](https://www.vcssl.org/en-us/vnano/)


---

## Credits and Trademarks

* Oracle と Java は、Oracle Corporation 及びその子会社、関連会社の米国及びその他の国における登録商標です。文中の社名、商品名等は各社の商標または登録商標である場合があります。

* Microsoft Windows は、米国 Microsoft Corporation の米国およびその他の国における登録商標です。

* Linux は、Linus Torvalds 氏の米国およびその他の国における商標または登録商標です。

* ChatGPT は、米国 OpenAI OpCo, LLC による米国またはその他の国における商標または登録商標です。

* その他、文中に使用されている商標は、その商標を保持する各社の各国における商標または登録商標です。




