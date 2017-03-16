#pragma once

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct {
	char manufacturer[127];	
	int year;				
	char model[128];		
	float price;			
	int x_size;				
	int y_size;				
	int optr;				
} SCAN_INFO;

typedef struct {
	int rec_nmb;		// number of records
	SCAN_INFO *recs;	// records 
} RECORD_SET;

int write_scan(FILE *dba, const SCAN_INFO *scanner); // 1
int write_from_csv(const char *dba_path, const char *csv_path); // 2

// functions for qsort()
int manufacturer_cmp(const void *a1, const void *a2);
int year_cmp(const void *a1, const void *a2);
int model_cmp(const void *a1, const void *a2);
int price_cmp(const void *a1, const void *a2);
int x_size_cmp(const void *a1, const void *a2);
int y_size_cmp(const void *a1, const void *a2);
int optr_cmp(const void *a1, const void *a2);

int make_index(char *dba_path, char *field_name); // 3
RECORD_SET *get_recs_by_index(char *dba_path, char *indx_field); // 4
void reindex(char *dba_path); // 5
int del_scan(char *dba_path, int n); // 6
int print_to_txt(char *dba_path, char *txt_path, float price); // 7