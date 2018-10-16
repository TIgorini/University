from planners import fcfs
from planners import rr
from process import generator
import threading


class dispatcher():
	def __init__(self, tick, interact, background, log_file_name):
		self.curr = 0
		self.int = interact
		self.int_time = 0.8 * tick
		self.bg = background
		self.bg_time = 0.2 * tick
		self.log_file_name = log_file_name

	def run_int(self, log_file):
		working_time = 0
		log_file.write('Interact\ntime    processes\n--------------------------\n')
		log_file.write('{:4d}:  {}\n'.format(working_time, str(self.int)))
		
		while working_time < self.int_time and self.int.proc_num != 0:
			curr_proc = self.int.get_process()
			if curr_proc.execute():
				self.int.process_done()
			working_time += 1
			log_file.write('{:4d}:  {}\n'.format(working_time, str(self.int)))
		
		log_file.write('--------------------------\n\n')

	def run_bg(self, log_file):
		working_time = 0
		log_file.write('Background\ntime    processes\n--------------------------\n')
		log_file.write('{:4d}: {}\n'.format(working_time, str(self.bg)))
		
		while working_time <= self.bgrking process located _time and self.bg.proc_num != 0:
			curr_proc = self.bg.get_process()
			while curr_proc.execute() == False:
				working_time += 1
				log_file.write('{:4d}:  {}{}\n'.format(working_time, curr_proc.exec_time, str(self.bg)))
			working_time += 1
			log_file.write('{:4d}: {}\n'.format(working_time, str(self.bg)))
		
		log_file.write('--------------------------\n\n')

	def run(self):
		print('Initial settings: ')
		print(' Interact time: {}\n Intercat processes: {}'.format(self.int_time, self.int))
		print('\n Background time: {}\n Background processes: {}'.format(self.bg_time, self.bg))
		with open(self.log_file_name, 'w') as log_file:
			while self.bg.proc_num != 0 or self.int.proc_num != 0:
				if self.curr:
					self.run_bg(log_file)
					self.curr = not self.curr
				else:
					self.run_int(log_file)
					self.curr = not self.curr
		print('\nWorking process located in log file: {}'.format(self.log_file_name))


if __name__ == '__main__':
	gr = generator(3, 24)
	interact = rr(2, gr.generate(10))
	background = fcfs(gr.generate(7))	
	disp = dispatcher(50, interact, background, 'dispatcher.log')
	disp.run()
