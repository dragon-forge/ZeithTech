package org.zeith.tech.utils;

import org.zeith.hammerlib.util.charging.fe.FECharge;

public class FEChargeWithLosses
		extends FECharge
{
	public float loss;
	
	public FEChargeWithLosses(int fe)
	{
		super(fe);
	}
	
	public FEChargeWithLosses looseFE(float loss)
	{
		this.loss += loss;
		return this;
	}
	
	public float getLoss()
	{
		return loss;
	}
	
	public int getFE()
	{
		return Math.round(FE - loss);
	}
}