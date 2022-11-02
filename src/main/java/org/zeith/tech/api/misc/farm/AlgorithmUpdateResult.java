package org.zeith.tech.api.misc.farm;

public enum AlgorithmUpdateResult
{
	SUCCESS(true, true),
	PASS(false, true),
	RETRY(true, false),
	RETRY_NOW(false, false);
	
	public final boolean wait, moveOn;
	
	AlgorithmUpdateResult(boolean wait, boolean moveOn)
	{
		this.wait = wait;
		this.moveOn = moveOn;
	}
}