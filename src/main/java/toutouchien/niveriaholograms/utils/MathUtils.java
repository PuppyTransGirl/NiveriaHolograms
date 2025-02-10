package toutouchien.niveriaholograms.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
	public static double decimalRound(double value, int scale) {
		return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
	}
}
