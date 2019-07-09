# RNAmut: Robust Identification of Clinically Relevant Mutations in Acyte Myeloid Leukemia (AML) Using RNA-seq

What is RNAmut?\
RNAmut is a fast integrated pipeline that detects somatic mutations from RNA-seq data for clinically important genes. To run RNAmut, user needs to provide 1) FASTQ files from transcriptomic RNA-seq (for single-end data, only 1 file is needed), 2) an index file for the genes of interest (a 26-gene panel for AML is provided) and 3) a text file containing oncogenicity filter (filter for AML genes is provided).

What is RNAmut not for?\
RNAmut is not designed for detecting mutation across the whole genome. For these purposes, please use Varscan or GATK.



