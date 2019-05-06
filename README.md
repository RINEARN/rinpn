# RINEARN Processor nano (RINPN)



RINEARN Processor nano (abbreviated: RINPN) is a simple & compact programmable calculator.

リニアンプロセッサー nano（略称：RINPN）は、シンプルでコンパクトなプログラマブル関数電卓です。


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
	- <a href="#how-to-compile-plugins">Compile Plug-Ins - プラグインのコンパイル</a>
- <a href="#how-to-use">How to Use - 使用方法</a>
	- <a href="#how-to-use-gui">How to Use in the GUI Mode - GUIモードでの使用方法</a>
	- <a href="#how-to-use-cui">How to Use in the CUI Mode - CUIモードでの使用方法</a>
	- <a href="#how-to-use-library">How to Define Variables and Functions as Script Code - スクリプトで変数や関数を定義する</a>
	- <a href="#how-to-implement-plugin">How to Implement Embedded Variables/Functions in Java&reg; - Java&reg;言語で組み込み変数/関数を実装する</a>
- <a href="#about-us">About Us - 開発元について</a>
- <a href="#references">References - 関連記事</a>



<a id="caution"></a>
## Caution - 注意

This software is under development, so it has not practical quality yet.

このソフトウェアは開発の途中であり、現時点でまだ実用的な品質ではありません。


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
### Step-1. Build the RINEARN Processor nano - リニアンプロセッサー nano のビルド

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
### Step-2. Build the Vnano Engine - Vnanoエンジンのビルド

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


<a id="how-to-compile-plugins"></a>
### Step-3. Compile Plug-Ins - プラグインのコンパイル

Finally, compile plug-ins which provide embedded-functions/variables to the Vnano Engine:

最後に、Vnanoエンジンに組み込み関数/変数を提供するプラグインをコンパイルします：

	cd <working-directory>/rinearn-processor-nano/plugin/
	javac -encoding UTF-8 @sourcelist.txt

where target source files of the compilation are listed in "&lt;working-directory&gt;/rinearn-processor-nano/plugin/sourcelist.txt".
If you want to append a new plug-in, write the path of the source file of the plug-in in the above "sourcelist.txt", 
and take the compilation again, and then specify its class-name in "Setting.vnano".

ここでコンパイル対象ファイルの一覧は「 &lt;working-directory&gt;/rinearn-processor-nano/plugin/sourcelist.txt 」内に記載されています。
もしも新しいプラグインを追加したい場合は、上記の sourcelist.txt 内にプラグインのソースコードのファイルパスを追記して再コンパイルした上で、「 Setting.vnano 」内でそのプラグインのクラス名を指定してください。


<a id="how-to-use"></a>
## How to Use - 使用方法

<a id="how-to-use-gui"></a>
### Step-1. How to Use in the GUI Mode - GUIモードでの使用方法

In the GUI mode, you can take calculations on the graphical calculator window.
At first, execute "RinearnProcessorNano.jar" from the command-line terminal as follows:

GUIモードでは、グラフィカルな電卓画面上で計算を行う事ができます。
それにはまず、コマンドラインで以下のように「 RinearnProcessorNano.jar 」を実行します：

	cd <working-directory>/rinearn-processor-nano/
	java -jar RinearnProcessorNano.jar



By the way, if you register the path of "bin" folder to the environment variable "PATH" (or "Path") 
of your OS, wherever the current directory is, you can launch more simply as follows:

なお、OSの環境変数 PATH （または Path ）に「bin」フォルダのパスを登録しておけば、カレントディレクトリの場所に関わらず、以下のように簡単なコマンドで実行できるようになります：

	rinpn


Alternatively, if you are using the 
<a href="https://download.rinearn.com/advanced/#processor-nano">pre-built package</a> 
on the OS of the Microsoft&reg; Windows&reg;, you can execute this software by double-clicking the batch file "RinearnProcessorNano.bat".

または、もし Microsoft&reg; Windows&reg; のOS上で 
<a href="https://download.rinearn.com/advanced/#processor-nano">ビルド済みパッケージ</a> 
を使用している場合は、バッチファイル「 RinearnProcessorNano.bat 」をダブルクリックして実行する事も可能です。

When you execute this software as above ways, the calculator window (the image below) will be launched:

