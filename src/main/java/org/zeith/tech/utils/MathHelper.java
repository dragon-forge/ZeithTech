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
}