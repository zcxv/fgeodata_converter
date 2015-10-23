## Geodata converter.

**Supports:**<br/>
 * l2j -> dat<br/>
 * dat -> l2j<br/>

**Version:** 1.1.1

**Commands:**<br/>
`-f / -folder` - input folder<br/>
`-o / -output` - output folder<br/>
`-repair` - repair dat files (header, multilevel cells)<br/>

**DAT header:**<br/>
*byte* - region by x axis<br/>
*byte* - region by y axis<br/>
*short/word* - unknown (value: 0x0080)<br/>
*short/word* - discrete (value: 0x0010)<br/>
*int/dword* - total cells in blocks<br/>
*int/dword* - simple blocks count: flat and complex<br/>
*int/dword* - flat block count<br/>
