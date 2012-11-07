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

	public SerialIO(String portIdentifier) throws NoSuchPortException, PortInUseException, IOException{
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portIdentifier);
		SerialPort serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);
		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();
		new Luisteraar(inputStream).start();
	}

	protected void sendCommand(byte opcode, byte[] data) throws IOException{
		System.out.println("[SERIAL] send " + printUnsignedByte(toByteArray(opcode, data)));
		outputStream.write(toByteArray(opcode, data));
		outputStream.write(new byte[]{10});
		outputStream.flush();
	}

	private short getUnsignedByte(byte opcode) {
		short s;
		if(opcode < 0)
			s = (short) (opcode + 256);
		else 
			s = opcode;
		return s;
	}
	
	private String printUnsignedByte(byte[] data){
		String s = "";
		for(int i=0; i<data.length; i++)s += getUnsignedByte(data[i]) + " ";
		return s;
	}

	protected void readCommand() throws IOException{
		while(inputStream.available()>0){
			System.out.println(inputStream.read());
		}
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
		output[0] = (byte)((s1 >> 8) & 0xff);
		output[1] = (byte)(s1 & 0xff);
		return output;
	}

	protected static byte[] toByteArray(short s1, short s2){
		byte[] output = new byte[4];
		output[0] = (byte)((s1 >> 8) & 0xff);
		output[1] = (byte)(s1 & 0xff);
		output[2] = (byte)((s2 >> 8) & 0xff);
		output[3] = (byte)(s2 & 0xff);
		return output;
	}
        
        
        public int[] getInputData(int i){
            return null;
        };
}

class Luisteraar extends Thread {
	private InputStream inputStream = null;

	public Luisteraar(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void run() {
		while(true){
			try {
				while(inputStream.available() > 0){
					int in;
					if((in = inputStream.read()) != 45)
						System.out.println(in);
					else
						System.out.println("---");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}