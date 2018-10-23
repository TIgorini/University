"""
Runnable program that models work of tickets shop with two
terminals and one server.
"""

import threading
import time
import random


class terminal(threading.Thread):
    """Provides interface to users who want buy tickets"""

    def __init__(self, name, semaphore):
        threading.Thread.__init__(self)
        self.name = name
        self.semaphore = semaphore

    def _ticket_random(self):
        num = random.randint(1, 6)
        if num == 1:
            return {'dst': 'Kyiv', 'price': 28}
        elif num == 2:
            return {'dst': 'Moscow', 'price': 37}
        elif num == 3:
            return {'dst': 'London', 'price': 50}
        elif num == 4:
            return {'dst': 'Berlin', 'price': 77}
        else:
            return {'dst': 'Paris', 'price': 91}

    def run(self):
        print(f'Terminal {self.name} has started work')

        while True:
            ticket = self._ticket_random()
            change = 100 - ticket['price']
            print(f'Terminal {self.name}. Ticket order to {ticket["dst"]} ({ticket["price"]})')

            while not self.semaphore.acquire(blocking=False):
                print(f'Terminal {self.name}. Waiting for server')

            print(f'Terminal {self.name}. Request for change: {change} cent')
            have_change = server.give_change(change)
            print(f'Bank status after operation: {server.bank_status()}')

            self.semaphore.release()

            if have_change:
                print(f'Terminal {self.name}. Order successful. Enjoy your journey')
            else:
                print(f'Terminal {self.name}. Order failed. Can\'t give change')

            time.sleep(1)

        print(f'Terminal {self.name} has ended work')


class server:
    """Takes requests from terminals to give change"""

    def __init__(self, bank):
        self.bank = bank

    def give_change(self, change):
        """Calculates change sum from bank and returns True if it
        is possible to give the change, otherwise returns False
        """
        used = {}
        for fval, number in self.bank.items():
            num_need = change // fval
            if number - num_need < 0:
                used[fval] = number
            else:
                used[fval] = num_need
            change -= fval * used[fval]

        if change != 0:
            return False

        for fval in self.bank:
            self.bank[fval] -= used[fval]
        return True

    def bank_status(self):
        return self.bank


if __name__ == '__main__':
    bank = {50: 5,
            25: 10,
            10: 15,
            5: 20,
            2: 25,
            1: 50, }

    server = server(bank)

    semaphore = threading.Semaphore()
    term_a = terminal('A', semaphore)
    term_b = terminal('B', semaphore)

    term_a.start()
    term_b.start()
