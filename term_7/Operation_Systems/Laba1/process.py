"""
Contains generator to create new processes.
"""

import random


class process:
    def __init__(self, id, exec_time):
        self.id = id
        self.exec_time = exec_time

    def execute(self):
        self.exec_time -= 1
        return True if self.exec_time == 0 else False

    def __str__(self):
        return str(self.exec_time)


class generator():
    """Creates new processes with random execution time

    min - bottom value of execution time
    max - top value of execution time
    """

    def __init__(self, min, max):
        self.min = min
        self.max = max
        self.last_proc_num = 0

    def generate(self, n=1):
        """Returns new process

        if n > 1 returns list of processes
        """
        if n == 1:
            self.last_proc_num += 1
            return process(self.last_proc_num,
                           random.randint(self.min, self.max))
        else:
            processes = []
            for i in range(1, n + 1):
                processes.append(self.generate())
            return processes
