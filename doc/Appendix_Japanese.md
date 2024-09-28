## 付録 - 主な組み込み関数/変数

&raquo; [English](Appendix.md)

&raquo; [AIに使い方を聞く（ChatGPTのアカウントが必要）](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

ここでは、RINPn で標準で利用できる関数および変数の中から、特によく使うものをリストアップして紹介します。

※ 以下に挙げる関数/変数の大半は、「 [プラグイン](Step5_Japanese.md) 」の項目でも触れた、**Vnano 標準プラグイン** が提供しています。 従って、全ての関数/変数のリスト、およびより詳細な仕様については、[Vnano 標準プラグインの仕様書](https://www.vcssl.org/ja-jp/vnano/plugin/) をご参照ください。 ここでは、説明内容を簡略化して掲載しています。

## 組み込み関数

### [rad( degree )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#rad)

度からラジアンへの変換関数です。

例： rad( 180.0 )

### [deg( radian )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#deg)

ラジアンから度への変換関数です。

例： deg( 2.0 * PI )

### [sin( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#sin)

正弦（サイン）関数です。引数 x の単位はラジアンです。

例： sin( PI / 2.0 )

### [cos( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#cos)

余弦（コサイン）関数です。引数 x の単位はラジアンです。

例： cos( 2.0 * PI )

### [tan( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#tan)

正接（タンジェント）関数です。引数 x の単位はラジアンです。

例： tan( PI / 4.0 )

### [asin( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#asin)

正弦関数の逆関数（アークサイン）です。結果の単位はラジアンです。

例： asin( 1.0 )

### [acos( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#acos)

余弦関数の逆関数（アークコサイン）です。結果の単位はラジアンです。

例： acos( 1.0 )

### [atan( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#atan)

正接関数の逆関数（アークタンジェント）です。結果の単位はラジアンです。

例： atan( 1.0 )

### [sqrt( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#sqrt)

平方根を求める関数です。

例： sqrt( 4.0 )

### [ln( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#ln)

自然数 e を底とする対数関数です。

例： ln( 10.0 )

### [log10( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#log10)

10 を底とする対数関数です。

例： log10( 1000.0 )

### [pow( x, exponent )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#pow)

x の exponent 乗を求める関数です。

例： pow( 2.0, 3.0 )

### [exp( exponent )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#exp)

自然数 e の exponent 乗を求める関数です。

例： exp( 1.2 )

### [abs( x )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#abs)

絶対値関数です。

例： abs( -1.23 )

### [sum( ... )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#sum)

和を求める関数です。

例： sum( 1.23 ,   4.56 ,   7.89 )

### [mean( ... )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#rad)

平均値（算術平均）を求める関数です。

例： mean( 1.23 ,   4.56 ,   7.89 )

### [van( ... )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#van)

分散（ 分母： n ）を求める関数です。

例： van( 1.23 ,   4.56 ,   7.89 )

### [van1( ... )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#van1)

分散（ 分母： n-1 ）を求める関数です。

例： van1( 1.23 ,   4.56 ,   7.89 )

### [sdn( ... )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#sdn)

標準偏差（ 分母： n ）を求める関数です。

例： sdn( 1.23 ,   4.56 ,   7.89 )

### [sdn1( ... )](https://www.vcssl.org/ja-jp/vnano/plugin/math/#sdn1)

標準偏差（ 分母： n-1 ）を求める関数です。

例： sdn1( 1.23 ,   4.56 ,   7.89 )

### [length( array, dim )](https://www.vcssl.org/ja-jp/vnano/plugin/system/#length)

配列 array における、dim 番目の次元の要素数を返す関数です。

例： length( array, 0 )

### [output( value )](https://www.vcssl.org/ja-jp/vnano/plugin/system/#output)

スクリプトの計算結果の値を表示するための関数です。 GUIモードでは、値は「 OUTPUT」 テキストフィールドに表示されます（複数回呼ぶと、表示内容は単純に上書きされます）。 CUIモードでは、値は標準出力に 1 行で出力されます。

例： output( 1.23 )

### [print( value )](https://www.vcssl.org/ja-jp/vnano/plugin/system/#print)

スクリプト内で、長い内容や複数行の内容を表示するために、汎用的に用いる関数です。 任意個数・任意次元・任意型の値を表示可能で、値は 1 個ずつ（ 1 要素ずつ）タブ空白区切りで表示されます。 GUIモードでは、値は独立なウィンドウ上に、自動改行なしで追記されていきます。 CUIモードでは、値は標準出力に自動改行なしで追記されていきます。

例： print( 1.2 , 3.4 , 5.6 )

### [println( value )](https://www.vcssl.org/ja-jp/vnano/plugin/system/#println)

上記の print 関数とほぼ同様ですが、この関数は内容を自動改行付きで表示/出力します。

例： println( 1.2 , 3.4 , 5.6 )

## 組み込み変数

### [PI](https://www.vcssl.org/ja-jp/vnano/plugin/math/#PI)

円周率 π の値を保持する変数（定数）です。

値： 3.141592653589793


---

## 商標等に関する表記

* ChatGPT は、米国 OpenAI OpCo, LLC による米国またはその他の国における商標または登録商標です。

* その他、文中に使用されている商標は、その商標を保持する各社の各国における商標または登録商標です。
