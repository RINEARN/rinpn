# RINEARN Processor nano



RINEARN Processor nano is a simple & compact programmable calculator.

リニアンプロセッサー nano は、シンプルでコンパクトなプログラマブル関数電卓です。


<div style="background-color:black; width: 890px; height: 463px; text-align:center; background-image: url('./signboard.jpg'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinearn-processor-nano/blob/master/signboard.jpg" alt="" width="890" />
</div>

This README is for users who want to build this software from source code by yourself.
You can also get prebuilt-packages of this software from: 
<a href="https://download.rinearn.com/advanced/#processor-nano">https://download.rinearn.com/advanced/#processor-nano</a>

このREADMEの内容は、このソフトウェアをソースコードからビルドしたい方のためのものです。
<a href="https://download.rinearn.com/advanced/#processor-nano">https://download.rinearn.com/advanced/#processor-nano</a>
からビルド済みのパッケージも入手できます。

<hr />



## Index - 目次
- <a href="#caution">Caution - 注意</a>
- <a href="#license">License - ライセンス</a>
- <a href="#requirements">Requirements - 必要な環境</a>
- <a href="#how-to-build">How to Build - ビルド方法</a>
	- <a href="#how-to-build-processor-nano">Build the RINEARN Processor nano - リニアンプロセッサー nano のビルド</a>
	- <a href="#how-to-build-vnano">Build the Vnano Engine - Vnanoエンジンのビルド</a>
- <a href="#how-to-use">How to Use - 使用方法</a>
	- <a href="#how-to-use-gui">How to in the GUI Mode - GUIモードでの使用方法</a>
	- <a href="#how-to-use-cui">How to in the CUI Mode - CUIモードでの使用方法</a>
	- <a href="#how-to-use-library">How to Declare Variables and Functions - 変数や関数の定義</a>
- <a href="#about-us">About Us - 開発元について</a>
- <a href="#references">References - 関連記事</a>



<a id="caution"></a>
## Caution - 注意

RINEARN Processor nano is under development, so it has not practical quality yet.

リニアンプロセッサー nano は開発の途中であり、現時点でまだ実用的な品質ではありません。


<a id="license"></a>
## License - ライセンス

This software is released under the MIT License.

このソフトウェアはMITライセンスで公開されています。


<a id="requirements"></a>
## Requirements - 必要な環境

1. Java&reg; Development Kit (JDK) 7 or later - Java&reg;開発環境 (JDK) 7以降



<a id="how-to-build"></a>
## How to Build - ビルド方法

<a id="how-to-build-processor-nano"></a>
### 1. Build the RINEARN Processor nano - リニアンプロセッサー nano のビルド

Firstly, get and build source code of the RINEARN Processor nano.

はじめに、リニアンプロセッサー nano のソースコードを入手してビルドします。

	cd <working-directory>
	git clone https://github.com/RINEARN/rinearn-processor-nano.git
	cd rinearn-processor-nano

for Microsoft&reg; Windows&reg; :

	.\build.bat

for Linux&reg;, etc. :

	./build.sh

for Apache Ant :

    ant -f build.xml

If you succeeded to build, the JAR file "RinearnProcessorNano.jar" will be generated. 

ビルドが成功すると、JARファイル「 RinearnProcessorNano.jar 」が生成されます。


<a id="how-to-build-vnano"></a>
### 2. Build the Vnano Engine - Vnanoエンジンのビルド

Next, get and build source code of the script engine of the <a href="https://github.com/RINEARN/vnano">Vnano</a> (Vnano Engine).

次に、<a href="https://github.com/RINEARN/vnano">Vnano</a>のスクリプトエンジン（Vnanoエンジン）のソースコードを入手してビルドします。

	cd <working-directory>
	git clone https://github.com/RINEARN/vnano.git
	cd vnano

for Microsoft&reg; Windows&reg; :

	.\build.bat

for Linux&reg;, etc. :

	./build.sh

