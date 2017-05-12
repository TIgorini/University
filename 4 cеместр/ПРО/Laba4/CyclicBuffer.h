#define SIZE 10

class CyclicBuffer{
public:
	
	CyclicBuffer();
	~CyclicBuffer();
	int put();
	int get();

private:
	int r_indx;
	int w_indx;
	int buf[SIZE];
};
