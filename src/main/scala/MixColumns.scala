package aes

import chisel3._
import chisel3.util._

// Implement MixColumns
class MixColumns(Pipelined: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val state_in = Input(Vec(Params.StateLength, UInt(8.W)))
    val state_out = Output(Vec(Params.StateLength, UInt(8.W)))
  })

  val mul02 = VecInit(Array(
    0x00.U, 0x02.U, 0x04.U, 0x06.U, 0x08.U, 0x0a.U, 0x0c.U, 0x0e.U, 0x10.U, 0x12.U, 0x14.U, 0x16.U, 0x18.U, 0x1a.U, 0x1c.U, 0x1e.U,
    0x20.U, 0x22.U, 0x24.U, 0x26.U, 0x28.U, 0x2a.U, 0x2c.U, 0x2e.U, 0x30.U, 0x32.U, 0x34.U, 0x36.U, 0x38.U, 0x3a.U, 0x3c.U, 0x3e.U,
    0x40.U, 0x42.U, 0x44.U, 0x46.U, 0x48.U, 0x4a.U, 0x4c.U, 0x4e.U, 0x50.U, 0x52.U, 0x54.U, 0x56.U, 0x58.U, 0x5a.U, 0x5c.U, 0x5e.U,
    0x60.U, 0x62.U, 0x64.U, 0x66.U, 0x68.U, 0x6a.U, 0x6c.U, 0x6e.U, 0x70.U, 0x72.U, 0x74.U, 0x76.U, 0x78.U, 0x7a.U, 0x7c.U, 0x7e.U,
    0x80.U, 0x82.U, 0x84.U, 0x86.U, 0x88.U, 0x8a.U, 0x8c.U, 0x8e.U, 0x90.U, 0x92.U, 0x94.U, 0x96.U, 0x98.U, 0x9a.U, 0x9c.U, 0x9e.U,
    0xa0.U, 0xa2.U, 0xa4.U, 0xa6.U, 0xa8.U, 0xaa.U, 0xac.U, 0xae.U, 0xb0.U, 0xb2.U, 0xb4.U, 0xb6.U, 0xb8.U, 0xba.U, 0xbc.U, 0xbe.U,
    0xc0.U, 0xc2.U, 0xc4.U, 0xc6.U, 0xc8.U, 0xca.U, 0xcc.U, 0xce.U, 0xd0.U, 0xd2.U, 0xd4.U, 0xd6.U, 0xd8.U, 0xda.U, 0xdc.U, 0xde.U,
    0xe0.U, 0xe2.U, 0xe4.U, 0xe6.U, 0xe8.U, 0xea.U, 0xec.U, 0xee.U, 0xf0.U, 0xf2.U, 0xf4.U, 0xf6.U, 0xf8.U, 0xfa.U, 0xfc.U, 0xfe.U,
    0x1b.U, 0x19.U, 0x1f.U, 0x1d.U, 0x13.U, 0x11.U, 0x17.U, 0x15.U, 0x0b.U, 0x09.U, 0x0f.U, 0x0d.U, 0x03.U, 0x01.U, 0x07.U, 0x05.U,
    0x3b.U, 0x39.U, 0x3f.U, 0x3d.U, 0x33.U, 0x31.U, 0x37.U, 0x35.U, 0x2b.U, 0x29.U, 0x2f.U, 0x2d.U, 0x23.U, 0x21.U, 0x27.U, 0x25.U,
    0x5b.U, 0x59.U, 0x5f.U, 0x5d.U, 0x53.U, 0x51.U, 0x57.U, 0x55.U, 0x4b.U, 0x49.U, 0x4f.U, 0x4d.U, 0x43.U, 0x41.U, 0x47.U, 0x45.U,
    0x7b.U, 0x79.U, 0x7f.U, 0x7d.U, 0x73.U, 0x71.U, 0x77.U, 0x75.U, 0x6b.U, 0x69.U, 0x6f.U, 0x6d.U, 0x63.U, 0x61.U, 0x67.U, 0x65.U,
    0x9b.U, 0x99.U, 0x9f.U, 0x9d.U, 0x93.U, 0x91.U, 0x97.U, 0x95.U, 0x8b.U, 0x89.U, 0x8f.U, 0x8d.U, 0x83.U, 0x81.U, 0x87.U, 0x85.U,
    0xbb.U, 0xb9.U, 0xbf.U, 0xbd.U, 0xb3.U, 0xb1.U, 0xb7.U, 0xb5.U, 0xab.U, 0xa9.U, 0xaf.U, 0xad.U, 0xa3.U, 0xa1.U, 0xa7.U, 0xa5.U,
    0xdb.U, 0xd9.U, 0xdf.U, 0xdd.U, 0xd3.U, 0xd1.U, 0xd7.U, 0xd5.U, 0xcb.U, 0xc9.U, 0xcf.U, 0xcd.U, 0xc3.U, 0xc1.U, 0xc7.U, 0xc5.U,
    0xfb.U, 0xf9.U, 0xff.U, 0xfd.U, 0xf3.U, 0xf1.U, 0xf7.U, 0xf5.U, 0xeb.U, 0xe9.U, 0xef.U, 0xed.U, 0xe3.U, 0xe1.U, 0xe7.U, 0xe5.U))

  val mul03 = VecInit(Array(
    0x00.U, 0x03.U, 0x06.U, 0x05.U, 0x0c.U, 0x0f.U, 0x0a.U, 0x09.U, 0x18.U, 0x1b.U, 0x1e.U, 0x1d.U, 0x14.U, 0x17.U, 0x12.U, 0x11.U,
    0x30.U, 0x33.U, 0x36.U, 0x35.U, 0x3c.U, 0x3f.U, 0x3a.U, 0x39.U, 0x28.U, 0x2b.U, 0x2e.U, 0x2d.U, 0x24.U, 0x27.U, 0x22.U, 0x21.U,
    0x60.U, 0x63.U, 0x66.U, 0x65.U, 0x6c.U, 0x6f.U, 0x6a.U, 0x69.U, 0x78.U, 0x7b.U, 0x7e.U, 0x7d.U, 0x74.U, 0x77.U, 0x72.U, 0x71.U,
    0x50.U, 0x53.U, 0x56.U, 0x55.U, 0x5c.U, 0x5f.U, 0x5a.U, 0x59.U, 0x48.U, 0x4b.U, 0x4e.U, 0x4d.U, 0x44.U, 0x47.U, 0x42.U, 0x41.U,
    0xc0.U, 0xc3.U, 0xc6.U, 0xc5.U, 0xcc.U, 0xcf.U, 0xca.U, 0xc9.U, 0xd8.U, 0xdb.U, 0xde.U, 0xdd.U, 0xd4.U, 0xd7.U, 0xd2.U, 0xd1.U,
    0xf0.U, 0xf3.U, 0xf6.U, 0xf5.U, 0xfc.U, 0xff.U, 0xfa.U, 0xf9.U, 0xe8.U, 0xeb.U, 0xee.U, 0xed.U, 0xe4.U, 0xe7.U, 0xe2.U, 0xe1.U,
    0xa0.U, 0xa3.U, 0xa6.U, 0xa5.U, 0xac.U, 0xaf.U, 0xaa.U, 0xa9.U, 0xb8.U, 0xbb.U, 0xbe.U, 0xbd.U, 0xb4.U, 0xb7.U, 0xb2.U, 0xb1.U,
    0x90.U, 0x93.U, 0x96.U, 0x95.U, 0x9c.U, 0x9f.U, 0x9a.U, 0x99.U, 0x88.U, 0x8b.U, 0x8e.U, 0x8d.U, 0x84.U, 0x87.U, 0x82.U, 0x81.U,
    0x9b.U, 0x98.U, 0x9d.U, 0x9e.U, 0x97.U, 0x94.U, 0x91.U, 0x92.U, 0x83.U, 0x80.U, 0x85.U, 0x86.U, 0x8f.U, 0x8c.U, 0x89.U, 0x8a.U,
    0xab.U, 0xa8.U, 0xad.U, 0xae.U, 0xa7.U, 0xa4.U, 0xa1.U, 0xa2.U, 0xb3.U, 0xb0.U, 0xb5.U, 0xb6.U, 0xbf.U, 0xbc.U, 0xb9.U, 0xba.U,
    0xfb.U, 0xf8.U, 0xfd.U, 0xfe.U, 0xf7.U, 0xf4.U, 0xf1.U, 0xf2.U, 0xe3.U, 0xe0.U, 0xe5.U, 0xe6.U, 0xef.U, 0xec.U, 0xe9.U, 0xea.U,
    0xcb.U, 0xc8.U, 0xcd.U, 0xce.U, 0xc7.U, 0xc4.U, 0xc1.U, 0xc2.U, 0xd3.U, 0xd0.U, 0xd5.U, 0xd6.U, 0xdf.U, 0xdc.U, 0xd9.U, 0xda.U,
    0x5b.U, 0x58.U, 0x5d.U, 0x5e.U, 0x57.U, 0x54.U, 0x51.U, 0x52.U, 0x43.U, 0x40.U, 0x45.U, 0x46.U, 0x4f.U, 0x4c.U, 0x49.U, 0x4a.U,
    0x6b.U, 0x68.U, 0x6d.U, 0x6e.U, 0x67.U, 0x64.U, 0x61.U, 0x62.U, 0x73.U, 0x70.U, 0x75.U, 0x76.U, 0x7f.U, 0x7c.U, 0x79.U, 0x7a.U,
    0x3b.U, 0x38.U, 0x3d.U, 0x3e.U, 0x37.U, 0x34.U, 0x31.U, 0x32.U, 0x23.U, 0x20.U, 0x25.U, 0x26.U, 0x2f.U, 0x2c.U, 0x29.U, 0x2a.U,
    0x0b.U, 0x08.U, 0x0d.U, 0x0e.U, 0x07.U, 0x04.U, 0x01.U, 0x02.U, 0x13.U, 0x10.U, 0x15.U, 0x16.U, 0x1f.U, 0x1c.U, 0x19.U, 0x1a.U))

  /*
  val mul09 = VecInit(Array(
    0x00.U, 0x09.U, 0x12.U, 0x1b.U, 0x24.U, 0x2d.U, 0x36.U, 0x3f.U, 0x48.U, 0x41.U, 0x5a.U, 0x53.U, 0x6c.U, 0x65.U, 0x7e.U, 0x77.U,
    0x90.U, 0x99.U, 0x82.U, 0x8b.U, 0xb4.U, 0xbd.U, 0xa6.U, 0xaf.U, 0xd8.U, 0xd1.U, 0xca.U, 0xc3.U, 0xfc.U, 0xf5.U, 0xee.U, 0xe7.U,
    0x3b.U, 0x32.U, 0x29.U, 0x20.U, 0x1f.U, 0x16.U, 0x0d.U, 0x04.U, 0x73.U, 0x7a.U, 0x61.U, 0x68.U, 0x57.U, 0x5e.U, 0x45.U, 0x4c.U,
    0xab.U, 0xa2.U, 0xb9.U, 0xb0.U, 0x8f.U, 0x86.U, 0x9d.U, 0x94.U, 0xe3.U, 0xea.U, 0xf1.U, 0xf8.U, 0xc7.U, 0xce.U, 0xd5.U, 0xdc.U,
    0x76.U, 0x7f.U, 0x64.U, 0x6d.U, 0x52.U, 0x5b.U, 0x40.U, 0x49.U, 0x3e.U, 0x37.U, 0x2c.U, 0x25.U, 0x1a.U, 0x13.U, 0x08.U, 0x01.U,
    0xe6.U, 0xef.U, 0xf4.U, 0xfd.U, 0xc2.U, 0xcb.U, 0xd0.U, 0xd9.U, 0xae.U, 0xa7.U, 0xbc.U, 0xb5.U, 0x8a.U, 0x83.U, 0x98.U, 0x91.U,
    0x4d.U, 0x44.U, 0x5f.U, 0x56.U, 0x69.U, 0x60.U, 0x7b.U, 0x72.U, 0x05.U, 0x0c.U, 0x17.U, 0x1e.U, 0x21.U, 0x28.U, 0x33.U, 0x3a.U,
    0xdd.U, 0xd4.U, 0xcf.U, 0xc6.U, 0xf9.U, 0xf0.U, 0xeb.U, 0xe2.U, 0x95.U, 0x9c.U, 0x87.U, 0x8e.U, 0xb1.U, 0xb8.U, 0xa3.U, 0xaa.U,
    0xec.U, 0xe5.U, 0xfe.U, 0xf7.U, 0xc8.U, 0xc1.U, 0xda.U, 0xd3.U, 0xa4.U, 0xad.U, 0xb6.U, 0xbf.U, 0x80.U, 0x89.U, 0x92.U, 0x9b.U,
    0x7c.U, 0x75.U, 0x6e.U, 0x67.U, 0x58.U, 0x51.U, 0x4a.U, 0x43.U, 0x34.U, 0x3d.U, 0x26.U, 0x2f.U, 0x10.U, 0x19.U, 0x02.U, 0x0b.U,
    0xd7.U, 0xde.U, 0xc5.U, 0xcc.U, 0xf3.U, 0xfa.U, 0xe1.U, 0xe8.U, 0x9f.U, 0x96.U, 0x8d.U, 0x84.U, 0xbb.U, 0xb2.U, 0xa9.U, 0xa0.U,
    0x47.U, 0x4e.U, 0x55.U, 0x5c.U, 0x63.U, 0x6a.U, 0x71.U, 0x78.U, 0x0f.U, 0x06.U, 0x1d.U, 0x14.U, 0x2b.U, 0x22.U, 0x39.U, 0x30.U,
    0x9a.U, 0x93.U, 0x88.U, 0x81.U, 0xbe.U, 0xb7.U, 0xac.U, 0xa5.U, 0xd2.U, 0xdb.U, 0xc0.U, 0xc9.U, 0xf6.U, 0xff.U, 0xe4.U, 0xed.U,
    0x0a.U, 0x03.U, 0x18.U, 0x11.U, 0x2e.U, 0x27.U, 0x3c.U, 0x35.U, 0x42.U, 0x4b.U, 0x50.U, 0x59.U, 0x66.U, 0x6f.U, 0x74.U, 0x7d.U,
    0xa1.U, 0xa8.U, 0xb3.U, 0xba.U, 0x85.U, 0x8c.U, 0x97.U, 0x9e.U, 0xe9.U, 0xe0.U, 0xfb.U, 0xf2.U, 0xcd.U, 0xc4.U, 0xdf.U, 0xd6.U,
    0x31.U, 0x38.U, 0x23.U, 0x2a.U, 0x15.U, 0x1c.U, 0x07.U, 0x0e.U, 0x79.U, 0x70.U, 0x6b.U, 0x62.U, 0x5d.U, 0x54.U, 0x4f.U, 0x46.U))

  val mul11 = VecInit(Array(
    0x00.U, 0x0b.U, 0x16.U, 0x1d.U, 0x2c.U, 0x27.U, 0x3a.U, 0x31.U, 0x58.U, 0x53.U, 0x4e.U, 0x45.U, 0x74.U, 0x7f.U, 0x62.U, 0x69.U,
    0xb0.U, 0xbb.U, 0xa6.U, 0xad.U, 0x9c.U, 0x97.U, 0x8a.U, 0x81.U, 0xe8.U, 0xe3.U, 0xfe.U, 0xf5.U, 0xc4.U, 0xcf.U, 0xd2.U, 0xd9.U,
    0x7b.U, 0x70.U, 0x6d.U, 0x66.U, 0x57.U, 0x5c.U, 0x41.U, 0x4a.U, 0x23.U, 0x28.U, 0x35.U, 0x3e.U, 0x0f.U, 0x04.U, 0x19.U, 0x12.U,
    0xcb.U, 0xc0.U, 0xdd.U, 0xd6.U, 0xe7.U, 0xec.U, 0xf1.U, 0xfa.U, 0x93.U, 0x98.U, 0x85.U, 0x8e.U, 0xbf.U, 0xb4.U, 0xa9.U, 0xa2.U,
    0xf6.U, 0xfd.U, 0xe0.U, 0xeb.U, 0xda.U, 0xd1.U, 0xcc.U, 0xc7.U, 0xae.U, 0xa5.U, 0xb8.U, 0xb3.U, 0x82.U, 0x89.U, 0x94.U, 0x9f.U,
    0x46.U, 0x4d.U, 0x50.U, 0x5b.U, 0x6a.U, 0x61.U, 0x7c.U, 0x77.U, 0x1e.U, 0x15.U, 0x08.U, 0x03.U, 0x32.U, 0x39.U, 0x24.U, 0x2f.U,
    0x8d.U, 0x86.U, 0x9b.U, 0x90.U, 0xa1.U, 0xaa.U, 0xb7.U, 0xbc.U, 0xd5.U, 0xde.U, 0xc3.U, 0xc8.U, 0xf9.U, 0xf2.U, 0xef.U, 0xe4.U,
    0x3d.U, 0x36.U, 0x2b.U, 0x20.U, 0x11.U, 0x1a.U, 0x07.U, 0x0c.U, 0x65.U, 0x6e.U, 0x73.U, 0x78.U, 0x49.U, 0x42.U, 0x5f.U, 0x54.U,
    0xf7.U, 0xfc.U, 0xe1.U, 0xea.U, 0xdb.U, 0xd0.U, 0xcd.U, 0xc6.U, 0xaf.U, 0xa4.U, 0xb9.U, 0xb2.U, 0x83.U, 0x88.U, 0x95.U, 0x9e.U,
    0x47.U, 0x4c.U, 0x51.U, 0x5a.U, 0x6b.U, 0x60.U, 0x7d.U, 0x76.U, 0x1f.U, 0x14.U, 0x09.U, 0x02.U, 0x33.U, 0x38.U, 0x25.U, 0x2e.U,
    0x8c.U, 0x87.U, 0x9a.U, 0x91.U, 0xa0.U, 0xab.U, 0xb6.U, 0xbd.U, 0xd4.U, 0xdf.U, 0xc2.U, 0xc9.U, 0xf8.U, 0xf3.U, 0xee.U, 0xe5.U,
    0x3c.U, 0x37.U, 0x2a.U, 0x21.U, 0x10.U, 0x1b.U, 0x06.U, 0x0d.U, 0x64.U, 0x6f.U, 0x72.U, 0x79.U, 0x48.U, 0x43.U, 0x5e.U, 0x55.U,
    0x01.U, 0x0a.U, 0x17.U, 0x1c.U, 0x2d.U, 0x26.U, 0x3b.U, 0x30.U, 0x59.U, 0x52.U, 0x4f.U, 0x44.U, 0x75.U, 0x7e.U, 0x63.U, 0x68.U,
    0xb1.U, 0xba.U, 0xa7.U, 0xac.U, 0x9d.U, 0x96.U, 0x8b.U, 0x80.U, 0xe9.U, 0xe2.U, 0xff.U, 0xf4.U, 0xc5.U, 0xce.U, 0xd3.U, 0xd8.U,
    0x7a.U, 0x71.U, 0x6c.U, 0x67.U, 0x56.U, 0x5d.U, 0x40.U, 0x4b.U, 0x22.U, 0x29.U, 0x34.U, 0x3f.U, 0x0e.U, 0x05.U, 0x18.U, 0x13.U,
    0xca.U, 0xc1.U, 0xdc.U, 0xd7.U, 0xe6.U, 0xed.U, 0xf0.U, 0xfb.U, 0x92.U, 0x99.U, 0x84.U, 0x8f.U, 0xbe.U, 0xb5.U, 0xa8.U, 0xa3.U))

  val mul13 = VecInit(Array(
    0x00.U, 0x0d.U, 0x1a.U, 0x17.U, 0x34.U, 0x39.U, 0x2e.U, 0x23.U, 0x68.U, 0x65.U, 0x72.U, 0x7f.U, 0x5c.U, 0x51.U, 0x46.U, 0x4b.U,
    0xd0.U, 0xdd.U, 0xca.U, 0xc7.U, 0xe4.U, 0xe9.U, 0xfe.U, 0xf3.U, 0xb8.U, 0xb5.U, 0xa2.U, 0xaf.U, 0x8c.U, 0x81.U, 0x96.U, 0x9b.U,
    0xbb.U, 0xb6.U, 0xa1.U, 0xac.U, 0x8f.U, 0x82.U, 0x95.U, 0x98.U, 0xd3.U, 0xde.U, 0xc9.U, 0xc4.U, 0xe7.U, 0xea.U, 0xfd.U, 0xf0.U,
    0x6b.U, 0x66.U, 0x71.U, 0x7c.U, 0x5f.U, 0x52.U, 0x45.U, 0x48.U, 0x03.U, 0x0e.U, 0x19.U, 0x14.U, 0x37.U, 0x3a.U, 0x2d.U, 0x20.U,
    0x6d.U, 0x60.U, 0x77.U, 0x7a.U, 0x59.U, 0x54.U, 0x43.U, 0x4e.U, 0x05.U, 0x08.U, 0x1f.U, 0x12.U, 0x31.U, 0x3c.U, 0x2b.U, 0x26.U,
    0xbd.U, 0xb0.U, 0xa7.U, 0xaa.U, 0x89.U, 0x84.U, 0x93.U, 0x9e.U, 0xd5.U, 0xd8.U, 0xcf.U, 0xc2.U, 0xe1.U, 0xec.U, 0xfb.U, 0xf6.U,
    0xd6.U, 0xdb.U, 0xcc.U, 0xc1.U, 0xe2.U, 0xef.U, 0xf8.U, 0xf5.U, 0xbe.U, 0xb3.U, 0xa4.U, 0xa9.U, 0x8a.U, 0x87.U, 0x90.U, 0x9d.U,
    0x06.U, 0x0b.U, 0x1c.U, 0x11.U, 0x32.U, 0x3f.U, 0x28.U, 0x25.U, 0x6e.U, 0x63.U, 0x74.U, 0x79.U, 0x5a.U, 0x57.U, 0x40.U, 0x4d.U,
    0xda.U, 0xd7.U, 0xc0.U, 0xcd.U, 0xee.U, 0xe3.U, 0xf4.U, 0xf9.U, 0xb2.U, 0xbf.U, 0xa8.U, 0xa5.U, 0x86.U, 0x8b.U, 0x9c.U, 0x91.U,
    0x0a.U, 0x07.U, 0x10.U, 0x1d.U, 0x3e.U, 0x33.U, 0x24.U, 0x29.U, 0x62.U, 0x6f.U, 0x78.U, 0x75.U, 0x56.U, 0x5b.U, 0x4c.U, 0x41.U,
    0x61.U, 0x6c.U, 0x7b.U, 0x76.U, 0x55.U, 0x58.U, 0x4f.U, 0x42.U, 0x09.U, 0x04.U, 0x13.U, 0x1e.U, 0x3d.U, 0x30.U, 0x27.U, 0x2a.U,
    0xb1.U, 0xbc.U, 0xab.U, 0xa6.U, 0x85.U, 0x88.U, 0x9f.U, 0x92.U, 0xd9.U, 0xd4.U, 0xc3.U, 0xce.U, 0xed.U, 0xe0.U, 0xf7.U, 0xfa.U,
    0xb7.U, 0xba.U, 0xad.U, 0xa0.U, 0x83.U, 0x8e.U, 0x99.U, 0x94.U, 0xdf.U, 0xd2.U, 0xc5.U, 0xc8.U, 0xeb.U, 0xe6.U, 0xf1.U, 0xfc.U,
    0x67.U, 0x6a.U, 0x7d.U, 0x70.U, 0x53.U, 0x5e.U, 0x49.U, 0x44.U, 0x0f.U, 0x02.U, 0x15.U, 0x18.U, 0x3b.U, 0x36.U, 0x21.U, 0x2c.U,
    0x0c.U, 0x01.U, 0x16.U, 0x1b.U, 0x38.U, 0x35.U, 0x22.U, 0x2f.U, 0x64.U, 0x69.U, 0x7e.U, 0x73.U, 0x50.U, 0x5d.U, 0x4a.U, 0x47.U,
    0xdc.U, 0xd1.U, 0xc6.U, 0xcb.U, 0xe8.U, 0xe5.U, 0xf2.U, 0xff.U, 0xb4.U, 0xb9.U, 0xae.U, 0xa3.U, 0x80.U, 0x8d.U, 0x9a.U, 0x97.U))

  val mul14 = VecInit(Array(
    0x00.U, 0x0e.U, 0x1c.U, 0x12.U, 0x38.U, 0x36.U, 0x24.U, 0x2a.U, 0x70.U, 0x7e.U, 0x6c.U, 0x62.U, 0x48.U, 0x46.U, 0x54.U, 0x5a.U,
    0xe0.U, 0xee.U, 0xfc.U, 0xf2.U, 0xd8.U, 0xd6.U, 0xc4.U, 0xca.U, 0x90.U, 0x9e.U, 0x8c.U, 0x82.U, 0xa8.U, 0xa6.U, 0xb4.U, 0xba.U,
    0xdb.U, 0xd5.U, 0xc7.U, 0xc9.U, 0xe3.U, 0xed.U, 0xff.U, 0xf1.U, 0xab.U, 0xa5.U, 0xb7.U, 0xb9.U, 0x93.U, 0x9d.U, 0x8f.U, 0x81.U,
    0x3b.U, 0x35.U, 0x27.U, 0x29.U, 0x03.U, 0x0d.U, 0x1f.U, 0x11.U, 0x4b.U, 0x45.U, 0x57.U, 0x59.U, 0x73.U, 0x7d.U, 0x6f.U, 0x61.U,
    0xad.U, 0xa3.U, 0xb1.U, 0xbf.U, 0x95.U, 0x9b.U, 0x89.U, 0x87.U, 0xdd.U, 0xd3.U, 0xc1.U, 0xcf.U, 0xe5.U, 0xeb.U, 0xf9.U, 0xf7.U,
    0x4d.U, 0x43.U, 0x51.U, 0x5f.U, 0x75.U, 0x7b.U, 0x69.U, 0x67.U, 0x3d.U, 0x33.U, 0x21.U, 0x2f.U, 0x05.U, 0x0b.U, 0x19.U, 0x17.U,
    0x76.U, 0x78.U, 0x6a.U, 0x64.U, 0x4e.U, 0x40.U, 0x52.U, 0x5c.U, 0x06.U, 0x08.U, 0x1a.U, 0x14.U, 0x3e.U, 0x30.U, 0x22.U, 0x2c.U,
    0x96.U, 0x98.U, 0x8a.U, 0x84.U, 0xae.U, 0xa0.U, 0xb2.U, 0xbc.U, 0xe6.U, 0xe8.U, 0xfa.U, 0xf4.U, 0xde.U, 0xd0.U, 0xc2.U, 0xcc.U,
    0x41.U, 0x4f.U, 0x5d.U, 0x53.U, 0x79.U, 0x77.U, 0x65.U, 0x6b.U, 0x31.U, 0x3f.U, 0x2d.U, 0x23.U, 0x09.U, 0x07.U, 0x15.U, 0x1b.U,
    0xa1.U, 0xaf.U, 0xbd.U, 0xb3.U, 0x99.U, 0x97.U, 0x85.U, 0x8b.U, 0xd1.U, 0xdf.U, 0xcd.U, 0xc3.U, 0xe9.U, 0xe7.U, 0xf5.U, 0xfb.U,
    0x9a.U, 0x94.U, 0x86.U, 0x88.U, 0xa2.U, 0xac.U, 0xbe.U, 0xb0.U, 0xea.U, 0xe4.U, 0xf6.U, 0xf8.U, 0xd2.U, 0xdc.U, 0xce.U, 0xc0.U,
    0x7a.U, 0x74.U, 0x66.U, 0x68.U, 0x42.U, 0x4c.U, 0x5e.U, 0x50.U, 0x0a.U, 0x04.U, 0x16.U, 0x18.U, 0x32.U, 0x3c.U, 0x2e.U, 0x20.U,
    0xec.U, 0xe2.U, 0xf0.U, 0xfe.U, 0xd4.U, 0xda.U, 0xc8.U, 0xc6.U, 0x9c.U, 0x92.U, 0x80.U, 0x8e.U, 0xa4.U, 0xaa.U, 0xb8.U, 0xb6.U,
    0x0c.U, 0x02.U, 0x10.U, 0x1e.U, 0x34.U, 0x3a.U, 0x28.U, 0x26.U, 0x7c.U, 0x72.U, 0x60.U, 0x6e.U, 0x44.U, 0x4a.U, 0x58.U, 0x56.U,
    0x37.U, 0x39.U, 0x2b.U, 0x25.U, 0x0f.U, 0x01.U, 0x13.U, 0x1d.U, 0x47.U, 0x49.U, 0x5b.U, 0x55.U, 0x7f.U, 0x71.U, 0x63.U, 0x6d.U,
    0xd7.U, 0xd9.U, 0xcb.U, 0xc5.U, 0xef.U, 0xe1.U, 0xf3.U, 0xfd.U, 0xa7.U, 0xa9.U, 0xbb.U, 0xb5.U, 0x9f.U, 0x91.U, 0x83.U, 0x8d.U))
*/
  val tmp_state = Wire(Vec(Params.StateLength, UInt(8.W)))

  tmp_state(0) := mul02(io.state_in(0)) ^ mul03(io.state_in(1)) ^ io.state_in(2) ^ io.state_in(3)
  tmp_state(1) := io.state_in(0) ^ mul02(io.state_in(1)) ^ mul03(io.state_in(2)) ^ io.state_in(3)
  tmp_state(2) := io.state_in(0) ^ io.state_in(1) ^ mul02(io.state_in(2)) ^ mul03(io.state_in(3))
  tmp_state(3) := mul03(io.state_in(0)) ^ io.state_in(1) ^ io.state_in(2) ^ mul02(io.state_in(3))

  tmp_state(4) := mul02(io.state_in(4)) ^ mul03(io.state_in(5)) ^ io.state_in(6) ^ io.state_in(7)
  tmp_state(5) := io.state_in(4) ^ mul02(io.state_in(5)) ^ mul03(io.state_in(6)) ^ io.state_in(7)
  tmp_state(6) := io.state_in(4) ^ io.state_in(5) ^ mul02(io.state_in(6)) ^ mul03(io.state_in(7))
  tmp_state(7) := mul03(io.state_in(4)) ^ io.state_in(5) ^ io.state_in(6) ^ mul02(io.state_in(7))

  tmp_state(8) := mul02(io.state_in(8)) ^ mul03(io.state_in(9)) ^ io.state_in(10) ^ io.state_in(11)
  tmp_state(9) := io.state_in(8) ^ mul02(io.state_in(9)) ^ mul03(io.state_in(10)) ^ io.state_in(11)
  tmp_state(10) := io.state_in(8) ^ io.state_in(9) ^ mul02(io.state_in(10)) ^ mul03(io.state_in(11))
  tmp_state(11) := mul03(io.state_in(8)) ^ io.state_in(9) ^ io.state_in(10) ^ mul02(io.state_in(11))

  tmp_state(12) := mul02(io.state_in(12)) ^ mul03(io.state_in(13)) ^ io.state_in(14) ^ io.state_in(15)
  tmp_state(13) := io.state_in(12) ^ mul02(io.state_in(13)) ^ mul03(io.state_in(14)) ^ io.state_in(15)
  tmp_state(14) := io.state_in(12) ^ io.state_in(13) ^ mul02(io.state_in(14)) ^ mul03(io.state_in(15))
  tmp_state(15) := mul03(io.state_in(12)) ^ io.state_in(13) ^ io.state_in(14) ^ mul02(io.state_in(15))

  if (Pipelined) {
    io.state_out := ShiftRegister(tmp_state, 1)
  } else {
    io.state_out := tmp_state
  }
}

object MixColumns {
  def apply(Pipelined: Boolean = false): MixColumns = Module(new MixColumns(Pipelined))
}