#define SIZE = 10;

class CyclicBuffer{
public:
	
	CyclicBuffer();
	~CyclicBuffer();
	void put();
	int  get();
	bool isFull();
	bool isEmpty();

private:
	int r_indx;
	int w_indx;
	int el_count;
	int buff[];
};
