package net.yuki24.exercise.nfc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import net.kazzz.felica.FeliCaException;
import net.kazzz.felica.lib.FeliCaLib;
import net.kazzz.felica.lib.FeliCaLib.CommandPacket;
import net.kazzz.felica.lib.FeliCaLib.IDm;

import com.felicanetworks.mfc.PushIntentSegment;
import com.felicanetworks.mfc.PushSegment;

public class PushCommand extends CommandPacket {
    private static final Charset URL_CHARSET = Charset.forName("iso8859-1");
    private static final Charset ICC_CHARSET = Charset.forName("iso8859-1");
	public static final byte PUSH = (byte) 0xb0;

	static {
		FeliCaLib.commandMap.put(PUSH, "Push");
	}

	public PushCommand(IDm idm, PushSegment segment) throws FeliCaException {
		super(PUSH, idm, packContent(packSegment(buildData(segment))));
	}

	private static byte[] packContent(byte[] segments) {
		byte[] buffer = new byte[segments.length + 1];
		buffer[0] = (byte) segments.length;
		System.arraycopy(segments, 0, buffer, 1, segments.length);
		return buffer;
	}

	private static byte[] packSegment(byte[]... segments) {
		// command(1byte) + check sum(2bytes)
		int bytes = 3;
		for (int i = 0; i < segments.length; ++i)
			bytes += segments[i].length;

		ByteBuffer buffer = ByteBuffer.allocate(bytes);
		buffer.put((byte) segments.length);
		for (int i = 0; i < segments.length; ++i) buffer.put(segments[i]);

		int sum = segments.length;
		for (int i = 0; i < segments.length; ++i) {
			byte[] e = segments[i];
			for (int j = 0; j < e.length; ++j)	sum += e[j];
		}
		int checksum = -sum & 0xffff;
		putAsBigEndian(checksum, buffer);
		return buffer.array();
	}

	private static byte[][] buildData(PushSegment segment) throws FeliCaException {
		try {
			return buildPushIntentSegment(1, ((PushIntentSegment) segment).getIntentData().getData().toString(), "ANDR01");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException("not supported " + segment);
	}

	private static byte[][] buildPushIntentSegment(int type, String url, String icc) throws UnsupportedEncodingException {
		byte[] urlBytes = url.getBytes(URL_CHARSET);
		byte[] iccBytes = icc.getBytes(ICC_CHARSET);
		int capacity = urlBytes.length + iccBytes.length + 7;
		ByteBuffer buffer = ByteBuffer.allocate(capacity);

		buffer.put((byte) type);
		int paramSize = capacity - 3;
		putAsLittleEndian(paramSize, buffer);
		putAsLittleEndian(0, buffer);
		putAsLittleEndian(iccBytes.length, buffer);
		buffer.put(iccBytes);
		buffer.put(urlBytes);
		return new byte[][] { buffer.array() };
    }

	private static void putAsLittleEndian(int i, ByteBuffer buffer) {
		buffer.put((byte) ((i >> 0) & 0xff));
		buffer.put((byte) ((i >> 8) & 0xff));
	}

	private static void putAsBigEndian(int i, ByteBuffer buffer){
		buffer.put((byte) ((i >> 8) & 0xff));
		buffer.put((byte) ((i >> 0) & 0xff));
	}
}