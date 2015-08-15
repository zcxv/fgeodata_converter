## Geodata converter.

**Supports:**
 * l2j -> dat
 * dat -> l2j

**Version:** 1.1.1

**Commands:**

`-f / -folder` - input folder

`-o / -output` - output folder

**DAT header:**
*byte* - region by x axis
*byte* - region by y axis
*short/word* - unknown (value: 0x0080); not used in C4
*short/word* - unknown (value: 0x0010); not used in C4
*int/dword* - total cells in blocks + layers
*int/dword* - unknown; not used in C4
*int/dword* - unknown; not used in C4

Matcher for first dword (cells + layers):
```Java
public static void main(String[] args) throws Throwable {
  int original, real = 0;
  try(RandomAccessFile raf = new RandomAccessFile(new File("./16_10_conv.dat"), "r"); FileChannel fc= raf.getChannel()) {
    ByteBuffer bb = fc.map(MapMode.READ_ONLY, 0, fc.size()).order(ByteOrder.LITTLE_ENDIAN);
    bb.position(6);
    original = bb.getInt();
    bb.position(18);
    for(int i = 0; i < 65536; i++) {
      short blocktype = bb.getShort();
      if(blocktype == 0) {
        bb.position(bb.position() + 4);
      } else if(blocktype == 0x0040) {
        bb.position(bb.position() + 128);
        real += 64;
      } else /*if(blocktype == 0x0048)*/ {
        for(int j = 0; j < 64; j++) {
          short layers = bb.getShort();
          real += layers;
          bb.position(bb.position() + (layers << 1));
        }
      }
    }
    System.out.println(bb.position());
    System.out.println(bb.limit());
  }
  System.out.println("Original " + original + ", \r\n" + "real " + real);
}
```