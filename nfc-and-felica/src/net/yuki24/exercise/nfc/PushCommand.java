package net.yuki24.exercise.nfc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import net.kazzz.felica.FeliCaException;
import net.kazzz.felica.lib.FeliCaLib;
import net.kazzz.felica.lib.FeliCaLib.CommandPacket;
import net.kazzz.felica.lib.FeliCaLib.IDm;

import com.felicanetworks.mfc.PushSegment;

public class PushCommand extends CommandPacket {
    private static final Charset URL_CHARSET = Charset.forName("iso8859-1");
    private static final Charset ICC_CHARSET = Charset.forName("iso8859-1");
    //private static final Charset STARTUP_PARAM_CHARSET = Charset.forName("Shift_JIS");
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
		int bytes = 3;									// 個別部数(1byte) + チェックサム(2bytes)
		for (int i = 0; i < segments.length; ++i)
			bytes += segments[i].length;

		ByteBuffer buffer = ByteBuffer.allocate(bytes);
		buffer.put((byte) segments.length);				// 個別部数
		for (int i = 0; i < segments.length; ++i)		// 個別部
			buffer.put(segments[i]);

		int sum = segments.length;						// チェックサム
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
			return buildPushIntentSegment(1, "market://details?id=com.main.typograffit", "ANDR01");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException("not supported " + segment);
	}

	private static byte[][] buildPushIntentSegment(int type, String url, String icc) throws UnsupportedEncodingException {
		byte[] urlBytes = url.getBytes(URL_CHARSET);
		byte[] iccBytes = icc.getBytes(ICC_CHARSET);

		// type(1byte) + paramBytesLength(2bytes) + urlBytesLength(2bytes) + iccBytesLength(2bytes)
		int capacity = urlBytes.length + iccBytes.length + 7;
		ByteBuffer buffer = ByteBuffer.allocate(capacity);

		// 個別部ヘッダ
		buffer.put((byte) type);				// 起動制御情報
		int paramSize = capacity - 3; 			// 個別部パラメータサイズ: type(1byte) + paramBytesLength(2)
		putAsLittleEndian(paramSize, buffer);

		// 個別部パラメータ
		putAsLittleEndian(0, buffer);				// URLサイズ
		//buffer.put(urlBytes);						// URL
		putAsLittleEndian(iccBytes.length, buffer);	// iccサイズ
		buffer.put(iccBytes);						// icc
		buffer.put(urlBytes);						// (アプリケーション起動パラメータ)
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