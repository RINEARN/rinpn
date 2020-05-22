# RINPn


RINPn (RINEARN Processor nano) is a simple & compact programmable calculator.

RINPn (RINEARN Processor nano、発音「りんぷん」) は、シンプルでコンパクトなプログラマブル関数電卓です。


<div style="background-color:black; width: 890px; height: 463px; text-align:center; background-image: url('./signboard.jpg'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinpn/blob/master/signboard.jpg" alt="" width="890" />
</div>

### The Official Website - 公式サイト

- English: <a href="https://www.rinearn.com/en-us/rinpn/">https://www.rinearn.com/en-us/rinpn/</a>
- 日本語:   <a href="https://www.rinearn.com/ja-jp/rinpn/">https://www.rinearn.com/ja-jp/rinpn/</a>

<hr />


This README is for users who want to build this software from source code by yourself.
You can also get prebuilt-packages of this software from the above official website.

このREADMEの内容は、このソフトウェアをソースコードからビルドしたい方のためのものです。
上記公式サイトから、ビルド済みのパッケージも入手できます。


## Index - 目次
- <a href="#caution">Caution - 注意</a>
- <a href="#license">License - ライセンス</a>
- <a href="#requirements">Requirements - 必要な環境</a>
- <a href="#how-to-build">How to Build - ビルド方法</a>
	- <a href="#how-to-build-processor-nano">Build the RINPn - RINPn のビルド</a>
	- <a href="#how-to-build-vnano">Build the Vnano Engine - Vnanoエンジンのビルド</a>
	- <a href="#how-to-compile-plugins">Compile Plug-Ins - プラグインのコンパイル</a>
	    - <a href="#how-to-compile-official-plugins">Get and Compile Vnano Official Plug-Ins - Vnano公式プラグインの入手とコンパイル</a>
	    - <a href="#how-to-compile-user-plugins">Compile User Plug-Ins - ユーザープラグインのコンパイル</a>
- <a href="#how-to-use">How to Use - 使用方法</a>
	- <a href="#how-to-use-gui">How to Use in the GUI Mode - GUIモードでの使用方法</a>
	- <a href="#how-to-use-cui">How to Use in the CUI Mode - CUIモードでの使用方法</a>
	- <a href="#how-to-use-script">How to Execute Script Code - スクリプトを実行する</a>
	- <a href="#how-to-use-library">How to Define Variables and Functions as Script Code - スクリプトで変数や関数を定義する</a>
	- <a href="#how-to-implement-plugin">How to Implement Built-in Variables/Functions in Java&reg; - Java&reg;言語で組み込み変数/関数を実装する</a>
- <a href="#built-in">Built-in Functions and Variables - 組み込み関数/変数</a>
	- <a href="#built-in-functions">Built-in Functions - 組み込み関数</a>
	- <a href="#built-in-variables">Built-in Variables - 組み込み変数</a>
- <a href="#architecture">Architecture - アーキテクチャ</a>
	- <a href="#architecture-abstract">Abstract and a Block Diagram - 概要とブロック図</a>
	- <a href="#architecture-model">Model - モデル</a>
	- <a href="#architecture-view">View - ビュー</a>
	- <a href="#architecture-presenter">Presenter - プレゼンター</a>
	- <a href="#architecture-engine">Script Engine - スクリプトエンジン</a>
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
### Step-1. Build the RINPn - RINPn のビルド

Firstly, get and build source code of the RINPn.

はじめに、RINPn のソースコードを入手してビルドします。

	cd <working-directory>
	git clone https://github.com/RINEARN/rinpn.git
	cd rinpn

for Microsoft&reg; Windows&reg; :

	.\build.bat

for Linux&reg;, etc. :

	./build.sh

for Apache Ant :

    ant -f build.xml

If you succeeded to build, the JAR file "RINPn.jar" will be generated. 

ビルドが成功すると、JARファイル「 RINPn.jar 」が生成されます。


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
This JAR file is the Vnano Engine which is necessary for RINPn, 
so put the JAR file of the Vnano Engine "Vnano.jar" in the same directory as "RINPn.jar" :

ビルドが成功すると、JARファイル「 Vnano.jar 」が生成されます。
このJARファイルがVnanoエンジンで、RINPn の動作に必要なので、
「RINPn.jar」と同じフォルダ内に配置します：

	cd <working-directory>

for Microsoft&reg; Windows&reg; :

	copy .\vnano\Vnano.jar .\rinpn\Vnano.jar

for Linux&reg;, etc. :

	cp ./vnano/Vnano.jar ./rinpn/Vnano.jar


You can check whether "Vnano.jar" is recognized expectedly on "RINPn.jar" or not as follows :

以下のように、「 RINPn.jar 」から「 Vnano.jar 」が正しく認識されているかどうかを確認できます：

    cd <working-directory>/rinpn
    java -jar RINPn.jar --version

結果は：

The result is :

    OK:  RINPn Ver.0.2.0  / with Vnano Ver.0.2.2
    NG:  RINPn Ver.0.2.0

