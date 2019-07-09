# RNAmut: Robust Identification of Clinically Relevant Mutations in Acyte Myeloid Leukemia (AML) Using RNA-seq

**What is RNAmut?**\
RNAmut is a fast integrated pipeline that detects somatic mutations from RNA-seq data for clinically important genes. To run RNAmut, user needs to provide **1)** FASTQ files from transcriptomic RNA-seq (for single-end data, only 1 file is needed), **2)** an index file for the genes of interest (a 26-gene panel for AML is provided) and **3)** a text file containing oncogenicity filter (filter for AML genes is provided).

**What is RNAmut not for?**\
RNAmut is not designed for detecting mutation across the whole genome. For these purposes, please use Varscan or GATK.


**User Guide** \
\
**Requirement** \
RNAmut requires Java Runtime Environment 8 installed on your computer. To check which Java version you have, go to Command Prompt in Windows or Terminal in Mac or Linux and type "java -version". If your version does not start with 1.8, please download and install it from oracle.com
![Alt text](/demo/img/java.png?raw=true "Optional Title")

**Download RNAmut**\
To download RNAmut, clicn the "Clone or download" button on the top right of this page, and then "Download ZIP". Once the ZIP file is saved on your computer, you **must** extract it or otherwise the program will not run. 
![Alt text](/demo/img/download.jpg?raw=true "Optional Title")


