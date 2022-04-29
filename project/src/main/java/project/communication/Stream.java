package project.communication;

import java.io.ByteArrayOutputStream;

/**
 * Byte array output stream class.
 *
 */
public class Stream extends ByteArrayOutputStream {

	/**
	 * Return the byte array extended from ByteArrayOutputStream
	 */
    @Override
    public synchronized byte[] toByteArray(){
        return super.toByteArray();
    }
}