さて、上記のように実行すると、電卓画面（下図）が起動します: 

<div style="background-color:white; width: 700px; height: 300px; text-align:center; background-image: url('./ui.png'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinearn-processor-nano/blob/master/ui.png" alt="" width="700" />
</div>


To take calculations, Input the expression into the "INPUT" text-field, and press the Enter key of your key board.
Then the calculated value of the expression will be output on the "OUTPUT" text-field.
For example:

計算を行うには、「 INPUT 」欄に計算式を入力し、そのままキーボードの Enter キーを押してください。
すると、計算された値が「 OUTPUT 」欄に表示されます。
例えば：


	INPUT:
	( 1 + 2 ) / 3 - 4 + 5

	OUTPUT:
	2


<a id="how-to-use-cui"></a>
### Step-2. How to Use in the CUI Mode - CUIモードでの使用方法

In the CUI mode, you can take calculations on the command-line terminal, whithout launching the calculator window.
To use the CUI mode, execute the "RinearnProcessorNano.jar" with passing an expression as a command-line argument as follows:

CUIモードでは、コマンドライン端末上で、電卓画面を起動せずにその場で計算を行う事ができます。
CUIモードを使用するには、コマンドラインで以下のように、計算式を引数として「 RinearnProcessor.jar 」を実行してください：

	cd <working-directory>/rinearn-processor-nano/
	java -jar RinearnProcessorNano.jar "(1 + 2 ) / 3 - 4 + 5"

	(result)
	2


If you register the path of "bin" folder to the environment variable "PATH" (or "Path") 
of your OS, wherever the current directory is, you can take calculations by more simply as follows:

ここでも、OSの環境変数 PATH （または Path ）に「bin」フォルダのパスを登録しておいた場合は、カレントディレクトリの場所に関わらず、以下のように簡単なコマンドで計算できるようになります：

	rinpn "( 1 + 2 ) / 3 - 4 + 5"

	(result)
	2


<a id="how-to-use-library"></a>
### Step-3. How to Define Variables and Functions as Script Code - スクリプトで変数や関数を定義する

