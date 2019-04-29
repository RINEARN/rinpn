/*
 * ==================================================
 * Array Data Container Interface 1 (ADCI 1)
 * ( for VCSSL / Vnano Plug-in Development )
 * --------------------------------------------------
 * This file is released under CC0.
 * Written in 2017-2019 by RINEARN (Fumihiro Matsui)
 * ==================================================
 */

package org.vcssl.connect;

/**
 * <p>
 * ADCI 1 (Array Data Container Interface 1) 形式のデータコンテナ・インターフェースです。
 * </p>
 *
 * <p>
 * <span style="font-weight: bold;">
 * ※ このインターフェースは未確定であり、
 * このインターフェースをサポートする処理系が正式にリリースされるまでの間、
 * 一部仕様が変更される可能性があります。
 * </span>
 * </p>
 *
 * <p>
 * ここでのデータコンテナとは、処理系内部や内外でデータをやり取りする単位として、
 * データを格納する事を目的としたオブジェクトの事を指します。
 * このデータコンテナ・インターフェースは、言語処理系と、処理系外部のプラグインとの間で、
 * 多次元配列データを直接的に（変換などを行わずに）やり取りしたい場合などに使用します。
 * </p>
 *
 * <p>
 * 外部変数や外部関数のプラグインはホスト言語で実装されるため、
 * ホスト言語とスクリプト言語との間におけるデータ型の違いや、
 * 処理系内部でのデータの扱いの違いなどを、どこかで吸収する必要があります。
 * 通常、プラグイン側はホスト言語のデータ型のみを用いて開発され、
 * そのようなデータ変換などは処理系側において自動的に行われます。
 * </p>
 *
 * <p>
 * しかし、オーバーヘッドを避けるためなどの理由で、
 * 自動のデータ変換を利用しない場合（プラグイン開発の際に任意に選択できます）、
 * プラグイン側は、処理系側の内部で使用されるデータコンテナの形式で、
 * データを受け渡しする必要があります。
 * その形式は処理系に依存しますが、このインターフェースは、
 * 主にベクトル演算ベースの処理系の内部で使用されるデータコンテナの仕様を、
 * プラグインの再利用性を確保するために抽象化して定義したものです。
 * </p>
 *
 * <p>
 * 現時点では、このインターフェースは
 * Vnano (VCSSL nano) 処理系の内部でのデータコンテナ形式において使用されています。
 * Vnano 処理系は仮想プロセッサ（いわゆるVM）がベクトル演算主体の設計であり、
 * レジスタや仮想メモリーのデータ単位が全て配列であるため、
 * スカラも含めたあらゆるデータにおいて、このインターフェースを実装したデータコンテナが使用されます。
 * 従って、Vnano用のプラグイン開発において、自動のデータ変換を利用しない場合、
 * このインターフェースを実装したデータコンテナによって、
 * 引数や戻り値などのデータをやり取りします。
 * </p>
 *
 * <p>
 * データの格納のされ方などの詳細については、具体的な実装を交えた説明の方が適しているため、
 * Vnano のスクリプトエンジン実装における org.vcssl.nano.vm.memory.DataContainer
 * クラスの説明などを参照してください。
 * </p>
 *
 * @param <T> 保持するデータの型
 * @author RINEARN (Fumihiro Matsui)
 */
public interface ArrayDataContainer1<T> {


	/** 動的ロード時などに処理系側から参照される、インターフェースの形式名（値は"ADCI"）です。*/
	public static String INTERFACE_TYPE = "ADCI";

	/** 動的ロード時などに処理系側から参照される、インターフェースの世代名（値は"1"）です。*/
	public static String INTERFACE_GENERATION = "1";


	/**
	 * このデータコンテナが格納するデータを設定します。
	 *
	 * このデータコンテナの仕様は、
	 * 内部でデータを1次元配列として保持する事を前提としているため、
	 * 引数 data には常に1次元配列を渡す必要があります。
	 *
	 * 多次元配列は、右端次元の要素が連続的に並ぶように1次元化した配列を
	 * 引数 data に渡してください。
	 *
	 * また、スカラ値は、要素数1の配列で包んでください。
	 * または、より大きな配列の中にそのスカラ値を格納した上で、
	 * このメソッドの代わりに
	 * {@link ArrayDataContainer1#setData(Object, int) setData(T, int)}
	 * を使用して、そのインデックスを指定してください。
	 *
	 * @param data 格納するデータ（1次元配列）
	 */
	public abstract void setData(T data);


	/**
	 * このデータコンテナが格納するデータをスカラ値と見なしたい場合において、
	 * そのスカラ値を中に含んでいる配列データと、
	 * その中でスカラ値が保持されているオフセット値を指定します。
	 *
	 * オフセット値とは、データコンテナが内部で保持している配列データ内において、
	 * スカラ値が格納されている要素のインデックスを意味します。
	 *
	 * このデータコンテナの仕様は、
	 * 内部でデータを1次元配列として保持する事を前提としているため、
	 * 引数 data には常に1次元配列を渡す必要があります。
	 *
	 * データコンテナが保持する設定値の組み合わせが、瞬間的にでも不整合な状態になる事を防ぐため、
	 * オフセット値（引数offset）のみを設定するメソッドは提供されません。
	 *
	 * @param data 格納するデータ（1次元配列）
	 * @param offset オフセット値（データ内でスカラ値が存在する配列インデックス）
	 */
	public abstract void setData(T data, int offset);


