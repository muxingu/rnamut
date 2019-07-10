# RNAmut: Robust Identification of Clinically Relevant Mutations in Acyte Myeloid Leukemia (AML) Using RNA-seq

### **What is RNAmut?**
RNAmut is a fast and user-friendly software that detects clinically important somatic mutations from RNA-seq data. To run RNAmut, user needs to provide **1)** FASTQ files from transcriptomic RNA-seq (for single-end data, only 1 file is needed), **2)** an index file for the genes of interest (a 26-gene panel for AML is provided) and **3)** a text file containing oncogenicity filter (the filter for AML genes is provided).

### **What is RNAmut not for?**
RNAmut is not designed for detecting mutation across the whole genome. For these purposes, please use Varscan, GATK or other software.


## **1 Getting Started** 
### **1.1 Requirement** 
RNAmut requires Java Runtime Environment 8 (i.e. Java 1.8) installed on your computer. To check which Java version you have, open "Command Prompt" in Windows or Terminal in Mac or Linux and type "java -version". If Java is not found or your Java version does not start with 1.8, please download and install it from oracle.com \
![Alt text](/demo/img/java.png)

### **1.2 Download RNAmut**
To download RNAmut, click the "Clone or download" button on the top right of this page, and then "Download ZIP". Once the ZIP file is saved on your computer, you **must** extract it to a separate folder. Otherwise the program will not run. \
![Alt text](/demo/img/download.png)

### **1.3 Run RNAmut**
The easiest way to run RNAmut is to open the RNAmut_Windows.bat file for Windows users. For Mac or Linux users, open Terminal, change directory to the RNAmut folder and type "sh RNAmut_Mac-Linux.sh". **Do not** open the RNAmut.jar file directly. Once the software has started, click the "New Analysis" button on the top left. A new window will pop up, asking for 1) input FASTQ files 2) gene index file and 3) oncogenicity filter file. For demonstration purpose, do not change anything and click the start button. The demo analysis will finish instantly. To analyse your own data, you need to provide FASTQ files from your own experiments and real RNA-seq data normally take 10-20 minutes depending on computers.\
![Alt text](/demo/img/run.png)

### **1.4 Output**
Once completed the analysis, RNAmut reports the detected oncogenic mutations within the provided RNA-seq data. It also reports all mutations prior to the oncogenicity filter for user's interest.
![Alt text](/demo/img/output.png)


## **2. Advanced Uses**
### **2.1 Increase Allocated Memory**
The previously described way or running RNAmut allocated 1024MB of memory for it. If you ran out of memory, you should increase the memory manually. To do this, open "Command Prompt" in Windows or Terminal in Mac or Linux. Use "cd" command to go to RNAmut folder. Type "java -Xmx???m -Xms???m -jar RNAmut.jar", where ??? is the memory in MB to allocate, and hit the Enter key. For example, to allocate 2048 MB of memory:\
![Alt text](/demo/img/java_mem.png)

### **2.2 Customise Oncogenicity Filter**
The default oncogenicity filter stored in the oncogenicity_filter.txt file was obtained from the Papaemmanuil et al 2016 paper in NEJM. To customise it, you need to keep the format unchanged:\
  - Each row must consit of two columns separated by TAB. \
  - The first column must be a gene within the gene index.
  - The second column consists of comma-separated oncogenic mutations to be retained by the filter. Mutation can be in either of the two format. A) Full mutation, for example V277M, which will retain V277M but discard V277L. B) Amino acid and location only, for example D835, which will retain all mutation at D835 of the gene.
  - Certain keywords are allowed in the comma-separated fields of Column 2. These keywords are: frameshift (for retaining all frameshifts) and stopcodon (for retaining all substitution causing the gain of stop codon)

### **2.3 Customise the Set of Test Genes**
To extend RNAmut to a larger set of indexed genes or to other cancers, users will need to provide a customised gene index file. We will provide a webserver for this purpose. To be updated.\