You can define variables and functions in the script file "ExampleLibrary.vnano" in "lib" folder.
Defined variables and functions are available in expressions of the Step-1 and 2. 
The content of the script file should be written in the script language of the Vnano 
(see "<a href="https://github.com/RINEARN/vnano#language">The Vnano as a Language</a>" for details).
It does not require installation of development environments such as the compiler, 
so simply open the above file by your favorite text editor, and write code in there.

「 lib 」フォルダ内にあるスクリプトファイル「 ExampleLibrary.vnano 」の中で、変数や関数を定義できます。
そこで定義した変数や関数は、Step-1 や 2 での計算式の中で使用できます。
なお、スクリプトファイルの中身は、Vnano のスクリプト言語の記法
（ 詳細は「 <a href="https://github.com/RINEARN/vnano#language">言語としてのVnano</a> 」を参照 ）
で記述する必要があります。
コンパイラなどの特別な開発環境の導入は不要なので、適当なテキストエディタで上記ファイルを開き、中にコードを書いてください。

For example, the default content of "ExampleLibrary.vnano" is as follows:

例えば、標準状態での「 ExampleLibrary.vnano 」の内容は以下の通りです：

	( in lib/ExampleLibrary.vnano )

	// Note: The precision of "float" type in the Vnano is 64-bit, same with "double".
	// 備考: Vnano での「 float 」型は、いわゆる「 double 」型と同様の64bit精度です。

	float a = 2.0;
	float b = -1.0;
	float c = 3.0;

	float r = 1.01;

	float f(float x) {
		return a*x*x + b*x + c;
	}

	float g(float x) {
		if (x < 0) {
			return 0.0;
		}
		float value = x;
		for (int i=0; i<10; i++) {
			value *= r;
		}
		return value;
	}

In the the expression of the calculation, you can use variables and functions defined in the above script, as the following example:

計算式の中で、上記のスクリプト内で定義されている変数や関数を、以下の例のように使用できます：

	INPUT:
	a + b + c

	OUTPUT:
	4

	INPUT:
	( 1 + 2 * f(3) ) / g(1.23)

	OUTPUT:
	27.23220921

If you want, you can create other script files and can define variables and functions in them.
All files with the extension ".vnano" in "lib" folder will be loaded automatically as scripts.

なお、必要に応じて、別のスクリプトファイルを作成し、その中で変数や関数を定義する事もできます。
「 lib 」フォルダ内に置いた、拡張子「 .vnano 」のファイルは、全てスクリプトとして自動で読み込まれます。



<a id="how-to-implement-plugin"></a>
### Step-4. How to Implement Embedded Variables/Functions in Java&reg; - Java&reg;言語で組み込み変数/関数を実装する

You can implement new embedded variables and function in the Java&reg; programming language.
In this way, compared to defining variables/functions as script code (in the step-3), 
high-functionality of Java&reg; might be the great merit.
On the other hand, it requres Java&reg; Development Kit (JDK) to compile implemented code.

Java&reg;言語を用いて、新しい組み込み関数/変数を実装する事もできます。
この方法では、Step-3 のようにスクリプトで関数や変数を定義する事と比べて、
Java&reg; 言語の高い機能性を利用できる事が大きなメリットになるかもしれません。
一方で、実装したコードをコンパイルする際に Java&reg; 言語の開発環境 (JDK) が必要になります。

On this software, we refer a Java&reg; program to add new embedded function/variables as "plug-in".
A simple example of a plug-in is bundled in "plugin" folder as "ExamplePlugin.java":

このソフトウェアでは、新しい組み込み関数/変数を追加するためのプログラムを「プラグイン」と呼びます。
「 plugin 」フォルダ内に、簡単なプラグインのサンプル「 ExamplePlugin.java 」が同梱されています：

	( in plugin/ExamplePlugin.java )

	public class ExamplePlugin {
    
		public double pivar = 1.0;

		public double pifun(double arg) {
			return arg * 2.0;
		}
	}

The compilation of this plug-in is contained in the building procedure, so it might already be compiled.
If you modified the above code, it requires recompilation to use.
About the compilaton, see "<a href="#how-to-compile-plugins">Compile Plug-Ins</a>".

このプラグインは恐らくビルド時にコンパイルされてるはずです。内容を編集した場合は再コンパイルが必要です。
コンパイル方法については「<a href="#how-to-compile-plugins">プラグインのコンパイル</a>」をご参照ください。

After the compilation, embedded variables/functions are available in the expressions of the Step-1 and 2, 
and in the script code of the step-3.
For example:

コンパイル済の組み込み関数/変数は、Step-1 や Step-2 での計算式の中や、Step-3 でのライブラリスクリプトの中で使用できます。
例えば：

	INPUT:
	pifun(pivar)

	OUTPUT:
	2

For more detailed explanation to implement plug-in, 
see: "<a href="https://github.com/RINEARN/vnano#plugin">Plugin Development</a>" section in the document of the Vnano.
Classes specified in the setting script "Setting.vnano" of this software 
will be passed as arguments of the "put" method of the script engine of the Vnano, 
to be connected as plug-ins.

プラグインの実装方法についてのより詳細な解説は、Vnanoのドキュメント内の
「 <a href="https://github.com/RINEARN/vnano#plugin">プラグインの開発</a> 」セクションをご参照ください。
このソフトウェアの設定スクリプト「 Setting.vnano 」内で指定したクラスが、Vnanoのスクリプトエンジンの「 put 」メソッドに引数として渡され、プラグインとして接続されます。


<a id="about-us"></a>
## About Us - 開発元について

<div style="background-color:white; width: 890px; height: 356px; text-align:center; background-image: url('./rinearn.jpg'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/vnano/blob/master/rinearn.jpg" alt="" width="890" />
</div>


This software is developed by <a href="https://www.rinearn.com/">RINEARN</a> 
which is a studio in Japan developing software for data-analysis, visualization, computation, and so on.
Please feel free to contact us if you have any questions/feedbacks about this software.

このソフトウェアは、日本の開発スタジオである <a href="https://www.rinearn.com/">RINEARN</a> が開発しています。
RINEARNでは、主にデータ解析や可視化、計算向けのソフトウェアを開発しています。
このソフトウェアに関するご質問やご意見・ご感想などをお持ちの場合は、ご気軽にお問い合せください。

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