for Apache Ant :

    ant -f build.xml

If you succeeded to build, the JAR file "Vnano.jar" will be generated. 
This JAR file is the Vnano Engine which is necessary for RINEARN Processor nano, 
so put the JAR file of the Vnano Engine "Vnano.jar" in the same directory as "RinearnProcessorNano.jar" :

ビルドが成功すると、JARファイル「 Vnano.jar 」が生成されます。
このJARファイルがVnanoエンジンで、リニアンプロセッサー nano の動作に必要なので、
「RinearnProcessorNano.jar」と同じフォルダ内に配置します：

	cd <working-directory>

for Microsoft&reg; Windows&reg; :

	copy .\vnano\Vnano.jar .\rinearn-processor-nano\Vnano.jar

for Linux&reg;, etc. :

	cp ./vnano/Vnano.jar ./rinearn-processor-nano/Vnano.jar


<a id="how-to-use"></a>
## How to Use - 使用方法

<a id="how-to-use-gui"></a>
### 1. How to in the GUI Mode - GUIモードでの使用方法

In the GUI mode, you can take calculations on the graphical calculator window.
At first, execute "RinearnProcessorNano.jar" from the command-line terminal as follows:

GUIモードでは、グラフィカルな電卓画面上で計算を行う事ができます。
まず、コマンドラインで以下のように「 RinearnProcessorNano.jar 」を実行してください：

	cd <working-directory>/rinearn-processor-nano/
	java -jar RinearnProcessorNano.jar

Alternatively, if you are using the 
<a href="https://download.rinearn.com/advanced/#processor-nano">pre-built package</a> 
on the OS of the Microsoft&reg; Windows&reg;, you can execute the above JAR file by double-clicking the batch file "RinearnProcessorNano.bat".

または、もし Microsoft&reg; Windows&reg; のOS上で 
<a href="https://download.rinearn.com/advanced/#processor-nano">ビルド済みパッケージ</a> 
を使用している場合は、バッチファイル「 RinearnProcessorNano.bat 」をダブルクリックする事で上記JARファイルを実行する事も可能です。

Then the window of the RINEARN Processor nano will be launched:

実行すると、以下のようにリニアンプロセッサー nano の画面が起動します: 

<div style="background-color:white; width: 700px; height: 300px; text-align:center; background-image: url('./ui.png'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinearn-processor-nano/blob/master/ui.png" alt="" width="700" />
</div>


To calculate, Input the expression into the "INPUT" text-field, and press the Enter key of your key board.
Then the calculated value of the expression will be output on the "OUTPUT" text-field.
For example:

計算を行うには、「 INPUT 」欄に計算式を入力し、そのままキーボードの Enter キーを押してください。
すると、計算された値が「 OUTPUT 」欄に表示されます。
例えば：


	INPUT:
	( 1 + 2 ) / 3 - 4 + 5

	OUTPUT:
	2.0


<a id="how-to-use-cui"></a>
### 2. How to Use in CUI Mode - CUIモードでの使用方法

In the CUI mode, you can take calculations on the command-line terminal, whithout launching the calculator window.
To use the CUI mode, execute the "RinearnProcessorNano.jar" with passing an expression as a command-line argument as follows:

CUIモードでは、コマンドライン端末上で、電卓画面を起動せずにその場で計算を行う事ができます。
CUIモードを使用するには、コマンドラインで以下のように、計算式を引数として「 RinearnProcessor.jar 」を実行してください：

	cd <working-directory>/rinearn-processor-nano/
	java -jar RinearnProcessorNano.jar "( 1 + 2 ) / 3 - 4 + 5"

	(result)
	2.0



<a id="how-to-use-library"></a>
### 3. How to Declare Variables and Functions - 変数や関数の定義

