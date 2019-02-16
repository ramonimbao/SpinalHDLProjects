package ShiftRegister

import spinal.core._

object Direction extends SpinalEnum {
	val Left, Right = newElement()
}

class ShiftRegister extends Component {
	val io = new Bundle {
		val KEY = in UInt(2 bits)
		val LED = out UInt(8 bits)
		val CLOCK_50 = in Bool
		val SW = in UInt(4 bits)
	}

	val coreClockDomain = ClockDomain(
		clock = io.KEY(0),
		reset = io.SW(3),
		config = ClockDomainConfig(
			clockEdge = FALLING,
			resetKind = ASYNC,
			resetActiveLevel = LOW
		)
	)

	new ClockingArea(coreClockDomain) {
		val dir = Reg(Direction) init (Direction.Left)
		val data = Reg(UInt(8 bits)) init (0)

		when(io.KEY(0).fall) {
			when(dir === Direction.Left) {
				data := data |<< 1 | U(!io.KEY(1)).resized
			} otherwise {
				data := data |>> 1 | U(!io.KEY(1)) << 7
			}
		}

		when(io.SW(0)) {
			dir := Direction.Left
		} otherwise {
			dir := Direction.Right
		}

		io.LED := data
	}
}

// Generate Verilog
object ShiftRegisterVerilog {
	def main(args: Array[String]): Unit = {
		SpinalConfig(targetDirectory = "output/ShiftRegister").generateVerilog(new ShiftRegister)
	}
}