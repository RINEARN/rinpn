# RINPn のソフトウェアアーキテクチャ

&raquo; [English](./Architecture.md)

ここでは、ソースコードを把握する際の参考となる情報として、このソフトウェアの内部的なアーキテクチャの説明を行います。

<a id="architecture-abstract"></a>
## 概要とブロック図

RINPn の本体は、
Model / View / Presenter の3つの主要コンポーネントを軸に構成される、MVPパターンに基づくアーキテクチャを採用しています。
各コンポーネントは、<a href="https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/">com.rinearn.rinpn</a> パッケージ内に、それぞれ単一のクラスとして実装されています。

また、RINPn 本体の実装とは完全に独立していますが、計算処理を担う Vnano のスクリプトエンジンも、ソフトウェア全体のアーキテクチャの観点では1つの重要なコンポーネントです。

下図は、各コンポーネントの関係を把握するためのブロック図です：

![Block Diagram](./img/architecture.jpg)

上図の通り、
<a href="https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/RINPn.java">RINPn</a> 
クラスがこのソフトウェアの実装の外枠で、その中で Model/View/Presenter の各コンポーネントが組み合わさって動いています。
以下では、各コンポーネントの役割を解説します。


<a id="architecture-model"></a>
## Model ( <a href="https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/Model.java">com.rinearn.rinpn.Model</a> クラス )

Model は、UI部分を除いた、電卓としての機能面を提供するコンポーネントです。
例えば、計算式が入力されると、その計算結果を出力として返します
（計算処理そのものについては、さらに下層のスクリプトエンジンに投げて実行します）

Model の処理は、CUIモードでは、RINPn クラスから直接呼ばれます。一方でGUIモードでは、ユーザーのUI操作に応じて、イベント駆動で Presenter から呼ばれます。


<a id="architecture-view"></a>
## View ( <a href="https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/View.java">com.rinearn.rinpn.View</a> クラス )

View は、GUIモードにおいて、ウィンドウやテキストフィールドなどで構成される、UIのグラフィカルな表面（見える部分）の役割を担うコンポーネントです。

ただし、View 自身は、UI部品が操作されてもイベントを処理しません。それは後述の Presenter が担います。
View は、単にUI部品をまとめて保持しているだけで、処理らしい処理は行いません。



<a id="architecture-presenter"></a>
## Presenter ( <a href="https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/presenter/">com.rinearn.rinpn.Presenter</a> クラス )

Presenter は、GUIモードにおいて、View と Model の間を仲介するコンポーネントです。

Presenter クラスは、内部クラスとして各種のイベントリスナーを持っており、それらが View のUI部品の操作に反応して、Model を呼び出して計算処理を実行し、完了すると View を更新して結果を表示します。



<a id="architecture-engine"></a>
## スクリプトエンジン ( <a href="https://github.com/RINEARN/vnano/blob/master/src/org/vcssl/nano/">org.vcssl.nano</a> パッケージ )

スクリプトエンジンは、Model から要求された計算を実行する役割を担います。
スクリプトの実行や、プラグインとのやり取りも、このコンポーネントによって行われます。
なお、このコンポーネントは、アプリケーション組み込み用のスクリプトエンジン「 Vnano 」として、このソフトウェアとは独立に開発進行中のものです。
従って、このコンポーネント自身についての詳細は、そちらのドキュメントをご参照ください： 
<a href="https://github.com/RINEARN/vnano">https://github.com/RINEARN/vnano</a>



---

## 本文中の商標など

- OracleとJavaは、Oracle Corporation 及びその子会社、関連会社の米国及びその他の国における登録商標です。文中の社名、商品名等は各社の商標または登録商標である場合があります。

- その他、文中に使用されている商標は、その商標を保持する各社の各国における商標または登録商標です。


