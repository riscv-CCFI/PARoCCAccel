package PARoCCAccelConfig

import chisel3.util._
import freechips.rocketchip.config._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.rocket._
import freechips.rocketchip.tile._
import freechips.rocketchip.util._
import PARoCCAccel._

class WithPARoCCAccel extends Config((site,here,up) => {
    case BuildRoCC => Seq(
        (p:Parameters) => {
            val regWidth = 64    
            val lcmAccel = LazyModule(new PARoCCAccel(OpcodeSet.custom0, regWidth)(p))
            lcmAccel
        }
    )
})
