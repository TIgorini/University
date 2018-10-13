import random


class process:
	def __init__(self, exec_time):
		self.exec_time = exec_time

	def execute(self):
		self.exec_time -= 1
		return True if self.exec_time == 0 else False

	def __str__(self):
		return str(self.exec_time)


class generator:
	def __init__(self, min, max):
		self.min = min
		self.max = max

	def generate(self, n=1):
		if n == 1:
			return process(random.randint(self.min, self.max))
		else:
			processes = []
			for i in range(1, n+1):
				processes.append(self.generate())
			return processes
