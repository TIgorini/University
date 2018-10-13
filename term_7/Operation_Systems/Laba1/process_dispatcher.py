from planners import fcfs
from planners import rr
from process import generator
import pdb


class dispatcher:
	def __init__(self, tick, interact, background):
		self.curr = 0
		self.int = interact
		self.int_time = 0.8 * tick
		self.bg = background
		self.bg_time = 0.2 * tick

	def run_int(self):
		working_time = 0
		while working_time <= self.int_time and self.int.length != 0:
			curr_proc = self.int.get_next()
			if curr_proc.execute():
				self.int.process_done()
			working_time += 1

	def run_bg(self):
		working_time = 0
		while working_time <= self.bg_time and len(self.bg.processes) != 0:
			curr_proc = self.bg.get_next()
			while curr_proc.execute() == False:
				working_time += 1

	def run(self):
		while True:
			if self.curr:
				print('Backround: ' + str(self.bg))
				self.run_bg()
				self.curr = not self.curr
			else:
				print('Interact: ' + str(self.int))
				self.run_int()
				self.curr = not self.curr
			# pdb.set_trace()


if __name__ == '__main__':
	gr = generator(3, 24)
	disp = dispatcher(20, rr(2, gr.generate(10)), fcfs(gr.generate(8)))
	disp.run()
