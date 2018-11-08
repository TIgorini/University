import random


class Process:
    def __init__(self, id):
        self.id = id
        self.nseg = random.randint(1, 4)
        self.vm = []
        self.vm_size = 0

        for i in range(self.nseg):
            self.vm.append({'id': i,
                            'addr': 0,
                            'size': random.randint(50, 100),
                            'in_mem': 0,
                            'partition': None})
            self.vm_size += self.vm[i]['size']

    def read(self, seg, addr):
        if seg > len(self.vm) - 1 or addr > self.vm_size - 1:
            return -1
        else:
            if self.vm[seg]['in_mem'] == 0:
                mem.upload(self.vm, self.vm_size, self.id)
            return self.vm[seg]['addr'] + addr

    def __str__(self):
        res = '--------------------Process-{}--------------------\n'.\
            format(self.id)
        res += 'Size: {}\n'.format(self.vm_size)
        for seg in self.vm:
            res += 'Segment {}:\n\tAddress: {}\n\tSize: {}\n\tIn memory: {}\n'\
                .format(seg['id'], seg['addr'], seg['size'], seg['in_mem'])
        return res + '-------------------------------------------------\n'


class Memory:
    def __init__(self, mem=1024):
        self.part_table = []
        self.part_table.append({'id': 0,
                                'addr': 0,
                                'size': 280,
                                'free': 280,
                                'nfree': 0,
                                'proc': None})
        self.part_table.append({'id': 1,
                                'addr': 280,
                                'size': 195,
                                'free': 195,
                                'nfree': 0,
                                'proc': None})
        self.part_table.append({'id': 2,
                                'addr': 475,
                                'size': 249,
                                'free': 249,
                                'nfree': 0,
                                'proc': None})
        # self.part_table.append({'id': 3,
        #                         'addr': 724,
        #                         'size': 300,
        #                         'free': 300,
        #                         'nfree': 0,
        #                         'proc': None})

    def upload(self, segments, size, pid):
        '''Uploads proccess segments to memory'''
        suits_by_size = []
        # Searching partitions that can contain all segments
        for part in self.part_table:
            if size <= part['size']:
                suits_by_size.append(part)

        if suits_by_size == []:
            self.upload(segments[:-1], size - segments[-1]['size'], pid)
            self.upload([segments[-1]], segments[-1]['size'], pid)
        else:
            # Searching for free partiotion
            free_parts = []
            for part in suits_by_size:
                if part['proc'] is None:
                    free_parts.append(part)

            if free_parts == []:
                # If there aren't free partitions
                min_part = suits_by_size[0]
                # Searching for partition with min size
                for part in suits_by_size:
                    if part['proc'] == pid:
                        continue
                    if part['size'] < min_part['size']:
                        min_part = part
                # Unloading old proccess from partition
                self.unload_part(min_part)
            else:
                min_part = free_parts[0]
                for part in free_parts:
                    if part['proc'] == pid:
                        continue
                    if part['size'] < min_part['size']:
                        min_part = part

            for seg in segments:
                seg['addr'] = min_part['addr'] + min_part['nfree']
                seg['in_mem'] = 1
                seg['partition'] = min_part['id']
                min_part['proc'] = pid
                min_part['nfree'] += seg['size']
                min_part['free'] -= seg['size']
            return

    def unload(self, segments):
        """Unloads proccess segments from memory"""
        rev_segments = reversed(segments)
        for seg in rev_segments:
            part_id = seg['partition']
            if part_id is None:
                continue
            part = self.part_table[part_id]
            seg['addr'] = 0
            seg['in_mem'] = 0
            seg['partition'] = None
            part['proc'] = None
            part['nfree'] -= seg['size']
            part['free'] += seg['size']

    def unload_part(self, part):
        """Unloads segments from partition part"""
        segments = processes[part['proc']].vm
        rev_segments = reversed(segments)
        for seg in rev_segments:
            if seg['partition'] == part['id']:
                seg['addr'] = 0
                seg['in_mem'] = 0
                seg['partition'] = None
        part['proc'] = None
        part['nfree'] = 0
        part['free'] = part['size']

    def __str__(self):
        res = '---------------------Memory---------------------\n'
        for part in self.part_table:
            res += 'Partition {}: (Size: {}, Free: {}'.\
                format(part['id'], part['size'], part['free'])
            if part['proc'] is None:
                res += ')\n  {}\n  .\n  .\tfree\n  .\n  {}\n'.\
                    format(part['addr'], part['addr'] + part['size'] - 1)
            else:
                proc = processes[part['proc']]
                res += ', Proccess: {})\n'.format(proc.id)
                for seg in proc.vm:
                    if seg['partition'] == part['id']:
                        res += '  {}\n  .\n  .\tsegment {}\n  .\n  {}\n'.\
                            format(seg['addr'], seg['id'], seg['addr'] + seg['size'] - 1)
                res += '  {}\n  .\n  .\tfree\n  .\n  {}\n'.\
                    format(part['addr'] + part['nfree'], part['addr'] + part['size'] - 1)
        return res + '-------------------------------------------------\n'


def print_commands():
    print('Commands:\n\
  1. upload [pid]\n\
  2. unload [pid]\n\
  3. show [pid]\n\
  4. read [pid]\n\
  5. show\n\
  6. help\n\
  7. exit\n')


if __name__ == '__main__':
    mem = Memory()
    print(mem)
    processes = []
    for i in range(4):
        processes.append(Process(i))
        print(processes[i])

    print_commands()

    while True:
        com = input('> ')
        com = com.split(' ')
        if len(com) == 1:
            if com[0] == 'show':
                print(mem)
            elif com[0] == 'exit':
                break
            else:
                print_commands()

        elif len(com) == 2:
            try:
                pid = int(com[1])
            except ():
                print_commands()
                continue

            if pid > len(processes) - 1:
                print('pid must be < {}'.format(len(processes) - 1))
            elif com[0] == 'upload':
                proc = processes[pid]
                mem.upload(proc.vm, proc.vm_size, proc.id)
                print(mem)
            elif com[0] == 'unload':
                proc = processes[pid]
                mem.unload(proc.vm)
                print(mem)
            elif com[0] == 'read':
                proc = processes[pid]
                print(proc)
                print('Choose address to read like: [seg] [addr]\n')
                com = input('> ')
                com = com.split(' ')
                seg = int(com[0])
                vm_addr = int(com[1])
                addr = proc.read(seg, vm_addr)
                if addr == -1:
                    print('No such address')
                else:
                    print('Virtual memory address: ({}, {})'.format(seg, vm_addr))
                    print('Real memory address: {}\n'.format(addr))
            elif com[0] == 'show':
                print(processes[pid])
            else:
                print_commands()
        else:
            print_commands()
