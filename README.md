# RINPn


RINPn (abbreviated of "RINEARN Processor nano") is a simple & compact programmable calculator.

RINPn （RINEARN Processor nano の略称、発音「りんぷん」）は、シンプルでコンパクトなプログラマブル関数電卓です。


<div style="background-color:black; width: 890px; height: 463px; text-align:center; background-image: url('./signboard.jpg'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinpn/blob/master/signboard.jpg" alt="" width="890" />
</div>

This README is for users who want to build this software from source code by yourself.
You can also get prebuilt-packages of this software from: 
<a href="https://download.rinearn.com/advanced/#rinpn">https://download.rinearn.com/advanced/#rinpn</a>

このREADMEの内容は、このソフトウェアをソースコードからビルドしたい方のためのものです。
<a href="https://download.rinearn.com/advanced/#rinpn">https://download.rinearn.com/advanced/#rinpn</a>
からビルド済みのパッケージも入手できます。

<hr />



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
	- <a href="#how-to-use-library">How to Define Variables and Functions as Script Code - スクリプトで変数や関数を定義する</a>
	- <a href="#how-to-implement-plugin">How to Implement Embedded Variables/Functions in Java&reg; - Java&reg;言語で組み込み変数/関数を実装する</a>
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


<a id="how-to-compile-plugins"></a>
### Step-3. Compile Plug-Ins - プラグインのコンパイル

Finally, compile plug-ins which provide embedded-functions/variables to the Vnano Engine.
On this software, you can develop your original plug-ins. 
In addition, and some official plug-ins are provided on the repository of RINEARN, 

最後に、Vnanoエンジンに組み込み関数/変数を提供するプラグインをコンパイルします。
このソフトウェアでは、ユーザーが独自のプラグインを開発する事もできます。
加えて、RINEARN のリポジトリ上で、いくつかの公式プラグインが提供されています。

<a id="how-to-compile-official-plugins"></a>
#### 3-1. Get and Compile Vnano Official Plug-Ins - Vnano公式プラグインの入手とコンパイル

Let's get and compile official plug-ins: 

公式プラグインを入手してコンパイルします：

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
on the OS of the Microsoft&reg; Windows&reg;, you can execute this software by double-clicking the batch file "RinearnProcessorNano.bat".

または、もし Microsoft&reg; Windows&reg; のOS上で 
<a href="https://download.rinearn.com/advanced/#processor-nano">ビルド済みパッケージ</a> 
を使用している場合は、バッチファイル「 RinearnProcessorNano.bat 」をダブルクリックして実行する事も可能です。

When you execute this software as above ways, the calculator window (the image below) will be launched:

さて、上記のように実行すると、電卓画面（下図）が起動します: 

<div style="background-color:white; width: 700px; height: 300px; text-align:center; background-image: url('./ui.png'); background-repeat: no-repeat; background-size: contain;">
  <img src="https://github.com/RINEARN/rinpn/blob/master/ui.png" alt="" width="700" />
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
About the compilaton, see "<a href="#how-to-compile-user-plugins">Compile User Plug-Ins</a>".

このプラグインは恐らくビルド時にコンパイルされてるはずです。内容を編集した場合は再コンパイルが必要です。
コンパイル方法については「<a href="#how-to-compile-user-plugins">ユーザープラグインのコンパイル</a>」をご参照ください。

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

<div style="background-color:black; width: 640px; height: 840px; text-align:center; background-image: url('./architecture.jpg'); background-repeat: no-repeat; background-size: contain;">
	<img src="https://github.com/RINEARN/rinpn/blob/master/architecture.jpg" alt="" width="700" />
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
Executions of library scripts, and communications with plug-ins, are also taken by this component.
By the way, this component is being developed independently as the compact script engine "Vnano" for embedded use in applications.
Therefore, for details of this component itself, see the document of: <a href="https://github.com/RINEARN/vnano">https://github.com/RINEARN/vnano</a>

このコンポーネントは、Model から要求された計算を実行する役割を担います。
ライブラリスクリプトの実行や、プラグインとのやり取りも、このコンポーネントによって行われます。
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


