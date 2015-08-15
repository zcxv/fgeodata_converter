package conv;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author PointerRage
 */
public class Starter {
	private final static Logger log = LoggerFactory.getLogger(Starter.class);
	
	public static void main(String[] args) {
		File folder = null;
		File output = null;
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-f") || args[i].equals("-folder")) {
				if(++i >= args.length) {
					log.error("folder not found");
					return;
				}
					
				folder = new File(args[i]);
			} else if(args[i].equals("-o") || args[i].equals("-output")) {
				if(++i >= args.length) {
					log.error("output not found");
					return;
				}
				
				output = new File(args[i]);
			}
		}
		
		if(folder == null || output==null) {
			log.error("output or input not found!");
			return;
		}
			
		
		if(!folder.exists() || !folder.isDirectory()) {
			log.error("folder not found {}", folder);
			return;
		}
		if(!output.exists() || !output.isDirectory())
			output.mkdirs();
	 
		log.info("Processing...");
		for(File f : folder.listFiles()) {
			final ConvType t = ConvType.L2j.isSupport(f) ? ConvType.L2j : ConvType.Dat.isSupport(f) ? ConvType.Dat : null;
			if(t == null) continue;
			
			log.info("{} - {}", f, t.name());
			int regx = Integer.parseInt(f.getName().substring(0, 2)),
				regy = Integer.parseInt(f.getName().substring(3, 5));
			
			try(RandomAccessFile raf = new RandomAccessFile(f, "r"); FileChannel fc = raf.getChannel()) {
				MappedByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, raf.length());
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				ByteBuffer convBuffer = t.convGeo(buffer, regx, regy);
				File out = new File(output, t.getFileName(regx, regy));
				out.createNewFile();
				try(RandomAccessFile rout = new RandomAccessFile(out, "rw"); FileChannel fcr = rout.getChannel()) {
					fcr.write(convBuffer);
				}
			} catch(Throwable e) {
				log.error("error {}", f, e);
			}
		}
	}

}
