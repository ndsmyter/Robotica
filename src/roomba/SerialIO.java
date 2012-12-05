package roomba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class SerialIO {
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private Luisteraar l = null;

	public SerialIO(String portIdentifier) throws NoSuchPortException, PortInUseException, IOException{
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portIdentifier);
		SerialPort serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);
		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();
		l = new Luisteraar(inputStream); l.start();
	}

	protected void sendCommand(byte opcode, byte[] data) throws IOException{
		System.out.println("[SERIAL] send " + printUnsignedByte(toByteArray(opcode, data)));
		outputStream.write(new byte[]{(byte)(data.length + 1)}); 	// aantal
		outputStream.write(toByteArray(opcode, data));				// header + data 
		//outputStream.write(new byte[]{10});
		outputStream.flush();
		//TODO wachten op ACK van roomba voor terug te keren.
	}
	
	protected byte[] getResponds(){
		while(!l.isReturnReady()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return l.getReturnFromArduino();
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
	private BufferedReader br = null;
	private byte[] returnFromArduino = null;
	private boolean returnReady = false;

	public Luisteraar(InputStream inputStream) {
		this.inputStream = inputStream;
		br = new BufferedReader(new InputStreamReader(inputStream));
	}
	
	public byte[] getReturnFromArduino(){
		returnReady = false;
		return returnFromArduino;
	}
	
	public boolean isReturnReady(){
		return returnReady;
	}

	public void run() {
		int in;
		while(true){
			try {
				byte[] input;
				while(inputStream.available() > 0){
					switch((in = inputStream.read())){
					case 0:
						System.out.println("Protocol Arduino: " + in);
						in = inputStream.read(); //Length of inputstring
						input = new byte[in];
						while(inputStream.available() < in){try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}};
						inputStream.read(input);
						System.out.println("[Arduino] " + new String(input));
						break;
					case 1:
						System.out.println("Protocol Roomba: " + in);
						System.out.println("Header Roomba package: " + inputStream.read());
						in = inputStream.read(); //Length of inputstring
						input = new byte[in];
						while(inputStream.available() < in){try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}};
						inputStream.read(input);
						System.out.println("[ROOMBA] " + new String(input));
						returnFromArduino = new byte[input.length];
						for(int i=0; i<input.length; i++){
							returnFromArduino[i] = input[i];
							//System.out.println(input[i]);
						}
						returnReady = true;
						break;
					default : System.err.println("Unknow protocol: " + in);
					}	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}