package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Reader {
	public static TargetDataLine openMic(int sampleRat, int bitsPerSam) {
		TargetDataLine ln = null;
		AudioFormat fmt = new AudioFormat(sampleRat, bitsPerSam, 1, true, true);
		DataLine.Info dlInf = new DataLine.Info(TargetDataLine.class, fmt);
		if(!AudioSystem.isLineSupported(dlInf)) {
			return null;
		}
		
		try {
		    ln = (TargetDataLine) AudioSystem.getTargetDataLine(fmt);
		    ln.open(fmt);
		} catch (LineUnavailableException ex) {
		    // Handle the error ... 
		}
		return ln;
	}
}
