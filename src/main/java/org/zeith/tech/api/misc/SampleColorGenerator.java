package org.zeith.tech.api.misc;

import net.minecraft.util.RandomSource;
import org.zeith.hammerlib.util.java.Hashers;
import org.zeith.tech.utils.MathHelper;

import java.awt.*;

public class SampleColorGenerator
		implements IColorProvider
{
	protected Color color;
	
	public SampleColorGenerator()
	{
		this.color = SampleColorGenerator.generateRandomColor(SampleColorGenerator.getCaller());
	}
	
	@Override
	public Color getColor()
	{
		return color;
	}
	
	public static Color generateRandomColor(StackTraceElement element, String... moreData)
	{
		float hue = RandomSource.create(stackTraceToSeed(element, moreData)).nextFloat();
		int rgb = MathHelper.hsvToRgb(hue, 1F, 1F);
		return new Color(rgb, false);
	}
	
	public static RandomSource generateRandom(StackTraceElement element, String... moreData)
	{
		return RandomSource.create(stackTraceToSeed(element, moreData));
	}
	
	public static StackTraceElement getCaller()
	{
		return Thread.currentThread().getStackTrace()[3];
	}
	
	public static long stackTraceToSeed(StackTraceElement elem, String... moreData)
	{
		return MathHelper.hashCode64(Hashers.SHA256.hashify((moreData.length > 0 ? "" : elem.getLineNumber() + ";" + elem.getMethodName() + ";" + elem.getClassName() + ";") + String.join(";", moreData)));
	}
}