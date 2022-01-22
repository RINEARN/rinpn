# RINPn のソフトウェアアーキテクチャ

&raquo; [English](./Architecture.md)

ここでは、ソースコードを把握する際の参考となる情報として、このソフトウェアの内部的なアーキテクチャの説明を行います。

<a id="architecture-abstract"></a>
## 概要とブロック図

RINPn の本体は、
Model / View / Presenter の3つの主要コンポーネントを軸に構成される、MVPパターンに基づくアーキテクチャを採用しています。
各コンポーネントは、それぞれ個別のパッケージとしてまとめられています。

また、RINPn 本体の実装とは完全に独立していますが、計算処理を担う Vnano のスクリプトエンジンも、ソフトウェア全体のアーキテクチャの観点では1つの重要なコンポーネントです。

下図は、各コンポーネントの関係を把握するためのブロック図です：

![Block Diagram](./img/architecture.jpg)

上図の通り、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
クラスがこのソフトウェアの実装の外枠で、その中で Model/View/Presenter の各コンポーネントが組み合わさって動いています。
以下では、各コンポーネントの役割を解説します。


<a id="architecture-model"></a>
## モデル ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/">com.rinearn.processornano.model</a> パッケージ )

このコンポーネントは、UI層を除いた、電卓としての機能面を提供します。
例えば、計算式が入力されると、その計算結果を出力として返します。


このコンポーネントの入出力は、GUIモードでは Presenter を介してイベント駆動で行われるため、処理の流れは少し複雑です（詳細は<a href="#architecture-presenter">Presenter の説明</a>参照）。
対して、CUIモードでの処理の流れは、連続的かつ同期的で、非常に単純です： 具体的には、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
クラスの main メソッド から calculate メソッドを介して、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> クラスの calculate メソッドが呼ばれます。
この引数に、コマンドラインから入力された計算式が渡されるので、
それをスクリプトエンジンに渡して計算して（詳細は<a href="#architecture-engine">Script Engine の説明</a>参照）、
結果を標準出力に表示するだけです。


<a id="architecture-view"></a>
## ビュー ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/">com.rinearn.processornano.view</a> パッケージ )

このコンポーネントは、GUIモードにおいて、ウィンドウやテキストフィールドなどで構成される、UIのグラフィカルな表面（見える部分）の役割を担います。
ただし、このコンポーネント自身は、UIからのイベントを処理しない事に留意が必要です（イベント処理は <a href="#architecture-presenter">Presenter</a> が担います）。


UI表面の実装は <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewImpl.java">ViewImpl</a> 
クラスによって提供され、このクラスのインスタンスには、View以外のコンポーネント（パッケージ）からは 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInterface.java">ViewInterface</a> 
インターフェースを介してアクセスされます。
ViewImpl クラスのインスタンスは、（"SwingUtilities.invokeAndWait" メソッドの機能を介して）イベントディスパッチスレッド上で
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInitializer.java">ViewInitializer</a> クラスを用いて初期化されます。


<a id="architecture-presenter"></a>
## プレゼンター ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/">com.rinearn.processornano.presenter</a> パッケージ )

このコンポーネントは、GUIモードにおいて、View と Model の間を仲介します。
このコンポーネント内の各クラスは、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/Presenter.java">Presenter</a> 
クラスの "link" メソッドによって Model と View の間に接続された後は、個々にイベント駆動で動作します。


ユーザーのUI操作による計算実行アクションには、まずこのコンポーネント内のUIイベントリスナ（
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunKeyListener.java">RunKeyListener</a> 
クラスや
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunButtonListener.java">RunButtonListener</a> 
クラスなど ）が反応します。
その処理で新しいスレッドが生成され、その上で非同期に計算を実行するために、model パッケージの 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationRunner.java">AsynchronousCalculationRunner</a> 
クラスのインスタンスが生成されます。
AsynchronousCalculationRunner クラスは、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a>
クラスの calculate メソッドを呼び出して計算を実行し、その結果を
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationListener.java">AsynchronousCalculationListener</a> インターフェースを実装した計算イベントリスナ（RunButtonクラス内で定義）に通知します。
すると、その計算イベントリスナが、ビューアップデータ（ 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/OutputFieldUpdater.java">OutputFieldUpdater</a> 
クラスなど 
）を介して、イベントディスパッチスレッド上でUIの表示を更新します。




<a id="architecture-engine"></a>
## スクリプトエンジン ( <a href="https://github.com/RINEARN/vnano/blob/master/src/org/vcssl/nano/">org.vcssl.nano</a> パッケージ )

このコンポーネントは、Model から要求された計算を実行する役割を担います。
スクリプトの実行や、プラグインとのやり取りも、このコンポーネントによって行われます。
なお、このコンポーネントは、アプリケーション組み込み用のスクリプトエンジン「 Vnano 」として、このソフトウェアとは独立に開発進行中のものです。
従って、このコンポーネント自身についての詳細は、そちらのドキュメントをご参照ください： 
<a href="https://github.com/RINEARN/vnano">https://github.com/RINEARN/vnano</a>


このコンポーネントは、Java&reg; Scripting API の javax.script.ScriptEngine インターフェースを介して、Model からアクセスされます。
CUIモードでは、
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
クラスの calculate メソッド内の処理において、スクリプトエンジンの eval メソッドが単純に呼び出されます。
この eval メソッドは、引数として渡された式（やスクリプト）の値を計算して、その結果を戻り値として返します。

GUIモードでは、ユーザーによって計算がリクエストされた際に 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationRunner.java">AsynchronousCalculationRunner</a> 
クラスのインスタンスが生成され、その run メソッドが別スレッドで実行されます。
そこから、先ほども述べた <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> クラスの calculate メソッドが実行され、その中でスクリプトエンジンの eval メソッドが呼び出されます
（<a href="#architecture-presenter">Presenter の説明</a>も参照）。



---

## 本文中の商標など

- OracleとJavaは、Oracle Corporation 及びその子会社、関連会社の米国及びその他の国における登録商標です。文中の社名、商品名等は各社の商標または登録商標である場合があります。

- その他、文中に使用されている商標は、その商標を保持する各社の各国における商標または登録商標です。


