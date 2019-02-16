package Blink

import spinal.core._
import spinal.lib._

class Blink extends Component {
	val io = new Bundle {
		val LED = out UInt(8 bits)
		val CLOCK_50 = in Bool
		val KEY = in UInt(2 bits)
	}

	val coreClockDomain = ClockDomain(
		clock = io.CLOCK_50,
		reset = io.KEY(0),
		config = ClockDomainConfig(
			clockEdge = RISING,
			resetKind = ASYNC,
			resetActiveLevel = LOW
		)
	)

	new ClockingArea(coreClockDomain) {
		val count = Reg(UInt(26 bits)) init(0)
		val LED = Reg(UInt(8 bits)) init(0)
		val count_up = Reg(Bool) init(True)

		when (count < 25000000) {
			count := count + 1
		} otherwise {
			count := 0
			when (count_up) {
				LED := LED + 1
			} otherwise {
				LED := LED - 1
			}

		}

		when (io.KEY(1).fall) {
			count_up := !count_up
		}

		io.LED := LED
	}
}

// Generate Verilog
object BlinkVerilog {
	def main(args: Array[String]): Unit = {
		val target_directory = "output/Blink"
		new java.io.File(target_directory).mkdirs()
		SpinalConfig(targetDirectory = target_directory).generateVerilog(new Blink)
	}
}