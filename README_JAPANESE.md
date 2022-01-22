# RINPn

&raquo; [English README](./README.md)

![Concept Image](./img/signboard.jpg)

RINPn（RINEARN Processor nano, 発音：りんぷん）は、単純でわかりやすい画面デザインを採用した関数電卓ソフトです。GUIとコマンドラインの両方で利用可能です。標準でいろいろな数学・統計関数が利用でき、ユーザー独自の関数を自作する事も可能です。また、複雑な計算処理をスクリプトとして書いて実行する事もできます。

**※ このREADMEの内容は、このソフトウェアをソースコードからビルドしたい方のためのものです。
下記公式サイトから、ビルド済みのパッケージも入手できます。**

### RINPn 公式サイト

- 日本語:   [https://www.rinearn.com/ja-jp/rinpn/](https://www.rinearn.com/ja-jp/rinpn/)</a>
- 英語: [https://www.rinearn.com/en-us/rinpn/](https://www.rinearn.com/en-us/rinpn/)</a>

<hr />



## 目次
- <a href="#version-note">留意事項</a>
- <a href="#license">ライセンス</a>
- <a href="#how-to-build">ビルド方法</a>
- <a href="#how-to-use">使用方法</a>
- <a href="#architecture">ソフトウェアアーキテクチャ</a>
- <a href="#about-us">開発元について</a>



<a id="version-note"></a>
## 留意事項

RINPn は、まだ正式リリース前のソフトウェアであり、現在はいわゆる「 オープンベータ版 」として公開しています。


<a id="license"></a>
## ライセンス

このソフトウェアはMITライセンスで公開されています。




<a id="how-to-build"></a>
## ビルド方法

### 必要なもの

1. Java&reg; Development Kit (JDK) 8 or later

1. Git


### Microsoft&reg; Windows&reg; を使用している場合:

1. リポジトリをローカルに複製

		cd <working-directory>
		git clone https://github.com/RINEARN/vnano.git
		git clone https://github.com/RINEARN/vnano-standard-plugin.git
		git clone https://github.com/RINEARN/rinpn.git

1. Vnanoスクリプトエンジンのビルドとコピー

		cd vnano
		.\build.bat
		(または:  ant -f build.xml )

		cd ..
		copy .\vnano\Vnano.jar .\rinpn\Vnano.jar

1. Vnano標準プラグインのビルドとコピー

		cd vnano-standard-plugin
		.\build.bat
		
		cd ..
		xcopy /s .\vnano-standard-plugin\plugin\* .\rinpn\plugin\
		xcopy .\vnano-standard-plugin\*.html .\rinpn\

1. RINPn のビルド

		cd rinpn
		.\build.bat
		(または:  ant -f build.xml )

		cd plugin
		javac -encoding UTF-8 ExamplePlugin.java
		cd ..

1. 確認

		java -jar RINPn.jar --version
		> RINPn Ver.?.?.?  / with Vnano Ver.?.?.?  (?: numbers)


### その他のOSを使用している場合 (Linux&reg; 等) :


1. リポジトリをローカルに複製

		cd <working-directory>
		git clone https://github.com/RINEARN/vnano.git
		git clone https://github.com/RINEARN/vnano-standard-plugin.git
		git clone https://github.com/RINEARN/rinpn.git

1. Vnanoスクリプトエンジンのビルドとコピー

		cd vnano
		chmod +x ./build.sh          # 必要に応じて: sudo ...
		./build.sh                   # または:  ant -f build.xml
		
		cd ..
		cp ./vnano/Vnano.jar ./rinpn/Vnano.jar

1. Vnano標準プラグインのビルドとコピー

		cd vnano-standard-plugin
		chmod +x ./build.sh          # 必要に応じて: sudo ...
		./build.sh
		
		cd ..
		cp -r ./vnano-standard-plugin/plugin/* ./rinpn/plugin/
		cp ./vnano-standard-plugin/*.html ./rinpn/

1. RINPn のビルド

		cd rinpn
		chmod +x ./build.sh          # 必要に応じて: sudo ...
		./build.sh                   # または:  ant -f build.xml

		cd plugin
		javac -encoding UTF-8 ExamplePlugin.java
		cd ..

		cd bin
		chmod +x ./rinpn             # 必要に応じて: sudo ...
		cd ..

1. 確認

		java -jar RINPn.jar --version
		> RINPn Ver.?.?.?  / with Vnano Ver.?.?.?  (?: numbers)



<a id="how-to-use"></a>
## 使用方法

Microsoft&reg; Windows&reg;上では、**バッチファイル "RINPN_\*.\*.\*.bat" をダブルクリックすると起動します。**

その他のOS（Linux&reg;など）では、以下のようにコマンドで起動します：

    java -jar RINPn.jar

なお、RINPn の「 bin 」フォルダのパスを環境変数 Path / PATH に登録しておくと、以下のように単純なコマンドで起動できます：

    rinpn

RINPn が起動されると、以下のような電卓画面が表示されます：

![Calculator Window](./img/gui_expression.png)


上の電卓画面上で、計算式を入力して Enter キーを押すか、または「 = 」ボタンを押すと、その式の値を計算できます。式の中では、sin や cos などの各種数学関数も標準で使用できます。
画面の大きさや色、透明度、その他色々は「 Settings.txt 」内の設定値を変更してカスタマイズできます。

加えて、RINPn はスクリプト言語 [Vnano](https://www.vcssl.org/ja-jp/vnano/) をサポートしています。それにより、「 lib 」フォルダ内にあるスクリプトファイル「 ExampleLibrary.vnano 」の中で自作の関数を定義して、それを RINPn の計算式の中で使用する事もできます。
また、複雑な計算処理などを記述したスクリプトファイルの名前やパスを入力する事で、それを実行する事も可能です。


なお、RINPn の bin フォルダのパスを環境変数 Path / PATH に登録しておくと、以下のようにコマンドラインでも計算できます：

    rinpn "1 + 2"
	> 3

	rinpn "sin( PI / 2 ) + ( 3 / 2 )"
	> 2.5

	rinpn "Example.vnano"
	> 0.8414709848


より詳しい使用方法は、リポジトリ内に同梱されている「 **RINPn_User_Guide_Japanese.html** 」をご参照ください。また、同内容のガイドがWeb上でも参照できます： 
[https://www.rinearn.com/ja-jp/rinpn/guide/](https://www.rinearn.com/ja-jp/rinpn/guide/)</a>


<a id="architecture"></a>
## ソフトウェアアーキテクチャ

RINPn のソースコードは、
Model / View / Presenter の3つの主要コンポーネントを軸に構成される、MVPパターンに基づくアーキテクチャを採用しています。
各コンポーネントは、それぞれ個別のパッケージとしてまとめられています。
また、数値演算処理やスクリプト処理などを担う部分は、「 [Vnano エンジン](https://www.vcssl.org/ja-jp/vnano/) (Vnano.jar) 」 として、RINPn 本体のコンポーネントとは独立な形で実装されています。

より詳しい説明は, このリポジトリ内同梱の「 [Architecture_Japanese.md](./Architecture_Japanese.md) 」をご参照ください。
上記ドキュメントでは、ソースコードを把握する際の参考となる情報として、このソフトウェアの内部的なアーキテクチャを解説しています。


<a id="about-us"></a>
## 開発元について


Exevalator は、日本の個人運営の開発スタジオ [RINEARN](https://www.rinearn.com/) が開発しています。著者は松井文宏です。ご質問やフィードバックなどをお持ちの方は、御気軽にどうぞ。


---

## 本文中の商標など


- OracleとJavaは、Oracle Corporation 及びその子会社、関連会社の米国及びその他の国における登録商標です。文中の社名、商品名等は各社の商標または登録商標である場合があります。

- Windows は、米国 Microsoft Corporation の米国およびその他の国における登録商標です。

- Linux は、Linus Torvalds 氏の米国およびその他の国における商標または登録商標です。 

- その他、文中に使用されている商標は、その商標を保持する各社の各国における商標または登録商標です。


