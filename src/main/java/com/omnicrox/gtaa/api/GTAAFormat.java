package com.omnicrox.gtaa.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Number display for GTAA machines. Small numbers keep their commas (12,345,678); anything from a billion up is
 * written in scientific notation like GTNH does: 1.08E16.
 */
public final class GTAAFormat {

    private GTAAFormat() {}

    public static final long SCIENTIFIC_FROM = 1_000_000_000L;

    public static String num(long v) {
        if (Math.abs(v) < SCIENTIFIC_FROM) return NumberFormat.getIntegerInstance(Locale.ROOT).format(v)
                .replace(' ', ',');
        return sci(BigInteger.valueOf(v));
    }

    public static String num(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return String.valueOf(v);
        if (Math.abs(v) < SCIENTIFIC_FROM) return num(Math.round(v));
        return sci(new BigDecimal(v).toBigInteger());
    }

    public static String num(BigInteger v) {
        if (v.abs().compareTo(BigInteger.valueOf(SCIENTIFIC_FROM)) < 0) return num(v.longValue());
        return sci(v);
    }

    private static String sci(BigInteger v) {
        String sign = v.signum() < 0 ? "-" : "";
        BigDecimal d = new BigDecimal(v.abs()).round(new MathContext(3));
        int exp = d.precision() - d.scale() - 1;
        BigDecimal mant = d.movePointLeft(exp).setScale(2, java.math.RoundingMode.HALF_UP);
        if (mant.compareTo(BigDecimal.TEN) >= 0) {
            mant = mant.movePointLeft(1).setScale(2, java.math.RoundingMode.HALF_UP);
            exp++;
        }
        return sign + mant.toPlainString() + "E" + exp;
    }
}
