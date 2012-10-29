package roomba;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;

public class SerialIO {
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	
	protected SerialIO(String portIdentifier) throws NoSuchPortException, PortInUseException, IOException{
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portIdentifier);
		SerialPort serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);
		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();

	}
	
	protected void sendCommand(byte opcode, byte[] data) throws IOException{
		outputStream.write(toByteArray(opcode, data));
	}
	
	protected static byte[] toByteArray(byte[] ba, byte b1){
		byte[] output = new byte[ba.length + 1];
		for(int i=0; i<ba.length; i++)output[i] = ba[i];
		output[ba.length] = b1;
		return output;
	}
	
	protected static byte[] toByteArray(byte b1, byte[] ba){
		byte[] output = new byte[ba.length + 1];
		output[0] = b1;
		for(int i=0; i<ba.length; i++)output[i+1] = ba[i];
		return output;
	}
	
	protected static byte[] toByteArray(short s1){
		byte[] output = new byte[2];
		output[0] = (byte)(s1 & 0xff);
		output[1] = (byte)((s1 >> 8) & 0xff);
		return output;
	}
	
	protected static byte[] toByteArray(short s1, short s2){
		byte[] output = new byte[4];
		output[0] = (byte)(s1 & 0xff);
		output[1] = (byte)((s1 >> 8) & 0xff);
		output[2] = (byte)(s2 & 0xff);
		output[3] = (byte)((s2 >> 8) & 0xff);
		return output;
	}
}
