
class fcfs:
	def __init__(self, processes=[]):
		self.processes = processes

	def add_process(self, process):
		self.processes.append(process)

	def get_next(self):
		return self.processes.pop(0)

	def __str__(self):
		res = 'FCFS processes (executing time):'
		for p in self.processes:
			res += ' ' + str(p)
		return res


class rr:
	def __init__(self, quantum, processes=[]):
		self.quantum = quantum
		self.processes = processes
		self.length = len(processes)
		self.curr_proc = 0

	def add_process(self, process):
		self.processes.append(process)
		self.length += 1

	def process_done(self):
		self.processes.pop(self.curr_proc)
		self.length -= 1

	def get_next(self):
		self.curr_proc = (self.curr_proc + 1) % self.length
		return self.processes[self.curr_proc]

	def __str__(self):
		res = 'RR processes (executing time):'
		for p in self.processes:
			res += ' ' + str(p)
		return res