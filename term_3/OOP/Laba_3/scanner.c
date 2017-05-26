#include "scanner.h"

// 1
int write_scan(FILE *dba, const SCAN_INFO *scanner)
{
	SCAN_INFO *buff;
	int num;

	if ((buff = (SCAN_INFO*)malloc(sizeof(SCAN_INFO))) == NULL)
		return 1;

	fseek(dba, 0, SEEK_SET);
	
	if (!feof(dba))
	{
		fread(&num, sizeof(int), 1, dba);

		while (!feof(dba))
		{
			fread(buff, sizeof(SCAN_INFO), 1, dba);
			if (memcmp(scanner, buff, sizeof(SCAN_INFO)) == 0)
			{
				free(buff);
				return 0;
			}
		}

		free(buff);
		fseek(dba, 0, SEEK_SET);
	}

	num++;
	fwrite(&num, sizeof(int), 1, dba);
	fseek(dba, 0, SEEK_END);
	fwrite(scanner, sizeof(SCAN_INFO), 1, dba);

	return 0;
}

// 2
int write_from_csv(const char *dba_path, const char *csv_path)
{
	FILE *dba;
	FILE *csv;
	SCAN_INFO *recs;

	if ((dba = fopen(dba_path, "w+")) == NULL)
	{
		return 1;
	}
	if ((csv = fopen(csv_path, "r")) == NULL)
	{
		fclose(dba);
		return 1;
	}
	if ((recs = (SCAN_INFO*)malloc(sizeof(SCAN_INFO))) == NULL)
	{
		fclose(dba);
		fclose(csv);
		return 1;
	}
	putw(0,dba);
	while (!feof(csv))
	{
		fscanf(csv, "%[A-z0-9];%[A-z0-9];%d;%f;%d;%d;%d\n", recs->manufacturer, recs->model, &(recs->year), &(recs->price), &(recs->x_size), &(recs->y_size), &(recs->optr));
		if (write_scan(dba, recs)) 
			break;
	}

	free(recs);
	fclose(dba);
	fclose(csv);
	
	return 0;
}

// 3
int make_index(char *dba_path, char *field_name)
{
	FILE *idx, *dba;
	SCAN_INFO **recs, *buff;
	char *idx_path;
	int i, j, num = 0;
	int len,
		fldlen;  //folder len
	
	len = strlen(field_name);
	fldlen = strlen("index/");
	if ((dba = fopen(dba_path, "rb")) == NULL)
		return 1;	

	if ((idx_path = (char*)malloc((len + 4 + fldlen)*sizeof(char))) == NULL)
	{
		fclose(dba);
		return 1;
	}
	strcpy(idx_path, "index/");
	strcpy(idx_path + fldlen, field_name);
	strcpy(idx_path + fldlen + len, ".idx");

	if ((idx = fopen(idx_path, "w")) == NULL)
	{
		free(idx_path);
		fclose(dba);
		return 1;
	}

	fread(&num, sizeof(int), 1, dba);

	if (num > 0)
	{
		recs = (SCAN_INFO**)malloc(num * sizeof(SCAN_INFO*));
		buff = (SCAN_INFO*)malloc(sizeof(SCAN_INFO));

		if (recs == NULL || buff == NULL)
		{
			fclose(dba);
			fclose(idx);
			return 1;
		}
		for (i = 0; i < num; i++)
		{
			if ((recs[i] = (SCAN_INFO*)malloc(sizeof(SCAN_INFO))) == NULL)
			{
				for (j = 0; j < i; j++)
					free(recs[j]);

				free(buff);
				free(recs);
				fclose(dba);
				fclose(idx);
				return 1;
			}
			fread(recs[i], sizeof(SCAN_INFO), 1, dba);
		}
	}
		
	if (field_name == "manufacturer")
		qsort(recs, num, sizeof(SCAN_INFO*), manufacturer_cmp);
	else if (field_name == "year")
		qsort(recs, num, sizeof(SCAN_INFO*), year_cmp);
	else if (field_name == "model")
		qsort(recs, num, sizeof(SCAN_INFO*), model_cmp);
	else if (field_name == "price")
		qsort(recs, num, sizeof(SCAN_INFO*), price_cmp);
	else if (field_name == "x_size")
		qsort(recs, num, sizeof(SCAN_INFO*), x_size_cmp);
	else if (field_name == "y_size")
		qsort(recs, num, sizeof(SCAN_INFO*), y_size_cmp);
	else if (field_name == "optr")
		qsort(recs, num, sizeof(SCAN_INFO*), optr_cmp);
	
	for (i = 0; i < num; i++)
	{
		fseek(dba, sizeof(int), SEEK_SET);
		for (j = 0; j < num; j++)
		{
			fread(buff, sizeof(SCAN_INFO), 1, dba);
			if (recs[i] != NULL && strcmp(buff->manufacturer, recs[i]->manufacturer) == 0 && strcmp(buff->model, recs[i]->model) == 0)
			{
				fprintf(idx, "%d ", j);
				free(recs[i]);
				recs[i] = NULL;
			}
		}
	}

	free(recs);
	free(buff);
	fclose(dba);
	fclose(idx);

	return 0;
}

