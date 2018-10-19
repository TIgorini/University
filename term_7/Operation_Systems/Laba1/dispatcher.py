from planners import fcfs
from planners import rr
from process import generator


class dispatcher:
    """Runs background and interactive processes"""

    def __init__(self, proc_time, interact, background, log_file_name):
        self.int = interact
        self.int_time = 0.8 * proc_time
        self.bg = background
        self.bg_time = 0.2 * proc_time
        self.log_file_name = log_file_name
        self.curr_mode = 0
        self.working_time = 0

    def run_int(self, log_file):
        """Runs interactive processes"""
        local_time = 0
        log_file.write('Interact\ntime  local    processes\
            \n------------------------------\n')
        log_file.write('{:4d} {:4d}:  {}\n'.format(
            self.working_time, local_time, str(self.int)))

        while local_time < self.int_time and self.int.proc_num != 0:
            curr_proc = self.int.get_process()
            if curr_proc.execute():
                self.int.remove_process()
            local_time += 1
            self.working_time += 1
            log_file.write('{:4d} {:4d}:  {}\n'.format(
                self.working_time, local_time, str(self.int)))

        log_file.write('-----------------------------\n\n')

    def run_bg(self, log_file):
        """Runs background processe"""
        local_time = 0
        log_file.write('Background\ntime  local    processes\
            \n-----------------------------\n')
        log_file.write('{:4d} {:4d}: {}\n'.format(
            self.working_time, local_time, str(self.bg)))

        while local_time <= self.bg_time and self.bg.proc_num != 0:
            curr_proc = self.bg.get_process()
            while not curr_proc.execute():
                local_time += 1
                self.working_time += 1
                log_file.write('{:4d} {:4d}:  {}{}\n'.format(
                    self.working_time, local_time,
                    curr_proc.exec_time, str(self.bg)))
            local_time += 1
            self.working_time += 1
            log_file.write('{:4d} {:4d}: {}\n'.format(
                self.working_time, local_time, str(self.bg)))

        log_file.write('-----------------------------\n\n')

    def run(self):
        """Begins work of dispatcher"""
        print('Initial settings: ')
        print(' Interact time: {}\n Intercat processes: {}'.format(
            self.int_time, self.int))
        print('\n Background time: {}\n Background processes: {}'.format(
            self.bg_time, self.bg))

        with open(self.log_file_name, 'w') as log_file:
            while self.bg.proc_num != 0 or self.int.proc_num != 0:
                if self.curr_mode:
                    self.run_bg(log_file)
                    self.curr_mode = not self.curr_mode
                else:
                    self.run_int(log_file)
                    self.curr_mode = not self.curr_mode

        print('\nWorking time: {}'.format(self.working_time))
        print('Log file: {}'.format(
            self.log_file_name))


if __name__ == '__main__':
    gr = generator(3, 24)
    interact = rr(2, gr.generate(6))
    background = fcfs(gr.generate(4))
    disp = dispatcher(50, interact, background, 'dispatcher.log')
    disp.run()
