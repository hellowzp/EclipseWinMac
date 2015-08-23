package net.pandoragames.charset;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Locale;

class BinaryCharset extends Charset {

	static final String[] names = new String[]{"X-BinaryCS", "BINARY"};
	
	protected static final char[] characterMap = new char[]{ 
	// 		0		1		2		3		4		5		6		7		8		9		A		B		C		D		E		F
		0x2300, 0x2654, 0x2655, 0x2656, 0x2657, 0x2658, 0x2659, 0x266a, 0x2190, 0x21a6, 0x21b5, 0x21a7, 0x21a1, 0x00b6, 0x2907, 0x2906, 
		0x2318, 0x265a, 0x265b, 0x265c, 0x265d, 0x265e, 0x265f, 0x268c, 0x268d, 0x268e, 0x268f, 0x2612, 0x2660, 0x2661, 0x2662, 0x2663, 
		0x0020, 0x0021, 0x0022, 0x0023, 0x0024, 0x0025, 0x0026, 0x0027, 0x0028, 0x0029, 0x002a, 0x002b, 0x002c, 0x002d, 0x002e, 0x002f, 
		0x0030, 0x0031, 0x0032, 0x0033, 0x0034, 0x0035, 0x0036, 0x0037, 0x0038, 0x0039, 0x003a, 0x003b, 0x003c, 0x003d, 0x003e, 0x003f, 
		0x0040, 0x0041, 0x0042, 0x0043, 0x0044, 0x0045, 0x0046, 0x0047, 0x0048, 0x0049, 0x004a, 0x004b, 0x004c, 0x004d, 0x004e, 0x004f, 
		0x0050, 0x0051, 0x0052, 0x0053, 0x0054, 0x0055, 0x0056, 0x0057, 0x0058, 0x0059, 0x005a, 0x005b, 0x005c, 0x005d, 0x005e, 0x005f, 
		0x0060, 0x0061, 0x0062, 0x0063, 0x0064, 0x0065, 0x0066, 0x0067, 0x0068, 0x0069, 0x006a, 0x006b, 0x006c, 0x006d, 0x006e, 0x006f, 
		0x0070, 0x0071, 0x0072, 0x0073, 0x0074, 0x0075, 0x0076, 0x0077, 0x0078, 0x0079, 0x007a, 0x007b, 0x007c, 0x007d, 0x007e, 0x00b1,  
		0x0e10, 0x0e11, 0x0e12, 0x0e13, 0x0e14, 0x0e15, 0x0e16, 0x0e17, 0x0e18, 0x0e19, 0x0e1a, 0x0e1b, 0x0e1c, 0x0e1d, 0x0e1e, 0x0e1f, 
		0x0e20, 0x0e21, 0x0e22, 0x0e23, 0x0e24, 0x0e25, 0x0e26, 0x0e27, 0x0e28, 0x0e29, 0x0e2a, 0x0e2b, 0x0e2c, 0x0e2d, 0x0e2e, 0x0e2f, 
		0x1000, 0x1001, 0x1002, 0x1003, 0x1004, 0x1005, 0x1006, 0x1007, 0x1008, 0x1009, 0x100a, 0x100b, 0x100c, 0x100d, 0x100e, 0x100f, 
		0x1010, 0x1011, 0x1012, 0x1013, 0x1014, 0x1015, 0x1016, 0x1017, 0x1018, 0x1019, 0x101a, 0x101b, 0x101c, 0x101d, 0x101e, 0x101f, 
		0x0910, 0x0911, 0x0912, 0x0913, 0x0914, 0x0915, 0x0916, 0x0917, 0x0918, 0x0919, 0x091a, 0x091b, 0x091c, 0x091d, 0x091e, 0x091f, 
		0x0920, 0x0921, 0x0922, 0x0923, 0x0924, 0x0925, 0x0926, 0x0927, 0x0928, 0x0929, 0x092a, 0x092b, 0x092c, 0x092d, 0x092e, 0x092f, 
		0x16a0, 0x16a1, 0x16a2, 0x16a3, 0x16a4, 0x16a5, 0x16a6, 0x16a7, 0x16a8, 0x16a9, 0x16aa, 0x16ab, 0x16ac, 0x16ad, 0x16ae, 0x16af, 
		0x16b0, 0x16b1, 0x16b2, 0x16b3, 0x16b4, 0x16b5, 0x16b6, 0x16b7, 0x16b8, 0x16b9, 0x16ba, 0x16bb, 0x16bc, 0x16bd, 0x16be, 0x262e };

	BinaryCharset() {
		super( names[0], names );
	}

	/**
	 * Returns "BINARY".
	 */
	public String displayName(Locale locale) {
		return displayName();
	}
	
	/**
	 * Returns "BINARY".
	 */
	public String displayName() {
		return names[1];
	}
	
	@Override
	public boolean contains(Charset cs) {
		return false;
	}

	@Override
	public CharsetDecoder newDecoder() {
		return new BinaryCSDecoder(this);
	}

	@Override
	public CharsetEncoder newEncoder() {
		return new BinaryCSEncoder(this);
	}

}

class BinaryCSDecoder extends CharsetDecoder {

	protected BinaryCSDecoder(Charset cs) {
		super(cs, 1, 1);
	}

	@Override
	protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
		int toBeRead = Math.min( in.remaining(), out.remaining());
		int bufferSize = Math.min(toBeRead, 1024);
		byte[] buffer = new byte[bufferSize];
		int index = 0;
		try {
			while( index < toBeRead ) {
				int count = Math.min(bufferSize, toBeRead - index);
				in.get(buffer,0, count);
				index = index + count;
				for(int i = 0; i < count; i++ ) {
					out.append( BinaryCharset.characterMap[ buffer[i] & 0xFF ] );
				}
			}
		} catch(BufferUnderflowException bux) {
			return CoderResult.UNDERFLOW;
		}
		return out.remaining() < in.remaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
	}
}

class BinaryCSEncoder extends CharsetEncoder {

	protected BinaryCSEncoder(Charset cs) {
		super(cs, 1, 1);
	}

	@Override
	protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
		int toBeRead = Math.min( in.remaining(), out.remaining());
		try {
			for(int i = 0; i < toBeRead; i++){
				char c = in.get();
				if( ' ' <= c && c <= '~' ) {
					out.put( (byte) c ); 
				} else {
					boolean found = false;
					for(int k = 0; k < 256; k++) {
						if(k == 32 ) k = 127;
						if( c == BinaryCharset.characterMap[ k ] ) {
							found = true;
							out.put( (byte) k );
						}
					}
					if( ! found ) {
						return CoderResult.unmappableForLength(1);
					}
				}
			}
		}catch(BufferUnderflowException bux) {
			return CoderResult.UNDERFLOW;
		} catch( BufferOverflowException box) {
			return CoderResult.OVERFLOW;
		}
		return out.remaining() < in.remaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
	}
	
}