Note that numbers depend on the version in above results.

なお、上記結果の数字部分はバージョンによって異なります。


<a id="how-to-compile-plugins"></a>
### Step-3. Compile Plug-Ins - プラグインのコンパイル

Finally, compile plug-ins which provide built-in functions/variables to the Vnano Engine.
On this software, you can develop your original plug-ins. 
In addition, and some official plug-ins are provided on the repository of RINEARN, 

最後に、Vnanoエンジンに組み込み関数/変数を提供するプラグインをコンパイルします。
このソフトウェアでは、ユーザーが独自のプラグインを開発する事もできます。
加えて、RINEARN のリポジトリ上で、いくつかの公式プラグインが提供されています。

<a id="how-to-compile-official-plugins"></a>
#### 3-1. Get and Compile Vnano Official Plug-Ins - Vnano 公式プラグインの入手とコンパイル

Let's get and compile <a href="https://github.com/RINEARN/vnano-plugin">Vnano official plug-ins</a>: 

<a href="https://github.com/RINEARN/vnano-plugin">Vnano 公式プラグイン</a>を入手してコンパイルします：

	cd <working-directory>
	git clone https://github.com/RINEARN/vnano-plugin.git

The "vnano-plugin" folder will be generated, and in there "plugin" folder exists. so copy the all contents in the "plugin" folder into the "plugin" folder of the RINPn, and then compile them:

これでフォルダ「 vnano-plugin 」が生成されるため、その中の「 plugin 」フォルダの中身を、
RINPn の plugin フォルダ内にコピーし、コンパイルします：

for Microsoft&reg; Windows&reg; :

    cd <working-directory>
	xcopy /s .\vnano-plugin\plugin\* .\rinpn\plugin\
	cd rinpn\plugin
	javac -classpath ".;../Vnano.jar" -encoding UTF-8 @org\vcssl\nano\plugin\sourcelist.txt

