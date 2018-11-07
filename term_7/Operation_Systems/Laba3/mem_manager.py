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

    def __str__(self):
        res = f'   Process {self.id}\nVM size: {self.vm_size}\n'
        for seg in self.vm:
            res += 'Segment {}:\n\tAddress: {}\n\tSize: {}\n\tIn memory: {}\n'\
                .format(seg['id'], seg['addr'], seg['size'], seg['in_mem'])
        return res


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
        self.part_table.append({'id': 3,
                                'addr': 724,
                                'size': 300,
                                'free': 300,
                                'nfree': 0,
                                'proc': None})

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
            for part in suits_by_size:
                if part['proc'] is None:
                    for seg in segments:
                        seg['addr'] = part['addr'] + part['nfree']
                        seg['in_mem'] = 1
                        seg['partition'] = part['id']
                        part['proc'] = pid
                        part['nfree'] += seg['size']
                        part['free'] -= seg['size']
                    return
            # If there aren't free partitions
            for part in suits_by_size:
                old_pid = part['proc']
                if old_pid != pid:
                    self.unload_part(part)
                    for seg in segments:
                        seg['addr'] = part['addr'] + part['nfree']
                        seg['in_mem'] = 1
                        seg['partition'] = part['id']
                        part['proc'] = pid
                        part['nfree'] += seg['size']
                        part['free'] -= seg['size']
                    return

    def unload(self, segments):
        """Unloads proccess segments from memory"""
        rev_segments = reversed(segments)
        for seg in rev_segments:
            part = self.part_table[seg['partition']]
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
        res = ''
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
        return res


if __name__ == '__main__':
    mem = Memory()
    print('---------------------------------------------')
    print(mem)
    print('---------------------------------------------')
    processes = []
    print('---------------------------------------------')
    for i in range(3):
        processes.append(Process(i))
        print(processes[i])
    print('---------------------------------------------')

    while True:
        pid = int(input('Enter pid to upload: '))
        proc = processes[pid]
        mem.upload(proc.vm, proc.vm_size, pid)
        print('---------------------------------------------')
        print(mem)
        print('---------------------------------------------')
