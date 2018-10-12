from planners import fcfs
from planners import rr
from process import generator


if __name__ == '__main__':
	back = fcfs()
	inter = rr(2)
