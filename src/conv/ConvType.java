package conv;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author PointerRage
 * 
 */
public enum ConvType {
	L2j { //to pts
		public boolean isSupport(File f) {
			return f.getName().endsWith(".l2j");
		}
		
		public String getFileName(int regx, int regy) {
			return regx + "_" + regy + "_conv.dat";
		}
		
		public ByteBuffer convGeo(ByteBuffer original, int x, int y) {
			ByteBuffer buffer = ByteBuffer.allocate(original.capacity() * 3);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.position(18);
			int cellCount = 0;
			int simpleBlocks = 0;
			int flatCount = 0;
			for(int i = 0; i < 65536; i++) {
				byte t = original.get();
				if(t == 0x00) {
					buffer.putShort((short) 0x00);
					flatCount++;
					simpleBlocks++;
				} else if(t == 0x01) {
					buffer.putShort((short) 0x0040);
					cellCount += 64;
					simpleBlocks++;
				}
				
				if(t == 0x00) {
					short val = original.getShort();
					buffer.putShort(val); //max height
					buffer.putShort(val); //min height
				} else if(t == 0x01) {
					for(int j = 0; j < 64; j++) {
						buffer.putShort(original.getShort());
					}
				} else {
					int cells = 0;
					int countPos = buffer.position();
					buffer.position(countPos + 2);
					for(int j = 0; j < 64; j++) {
						byte layers = original.get();
						cells += layers;
						cellCount += layers;
						buffer.putShort(layers);
						for(int l = 0; l < layers; l++) {
							buffer.putShort(original.getShort());
						}
					}
					
					buffer.putShort(countPos, (short)cells);
				}
			}
			buffer.flip();
			
			buffer.put((byte)x);
			buffer.put((byte)y);
			buffer.putShort((short)0x80);
			buffer.putShort((short)0x10);
			buffer.putInt(cellCount);
			buffer.putInt(simpleBlocks);
			buffer.putInt(flatCount);
			
			buffer.position(0);
			return buffer;
		}
	},
	Dat { //to java
		public boolean isSupport(File f) {
			return f.getName().endsWith("_conv.dat");
		}
		
		public String getFileName(int regx, int regy) {
			return regx + "_" + regy + ".l2j";
		}
		
		public ByteBuffer convGeo(ByteBuffer original, int x, int y) {
			ByteBuffer buffer = ByteBuffer.allocate(original.capacity());
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			original.position(18);
			for(int i = 0; i < 65536; i++) {
				short t = original.getShort();
				if(t == 0x00)
					buffer.put((byte) 0x00);
				else if(t == 0x0040)
					buffer.put((byte) 0x01);
				else 
					buffer.put((byte) 0x02);
				
				if(t == 0x00) {
					original.getShort();
					buffer.putShort(original.getShort());
				} else if(t == 0x0040) {
					for(int j = 0; j < 64; j++)
						buffer.putShort(original.getShort());
				} else {
					for(int j = 0; j < 64; j++) {
						short layers = original.getShort();
						buffer.put((byte)layers);
						for(int l = 0; l < layers; l++)
							buffer.putShort(original.getShort());
					}
				}
			}
			buffer.flip();
			return buffer;
		}
	}, 
	REPAIR { //repair dat
		public boolean isSupport(File f) {
			return f.getName().endsWith("_conv.dat");
		}
		
		public String getFileName(int regx, int regy) {
			return regx + "_" + regy + "_conv.dat";
		}
		
		public ByteBuffer convGeo(ByteBuffer original, int x, int y) {
			ByteBuffer buffer = ByteBuffer.allocate(original.capacity());
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.position(18);
			
			int cellCount = 0, simpleBlocks = 0, flatCount = 0;
			
			original.position(18); //skip original header
			for(int i = 0; i < 65536; i++) {
				short t = original.getShort();
				if(t == 0x00) {
					buffer.putShort(t);
					flatCount++;
					simpleBlocks++;
				} else if(t == 0x40) {
					buffer.putShort(t);
					cellCount += 64;
					simpleBlocks++;
				}
				
				if(t == 0x00) {
					buffer.putShort(original.getShort());
					buffer.putShort(original.getShort());
				} else if(t == 0x40) {
					for(int j = 0; j < 64; j++) {
						buffer.putShort(original.getShort());
					}
				} else {
					int cells = 0, countPos = buffer.position();
					buffer.position(countPos + 2);
					for(int j = 0; j < 64; j++) {
						short layers = original.getShort();
						cells += layers;
						cellCount += layers;
						buffer.putShort(layers);
						for(int l = 0; l < layers; l++) {
							buffer.putShort(original.getShort());
						}
					}
					
					buffer.putShort(countPos, (short)cells);
				}
			}
			buffer.flip();
			
			buffer.put((byte)x);
			buffer.put((byte)y);
			buffer.putShort((short)0x80);
			buffer.putShort((short)0x10);
			buffer.putInt(cellCount);
			buffer.putInt(simpleBlocks);
			buffer.putInt(flatCount);
			
			buffer.position(0);
			return buffer;
		}
	};
	
	public abstract boolean isSupport(File f);
	public abstract String getFileName(int regx, int regy);
	public abstract ByteBuffer convGeo(ByteBuffer original, int x, int y);
}
