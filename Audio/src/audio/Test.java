package audio;

import java.io.File;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import javax.sound.sampled.TargetDataLine;

public class Test {
	
	final static int sampleMin = 99;
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		int smpRat = 0;
		int bitsPerSam = 0;
		while(!(smpRat > 0)) { 
			System.out.println("Please input the sample rate you would like to use, it needs to be larger then " +
			sampleMin + ", I reccomend 44000");
			String samp = s.nextLine();
			if(samp.equals(""))continue;
			//Matches any digit
			samp = samp.replaceAll("\n", "");
			if(samp.trim().replaceAll("\\d*", "").equals("")) {
				smpRat = Integer.parseInt(samp.trim());
				if(smpRat <= sampleMin) {
					System.out.println("Number is too Small, please renter");
					smpRat = 0;
					continue;
				}
				break;
			}
			else if(samp.trim().contains("-")) {
				System.out.println("This number should not be negetive");
				continue;
			}
			System.out.println("Please enter a number, could not read that");
		}
		s = new Scanner(System.in);
		while(!(bitsPerSam > 0)) { 
			System.out.println("Please input the bit rate you would like to use, it must be divisible by 8, I reccomend 8 or 16");
			String bit = s.nextLine();
			//Matches any digit
			bit = bit.replaceAll("\n", "");
			if(bit.trim().replaceAll("\\d*", "").equals("")) {
				bitsPerSam = Integer.parseInt(bit.trim());
				if(bitsPerSam <= 0) {
					System.out.println("Number is too Small, please renter");
					bitsPerSam = 0;
					continue;
				}
				if(bitsPerSam % 8 != 0) {
					System.out.println("Number is not divisible by 8, please renter");
					bitsPerSam = 0;
					continue;
				}
				break;
			}
			else if(bit.trim().contains("-")) {
				System.out.println("This number should not be negetive");
				continue;
			}
			System.out.println("Please enter a number, could not read that");
		}
		TargetDataLine ln = Reader.openMic(smpRat, bitsPerSam);
		if(ln == null) {
			System.out.print("Can't find a microphone, please connect one and restart the program");
			System.exit(1);
		}

		//Prep holder file
		File temp = new File("Temp.mus");
		FileManager.writeToFile(temp, StandardOpenOption.TRUNCATE_EXISTING, new byte[] {});
		byte[] data = new byte[ln.getBufferSize() / 5];
		Graphical g = new Graphical(temp, smpRat, bitsPerSam);
		// Begin audio capture.
		ln.start();
		g.start();
		// Here, stopped is a global boolean set by another thread.
		// Read the next chunk of data from the TargetDataLine.
		while(g.isAlive()) {
			if(g.wait) {
				while(g.wait) {}
				ln.flush();
			}
			byte[] d = new byte[data.length];
			ln.read(d, 0, data.length);
			g.addSeg(new Segment(d));
		}
		ln.stop();
		ln.close();
	}

}