// functions for qsort()
int manufacturer_cmp(const void *a1, const void *a2)
{
	return strcmp((*(SCAN_INFO**)a1)->manufacturer, (*(SCAN_INFO**)a2)->manufacturer); 
}
int year_cmp(const void *a1, const void *a2) 
{
	return (*(SCAN_INFO**)a1)->year - (*(SCAN_INFO**)a2)->year;
}
int model_cmp(const void *a1, const void *a2)
{
	return strcmp((*(SCAN_INFO**)a1)->model, (*(SCAN_INFO**)a2)->model);
}
int price_cmp(const void *a1, const void *a2)
{
	return (int)((*(SCAN_INFO**)a1)->price - (*(SCAN_INFO**)a2)->price);
}
int x_size_cmp(const void *a1, const void *a2)
{
	return (*(SCAN_INFO**)a1)->x_size - (*(SCAN_INFO**)a2)->x_size;
}
int y_size_cmp(const void *a1, const void *a2)
{
	return (*(SCAN_INFO**)a1)->y_size - (*(SCAN_INFO**)a2)->y_size;
}
int optr_cmp(const void *a1, const void *a2)
{
	return (*(SCAN_INFO**)a1)->optr - (*(SCAN_INFO**)a2)->optr;
}

// 4
RECORD_SET *get_recs_by_index(char *dba_path, char *indx_field)
{
	RECORD_SET *set;
	FILE *dba, *idx;
	int i = 0, j = 0; 

	if ((dba = fopen(dba_path, "rb")) == NULL)
		return NULL;

	if ((idx = fopen(indx_field, "rb")) == NULL)
	{
		fclose(dba);
		return NULL;
	}
	if ((set = (RECORD_SET*)malloc(sizeof(RECORD_SET))) == NULL)
	{
		fclose(dba);
		fclose(idx);
		return NULL;
	}

	fread(&(set->rec_nmb), sizeof(int), 1, dba);
	
	if (set->rec_nmb > 0)
	{
		if ((set->recs = (SCAN_INFO*)malloc(set->rec_nmb * sizeof(SCAN_INFO))) == NULL)
		{
			free(set);
			fclose(dba);
			fclose(idx);
			return NULL;
		}
		for (i = 0; i < set->rec_nmb; i++)
		{
			fscanf(idx, "%d ", &j);
			fseek(dba, sizeof(int), SEEK_SET);
			fseek(dba, j * sizeof(SCAN_INFO), SEEK_CUR);
			fread(set->recs + i, sizeof(SCAN_INFO), 1, dba);
		}
	}

	fclose(dba);
	fclose(idx);

	return set;
}

//5
void reindex(char *dba_path)
{
	make_index(dba_path, "manufacturer");
	make_index(dba_path, "year");
	make_index(dba_path, "model");
	make_index(dba_path, "price");
	make_index(dba_path, "x_size");
	make_index(dba_path, "y_size");
	make_index(dba_path, "optr");
}

//6
int del_scan(char *dba_path, int n)
{
	FILE *dba;
	int num, i, j = 0;
	SCAN_INFO *recs;

	if ((dba = fopen(dba_path, "rb")) == NULL)
		return 1;

	fread(&num, sizeof(int), 1, dba);

	if (num == 0 || n >= num)
	{
		fclose(dba);
		return 1;
	}
	if ((recs = (SCAN_INFO*)malloc(num * sizeof(SCAN_INFO))) == NULL)
	{
		fclose(dba);
		return 1;
	}
	for (i = 0; i < num; i++)
	{	
		fread(recs + j, sizeof(SCAN_INFO), 1, dba);
		if (i == n) continue;
		j++;
	}
	if ((dba = freopen(dba_path, "wb", dba)) == NULL)
	{
		free(recs);
		fclose(dba);
		return 1;
	}

	rewind(dba);
	num--;
	fwrite(&num, sizeof(int), 1, dba);
	fwrite(recs, sizeof(SCAN_INFO), num, dba);

	free(recs);
	fclose(dba);
	reindex(dba_path);

	return 0;
}

//7
int print_to_txt(char *dba_path, char *txt_path, float price)
{
	FILE *dba;
	FILE *txt;
	SCAN_INFO *recs;
	int num = 0, i = 0;

	if ((dba = fopen(dba_path, "r")) == NULL)
		return 1;

	if ((txt = fopen(txt_path, "w")) == NULL)
	{
		fclose(dba);
		return 1;
	}

	fread(&num, sizeof(int), 1, dba);

	if (num > 0)
	{
		if ((recs = (SCAN_INFO*)malloc(num * sizeof(SCAN_INFO))) == NULL)
		{
			fclose(dba);
			fclose(txt);
			return 1;
		}

		fread(recs, sizeof(SCAN_INFO), num, dba);
		for (i = 0; i < num; i++)
		{
			if (recs[i].price <= price)
				fprintf(txt, "%s;%s;%d;%g;%d;%d;%d\n", recs[i].manufacturer, recs[i].model, recs[i].year, recs[i].price, recs[i].x_size, recs[i].y_size, recs[i].optr);
		}
		free(recs);
	}

	fclose(dba);
	fclose(txt);

	return 0;
}