for Linux&reg;, etc. :

    cd <working-directory>
	cp -r ./vnano-plugin/plugin/* ./rinpn/plugin/
	cd rinpn/plugin
	javac -classpath ".:../Vnano.jar" -encoding UTF-8 @org/vcssl/nano/plugin/sourcelist.txt


<a id="how-to-compile-user-plugins"></a>
#### 3-2. Compile User Plug-Ins - ユーザープラグインのコンパイル

"ExamplePlugin.java" in "plugin" folder is a simple example of the plug-in, 
assumed that users customize the code of it, or create new plug-ins by referencing code of it. 
We refer such plug-ins as "User Plug-ins".
Let's compile it as follows: 

「 plugin 」フォルダ内にある「 ExamplePlugin.java 」は、非常に単純なプラグインの実装例で、ユーザーがそのコード内容を改造したり、参考にして新しいプラグインを作成する事を想定しています。そのようなプラグインを「 ユーザープラグイン 」と呼びます。
以下のようにコンパイルします：

for Microsoft&reg; Windows&reg; :

    cd <working-directory>\rinpn\plugin
	javac -classpath ".;../Vnano.jar" -encoding UTF-8 ExamplePlugin.java

for Linux&reg;, etc. :

    cd <working-directory>/rinpn/plugin
	javac -classpath ".:../Vnano.jar" -encoding UTF-8 ExamplePlugin.java

If you have created/appended a new user plug-in, describe its file path in the content of the text file "VnanoPluginList.txt" in "plugin" folder, for loading it.

新しいユーザープラグインを作成/追加した際は、そのプラグインのファイルパスを、「 plugin 」フォルダ内にあるテキストファイル「 VnanoPluginList.txt 」内に記載（追記）してください。そうすると、そのプラグインが読み込まれるようになります。


<a id="how-to-use"></a>
## How to Use - 使用方法

<a id="how-to-use-gui"></a>
### Step-1. How to Use in the GUI Mode - GUIモードでの使用方法

In the GUI mode, you can take calculations on the graphical calculator window.
At first, execute "RINPn.jar" from the command-line terminal as follows:

GUIモードでは、グラフィカルな電卓画面上で計算を行う事ができます。
それにはまず、コマンドラインで以下のように「 RINPn.jar 」を実行します：

	cd <working-directory>/rinpn/
	java -jar RINPn.jar



By the way, if you register the path of "bin" folder to the environment variable "PATH" (or "Path") 
of your OS, wherever the current directory is, you can launch more simply as follows:

なお、OSの環境変数 PATH （または Path ）に「bin」フォルダのパスを登録しておけば、カレントディレクトリの場所に関わらず、以下のように簡単なコマンドで実行できるようになります：

	rinpn


Alternatively, if you are using the 
<a href="https://download.rinearn.com/advanced/#processor-nano">pre-built package</a> 
on the OS of the Microsoft&reg; Windows&reg;, you can execute this software by double-clicking the batch file "RINPn.bat".

または、もし Microsoft&reg; Windows&reg; のOS上で 
<a href="https://download.rinearn.com/advanced/#processor-nano">ビルド済みパッケージ</a> 
を使用している場合は、バッチファイル「 RINPn.bat 」をダブルクリックして実行する事も可能です。

When you execute this software as above ways, the calculator window (the image below) will be launched:

さて、上記のように実行すると、電卓画面（下図）が起動します: 

<div style="background-color:white; width: 700px; height: 300px; text-align:center; background-image: url('./gui_expression.png'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinpn/blob/master/gui_expression.png" alt="" width="700" />
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
To use the CUI mode, execute the "RINPn.jar" with passing an expression as a command-line argument as follows:

CUIモードでは、コマンドライン端末上で、電卓画面を起動せずにその場で計算を行う事ができます。
CUIモードを使用するには、コマンドラインで以下のように、計算式を引数として「 RinearnProcessor.jar 」を実行してください：

	cd <working-directory>/rinpn/
	java -jar RINPn.jar "(1 + 2 ) / 3 - 4 + 5"

	(result)
	2


If you register the path of "bin" folder to the environment variable "PATH" (or "Path") 
of your OS, wherever the current directory is, you can take calculations by more simply as follows:

ここでも、OSの環境変数 PATH （または Path ）に「bin」フォルダのパスを登録しておいた場合は、カレントディレクトリの場所に関わらず、以下のように簡単なコマンドで計算できるようになります：

	rinpn "( 1 + 2 ) / 3 - 4 + 5"

	(result)
	2


<a id="how-to-use-script"></a>
### Step-3. How to Execute Script Code - スクリプトを実行する

The RINPn can execute a script file written in the 
"<a href="https://www.vcssl.org/en-us/vnano/">Vnano</a>", 
to perform procedural, algorithmic, or other complicated calculations.
The vnano is a simple programming language with C-like syntax.
For details of syntax and so on of the Vnano, see: "<a href="https://www.vcssl.org/en-us/vnano/doc/tutorial/language">Features of the Vnano as a Language</a>".
To create a script of the Vnano, no installation of development environments such as the compiler is required. Simply create a text file of which name ends with the extention ".vnano", and write code in there.

RINPn では、手続き的な計算やアルゴリズム的な計算、またはその他の複雑な計算処理を行いたい場合のために、「 <a href="https://www.vcssl.org/ja-jp/vnano/">Vnano</a> 」という言語で記述したスクリプトファイルを実行する事もできます。Vnano は、C言語系の文法を持つ簡易プログラミング言語で、
具体的な記法などについては「 <a href="https://www.vcssl.org/ja-jp/vnano/doc/tutorial/language">言語としてのVnano</a> 」をご参照ください。
Vnano のスクリプトを作成するために、コンパイラなどの特別な開発環境の導入は不要です。拡張子「 .vnano 」で終わる名前のテキストファイルを作成して、その中にコードを書くだけでOKです。

As an example, the following script file is bundled in this repository 
(and in the downloaded package).
This example script calculates the numerical integration value of cos(x) from 0 to 1:

記述例として、以下のスクリプトファイル「 Example.vnano 」がこのリポジトリ内（およびダウンロードパッケージ内）に同梱されています。このサンプルスクリプトは、cos(x) の 0 から 1 までの数値積分値を計算する内容になっています：

	( in Example.vnano )

	// Integration parameters - 積分パラメータ
	float A = 0.0;
	float B = 1.0;
	int N = 100000;

	// Integrant function - 被積分関数
	float f(float x) {
    	return cos(x);
	}

	// Perform integration - 積分を実行
	float delta = (B - A) / N;
	float value = 0.0;
	for(int i=0; i<N; ++i) {
    	float x = A + i * delta;
    	value += ( f(x) + f(x+delta) + 4.0 * f(x+delta/2.0) ) * delta / 6.0;
	}

	// Output result - 結果を出力
	output(value);

To execute this script, input the script file name (or path) into "INPUT" text-field as follows, and press Enter key. 
Then the script will be executed and its output value will be displayed on "OUTPUT" text-field: 

このスクリプトを実行するには、スクリプトファイルの名前（またはパス）を「 INPUT 」欄に下図のように入力して、 Enter キーを押します。すると実行され、「 OUTPUT 」欄にスクリプトの出力値が表示されます：

<div style="background-color:white; width: 700px; height: 300px; text-align:center; background-image: url('./gui_script.png'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinpn/blob/master/gui_script.png" alt="" width="700" />
</div>

The above output value "0.8414709848" is matched well with the value of sin(1) which is the theoretical integration value of cos(x) from 0 to 1, so it indicates that the above example script has run correctly.

この出力値「 0.8414709848 」は、cos(x) の 0 から 1 までの積分の理論値である sin(1) とよく一致しており、
上記サンプルスクリプトが正しく実行された事がわかります。

If you want to execute a script file in the different folder with the folder at which you has launched the RINPn, it is necessary to input the absolute file path ("C:\\...\\Example.vnano", "/home/.../Example.vnano", etc.) of the script to execute it.
On the other hand, if the path of "bin" folder is registered to the environment variable "PATH" (or "Path"), you can execute a script in any folder easily. Firstly "cd" to the folder in which the script is locating, and then pass the name of the script file to "rinpn" command:

RINPn を起動したフォルダとは別の場所にあるスクリプトを実行したい場合、通常はそのスクリプトファイルの絶対パス（例えば "C:\\...\\Example.vnano" や "/home/.../Example.vnano" など）を入力する必要があります。
そこで、OSの環境変数 PATH （または Path ）に「bin」フォルダのパスを登録しておくと、
次のようにスクリプトのあるフォルダに cd した後は、rinpn コマンドにスクリプト名を渡すだけで実行できるようになります：

	cd <folder>
	rinpn "Example.vnano"

	(result)
	0.8414709848

where \<folder\> is the path of the folder in which Example.vnano (or your script to be executed) is locating.

ここで上記の \<folder\> は、Example.vnano （または実行したいスクリプトファイル）があるフォルダのパスを表しています。


<a id="how-to-use-library"></a>
### Step-4. How to Define Variables and Functions as Script Code - スクリプトで変数や関数を定義する

You can define variables and functions in the script file "ExampleLibrary.vnano" in "lib" folder, and use them in expressions of the Step-1 and 2, and in scripts of the Step-3. 
For example, the default content of "ExampleLibrary.vnano" is as follows:

「 lib 」フォルダ内にあるスクリプトファイル「 ExampleLibrary.vnano 」の中で、変数や関数を定義すると、それを Step-1 や 2 での計算式の中や、Step-3 のスクリプト内で使用できます。
例えば、標準状態での「 ExampleLibrary.vnano 」の記述内容は以下の通りです：

	( in lib/ExampleLibrary.vnano )

	float libvar = 2.0;

	float libfun(float x) {
		float result = libvar * x + 1;
		return result;
	}

In the the expression/script inputted to the the calculator (see Step-1, 2, and 3), you can use variables and functions defined in the above "ExampleLibrary.vnano", as the following example:

Step-1 から 3 までで扱った、電卓に入力する計算式やスクリプトの中で、上記の「 ExampleLibrary.vnano 」内で定義されている変数や関数を、以下の例のように使用できます：

	INPUT:
	libvar

	OUTPUT:
	2

	INPUT:
	libfun(1.23)

	OUTPUT:
	3.46

Like "ExampleLibrary.vnano", a script for providing functions/variables is referred as "library script" on the RINPn and the Vnano. 
If you want, you can create other library script files and can define variables and functions in them. 
When you have created/appended new library script files, describe its file path in the content of the text file "VnanoLibraryList.txt" in "lib" folder, for loading it.

この「 ExampleLibrary.vnano 」のように、関数や変数の提供を目的とするスクリプトの事を、RINPn および Vnano では「ライブラリスクリプト」と呼びます。
必要に応じて、別の新しいライブラリスクリプトのファイルを作成し、その中で変数や関数を定義する事もできます。
新しいライブラリスクリプトファイルを作成/追加した際は、そのファイルパスを、「 lib 」フォルダ内にあるテキストファイル「 VnanoLibraryList.txt 」内に記載（追記）してください。
そうすると、そのライブラリスクリプトが読み込まれるようになります。


<a id="how-to-implement-plugin"></a>
### Step-5. How to Implement Built-in Variables/Functions in Java&reg; - Java&reg;言語で組み込み変数/関数を実装する

You can implement new built-in variables and function in the Java&reg; programming language.
In this way, compared to defining variables/functions as script code (in the step-4), 
high-functionality of Java&reg; might be the great merit.
On the other hand, it requres Java&reg; Development Kit (JDK) to compile implemented code.

Java&reg;言語を用いて、新しい組み込み関数/変数を実装する事もできます。
この方法では、Step-4 のようにスクリプトで関数や変数を定義する事と比べて、
Java&reg; 言語の高い機能性を利用できる事が大きなメリットになるかもしれません。
一方で、実装したコードをコンパイルする際に Java&reg; 言語の開発環境 (JDK) が必要になります。

On the RINPn and the Vnano, we refer a Java&reg; program to provide built-in functions/variables as "plug-in".
A simple example of a plug-in is bundled in "plugin" folder as "ExamplePlugin.java":

RINPn および Vnano では、組み込み関数/変数を提供する事を目的とした、Java&reg; 言語で記述されたプログラムの事を「プラグイン」と呼びます。
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
About the compilaton, see "<a href="#how-to-compile-user-plugins">Compile User Plug-Ins</a>".

このプラグインは恐らくビルド時にコンパイルされてるはずです。内容を編集した場合は再コンパイルが必要です。
コンパイル方法については「<a href="#how-to-compile-user-plugins">ユーザープラグインのコンパイル</a>」をご参照ください。

After the compilation, built-in variables/functions are available in the expression inputted to the calculator (see Step-1 and 2), 
and in the script code (see step-3 and 4).
For example:

コンパイル済みの組み込み関数/変数は、Step-1 や 2 で扱った計算式の中や、Step-3 や 4 で扱ったスクリプトコードの中で使用できます。
例えば：

	INPUT:
	pifun(pivar)

	OUTPUT:
	2

For more detailed explanation to implement plug-in, 
see: "<a href="https://www.vcssl.org/en-us/vnano/doc/tutorial/plugin">Plugin Development</a>" section in the document of the Vnano.

プラグインの実装方法についてのより詳細な解説は、Vnanoのドキュメント内の
「 <a href="https://www.vcssl.org/ja-jp/vnano/doc/tutorial/plugin">プラグインの開発</a> 」セクションをご参照ください。

On the RINPn, class files specified in the text file "VnanoPluginList.txt" in "plugin" folder will be connected to the script engine of the Vnano as plug-ins.
If you have created/appended a new user plug-in, describe its file path in the content of "VnanoPluginList.txt".

RINPnでは、「 plugin 」フォルダ内のテキストファイル「 VnanoPluginList.txt 」内で指定したクラスファイルが、Vnanoのスクリプトエンジンにプラグインとして接続されます。
新しいユーザープラグインを作成/追加した際は、そのプラグインのファイルパスを「 VnanoPluginList.txt 」内に記載（追記）してください。


<a id="built-in"></a>
## Built-in Functions and Variables - 組み込み関数/変数


<a id="built-in-functions"></a>

On this software, following functions and variables are available by default.

このソフトウェアでは、以下の関数および変数が標準で利用できます。

### Built-in Functions - 組み込み関数

<dl style="margin-left: 30px;">
	<dt style="display: list-item;">rad( degree )</dt>
	<dd>
		<p>
		The conversion function from degree to radian. Example:
		<br />
		度からラジアンへの変換関数です。例：
		</p>
		<p>
		rad( 180.0 )
		</p>
	</dd>
	<dt style="display: list-item;">deg( radian )</dt>
	<dd>
		<p>
		The conversion function from radian to degree. Example:
		<br />
		ラジアンから度への変換関数です。例：
		</p>
		<p>
		deg( 2.0 * PI )
		</p>
	</dd>
	<dt style="display: list-item;">sin( x )</dt>
	<dd>
		<p>
		The sine function. The unit of the argument "x" is radian. Example:
		<br />
		正弦（サイン）関数です。引数 x の単位はラジアンです。例：
		</p>
		<p>
		sin( PI / 2.0 )
		</p>
	</dd>
	<dt style="display: list-item;">cos( x )</dt>
	<dd>
		<p>
		The cosine function. The unit of the argument "x" is radian. Example:
		<br />
		余弦（コサイン）関数です。引数 x の単位はラジアンです。例：
		</p>
		<p>
		cos( 2.0 * PI )
		</p>
	</dd>
	<dt style="display: list-item;">tan( x )</dt>
	<dd>
		<p>
		The tangent function. The unit of the argument "x" is radian. Example:
		<br />
		正接（タンジェント）関数です。引数 x の単位はラジアンです。例：
		</p>
		<p>
		tan( PI / 4.0 )
		</p>
	</dd>
	<dt style="display: list-item;">asin( x )</dt>
	<dd>
		<p>
		The inverse function of sine (arc-sine). The unit of the result is radian. Example:
		<br />
		正弦関数の逆関数（アークサイン）です。結果の単位はラジアンです。例：
		</p>
		<p>
		asin( 1.0 )
		</p>
	</dd>
	<dt style="display: list-item;">acos( x )</dt>
	<dd>
		<p>
		The inverse function of cosine (arc-cosine). The unit of the result is radian.  Example:
		<br />
		余弦関数の逆関数（アークコサイン）です。結果の単位はラジアンです。例：
		</p>
		<p>
		acos( 1.0 )
		</p>
	</dd>
	<dt style="display: list-item;">atan( x )</dt>
	<dd>
		<p>
		The inverse function of tangent (arc-tangent). The unit of the result is radian. Example:
		<br />
		正接関数の逆関数（アークタンジェント）です。結果の単位はラジアンです。例：
		</p>
		<p>
		atan( 1.0 )
		</p>
	</dd>
	<dt style="display: list-item;">sqrt( x )</dt>
	<dd>
		<p>
		The square-root function. Example:
		<br />
		平方根を求める関数です。例：
		</p>
		<p>
		sqrt( 4.0 )
		</p>
	</dd>
	<dt style="display: list-item;">ln( x )</dt>
	<dd>
		<p>
		The logarithm function with the base "e" (napier number). Example:
		<br />
		自然数 e を底とする対数関数です。例：
		</p>
		<p>
		ln( 10.0 )
		</p>
	</dd>
	<dt style="display: list-item;">log10( x )</dt>
	<dd>
		<p>
		The logarithm function with the base "10". Example:
		<br />
		10 を底とする対数関数です。例：
		</p>
		<p>
		log10( 1000.0 )
		</p>
	</dd>
	<dt style="display: list-item;">pow( x, exponent )</dt>
	<dd>
		<p>
		The function which returns the value of "x" to "exponent"-power. Example:
		<br />
		x の exponent 乗を求める関数です。例：
		</p>
		<p>
		pow( 2.0, 3.0 )
		</p>
	</dd>
	<dt style="display: list-item;">exp( exponent )</dt>
	<dd>
		<p>
		The function which returns the value of "e" (napier number) to "exponent"-power. Example:
		<br />
		自然数 e の exponent 乗を求める関数です。例：
		</p>
		<p>
		exp( 1.2 )
		</p>
	</dd>
	<dt style="display: list-item;">abs( x )</dt>
	<dd>
		<p>
		The absolute-value function. Example:
		<br />
		絶対値関数です。例：
		</p>
		<p>
		abs( -1.23 )
		</p>
	</dd>
	<dt style="display: list-item;">sum( ... )</dt>
	<dd>
		<p>
		The summation function. Example:
		<br />
		和を求める関数です。例：
		</p>
		<p>
		sum( 1.23 , &nbsp; 4.56 , &nbsp; 7.89 )
		</p>
	</dd>
	<dt style="display: list-item;">mean( ... )</dt>
	<dd>
		<p>
		The mean-value (arithmetic mean) function. Example:
		<br />
		平均値（算術平均）を求める関数です。例：
		</p>
		<p>
		mean( 1.23 , &nbsp; 4.56 , &nbsp; 7.89 )
		</p>
	</dd>
	<dt style="display: list-item;">van( ... )</dt>
	<dd>
		<p>
		The variance function ( denominator : n ). Example:
		<br />
		分散（ 分母： n ）を求める関数です。例：
		</p>
		<p>
		van( 1.23 , &nbsp; 4.56 , &nbsp; 7.89 )
		</p>
	</dd>
	<dt style="display: list-item;">van1( ... )</dt>
	<dd>
		<p>
		The variance function ( denominator : n-1 ). Example:
		<br />
		分散（ 分母： n-1 ）を求める関数です。例：
		</p>
		<p>
		van1( 1.23 , &nbsp; 4.56 , &nbsp; 7.89 )
		</p>
	</dd>
	<dt style="display: list-item;">sdn( ... )</dt>
	<dd>
		<p>
		The standard-deviation function ( denominator : n ). Example:
		<br />
		標準偏差（ 分母： n ）を求める関数です。例：
		</p>
		<p>
		sdn( 1.23 , &nbsp; 4.56 , &nbsp; 7.89 )
		</p>
	</dd>
	<dt style="display: list-item;">sdn1( ... )</dt>
	<dd>
		<p>
		The standard-deviation function ( denominator : n-1 ). Example:
		<br />
		標準偏差（ 分母： n-1 ）を求める関数です。例：
		</p>
		<p>
		sdn1( 1.23 , &nbsp; 4.56 , &nbsp; 7.89 )
		</p>
	</dd>
	<dt style="display: list-item;">length( array, dim )</dt>
	<dd>
		<p>
		The function which returns length of the "dim"-th dimension of an "array".
		<br />
		配列 array における、dim 番目の次元の要素数を返す関数です。
		</p>
		<p>
		length( array, 0 )
		</p>
	</dd>
	<dt style="display: list-item;">output( value )</dt>
	<dd>
		<p>
		The function to display the calculated value of scripts.
		On GUI mode, the value will be displayed on "OUTPUT" text-field (so when this function is called multiple times, the displayed value will be overwritten).
		On CUI mode, the value will be outputted on the standard-output as a line.
		<br />
		スクリプトの計算結果の値を表示するための関数です。
		GUIモードでは、値は「 OUTPUT」 テキストフィールドに表示されます（複数回呼ぶと、表示内容は単純に上書きされます）。
		CUIモードでは、値は標準出力に 1 行で出力されます。
		</p>
		<p>
		output( 1.23 )
		</p>
	</dd>
</dl>

### Built-in Variables - 組み込み変数

<dl style="margin-left: 30px;">
	<dt style="display: list-item;">PI</dt>
	<dd>
		<p>
		The constant variable storing the value of the circle ratio π. Value: 
		<br />
		円周率 π の値を保持する変数（定数）です。値：
		</p>
		<p>
		3.141592653589793
		</p>
	</dd>
</dl>

<a id="architecture"></a>
## Architecture - アーキテクチャ

In this section, we will explain the internal architecture of this software, which help you to grasp the global structure of the implementation before reading source code.

ここでは、ソースコードを把握する際の参考となる情報として、このソフトウェアの内部的なアーキテクチャの説明を行います。

<a id="architecture-abstract"></a>
### Abstract and a Block Diagram - 概要とブロック図

The architecture of the RINPn adopts the MVP pattern which consists mainly of 3 core components: Model, View, and Presenter.
Each component is packed as a package.

RINPn の本体は、
Model / View / Presenter の3つの主要コンポーネントを軸に構成される、MVPパターンに基づくアーキテクチャを採用しています。
各コンポーネントは、それぞれ個別のパッケージとしてまとめられています。


In addition, although it is completely independent from the implementation of the RINPn, the script engine of the Vnano to take calculations is also an important component from the point of view of the architecture of the whole software.

また、RINPn 本体の実装とは完全に独立していますが、計算処理を担う Vnano のスクリプトエンジンも、ソフトウェア全体のアーキテクチャの観点では1つの重要なコンポーネントです。


The following is a block diagram to grasp relationship between components we mentioned above:

下図は、各コンポーネントの関係を把握するためのブロック図です：

<div style="background-color:black; width: 640px; height: 830px; text-align:center; background-image: url('./architecture.jpg'); background-repeat: no-repeat; background-size: contain;">
	<img src="https://github.com/RINEARN/rinpn/blob/master/architecture.jpg" alt="" width="640" />
</div>

As in the above diagram, 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
class plays the role of the outer frame of implementation of this software, 
and in there Model/View/Presenter components are combined and work together.
In the following, we will explain roles of components.

上図の通り、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
クラスがこのソフトウェアの実装の外枠で、その中で Model/View/Presenter の各コンポーネントが組み合わさって動いています。
以下では、各コンポーネントの役割を解説します。


<a id="architecture-model"></a>
### Model - モデル ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/">com.rinearn.processornano.model</a> package )

This component provides the functional aspects of the calculator, excepting the UI.
For example, this component takes a calculation expression as an input, and return the calculated result as an output.

このコンポーネントは、UI層を除いた、電卓としての機能面を提供します。
例えば、計算式が入力されると、その計算結果を出力として返します。

In the GUI mode, input/output (I/O) to this component are performed in event-driven ways through the Presenter, so it is a little difficult to grasp the processing flow (see <a href="#architecture-presenter">the explanation of the Presenter</a> for details).
In the contrast, the processing flow in the CUI mode is continuous and synchronized, so it is very easy to grasp: Firstly, 
"calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
class will be called from the "calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
class, which will be called from "main" method.
The calculation expression inputted from the command-line will be passed as an argument of the method, so then next, it takes the calculation by calling the script engine (see <a href="#architecture-engine">the explanation of the script engine</a> for details), and output the result to the standard-output.

このコンポーネントの入出力は、GUIモードでは Presenter を介してイベント駆動で行われるため、処理の流れは少し複雑です（詳細は<a href="#architecture-presenter">Presenter の説明</a>参照）。
対して、CUIモードでの処理の流れは、連続的かつ同期的で、非常に単純です： 具体的には、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
クラスの main メソッド から calculate メソッドを介して、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> クラスの calculate メソッドが呼ばれます。
この引数に、コマンドラインから入力された計算式が渡されるので、
それをスクリプトエンジンに渡して計算して（詳細は<a href="#architecture-engine">Script Engine の説明</a>参照）、
結果を標準出力に表示するだけです。


<a id="architecture-view"></a>
### View - ビュー ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/">com.rinearn.processornano.view</a> package )

In the GUI mode, this component play the role of the graphical surface of the UI, which composed of a window, text fields, and so on.
Please note that this component does NOT handle any events from the UI by itself (it is a role of the <a href="#architecture-presenter">Presenter</a>).

このコンポーネントは、GUIモードにおいて、ウィンドウやテキストフィールドなどで構成される、UIのグラフィカルな表面（見える部分）の役割を担います。
ただし、このコンポーネント自身は、UIからのイベントを処理しない事に留意が必要です（イベント処理は <a href="#architecture-presenter">Presenter</a> が担います）。

The implementation of the surface of the UI is provided by 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewImpl.java">ViewImpl</a> class, 
and an instance of this class is accessed through 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInterface.java">ViewInterface</a> interface 
from outside of the View component (package).
An instance of ViewImpl class is initialized by 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInitializer.java">ViewInitializer</a> class on the event-dispatching thread  (by using the feature of "SwingUtilities.invokeAndWait" method).

UI表面の実装は <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewImpl.java">ViewImpl</a> 
クラスによって提供され、このクラスのインスタンスには、View以外のコンポーネント（パッケージ）からは 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInterface.java">ViewInterface</a> 
インターフェースを介してアクセスされます。
ViewImpl クラスのインスタンスは、（"SwingUtilities.invokeAndWait" メソッドの機能を介して）イベントディスパッチスレッド上で
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInitializer.java">ViewInitializer</a> クラスを用いて初期化されます。


<a id="architecture-presenter"></a>
### Presenter - プレゼンター ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/">com.rinearn.processornano.presenter</a> package )

This component mediates between the Model and the View.
Classes in this component will behave individually in event-driven ways, after they are linked to the Model and the View by "link" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/Presenter.java">Presenter</a> 
class.

このコンポーネントは、GUIモードにおいて、View と Model の間を仲介します。
このコンポーネント内の各クラスは、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/Presenter.java">Presenter</a> 
クラスの "link" メソッドによって Model と View の間に接続された後は、個々にイベント駆動で動作します。


The action to the UI by the user to take a calculation will be catched by UI event listeners ( e.g. 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunKeyListener.java">RunKeyListener</a> 
class, 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunButtonListener.java">RunButtonListener</a> 
class, and so on
) in this component.
Then the listener will request to the Model to take the calculation asynchronously on an other thread, by calling "calculateAsynchronously" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a>. 
Also, as an argument of the method, the lisner will create and passe an new event listener which implements
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationListener.java">AsynchronousCalculationListener</a> 
interface, to catch the event notifying the finishing of the calculation 
and invoke subsequent procedures.
After the calculation will have been finished, view updaters ( e.g. 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/OutputFieldUpdater.java">OutputFieldUpdater</a>, and so on	
) will update the UI on the event-dispatching thread  (by using the feature of "SwingUtilities.invokeAndWait" method).




ユーザーのUI操作による計算実行アクションには、まずこのコンポーネント内のUIイベントリスナ（
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunKeyListener.java">RunKeyListener</a> 
クラスや
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunButtonListener.java">RunButtonListener</a> 
クラスなど ）が反応します。
そこから、別スレッドで非同期に計算を行うリクエストが、Model 内の
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
クラスの calculateAsynchronously メソッドに投げられます。
その際、計算完了時に通知を受け取って、後に続く処理を行うために、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationListener.java">AsynchronousCalculationListener</a> インターフェースを実装したイベントリスナが引数として渡されます。
計算完了後は、（"SwingUtilities.invokeAndWait" メソッドの機能を介して）イベントディスパッチスレッド上で、ビューアップデータ（ 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/OutputFieldUpdater.java">OutputFieldUpdater</a> 
クラスなど 
）がUIの表示を更新します。




<a id="architecture-engine"></a>
### Script Engine - スクリプトエンジン ( <a href="https://github.com/RINEARN/vnano/blob/master/src/org/vcssl/nano/">org.vcssl.nano</a> package )

This component takes calculations requested by the Model. 
Executions of scripts, and communications with plug-ins, are also taken by this component.
By the way, this component is being developed independently as the compact script engine "Vnano" for embedded use in applications.
Therefore, for details of this component itself, see the document of: <a href="https://github.com/RINEARN/vnano">https://github.com/RINEARN/vnano</a>

このコンポーネントは、Model から要求された計算を実行する役割を担います。
スクリプトの実行や、プラグインとのやり取りも、このコンポーネントによって行われます。
なお、このコンポーネントは、アプリケーション組み込み用のスクリプトエンジン「 Vnano 」として、このソフトウェアとは独立に開発進行中のものです。
従って、このコンポーネント自身についての詳細は、そちらのドキュメントをご参照ください： 
<a href="https://github.com/RINEARN/vnano">https://github.com/RINEARN/vnano</a>


This component is accessed from the Model through javax.script.ScriptEngine interface of Java&reg; Scripting API.
In the CUI mode, "eval" method of the script engine is simply called in the processing of the "calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> class.
The "eval" method takes a calculation expression (or script) as an argument, and returns the calculated result as a return-value.

このコンポーネントは、Java&reg; Scripting API の javax.script.ScriptEngine インターフェースを介して、Model からアクセスされます。
CUIモードでは、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
クラスの calculate メソッド内の処理において、スクリプトエンジンの eval メソッドが単純に呼び出されます。
この eval メソッドは、引数として渡された式（やスクリプト）の値を計算して、その結果を戻り値として返します。

In the GUI mode, in the processing of "calculateAsynchronously" method of
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
class, an instance of <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationRunner.java">AsynchronousCalculationRunner</a> class will be created, and "run" method of the class will be invoked on an other thread.
From there, "calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a>
class which we mentioned above will be called, and "eval" method of the script engine will be called in there (see also: <a href="#architecture-presenter">the explanation of the Presenter</a>).

GUIモードでは、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
クラスの calculateAsynchronously メソッド内において、まず 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationRunner.java">AsynchronousCalculationRunner</a> 
クラスのインスタンスが生成され、その run メソッドが別スレッドで実行されます。
そこから、先ほども述べた <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> クラスの calculate メソッドが実行され、その中でスクリプトエンジンの eval メソッドが呼び出されます
（<a href="#architecture-presenter">Presenter の説明</a>も参照）。


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
	<dt style="display: list-item; margin-left:40px;">
		"小型関数電卓 RINPn（旧称リニアンプロセッサー nano）の公式ページを開設" - RINEARN Website (2019/10/02)
	</dt>
	<dd>
		<a href="https://www.rinearn.com/ja-jp/info/news/2019/1002-rinpn-website">https://www.rinearn.com/ja-jp/info/news/2019/1002-rinpn-website</a>
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