	/**
	 * 格納するデータを、次元ごとの長さ情報と共に設定します。
	 *
	 * このデータコンテナの仕様は、
	 * 内部でデータを1次元配列として保持する事を前提としているため、
	 * 引数 data には常に1次元配列を渡す必要があります。
	 *
	 * 多次元配列は、右端次元の要素が連続的に並ぶように1次元化した配列を
	 * 引数 data に渡してください。
	 * スカラは、要素数1の配列とするか、より大きな配列のどこかに格納した上で、
	 * このメソッドの代わりに
	 * {@link ArrayDataContainer1#setData(Object, int) setData(T, int)}
	 * メソッドでそのインデックスを指定してください。
	 *
	 * 引数 lengths には、次元ごとの長さを格納する配列を指定してください。
	 * 要素の順序については、スクリプト言語内での多次元配列との対応において、
	 * 左端次元の要素数を[0]番要素、その一つ右隣りにある次元の要素数を[1]番要素 ...
	 * という順で格納してください。
	 * また、このデータコンテナの保持データをスカラ値として扱わせたい場合は、
	 * 引数 lengths には要素数0の配列を指定してください（つまりスカラは0次元の配列と見なします）。
	 *
	 * データコンテナが保持する設定値の組み合わせが、瞬間的にでも不整合な状態になる事を防ぐため、
	 * 次元ごとの長さ情報（引数 lengths）のみを設定するメソッドは提供されません。
	 *
	 * @param data 格納するデータ（1次元配列）
	 * @param lengths 次元ごとの長さを格納する配列
	 */
	public abstract void setData(T data, int[] lengths);


	/**
	 * 格納されているデータを取得します。
	 *
	 * このデータコンテナの仕様は、
	 * 内部でデータを1次元配列として保持する事を前提としているため、
	 * 戻り値は1次元配列として返されます。
	 *
	 * 多次元配列は、右端次元の要素が連続的に並ぶように1次元化されます。
	 * スカラは、要素数1の配列として返されるか、
	 * またはより大きな配列のどこかに格納した上で返され、後者の場合は
	 * {@link ArrayDataContainer1#getOffset getOffset}
	 * メソッドでそのインデックスを取得できます。
	 *
	 * @return 格納されているデータ（1次元配列）
	 */
	public abstract T getData();


	/**
	 * 格納されているデータをスカラ値と見なす場合における、オフセット値を取得します。
	 *
	 * オフセット値とは、データコンテナが内部で保持している配列データ内において、
	 * スカラ値が格納されている要素のインデックスを意味します。
	 * この仕様は、ベクトル演算ベースの処理系において、
	 * 配列ベースのデータコンテナでスカラデータを効率的に扱うためのものです。
	 *
	 * なお、データコンテナが保持する設定値の組み合わせが、瞬間的にでも不整合な状態になる事を防ぐため、
	 * オフセット値のみを設定するメソッドは提供されません。
	 * 設定したい場合は {@link ArrayDataContainer1#setData(Object, int) setData(T, int) }
	 * メソッドを使用して、データと共に設定してください。
	 *
	 * @return スカラデータの配列内位置を示すオフセット値
	 */
	public abstract int getOffset();


	/**
	 * 多次元配列の次元ごとの長さを取得します。
	 *
	 * なお、データコンテナが保持する設定値の組み合わせが、瞬間的にでも不整合な状態になる事を防ぐため、
	 * 次元ごとの長さのみを設定するメソッドは提供されません。
	 * 設定したい場合は {@link ArrayDataContainer1#setData(Object, int[]) setData(T, int[]) }
	 * メソッドを使用して、データと共に設定してください。
	 *
	 * @return 次元ごとの長さを格納する配列
	 */
	public abstract int[] getLengths();


	/**
	 * サイズを取得します。
	 *
	 * ここでのサイズとは、多次元配列における総要素数の事です。
	 * 具体的には、データがスカラではない場合には、サイズは
	 * {@link ArrayDataContainer1#getLengths getLengths}
	 * メソッドで取得できる次元長配列の、全要素の積に一致します。
	 *
	 * データがスカラである場合には、サイズは常に 1 となります。
	 * 仮に {@link ArrayDataContainer1#getData getData}
	 * メソッドで取得したデータの配列の要素数が 1 よりも大きく
	 * その配列内に要素として（オフセット値で指定される位置に）
	 * スカラ値が格納されている場合でも、
	 * このメソッドで返されるサイズは 1 になります。
	 *
	 * サイズとデータの要素数を独立に設定する事はできないため、
	 * サイズの setter はありません。
	 * 設定したい場合は {@link ArrayDataContainer1#setData(Object, int[]) setData(T, int[]) }
	 * メソッドなどを使用して、データおよび次元ごとの長さ情報と共に設定してください。
	 *
	 * @return サイズ
	 */
	public abstract int getSize();


	/**
	 * 多次元配列の次元数（次元の総数）を取得します。
	 *
	 * 具体的には、{@link ArrayDataContainer1#getLengths getLengths}
	 * メソッドで取得できる次元長配列の、要素数に一致します。
	 *
	 * 次元数と、次元ごとの長さを独立に設定する事はできないため、
	 * 次元数の setter はありません。
	 * 設定したい場合は {@link ArrayDataContainer1#setData(Object, int[]) setData(T, int[]) }
	 * メソッドなどを使用して、データおよび次元ごとの長さ情報と共に設定してください。
	 *
	 * @return 次元数
	 */
	public abstract int getRank();

}
