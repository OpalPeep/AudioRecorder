package audio;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.nio.file.StandardOpenOption;

public class Graphical extends Thread{

	Frame frame;
	LimitedQueueNode<Segment> head;
	LimitedQueueNode<Segment> toWrite;
	//For Writting
	File temp;
	int samRate;
	int bitPeSam;
	
	int multiX = 1;
	int multiY = 1;
	BufferStrategy strats;
	Graphics g;
	boolean run = true;
	boolean wait = false;
	boolean leftC = false;
	int cX;
	int cY;
	int curX = 0;
	int curY = 0;
	
	Graphical(File writeT, int sampleR, int bitsPerS){
		frame = new Frame();
		head = new LimitedQueueNode<Segment>(new Segment(new byte[] {0}));
		Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
		Insets inst = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
		int wdt = sz.width - inst.right - inst.left;
		int hght = sz.height - inst.bottom - inst.top;
		frame.setSize(wdt, hght);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		temp = writeT;
		samRate = sampleR;
		bitPeSam = bitsPerS;
		setMultiX(frame.getWidth() / 100);
		setMultiY(10);
		head.makeNext(new Segment(new byte[]{0}));
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				handleKeyPress(e);
			}
		});
		frame.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				handleMouseMovement(e);
			}
		});
		frame.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				handleMouseRelease(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				handleMousePress(e);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
	}
	Segment seg;
	protected void handleMousePress(MouseEvent e) {
		if(wait && e.getButton() == MouseEvent.BUTTON1) {
			leftC = true;
			cX = e.getX();
			cY = e.getY();
			curX = e.getX();
			curY = e.getY();
			boolean fnd = false;
			LimitedQueueNode<Segment> node = head;
			while(!fnd && node != null){
				if(node.getTailLength() == head.getTailLength() - (cX / multiX)) {
					seg = node.getValue();
					fnd = true;
				}
				node = node.getNext();
			}	
		}
	}

	protected void handleMouseRelease(MouseEvent e) {
		if(leftC && seg != null) {
			seg.addToAll((byte)(seg.getPotenVal() - seg.getVal()));
			seg = null;
		}
		if(e.getButton() == MouseEvent.BUTTON1)leftC = false;
	}

	protected void handleMouseMovement(MouseEvent e) {
		if(wait && leftC) {
			curX = e.getX();
			curY = e.getY();
			double toAdd =  (((((double)frame.getHeight()) / 2 ) - ((double)curY)));
			if(seg != null && toAdd != 0) {
				seg.setPotenVal(seg.avg + toAdd);
			}
		}
	}

	protected void handleKeyPress(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE : run = false;
				byte[] data = null;
				while(toWrite != null) {
					data = FileManager.addArray(data, toWrite.getValue().getFull());
					toWrite = toWrite.getNext();
				}
				while(head != null) {
					data = FileManager.addArray(data, head.getValue().getFull());
					head = head.getNext();
				}
				FileManager.writeToFile(temp, StandardOpenOption.APPEND, data);
				break;
			case KeyEvent.VK_SPACE : wait = !wait; 		
				if(wait == false) {leftC = false;if(seg != null)seg.setPotenVal(seg.getVal());}
				break;
		}
	}

	public void addSeg(Segment seg) {
		if(run)
		synchronized (head) {
			head.makeNext(seg);
		}
	}
	
	@Override
	public void run() {
		while(run) {
			g = frame.getBufferStrategy().getDrawGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
			if(leftC) {
				int dex = (cX / multiX);
				int sep = dex * multiX;
				g.setColor(new Color(100, 255, 0, 50));
				g.fillRect(sep - (multiX / 2), 0, multiX , frame.getHeight());
			}
			g.setColor(Color.RED);
			LimitedQueueNode<Segment> node = head;
			int I = 0;
			while(node != null) {
				g.drawLine(I * multiX, (frame.getHeight() / 2), I * multiX, (frame.getHeight() / 2) - ((int)node.getValue().getVal() * multiY));
				if(node.nxt != null) {
					g.drawLine(I * multiX, (frame.getHeight() / 2) - ((int)node.getValue().getVal() * multiY), (I + 1) * multiX, (frame.getHeight() / 2) - ((int)node.getNext().getValue().getVal() * multiY));
					if(node.getNext().getValue().getPotenVal() != node.getNext().getValue().getVal()) {
						g.setColor(Color.BLUE);
						g.drawLine(I * multiX, (frame.getHeight() / 2), (I + 1) * multiX, (frame.getHeight() / 2) - ((int)node.getNext().getValue().getPotenVal() * multiY));
						g.setColor(Color.RED);
					}
				}
				if(node.getValue().potentialAvg != node.getValue().avg) {
					g.setColor(Color.BLUE);
					g.drawLine(I * multiX, (frame.getHeight() / 2), I * multiX, (frame.getHeight() / 2) - ((int)node.getValue().getPotenVal() * multiY));
					if(node.nxt != null)
						g.drawLine(I * multiX, (frame.getHeight() / 2) - ((int)node.getValue().getPotenVal() * multiY), (I + 1) * multiX, (frame.getHeight() / 2) - ((int)node.getNext().getValue().getVal() * multiY));
					g.setColor(Color.RED);
				}
				node = node.getNext();
				I++;
			}
			if(I + 1 > frame.getWidth() / multiX) {
				if(toWrite == null)toWrite = new LimitedQueueNode<Segment>(head.getValue());
				else toWrite.makeNext(head.getValue());
				if(toWrite.getTailLength() == 100) {
					byte[] data = null;
					while(toWrite != null) {
						data = FileManager.addArray(data, toWrite.getValue().getFull());
						toWrite = toWrite.getNext();
					}
					FileManager.writeToFile(temp, StandardOpenOption.APPEND, data);
				}
				head = head.getNext();
			}
			frame.getBufferStrategy().show();
		}
		//User Pressed Escape
		synchronized (this) {
			try {
				this.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileManager.writeWAVFromFile(new File("Done.wav"), temp, samRate, bitPeSam);
		System.out.println("Done Writing");;
		frame.dispose();
		
	}
	
	public void setMultiX(int x) {multiX = x;}
	public void setMultiY(int y) { multiY = y;}
}
