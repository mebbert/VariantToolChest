### Introduction
The Variant Tool Chest (VTC) is designed to be a tool chest for analyzing
variants (particularly VCF files) and to be **easily extensible**. We hope you
will incorporate your valuable tools/algorithms to VTC. These tools/algorithms
may be independently published or not. In either case, we want to build a
well-integrated tool chest. Please see
our BMC Bioinformatics paper (in
press--to be published May 28, 2014). VTC is still under development and we 
are working to improve features and reliability, but there are
several specific gaps VTC currently fills including:

* Advanced set operations
* Simple VCF comparisons
* Variant summaries (detailed and minimal)
* Variant statistics (e.g. association studies)

There are currently two tools in VTC known as SetOperator and VarStats. 
VarStats is still under development but currently performs association analyses
on single or multi-sample VCFs and will print out detailed VCF summaries.


### SetOperator (SO)
Some important features for SetOperator are:

* Handles multi-sample VCFs
* Genotype aware set operations (e.g., only intersect on hets)
* Powerful set operation syntax
* Operation stringing (predefine operations whose result should be passed
  directly to the next operation
* Auto 'chr' handling (i.e., we don't care if some VCFs have 'chr' preprended to
  the #CHROM value and some don't.)
* INDEL fuzzy matching (sometimes INDELS don't align the same even though they
  are the same). We'll tell you about these matches.

#### Multi-sample VCFS
Short and sweet. We handle multi-sample VCFs. :)

```
##fileformat=VCFv4.1
##fileDate=20090805
##source=myImputationProgramV3.1
##reference=file:///seq/references/1000GenomesPilot-NCBI36.fasta
##contig=<ID=20,length=62435964,assembly=B36,md5=f126cdf8a6e0c7f379d618ff66beb2da,species="Homo sapiens",taxonomy=x>
##phasing=partial
##INFO=<ID=NS,Number=1,Type=Integer,Description="Number of Samples With Data">
##INFO=<ID=DP,Number=1,Type=Integer,Description="Total Depth">
##INFO=<ID=AF,Number=A,Type=Float,Description="Allele Frequency">
##INFO=<ID=AA,Number=1,Type=String,Description="Ancestral Allele">
##INFO=<ID=DB,Number=0,Type=Flag,Description="dbSNP membership, build 129">
##INFO=<ID=H2,Number=0,Type=Flag,Description="HapMap2 membership">
##FILTER=<ID=q10,Description="Quality below 10">
##FILTER=<ID=s50,Description="Less than 50% of samples have data">
##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##FORMAT=<ID=GQ,Number=1,Type=Integer,Description="Genotype Quality">
##FORMAT=<ID=DP,Number=1,Type=Integer,Description="Read Depth">
##FORMAT=<ID=HQ,Number=2,Type=Integer,Description="Haplotype Quality">
#CHROM POS     ID        REF    ALT     QUAL FILTER INFO                              FORMAT      NA00001        NA00002        NA00003
20     14370   rs6054257 G      A       29   PASS   NS=3;DP=14;AF=0.5;DB;H2           GT:GQ:DP:HQ 0|0:48:1:51,51 1|0:48:8:51,51 1/1:43:5:.,.
20     17330   .         T      A       3    q10    NS=3;DP=11;AF=0.017               GT:GQ:DP:HQ 0|0:49:3:58,50 0|1:3:5:65,3   0/0:41:3
20     1110696 rs6040355 A      G,T     67   PASS   NS=2;DP=10;AF=0.333,0.667;AA=T;DB GT:GQ:DP:HQ 1|2:21:6:23,27 2|1:2:0:18,2 2/2:35:4
20     1230237 .         T      .       47   PASS   NS=3;DP=13;AA=T                   GT:GQ:DP:HQ 0|0:54:7:56,60 0|0:48:4:51,51 0/0:61:2
20     1234567 microsat1 GTC    G,GTCT  50   PASS   NS=3;DP=9;AA=G                    GT:GQ:DP    0/1:35:4       0/2:17:2       1/1:40:3
```




### VarStat (vs)

This portion of the program will output the the variant statistical report as a summary or the association test or both.

Sample inputs are as follow:

<pre><code>
>vs -i input1.vcf -s
# or
>vs -i input1.vcf input2.vcf -s -c
# or
>vs -i input1.vcf input2.vcf -s
# or
>vs -i input1.vcf -a -p pheno1.txt -s
# or
>vs -i input1.vcf -a -p pheno1.txt 
</code></pre>


**Statistical Summary (-s)**

To run the summary multiple input file are allowed. The default print function will print one summary per file.
The -c parameter will print a combined summary with all the files' names printed at the top.  The input files may only
be .vcf files.

Sample terminal input:
<pre><code>
>vs -i input1.vcf -s
# or
>vs -i input1.vcf input2.vcf -s -c
# or
>vs -i input1.vcf input2.vcf -s
</code></pre>

The first option will output the summary for that file.  
The second will output the combined summary of the two files.
The third will output individual summaries for each of the files.

The Summary outputs the following statistics to the screen.
	
<pre><code>
===============================
                               
 Summary of v1: input1.vcf     
                               
===============================
+-----------------------------+
|TotalVars:              3200 |
|Total Samples:             1 |
+-----------------------------+
|    SNVs:                151 |
|         Ti/Tv:         2.78 |
|   (Geno)Ti/Tv:         2.78 |
+-----------------------------+
|    MNVs:                  0 |
+-----------------------------+
|    INDELs:             3049 |
|           INS:         1700 |
|           DEL:         1349 |
|      smallINS:            2 |
|      largeINS:           30 |
|        avgINS:            7 |
|      smallDEL:            2 |
|      largeDEL:           27 |
|        avgDEL:            7 |
+-----------------------------+
|    StructVars:            0 |
|     StructINS:            0 |
|     StructDEL:            0 |
+-----------------------------+
|MultiAlts:                 0 |
+-----------------------------+
</code></pre>
	
* **TotalVars** counts the total number of variants in the file/files.
* **Total Samples** gives the count of the total number of samples in the file/files.
* **SNVs** counts the number of Single Nucleotide Variants (SNVs) in the file/files.
* **Ti/Tv** outputs the ratio of Transition versus Transition SNVs. 
* **(Geno)Ti/Tv** outputs the genotypic ratio of Transition versus Tranversion SNVs.
* **INDELs** counts the number of Insertions & Deletions.
* **smallINS** the smallest (length) observed insertion.
* **largeINS** the largest (length) observed insertion.
* **avgINS** the average (length) observed insertion.
* **smallDEL** the smallest (length) observed deletion.
* **largeDEL** the largest (length) observed deletion.
* **avgDEL** the average (length) observed deletion.
* **StructVars** counts the number of Structural Variants in the file.
* **MultiAlts** counts the number of variants that have multiple alternate alleles.


If there is a "NaN" for either Ti/Tv or (Geno)Ti/Tv it means that there is division by zero.

A tab delimited file is written per variant that is named filename_summary.txt.  In this case input1_summary.txt:

<pre><code>
Chr     Pos     ID          Ref Alts RefCount AltCount AvgDepth MinDepth MaxDepth Qual Errors
chr20   14370   rs6054257   G   A    3        3        4.67     1        8        29.0
chr20   17330   .           T   A    4        2        3.67     3        5         3.0
chr20   1110696 rs6040355   A   G,T  0        2,4      5        4        6        67.0 Incorrect depth calls in samples: NA00002.
...
</code></pre>

* **Chr** is the chromosome number.
* **Pos** is the start position of the variant.
* **ID** is the SNP identification number.
* **Ref** is the reference allele.
* **Alts** is the comma delimited list of alternate alleles.
* **RefCount** is the total count of the reference alleles per variant.
* **AltCount** is the comma delimited list of the alternate allele counts (in the same order as the Alts column).
* **AvgDepth** is the average read depth per variant.
* **MinDepth** is the minimum read depth per variant. 
* **MaxDepth** is the maximum read depth per variant.
* **Qual** is the quality score per variant. If there is a "NA" it means that there was no quality score or a quality score of 0.
* **Errors** is a list of sample IDs that had no read depth score for that particular variant.


**Association Test (-a)**

This test calculates the p-value from the chi square test from the case-control allelic counts. 

The following are possible inputs:
<pre><code>
>vs -i input1.vcf -a -p pheno1.txt -s
# or
>vs -i input1.vcf -a -p pheno1.txt 
</code></pre>

The phenotype file needs to be a tab delimited file formatted as follows:
<pre><code>
HG00127	1
HG00128	2
HG00136	2
HG00137	1
...
</code></pre>
The first column is the sample ID, and the second column is the case (2) control (1) status. All of the sample ID's that are in common
between the Pheno file and the VCF file will be analyzed in the association test.  There may be Samples that are not in one or the other, 
but these will not be included in the analysis and will not throw an error.

The tab delimited output file is named filename_Assoc.txt has the following format:
<pre><code>
Chr ID  Pos     Ref Alt CaseRefCount CaseAltCount ControlRefCount ControlAltCount OR     P-Value
20  .   669442  TG  T   389          33           323             21              0.7664 0.3563
20  .   719486  C   CT  420          0            339             1               NA     0.2661
20  .   890696  C   CAT 419          3            341             3               1.229  0.8013
20  .   1102516 CT  C   419          1            338             2               2.479  0.4440
20  .   1149576 CT  C   420          2            342             0               NA     0.2024
20  .   1195706 AAG A   231          191          199             143             0.8691 0.3394
...
</code></pre>

The columns are as follows:
* **Chr** is the chromosome number.
* **ID** is the variant ID (i.e. rs2228467).
* **Pos** is the position of the variant on the chromosome.
* **Ref** is the reference allele.
* **Alt** is the alternate allele. If there are more than one it is printed on another line.
* **CaseRefCount** is the count of of how many reference alleles are present in the case samples.
* **CaseAltCount** is the count of of how many alternate alleles are present in the case samples.
* **ControlRefCount** is the count of of how many reference alleles are present in the control samples.
* **ControlAltCount** is the count of of how many alternate alleles are present in the control samples.
* **OR** is the odds ratio calculated from the case and control allele counts.
* **P-Value** is the p-value calculated from the chi square test using the case and control allele counts.


