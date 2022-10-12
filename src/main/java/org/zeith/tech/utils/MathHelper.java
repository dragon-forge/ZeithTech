package org.zeith.tech.utils;

public class MathHelper
{
	public static int findGCD(int[] arr)
	{
		int result = arr[0];
		for(int element : arr)
		{
			result = gcd(result, element);
			if(result == 1) return 1;
		}
		return result;
	}
	
	private static int gcd(int a, int b)
	{
		if(a == 0) return b;
		return gcd(b % a, a);
	}
	
	public static long hashCode64(Object... a)
	{
		if(a == null)
			return 0L;
		long result = 1L;
		for(Object element : a)
			result = 31L * result + (element == null ? 0 : element.hashCode());
		return result;
	}
	
	public static int hsvToRgb(float h, float s, float v)
	{
		int i = (int) (h * 6.0F) % 6;
		float f = h * 6.0F - (float) i;
		float f1 = v * (1.0F - s);
		float f2 = v * (1.0F - f * s);
		float f3 = v * (1.0F - (1.0F - f) * s);
		
		float r;
		float g;
		float b;
		
		switch(i)
		{
			case 0 ->
			{
				r = v;
				g = f3;
				b = f1;
			}
			case 1 ->
			{
				r = f2;
				g = v;
				b = f1;
			}
			case 2 ->
			{
				r = f1;
				g = v;
				b = f3;
			}
			case 3 ->
			{
				r = f1;
				g = f2;
				b = v;
			}
			case 4 ->
			{
				r = f3;
				g = f1;
				b = v;
			}
			case 5 ->
			{
				r = v;
				g = f1;
				b = f2;
			}
			default -> throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + h + ", " + s + ", " + v);
		}
		
		int j = clamp((int) (r * 255.0F), 0, 255);
		int k = clamp((int) (g * 255.0F), 0, 255);
		int l = clamp((int) (b * 255.0F), 0, 255);
		return j << 16 | k << 8 | l;
	}
	
	public static int clamp(int value, int min, int max)
	{
		if(value < min)
			return min;
		else
			return value > max ? max : value;
	}
}