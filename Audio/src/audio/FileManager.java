package audio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileManager {

	public static void writeToFile(File writeTo, StandardOpenOption option, byte  ... bytes) {
		System.out.println("Begining to write to " + writeTo.getAbsolutePath());
		try (BufferedOutputStream writer = new BufferedOutputStream(Files.newOutputStream(writeTo.toPath(), option, StandardOpenOption.WRITE, StandardOpenOption.CREATE))) {
			/* 
		    REMEMBER 
		if(text) 
			for(int I = 0; I < strings.length; I++) {
				if(strings[I] != null) {
					count++;
					writer.write(strings[I]);
					if(I != strings.length) {
						writer.newLine();
					}
				}
				if(I == strings.length) {
					writer.close();
				}
			}
			*/
		writer.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeWAVFromFile(File writeTo, File dataFrom, int sampleR, int bitsPerS) {
		byte[] fromFile = readFile(dataFrom);
		byte[] toWrite = null;
		//FileMetaType
		toWrite = addArray(toWrite, "RIFF".getBytes(StandardCharsets.US_ASCII));
		//Size of the rest of the file
		toWrite = addArray(toWrite, bigToLit(fromFile.length + 36));
		//WAVE ID
		toWrite = addArray(toWrite, "WAVE".getBytes(StandardCharsets.US_ASCII));
		
		//FORMAT CHUNK
		
		//Format chunk header
		toWrite = addArray(toWrite, "fmt ".getBytes(StandardCharsets.US_ASCII));
		//Format chunk size
		toWrite = addArray(toWrite, bigToLit(16));
		//Is it compressed? 1 is raw data, anything else has specified meanings
		//Needs to be 2 bytes long
		byte[] one = bigToLit(1);
		byte[] toA = new byte[2];
		toA[0] = one[0];
		toA[1] = one[1];
		toWrite = addArray(toWrite, toA);
		//Channels encoded, for my use, 1
		toWrite = addArray(toWrite, toA);
		//Rate of samples per second
		toWrite = addArray(toWrite, bigToLit(sampleR));
		//Byterate = SampleRate * Number Of Channels * Bits per sample / 8 to make it bytes
		toWrite = addArray(toWrite, bigToLit(sampleR * 1 * bitsPerS / 8));
		//BlockAllign = NumChannels * BitsPerSample / 8 Number of bytes for one sample
		ByteBuffer buff = ByteBuffer.allocate(4);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		buff.putInt(1 * bitsPerS / 8);
		one = buff.array();
		toA = new byte[2];
		toA[0] = one[0];
		toA[1] = one[1];
		toWrite = addArray(toWrite, toA);
		//Bits per Sample, needs to be 2 bytes long
		one = bigToLit(bitsPerS);
		toA = new byte[2];
		toA[0] = one[0];
		toA[1] = one[1];
		toWrite = addArray(toWrite, toA);
		
		//DATA CHUNK
		
		//Data chunk header
		toWrite = addArray(toWrite, "data".getBytes(StandardCharsets.US_ASCII));
		//Data chunk size
		toWrite = addArray(toWrite, bigToLit(fromFile.length));
		//Data
		buff = ByteBuffer.allocate(fromFile.length);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		buff.put(fromFile);
		fromFile = buff.array();
		toWrite = addArray(toWrite, fromFile);
		writeToFile(writeTo, StandardOpenOption.TRUNCATE_EXISTING, toWrite);
	}

	public static byte[] readFile(File f) {
		if(f.exists() && f.canRead()) {
			byte[] cont = null;
			try {
				InputStream reader = Files.newInputStream(f.toPath(), StandardOpenOption.READ);
				while(reader.available() != 0) {
					byte[] b = new byte[reader.available()];
					reader.read(b);
					cont = addArray(cont, b);
					System.out.println(cont.length);
				}
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cont;
		}
		return null;
	}
	
	public static byte[] addArray(byte[] a1, byte[] a2){
		if(a1 == null) return a2;
		if(a2 == null) return a1;
		int sz = 0;
		for(byte t : a1)sz++;
		for(byte t : a2)sz++;
		byte[] holder = new byte[sz];
		sz = 0;
		for(byte t : a1) {holder[sz] = t;sz++;}
		for(byte t : a2) {holder[sz] = t;sz++;}
		return holder;
	}
	
	public static byte[] bigToLit(int toFl) {
		ByteBuffer buff = ByteBuffer.allocate(4);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		buff.putInt(toFl);
		buff.position(0);
		return buff.array();
	}
}