You can define variables and functions in the script file "Library.vnano".
Defined variables and functions are available in expressions written in the "INPUT" text-field of the Step-2. 
The content of "Library.vnano" should be written in the script language of the Vnano 
(see "<a href="https://github.com/RINEARN/vnano#language">The Vnano as a Language</a>" for details).

スクリプトファイル「 Library.vnano 」の中で、変数や関数を定義できます。
そこで定義した変数や関数は、ステップ 2 での「INPUT」欄の計算式の中で使用できます。
なお、「 Library.vnano 」の中身は、Vnano のスクリプト言語
（ 詳細は「 <a href="https://github.com/RINEARN/vnano#language">言語としてのVnano</a> 」を参照 ）
で記述する必要があります。

For example:

例えば：

	( in Library.vnano )

	float var1 = 1.2345678;

	float fun1(float arg) {
		return arg * 2;
	}

	float fun2(float arg) {
    	return arg + 5;
	}

and use the above variable and functions on the calculation:

と定義して、計算時に以下のように使用できます：

	INPUT:
	2 * var1

	OUTPUT:
	2.4691356

	INPUT:
	fun1(2) + fun2(3)

	OUTPUT:
	12.0







<a id="about-us"></a>
## About Us - 開発元について

<div style="background-color:white; width: 890px; height: 356px; text-align:center; background-image: url('./rinearn.jpg'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/vnano/blob/master/rinearn.jpg" alt="" width="890" />
</div>


RINEARN Processor nano is developed by <a href="https://www.rinearn.com/">RINEARN</a> 
which is a personal studio in Japan developing software for data-analysis, visualization, computation, and so on.
Please feel free to contact us if you have any question about RINEARN Processor nano, or you are interested in RINEARN Processor nano.

リニアンプロセッサー nano は、日本の開発スタジオである <a href="https://www.rinearn.com/">RINEARN</a> が開発しています。
RINEARNでは、主にデータ解析や可視化、計算向けのソフトウェアを開発しています。
リニアンプロセッサー nano に関するご質問や、リニアンプロセッサー nano にご興味をお持ちの場合は、ご気軽にお問い合せください。

### Our website - ウェブサイト

- <a href="https://www.rinearn.com/">https://www.rinearn.com/</a>


---

<a id="references"></a>
## References - 関連記事

<dl>
	<dt style="display: list-item; margin-left:40px;">
		"シンプル＆コンパクトなプログラム関数電卓「 リニアンプロセッサー nano 」の概要" - RINEARN Website (2019/01/26)
	</dt>
	<dd>
		<a href="https://www.rinearn.com/ja-jp/info/news/2019/0126-rinearn-processor-nano-concept">https://www.rinearn.com/ja-jp/info/news/2019/0126-rinearn-processor-nano-concept</a>
	</dd>
	<dt style="display: list-item; margin-left:40px;">
		"リニアンプロセッサー nano の先行開発版やソースコードリポジトリを公開" - RINEARN Website (2019/04/16)
	</dt>
	<dd>
		<a href="https://www.rinearn.com/ja-jp/info/news/2019/0416-rinearn-processor-nano-advanced">https://www.rinearn.com/ja-jp/info/news/2019/0416-rinearn-processor-nano-advanced</a>
	</dd>
</dl>

---

## Credits - 本文中の商標など

- Oracle and Java are registered trademarks of Oracle and/or its affiliates. 

- Microsoft Windows is either a registered trademarks or trademarks of Microsoft Corporation in the United States and/or other countries. 

- Linux is a trademark of linus torvalds in the United States and/or other countries. 

- Other names may be either a registered trademarks or trademarks of their respective owners. 

- OracleとJavaは、Oracle Corporation 及びその子会社、関連会社の米国及びその他の国における登録商標です。文中の社名、商品名等は各社の商標または登録商標である場合があります。

- Windows は、米国 Microsoft Corporation の米国およびその他の国における登録商標です。

- Linux は、Linus Torvalds 氏の米国およびその他の国における商標または登録商標です。 

- その他、文中に使用されている商標は、その商標を保持する各社の各国における商標または登録商標です。


