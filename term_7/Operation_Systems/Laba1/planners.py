class fcfs:
	def __init__(self, processes=[]):
		self.processes = processes
		self.proc_num = len(processes)

	def add_process(self, process):
		self.processes.append(process)
		self.proc_num += 1

	def get_process(self):
		self.proc_num -= 1
		return self.processes.pop(0)

	def __str__(self):
		res = ''
		for p in self.processes:
			res += ' ' + str(p)
		return res


class rr:
	def __init__(self, quantum, processes=[]):
		self.quantum = quantum
		self.curr_quant_time = 0
		self.processes = processes
		self.proc_num = len(processes)
		self.curr_proc = 0

	def add_process(self, process):
		self.processes.append(process)
		self.proc_num += 1

	def process_done(self):
		self.processes.pop(self.curr_proc)
		self.proc_num -= 1
		self.curr_quant_time = 0
		if self.proc_num != 0:
			self.curr_proc = self.curr_proc % self.proc_num

	def get_process(self):
		if self.curr_quant_time >= self.quantum:
			self.curr_proc = (self.curr_proc + 1) % self.proc_num
			self.curr_quant_time = 1
		else:
			self.curr_quant_time += 1
		return self.processes[self.curr_proc]

	def __str__(self):
		res = ''
		for p in self.processes:
			res += ' ' + str(p)
		return res