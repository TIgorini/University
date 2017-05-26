#include "scanner.h"

int main()
{
	if (write_from_csv("Scanners.dba", "../Scanners.csv"))
		printf("Error with .dba creating\n");
	reindex("Scanners.dba");

	if (get_recs_by_index("Scanners.dba", "index/manufacturer.idx") == NULL)
		printf("Error in get_recs_by_index()\n");

	if (del_scan("Scanners.dba", 2))
		printf("Error with deleting scanner\n");
	
	if (print_to_txt("Scanners.dba", "Scanners_by_price.txt", 1000.0))
		printf("Error with input to txt\n");
	return 0;
}