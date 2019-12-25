import random
import math


chan_weights = [2, 3, 5, 8, 10, 12, 14, 15, 20, 24, 28, 32]


class Channel:
    def __init__(self, id, nodes, type):
        self.id = id
        self.weight = random.choice(chan_weights)
        self.err = random.uniform(0, 0.2)
        self.active = 1

        if type == 0:
            self.type = 'duplex'
        elif type == 1:
            self.type = 'half-duplex'
            self.weight = round(1.5 * self.weight)
        else:
            self.type = 'satellite'
            self.weight *= 3

        self.nodes = nodes
        for node in nodes:
            node.channels.append(self)

    def to_from(self, node):
        if node is self.nodes[0]:
            return self.nodes[1]
        else:
            return self.nodes[0]

    def exists(self, nodes):
        for node in nodes:
            if node not in self.nodes:
                return False
        return True


class Node:
    def __init__(self, id, type):
        self.id = id
        self.channels = []
        self.type = type
        self.routes = {}

    def calculate_routes(self, nodes):
        not_visited = []
        for node in nodes:
            not_visited.append(node)
            self.routes[node.id] = {'length': math.inf, 'route': []}

        self.routes[self.id] = {'length': 0, 'route': [self.id]}
        curr = self
        while len(not_visited) != 0:
            for chan in curr.channels:
                node = chan.to_from(curr)
                if node not in not_visited or not chan.active:
                    continue

                length = self.routes[curr.id]['length'] + chan.weight
                if length < self.routes[node.id]['length']:
                    self.routes[node.id]['length'] = length
                    new_route = self.routes[curr.id]['route'].copy() + [node.id]
                    self.routes[node.id]['route'] = new_route
            not_visited.remove(curr)

            min_len = math.inf
            for node in not_visited:
                length = self.routes[node.id]['length']
                if length <= min_len:
                    min_len = length
                    curr = node


class Network:
    def __init__(self):
        self.nodes_num = 32
        self.average_pow = 5
        self.last_chan_id = 0
        self.nodes = []
        self.channels = []

        for i in range(self.nodes_num):
            if i % 4 == 0:
                type = 1
            else:
                type = 0
            self.nodes.append(Node(i, type))

        chans_num = int(self.average_pow * self.nodes_num / 2)
        for i in range(chans_num):
            node_centr = i % self.nodes_num
            low = (node_centr - 3)
            hight = (node_centr + 4)
            if hight >= self.nodes_num:
                hight = self.nodes_num - 1
            if low < 0:
                low = 0

            while True:
                node1 = self.nodes[random.randint(low, hight)]
                node2 = self.nodes[random.randint(low, hight)]
                nodes = [node1, node2]
                if node1.id != node2.id and not self.channel_exists(nodes):
                    if i % (int(chans_num / 3) + 1) == 0:
                        chan = Channel(i, nodes, 2)
                    else:
                        chan = Channel(i, nodes, random.randint(0, 1))
                    self.channels.append(chan)
                    break

        for node in self.nodes:
            node.calculate_routes(self.nodes)

    def send(self, src_id, msg):
        src = self.nodes[src_id]
        num = int(math.ceil(msg['msg_size'] / msg['pack_size']))
        dst_nodes = []
        for node in self.nodes:
            if node.id % 4 == 0 and node.id != src_id:
                dst_nodes.append(node)

        res = {
            'results': {
                'Datagram': [],
                'Logic connection': [],
                'Virtual channel': [],
            },
            'times': [],
        }
        for dst in dst_nodes:
            dg_res = self.send_datagram(src, dst, num)
            res['results']['Datagram'].append({
                'dst': dst.id,
                'time': dg_res['time'],
                'routes': dg_res['routes']
            })

            log_res = self.send_log(src, dst, num)
            res['results']['Logic connection'].append({
                'dst': dst.id,
                'time': log_res['time'],
                'routes': log_res['routes']
            })

            virt_res = self.send_virtual(src, dst, num)
            res['results']['Virtual channel'].append({
                'dst': dst.id,
                'time': virt_res['time'],
                'routes': virt_res['routes']
            })
            res['times'].append({
                'dst': dst.id,
                'datagram': dg_res['time'],
                'log': log_res['time'],
                'virtual': virt_res['time'],
            })

        return res

    def send_datagram(self, src, dst, num):
        routes = []
        while len(src.routes[dst.id]['route']) != 0:
            routes.append(src.routes[dst.id].copy())
            for chan in src.channels:
                if chan.to_from(src).id == routes[-1]['route'][1]:
                    chan.active = 0
                    break
            src.calculate_routes(self.nodes)

        for chan in src.channels:
            chan.active = 1
        src.calculate_routes(self.nodes)

        if num <= len(routes):
            time = routes[-1]['length']
            routes = routes[:num]
        else:
            time = (num - len(routes) + 1) * routes[0]['length']

        return {'time': time, 'routes': [route['route'] for route in routes]}

    def send_log(self, src, dst, num):
        routes = []
        while len(src.routes[dst.id]['route']) != 0:
            routes.append(src.routes[dst.id].copy())
            for chan in src.channels:
                if chan.to_from(src).id == routes[-1]['route'][1]:
                    chan.active = 0
                    break
            src.calculate_routes(self.nodes)

        for chan in src.channels:
            chan.active = 1
        src.calculate_routes(self.nodes)

        if num <= len(routes):
            time = routes[-1]['length']
            routes = routes[:num]
        else:
            time = (num - len(routes) + 1) * routes[0]['length']
            if time < routes[-1]['length']:
                time = routes[-1]['length']

        route_len = src.routes[dst.id]['length']
        time += 2 * route_len
        time += num * route_len
        time += 2 * route_len

        return {'time': time, 'routes': [route['route'] for route in routes]}

    def send_virtual(self, src, dst, num):
        route_len = src.routes[dst.id]['length']
        time = 2 * route_len
        time += num * route_len
        time += num * route_len
        time += 2 * route_len
        return {'time': time, 'routes': [src.routes[dst.id]['route']]}

    def channel_exists(self, nodes):
        for chan in self.channels:
            if chan.exists(nodes):
                return True
        return False
