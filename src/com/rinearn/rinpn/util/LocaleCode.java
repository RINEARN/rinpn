/*
 * Copyright(C) 2019-2020 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.util;

import java.util.Locale;

public final class LocaleCode {
	public static final String JA_JP = "ja-jp";
	public static final String EN_US = "en-us";
	public static final String AUTO = "auto";

	public static final boolean isSupported(String localeCode) {
		if (localeCode.equals(JA_JP)) {
			return true;
		}
		if (localeCode.equals(EN_US)) {
			return true;
		}
		if (localeCode.equals(AUTO)) {
			return true;
		}
		return false;
	}

	public static final Locale toLocale(String localeCode) {
		Locale locale = null;
		if (localeCode.equals(JA_JP)) {
			return Locale.of("ja", "JP");
		}
		if (localeCode.equals(EN_US)) {
			return Locale.of("en", "US");
		}
		if (localeCode.equals(AUTO)) {
			return Locale.getDefault();
		}
		return locale;
	}

	public static final String getDefaultLocaleCode() {
		Locale locale = Locale.getDefault();

		if (   ( locale.getLanguage()!=null && locale.getLanguage().toLowerCase().equals("ja") )
			   || ( locale.getCountry()!=null && locale.getCountry().toLowerCase().equals("jp") )   ) {

			return LocaleCode.JA_JP;

		} else {

			return LocaleCode.EN_US;
		}
	}
}
