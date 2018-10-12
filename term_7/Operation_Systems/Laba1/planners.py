
class fcfs:
	def __init__(self, processes=[]):
		self.processes = processes

	def add_process(self, process):
		self.processes.append(process)

	def get_next(self):
		return self.process.popleft()


class rr:
	def __init__(self, quantum, processes=[]):
		self.quantum = quantum
		self.processes = processes
		self.length = len(processes)
		self.i = 0

	def add_process(self, process):
		self.processes.append(process)
		self.length += 1

	def get_next(self):
		self.i = (self.i + 1) % self.length
		return self.processes[i]
