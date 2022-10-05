package org.zeith.tech.core.audio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VorbisDuration
{
	/**
	 * From <a href="https://stackoverflow.com/a/44407355">StackOverflow</a>
	 */
	public static double calculateDuration(final InputStream stream) throws IOException
	{
		int rate = -1;
		int length = -1;
		
		byte[] t = stream.readAllBytes();
		stream.close();
		
		//4 bytes for "OggS", 2 unused bytes, 8 bytes for length
		// Looking for length (value after last "OggS")
		for(int i = t.length - 1 - 8 - 2 - 4; i >= 0 && length < 0; i--)
			if(t[i] == (byte) 'O' && t[i + 1] == (byte) 'g' && t[i + 2] == (byte) 'g' && t[i + 3] == (byte) 'S')
			{
				byte[] byteArray = new byte[] {
						t[i + 6],
						t[i + 7],
						t[i + 8],
						t[i + 9],
						t[i + 10],
						t[i + 11],
						t[i + 12],
						t[i + 13]
				};
				ByteBuffer bb = ByteBuffer.wrap(byteArray);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				length = bb.getInt(0);
			}
		
		// Looking for rate (first value after "vorbis")
		for(int i = 0; i < t.length - 8 - 2 - 4 && rate < 0; i++)
			if(t[i] == (byte) 'v' && t[i + 1] == (byte) 'o' && t[i + 2] == (byte) 'r' && t[i + 3] == (byte) 'b' && t[i + 4] == (byte) 'i' && t[i + 5] == (byte) 's')
			{
				byte[] byteArray = new byte[] {
						t[i + 11],
						t[i + 12],
						t[i + 13],
						t[i + 14]
				};
				ByteBuffer bb = ByteBuffer.wrap(byteArray);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				rate = bb.getInt(0);
			}
		
		return length / (double) rate;
	}
}